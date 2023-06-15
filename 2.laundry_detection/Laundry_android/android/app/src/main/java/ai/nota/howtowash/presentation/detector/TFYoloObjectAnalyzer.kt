package ai.nota.howtowash.presentation.detector

import android.content.res.AssetFileDescriptor
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.impl.utils.MatrixExt.postRotate
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import java.io.InputStream

class TFYoloObjectAnalyzer(
    fileDescriptor: AssetFileDescriptor,
    label: InputStream,
    private val listener: (image: Bitmap?, width: Int, height: Int, List<Recognition>, latency: Long, fps: Float) -> Unit
): ImageAnalysis.Analyzer {
    private val objectDetector: YoloProcessingDetector
    private lateinit var bitmap: Bitmap
    private var rotationDegrees = 0

    init {
        objectDetector = YoloProcessingDetector.createFromFile(fileDescriptor, label)
    }

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val startTime = System.currentTimeMillis()
        rotationDegrees = imageProxy.imageInfo.rotationDegrees

        if (!::bitmap.isInitialized) {
            bitmap = Bitmap.createBitmap(
                imageProxy.width, imageProxy.height, Bitmap.Config.ARGB_8888)
        }

        imageProxy.use { bitmap.copyPixelsFromBuffer(imageProxy.planes[0].buffer) }

        val rotatedBitmap = bitmap.rotate(rotationDegrees.toFloat())

        val imageProcessor = ImageProcessor.Builder().build()

        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(rotatedBitmap))
        val objects = objectDetector.detect(tensorImage)
        val latency = if (objects.isEmpty()) -1 else System.currentTimeMillis() - startTime
        val fps = String.format("%.2f", 1000.toFloat() / latency.toFloat()).toFloat()

        try {
            listener(rotatedBitmap, rotatedBitmap.width, rotatedBitmap.height, objects, latency, fps)
        } catch (e: Exception) {
            Log.e("ObjectAnalyzer", "analyze: ", e)
        }

        imageProxy.close()
    }
}

private fun Bitmap.rotate(degrees: Float): Bitmap =
    Bitmap.createBitmap(this, 0, 0, width, height, Matrix().apply { postRotate(degrees) }, true)