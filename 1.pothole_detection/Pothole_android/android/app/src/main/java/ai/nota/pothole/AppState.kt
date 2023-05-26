package ai.nota.pothole

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun rememberAppState(
    navController: NavHostController = rememberNavController()
): AppState {
    return remember(navController) {
        AppState(navController)
    }
}

class AppState(val navController: NavHostController) {
    fun popBackStack() {
        navController.popBackStack()
    }

    fun popBackStack(route: String, inclusive: Boolean = true, saveState: Boolean = false) {
        navController.popBackStack(route, inclusive, saveState)
    }

    fun navigate(route: String) {
        navController.navigate(route)
    }
}