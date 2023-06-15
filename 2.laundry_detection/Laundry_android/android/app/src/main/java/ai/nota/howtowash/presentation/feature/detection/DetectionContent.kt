package ai.nota.howtowash.presentation.feature.detection

import ai.nota.howtowash.presentation.component.camera.Camera
import ai.nota.howtowash.presentation.detector.Recognition
import ai.nota.howtowash.presentation.detector.TFYoloObjectAnalyzer
import ai.nota.howtowash.presentation.ext.findActivity
import ai.nota.howtowash.presentation.ext.saveImage
import android.content.pm.ActivityInfo
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import kotlin.random.Random

@Composable
fun DetectionContent(
    modelType: String,
    navigateToResultScreen: (imagePath: String, objects: List<Recognition>, latency: Long, fps: Float) -> Unit
) {
    val context = LocalContext.current

    val labelInputStream = context.assets.open("label.txt")
    val modelInputStream = context.assets.openFd("$modelType.tflite")
    var loadImageState by remember { mutableStateOf(false) }
    var latency by remember { mutableStateOf(0L) }
    var fps by remember { mutableStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()

//    val activity = context.findActivity()
//
//    activity.apply {
//        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//
//        DisposableEffect(key1 = Unit) {
//            onDispose {
//                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
//            }
//        }
//    }

    val imageAnalysis = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
        .build()

    LaunchedEffect(Unit) {
        val executors = Executors.newSingleThreadExecutor()
        val analyzer = TFYoloObjectAnalyzer(
            modelInputStream,
            labelInputStream
        ) { image, _, _, detection, inferenceLatency, inferenceFps ->
            if (inferenceLatency != -1L) {
                latency = inferenceLatency
                fps = inferenceFps
            }

            if (detection.isNotEmpty()) {
                loadImageState = true
                coroutineScope.launch(Dispatchers.IO) {
                    image?.saveImage(getRandomString(), context)?.let {
                        withContext(Dispatchers.Main) {
                            navigateToResultScreen(it, detection, inferenceLatency, inferenceFps)
                        }
                    }
                    loadImageState = false
                }
            }
        }

        imageAnalysis.setAnalyzer(executors, analyzer)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (!loadImageState) {
            Camera(
                modifier = Modifier.fillMaxSize(),
                imageAnalysis = imageAnalysis,
            )
        } else {
            Column(modifier = Modifier.align(Alignment.Center)) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                Text(text = "Image Loading ...")
            }
        }
    }
}

fun getRandomString() : String {
    val charset = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz0123456789"
    val length = Random(100).nextInt()
    return (1..length)
        .map { charset.random() }
        .joinToString("")
}