package com.callmangement.support

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ImageUtilsForRotate {
    @JvmStatic
    @Throws(IOException::class)
    fun ensurePortrait(imagePath: String): Bitmap {
        // Decode the image from file
        var bitmap = BitmapFactory.decodeFile(imagePath)

        // Check if the image is in landscape mode
        if (bitmap.width > bitmap.height) {
            // Rotate the image to portrait mode
            bitmap = rotateBitmap(bitmap, 90)
        }

        // (Optional) Save the corrected image back to the file
        saveBitmapToFile(bitmap, imagePath)

        return bitmap
    }

    private fun rotateBitmap(bitmap: Bitmap, degrees: Int): Bitmap {
        // Create a Matrix object to rotate the image
        val matrix = Matrix()
        matrix.postRotate(degrees.toFloat())

        // Return the rotated bitmap
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    @Throws(IOException::class)
    private fun saveBitmapToFile(bitmap: Bitmap, filePath: String) {
        val file = File(filePath)
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
    }
}
