package ai.nota.pothole

import ai.nota.pothole.ui.theme.Pothole_DetectorTheme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Pothole_DetectorTheme {
                MainContent {
                    finishAndRemoveTask()
                }
            }
        }
    }
}