# Camera

`io.github.lamplis:vision-camera` is built by `:peekaboo-ui`. `PeekabooCamera` shows the preview. `rememberPeekabooCameraState` holds the session: `setTorchActive` and `toggleTorch` control the torch, and `capture()` takes a still. Pass `permissionDeniedContent` when camera access is denied. `onScannerFrame` delivers each `PeekabooCameraFrame`. `LiveFramePump` calls `toJpeg()` at most once per `minIntervalMs` (default 300 ms) and passes that JPEG to a `FrameAnalyzer`. The pump lives in `io.github.lamplis.vision.camera`.

![Vision Lab camera screen on the Android emulator](images/camera.png){ .guide-preview }

*Captured from [Vision Lab](tutorial.md), the runnable tutorial.*

The preview above uses the Android emulator’s virtual scene. For physical camera capture on iOS, run the tutorial on a device.

Before asynchronous analysis, call `frame.retainForAsyncAnalysis()` and always pair it with `frame.releaseAfterAsyncAnalysis()` in `finally`. Keep analysis bounded so new camera frames do not create an unbounded coroutine queue.
