package ai.nota.howtowash.presentation.feature.result

import ai.nota.howtowash.R
import ai.nota.howtowash.presentation.component.overlay.DetectionOverlay
import ai.nota.howtowash.presentation.detector.Recognition
import ai.nota.howtowash.presentation.type.WashClasses
import ai.nota.howtowash.presentation.type.WashType
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ResultContent(
    imagePath: String,
    objects: List<Recognition>,
    latency: Long,
    fps: Float
) {
    val bitmap = BitmapFactory.decodeFile(imagePath)

    LazyColumn {
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    modifier = Modifier.fillMaxWidth(),
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = "",
                    contentScale = ContentScale.Crop
                )
                DetectionOverlay(
                    imageWidth = bitmap.width,
                    imageHeight = bitmap.height,
                    objects = objects
                )
            }
        }

        item {
            Column(modifier = Modifier.padding(start = 10.dp, top = 10.dp)) {
                Text(text = "Latency : ${latency}ms")
                Text(text = "FPS : $fps")
            }
        }

        itemsIndexed(objects) { index: Int, item: Recognition ->
            val (washType, explain, icons) = WashClasses.valueOf(item.labelName)
            ClassItem(washType = washType, isLastIndex = index == objects.lastIndex, explain = explain, icons = icons)
        }
    }
}

@Composable
fun ClassItem(
    washType: WashType,
    isLastIndex: Boolean,
    @StringRes explain: Int,
    @DrawableRes vararg icons: Int,
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .padding(top = 10.dp, bottom = if (isLastIndex) 10.dp else 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icons.isNotEmpty()) {
            Icon(
                modifier = Modifier.size(48.dp),
                painter = painterResource(id = icons[0]),
                contentDescription = "세탁 기호"
            )
        }

        Column(Modifier.padding(start = 10.dp, bottom = 10.dp)) {
            Text(text = washType.type, style = MaterialTheme.typography.titleMedium, fontWeight = Bold)
            Text(text = context.getString(explain))
        }
    }
}

@Preview
@Composable
fun ClassItemPreview() {
    Column {
        Column(modifier = Modifier.padding(start = 10.dp, top = 10.dp)) {
            Text(text = "Latency : 1000ms")
            Text(text = "FPS : 1.0")
        }
        ClassItem(WashType.Ironing, isLastIndex = true, R.string.steaming_prohibited, R.drawable.ic_steaming_is_prohibited)
    }
}