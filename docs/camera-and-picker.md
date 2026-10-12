# Camera and picker

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
