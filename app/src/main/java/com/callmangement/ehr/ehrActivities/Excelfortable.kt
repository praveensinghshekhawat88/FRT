package com.callmangement.ehr.ehrActivities

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.WebView
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.callmangement.R
import com.callmangement.databinding.ActivityExcelTableBinding
import com.callmangement.ehr.models.AttendanceDetailsInfo
import com.callmangement.utils.PrefManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.PageSize
import com.itextpdf.text.Paragraph
import com.itextpdf.text.Phrase
import com.itextpdf.text.Rectangle
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Sheet
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Excelfortable : BaseActivity() {
    var mActivity: Activity? = null
    private var binding: ActivityExcelTableBinding? = null
    var preference: PrefManager? = null
    var attendanceDetailsInfoList: List<AttendanceDetailsInfo>? = null
    var TrainingNo: String = ""
    var sharedPreferences: SharedPreferences? = null
    var targetPdf: String = "Attendance Record.pdf"
    private val editTextExcel: EditText? = null
    private val filePathh = File("/storage/emulated/0/Download" + "/Demo.xlsx")
    private val filePathnew =
        File(Environment.getExternalStorageDirectory().toString() + "/Demo.xlsx")
    var startdateData: String? = null
    var enddateData: String? = null

    var webView: WebView? = null

    private var imageView: ImageView? = null
    private var pdfRendererHelper: PdfRendererHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivityExcelTableBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        init()
    }

    private fun init() {
        val intent = intent
        startdateData = intent.extras!!.getString("startDateKey")
        enddateData = intent.extras!!.getString("endDateKey")
        mActivity = this
        preference = PrefManager(mActivity!!)
        sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE)
        // creating a variable for gson.
        val gson = Gson()
        // below line is to get to string present from our
        // shared prefs if not present setting it as null.
        val json = sharedPreferences!!.getString("courses", null)

        if (json == null) {
            Log.e("ExcelGeneration", "No data found in SharedPreferences.")
            Toast.makeText(this, "No data available to generate Excel", Toast.LENGTH_SHORT).show()
            return
        }

        // below line is to get the type of our array list.
        val type = object : TypeToken<ArrayList<AttendanceDetailsInfo?>?>() {}.type
        // in below line we are getting data from gson
        // and saving it to our array list
        attendanceDetailsInfoList = gson.fromJson(json, type)


        if (attendanceDetailsInfoList == null || attendanceDetailsInfoList!!.isEmpty()) {
            attendanceDetailsInfoList = ArrayList()

            Log.e("PDFGeneration", "Attendance list is empty.")
            Toast.makeText(this, "No attendance data found", Toast.LENGTH_SHORT).show()
            return
        }


        //  Log.d("gfhvbb",""+attendanceDetailsInfoList);


        //  TrainingNo = bundle.getString("TrainingNo", "");
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.buttonPDF.visibility = View.VISIBLE
        binding!!.actionBar.textToolbarTitle.text =
            resources.getString(R.string.attendance_record_pdf)
        setUpData()
        setClickListener()
        createPdf(targetPdf)


        // createExcelForm();
        //  createAndShowExcelSheet();
    }

    private fun setUpData() {
    }

    private fun setClickListener() {
        binding!!.actionBar.ivBack.setOnClickListener { /*     SharedPreferences.Editor editor = sharedPreferences.edit();
                                                                editor.remove("courses");
                                                                editor.apply();*/
            onBackPressed()
        }




        binding!!.actionBar.buttonPDF.setOnClickListener {
            val file = File(getExternalFilesDir(null), targetPdf)
            val pdfURi = FileProvider.getUriForFile(
                mActivity!!,
                mActivity!!.packageName + ".provider",
                file,
                targetPdf
            )
            val share = Intent()
            share.setAction(Intent.ACTION_SEND)
            // share.setType("Attendance/pdf");
            share.setDataAndType(pdfURi, "application/pdf")
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            share.putExtra(Intent.EXTRA_STREAM, pdfURi)
            startActivity(share)


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
            val contentUri = Uri.parse(pdfURi.toString())
            downloadFromContentUri(this@Excelfortable, contentUri)
        }
    }


    private fun downloadFromContentUri(context: Context, contentUri: Uri) {
        val contentResolver = context.contentResolver

        // Create a new file in the Downloads directory
        //   File downloadDir = new File(String.valueOf(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)));

        //  File outputFile = new File(downloadDir, "Attendance Record.pdf");
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

        val outputFile = File("/storage/emulated/0/Download/$timeStamp.pdf")


        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null

        try {
            // Open an input stream to the content URI
            inputStream = contentResolver.openInputStream(contentUri)

            // Open an output stream to the file
            outputStream = FileOutputStream(outputFile)

            // Copy the content from the input stream to the output stream
            val buffer = ByteArray(4096)
            var bytesRead: Int
            while ((inputStream!!.read(buffer).also { bytesRead = it }) != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            //Toast.makeText(this, "File downloaded: " + outputFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "File downloaded", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Download failed", Toast.LENGTH_SHORT).show()
        } finally {
            // Close the streams to release resources
            try {
                inputStream?.close()
                outputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
    }

    private fun createPdf(pdfFileName: String) {
        val filePath = File(getExternalFilesDir(null), pdfFileName)
        val document = Document(PageSize.A4)
        try {
            PdfWriter.getInstance(document, FileOutputStream(filePath))
            document.open()
            val boldHeading = Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.BOLD)

            //   Paragraph pHeading = new Paragraph("Attendance Record ", boldHeading);
            val pHeading = if (startdateData!!.isEmpty() && enddateData!!.isEmpty()) {
                Paragraph(
                    """
                        Attendance Record 
                        All-Attendance
                        """.trimIndent(),
                    boldHeading
                )
            } else {
                Paragraph(
                    "Attendance Record \n$startdateData  -  $enddateData",
                    boldHeading
                )
            }



            pHeading.spacingAfter = 5f
            pHeading.alignment = Element.ALIGN_CENTER
            document.add(pHeading)
            val boldTableCell = Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.BOLD)


            val boldMidTableCell = Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.NORMAL)
            val tableMid = PdfPTable(8)
            tableMid.widthPercentage = 100f
            tableMid.spacingBefore = 40f
            tableMid.setWidths(intArrayOf(1, 1, 1, 1, 1, 1, 2, 2))
            val cellSrNoStatic = PdfPCell(Phrase("District\n name", boldTableCell))
            tableMid.addCell(cellSrNoStatic)
            val cellDateStatic = PdfPCell(Phrase("User\nName", boldTableCell))
            tableMid.addCell(cellDateStatic)
            val PunchDate = PdfPCell(Phrase("Punch\nDate", boldTableCell))
            tableMid.addCell(PunchDate)
            val PunchDay = PdfPCell(Phrase("Punch\nDay", boldTableCell))
            tableMid.addCell(PunchDay)
            val PunchInTime = PdfPCell(Phrase("PunchIn\nTime", boldTableCell))
            tableMid.addCell(PunchInTime)
            val cellSignatureStatic = PdfPCell(Phrase("Punch\nOut\nTime", boldTableCell))
            tableMid.addCell(cellSignatureStatic)
            val addressIn = PdfPCell(Phrase("AddressIn", boldTableCell))
            tableMid.addCell(addressIn)
            val addressOut = PdfPCell(Phrase("AddressOut", boldTableCell))
            tableMid.addCell(addressOut)
            if (attendanceDetailsInfoList != null && attendanceDetailsInfoList!!.size > 0) {
                for (i in attendanceDetailsInfoList!!.indices) {
                    val cellEmpty = PdfPCell(Phrase("\n", boldMidTableCell))
                    //tableMid.addCell(cellEmpty);
                    // tableMid.addCell(cellEmpty);
                    val cellFPS = PdfPCell(
                        Phrase(
                            attendanceDetailsInfoList!![i].districtName,
                            boldMidTableCell
                        )
                    )
                    tableMid.addCell(cellFPS)
                    val cellDealerName =
                        PdfPCell(Phrase(attendanceDetailsInfoList!![i].username, boldMidTableCell))
                    tableMid.addCell(cellDealerName)
                    val cellPhoneNumber = PdfPCell(
                        Phrase(
                            attendanceDetailsInfoList!![i].punchInDate,
                            boldMidTableCell
                        )
                    )
                    tableMid.addCell(cellPhoneNumber)
                    val day =
                        PdfPCell(Phrase(attendanceDetailsInfoList!![i].dayName, boldMidTableCell))
                    tableMid.addCell(day)
                    val intime = PdfPCell(
                        Phrase(
                            attendanceDetailsInfoList!![i].punchInTime,
                            boldMidTableCell
                        )
                    )
                    tableMid.addCell(intime)
                    val outtime = PdfPCell(
                        Phrase(
                            attendanceDetailsInfoList!![i].punchOutTime,
                            boldMidTableCell
                        )
                    )
                    tableMid.addCell(outtime)
                    val addressIN = PdfPCell(
                        Phrase(
                            attendanceDetailsInfoList!![i].address_In,
                            boldMidTableCell
                        )
                    )
                    tableMid.addCell(addressIN)
                    val addressOuT = PdfPCell(
                        Phrase(
                            attendanceDetailsInfoList!![i].address_Out,
                            boldMidTableCell
                        )
                    )
                    tableMid.addCell(addressOuT)
                }
            } else {
                for (i in 0..19) {
                    val cellEmpty = PdfPCell(Phrase("\n", boldMidTableCell))
                    tableMid.addCell(cellEmpty)
                    tableMid.addCell(cellEmpty)
                    tableMid.addCell(cellEmpty)
                    tableMid.addCell(cellEmpty)
                    tableMid.addCell(cellEmpty)
                    tableMid.addCell(cellEmpty)
                }
            }
            document.add(tableMid)
            val tableSignatures = PdfPTable(2)
            tableSignatures.widthPercentage = 100f
            tableSignatures.spacingBefore = 60f
            val cellCustomerSignatureStatic =
                PdfPCell(Phrase("District Coordinator Sign", boldTableCell))
            cellCustomerSignatureStatic.border = Rectangle.NO_BORDER
            cellCustomerSignatureStatic.horizontalAlignment = Element.ALIGN_LEFT
            tableSignatures.addCell(cellCustomerSignatureStatic)
            val cellEngineerSignatureStatic = PdfPCell(Phrase("Area Inspector Sign", boldTableCell))
            cellEngineerSignatureStatic.border = Rectangle.NO_BORDER
            cellEngineerSignatureStatic.horizontalAlignment = Element.ALIGN_RIGHT
            tableSignatures.addCell(cellEngineerSignatureStatic)
            document.add(tableSignatures)
            document.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        viewPDF()
    }


    private fun viewPDF() {
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




        imageView = findViewById(R.id.imagepdfView)

        // Path to your PDF file
        val pdfFile = File(getExternalFilesDir(null), targetPdf)

        pdfRendererHelper = PdfRendererHelper(pdfFile)
        val firstPage = pdfRendererHelper!!.renderPage(0)
        imageView!!.setImageBitmap(firstPage)
    }


    // Set up PDF Viewer
    override fun onDestroy() {
        super.onDestroy()
        if (pdfRendererHelper != null) {
            pdfRendererHelper!!.close()
        }
    }

    private fun createAndShowExcelSheet() {
        // Create a new Excel workbook
        val workbook = HSSFWorkbook()
        // Create a sheet in the workbook
        val sheet: Sheet = workbook.createSheet("Sheet1")

        // Create rows and cells with sample data
        val row1 = sheet.createRow(0)
        val cellA1 = row1.createCell(0)
        cellA1.setCellValue("Name")

        val row2 = sheet.createRow(1)
        val cellA2 = row2.createCell(0)
        cellA2.setCellValue("John Doe")

        val row3 = sheet.createRow(2)
        val cellA3 = row3.createCell(0)
        cellA3.setCellValue("Jane Smith")

        // Save the workbook to a file
        val filePath = Environment.getExternalStorageDirectory().path + "/sample.xlsx"
        val file = File(filePath)

        try {
            val outputStream = FileOutputStream(file)
            workbook.write(outputStream)
            //  workbook.close();
            outputStream.close()

            Toast.makeText(this, "Excel sheet created successfully", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Display the file using an appropriate application
        openExcelFile(file)
    }

    private fun openExcelFile(file: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(
            Uri.fromFile(file),
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        )
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No application found to open Excel files", Toast.LENGTH_SHORT)
                .show()
        }
    }
}


