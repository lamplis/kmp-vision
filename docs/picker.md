# Picker

`io.github.lamplis:vision-picker` is built by `:peekaboo-image-picker`. The Kotlin package is `com.preat.peekaboo.image.picker`. `rememberImagePickerLauncher` opens the system photo picker for `SelectionMode.Single` or `SelectionMode.Multiple`. The selected images arrive in `onResult`. Dismissing the picker calls `onResult` with an empty list. `ResizeOptions` and `FilterOptions` are available. The samples below show both.

![Photo picker](images/picker.jpg)

## Image resizing

Resizing applies to both single and multiple image selections, including a threshold so an image is resized only when it exceeds a file size.

- The default resizing dimensions are `800 x 800` pixels.
- The default threshold for resizing is `1MB`. Images larger than this size are resized.
- You can customize the resizing dimensions, threshold, and compression quality.

Set the `resizeOptions` parameter in `rememberImagePickerLauncher`:

```kotlin
val resizeOptions = ResizeOptions(
    width = 1200, // Custom width
    height = 1200, // Custom height
    resizeThresholdBytes = 2 * 1024 * 1024L, // Custom threshold for 2MB,
    compressionQuality = 0.5 // Adjust compression quality (0.0 to 1.0)
)
```

### Single image with resizing

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

### Multiple images with resizing

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

While resizing, the aspect ratio of the original images is preserved. The final dimensions may slightly vary to maintain the original proportions.

## Image filters

`vision-picker` offers filter options for selected images on Android and iOS.

| Default | GrayScale | Sepia | Invert |
| --- | --- | --- | --- |
| <img src="https://github.com/TEAM-PREAT/peekaboo/assets/76798309/2f47becf-1512-47e2-83c1-2120d58d6d11" width="300" height="700"> | <img src="https://github.com/TEAM-PREAT/peekaboo/assets/76798309/3c864a79-83df-451c-91e1-1e71fbdb3066" width="300" height="700"> | <img src="https://github.com/TEAM-PREAT/peekaboo/assets/76798309/52dee8e8-1979-4725-bfca-12af1b3c4b3d" width="300" height="700"> | <img src="https://github.com/TEAM-PREAT/peekaboo/assets/76798309/cbe927d1-55c2-40c5-ae35-2be81997e9fb" width="300" height="700"> |

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

The default filter option is `Default`, which applies no filter. Choose `GrayScale`, `Sepia`, or `Invert` for different effects.

## ByteArray to ImageBitmap

`toImageBitmap()` converts a `ByteArray` into an `ImageBitmap`.

```kotlin
val imageBitmap = byteArray.toImageBitmap()
```
