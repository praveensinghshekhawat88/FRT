package com.callmangement.EHR.ehrActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.core.content.FileProvider;

import com.callmangement.R;
import com.callmangement.databinding.ActivityCampDailyWorkReportPdfBinding;
import com.callmangement.EHR.models.DealersInfo;
import com.callmangement.EHR.support.OnSingleClickListener;
import com.callmangement.EHR.support.Preference;
import com.callmangement.utils.PrefManager;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class CampDailyWorkReportPdfActivity extends BaseActivity {

    Activity mActivity;
    private ActivityCampDailyWorkReportPdfBinding binding;

    PrefManager preference;
    ArrayList<DealersInfo> dealersInfoArrayList = null;
    String TrainingNo = "";

    String targetPdf = "Camp Daily Work Report.pdf";
    Context context;
    private ImageView imageView;
    private PdfRendererHelper pdfRendererHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityCampDailyWorkReportPdfBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        mActivity = this;
        context=this;
        preference =  new PrefManager(context);

        Bundle bundle = getIntent().getExtras();
        dealersInfoArrayList = (ArrayList<DealersInfo>) bundle.getSerializable("mylist");
        TrainingNo = bundle.getString("TrainingNo", "");
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.buttonPDF.setVisibility(View.VISIBLE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.camp_daily_work_report));
        setUpData();
        setClickListener();
        createPdf(targetPdf);
    }

    private void setUpData() {
    }

    private void setClickListener() {
        binding.actionBar.ivBack.setOnClickListener(view -> onBackPressed());
        binding.actionBar.buttonPDF.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                File file = new File(getExternalFilesDir(null), targetPdf);
                Uri pdfURi = FileProvider.getUriForFile(mActivity, mActivity.getPackageName() + ".provider", file , targetPdf );
                Intent share = new Intent();
                share.setAction(Intent.ACTION_SEND);
                share.setType("application/pdf");
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                share.putExtra(Intent.EXTRA_STREAM, pdfURi);
                startActivity(share);
            }
        });
    }

    private void createPdf(final String pdfFileName) {
        File filePath;
        filePath = new File(getExternalFilesDir(null), pdfFileName);
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            Font boldHeading = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
            Paragraph pHeading = new Paragraph("Camp Daily Report", boldHeading);
            pHeading.setSpacingAfter(5);
            pHeading.setAlignment(Element.ALIGN_CENTER);
            document.add(pHeading);
            Font boldTableCell = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
            PdfPTable tableTop1 = new PdfPTable(3);
            tableTop1.setWidthPercentage(100);
            tableTop1.setSpacingBefore(20);
            PdfPCell cellDateStaticTop = new PdfPCell(new Phrase("Date __________________", boldTableCell));
            cellDateStaticTop.setBorder(Rectangle.NO_BORDER);
            tableTop1.addCell(cellDateStaticTop);
            PdfPCell cellBlockStatic = new PdfPCell(new Phrase("Block __________________", boldTableCell));
            cellBlockStatic.setBorder(Rectangle.NO_BORDER);
            tableTop1.addCell(cellBlockStatic);
            PdfPCell cellDistrictStatic = new PdfPCell(new Phrase("District __________________", boldTableCell));
            cellDistrictStatic.setBorder(Rectangle.NO_BORDER);
            tableTop1.addCell(cellDistrictStatic);
            document.add(tableTop1);
            PdfPTable tableTop2 = new PdfPTable(3);
            tableTop2.setWidthPercentage(100);
            tableTop2.setSpacingBefore(20);
            PdfPCell cellCampNoStatic = new PdfPCell(new Phrase("Camp No " + TrainingNo, boldTableCell));
            cellCampNoStatic.setColspan(3);
            cellCampNoStatic.setBorder(Rectangle.NO_BORDER);
            tableTop2.addCell(cellCampNoStatic);
            document.add(tableTop2);
           PdfPTable tableTop3 = new PdfPTable(3);
            tableTop3.setWidthPercentage(100);
            tableTop3.setSpacingBefore(20);
            PdfPCell cellDistrictCoordinatorStatic = new PdfPCell(new Phrase("District Coordinator __________________", boldTableCell));
            cellDistrictCoordinatorStatic.setColspan(3);
            cellDistrictCoordinatorStatic.setBorder(Rectangle.NO_BORDER);
            tableTop3.addCell(cellDistrictCoordinatorStatic);
            document.add(tableTop3);
            Font boldMidTableCell = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
            PdfPTable tableMid = new PdfPTable(6);
            tableMid.setWidthPercentage(100);
            tableMid.setSpacingBefore(40);
            tableMid.setWidths(new int[]{1, 1, 1, 2, 2, 2});
            PdfPCell cellSrNoStatic = new PdfPCell(new Phrase("Sr No", boldTableCell));
            tableMid.addCell(cellSrNoStatic);
            PdfPCell cellDateStatic = new PdfPCell(new Phrase("Date", boldTableCell));
            tableMid.addCell(cellDateStatic);
            PdfPCell cellFPSStatic = new PdfPCell(new Phrase("FPS", boldTableCell));
            tableMid.addCell(cellFPSStatic);
            PdfPCell cellDealerNameStatic = new PdfPCell(new Phrase("Dealer Name", boldTableCell));
            tableMid.addCell(cellDealerNameStatic);
            PdfPCell cellPhoneNumberStatic = new PdfPCell(new Phrase("Phone Number", boldTableCell));
            tableMid.addCell(cellPhoneNumberStatic);
            PdfPCell cellSignatureStatic = new PdfPCell(new Phrase("Signature", boldTableCell));
            tableMid.addCell(cellSignatureStatic);
            if(dealersInfoArrayList != null && dealersInfoArrayList.size() > 0) {
                for (int i = 0; i < dealersInfoArrayList.size(); i++) {
                    PdfPCell cellEmpty = new PdfPCell(new Phrase("\n", boldMidTableCell));
                    tableMid.addCell(cellEmpty);
                    tableMid.addCell(cellEmpty);
                    PdfPCell cellFPS = new PdfPCell(new Phrase(dealersInfoArrayList.get(i).getFpscode(), boldMidTableCell));
                    tableMid.addCell(cellFPS);
                    PdfPCell cellDealerName = new PdfPCell(new Phrase(dealersInfoArrayList.get(i).getCustomerNameEng(), boldMidTableCell));
                    tableMid.addCell(cellDealerName);
                    PdfPCell cellPhoneNumber = new PdfPCell(new Phrase(dealersInfoArrayList.get(i).getMobileNo(), boldMidTableCell));
                    tableMid.addCell(cellPhoneNumber);
                    tableMid.addCell(cellEmpty);
                }
            }
            else {
                for (int i = 0; i < 20; i++) {
                    PdfPCell cellEmpty = new PdfPCell(new Phrase("\n", boldMidTableCell));
                    tableMid.addCell(cellEmpty);
                    tableMid.addCell(cellEmpty);
                    tableMid.addCell(cellEmpty);
                    tableMid.addCell(cellEmpty);
                    tableMid.addCell(cellEmpty);
                    tableMid.addCell(cellEmpty);
                }
            }

            document.add(tableMid);


            PdfPTable tableSignatures = new PdfPTable(2);
            tableSignatures.setWidthPercentage(100);
            tableSignatures.setSpacingBefore(60);
            PdfPCell cellCustomerSignatureStatic = new PdfPCell(new Phrase("District Coordinator Sign", boldTableCell));
            cellCustomerSignatureStatic.setBorder(Rectangle.NO_BORDER);
            cellCustomerSignatureStatic.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableSignatures.addCell(cellCustomerSignatureStatic);
            PdfPCell cellEngineerSignatureStatic = new PdfPCell(new Phrase("Area Inspector Sign", boldTableCell));
            cellEngineerSignatureStatic.setBorder(Rectangle.NO_BORDER);
            cellEngineerSignatureStatic.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableSignatures.addCell(cellEngineerSignatureStatic);

            document.add(tableSignatures);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

  viewPDF();
    }

 private void viewPDF() {
       /* File file = new File(getExternalFilesDir(null), targetPdf);
        binding.pdfView.recycle();
        binding.pdfView.fromFile(file)
                .enableSwipe(true) // allows to block changing pages using swipe
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .defaultPage(0)
                .onRender((nbPages, pageWidth, pageHeight) -> {
                    binding.pdfView.fitToWidth(); // optionally pass page number
                }) // called after document is rendered for the first time
                .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
                .scrollHandle(null)
                .enableAntialiasing(true) // improve rendering a little bit on low-res screens
                // spacing between pages in dp. To define spacing color, set view background
                .spacing(0)
                .invalidPageColor(Color.WHITE) // color of page that is invalid and cannot be loaded
                .load();*/




     imageView = findViewById(R.id.imagepdfView);

     // Path to your PDF file
     File pdfFile = new File(getExternalFilesDir(null), targetPdf);

     try {
         pdfRendererHelper = new PdfRendererHelper(pdfFile);
         Bitmap firstPage = pdfRendererHelper.renderPage(0);
         imageView.setImageBitmap(firstPage);
     } catch (IOException e) {
         e.printStackTrace();
     }
 }





}
