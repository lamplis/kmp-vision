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
package io.github.lamplis.vision.text

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.useContents
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.dataWithBytes
import platform.UIKit.UIImage
import platform.Vision.VNImageRequestHandler
import platform.Vision.VNRecognizeTextRequest
import platform.Vision.VNRecognizedText
import platform.Vision.VNRecognizedTextObservation
import platform.Vision.VNRequestTextRecognitionLevelAccurate

actual fun createTextRecognizer(recognitionLanguages: List<String>): TextRecognizer = IosTextRecognizer(recognitionLanguages)

@OptIn(ExperimentalForeignApi::class)
private class IosTextRecognizer(
    private val recognitionLanguages: List<String>,
) : TextRecognizer {
    override suspend fun recognize(encodedImage: ByteArray): List<TextObservation> {
        val image = UIImage.imageWithData(encodedImage.toNSData()) ?: return emptyList()
        val cgImage = image.CGImage ?: return emptyList()
        val request =
            VNRecognizeTextRequest().apply {
                recognitionLevel = VNRequestTextRecognitionLevelAccurate
                usesLanguageCorrection = recognitionLanguages.isNotEmpty()
                if (recognitionLanguages.isNotEmpty()) {
                    this.recognitionLanguages = recognitionLanguages
                }
            }
        val handler =
            VNImageRequestHandler(
                cGImage = cgImage,
                options = emptyMap<Any?, Any?>(),
            )
        if (!handler.performRequests(listOf(request), error = null)) {
            return emptyList()
        }
        return request.results.orEmpty().mapNotNull { result ->
            val observation = result as? VNRecognizedTextObservation ?: return@mapNotNull null
            val candidate =
                observation.topCandidates(1u).firstOrNull() as? VNRecognizedText
                    ?: return@mapNotNull null
            val content = candidate.string.takeIf { it.isNotBlank() } ?: return@mapNotNull null
            val bounds =
                observation.boundingBox.useContents {
                    visionBottomLeftToTopLeft(
                        x = origin.x.toFloat(),
                        y = origin.y.toFloat(),
                        width = size.width.toFloat(),
                        height = size.height.toFloat(),
                    )
                }
            TextObservation(
                text = content,
                confidence = candidate.confidence,
                boundingBox = bounds,
            )
        }
    }

    override fun close() = Unit
}

@OptIn(ExperimentalForeignApi::class)
private fun ByteArray.toNSData(): NSData =
    usePinned { pinned ->
        NSData.dataWithBytes(pinned.addressOf(0), size.toULong())
    }
