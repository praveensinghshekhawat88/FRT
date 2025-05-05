package com.callmangement.ehr.support

import com.itextpdf.text.pdf.PdfContentByte

interface LineDash {
    fun applyLineDash(canvas: PdfContentByte?)
}
