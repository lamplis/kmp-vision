package io.github.lamplis.vision.tutorial

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.preat.peekaboo.image.picker.toImageBitmap
import com.preat.peekaboo.ui.camera.PeekabooCamera
import com.preat.peekaboo.ui.camera.rememberPeekabooCameraState
import io.github.lamplis.vision.ocr.OcrLine
import io.github.lamplis.vision.ocr.createOcr
import io.github.lamplis.vision.overlay.NormalizedRect
import io.github.lamplis.vision.overlay.OverlayShape
import io.github.lamplis.vision.overlay.VisionOverlay
import io.github.lamplis.vision.tutorial.resources.Res
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay

private val Ink = Color(0xFF0D1823)
private val Panel = Color(0xFF172735)
private val Mint = Color(0xFF74F0C0)
private val Muted = Color(0xFFA7BDCC)

/** One shared screen. Capture mode only automates interactions; OCR results are never mocked. */
@Composable
fun VisionTutorial(
    platform: String,
    captureMode: Boolean = false,
) {
    var bytes by remember { mutableStateOf<ByteArray?>(null) }
    var lines by remember { mutableStateOf<List<OcrLine>>(emptyList()) }
    var request by remember { mutableStateOf(0) }
    var busy by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("Ready to recognize") }
    var showBoxes by remember { mutableStateOf(true) }
    var camera by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val picker =
        rememberImagePickerLauncher(
            selectionMode = SelectionMode.Single,
            scope = scope,
            onResult = { result ->
                result.firstOrNull()?.let {
                    bytes = it
                    lines = emptyList()
                    message = "Image loaded. Tap Read text."
                }
            },
        )
    LaunchedEffect(Unit) { bytes = Res.readBytes("files/vision-card.png") }
    LaunchedEffect(captureMode) {
        if (captureMode) {
            delay(4500)
            request++
        }
    }
    LaunchedEffect(request) {
        if (request == 0) return@LaunchedEffect
        val input = bytes ?: return@LaunchedEffect
        busy = true
        lines = emptyList()
        message = "Recognizing on device…"
        val recognizer = createOcr()
        try {
            lines = recognizer.recognize(input)
            message = if (lines.isEmpty()) "No text found. Try another image." else "${lines.size} lines recognized on device"
            println("VisionTutorial: $platform: ${lines.size} lines recognized")
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Exception) {
            message = "Recognition failed. Try another image."
        } finally {
            recognizer.close()
            busy = false
        }
    }
    val bitmap = remember(bytes) { bytes?.toImageBitmap() }
    val shapes =
        remember(lines) {
            lines.map { line ->
                val b = line.boundingBox
                OverlayShape.Rect(NormalizedRect(b.left, b.top, b.right, b.bottom))
            }
        }
    MaterialTheme(colors = darkColors(primary = Mint, background = Ink, surface = Panel, onPrimary = Ink)) {
        Surface(color = Ink, modifier = Modifier.fillMaxSize().background(Ink).windowInsetsPadding(WindowInsets.safeDrawing)) {
            if (camera) {
                val state =
                    rememberPeekabooCameraState(onCapture = { image ->
                        image?.let {
                            bytes = it
                            lines = emptyList()
                            message = "Photo captured. Tap Read text."
                        }
                        camera = false
                    })
                Box(Modifier.fillMaxSize()) {
                    PeekabooCamera(state = state, modifier = Modifier.fillMaxSize(), permissionDeniedContent = {
                        Text("Camera permission is required. Enable it in Settings.", modifier = Modifier.padding(32.dp))
                    })
                    Row(Modifier.fillMaxWidth().padding(24.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Button(onClick = { camera = false }) { Text("Back") }
                        Button(onClick = state::capture, enabled = state.isCameraReady) { Text("Capture") }
                    }
                }
            } else {
                Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 16.dp)) {
                    Text("KMP–VISION  /  TUTORIAL 01", color = Mint, fontSize = 11.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    Text("Vision Lab", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("One shared screen. Two native engines.", color = Muted, fontSize = 14.sp)
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(platform, color = Mint, fontSize = 12.sp)
                        Text("ON-DEVICE OCR", color = Muted, fontSize = 10.sp, letterSpacing = 1.sp)
                    }
                    Spacer(Modifier.height(12.dp))
                    if (bitmap != null) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .aspectRatio(bitmap.width.toFloat() / bitmap.height)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White),
                        ) {
                            Image(
                                bitmap,
                                "Input image for text recognition",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.FillBounds,
                            )
                            if (showBoxes) {
                                VisionOverlay(
                                    shapes,
                                    Modifier.fillMaxSize(),
                                    strokeColor = Color(0xFF087D62),
                                    strokeWidth = 2.dp,
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(message, color = if (lines.isEmpty()) Muted else Mint, fontSize = 13.sp)
                    if (busy) LinearProgressIndicator(Modifier.fillMaxWidth().padding(top = 8.dp), color = Mint)
                    Spacer(Modifier.height(10.dp))
                    Button(
                        onClick = {
                            request++
                        },
                        enabled = bytes != null && !busy,
                        modifier =
                            Modifier.fillMaxWidth().height(
                                48.dp,
                            ),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text("Read text", fontWeight = FontWeight.Bold)
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        TextButton(onClick = { picker.launch() }, enabled = !busy, modifier = Modifier.weight(1f)) { Text("Pick image") }
                        TextButton(onClick = { camera = true }, enabled = !busy, modifier = Modifier.weight(1f)) { Text("Use camera") }
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            "RECOGNIZED TEXT",
                            color = Muted,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(top = 15.dp),
                        )
                        Row {
                            Text("Boxes", color = Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 14.dp))
                            Switch(checked = showBoxes, onCheckedChange = { showBoxes = it })
                        }
                    }
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Panel)
                            .padding(16.dp),
                    ) {
                        if (lines.isEmpty()) {
                            Text(
                                "Tap Read text to run native recognition.\nNo API key. No recognition server.",
                                color = Muted,
                                fontSize = 13.sp,
                            )
                        }
                        lines.forEach { Text(it.text, color = Color.White, fontSize = 14.sp, modifier = Modifier.padding(vertical = 1.dp)) }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("Kotlin + Compose Multiplatform", color = Mint, fontSize = 12.sp)
                    Text("Open source. Built to be shared.", color = Muted, fontSize = 12.sp)
                }
            }
        }
    }
}
