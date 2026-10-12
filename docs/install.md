# Install

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
