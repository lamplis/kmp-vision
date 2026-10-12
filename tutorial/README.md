# Vision Lab

A runnable KMP-Vision tutorial: one Compose screen, real native OCR on Android and iOS, and overlays drawn from the returned coordinates. The bundled card is generated specifically for this project and contains no personal information.

## Run

From the repository root, with JDK 17+ and Android SDK API 37 installed:

```sh
./gradlew :tutorial:android:installDebug
adb shell am start -n io.github.lamplis.vision.tutorial/.MainActivity
```

For iOS, open `tutorial/ios/VisionLab.xcodeproj`, choose the VisionLab scheme and an arm64 iPhone simulator, and Run. Xcode's build phase builds the shared framework. On a physical device, configure your own signing team. The tutorial targets iOS 15+; camera capture requires a physical iOS device.

Tap **Read text**, toggle **Boxes**, then try **Pick image** or **Use camera**. No OCR results are hardcoded. The `common` module holds the UI and recognition workflow; `android` and `ios` hold the application wrappers.

Read the [step-by-step guide](https://lamplis.github.io/kmp-vision/tutorial/).

## Documentation captures

The optional capture flag triggers recognition after 4.5 seconds, using exactly the normal OCR path. It does not alter text, coordinates, or outputs. Without the flag, recognition is manual.

Android (with the app installed and an emulator booted):

```sh
adb shell am force-stop io.github.lamplis.vision.tutorial
adb shell am start -n io.github.lamplis.vision.tutorial/.MainActivity -f 0x10008000 --ez capture true
adb exec-out screencap -p > before.png
# After recognition completes:
adb exec-out screencap -p > after.png
```

For iOS, build/install the app using Xcode, then:

```sh
xcrun simctl launch booted io.github.lamplis.vision.tutorial -capture
xcrun simctl io booted screenshot before-ios.png
# After recognition completes:
xcrun simctl io booted screenshot after-ios.png
```

`tools/build_doc_previews.py` combines the captured platform screenshots into the homepage comparison and creates a GIF from the before/after Android captures. It only resizes and frames captures; it does not fabricate application state or recognition results. See its `--help` for inputs. Pillow is required for this offline asset tool.

The diagram in the models guide explains model packaging; this tutorial does not bundle a model or claim to demonstrate inference. The camera preview captured on the Android emulator uses its virtual scene.
