package ai.nota.pothole.feature.detection

import ai.nota.pothole.component.camera.Camera
import ai.nota.pothole.component.overlay.DetectionOverlay
import ai.nota.pothole.detector.TFYoloObjectAnalyzer
import ai.nota.pothole.ext.findActivity
import android.content.pm.ActivityInfo
import androidx.camera.core.AspectRatio
import androidx.camera.core.ImageAnalysis
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import org.tensorflow.lite.task.vision.detector.Detection
import java.util.concurrent.Executors

@Composable
fun DetectionContent(
    modelType: String
) {
    val context = LocalContext.current

    val modelInputStream = context.assets.open("$modelType.tflite")
    var width by remember { mutableStateOf(0) }
    var height by remember { mutableStateOf(0) }
    var latency by remember { mutableStateOf(0L) }
    var fps by remember { mutableStateOf(0f) }

    val objectState = remember { mutableStateListOf<Detection>() }

    context.findActivity()?.apply {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        DisposableEffect(key1 = Unit) {
            onDispose {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }
    }

    val imageAnalysis = ImageAnalysis.Builder()
        .setTargetAspectRatio(AspectRatio.RATIO_16_9)
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
        .build()

    LaunchedEffect(Unit) {
        val analyzer = TFYoloObjectAnalyzer(modelInputStream, null) { imageWidth, imageHeight, detection, inferenceLatency, inferenceFps ->
            width = imageWidth
            height = imageHeight
            if (inferenceLatency != -1L) {
                latency = inferenceLatency
                fps = inferenceFps
            }
            objectState.clear()
            objectState.addAll(detection)
        }

        imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), analyzer)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Camera(
            modifier = Modifier.fillMaxSize(),
            imageAnalysis = imageAnalysis
        )

        DetectionOverlay(
            imageWidth = width,
            imageHeight = height,
            objects = objectState
        )

        Column(modifier = Modifier.align(Alignment.TopStart)) {
            Text(
                text = "latency: ${latency}ms",
                color = Color.White,
            )
            Text(
                text = "fps: ${fps} FPS",
                color = Color.White,
            )
        }
    }
}