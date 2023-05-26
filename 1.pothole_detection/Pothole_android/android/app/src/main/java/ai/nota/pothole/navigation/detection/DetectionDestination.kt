package ai.nota.pothole.navigation.detection

import ai.nota.pothole.feature.detection.DetectionScreen
import ai.nota.pothole.feature.home.ModelType
import ai.nota.pothole.navigation.base.BaseDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.tensorflow.lite.schema.ModelT

object DetectionDestination : BaseDestination {
    override val route: String = "detection?modelType={modelType}"
    override val destination: String = "detection"
}

fun NavGraphBuilder.installDetectionDestination() {
    composable(
        route = DetectionDestination.route,
        arguments = listOf(
            navArgument("modelType") { defaultValue = ModelType.Original.name }
        )
    ) {
        val modelType = it.arguments?.getString("modelType") ?: ModelType.Original.name
        DetectionScreen(modelType)
    }
}