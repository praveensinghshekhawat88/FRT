package com.callmangement.support;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtilsForRotate {

    public static Bitmap ensurePortrait(String imagePath) throws IOException {
        // Decode the image from file
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

        // Check if the image is in landscape mode
        if (bitmap.getWidth() > bitmap.getHeight()) {
            // Rotate the image to portrait mode
            bitmap = rotateBitmap(bitmap, 90);
        }

        // (Optional) Save the corrected image back to the file
        saveBitmapToFile(bitmap, imagePath);

        return bitmap;
    }

    private static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        // Create a Matrix object to rotate the image
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);

        // Return the rotated bitmap
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private static void saveBitmapToFile(Bitmap bitmap, String filePath) throws IOException {
        File file = new File(filePath);
        FileOutputStream outputStream = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        outputStream.flush();
        outputStream.close();
    }
}
