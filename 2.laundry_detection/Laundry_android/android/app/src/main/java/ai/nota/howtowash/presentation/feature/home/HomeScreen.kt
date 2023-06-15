package ai.nota.howtowash.presentation.feature.home

import androidx.compose.runtime.Composable

@Composable
fun HomeScreen(navigateToDetectionScreen: (modelType: String) -> Unit) {
    HomeContent(navigateToDetectionScreen = navigateToDetectionScreen)
}