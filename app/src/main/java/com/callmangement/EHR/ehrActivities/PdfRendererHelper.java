package com.callmangement.EHR.ehrActivities;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.IOException;

public class PdfRendererHelper  {

    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page currentPage;

    public PdfRendererHelper(File pdfFile) throws IOException {
        ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);
        pdfRenderer = new PdfRenderer(fileDescriptor);
    }

    public Bitmap renderPage(int pageIndex) {
        if (currentPage != null) {
            currentPage.close();
        }
        currentPage = pdfRenderer.openPage(pageIndex);
        Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(), Bitmap.Config.ARGB_8888);
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        return bitmap;
    }

    public int getPageCount() {
        return pdfRenderer.getPageCount();
    }

    public void close() {
        if (currentPage != null) {
            currentPage.close();
        }
        pdfRenderer.close();
    }
}