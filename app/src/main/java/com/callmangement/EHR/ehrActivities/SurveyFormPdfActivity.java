package com.callmangement.EHR.ehrActivities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.callmangement.R;
import androidx.core.content.FileProvider;

import com.callmangement.databinding.ActivitySurveyFormBinding;
import com.callmangement.EHR.models.SurveyFormDetailsInfo;
import com.callmangement.EHR.support.CustomBorder;
import com.callmangement.EHR.support.LineDash;
import com.callmangement.EHR.support.OnSingleClickListener;
import com.callmangement.EHR.support.Preference;
import com.callmangement.EHR.support.SolidLine;
import com.callmangement.utils.PrefManager;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class SurveyFormPdfActivity extends BaseActivity {

    Activity mActivity;
    private ActivitySurveyFormBinding binding;

    PrefManager preference;

    String targetPdf = "survey form.pdf";
    SurveyFormDetailsInfo surveyFormDetailsInfo = null;
    private ImageView imageView;
    private PdfRendererHelper pdfRendererHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivitySurveyFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        mActivity = this;
        preference =  new PrefManager(mActivity);

        Bundle bundle = getIntent().getExtras();
        surveyFormDetailsInfo = (SurveyFormDetailsInfo) bundle.getSerializable("surveyFormDetailsInfo");

        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.buttonPDF.setVisibility(View.VISIBLE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.survey_form));

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
                Uri pdfURi = FileProvider.getUriForFile(mActivity, mActivity.getPackageName() + ".provider", file, targetPdf);

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

            Font boldInstall = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
            Paragraph pInstall = new Paragraph("Installation Cum Service and Supply Report", boldInstall);
            pInstall.setAlignment(Element.ALIGN_CENTER);
            document.add(pInstall);

            Font boldBalaji = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
            Paragraph pBalaji = new Paragraph("Balaji Info Lube", boldBalaji);
            pBalaji.setAlignment(Element.ALIGN_CENTER);
            document.add(pBalaji);

            Font boldContact = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
            Paragraph pContact = new Paragraph("Contact: (0291) 2640964, 9950387164  e-mail: balajilubes@gmail.com", boldContact);
            pContact.setAlignment(Element.ALIGN_CENTER);
            pContact.setSpacingAfter(5);
            document.add(pContact);

            Font boldGSTNumber = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
            Paragraph pGSTNumberStatic = new Paragraph("GSTIN NO - " + ((surveyFormDetailsInfo != null && surveyFormDetailsInfo.getGstin_No() != null) ? surveyFormDetailsInfo.getGstin_No() : ""), boldGSTNumber);
            pGSTNumberStatic.setAlignment(Element.ALIGN_LEFT);
            pGSTNumberStatic.setSpacingBefore(10);
            document.add(pGSTNumberStatic);

            Font boldTableCell = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{1, 1, 1, 1});
            PdfPCell cellCustomerNameStatic = new PdfPCell(new Phrase("CUSTOMER NAME", boldTableCell));
            table.addCell(cellCustomerNameStatic);
            PdfPCell cellCustomerName = new PdfPCell(new Phrase((surveyFormDetailsInfo != null && surveyFormDetailsInfo.getCustomerName() != null) ? surveyFormDetailsInfo.getCustomerName() : ""));
            table.addCell(cellCustomerName);
            PdfPCell cellBillChallanNoStatic = new PdfPCell(new Phrase("BILL/ CHALLAN NO.", boldTableCell));
            table.addCell(cellBillChallanNoStatic);
            PdfPCell cellBillChallanNo = new PdfPCell(new Phrase((surveyFormDetailsInfo != null && surveyFormDetailsInfo.getTicketNo() != null) ? surveyFormDetailsInfo.getTicketNo() : ""));
            table.addCell(cellBillChallanNo);

            PdfPCell cellCustomerAddressStatic = new PdfPCell(new Phrase("CUSTOMER ADDRESS", boldTableCell));
            table.addCell(cellCustomerAddressStatic);
            PdfPCell cellCustomerAddress = new PdfPCell(new Phrase((surveyFormDetailsInfo != null && surveyFormDetailsInfo.getAddress() != null) ? surveyFormDetailsInfo.getAddress() : ""));
            table.addCell(cellCustomerAddress);
            PdfPCell cellBillRemarksStatic = new PdfPCell(new Phrase("BILL REMARKS IF ANY : " + ((surveyFormDetailsInfo != null && surveyFormDetailsInfo.getBillRemark() != null) ? surveyFormDetailsInfo.getBillRemark() : ""), boldTableCell));
            cellBillRemarksStatic.setColspan(2);
            cellBillRemarksStatic.setRowspan(2);
            table.addCell(cellBillRemarksStatic);

            PdfPCell cellPointOfContactStatic = new PdfPCell(new Phrase("POINT OF CONTACT", boldTableCell));
            table.addCell(cellPointOfContactStatic);
            PdfPCell cellPointOfContact = new PdfPCell(new Phrase((surveyFormDetailsInfo != null && surveyFormDetailsInfo.getMobileNumber() != null) ? "1" + surveyFormDetailsInfo.getMobileNumber() : ""));
            table.addCell(cellPointOfContact);

            PdfPCell cellMobileNoStatic = new PdfPCell(new Phrase("MOBILE NO.", boldTableCell));
            table.addCell(cellMobileNoStatic);
            PdfPCell cellMobileNo = new PdfPCell(new Phrase((surveyFormDetailsInfo != null && surveyFormDetailsInfo.getMobileNumber() != null) ? surveyFormDetailsInfo.getMobileNumber() : ""));
            table.addCell(cellMobileNo);
            PdfPCell cellDateOfInstallationStatic = new PdfPCell(new Phrase("DATE OF INSTALLATION", boldTableCell));
            table.addCell(cellDateOfInstallationStatic);
            PdfPCell cellDateOfInstallation = new PdfPCell(new Phrase((surveyFormDetailsInfo != null && surveyFormDetailsInfo.getInstallationDateStr() != null) ? surveyFormDetailsInfo.getInstallationDateStr() : ""));
            table.addCell(cellDateOfInstallation);

            Paragraph pSpace = new Paragraph("", boldContact);
            pSpace.setAlignment(Element.ALIGN_CENTER);
            pSpace.setSpacingAfter(5);
            document.add(pSpace);

            document.add(table);

            PdfPTable tableTypeOfCall = new PdfPTable(6);
            tableTypeOfCall.setWidthPercentage(100);
            tableTypeOfCall.setSpacingBefore(20);
            tableTypeOfCall.setSpacingAfter(10);
            PdfPCell cellTypeOfCallStatic = new PdfPCell(new Phrase("Type Of Call", boldTableCell));
            cellTypeOfCallStatic.setBorder(Rectangle.NO_BORDER);
            tableTypeOfCall.addCell(cellTypeOfCallStatic);

            PdfPCell cellTypeOfCallSupplyStatic = new PdfPCell();
            PdfPCell cellTypeOfCallWarrantyStatic = new PdfPCell();
            PdfPCell cellTypeOfCallInstallationStatic = new PdfPCell();
            PdfPCell cellTypeOfCallPayableStatic = new PdfPCell();
            PdfPCell cellTypeOfCallCourtesyStatic = new PdfPCell();

            Image imageChkBoxUnchecked;
            Image imageChkBoxChecked;
            try {
                InputStream ims = getAssets().open("check_box_unchecked_in_pdf.png");
                Bitmap bmp = BitmapFactory.decodeStream(ims);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                imageChkBoxUnchecked = Image.getInstance(stream.toByteArray());
                imageChkBoxUnchecked.scaleAbsolute(20, 20);
                imageChkBoxUnchecked.setAlignment(Element.ALIGN_CENTER);
            } catch (Exception ex) {
                return;
            }

            try {
                InputStream ims = getAssets().open("check_box_checked_in_pdf.png");
                Bitmap bmp = BitmapFactory.decodeStream(ims);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                imageChkBoxChecked = Image.getInstance(stream.toByteArray());
                imageChkBoxChecked.scaleAbsolute(20, 20);
                imageChkBoxChecked.setAlignment(Element.ALIGN_CENTER);
            } catch (Exception ex) {
                return;
            }

            Paragraph pTypeOfCallSupplyStatic = new Paragraph("Supply");
            pTypeOfCallSupplyStatic.setAlignment(Element.ALIGN_CENTER);
            cellTypeOfCallSupplyStatic.setBorder(Rectangle.NO_BORDER);


            Paragraph pTypeOfCallWarrantyStatic = new Paragraph("Warranty");
            pTypeOfCallWarrantyStatic.setAlignment(Element.ALIGN_CENTER);
            cellTypeOfCallWarrantyStatic.setBorder(Rectangle.NO_BORDER);
            cellTypeOfCallWarrantyStatic.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellTypeOfCallWarrantyStatic.setVerticalAlignment(Element.ALIGN_MIDDLE);


            Paragraph pTypeOfCallInstallationStatic = new Paragraph("Installation");
            pTypeOfCallInstallationStatic.setAlignment(Element.ALIGN_CENTER);
            cellTypeOfCallInstallationStatic.setBorder(Rectangle.NO_BORDER);
            cellTypeOfCallInstallationStatic.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellTypeOfCallInstallationStatic.setVerticalAlignment(Element.ALIGN_MIDDLE);


            Paragraph pTypeOfCallPayableStatic = new Paragraph("Payable");
            pTypeOfCallPayableStatic.setAlignment(Element.ALIGN_CENTER);
            cellTypeOfCallPayableStatic.setBorder(Rectangle.NO_BORDER);
            cellTypeOfCallPayableStatic.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellTypeOfCallPayableStatic.setVerticalAlignment(Element.ALIGN_MIDDLE);


            Paragraph pTypeOfCallCourtesyStatic = new Paragraph("Courtesy");
            pTypeOfCallCourtesyStatic.setAlignment(Element.ALIGN_CENTER);
            cellTypeOfCallCourtesyStatic.setBorder(Rectangle.NO_BORDER);
            cellTypeOfCallCourtesyStatic.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellTypeOfCallCourtesyStatic.setVerticalAlignment(Element.ALIGN_MIDDLE);


            if (surveyFormDetailsInfo != null && surveyFormDetailsInfo.getTypeOfCallId() != null) {
                if (surveyFormDetailsInfo.getTypeOfCallId().contains(",")) {
                    String[] result = surveyFormDetailsInfo.getTypeOfCallId().split(",");
                    ArrayList<String> typeOfCallIdList = new ArrayList<>(Arrays.asList(result));

                    if(typeOfCallIdList.contains("1")) {
                        if (imageChkBoxChecked != null)
                            cellTypeOfCallSupplyStatic.addElement(imageChkBoxChecked);
                    }
                    else {
                        if (imageChkBoxUnchecked != null)
                            cellTypeOfCallSupplyStatic.addElement(imageChkBoxUnchecked);
                    }

                    if(typeOfCallIdList.contains("2")) {
                        if (imageChkBoxChecked != null)
                            cellTypeOfCallWarrantyStatic.addElement(imageChkBoxChecked);
                    }
                    else {
                        if (imageChkBoxUnchecked != null)
                            cellTypeOfCallWarrantyStatic.addElement(imageChkBoxUnchecked);
                    }

                    if(typeOfCallIdList.contains("3")) {
                        if (imageChkBoxChecked != null)
                            cellTypeOfCallInstallationStatic.addElement(imageChkBoxChecked);
                    }
                    else {
                        if (imageChkBoxUnchecked != null)
                            cellTypeOfCallInstallationStatic.addElement(imageChkBoxUnchecked);
                    }

                    if(typeOfCallIdList.contains("4")) {
                        if (imageChkBoxChecked != null)
                            cellTypeOfCallPayableStatic.addElement(imageChkBoxChecked);
                    }
                    else {
                        if (imageChkBoxUnchecked != null)
                            cellTypeOfCallPayableStatic.addElement(imageChkBoxUnchecked);
                    }

                    if(typeOfCallIdList.contains("5")) {
                        if (imageChkBoxChecked != null)
                            cellTypeOfCallCourtesyStatic.addElement(imageChkBoxChecked);
                    }
                    else {
                        if (imageChkBoxUnchecked != null)
                            cellTypeOfCallCourtesyStatic.addElement(imageChkBoxUnchecked);
                    }


                } else {
                    if (surveyFormDetailsInfo.getTypeOfCallId().length() > 0) {

                        if(surveyFormDetailsInfo.getTypeOfCallId().equalsIgnoreCase("1")) {
                            if (imageChkBoxChecked != null)
                                cellTypeOfCallSupplyStatic.addElement(imageChkBoxChecked);
                        }
                        else {
                            if (imageChkBoxUnchecked != null)
                                cellTypeOfCallSupplyStatic.addElement(imageChkBoxUnchecked);
                        }

                        if(surveyFormDetailsInfo.getTypeOfCallId().equalsIgnoreCase("2")) {
                            if (imageChkBoxChecked != null)
                                cellTypeOfCallWarrantyStatic.addElement(imageChkBoxChecked);
                        }
                        else {
                            if (imageChkBoxUnchecked != null)
                                cellTypeOfCallWarrantyStatic.addElement(imageChkBoxUnchecked);
                        }

                        if(surveyFormDetailsInfo.getTypeOfCallId().equalsIgnoreCase("3")) {
                            if (imageChkBoxChecked != null)
                                cellTypeOfCallInstallationStatic.addElement(imageChkBoxChecked);
                        }
                        else {
                            if (imageChkBoxUnchecked != null)
                                cellTypeOfCallInstallationStatic.addElement(imageChkBoxUnchecked);
                        }

                        if(surveyFormDetailsInfo.getTypeOfCallId().equalsIgnoreCase("4")) {
                            if (imageChkBoxChecked != null)
                                cellTypeOfCallPayableStatic.addElement(imageChkBoxChecked);
                        }
                        else {
                            if (imageChkBoxUnchecked != null)
                                cellTypeOfCallPayableStatic.addElement(imageChkBoxUnchecked);
                        }

                        if(surveyFormDetailsInfo.getTypeOfCallId().equalsIgnoreCase("5")) {
                            if (imageChkBoxChecked != null)
                                cellTypeOfCallCourtesyStatic.addElement(imageChkBoxChecked);
                        }
                        else {
                            if (imageChkBoxUnchecked != null)
                                cellTypeOfCallCourtesyStatic.addElement(imageChkBoxUnchecked);
                        }

                    }
                    else {

                        if (imageChkBoxUnchecked != null)
                            cellTypeOfCallSupplyStatic.addElement(imageChkBoxUnchecked);
                        if (imageChkBoxUnchecked != null)
                            cellTypeOfCallWarrantyStatic.addElement(imageChkBoxUnchecked);
                        if (imageChkBoxUnchecked != null)
                            cellTypeOfCallInstallationStatic.addElement(imageChkBoxUnchecked);
                        if (imageChkBoxUnchecked != null)
                            cellTypeOfCallPayableStatic.addElement(imageChkBoxUnchecked);
                        if (imageChkBoxUnchecked != null)
                            cellTypeOfCallCourtesyStatic.addElement(imageChkBoxUnchecked);
                    }
                }
            }

            cellTypeOfCallSupplyStatic.addElement(pTypeOfCallSupplyStatic);
            tableTypeOfCall.addCell(cellTypeOfCallSupplyStatic);

            cellTypeOfCallWarrantyStatic.addElement(pTypeOfCallWarrantyStatic);
            tableTypeOfCall.addCell(cellTypeOfCallWarrantyStatic);

            cellTypeOfCallInstallationStatic.addElement(pTypeOfCallInstallationStatic);
            tableTypeOfCall.addCell(cellTypeOfCallInstallationStatic);

            cellTypeOfCallPayableStatic.addElement(pTypeOfCallPayableStatic);
            tableTypeOfCall.addCell(cellTypeOfCallPayableStatic);


            cellTypeOfCallCourtesyStatic.addElement(pTypeOfCallCourtesyStatic);
            tableTypeOfCall.addCell(cellTypeOfCallCourtesyStatic);

            document.add(tableTypeOfCall);


            PdfPTable tableItemsDetails = new PdfPTable(1);
            tableItemsDetails.setWidthPercentage(80);
            tableItemsDetails.setSpacingBefore(20);
            PdfPCell cellItemsDetailsStatic = new PdfPCell(new Phrase("Items Detail :-" + ((surveyFormDetailsInfo != null && surveyFormDetailsInfo.getItemDetail() != null) ? surveyFormDetailsInfo.getItemDetail() : "")));
            tableItemsDetails.addCell(cellItemsDetailsStatic);

            LineDash solid = new SolidLine();

            PdfPCell cellPurchaseOrderDetails = new PdfPCell(new Phrase("PURCHASE ORDER DETAILS :-\n" + ((surveyFormDetailsInfo != null && surveyFormDetailsInfo.getPurchaseOrderDtl() != null) ? surveyFormDetailsInfo.getPurchaseOrderDtl() : "")));
            cellPurchaseOrderDetails.setBorder(Rectangle.ALIGN_BOTTOM);
            cellPurchaseOrderDetails.setCellEvent(new CustomBorder(solid, solid, null, null));
            tableItemsDetails.addCell(cellPurchaseOrderDetails);

            PdfPCell cellSpecificationOfMachineInstalled = new PdfPCell(new Phrase("SPECIFICATION OF MACHINE INSTALLED :-\n" + ((surveyFormDetailsInfo != null && surveyFormDetailsInfo.getInstalledMachineSpecification() != null) ? surveyFormDetailsInfo.getInstalledMachineSpecification() : "")));
            cellSpecificationOfMachineInstalled.setBorder(Rectangle.ALIGN_BOTTOM);
            cellSpecificationOfMachineInstalled.setCellEvent(new CustomBorder(solid, solid, null, null));
            tableItemsDetails.addCell(cellSpecificationOfMachineInstalled);

            PdfPCell cellAnyAccessory = new PdfPCell(new Phrase("ANY ACCESSORY :-\n" + ((surveyFormDetailsInfo != null && surveyFormDetailsInfo.getAccessesory() != null) ? surveyFormDetailsInfo.getAccessesory() : "")));
            cellAnyAccessory.setBorder(Rectangle.ALIGN_BOTTOM);
            cellAnyAccessory.setCellEvent(new CustomBorder(solid, solid, null, null));
            tableItemsDetails.addCell(cellAnyAccessory);

            document.add(tableItemsDetails);

            PdfPTable tableInstallationDone = new PdfPTable(1);
            tableInstallationDone.setWidthPercentage(80);
            PdfPCell cellInstallationDoneStatic = new PdfPCell(new Phrase("INSTALLATION DONE :-" + ((surveyFormDetailsInfo != null && surveyFormDetailsInfo.getInstallationDone() != null) ? surveyFormDetailsInfo.getInstallationDone() : "")));
            tableInstallationDone.addCell(cellInstallationDoneStatic);

            document.add(tableInstallationDone);


            Font boldCustomerRemarks = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
            Paragraph pCustomerRemarksStatic = new Paragraph("Customer Remark : ", boldCustomerRemarks);
            pCustomerRemarksStatic.setAlignment(Element.ALIGN_LEFT);
            pCustomerRemarksStatic.setSpacingBefore(10);
            document.add(pCustomerRemarksStatic);

            Paragraph pCustomerRemarks = new Paragraph((surveyFormDetailsInfo != null && surveyFormDetailsInfo.getCustomer_Remark() != null) ? surveyFormDetailsInfo.getCustomer_Remark() : "");
            pCustomerRemarks.setAlignment(Element.ALIGN_LEFT);
            document.add(pCustomerRemarks);

            PdfPTable tableNames = new PdfPTable(2);
            tableNames.setWidthPercentage(100);
            tableNames.setSpacingBefore(40);
            PdfPCell cellCustomerNameAtBottomStatic = new PdfPCell(new Phrase((surveyFormDetailsInfo != null && surveyFormDetailsInfo.getCustomerName() != null) ? surveyFormDetailsInfo.getCustomerName() : ""));
            cellCustomerNameAtBottomStatic.setBorder(Rectangle.NO_BORDER);
            cellCustomerNameAtBottomStatic.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableNames.addCell(cellCustomerNameAtBottomStatic);
            PdfPCell cellEngineerNameStatic = new PdfPCell(new Phrase((surveyFormDetailsInfo != null && surveyFormDetailsInfo.getEngineerName() != null) ? surveyFormDetailsInfo.getEngineerName() : ""));
            cellEngineerNameStatic.setBorder(Rectangle.NO_BORDER);
            cellEngineerNameStatic.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableNames.addCell(cellEngineerNameStatic);

            document.add(tableNames);


            PdfPTable tableSignaturesLines = new PdfPTable(2);
            tableSignaturesLines.setWidthPercentage(100);
            PdfPCell cellCustomerSignatureLinesStatic = new PdfPCell(new Phrase("___________________________"));
            cellCustomerSignatureLinesStatic.setBorder(Rectangle.NO_BORDER);
            cellCustomerSignatureLinesStatic.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableSignaturesLines.addCell(cellCustomerSignatureLinesStatic);
            PdfPCell cellEngineerSignatureLinesStatic = new PdfPCell(new Phrase("___________________________"));
            cellEngineerSignatureLinesStatic.setBorder(Rectangle.NO_BORDER);
            cellEngineerSignatureLinesStatic.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableSignaturesLines.addCell(cellEngineerSignatureLinesStatic);

            document.add(tableSignaturesLines);


            PdfPTable tableSignatures = new PdfPTable(2);
            tableSignatures.setWidthPercentage(100);
            PdfPCell cellCustomerSignatureStatic = new PdfPCell(new Phrase("Customer Name, Sign & Stamp", boldTableCell));
            cellCustomerSignatureStatic.setBorder(Rectangle.NO_BORDER);
            cellCustomerSignatureStatic.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableSignatures.addCell(cellCustomerSignatureStatic);
            PdfPCell cellEngineerSignatureStatic = new PdfPCell(new Phrase("Engineer Name & Sign", boldTableCell));
            cellEngineerSignatureStatic.setBorder(Rectangle.NO_BORDER);
            cellEngineerSignatureStatic.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tableSignatures.addCell(cellEngineerSignatureStatic);
            document.add(tableSignatures);
            Font boldHeadOffice = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
            Paragraph pHeadOffice = new Paragraph("Head Office: - 21, Residency Road, Opp. Manidhari Brain Hospital, Jodhpur", boldHeadOffice);
            pHeadOffice.setSpacingBefore(20);
            pHeadOffice.setAlignment(Element.ALIGN_CENTER);
            document.add(pHeadOffice);
            Font boldTel = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
            Paragraph pTel = new Paragraph("Tel: (0291) - 2640964, Fax +91-291-2654117", boldTel);
            pTel.setAlignment(Element.ALIGN_CENTER);
            document.add(pTel);
            Font boldStatement = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
            Paragraph pStatement = new Paragraph("\"We'll bend over backward to meet your needs\"", boldStatement);
            pStatement.setAlignment(Element.ALIGN_CENTER);
            pStatement.setSpacingAfter(5);
            document.add(pStatement);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

 viewPDF();
    }

 private void viewPDF() {

     /*   File file = new File(getExternalFilesDir(null), targetPdf);

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
                .load();
    }*/



    imageView = findViewById(R.id.imagepdfView);

    // Path to your PDF file
    File pdfFile = new File(getExternalFilesDir(null), targetPdf);

     try {
        pdfRendererHelper = new PdfRendererHelper(pdfFile);
        Bitmap firstPage = pdfRendererHelper.renderPage(0);
        imageView.setImageBitmap(firstPage);
    } catch (
    IOException e) {
        e.printStackTrace();
    }
}
}
