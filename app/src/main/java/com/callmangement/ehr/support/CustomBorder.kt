package com.callmangement.ehr.support

import com.itextpdf.text.Rectangle
import com.itextpdf.text.pdf.PdfContentByte
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPCellEvent
import com.itextpdf.text.pdf.PdfPTable

class CustomBorder(
    protected var left: LineDash?, protected var right: LineDash?,
    protected var top: LineDash?, protected var bottom: LineDash?
) :
    PdfPCellEvent {
    override fun cellLayout(
        cell: PdfPCell, position: Rectangle,
        canvases: Array<PdfContentByte>
    ) {
        val canvas = canvases[PdfPTable.LINECANVAS]
        if (top != null) {
            canvas.saveState()
            top!!.applyLineDash(canvas)
            canvas.moveTo(position.right, position.top)
            canvas.lineTo(position.left, position.top)
            canvas.stroke()
            canvas.restoreState()
        }
        if (bottom != null) {
            canvas.saveState()
            bottom!!.applyLineDash(canvas)
            canvas.moveTo(position.right, position.bottom)
            canvas.lineTo(position.left, position.bottom)
            canvas.stroke()
            canvas.restoreState()
        }
        if (right != null) {
            canvas.saveState()
            right!!.applyLineDash(canvas)
            canvas.moveTo(position.right, position.top)
            canvas.lineTo(position.right, position.bottom)
            canvas.stroke()
            canvas.restoreState()
        }
        if (left != null) {
            canvas.saveState()
            left!!.applyLineDash(canvas)
            canvas.moveTo(position.left, position.top)
            canvas.lineTo(position.left, position.bottom)
            canvas.stroke()
            canvas.restoreState()
        }
    }
}
