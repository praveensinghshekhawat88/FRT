package com.callmangement.EHR.ehrActivities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.callmangement.EHR.models.CampDetailsInfo;
import com.callmangement.R;
import com.callmangement.databinding.ActivityExcelCampBinding;
import com.callmangement.ui.ins_weighing_scale.adapter.ViewImageInstalledAdapterIris;
import com.callmangement.utils.PrefManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExcelforCamp extends BaseActivity {

    Activity mActivity;
    private ActivityExcelCampBinding binding;

    PrefManager preference;
    List<CampDetailsInfo> campDetailsInfos ;
    String TrainingNo = "";
    SharedPreferences sharedPreferences;
    String targetPdf = "Camp Record.pdf";
    private EditText editTextExcel;
    private final File filePath = new File("/storage/emulated/0/Download" + "/Demo.xls");
    String startdateData,enddateData;
    private ImageView imageView;
    private PdfRendererHelper pdfRendererHelper;
    private ViewImageInstalledAdapterIris.OnItemViewClickListener mOnItemViewClickListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityExcelCampBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {

        Intent intent = getIntent();
        startdateData = intent.getExtras().getString("startDateKey");
        enddateData = intent.getExtras().getString("endDateKey");
        mActivity = this;
        preference =  new PrefManager(mActivity);


        sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);

        // creating a variable for gson.
        Gson gson = new Gson();

        // below line is to get to string present from our
        // shared prefs if not present setting it as null.
        String json = sharedPreferences.getString("coursescamp", null);

        // below line is to get the type of our array list.
        Type type = new TypeToken<ArrayList<CampDetailsInfo>>() {}.getType();

        // in below line we are getting data from gson
        // and saving it to our array list
        campDetailsInfos = gson.fromJson(json, type);

        // checking below if the array list is empty or not
        if (campDetailsInfos == null) {
            // if the array list is empty
            // creating a new array list.
            campDetailsInfos = new ArrayList<>();
          //  Log.d("nbb",""+campDetailsInfos);

        }
       // Log.d("gfhvbb",""+campDetailsInfos);



        //  TrainingNo = bundle.getString("TrainingNo", "");
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.buttonPDF.setVisibility(View.VISIBLE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.camp_record_pdf));
        setUpData();
        setClickListener();
        createPdf(targetPdf);
        // createExcelForm();
        //  createAndShowExcelSheet();

    }

    private void setUpData() {
    }

    private void setClickListener() {
        binding.actionBar.ivBack.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                       /*     SharedPreferences.Editor editor = sharedPreferences.edit();
                                                            editor.remove("courses");
                                                            editor.apply();*/
                                                            onBackPressed();
                                                        }
                                                    }




        );
  /*  binding.actionBar.buttonPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
                HSSFSheet hssfSheet = hssfWorkbook.createSheet("Custom Sheet");

             *//*  HSSFRow hssfRow = hssfSheet.createRow(0);
                HSSFCell hssfCell = hssfRow.createCell(0);

                hssfCell.setCellValue("hi am cell");*//*


                // Create rows and cells with sample data


                Row row1 = hssfSheet.createRow(0);
                Cell cellA1 = row1.createCell(0);
                cellA1.setCellValue("District\n name");
                Cell cellA2 = row1.createCell(1);
                cellA2.setCellValue("User\nName");
                Cell cellA3 = row1.createCell(2);
                cellA3.setCellValue("Punch\nDate");
*//*

                Row row3 = hssfSheet.createRow(0);
                Cell cellA3 = row3.createCell(2);
                cellA3.setCellValue("Punch\nDate");

  Row row4 = hssfSheet.createRow(2);
                Cell cellA4 = row4.createCell(0);
                cellA4.setCellValue("Punch\nDay");
  Row row5 = hssfSheet.createRow(5);
                Cell cellA5 = row5.createCell(0);
                cellA5.setCellValue("PunchIn\nTime");
  Row row6 = hssfSheet.createRow(9);
                Cell cellA6 = row6.createCell(0);
                cellA6.setCellValue("Punch\nOut\nTime");

 Row row7 = hssfSheet.createRow(0);
                Cell cellA7 = row7.createCell(6);
                cellA7.setCellValue("AddressIn");

 Row row8 = hssfSheet.createRow(0);
                Cell cellA8 = row8.createCell(7);
                cellA8.setCellValue("AddressOut");


*//*
                HSSFRow hssfRow = hssfSheet.createRow(0);

                for (int i = 0; i<attendanceDetailsInfoList.size(); i++){
                    HSSFCell hssfCell = hssfRow.createCell(i);


                    hssfCell.setCellValue(attendanceDetailsInfoList.get(i).getDistrictName());
                }

                //   saveWorkBook(hssfWorkbook);

                try {
                    if (!filePath.exists()){
                        filePath.createNewFile();
                    }

                    FileOutputStream fileOutputStream= new FileOutputStream(filePath);
                    hssfWorkbook.write(fileOutputStream);
                    Log.d("Excel", "Excel file created successfully.");
                    Toast.makeText(mActivity, "Excel file created successfully", Toast.LENGTH_SHORT).show();


                    if (fileOutputStream!=null){
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    }
                    hssfSheet.getRow(0).setHeightInPoints(30);
                    // Increase the cell width for the first three columns (0, 1, and 2)


                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("Excel", "Excel file created not  successfully.");
                    Toast.makeText(mActivity, "Excel file created not successfully", Toast.LENGTH_SHORT).show();


                }

            }
        });*/


        binding.actionBar.buttonPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File file = new File(getExternalFilesDir(null), targetPdf);
                Uri pdfURi = FileProvider.getUriForFile(mActivity, mActivity.getPackageName() + ".provider", file , targetPdf );
                Intent share = new Intent();
                share.setAction(Intent.ACTION_SEND);
              //  share.setType("application/pdf");
                share.setDataAndType(pdfURi, "application/pdf");
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                share.putExtra(Intent.EXTRA_STREAM, pdfURi);
                startActivity(share);

                Uri contentUri = Uri.parse(String.valueOf(pdfURi));
                downloadFromContentUri(ExcelforCamp.this, contentUri);


            }
        });





    }

    private void downloadFromContentUri(Context context, Uri contentUri) {
        ContentResolver contentResolver = context.getContentResolver();

        // Create a new file in the Downloads directory
        //   File downloadDir = new File(String.valueOf(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)));

        //  File outputFile = new File(downloadDir, "Attendance Record.pdf");
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        File outputFile = new File("/storage/emulated/0/Download/" + timeStamp+".pdf");


        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            // Open an input stream to the content URI
            inputStream = contentResolver.openInputStream(contentUri);

            // Open an output stream to the file
            outputStream = new FileOutputStream(outputFile);

            // Copy the content from the input stream to the output stream
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            //Toast.makeText(this, "File downloaded: " + outputFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "File downloaded", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Download failed", Toast.LENGTH_SHORT).show();
        } finally {
            // Close the streams to release resources
            try {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    public void onBackPressed() {

        super.onBackPressed();

    }
    private void createPdf(final String pdfFileName) {
        File filePath;
        filePath = new File(getExternalFilesDir(null), pdfFileName);
        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            Font boldHeading = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
           // Paragraph pHeading = new Paragraph("Camp Record \n " + startdateData+ "  -  "+enddateData, boldHeading);
            Paragraph pHeading;
            if(startdateData.isEmpty() && enddateData.isEmpty()){
                pHeading = new Paragraph("Camp Record \n"+ "All"+ "-"+"Camp", boldHeading);

            }
            else
            {
                pHeading = new Paragraph("Camp Record \n"+ startdateData+ "  -  "+enddateData, boldHeading);


            }





            pHeading.setSpacingAfter(5);
            pHeading.setAlignment(Element.ALIGN_CENTER);
            document.add(pHeading);
            Font boldTableCell = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);

            Font boldMidTableCell = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
            PdfPTable tableMid = new PdfPTable(6);
            tableMid.setWidthPercentage(100);
            tableMid.setSpacingBefore(40);
            tableMid.setWidths(new int[]{1, 1, 1, 1,1,2});
            PdfPCell cellSrNoStatic = new PdfPCell(new Phrase("District\nName", boldTableCell));
            cellSrNoStatic.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableMid.addCell(cellSrNoStatic);
            PdfPCell cellDateStatic = new PdfPCell(new Phrase("Block\nName", boldTableCell));
            cellDateStatic.setHorizontalAlignment(Element.ALIGN_CENTER);

            tableMid.addCell(cellDateStatic);
            PdfPCell PunchDate = new PdfPCell(new Phrase("StartDate", boldTableCell));
            PunchDate.setHorizontalAlignment(Element.ALIGN_CENTER);

            tableMid.addCell(PunchDate);
            PdfPCell PunchDay = new PdfPCell(new Phrase("EndDay", boldTableCell));
            PunchDay.setHorizontalAlignment(Element.ALIGN_CENTER);

            tableMid.addCell(PunchDay);
            PdfPCell PunchInTime = new PdfPCell(new Phrase("Status", boldTableCell));
            PunchInTime.setHorizontalAlignment(Element.ALIGN_CENTER);

            tableMid.addCell(PunchInTime);
            PdfPCell cellSignatureStatic = new PdfPCell(new Phrase("Address", boldTableCell));
            cellSignatureStatic.setHorizontalAlignment(Element.ALIGN_CENTER);

            tableMid.addCell(cellSignatureStatic);
       //  PdfPCell addressIn = new PdfPCell(new Phrase("AddressIn", boldTableCell));
       //    tableMid.addCell(addressIn);
        //   PdfPCell addressOut = new PdfPCell(new Phrase("AddressOut", boldTableCell));
       //   tableMid.addCell(addressOut);
            if(campDetailsInfos != null && campDetailsInfos.size() > 0) {
                for (int i = 0; i < campDetailsInfos.size(); i++) {
                    PdfPCell cellEmpty = new PdfPCell(new Phrase("\n", boldMidTableCell));
                    //tableMid.addCell(cellEmpty);
                    // tableMid.addCell(cellEmpty);
                    PdfPCell cellFPS = new PdfPCell(new Phrase(campDetailsInfos.get(i).getDistrictNameEng(), boldMidTableCell));
                    cellFPS.setHorizontalAlignment(Element.ALIGN_CENTER);

                    tableMid.addCell(cellFPS);
                    PdfPCell cellDealerName = new PdfPCell(new Phrase(String.valueOf(campDetailsInfos.get(i).getBlockName()), boldMidTableCell));
                    cellDealerName.setHorizontalAlignment(Element.ALIGN_CENTER);

                    tableMid.addCell(cellDealerName);

                    PdfPCell day = new PdfPCell(new Phrase(campDetailsInfos.get(i).getStartDate(), boldMidTableCell));
                    day.setHorizontalAlignment(Element.ALIGN_CENTER);

                    tableMid.addCell(day);
                    PdfPCell intime = new PdfPCell(new Phrase(campDetailsInfos.get(i).getEndDate(), boldMidTableCell));
                    intime.setHorizontalAlignment(Element.ALIGN_CENTER);


                    tableMid.addCell(intime);
                    PdfPCell outtime = new PdfPCell(new Phrase(campDetailsInfos.get(i).getStatus(), boldMidTableCell));
                    outtime.setHorizontalAlignment(Element.ALIGN_CENTER);

                    tableMid.addCell(outtime);
                    PdfPCell cellPhoneNumber = new PdfPCell(new Phrase(campDetailsInfos.get(i).getAddress(), boldMidTableCell));
                    cellPhoneNumber.setHorizontalAlignment(Element.ALIGN_CENTER);

                    tableMid.addCell(cellPhoneNumber);
              //    PdfPCell addressIN = new PdfPCell(new Phrase(campDetailsInfos.get(i).getBlockId(), boldMidTableCell));
               //   tableMid.addCell(addressIN);
               //  PdfPCell addressOuT = new PdfPCell(new Phrase(campDetailsInfos.get(i).getBlockId(), boldMidTableCell));
               //  tableMid.addCell(addressOuT);

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
            PdfPCell cellCustomerSignatureStatic = new PdfPCell(new Phrase("", boldTableCell));
            cellCustomerSignatureStatic.setBorder(Rectangle.NO_BORDER);
            cellCustomerSignatureStatic.setHorizontalAlignment(Element.ALIGN_LEFT);
            tableSignatures.addCell(cellCustomerSignatureStatic);
            PdfPCell cellEngineerSignatureStatic = new PdfPCell(new Phrase("", boldTableCell));
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


