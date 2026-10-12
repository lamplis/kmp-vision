# Jump start

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
