package io.github.lamplis.vision.tutorial

import androidx.compose.ui.window.ComposeUIViewController
import platform.Foundation.NSProcessInfo

@Suppress("FunctionName")
fun MainViewController() =
    ComposeUIViewController {
        VisionTutorial(platform = "iOS / Apple Vision", captureMode = NSProcessInfo.processInfo.arguments.contains("-capture"))
    }
