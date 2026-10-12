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

import kotlin.test.Test
import kotlin.test.assertEquals

class VisionBoxFlipTest {
    @Test
    fun flipsVisionBottomLeftBoxToTopLeft() {
        assertEquals(
            NormalizedRect(left = 0.1f, top = 0.4f, right = 0.4f, bottom = 0.8f),
            visionBottomLeftToTopLeft(x = 0.1f, y = 0.2f, width = 0.3f, height = 0.4f),
        )
    }
}
