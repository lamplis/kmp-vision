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
package io.github.lamplis.vision.overlay

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun VisionOverlay(
    shapes: List<OverlayShape>,
    modifier: Modifier = Modifier,
    strokeColor: Color = MaterialTheme.colors.primary,
    strokeWidth: Dp = 1.dp,
    labelColor: Color = MaterialTheme.colors.primary,
) {
    BoxWithConstraints(modifier = modifier) {
        shapes.forEach { shape ->
            val bounds =
                when (shape) {
                    is OverlayShape.Rect -> shape.bounds
                    is OverlayShape.Label -> shape.bounds
                }
            val left = maxWidth * bounds.left
            val top = maxHeight * bounds.top
            val boxWidth = maxWidth * (bounds.right - bounds.left)
            val boxHeight = maxHeight * (bounds.bottom - bounds.top)
            Box(
                modifier =
                    Modifier
                        .offset(x = left, y = top)
                        .size(width = boxWidth, height = boxHeight)
                        .border(width = strokeWidth, color = strokeColor),
            )
            if (shape is OverlayShape.Label) {
                Text(
                    text = shape.text,
                    color = labelColor,
                    modifier =
                        Modifier
                            .offset(x = left, y = top)
                            .padding(4.dp),
                )
            }
        }
    }
}
