package pet.loyal.provider.util

import android.media.ExifInterface
import android.text.TextUtils
import java.io.IOException
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import java.io.File
import java.io.FileOutputStream


fun getOrientationDegree(imagePath: String): Float {

    if (TextUtils.isEmpty(imagePath)) {
        return 0f
    }

    try {
        val exifInterface = ExifInterface(imagePath)
        val attributeInt =
            exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
        when (attributeInt) {
            ExifInterface.ORIENTATION_ROTATE_90 -> return 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> return 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> return 270f
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return 0f
}

fun rotateBitmap(bitmap: Bitmap?, degree: Float): Bitmap? {
    if (degree == 0f || bitmap == null) {
        return bitmap
    }

    val matrix = Matrix()
    matrix.setRotate(degree, bitmap.width / 2f, bitmap.height / 2f)
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

fun resetOrientation(filePath: File?) {

    if (filePath != null) {
        var bitmap = BitmapFactory.decodeFile(filePath.path)
        getOrientationDegree(filePath.path).apply {
            bitmap = rotateBitmap(bitmap, this)
            if (bitmap != null) {
                saveImageToExternalStorage(bitmap, filePath.path)
            }
        }
    }
}

private fun saveImageToExternalStorage(bitmap: Bitmap, filePath: String) {
    // Create a file to save the image
    val file = File(filePath)
    try {
        // Get the file output stream
        val stream = FileOutputStream(file)

        // Compress the bitmap
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

        // Flush the output stream
        stream.flush()

        // Close the output stream
        stream.close()
    } catch (e: IOException) { // Catch the exception
        e.printStackTrace()
    }
}
