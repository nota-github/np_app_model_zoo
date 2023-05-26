package ai.nota.pothole.feature.home

import androidx.compose.runtime.Composable

@Composable
fun HomeScreen(navigateToTestScreen: (modelType: String) -> Unit) {
    HomeContent(navigateToDetectionScreen = navigateToTestScreen)
}