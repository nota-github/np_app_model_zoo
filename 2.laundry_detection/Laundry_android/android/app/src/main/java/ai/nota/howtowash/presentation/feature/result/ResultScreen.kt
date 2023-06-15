package ai.nota.howtowash.presentation.feature.result
import ai.nota.howtowash.presentation.detector.Recognition
import androidx.compose.runtime.Composable

@Composable
fun ResultScreen(
    imagePath: String,
    objects: List<Recognition>,
    latency: Long,
    fps: Float
) {
    ResultContent(imagePath, objects.distinctBy { it.labelName }, latency, fps)
}

