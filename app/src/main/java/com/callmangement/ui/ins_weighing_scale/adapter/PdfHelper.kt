package com.callmangement.ui.ins_weighing_scale.adapter

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File

object PdfHelper {
    fun openPdfFile(context: Context, fileName: String, fileUrl: String?) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val uri = Uri.parse(fileUrl)

        val request = DownloadManager.Request(uri)
        request.setTitle(fileName)
        request.setDescription("Downloading")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

        downloadManager.enqueue(request)


        val file = File(context.getExternalFilesDir(null), fileName)
        val pdfURi =
            FileProvider.getUriForFile(context, context.packageName + ".provider", file, fileName)
        val share = Intent()
        share.setAction(Intent.ACTION_SEND)
        // share.setType("Attendance/pdf");
        share.setDataAndType(pdfURi, "application/pdf")
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        share.putExtra(Intent.EXTRA_STREAM, pdfURi)
        context.startActivity(share)
        // startActivity(Intent.createChooser(share, "Share via"));
        Log.d("yesss", "ijowh$pdfURi")
    }
}
