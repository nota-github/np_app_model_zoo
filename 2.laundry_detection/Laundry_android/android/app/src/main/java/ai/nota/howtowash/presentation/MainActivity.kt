package ai.nota.howtowash.presentation

import ai.nota.howtowash.presentation.theme.HowToWashTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HowToWashTheme {
                MainContent {
                    finishAndRemoveTask()
                }
            }
        }
    }
}