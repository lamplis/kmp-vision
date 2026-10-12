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
package io.github.lamplis.vision.ocr

import android.graphics.BitmapFactory
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await

@Suppress("UNUSED_PARAMETER")
actual fun createOcr(recognitionLanguages: List<String>): Ocr = AndroidOcr()

private class AndroidOcr : Ocr {
    private val client = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    override suspend fun recognize(encodedImage: ByteArray): List<OcrLine> {
        val bitmap = BitmapFactory.decodeByteArray(encodedImage, 0, encodedImage.size) ?: return emptyList()
        return try {
            val text = client.process(InputImage.fromBitmap(bitmap, 0)).await()
            text.textBlocks.flatMap { block -> block.lines }.mapNotNull { line ->
                val content = line.text.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                val box = line.boundingBox
                val bounds =
                    if (box == null || bitmap.width == 0 || bitmap.height == 0) {
                        NormalizedRect(0f, 0f, 1f, 1f)
                    } else {
                        NormalizedRect(
                            left = box.left.toFloat() / bitmap.width,
                            top = box.top.toFloat() / bitmap.height,
                            right = box.right.toFloat() / bitmap.width,
                            bottom = box.bottom.toFloat() / bitmap.height,
                        )
                    }
                OcrLine(
                    text = content,
                    confidence = line.confidence,
                    boundingBox = bounds,
                )
            }
        } finally {
            bitmap.recycle()
        }
    }

    override fun close() {
        client.close()
    }
}
