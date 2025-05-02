package com.callmangement.EHR.support;

import com.itextpdf.text.pdf.PdfContentByte;

public class SolidLine implements LineDash {
    public void applyLineDash(PdfContentByte canvas) { }
}

class DottedLine implements LineDash {
    public void applyLineDash(PdfContentByte canvas) {
        canvas.setLineCap(PdfContentByte.LINE_CAP_ROUND);
        canvas.setLineDash(0, 4, 2);
    }
}

class DashedLine implements LineDash {
    public void applyLineDash(PdfContentByte canvas) {
        canvas.setLineDash(3, 3);
    }
}