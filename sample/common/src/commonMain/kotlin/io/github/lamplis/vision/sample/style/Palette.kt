/*
 * Copyright 2023 onseok
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
package io.github.lamplis.vision.sample.style

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

object VisionColors {
    val background = Color(0xFFFFFFFF)
    val onBackground = Color(0xFF19191C)
    val uiLightBlack = Color(25, 25, 28).copy(alpha = 0.7f)
    val darkBackground = Color(0xFF19191C)
    val darkOnBackground = Color(0xFFFFFFFF)
    val primary = Color(0xFFE0E0E0)
    val onPrimary = Color(0xFF000000)
    val darkPrimary = Color(0xFF424242)
    val darkOnPrimary = Color(0xFFFFFFFF)
}

@Suppress("FunctionName")
@Composable
fun VisionTheme(content: @Composable () -> Unit) {
    val isDarkTheme = isSystemInDarkTheme()
    val colors =
        if (isDarkTheme) {
            MaterialTheme.colors.copy(
                background = VisionColors.darkBackground,
                onBackground = VisionColors.darkOnBackground,
                primary = VisionColors.darkPrimary,
                onPrimary = VisionColors.darkOnPrimary,
            )
        } else {
            MaterialTheme.colors.copy(
                background = VisionColors.background,
                onBackground = VisionColors.onBackground,
                primary = VisionColors.primary,
                onPrimary = VisionColors.onPrimary,
            )
        }

    MaterialTheme(
        colors = colors,
    ) {
        ProvideTextStyle(LocalTextStyle.current.copy(letterSpacing = 0.sp)) {
            content()
        }
    }
}
