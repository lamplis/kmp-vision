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

![Live OCR on the camera](images/live.gif)

## Install

The minimum supported Android SDK is 24 (Android 7.0). Add the artifacts you use. They are on Maven Central.

```toml
[versions]
kmp-vision = "0.6.0"

[libraries]
vision-camera = { module = "io.github.lamplis:vision-camera", version.ref = "kmp-vision" }
vision-overlay = { module = "io.github.lamplis:vision-overlay", version.ref = "kmp-vision" }
vision-picker = { module = "io.github.lamplis:vision-picker", version.ref = "kmp-vision" }
vision-ocr = { module = "io.github.lamplis:vision-ocr", version.ref = "kmp-vision" }
vision-infer = { module = "io.github.lamplis:vision-infer", version.ref = "kmp-vision" }
```

```kotlin
commonMain {
    dependencies {
        implementation(libs.vision.camera)
        implementation(libs.vision.overlay)
        implementation(libs.vision.picker)
        implementation(libs.vision.ocr)
        implementation(libs.vision.infer)
    }
}
```

`vision-camera` already exposes `vision-overlay` on the compile classpath, so a live preview can depend on the camera artifact alone. Depend on `vision-overlay` by itself when you draw boxes and never open the camera.

## Artifacts

### vision-camera

`io.github.lamplis:vision-camera` is built by `:peekaboo-ui`. `PeekabooCamera` shows the preview. `rememberPeekabooCameraState` holds the session: `setTorchActive` and `toggleTorch` control the torch, and `capture()` takes a still. Pass `permissionDeniedContent` when camera access is denied. `onScannerFrame` delivers each `PeekabooCameraFrame`. `LiveFramePump` calls `toJpeg()` at most once per `minIntervalMs` (default 300 ms) and passes that JPEG to a `FrameAnalyzer`. The pump lives in `io.github.lamplis.vision.camera`.

![Camera preview](images/camera.jpg)

### vision-overlay

`io.github.lamplis:vision-overlay` is built by `:vision-overlay`. The Kotlin package is `io.github.lamplis.vision.overlay`. `VisionOverlay` draws `OverlayShape.Rect` and `OverlayShape.Label` from top-left normalized boxes. It does not open the camera. Pass `strokeColor`, `strokeWidth`, and `labelColor` to restyle the boxes.

![Detection boxes](images/overlay.jpg)

### vision-picker

`io.github.lamplis:vision-picker` is built by `:peekaboo-image-picker`. The Kotlin package is `com.preat.peekaboo.image.picker`. `rememberImagePickerLauncher` opens the system photo picker for `SelectionMode.Single` or `SelectionMode.Multiple`. The selected images arrive in `onResult`. Dismissing the picker calls `onResult` with an empty list. `ResizeOptions` and `FilterOptions` are available. The samples below show both.

![Photo picker](images/picker.jpg)

### vision-ocr

`io.github.lamplis:vision-ocr` is built by `:vision-ocr`. The Kotlin package is `io.github.lamplis.vision.ocr`. `createOcr()` reads an encoded image and returns `OcrLine`s. Each `boundingBox` is a top-left `NormalizedRect` (`left`, `top`, `right`, `bottom`). Android uses ML Kit text recognition. iOS uses Vision `VNRecognizeTextRequest`. `visionBottomLeftToTopLeft` maps a Vision box into that top-left rect; callers of `createOcr()` already receive top-left boxes. The approach follows [Compose Multiplatform Text Recognition](https://github.com/Amirhosseinijadi/ComposeMultiplatformTextRecognition) by Amirhossein Ijadi. That repository is not copied into this one.

```kotlin
val recognizer = createOcr()
val lines = recognizer.recognize(jpeg)
lines.forEach { line ->
    val box = line.boundingBox
    println("${line.text} ${box.left},${box.top} ${box.right},${box.bottom}")
}
recognizer.close()
```

![OCR lines](images/ocr.jpg)

### vision-infer

`io.github.lamplis:vision-infer` is built by `:vision-infer`. The Kotlin package is `io.github.lamplis.vision.infer`. `loadOptimizedModel(OptimizedVisionModel)` picks the file for the current platform when one object holds both the float32 `.tflite` bytes and the `.mlmodelc` directory. `loadVisionInterpreter` loads a model you already have: `ModelFormat.Tflite` plus bytes on Android, `ModelFormat.CoreMl` plus a `.mlmodelc` path on iOS. Both return the same `VisionInterpreter`. Android runs on LiteRT and keeps the GPU only when a short trial is faster than CPU. iOS runs on Core ML and uses the Neural Engine when `preferAccelerator` is true. `run` returns `VisionTensor` values. The app does not load ONNX bytes at runtime. The Models section below shows both ways to supply the files.

## Models

### Keep one ONNX file

Install the converter once, then turn that file into the two models the app loads:

```bash
pip install -r tools/requirements-vision.txt
./gradlew :vision-infer:convertVisionOnnx -PvisionOnnx=model.onnx
```

The task writes `android/<stem>.tflite` and `ios/<stem>.mlmodelc` under `vision-infer/build/vision-models`. Pass both to `loadOptimizedModel`. Android runs the TFLite file on LiteRT and uses the GPU when a short trial is faster than CPU. iOS runs the Core ML bundle on the Neural Engine. The app does not load ONNX bytes at runtime.

### Models you already have

Ship a float32 `.tflite` and a compiled `.mlmodelc` yourself. `loadVisionInterpreter` returns the same `VisionInterpreter` on both platforms. `run` returns `VisionTensor` values. Android still uses LiteRT and keeps the GPU when a short trial is faster than CPU. iOS still uses the Neural Engine when `preferAccelerator` is true. Use `loadOptimizedModel` when one `OptimizedVisionModel` holds both files.

```kotlin
// Android: float32 .tflite bytes
loadVisionInterpreter("", ModelFormat.Tflite, tfliteBytes)

// iOS: compiled .mlmodelc directory
loadVisionInterpreter(mlmodelcPath, ModelFormat.CoreMl, null)
```

![Model on device](images/model.jpg)

## Camera and picker

Add this key to the iOS `Info.plist` before the preview can open:

```xml
<key>NSCameraUsageDescription</key>
<string>This app uses camera for capturing photos.</string>
```

```kotlin
@Composable
fun CustomCameraView() {
    val state = rememberPeekabooCameraState(onCapture = { /* Handle captured images */ })
    PeekabooCamera(
        state = state,
        modifier = Modifier.fillMaxSize(),
        permissionDeniedContent = {
            // Custom UI content for permission denied scenario
        },
    )
    Button(onClick = state::capture) {
        Text("Capture")
    }
}
```

`state` is the `PeekabooCameraState` that controls the camera. `permissionDeniedContent` replaces the preview when camera access is denied.

```kotlin
val scope = rememberCoroutineScope()

val singleImagePicker = rememberImagePickerLauncher(
    selectionMode = SelectionMode.Single,
    scope = scope,
    onResult = { byteArrays ->
        byteArrays.firstOrNull()?.let {
            println(it)
        }
    }
)

Button(onClick = { singleImagePicker.launch() }) {
    Text("Pick Single Image")
}
```

```kotlin
val multipleImagePicker = rememberImagePickerLauncher(
    selectionMode = SelectionMode.Multiple(maxSelection = 5),
    scope = rememberCoroutineScope(),
    onResult = { byteArrays ->
        byteArrays.forEach { println(it) }
    }
)

Button(onClick = { multipleImagePicker.launch() }) {
    Text("Pick Multiple Images")
}
```


## Image Resizing Options
`peekaboo` offers customizable resizing options for both single and multiple image selections, along with a new feature to resize images only if they exceed a certain file size. <br/>
This feature allows you to resize the selected images to specific dimensions, optimizing them for your application's requirements and enhancing performance.

- The default resizing dimensions are set to `800 x 800` pixels.
- The default threshold for resizing is set to `1MB`, meaning images larger than this size will be resized.
- You can customize the resizing **dimensions**, **threshold**, and now the **compression quality** according to your needs.

### Usage
Set the `resizeOptions` parameter in `rememberImagePickerLauncher` with your desired dimensions, threshold and compression quality:

```kotlin
val resizeOptions = ResizeOptions(
    width = 1200, // Custom width
    height = 1200, // Custom height
    resizeThresholdBytes = 2 * 1024 * 1024L, // Custom threshold for 2MB,
    compressionQuality = 0.5 // Adjust compression quality (0.0 to 1.0)
)
```

#### Single Image Selection with Resizing
```kotlin
val singleImagePicker = rememberImagePickerLauncher(
    selectionMode = SelectionMode.Single,
    scope = rememberCoroutineScope(),
    resizeOptions = resizeOptions,
    onResult = { byteArrays ->
        byteArrays.firstOrNull()?.let {
            // Process the resized image's ByteArray
            println(it)
        }
    }
)
```

#### Multiple Images Selection with Resizing
```kotlin
val multipleImagePicker = rememberImagePickerLauncher(
    selectionMode = SelectionMode.Multiple(maxSelection = 5),
    scope = rememberCoroutineScope(),
    resizeOptions = resizeOptions,
    onResult = { byteArrays ->
        byteArrays.forEach {
            // Process the resized images' ByteArrays
            println(it)
        }
    }
)
```

>💡 Note: While resizing, the aspect ratio of the original images is preserved. The final dimensions may slightly vary to maintain the original proportions.

<br/>

## Image Filter Options
**`peekaboo-image-picker`** now offers customizable filter options for selected images.

This feature is available on both `Android` and `iOS` devices.

| Default                                                         | GrayScale                                                     | Sepia                                                     | Invert                                                     |
|-----------------------------------------------------------------|---------------------------------------------------------|---------------------------------------------------------|---------------------------------------------------------|
| <img src="https://github.com/TEAM-PREAT/peekaboo/assets/76798309/2f47becf-1512-47e2-83c1-2120d58d6d11" width="300" height="700"> | <img src="https://github.com/TEAM-PREAT/peekaboo/assets/76798309/3c864a79-83df-451c-91e1-1e71fbdb3066" width="300" height="700"> | <img src="https://github.com/TEAM-PREAT/peekaboo/assets/76798309/52dee8e8-1979-4725-bfca-12af1b3c4b3d" width="300" height="700"> | <img src="https://github.com/TEAM-PREAT/peekaboo/assets/76798309/cbe927d1-55c2-40c5-ae35-2be81997e9fb" width="300" height="700"> |

### Usage
Set the `filterOptions` parameter in `rememberImagePickerLauncher`:

```kotlin
val imagePicker = rememberImagePickerLauncher(
    selectionMode = SelectionMode.Single,
    scope = rememberCoroutineScope(),
    filterOptions = FilterOptions.GrayScale,
    onResult = { byteArrays ->
        // Process the filtered images' ByteArrays
    }
)
```

>💡 Note: The default filter option is `Default`, which applies no filter.
> Choose from `GrayScale`, `Sepia`, or `Invert` for different effects.

<br/>

## ByteArray to ImageBitmap Conversion
We've added a new extension function `toImageBitmap()` to convert a `ByteArray` into an `ImageBitmap`. <br/>
This function simplifies the process of converting image data into a displayable format, enhancing the app's capability to handle image processing efficiently.

### Usage
```kotlin
val imageBitmap = byteArray.toImageBitmap()
```

<br/>

## Contributions 🙏

Contributions are always welcome!

If you'd like to contribute, please feel free to create a PR or open an issue. 👍

<br/>

## Stargazers :star:
Support it by joining __[stargazers](https://github.com/onseok/peekaboo/stargazers)__ for this repository. :star: <br>

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
