package ai.nota.pothole.component.overlay

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.util.fastForEach
import org.tensorflow.lite.task.vision.detector.Detection

@OptIn(ExperimentalTextApi::class)
@Composable
fun DetectionOverlay(
    modifier: Modifier = Modifier,
    imageWidth: Int,
    imageHeight: Int,
    objects: List<Detection>
) {
    var width by remember { mutableStateOf(1) }
    var height by remember { mutableStateOf(1) }

    var scale by remember { mutableStateOf(1f) }
    var xOffset by remember { mutableStateOf(0f) }
    var yOffset by remember { mutableStateOf(0f) }
    val textMeasurer = rememberTextMeasurer()

    if (imageWidth != 0 && imageHeight != 0) {
        val overlayRatio = (width / height).toFloat()
        val imageRatio = (imageWidth / imageHeight).toFloat()

        if (overlayRatio < imageRatio) {
            scale = height / imageHeight.toFloat()
            xOffset = (imageWidth * scale - width) * 0.5f
            yOffset = 0f
        } else {
            scale = width / imageWidth.toFloat()
            xOffset = 0f
            yOffset = (imageHeight * scale - height) * 0.5f
        }
    }

    Canvas(
        modifier = modifier.then(
            Modifier
                .fillMaxSize()
                .onGloballyPositioned { coordinates ->
                    width = coordinates.size.width
                    height = coordinates.size.height
                }
        )
    ) {
        if (objects.isNotEmpty())
            objects.fastForEach { obj ->

                val boundingBox = obj.boundingBox

                val top = (boundingBox.top * scale) - yOffset
                val left = (boundingBox.left * scale) - xOffset
                val boxWidth = boundingBox.width() * scale
                val boxHeight = boundingBox.height() * scale

                drawBoundingBox(
                    top = top,
                    left = left,
                    width = boxWidth,
                    height = boxHeight,
                    textMeasurer = textMeasurer
                )
            }
    }
}
@OptIn(ExperimentalTextApi::class)
private fun DrawScope.drawBoundingBox(
    top: Float,
    width: Float,
    left: Float,
    height: Float,
    textMeasurer: TextMeasurer
) {
    val measuredText = textMeasurer.measure(
        AnnotatedString("Warning!!"),
        style = TextStyle(fontSize = 10.sp)
    )

    drawRect(
        color = Color.Black,
        size = Size(width + 5.dp.toPx(), measuredText.size.height.toFloat()),
        topLeft = Offset(left - 5.dp.toPx() / 2.toFloat(), top - measuredText.size.height),
    )
    drawText(
        measuredText,
        color = Color.Red,
        topLeft = Offset(left - 5.dp.toPx() / 2.toFloat(), top - measuredText.size.height),
    )
    drawRect(
        color = Color.Black,
        topLeft = Offset(left, top),
        size = Size(width, height),
        style = Stroke(width = 5.dp.toPx())
    )
}