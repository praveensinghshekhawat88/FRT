package com.callmangement.ehr.ehrActivities

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.callmangement.R
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.rendering.PDFRenderer
import java.io.File
import java.io.IOException

class PDFViewActivity : AppCompatActivity() {
    private var imageView: ImageView? = null
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_view)

        imageView = findViewById(R.id.pdfImageView)
        progressBar = findViewById(R.id.progressBar)

        val pdfPath = intent.getStringExtra("pdfPath")
        val file = File(pdfPath)

        if (file.exists()) {
            renderPdf(file)
        }
    }

    private fun renderPdf(file: File) {
        Thread {
            try {
                val document = PDDocument.load(file)
                val pdfRenderer = PDFRenderer(document)

                // Render the first page as a Bitmap
                val bitmap =
                    pdfRenderer.renderImageWithDPI(0, 300f) // Adjust DPI as needed

                runOnUiThread {
                    imageView!!.setImageBitmap(bitmap)
                    progressBar!!.visibility = View.GONE
                }

                document.close()
            } catch (e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    progressBar!!.visibility = View.GONE
                }
            }
        }.start()
    }
}