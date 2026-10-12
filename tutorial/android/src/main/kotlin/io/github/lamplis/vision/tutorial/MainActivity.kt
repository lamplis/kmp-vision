package io.github.lamplis.vision.tutorial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { VisionTutorial(platform = "Android / ML Kit", captureMode = intent.getBooleanExtra("capture", false)) }
    }
}
