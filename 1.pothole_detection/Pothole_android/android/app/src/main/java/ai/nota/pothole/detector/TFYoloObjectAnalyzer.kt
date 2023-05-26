package ai.nota.pothole.detector

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.Detection
import java.io.File
import java.io.InputStream

class TFYoloObjectAnalyzer(
    model: InputStream,
    label: File?,
    private val listener: (width: Int, height: Int, List<Detection>, latency: Long, fps: Float) -> Unit
): ImageAnalysis.Analyzer {
    private val objectDetector: YoloProcessingDetector
    private lateinit var bitmap: Bitmap
    private var rotationDegrees = 0

    init {
        objectDetector = if (label != null)
            YoloProcessingDetector.createFromFile(model, label)
        else
            YoloProcessingDetector.createFromFile(model)
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

        val imageProcessor = ImageProcessor.Builder().build()

        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmap))
        val objects = objectDetector.detect(tensorImage)
        val latency = if (objects.isEmpty()) -1 else System.currentTimeMillis() - startTime
        val fps = String.format("%.2f", 1000.toFloat() / latency.toFloat()).toFloat()

        try {
            listener(imageProxy.width,  imageProxy.height, objects, latency, fps)
        } catch (e: Exception) {
            Log.e("ObjectAnalyzer", "analyze: ", e)
        }

        imageProxy.close()
    }
}