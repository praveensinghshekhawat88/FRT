package com.callmangement.report_pdf

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.print.PrintAttributes
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.FileProvider
import com.callmangement.R
import com.callmangement.support.pdfcreator.activity.PDFViewerActivity
import com.callmangement.support.pdfcreator.utils.PDFUtil
import java.net.URLConnection

class PdfViewerActivity : PDFViewerActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = "Pdf Viewer"
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_pdf_viewer, menu)
        // return true so that the menu pop up is opened
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        } else if (item.itemId == R.id.menuPrintPdf) {
            val fileToPrint = pdfFile
            if (fileToPrint == null || !fileToPrint.exists()) {
                Toast.makeText(this, R.string.text_generated_file_error, Toast.LENGTH_SHORT).show()
                return super.onOptionsItemSelected(item)
            }

            val printAttributeBuilder = PrintAttributes.Builder()
            printAttributeBuilder.setMediaSize(PrintAttributes.MediaSize.ISO_A4)
            printAttributeBuilder.setMinMargins(PrintAttributes.Margins.NO_MARGINS)

            PDFUtil.printPdf(this@PdfViewerActivity, fileToPrint, printAttributeBuilder.build())
        } else if (item.itemId == R.id.menuSharePdf) {
            val fileToShare = pdfFile
            if (fileToShare == null || !fileToShare.exists()) {
                Toast.makeText(this, R.string.text_generated_file_error, Toast.LENGTH_SHORT).show()
                return super.onOptionsItemSelected(item)
            }

            val intentShareFile = Intent(Intent.ACTION_SEND)

            val apkURI = FileProvider.getUriForFile(
                applicationContext,
                applicationContext.packageName + ".provider",
                fileToShare
            )
            intentShareFile.setDataAndType(
                apkURI,
                URLConnection.guessContentTypeFromName(fileToShare.name)
            )
            intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(fileToShare))
            startActivity(Intent.createChooser(intentShareFile, "Share File"))
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
