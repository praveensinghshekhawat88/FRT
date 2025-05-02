package com.callmangement.support.pdfcreator.views.basic;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.callmangement.support.pdfcreator.views.basic.PDFView;

import java.io.Serializable;

public class PDFHorizontalView extends com.callmangement.support.pdfcreator.views.basic.PDFView implements Serializable {

    public PDFHorizontalView(Context context) {
        super(context);

        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams childLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0);
        linearLayout.setLayoutParams(childLayoutParams);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        super.setView(linearLayout);
    }

    @Override
    public PDFHorizontalView addView(@NonNull PDFView viewToAdd) {
        getView().addView(viewToAdd.getView());
        super.addView(viewToAdd);
        return this;
    }

    @Override
    public PDFHorizontalView setLayout(@NonNull ViewGroup.LayoutParams layoutParams) {
        super.setLayout(layoutParams);
        return this;
    }

    @Override
    public LinearLayout getView() {
        return (LinearLayout) super.getView();
    }
}
