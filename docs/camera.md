# Camera

`io.github.lamplis:vision-camera` is built by `:peekaboo-ui`. `PeekabooCamera` shows the preview. `rememberPeekabooCameraState` holds the session: `setTorchActive` and `toggleTorch` control the torch, and `capture()` takes a still. Pass `permissionDeniedContent` when camera access is denied. `onScannerFrame` delivers each `PeekabooCameraFrame`. `LiveFramePump` calls `toJpeg()` at most once per `minIntervalMs` (default 300 ms) and passes that JPEG to a `FrameAnalyzer`. The pump lives in `io.github.lamplis.vision.camera`.

![Camera preview](images/camera.jpg)
