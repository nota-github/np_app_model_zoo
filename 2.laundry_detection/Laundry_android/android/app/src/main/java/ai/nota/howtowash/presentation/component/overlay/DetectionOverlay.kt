package ai.nota.howtowash.presentation.component.overlay

import ai.nota.howtowash.presentation.detector.Recognition
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach

@Composable
fun DetectionOverlay(
    modifier: Modifier = Modifier,
    imageWidth: Int,
    imageHeight: Int,
    objects: List<Recognition>
) {
    var width by remember { mutableStateOf(1) }
    var height by remember { mutableStateOf(1) }

    var scale by remember { mutableStateOf(1f) }
    var xOffset by remember { mutableStateOf(0f) }
    var yOffset by remember { mutableStateOf(0f) }

    if (imageWidth != 0 && imageHeight != 0 && width != 0 && height != 0) {
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
                    width = coordinates.parentLayoutCoordinates?.size!!.width
                    height = coordinates.parentLayoutCoordinates?.size!!.height
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
                    height = boxHeight
                )
            }
    }
}

private fun DrawScope.drawBoundingBox(
    top: Float,
    width: Float,
    left: Float,
    height: Float,
) {
    drawRect(
        color = Color.Black,
        topLeft = Offset(left, top),
        size = Size(width, height),
        style = Stroke(width = 1.dp.toPx())
    )
}