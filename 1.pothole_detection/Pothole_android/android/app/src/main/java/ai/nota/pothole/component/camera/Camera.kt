package ai.nota.pothole.component.camera

import ai.nota.pothole.ext.getCameraProvider
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.launch

@Composable
fun Camera(
    modifier: Modifier = Modifier,
    imageAnalysis: ImageAnalysis? = null,
    scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
) {
    val coroutineScope = rememberCoroutineScope()
    var cameraProvider: ProcessCameraProvider? = null

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = PreviewView(context).apply {
        this.scaleType = scaleType
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    val preview = Preview.Builder()
        .build()
        .also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

    DisposableEffect(Unit) {
        onDispose {
            previewView.controller?.clearImageAnalysisAnalyzer()
            cameraProvider?.unbindAll()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = {
            coroutineScope.launch {
                cameraProvider = context.getCameraProvider()

                try {
                    if (imageAnalysis == null) {
                        cameraProvider!!.bindToLifecycle(
                            lifecycleOwner, cameraSelector, preview
                        )
                    } else {
                        cameraProvider!!.bindToLifecycle(
                            lifecycleOwner, cameraSelector, preview, imageAnalysis
                        )
                    }
                } catch (e: Exception) {
                    Log.e("Camera", "Binding Failed", e)
                }
            }

            previewView
        }
    )
}