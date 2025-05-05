package com.callmangement.ehr.support

import com.itextpdf.text.pdf.PdfContentByte

class SolidLine : LineDash {
    override fun applyLineDash(canvas: PdfContentByte?) {
        // Solid line: No dash
    }
}

class DottedLine : LineDash {
    override fun applyLineDash(canvas: PdfContentByte?) {
        canvas?.let {
            it.setLineCap(PdfContentByte.LINE_CAP_ROUND)
            it.setLineDash(0f, 4f, 2f)
        }
    }
}

class DashedLine : LineDash {
    override fun applyLineDash(canvas: PdfContentByte?) {
        canvas?.setLineDash(3f, 3f)
    }
}