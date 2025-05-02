package com.callmangement.report_pdf;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.callmangement.R;
import com.callmangement.model.fps_repeat_on_service_center.ModelRepeatFpsComplaintsList;
import com.callmangement.ui.distributor.model.PosDistributionDetail;
import com.callmangement.utils.Constants;
import com.callmangement.utils.DateTimeUtils;
import com.callmangement.support.pdfcreator.activity.PDFCreatorActivity;
import com.callmangement.support.pdfcreator.utils.PDFUtil;
import com.callmangement.support.pdfcreator.views.PDFBody;
import com.callmangement.support.pdfcreator.views.PDFFooterView;
import com.callmangement.support.pdfcreator.views.PDFHeaderView;
import com.callmangement.support.pdfcreator.views.PDFTableView;
import com.callmangement.support.pdfcreator.views.basic.PDFHorizontalView;
import com.callmangement.support.pdfcreator.views.basic.PDFImageView;
import com.callmangement.support.pdfcreator.views.basic.PDFLineSeparatorView;
import com.callmangement.support.pdfcreator.views.basic.PDFTextView;

import java.io.File;
import java.util.Locale;

public class FPSRepeatOnServiceCenterPDFActivity extends PDFCreatorActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        createPDF(""+System.currentTimeMillis(), new PDFUtil.PDFUtilListener() {
            @Override
            public void pdfGenerationSuccess(File savedPDFFile) {
                //Toast.makeText(ReportPdfActivity.this, "PDF Created", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void pdfGenerationFailure(Exception exception) {
                Toast.makeText(FPSRepeatOnServiceCenterPDFActivity.this, "PDF NOT Created", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected PDFHeaderView getHeaderView(int pageIndex) {
        PDFHeaderView headerView = new PDFHeaderView(getApplicationContext());

        PDFHorizontalView horizontalView = new PDFHorizontalView(getApplicationContext());

        PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.HEADER);
        SpannableString word = new SpannableString("FPS Repeat on Service Center Report");
        word.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        pdfTextView.setText(word);
        pdfTextView.setLayout(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        pdfTextView.getView().setGravity(Gravity.CENTER_HORIZONTAL);
        pdfTextView.getView().setTypeface(pdfTextView.getView().getTypeface(), Typeface.BOLD);

        horizontalView.addView(pdfTextView);

        headerView.addView(horizontalView);

        PDFLineSeparatorView lineSeparatorView1 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.WHITE);
        headerView.addView(lineSeparatorView1);

        return headerView;
    }

    @Override
    protected PDFBody getBodyViews() {
        PDFBody pdfBody = new PDFBody();

        PDFLineSeparatorView lineSeparatorView1 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.WHITE);
        lineSeparatorView1.setLayout(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                8, 0));
        pdfBody.addView(lineSeparatorView1);


        PDFLineSeparatorView lineSeparatorView2 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.WHITE);
        lineSeparatorView2.setLayout(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                8, 0));
        pdfBody.addView(lineSeparatorView2);

        PDFLineSeparatorView lineSeparatorView3 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.WHITE);
        lineSeparatorView3.setLayout(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                8, 0));
        pdfBody.addView(lineSeparatorView3);

        PDFLineSeparatorView lineSeparatorView4 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.BLACK);
        pdfBody.addView(lineSeparatorView4);

        int[] widthPercent = {15, 20, 15, 25, 20}; // Sum should be equal to 100%
        String[] textInTable = {"SrNo", "FPS Code", "District", "Fps Repeat Count", "Date"};

        PDFTableView.PDFTableRowView tableHeader = new PDFTableView.PDFTableRowView(getApplicationContext());
        for (String s : textInTable) {
            PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.H3);
            pdfTextView.setText(s);
            pdfTextView.getView().setTypeface(pdfTextView.getView().getTypeface(), Typeface.BOLD);
            pdfTextView.getView().setPaddingRelative(0,5,0,5);
            tableHeader.addToRow(pdfTextView);
        }

        if(Constants.modelRepeatFpsComplaintsList != null && Constants.modelRepeatFpsComplaintsList.size() > 0) {

            PDFTableView.PDFTableRowView tableRowView1 = new PDFTableView.PDFTableRowView(getApplicationContext());
            tableRowView1.getView().setPaddingRelative(0,5,0,0);

            ModelRepeatFpsComplaintsList modelRepeatFpsComplaintsList = Constants.modelRepeatFpsComplaintsList.get(0);

            PDFTextView pdfTextViewSrNo = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            pdfTextViewSrNo.setText("1");
            tableRowView1.addToRow(pdfTextViewSrNo);

            PDFTextView pdfTextViewFPSCode = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            pdfTextViewFPSCode.setText(""+modelRepeatFpsComplaintsList.getFpscode());
            tableRowView1.addToRow(pdfTextViewFPSCode);

            PDFTextView pdfTextViewDistrict = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            pdfTextViewDistrict.setText(""+modelRepeatFpsComplaintsList.getDistrict());
            tableRowView1.addToRow(pdfTextViewDistrict);

            PDFTextView pdfTextViewFPSRepeat = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            pdfTextViewFPSRepeat.setText(""+modelRepeatFpsComplaintsList.getCntReptOnSerCenter());
            tableRowView1.addToRow(pdfTextViewFPSRepeat);

            PDFTextView pdfTextViewDate = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            pdfTextViewDate.setText(""+modelRepeatFpsComplaintsList.getIsSentToServiceCentreOnStr());
            tableRowView1.addToRow(pdfTextViewDate);

            PDFTableView tableView = new PDFTableView(getApplicationContext(), tableHeader, tableRowView1);

            for (int i = 1; i < Constants.modelRepeatFpsComplaintsList.size(); i++) {

                modelRepeatFpsComplaintsList = Constants.modelRepeatFpsComplaintsList.get(i);

                int srNo = i + 1;

                tableRowView1 = new PDFTableView.PDFTableRowView(getApplicationContext());
                pdfTextViewSrNo = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
                pdfTextViewSrNo.setText(""+srNo);
                tableRowView1.addToRow(pdfTextViewSrNo);

                pdfTextViewFPSCode = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
                pdfTextViewFPSCode.setText(""+modelRepeatFpsComplaintsList.getFpscode());
                tableRowView1.addToRow(pdfTextViewFPSCode);

                pdfTextViewDistrict = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
                pdfTextViewDistrict.setText(""+modelRepeatFpsComplaintsList.getDistrict());
                tableRowView1.addToRow(pdfTextViewDistrict);

                pdfTextViewFPSRepeat = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
                pdfTextViewFPSRepeat.setText(""+modelRepeatFpsComplaintsList.getCntReptOnSerCenter());
                tableRowView1.addToRow(pdfTextViewFPSRepeat);

                pdfTextViewDate = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
                pdfTextViewDate.setText(""+modelRepeatFpsComplaintsList.getIsSentToServiceCentreOnStr());
                tableRowView1.addToRow(pdfTextViewDate);


                tableView.addRow(tableRowView1);
            }

            tableView.setColumnWidth(widthPercent);
            pdfBody.addView(tableView);

        }

        PDFLineSeparatorView lineSeparatorView5 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.WHITE);
        lineSeparatorView5.setLayout(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                8, 0));
        pdfBody.addView(lineSeparatorView5);

        PDFLineSeparatorView lineSeparatorView6 = new PDFLineSeparatorView(getApplicationContext()).setBackgroundColor(Color.BLACK);
        pdfBody.addView(lineSeparatorView6);

        return pdfBody;
    }

    @SuppressLint("RtlHardcoded")
    @Override
    protected PDFFooterView getFooterView(int pageIndex) {
        PDFFooterView footerView = new PDFFooterView(getApplicationContext());
        PDFTextView pdfTextViewPage = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
        PDFTextView pdfTextViewPage1 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);

        pdfTextViewPage.setText(String.format(Locale.getDefault(), "Page: %d", pageIndex + 1));
        pdfTextViewPage.setLayout(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 0));
        pdfTextViewPage.getView().setGravity(Gravity.CENTER_HORIZONTAL);

        pdfTextViewPage1.setText(""+ DateTimeUtils.getCurrentTime());
        pdfTextViewPage1.setLayout(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 0));
        pdfTextViewPage1.getView().setGravity(Gravity.LEFT);

        footerView.addView(pdfTextViewPage);
        footerView.addView(pdfTextViewPage1);

        return footerView;
    }

    @Nullable
    @Override
    protected PDFImageView getWatermarkView(int forPage) {
        PDFImageView pdfImageView = new PDFImageView(getApplicationContext());
        FrameLayout.LayoutParams childLayoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                200, Gravity.CENTER);
        pdfImageView.setLayout(childLayoutParams);

        pdfImageView.setImageResource(R.drawable.app_logo);
        pdfImageView.setImageScale(ImageView.ScaleType.FIT_CENTER);
        pdfImageView.getView().setAlpha(0.3F);

        return pdfImageView;
    }

    @Override
    protected void onNextClicked(final File savedPDFFile) {
//        Uri pdfUri = Uri.fromFile(savedPDFFile);
//        Intent intentPdfViewer = new Intent(ReportPdfActivity.this, PdfViewerActivity.class);
//        intentPdfViewer.putExtra(PdfViewerActivity.PDF_FILE_URI, pdfUri);
//        startActivity(intentPdfViewer);

        if (savedPDFFile == null || !savedPDFFile.exists()) {
            Toast.makeText(this, R.string.text_generated_file_error, Toast.LENGTH_SHORT).show();
            return;
        }
        PrintAttributes.Builder printAttributeBuilder = new PrintAttributes.Builder();
        printAttributeBuilder.setMediaSize(PrintAttributes.MediaSize.ISO_A4);
        printAttributeBuilder.setMinMargins(PrintAttributes.Margins.NO_MARGINS);
        PDFUtil.printPdf(FPSRepeatOnServiceCenterPDFActivity.this, savedPDFFile, printAttributeBuilder.build());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}