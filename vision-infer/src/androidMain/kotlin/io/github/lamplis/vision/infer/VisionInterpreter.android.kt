/*
 * Copyright 2026 lamplis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.lamplis.vision.infer

import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import java.nio.ByteBuffer
import java.nio.ByteOrder

actual fun loadVisionInterpreter(
    bytesOrPath: String,
    format: ModelFormat,
    bytes: ByteArray?,
    preferAccelerator: Boolean,
): VisionInterpreter {
    if (format != ModelFormat.Tflite || bytes == null) {
        throw IllegalArgumentException("Android vision-infer requires ModelFormat.Tflite and model bytes")
    }
    return AndroidVisionInterpreter(bytes, preferAccelerator)
}

actual fun loadOptimizedModel(
    model: OptimizedVisionModel,
    preferAccelerator: Boolean,
): VisionInterpreter = AndroidVisionInterpreter(model.tfliteBytes, preferAccelerator)

private class AndroidVisionInterpreter(
    modelBytes: ByteArray,
    preferAccelerator: Boolean,
) : VisionInterpreter {
    private val gpuDelegate: GpuDelegate?
    private val interpreter: Interpreter

    init {
        val threads = Runtime.getRuntime().availableProcessors().coerceIn(2, 4)
        val cpu = newInterpreter(modelBytes, threads, delegate = null)
        val gpu = if (preferAccelerator) openGpu(modelBytes) else null
        if (gpu == null) {
            interpreter = cpu
            gpuDelegate = null
        } else {
            val cpuMedian = medianRunMs(cpu)
            val gpuMedian = runCatching { medianRunMs(gpu.interpreter) }.getOrNull()
            if (gpuMedian != null && gpuMedian < cpuMedian * GPU_KEEP_RATIO) {
                cpu.close()
                interpreter = gpu.interpreter
                gpuDelegate = gpu.delegate
            } else {
                gpu.close()
                interpreter = cpu
                gpuDelegate = null
            }
        }
    }

    override suspend fun run(input: FloatArray): List<VisionTensor> {
        val inputBuffer = floatBuffer(input.size)
        input.forEach { inputBuffer.putFloat(it) }
        inputBuffer.rewind()
        val outputs =
            Array(interpreter.outputTensorCount) { index ->
                floatBuffer(interpreter.getOutputTensor(index).numElements())
            }
        val outputMap = outputs.mapIndexed { index, buffer -> index to buffer }.toMap()
        interpreter.runForMultipleInputsOutputs(arrayOf(inputBuffer), outputMap)
        return outputs.mapIndexed { index, buffer ->
            buffer.rewind()
            val values = FloatArray(buffer.capacity() / FLOAT_BYTES) { buffer.float }
            VisionTensor(
                shape = interpreter.getOutputTensor(index).shape().toList(),
                values = values,
            )
        }
    }

    override fun close() {
        interpreter.close()
        gpuDelegate?.close()
    }
}

private class GpuInterpreter(
    val interpreter: Interpreter,
    val delegate: GpuDelegate,
) {
    fun close() {
        interpreter.close()
        delegate.close()
    }
}

private fun openGpu(modelBytes: ByteArray): GpuInterpreter? {
    val compat = CompatibilityList()
    if (!compat.isDelegateSupportedOnThisDevice) return null
    val delegate = GpuDelegate(compat.bestOptionsForThisDevice)
    return try {
        GpuInterpreter(newInterpreter(modelBytes, threads = 1, delegate = delegate), delegate)
    } catch (_: Throwable) {
        delegate.close()
        null
    }
}

private fun newInterpreter(
    modelBytes: ByteArray,
    threads: Int,
    delegate: GpuDelegate?,
): Interpreter {
    val options =
        Interpreter.Options().apply {
            numThreads = threads
            if (delegate != null) {
                addDelegate(delegate)
            }
        }
    val buffer =
        ByteBuffer.allocateDirect(modelBytes.size).order(ByteOrder.nativeOrder()).apply {
            put(modelBytes)
            rewind()
        }
    return Interpreter(buffer, options)
}

private fun medianRunMs(model: Interpreter): Long {
    val samples = LongArray(TRIAL_RUNS)
    repeat(TRIAL_RUNS) { index ->
        val input = floatBuffer(model.getInputTensor(0).numElements())
        val output = floatBuffer(model.getOutputTensor(0).numElements())
        val startedAt = System.nanoTime()
        model.run(input, output)
        samples[index] = (System.nanoTime() - startedAt) / 1_000_000L
    }
    samples.sort()
    return samples[samples.size / 2]
}

private fun floatBuffer(elements: Int): ByteBuffer = ByteBuffer.allocateDirect(elements * FLOAT_BYTES).order(ByteOrder.nativeOrder())

private const val GPU_KEEP_RATIO = 0.8
private const val TRIAL_RUNS = 3
private const val FLOAT_BYTES = 4
