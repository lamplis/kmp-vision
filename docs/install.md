# Get started

The fastest verified way to try KMP-Vision is the [Vision Lab tutorial](tutorial.md). It builds directly against the modules in this repository and runs on Android and iOS.

## Run from source

```bash
git clone https://github.com/lamplis/kmp-vision.git
cd kmp-vision
./gradlew :tutorial:android:installDebug
```

For iOS, open `tutorial/ios/VisionLab.xcodeproj` in Xcode and run the **VisionLab** scheme on an arm64 simulator. See the [tutorial](tutorial.md) for requirements and a walkthrough.

## Add a checkout to an existing project

Clone KMP-Vision alongside your application. In your application's `settings.gradle.kts`, include it as a composite build and map the artifact names to the source modules:

```kotlin
includeBuild("../kmp-vision") {
    dependencySubstitution {
        substitute(module("io.github.lamplis:vision-camera"))
            .using(project(":vision-camera"))
        substitute(module("io.github.lamplis:vision-gallery"))
            .using(project(":vision-gallery"))
        substitute(module("io.github.lamplis:vision-picker"))
            .using(project(":vision-picker"))
        substitute(module("io.github.lamplis:vision-overlay"))
            .using(project(":vision-overlay"))
        substitute(module("io.github.lamplis:vision-ocr"))
            .using(project(":vision-ocr"))
        substitute(module("io.github.lamplis:vision-infer"))
            .using(project(":vision-infer"))
    }
}
```

Then choose your modules in the application's shared source set:

```kotlin
commonMain.dependencies {
    implementation("io.github.lamplis:vision-ocr:1.0.0")
    implementation("io.github.lamplis:vision-overlay:1.0.0")
    // Optional:
    // implementation("io.github.lamplis:vision-camera:1.0.0")
    // implementation("io.github.lamplis:vision-gallery:1.0.0")
    // implementation("io.github.lamplis:vision-picker:1.0.0")
    // implementation("io.github.lamplis:vision-infer:1.0.0")
}
```

Here the composite build resolves these coordinates to your local checkout. `1.0.0` is the source version in this repository; this example does not rely on Maven Central. Pin the checkout to the commit you have tested when adopting it in your application.

!!! note "Published artifacts"
    As checked on October 12, 2026, Maven Central does not expose metadata for `io.github.lamplis:vision-ocr`. Use the source workflow above until a published release is available. The repository's publishing configuration alone does not confirm that a release has been uploaded.

## Choose only what you need

| Artifact | Source module | Purpose |
| --- | --- | --- |
| `vision-camera` | `:vision-camera` | Compose camera UI and analysis frames |
| `vision-gallery` | `:vision-gallery` | Device photo gallery |
| `vision-picker` | `:vision-picker` | System image selection |
| `vision-overlay` | `:vision-overlay` | Rectangles and labels |
| `vision-ocr` | `:vision-ocr` | Native text recognition |
| `vision-infer` | `:vision-infer` | Compatible on-device models |

`vision-camera` exposes `vision-overlay` on its compile classpath. Add `vision-ocr` separately when recognizing text; the camera dependency alone does not include OCR.

## Platform setup

- **Android:** minimum API 24. The tutorial uses compile SDK 37 for the current Compose dependencies. Camera capture requires the camera permission; the tutorial declares it and handles the library's permission UI.
- **iOS:** the tutorial targets iOS 15+, with arm64 device and simulator targets. Add `NSCameraUsageDescription` before using camera capture. Physical camera capture needs a device.
- **Models:** package compatible `.tflite` and `.mlmodelc` assets and supply your own preprocessing. See [model integration](models.md).

The [camera and picker guide](camera-and-picker.md) covers app configuration and API examples.
