# kmp-vision
[![Kotlin](https://img.shields.io/badge/kotlin-2.4.21-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-v1.12.1-blue)](https://github.com/JetBrains/compose-multiplatform)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.lamplis/vision-ocr?color=orange)](https://central.sonatype.com/namespace/io.github.lamplis)
[![Apache-2.0](https://img.shields.io/badge/License-Apache%202.0-green.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build](https://github.com/lamplis/kmp-vision/actions/workflows/ci_check.yml/badge.svg)](https://github.com/lamplis/kmp-vision/actions/workflows/ci_check.yml)
![badge-android](http://img.shields.io/badge/platform-android-6EDB8D.svg?style=flat)
![badge-ios](http://img.shields.io/badge/platform-ios-CDCDCD.svg?style=flat)

On-device camera, OCR, and models for Compose Multiplatform on Android and iOS.

## Jump start

One camera dependency draws the preview and the boxes. `LiveFramePump` throttles frames. `createOcr().recognize(jpeg)` reads each JPEG. `VisionOverlay` paints the lines. `strokeColor`, `strokeWidth`, and `labelColor` restyle the chrome. Boxes are top-left: `left`, `top`, `right`, `bottom`.

```kotlin
val pump = LiveFramePump { jpeg ->
    createOcr().recognize(jpeg).flatMap { line ->
        val box = NormalizedRect(line.boundingBox.left, line.boundingBox.top, line.boundingBox.right, line.boundingBox.bottom)
        listOf(OverlayShape.Rect(box), OverlayShape.Label(box, line.text))
    }
}
val state = rememberPeekabooCameraState(
    onScannerFrame = { frame ->
        scope.launch { shapes = pump.onFrame(frame, nowMs) }
    },
    onCapture = {},
)
Box(Modifier.fillMaxSize()) {
    PeekabooCamera(
        state = state,
        modifier = Modifier.fillMaxSize(),
        permissionDeniedContent = {},
    )
    VisionOverlay(
        shapes = shapes,
        modifier = Modifier.fillMaxSize(),
        strokeColor = Color(0xFF38D9A9),
        strokeWidth = 3.dp,
        labelColor = Color.White,
    )
}
```

![Live OCR on the camera](docs/images/live.gif)

Documentation: https://lamplis.github.io/kmp-vision/

## License

```
Copyright 2024 onseok

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
