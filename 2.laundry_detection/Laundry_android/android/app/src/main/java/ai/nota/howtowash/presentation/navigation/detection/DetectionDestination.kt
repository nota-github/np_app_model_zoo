package ai.nota.howtowash.presentation.navigation.detection

import ai.nota.howtowash.presentation.detector.Recognition
import ai.nota.howtowash.presentation.feature.detection.DetectionScreen
import ai.nota.howtowash.presentation.feature.home.ModelType
import ai.nota.howtowash.presentation.navigation.base.BaseDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

object DetectionDestination : BaseDestination {
    override val route: String = "detection?modelType={modelType}"
    override val destination: String = "detection"
}

fun NavGraphBuilder.installDetectionDestination(
    navigateToResultScreen: (imagePath: String, objects: List<Recognition>, latency: Long, fps: Float) -> Unit
) {
    composable(
        route = DetectionDestination.route,
        arguments = listOf(
            navArgument("modelType") { defaultValue = ModelType.Medium.name }
        )
    ) {
        val modelType = it.arguments?.getString("modelType") ?: ModelType.Medium.name
        DetectionScreen(modelType, navigateToResultScreen)
    }
}