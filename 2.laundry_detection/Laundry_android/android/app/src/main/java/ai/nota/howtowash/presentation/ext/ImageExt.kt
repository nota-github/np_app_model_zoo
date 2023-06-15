package ai.nota.howtowash.presentation.ext

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

fun Bitmap.saveImage(name: String, context: Context): String {
    val cacheDir = context.cacheDir
    val fileName = "$name.png"
    val file = File(cacheDir, fileName)

    try {
        val out = FileOutputStream(file)
        compress(Bitmap.CompressFormat.PNG, 100, out)
        out.close()
    } catch (fe: FileNotFoundException) {
        Log.e("saveImage", "FileNotFoundException: ${fe.message}")
    } catch (e: IOException) {
        Log.e("saveImage", "IOException: ${e.message}")
    }

    return file.path
}