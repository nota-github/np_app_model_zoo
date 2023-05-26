package ai.nota.pothole.navigation.home

import ai.nota.pothole.feature.home.HomeScreen
import ai.nota.pothole.navigation.base.BaseDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

object HomeDestination : BaseDestination {
    override val route: String = "home"
    override val destination: String = "home"
}

fun NavGraphBuilder.installHomeDestination(
    navigateToTestScreen: (modelType: String) -> Unit
) {
    composable(
        route = HomeDestination.route
    ) {
        HomeScreen(navigateToTestScreen = navigateToTestScreen)
    }
}