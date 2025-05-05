package com.callmangement.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class CompressImage {
    private fun getRealPathFromURI(contentURI: String, context: Context): String? {
        val contentUri = Uri.parse(contentURI)
        @SuppressLint("Recycle") val cursor =
            context.contentResolver.query(contentUri, null, null, null, null)
        if (cursor == null) {
            return contentUri.path
        } else {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            return cursor.getString(index)
        }
    }

    companion object {
        fun compress(mfilepath: String, context: Context): String {
            try {
                var scaledBitmap: Bitmap? = null

                val options = BitmapFactory.Options()

                options.inJustDecodeBounds = true
                var bmp = BitmapFactory.decodeFile(mfilepath, options)

                var actualHeight = options.outHeight
                var actualWidth = options.outWidth

                /*float maxHeight = 816.0f;
            float maxWidth = 612.0f;*/
                /*float maxHeight = 1920.0f;
            float maxWidth = 1080.0f;*/
                val maxHeight = 1200.0f
                val maxWidth = 944.0f
                var imgRatio = (actualWidth / actualHeight).toFloat()
                val maxRatio = maxWidth / maxHeight

                if (actualHeight > maxHeight || actualWidth > maxWidth) {
                    if (imgRatio < maxRatio) {
                        imgRatio = maxHeight / actualHeight
                        actualWidth = (imgRatio * actualWidth).toInt()
                        actualHeight = maxHeight.toInt()
                    } else if (imgRatio > maxRatio) {
                        imgRatio = maxWidth / actualWidth
                        actualHeight = (imgRatio * actualHeight).toInt()
                        actualWidth = maxWidth.toInt()
                    } else {
                        actualHeight = maxHeight.toInt()
                        actualWidth = maxWidth.toInt()
                    }
                }

                //      setting inSampleSize value allows to load a scaled down version of the original image
                options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)

                //      inJustDecodeBounds set to false to load the actual bitmap
                options.inJustDecodeBounds = false

                //      this options allow android to claim the bitmap memory if it runs low on memory
                options.inPurgeable = true
                options.inInputShareable = true
                options.inTempStorage = ByteArray(16 * 1024)

                try {
                    //          load the bitmap from its path
                    bmp = BitmapFactory.decodeFile(mfilepath, options)
                } catch (exception: OutOfMemoryError) {
                    exception.printStackTrace()
                }
                try {
                    scaledBitmap =
                        Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
                } catch (exception: OutOfMemoryError) {
                    exception.printStackTrace()
                }

                val ratioX = actualWidth / options.outWidth.toFloat()
                val ratioY = actualHeight / options.outHeight.toFloat()
                val middleX = actualWidth / 2.0f
                val middleY = actualHeight / 2.0f

                val scaleMatrix = Matrix()
                scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)

                val canvas = Canvas(scaledBitmap!!)
                canvas.setMatrix(scaleMatrix)
                canvas.drawBitmap(
                    bmp, middleX - bmp.width / 2, middleY - bmp.height / 2, Paint(
                        Paint.FILTER_BITMAP_FLAG
                    )
                )

                //      check the rotation of the image and display it properly
                val exif: ExifInterface
                try {
                    exif = ExifInterface(mfilepath)

                    val orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, 0
                    )

                    val matrix = Matrix()
                    if (orientation == 6) {
                        matrix.postRotate(90f)
                    } else if (orientation == 3) {
                        matrix.postRotate(180f)
                    } else if (orientation == 8) {
                        matrix.postRotate(270f)
                    }
                    scaledBitmap = Bitmap.createBitmap(
                        scaledBitmap, 0, 0,
                        scaledBitmap.width, scaledBitmap.height, matrix,
                        true
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                val out: FileOutputStream
                val filepath = getFilename(context)
                try {
                    out = FileOutputStream(filepath)
                    //          write the compressed bitmap at the destination specified by filename.
                    scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    return filepath
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ""
        }

        private fun calculateInSampleSize(
            options: BitmapFactory.Options,
            reqWidth: Int,
            reqHeight: Int
        ): Int {
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1

            if (height > reqHeight || width > reqWidth) {
                val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
                val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
                inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
            }
            val totalPixels = (width * height).toFloat()
            val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++
            }

            return inSampleSize
        }

        fun getFilename(context: Context): String {
            val file = File(
                context.filesDir,  /*Environment.getExternalStorageDirectory().getPath()*/
                "Epdsfrt/Images"
            )
            if (!file.exists()) {
                file.mkdirs()
            }
            val uriSting = (file.absolutePath + "/" + System.currentTimeMillis() + ".jpg")
            return uriSting
        }
    }
}
