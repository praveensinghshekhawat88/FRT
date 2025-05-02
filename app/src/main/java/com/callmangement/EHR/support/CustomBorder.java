package com.callmangement.EHR.support;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;

public class CustomBorder implements PdfPCellEvent {
    protected LineDash left;
    protected LineDash right;
    protected LineDash top;
    protected LineDash bottom;
    public CustomBorder(LineDash left, LineDash right,
                        LineDash top, LineDash bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }
    public void cellLayout(PdfPCell cell, Rectangle position,
                           PdfContentByte[] canvases) {
        PdfContentByte canvas = canvases[PdfPTable.LINECANVAS];
        if (top != null) {
            canvas.saveState();
            top.applyLineDash(canvas);
            canvas.moveTo(position.getRight(), position.getTop());
            canvas.lineTo(position.getLeft(), position.getTop());
            canvas.stroke();
            canvas.restoreState();
        }
        if (bottom != null) {
            canvas.saveState();
            bottom.applyLineDash(canvas);
            canvas.moveTo(position.getRight(), position.getBottom());
            canvas.lineTo(position.getLeft(), position.getBottom());
            canvas.stroke();
            canvas.restoreState();
        }
        if (right != null) {
            canvas.saveState();
            right.applyLineDash(canvas);
            canvas.moveTo(position.getRight(), position.getTop());
            canvas.lineTo(position.getRight(), position.getBottom());
            canvas.stroke();
            canvas.restoreState();
        }
        if (left != null) {
            canvas.saveState();
            left.applyLineDash(canvas);
            canvas.moveTo(position.getLeft(), position.getTop());
            canvas.lineTo(position.getLeft(), position.getBottom());
            canvas.stroke();
            canvas.restoreState();
        }
    }
}
