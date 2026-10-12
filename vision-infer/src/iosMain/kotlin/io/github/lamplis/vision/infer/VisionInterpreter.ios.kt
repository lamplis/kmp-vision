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

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.FloatVar
import kotlinx.cinterop.get
import kotlinx.cinterop.plus
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.set
import platform.CoreML.MLComputeUnitsCPUAndNeuralEngine
import platform.CoreML.MLComputeUnitsCPUOnly
import platform.CoreML.MLDictionaryFeatureProvider
import platform.CoreML.MLFeatureValue
import platform.CoreML.MLModel
import platform.CoreML.MLModelConfiguration
import platform.CoreML.MLMultiArray
import platform.Foundation.NSNumber
import platform.Foundation.NSURL
import visionalloc.VisionAllocMultiArray

actual fun loadVisionInterpreter(
    bytesOrPath: String,
    format: ModelFormat,
    bytes: ByteArray?,
    preferAccelerator: Boolean,
): VisionInterpreter {
    if (format != ModelFormat.CoreMl) {
        throw IllegalArgumentException("iOS vision-infer requires ModelFormat.CoreMl and a .mlmodelc path")
    }
    return IosVisionInterpreter(bytesOrPath, preferAccelerator)
}

actual fun loadOptimizedModel(
    model: OptimizedVisionModel,
    preferAccelerator: Boolean,
): VisionInterpreter = IosVisionInterpreter(model.coreMlDirectory, preferAccelerator)

@OptIn(ExperimentalForeignApi::class)
private class IosVisionInterpreter(
    modelPath: String,
    preferAccelerator: Boolean,
) : VisionInterpreter {
    private val model: MLModel =
        MLModel.modelWithContentsOfURL(
            url = NSURL.fileURLWithPath(modelPath),
            configuration =
                MLModelConfiguration().apply {
                    computeUnits =
                        if (preferAccelerator) {
                            MLComputeUnitsCPUAndNeuralEngine
                        } else {
                            MLComputeUnitsCPUOnly
                        }
                },
            error = null,
        ) ?: throw IllegalStateException("Failed to load Core ML model at $modelPath")

    override suspend fun run(input: FloatArray): List<VisionTensor> {
        val inputName =
            model.modelDescription.inputDescriptionsByName.keys
                .firstOrNull()
                ?.toString()
                ?: throw IllegalStateException("Core ML model has no inputs")
        val multiArray = allocateMultiArray(input.size)
        val pointer = multiArray.dataPointer?.reinterpret<FloatVar>()
        if (pointer != null) {
            input.forEachIndexed { index, value ->
                (pointer + index)!!.set(index = 0, value = value)
            }
        }
        val provider =
            MLDictionaryFeatureProvider(
                dictionary =
                    mapOf<Any?, Any?>(
                        inputName to MLFeatureValue.featureValueWithMultiArray(multiArray),
                    ),
                error = null,
            )
        val result =
            model.predictionFromFeatures(provider, error = null)
                ?: throw IllegalStateException("Core ML prediction failed")
        return result.featureNames.mapNotNull { name ->
            val featureName = name.toString()
            val array = result.featureValueForName(featureName)?.multiArrayValue ?: return@mapNotNull null
            val count = array.count.toInt()
            val pointer = array.dataPointer?.reinterpret<FloatVar>()
            val values =
                if (pointer != null) {
                    FloatArray(count) { index -> pointer[index] }
                } else {
                    FloatArray(count)
                }
            VisionTensor(
                shape = array.shape.map { (it as NSNumber).intValue },
                values = values,
            )
        }
    }

    override fun close() = Unit
}

@OptIn(ExperimentalForeignApi::class)
private fun allocateMultiArray(size: Int): MLMultiArray =
    VisionAllocMultiArray(listOf(NSNumber(int = size)))
        ?: throw IllegalStateException("Failed to allocate Core ML input")
