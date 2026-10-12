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
package io.github.lamplis.vision.session

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FrameCadenceTest {
    @Test
    fun dropsFramesInsideTheInterval() {
        assertFalse(shouldAcceptFrame(nowMs = 1000L, lastAcceptedMs = 800L, minIntervalMs = 300L))
        assertTrue(shouldAcceptFrame(nowMs = 1100L, lastAcceptedMs = 800L, minIntervalMs = 300L))
    }
}
