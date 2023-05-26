package ai.nota.pothole.detector

import android.graphics.RectF


data class Recognitions(
    val labelId: Int,
    var labelName: String,
    var confidence: Float,
    val boundingBox: RectF,
)