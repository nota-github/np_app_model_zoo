package ai.nota.howtowash.presentation.navigation.home

import ai.nota.howtowash.presentation.feature.home.HomeScreen
import ai.nota.howtowash.presentation.navigation.base.BaseDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

object HomeDestination : BaseDestination {
    override val route: String = "home"
    override val destination: String = "home"
}

fun NavGraphBuilder.installHomeDestination(
    navigateToDetectionScreen: (modelType: String) -> Unit
) {
    composable(
        route = HomeDestination.route
    ) {
        HomeScreen(navigateToDetectionScreen = navigateToDetectionScreen)
    }
}