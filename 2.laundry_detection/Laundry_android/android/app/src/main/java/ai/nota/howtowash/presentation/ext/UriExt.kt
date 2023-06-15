package ai.nota.howtowash.presentation.ext

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream

@Suppress("DEPRECATION")
fun Uri.asCacheDir(cacheDir: File, contentResolver: ContentResolver): String {
    contentResolver.query(this, null, null, null, null)?.let {
        if (it.moveToNext()) {
            val displayName = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))

            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver,this))
            } else {
                MediaStore.Images.Media.getBitmap(contentResolver, this)
            }

            val storage: File = cacheDir
            val tempFile = File(storage, displayName)
            tempFile.createNewFile()
            val out = FileOutputStream(tempFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.close()
            it.close()

            return "${cacheDir}/${displayName}.png"
        } else {
            it.close()
            return ""
        }
    }

    return ""
}