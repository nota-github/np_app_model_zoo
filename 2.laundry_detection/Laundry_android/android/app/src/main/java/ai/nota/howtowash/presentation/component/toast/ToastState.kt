package ai.nota.howtowash.presentation.component.toast

import android.content.Context
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberToastState(
    context: Context = LocalContext.current
): ToastState {
    val toast = Toast.makeText(context, "", LENGTH_SHORT)
    return remember { ToastState(toast) }
}

@Stable
class ToastState(private val toast: Toast) {
    fun show(text: String) {
        toast.apply {
            setText(text)
            show()
        }
    }
}

