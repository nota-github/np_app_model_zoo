package ai.nota.howtowash.presentation.feature.detection

import ai.nota.howtowash.presentation.detector.Recognition
import androidx.compose.runtime.Composable

@Composable
fun DetectionScreen(
    modelType: String,
    navigateToResultScreen: (imagePath: String, objects: List<Recognition>, latency: Long, fps: Float) -> Unit
) {
    DetectionContent(modelType, navigateToResultScreen)
}