package com.callmangement.EHR.ehrActivities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.callmangement.EHR.models.AttendanceDetailsInfo;
import com.callmangement.R;
import com.callmangement.databinding.ActivityExcelTableBinding;
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

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

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


public class Excelfortable extends BaseActivity {
    Activity mActivity;
    private ActivityExcelTableBinding binding;
    PrefManager preference;
    List<AttendanceDetailsInfo> attendanceDetailsInfoList ;
    String TrainingNo = "";
    SharedPreferences sharedPreferences;
    String targetPdf = "Attendance Record.pdf";
    private EditText editTextExcel;
    private final File filePathh = new File("/storage/emulated/0/Download" + "/Demo.xlsx");
    private final File filePathnew = new File(Environment.getExternalStorageDirectory() + "/Demo.xlsx");
    String startdateData,enddateData;

    WebView webView;

    private ImageView imageView;
    private PdfRendererHelper pdfRendererHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityExcelTableBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        Intent intent = getIntent();
        startdateData = intent.getExtras().getString("startDateKey");
        enddateData = intent.getExtras().getString("endDateKey");
        mActivity = this;
        preference = new PrefManager(mActivity);
        sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        // creating a variable for gson.
        Gson gson = new Gson();
        // below line is to get to string present from our
        // shared prefs if not present setting it as null.
        String json = sharedPreferences.getString("courses", null);

        if (json == null) {
            Log.e("ExcelGeneration", "No data found in SharedPreferences.");
            Toast.makeText(this, "No data available to generate Excel", Toast.LENGTH_SHORT).show();
            return;
        }

        // below line is to get the type of our array list.
        Type type = new TypeToken<ArrayList<AttendanceDetailsInfo>>() {}.getType();
        // in below line we are getting data from gson
        // and saving it to our array list
        attendanceDetailsInfoList = gson.fromJson(json, type);


        if (attendanceDetailsInfoList == null || attendanceDetailsInfoList.isEmpty()) {
            attendanceDetailsInfoList = new ArrayList<>();

            Log.e("PDFGeneration", "Attendance list is empty.");
            Toast.makeText(this, "No attendance data found", Toast.LENGTH_SHORT).show();
            return;
        }










        //  Log.d("gfhvbb",""+attendanceDetailsInfoList);



        //  TrainingNo = bundle.getString("TrainingNo", "");
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.buttonPDF.setVisibility(View.VISIBLE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.attendance_record_pdf));
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




        binding.actionBar.buttonPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(getExternalFilesDir(null), targetPdf);
                Uri pdfURi = FileProvider.getUriForFile(mActivity, mActivity.getPackageName() + ".provider", file , targetPdf );
                Intent share = new Intent();
                share.setAction(Intent.ACTION_SEND);
                // share.setType("Attendance/pdf");
                share.setDataAndType(pdfURi, "application/pdf");
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                share.putExtra(Intent.EXTRA_STREAM, pdfURi);
                startActivity(share);
                // startActivity(Intent.createChooser(share, "Share via"));
                //  Log.d("yesss","ijowh"+pdfURi);





/*

            File file = new File(getExternalFilesDir(null), targetPdf);
            Uri pdfURi = FileProvider.getUriForFile(mActivity, mActivity.getPackageName() + ".provider", file, targetPdf);

            Intent share = new Intent();
            share.setAction(Intent.ACTION_SEND);
            share.setType("application/pdf");
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            share.putExtra(Intent.EXTRA_STREAM, pdfURi);
            startActivity(share);
*/

                // Replace contentUri with the actual content URI of the file you want to download
                Uri contentUri = Uri.parse(String.valueOf(pdfURi));
                downloadFromContentUri(Excelfortable.this, contentUri);



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
            //   Paragraph pHeading = new Paragraph("Attendance Record ", boldHeading);

            Paragraph pHeading;
            if(startdateData.isEmpty() && enddateData.isEmpty()){
                pHeading = new Paragraph("Attendance Record \n"+ "All"+ "-"+"Attendance", boldHeading);

            }
            else
            {
                pHeading = new Paragraph("Attendance Record \n"+ startdateData+ "  -  "+enddateData, boldHeading);


            }



            pHeading.setSpacingAfter(5);
            pHeading.setAlignment(Element.ALIGN_CENTER);
            document.add(pHeading);
            Font boldTableCell = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);


            Font boldMidTableCell = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
            PdfPTable tableMid = new PdfPTable(8);
            tableMid.setWidthPercentage(100);
            tableMid.setSpacingBefore(40);
            tableMid.setWidths(new int[]{1, 1,1, 1, 1, 1,2,2});
            PdfPCell cellSrNoStatic = new PdfPCell(new Phrase("District\n name", boldTableCell));
            tableMid.addCell(cellSrNoStatic);
            PdfPCell cellDateStatic = new PdfPCell(new Phrase("User\nName", boldTableCell));
            tableMid.addCell(cellDateStatic);
            PdfPCell PunchDate = new PdfPCell(new Phrase("Punch\nDate", boldTableCell));
            tableMid.addCell(PunchDate);
            PdfPCell PunchDay = new PdfPCell(new Phrase("Punch\nDay", boldTableCell));
            tableMid.addCell(PunchDay);
            PdfPCell PunchInTime = new PdfPCell(new Phrase("PunchIn\nTime", boldTableCell));
            tableMid.addCell(PunchInTime);
            PdfPCell cellSignatureStatic = new PdfPCell(new Phrase("Punch\nOut\nTime", boldTableCell));
            tableMid.addCell(cellSignatureStatic);
            PdfPCell addressIn = new PdfPCell(new Phrase("AddressIn", boldTableCell));
            tableMid.addCell(addressIn);
            PdfPCell addressOut = new PdfPCell(new Phrase("AddressOut", boldTableCell));
            tableMid.addCell(addressOut);
            if(attendanceDetailsInfoList != null && attendanceDetailsInfoList.size() > 0) {
                for (int i = 0; i < attendanceDetailsInfoList.size(); i++) {
                    PdfPCell cellEmpty = new PdfPCell(new Phrase("\n", boldMidTableCell));
                    //tableMid.addCell(cellEmpty);
                    // tableMid.addCell(cellEmpty);
                    PdfPCell cellFPS = new PdfPCell(new Phrase(attendanceDetailsInfoList.get(i).getDistrictName(), boldMidTableCell));
                    tableMid.addCell(cellFPS);
                    PdfPCell cellDealerName = new PdfPCell(new Phrase(attendanceDetailsInfoList.get(i).getUsername(), boldMidTableCell));
                    tableMid.addCell(cellDealerName);
                    PdfPCell cellPhoneNumber = new PdfPCell(new Phrase(attendanceDetailsInfoList.get(i).getPunchInDate(), boldMidTableCell));
                    tableMid.addCell(cellPhoneNumber);
                    PdfPCell day = new PdfPCell(new Phrase(attendanceDetailsInfoList.get(i).getDayName(), boldMidTableCell));
                    tableMid.addCell(day);
                    PdfPCell intime = new PdfPCell(new Phrase(attendanceDetailsInfoList.get(i).getPunchInTime(), boldMidTableCell));
                    tableMid.addCell(intime);
                    PdfPCell outtime = new PdfPCell(new Phrase(attendanceDetailsInfoList.get(i).getPunchOutTime(), boldMidTableCell));
                    tableMid.addCell(outtime);
                    PdfPCell addressIN = new PdfPCell(new Phrase(attendanceDetailsInfoList.get(i).getAddress_In(), boldMidTableCell));
                    tableMid.addCell(addressIN);
                    PdfPCell addressOuT = new PdfPCell(new Phrase(attendanceDetailsInfoList.get(i).getAddress_Out(), boldMidTableCell));
                    tableMid.addCell(addressOuT);

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



    // Set up PDF Viewer




    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pdfRendererHelper != null) {
            pdfRendererHelper.close();
        }
    }
    private void createAndShowExcelSheet() {
        // Create a new Excel workbook
        HSSFWorkbook workbook = new HSSFWorkbook ();
        // Create a sheet in the workbook
        Sheet sheet = workbook.createSheet("Sheet1");

        // Create rows and cells with sample data
        Row row1 = sheet.createRow(0);
        Cell cellA1 = row1.createCell(0);
        cellA1.setCellValue("Name");

        Row row2 = sheet.createRow(1);
        Cell cellA2 = row2.createCell(0);
        cellA2.setCellValue("John Doe");

        Row row3 = sheet.createRow(2);
        Cell cellA3 = row3.createCell(0);
        cellA3.setCellValue("Jane Smith");

        // Save the workbook to a file
        String filePath = Environment.getExternalStorageDirectory().getPath() + "/sample.xlsx";
        File file = new File(filePath);

        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            //  workbook.close();
            outputStream.close();

            Toast.makeText(this, "Excel sheet created successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Display the file using an appropriate application
        openExcelFile(file);
    }

    private void openExcelFile(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application found to open Excel files", Toast.LENGTH_SHORT).show();
        }
    }
}


