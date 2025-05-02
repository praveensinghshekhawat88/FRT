package com.callmangement.report_pdf;

import androidx.annotation.Nullable;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
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
import com.callmangement.model.complaints.ModelComplaintList;
import com.callmangement.model.reports.Monthly_Reports_Info;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReportPdfActivity extends PDFCreatorActivity {
    private String district;
    private String title;
    private String name;
    private String email;
    String fromWhereStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        fromWhereStr = getIntent().getStringExtra("from_where");
        title = getIntent().getStringExtra("title");
        district = getIntent().getStringExtra("district");
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");

        createPDF(""+System.currentTimeMillis(), new PDFUtil.PDFUtilListener() {
            @Override
            public void pdfGenerationSuccess(File savedPDFFile) {
                //Toast.makeText(ReportPdfActivity.this, "PDF Created", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void pdfGenerationFailure(Exception exception) {
                Toast.makeText(ReportPdfActivity.this, "PDF NOT Created", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected PDFHeaderView getHeaderView(int pageIndex) {
        PDFHeaderView headerView = new PDFHeaderView(getApplicationContext());

        PDFHorizontalView horizontalView = new PDFHorizontalView(getApplicationContext());

        PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.HEADER);
        SpannableString word = new SpannableString(""+title);
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

        PDFTextView pdfCompanyNameView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.H3);
        pdfCompanyNameView.setText("Name       :     "+name+"\n"+"Email       :     "+email+"\n"+"District     :    "+district);
        pdfBody.addView(pdfCompanyNameView);

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

        int[] widthPercent = {20, 20, 20, 20}; // Sum should be equal to 100%
        String[] textInTable = {"Date", "Register", "Resolved", "Not Resolve"};

        PDFTableView.PDFTableRowView tableHeader = new PDFTableView.PDFTableRowView(getApplicationContext());
        for (String s : textInTable) {
            PDFTextView pdfTextView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.H3);
            pdfTextView.setText(s);
            pdfTextView.getView().setTypeface(pdfTextView.getView().getTypeface(), Typeface.BOLD);
            pdfTextView.getView().setPaddingRelative(0,5,0,5);
            tableHeader.addToRow(pdfTextView);
        }

        if (fromWhereStr.equals("monthly")){
            if(Constants.listMonthReport != null && Constants.listMonthReport.size() > 0) {
                PDFTableView.PDFTableRowView tableRowView1 = new PDFTableView.PDFTableRowView(getApplicationContext());
                tableRowView1.getView().setPaddingRelative(0,5,0,0);

                Monthly_Reports_Info monthly_reports_info = new Monthly_Reports_Info();
                List<List<ModelComplaintList>> resolvedNotResolvedList = getResolvedNotResolvedList(Constants.listMonthReport.get(0).date, Constants.listMonthReport.get(0).list);
                monthly_reports_info.date = Constants.listMonthReport.get(0).date;
                monthly_reports_info.resolved = resolvedNotResolvedList.get(0).size();
                monthly_reports_info.not_resolved = resolvedNotResolvedList.get(1).size();

                int total = resolvedNotResolvedList.get(0).size() + resolvedNotResolvedList.get(1).size();
                monthly_reports_info.total = total;

                PDFTextView pdfTextViewDate = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
                pdfTextViewDate.setText(monthly_reports_info.date);
                tableRowView1.addToRow(pdfTextViewDate);

                PDFTextView pdfTextViewTotal = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
                pdfTextViewTotal.setText("" + (monthly_reports_info.not_resolved + monthly_reports_info.resolved));
                tableRowView1.addToRow(pdfTextViewTotal);

                PDFTextView pdfTextViewResolved = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
                pdfTextViewResolved.setText(""+ monthly_reports_info.resolved);
                tableRowView1.addToRow(pdfTextViewResolved);

                PDFTextView pdfTextViewNotResolve = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
                pdfTextViewNotResolve.setText(""+ monthly_reports_info.not_resolved);
                tableRowView1.addToRow(pdfTextViewNotResolve);

                PDFTableView tableView = new PDFTableView(getApplicationContext(), tableHeader, tableRowView1);

                for (int i = 1; i < Constants.listMonthReport.size(); i++) {

                    monthly_reports_info = new Monthly_Reports_Info();
                    resolvedNotResolvedList = getResolvedNotResolvedList(Constants.listMonthReport.get(i).date, Constants.listMonthReport.get(i).list);
                    monthly_reports_info.date = Constants.listMonthReport.get(i).date;
                    monthly_reports_info.resolved = resolvedNotResolvedList.get(0).size();
                    monthly_reports_info.not_resolved = resolvedNotResolvedList.get(1).size();
                    total = resolvedNotResolvedList.get(0).size() + resolvedNotResolvedList.get(1).size();
                    monthly_reports_info.total = total;

                    tableRowView1 = new PDFTableView.PDFTableRowView(getApplicationContext());
                    pdfTextViewDate = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
                    pdfTextViewDate.setText(monthly_reports_info.date);
                    tableRowView1.addToRow(pdfTextViewDate);

                    pdfTextViewTotal = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
                    pdfTextViewTotal.setText("" + (monthly_reports_info.not_resolved + monthly_reports_info.resolved));
                    tableRowView1.addToRow(pdfTextViewTotal);

                    pdfTextViewResolved = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
                    pdfTextViewResolved.setText(""+ monthly_reports_info.resolved);
                    tableRowView1.addToRow(pdfTextViewResolved);

                    pdfTextViewNotResolve = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
                    pdfTextViewNotResolve.setText(""+ monthly_reports_info.not_resolved);
                    tableRowView1.addToRow(pdfTextViewNotResolve);

                    tableView.addRow(tableRowView1);
                }

                tableView.setColumnWidth(widthPercent);
                pdfBody.addView(tableView);
            }

        }else if (fromWhereStr.equals("daily")){
            PDFTableView.PDFTableRowView tableRowView1 = new PDFTableView.PDFTableRowView(getApplicationContext());
            tableRowView1.getView().setPaddingRelative(0,5,0,0);

            String resolved = getIntent().getStringExtra("resolved");
            String notResolved = getIntent().getStringExtra("not_resolved");
            String date = getIntent().getStringExtra("date");
            String total = getIntent().getStringExtra("total");

            Monthly_Reports_Info monthly_reports_info = new Monthly_Reports_Info();
            monthly_reports_info.date = date;
            monthly_reports_info.resolved = Integer.parseInt(resolved);
            monthly_reports_info.not_resolved = Integer.parseInt(notResolved);
            monthly_reports_info.total = Integer.parseInt(total);

            PDFTextView pdfTextViewDate = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            pdfTextViewDate.setText(monthly_reports_info.date);
            tableRowView1.addToRow(pdfTextViewDate);

            PDFTextView pdfTextViewTotal = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            pdfTextViewTotal.setText("" + (monthly_reports_info.not_resolved + monthly_reports_info.resolved));
            tableRowView1.addToRow(pdfTextViewTotal);

            PDFTextView pdfTextViewResolved = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            pdfTextViewResolved.setText(""+ monthly_reports_info.resolved);
            tableRowView1.addToRow(pdfTextViewResolved);

            PDFTextView pdfTextViewNotResolve = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
            pdfTextViewNotResolve.setText(""+ monthly_reports_info.not_resolved);
            tableRowView1.addToRow(pdfTextViewNotResolve);

            PDFTableView tableView = new PDFTableView(getApplicationContext(), tableHeader, tableRowView1);

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

    private List<List<ModelComplaintList>> getResolvedNotResolvedList(String date,List<ModelComplaintList> totalList) {
        List<ModelComplaintList> resolvedList = new ArrayList<>();
        List<ModelComplaintList> notResolvedList = new ArrayList<>();
        List<List<ModelComplaintList>> resolveAndNotResolvedList = new ArrayList<>();
        try {
            if (totalList != null) {
                if (totalList.size() > 0) {
                    for (ModelComplaintList model : totalList) {
                        if (model.getComplainStatusId().equals("3") && DateTimeUtils.getTimeStamp(date) == DateTimeUtils.getTimeStamp(model.sermarkDateStr))
                            resolvedList.add(model);
                        else notResolvedList.add(model);
                    }
                }
            }
            resolveAndNotResolvedList.add(resolvedList);
            resolveAndNotResolvedList.add(notResolvedList);
            return resolveAndNotResolvedList;
        } catch (Exception e) {
            e.printStackTrace();
            return resolveAndNotResolvedList;
        }
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
        PDFUtil.printPdf(ReportPdfActivity.this, savedPDFFile, printAttributeBuilder.build());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}