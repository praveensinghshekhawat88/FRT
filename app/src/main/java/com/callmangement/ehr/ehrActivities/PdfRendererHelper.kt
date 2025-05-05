package com.callmangement.ehr.ehrActivities

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import java.io.File

class PdfRendererHelper(pdfFile: File?) {
    private val pdfRenderer: PdfRenderer
    private var currentPage: PdfRenderer.Page? = null

    init {
        val fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
        pdfRenderer = PdfRenderer(fileDescriptor)
    }

    fun renderPage(pageIndex: Int): Bitmap {
        if (currentPage != null) {
            currentPage!!.close()
        }
        currentPage = pdfRenderer.openPage(pageIndex)
        val bitmap = Bitmap.createBitmap(
            currentPage!!.getWidth(),
            currentPage!!.getHeight(),
            Bitmap.Config.ARGB_8888
        )
        currentPage!!.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        return bitmap
    }

    val pageCount: Int
        get() = pdfRenderer.pageCount

    fun close() {
        if (currentPage != null) {
            currentPage!!.close()
        }
        pdfRenderer.close()
    }
}