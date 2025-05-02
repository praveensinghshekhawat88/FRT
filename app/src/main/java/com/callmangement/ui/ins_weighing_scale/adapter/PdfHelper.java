package com.callmangement.ui.ins_weighing_scale.adapter;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;

public class PdfHelper {

    public static void openPdfFile(Context context, String fileName, String fileUrl) {



            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

            Uri uri = Uri.parse(fileUrl);

            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setTitle(fileName);
            request.setDescription("Downloading");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

            downloadManager.enqueue(request);











        File file = new File(context.getExternalFilesDir(null), fileName);
        Uri pdfURi = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file , fileName );
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        // share.setType("Attendance/pdf");
        share.setDataAndType(pdfURi, "application/pdf");
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        share.putExtra(Intent.EXTRA_STREAM, pdfURi);
        context.startActivity(share);
        // startActivity(Intent.createChooser(share, "Share via"));
        Log.d("yesss","ijowh"+pdfURi);


    }
}
