package ai.nota.pothole.detector

import android.graphics.RectF
import org.jetbrains.kotlinx.multik.api.d1array
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.D3
import org.jetbrains.kotlinx.multik.ndarray.data.DN
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.NDArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.set
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.label.Category
import org.tensorflow.lite.task.vision.detector.Detection
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.IllegalArgumentException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.MappedByteBuffer
import kotlin.math.exp


fun IntArray.permute(vararg changeIndex: Int): IntArray {
    val newArray = IntArray(this.size) { 0 }
    for (i in 0 until this.size) {
        newArray[i] = this[changeIndex[i]]
    }
    return newArray
}

fun IntArray.multiplyAll(): Int {
    var value = 1
    forEach {
        value *= it
    }
    return value
}

fun FloatBuffer.toFloatArray(): FloatArray {
    val floatArray = FloatArray(capacity())
    for (i in 0 until capacity()) {
        floatArray[i] = this[i]
    }

    return floatArray
}

class YoloProcessingDetector(
    modelFile: InputStream,
    labelFile: File? = null,
    private val detectThreshold: Float = 0.4f,
    private val iouThreshold: Float = 0.4f,
    private var isNeedPostProcess: Boolean = false,
    private val numOfDetections: Int = 3,
    private var isUInt8: Boolean = false
) {

    private val anchor = arrayOf(
        arrayOf(arrayOf(7.6640625, 4.0625), arrayOf(13.359375, 8.9296875), arrayOf(16.375, 18.9375)),
        arrayOf(arrayOf(31.859375, 12.0234375), arrayOf(28.734375, 21.28125), arrayOf(27.3125, 36.6875)),
        arrayOf(arrayOf(56.03125, 22.890625), arrayOf(42.25, 65.75), arrayOf(141.75, 57.96875))
    )

    private lateinit var tfLiteInterpreter: Interpreter
    private val labels: ArrayList<String> = arrayListOf("손")
    private lateinit var result: ByteBuffer
    init {
        if (labelFile != null) {
            labels.clear()
            val inputStream = labelFile.inputStream()
            val br = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            while (br.readLine().also { line = it } != null) {
                line?.let { labels.add(it) }
            }
            br.close()
        }

        try {
            tfLiteInterpreter =
                Interpreter(
                    loadModelFile(modelFile),
                    Interpreter.Options()
                        .setNumThreads(4)
                        .setUseNNAPI(true)
                )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        fun createFromFile(modelFile: InputStream, labelFile: File): YoloProcessingDetector {
            return YoloProcessingDetector(modelFile, labelFile)
        }


        fun createFromFile(modelFile: InputStream): YoloProcessingDetector {
            return YoloProcessingDetector(modelFile, null)
        }
    }

    @Throws(IOException::class)
    private fun loadModelFile(inputStream: InputStream): ByteBuffer {
        val bytes = inputStream.readBytes()
        inputStream.read(bytes)
        result = MappedByteBuffer.allocateDirect(bytes.size)
        result.order(ByteOrder.nativeOrder())
        result.put(bytes)
        inputStream.close()
        return result
    }

    fun detect(tensorImage: TensorImage): List<Detection> {
        isNeedPostProcess = tfLiteInterpreter.outputTensorCount != 1

        val inputShape = createInputShape()
        val outputShapes = createOutputShapes()
        val outputArrays = createOutputArrays(outputShapes)
        val inferenceOutput: HashMap<Int, Any> = inference(inputShape, tensorImage, outputArrays)
        return  if (isNeedPostProcess)
            postProcessing(inputShape, outputShapes, inferenceOutput)
        else {
            val output = (inferenceOutput[0] as FloatBuffer).toFloatArray()
            val result = mk.d1array(output.size) { output[it] }.reshape(outputShapes[0][0], outputShapes[0][1], outputShapes[0][2])
            locateItemFromBitmap(inputShape, result)
        }.run {
            if (size > numOfDetections) {
                subList(0, numOfDetections)
            } else {
                this
            }
        }
    }

    private fun createOutputShapes(): Array<IntArray> {
        return Array(tfLiteInterpreter.outputTensorCount) { tensorIdx ->
            tfLiteInterpreter.getOutputTensor(tensorIdx).shape()
        }
    }
    private fun createInputShape(): IntArray {
        return tfLiteInterpreter.getInputTensor(0).shape().apply {
            if (this[1] == 3) permute(0, 2, 3, 1)
        }
    }
    private fun createOutputArrays(outputShapes: Array<IntArray>): HashMap<Int, Any> {
        return HashMap<Int, Any>().apply {
            for (i in 0 until tfLiteInterpreter.outputTensorCount) {
                this[i] = ByteBuffer.allocateDirect(outputShapes[i].multiplyAll() * 4)
                    .asFloatBuffer()
            }
        }
    }
    private fun inference(
        inputShape: IntArray,
        tensorImage: TensorImage,
        outputShapes: HashMap<Int, Any>
    ): HashMap<Int, Any> {
        val imageProcessor = ImageProcessor.Builder()
            .add(
                ResizeOp(
                    inputShape[1],
                    inputShape[2],
                    ResizeOp.ResizeMethod.BILINEAR
                )
            )
            .add(NormalizeOp(0f, 255f))
            .build()
        val tensorProcessedImage = imageProcessor.process(tensorImage)

        tfLiteInterpreter.runForMultipleInputsOutputs(arrayOf(tensorProcessedImage.buffer), outputShapes)
        return outputShapes
    }

    private fun postProcessing(inputShape: IntArray, outputShapes: Array<IntArray>, inferenceOutput: HashMap<Int, Any>): List<Detection> {
        val allDetections = arrayListOf<Recognitions>()

        fun sigmoid(x: Float): Float {
            return 1 / (1 + exp(-x))
        }


        val inferenceOutputArrays = ArrayList<NDArray<Float, DN>>()
        inferenceOutput.forEach {
            val out = (it.value as FloatBuffer).toFloatArray()
            val firstShape = outputShapes[0]
            val secondShape = outputShapes[1]
            val thirdShape = outputShapes[2]
            val outputArray = when(out.size) {
                firstShape.multiplyAll() ->
                    mk.d1array(out.size) { out[it] }.reshape(firstShape[0], firstShape[1],  firstShape[2], firstShape[3], firstShape[4])
                secondShape.multiplyAll() ->
                    mk.d1array(out.size) { out[it] }.reshape(secondShape[0], secondShape[1],  secondShape[2], secondShape[3], secondShape[4])
                thirdShape.multiplyAll() ->
                    mk.d1array(out.size) { out[it] }.reshape(thirdShape[0], thirdShape[1],  thirdShape[2], thirdShape[3], thirdShape[4])
                else -> throw IllegalArgumentException()
            }
            inferenceOutputArrays.add(outputArray)
        }
        inferenceOutputArrays.sortByDescending { it.size }

        val stride = arrayOf(8, 16, 32)
        var outputSize = 0

        for (i in 0 until 3) {
            val opt = inferenceOutputArrays[i].transpose(0, 2, 1, 4, 3) // 1,20,3,6,20 -> 1,3,20,20,6
            val (_, nc, ny, nx, _) = opt.shape

            for (c in 0 until nc)
                for (gy in 0 until ny) {
                    for (gx in 0 until nx) {
                        val confidence = sigmoid(opt[0, c, gy, gx, 4])
                        if (confidence > detectThreshold) {
                            val arr = mk.d1array(6) { 0f }
                            val width = sigmoid(opt[0, c, gy, gx, 2])
                            val height = sigmoid(opt[0, c, gy, gx, 3])
                            arr[0] = ((sigmoid(opt[0, c, gy, gx, 0]) * 2.0f - 0.5f + gx) * stride[i]) / inputShape[1]
                            arr[1] = ((sigmoid(opt[0, c, gy, gx, 1]) * 2.0f - 0.5f + gy) * stride[i]) / inputShape[2]
                            arr[2] = (((width * 2.0f) * (width * 2.0f)) * anchor[i][c][0].toFloat()) / inputShape[1]
                            arr[3] = (((height * 2.0f) * (height * 2.0f)) * anchor[i][c][1].toFloat()) / inputShape[2]
                            arr[4] = confidence
                            arr[5] = sigmoid(opt[0, c, gy, gx, 5])
                            findDetection(allDetections, arr)
                        }
                    }
                }
            outputSize += nc * ny * nx
        }
        val nmsDetections: ArrayList<Recognitions> = nms(outputSize, allDetections)
        return nmsDetections.map {
            Detection.create(it.boundingBox, listOf(
                Category(it.labelName, 0f)
            ))
        }
    }

    private fun locateItemFromBitmap(inputShape: IntArray, output: NDArray<Float, D3>) : List<Detection> {
        val allDetections = arrayListOf<Recognitions>()
        for (i in 0 until output.shape[0]) {
            for (j in 0 until output.shape[1]) {
                findDetection(allDetections, output[i, j])
            }
        }

        val nmsDetections: ArrayList<Recognitions> = nms(output.shape[2], allDetections)
        return nmsDetections.map {
            Detection.create(it.boundingBox, listOf(
                Category(it.labelName, 0f)
            ))
        }
    }

    private fun findDetection(allDetections: ArrayList<Recognitions>, item: MultiArray<Float, D1>) {
        val confidence: Float = item[4]

        if (confidence > detectThreshold) {
            val classIndex: Float = item[5]
            val x: Float = item[0] * 1280
            val y: Float = item[1] * 720
            val w: Float = item[2] * 1280
            val h: Float = item[3] * 720

            val x1 = x - (w / 2)
            val y1 = y - (h / 2)
            val x2 = x + (w / 2)
            val y2 = y + (h / 2)
            val recognition = Recognitions(
                classIndex.toInt(),
                labels[classIndex.toInt()],
                confidence,
                RectF(x1, y1, x2, y2)
            )
            allDetections.add(recognition)
        }
    }

    private fun nms(outputSize: Int, allDetections: ArrayList<Recognitions>): ArrayList<Recognitions> {
        allDetections.sortByDescending { it.confidence }
        val nmsDetections: ArrayList<Recognitions> = ArrayList()
        val detectedItems = arrayListOf<Recognitions>()
        for (i in 0 until outputSize - 5) {
            for (j in allDetections.indices) {
                if (allDetections[j].labelId == i) {
                    detectedItems.add(allDetections[j])
                }
            }

            while (detectedItems.size > 0) {
                val detections: Array<Recognitions> = detectedItems.toTypedArray()
                val max: Recognitions = detections[0]
                nmsDetections.add(max)
                detectedItems.clear()
                for (k in 1 until detections.size) {
                    val recognitions: Recognitions = detections[k]
                    if (boxIou(max.boundingBox, recognitions.boundingBox) < iouThreshold) {
                        detectedItems.add(recognitions)
                    }
                }
            }
        }
        return nmsDetections
    }

    private fun boxIou(a: RectF, b: RectF): Float {
        val intersection = boxIntersection(a, b)
        val union = boxUnion(a, b)
        return if (union <= 0) 1f else intersection / union
    }

    private fun boxIntersection(
        a: RectF,
        b: RectF
    ): Float {
        val maxLeft = if (a.left > b.left) a.left else b.left
        val maxTop = if (a.top > b.top) a.top else b.top
        val minRight = if (a.right < b.right) a.right else b.right
        val minBottom = if (a.bottom < b.bottom) a.bottom else b.bottom
        val w = minRight - maxLeft
        val h = minBottom - maxTop
        return if (w < 0 || h < 0) 0f else w * h
    }

    private fun boxUnion(a: RectF, b: RectF): Float {
        val i = boxIntersection(a, b)
        return (a.right - a.left) * (a.bottom - a.top) + (b.right - b.left) * (b.bottom - b.top) - i
    }

}