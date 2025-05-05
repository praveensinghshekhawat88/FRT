package com.callmangement.ehr.ehrActivities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.callmangement.R
import com.callmangement.databinding.ActivityExcelCampBinding
import com.callmangement.ehr.models.CampDetailsInfo
import com.callmangement.ui.ins_weighing_scale.adapter.ViewImageInstalledAdapterIris
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
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExcelforCamp : BaseActivity() {

    private var mActivity: Activity? = null
    private var binding: ActivityExcelCampBinding? = null
    private var preference: PrefManager? = null
    private var campDetailsInfos: List<CampDetailsInfo>? = null
    private var TrainingNo: String = ""
    private var sharedPreferences: SharedPreferences? = null
    private var targetPdf: String = "Camp Record.pdf"
    private val editTextExcel: EditText? = null
    private val filePath = File("/storage/emulated/0/Download" + "/Demo.xls")
    private var startdateData: String? = null
    private var enddateData: String? = null
    private var imageView: ImageView? = null
    private var pdfRendererHelper: PdfRendererHelper? = null
    private val mOnItemViewClickListener: ViewImageInstalledAdapterIris.OnItemViewClickListener? =
        null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivityExcelCampBinding.inflate(layoutInflater)
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
        val json = sharedPreferences!!.getString("coursescamp", null)
        // below line is to get the type of our array list.
        val type = object : TypeToken<ArrayList<CampDetailsInfo?>?>() {}.type

        // in below line we are getting data from gson
        // and saving it to our array list
        campDetailsInfos = gson.fromJson(json, type)

        // checking below if the array list is empty or not
        if (campDetailsInfos == null) {
            // if the array list is empty
            // creating a new array list.
            campDetailsInfos = ArrayList()
            //  Log.d("nbb",""+campDetailsInfos);
        }

        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.buttonPDF.visibility = View.VISIBLE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.camp_record_pdf)
        setUpData()
        setClickListener()
        createPdf(targetPdf)
    }

    private fun setUpData() {
    }

    private fun setClickListener() {
        binding!!.actionBar.ivBack.setOnClickListener {
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
            //  share.setType("application/pdf");
            share.setDataAndType(pdfURi, "application/pdf")
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            share.putExtra(Intent.EXTRA_STREAM, pdfURi)
            startActivity(share)

            val contentUri = Uri.parse(pdfURi.toString())
            downloadFromContentUri(this@ExcelforCamp, contentUri)
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

    private fun createPdf(pdfFileName: String) {
        val filePath = File(getExternalFilesDir(null), pdfFileName)
        val document = Document(PageSize.A4)
        try {
            PdfWriter.getInstance(document, FileOutputStream(filePath))
            document.open()
            val boldHeading = Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.BOLD)
            // Paragraph pHeading = new Paragraph("Camp Record \n " + startdateData+ "  -  "+enddateData, boldHeading);
            val pHeading = if (startdateData!!.isEmpty() && enddateData!!.isEmpty()) {
                Paragraph(
                    """
                        Camp Record 
                        All-Camp
                        """.trimIndent(), boldHeading
                )
            } else {
                Paragraph(
                    "Camp Record \n$startdateData  -  $enddateData",
                    boldHeading
                )
            }

            pHeading.spacingAfter = 5f
            pHeading.alignment = Element.ALIGN_CENTER
            document.add(pHeading)
            val boldTableCell = Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.BOLD)

            val boldMidTableCell = Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.NORMAL)
            val tableMid = PdfPTable(6)
            tableMid.widthPercentage = 100f
            tableMid.spacingBefore = 40f
            tableMid.setWidths(intArrayOf(1, 1, 1, 1, 1, 2))
            val cellSrNoStatic = PdfPCell(Phrase("District\nName", boldTableCell))
            cellSrNoStatic.horizontalAlignment = Element.ALIGN_CENTER
            tableMid.addCell(cellSrNoStatic)
            val cellDateStatic = PdfPCell(Phrase("Block\nName", boldTableCell))
            cellDateStatic.horizontalAlignment = Element.ALIGN_CENTER

            tableMid.addCell(cellDateStatic)
            val PunchDate = PdfPCell(Phrase("StartDate", boldTableCell))
            PunchDate.horizontalAlignment = Element.ALIGN_CENTER

            tableMid.addCell(PunchDate)
            val PunchDay = PdfPCell(Phrase("EndDay", boldTableCell))
            PunchDay.horizontalAlignment = Element.ALIGN_CENTER

            tableMid.addCell(PunchDay)
            val PunchInTime = PdfPCell(Phrase("Status", boldTableCell))
            PunchInTime.horizontalAlignment = Element.ALIGN_CENTER

            tableMid.addCell(PunchInTime)
            val cellSignatureStatic = PdfPCell(Phrase("Address", boldTableCell))
            cellSignatureStatic.horizontalAlignment = Element.ALIGN_CENTER

            tableMid.addCell(cellSignatureStatic)
            //  PdfPCell addressIn = new PdfPCell(new Phrase("AddressIn", boldTableCell));
            //    tableMid.addCell(addressIn);
            //   PdfPCell addressOut = new PdfPCell(new Phrase("AddressOut", boldTableCell));
            //   tableMid.addCell(addressOut);
            if (campDetailsInfos != null && campDetailsInfos!!.size > 0) {
                for (i in campDetailsInfos!!.indices) {
                    val cellEmpty = PdfPCell(Phrase("\n", boldMidTableCell))
                    //tableMid.addCell(cellEmpty);
                    // tableMid.addCell(cellEmpty);
                    val cellFPS =
                        PdfPCell(Phrase(campDetailsInfos!![i].districtNameEng, boldMidTableCell))
                    cellFPS.horizontalAlignment = Element.ALIGN_CENTER

                    tableMid.addCell(cellFPS)
                    val cellDealerName = PdfPCell(
                        Phrase(
                            campDetailsInfos!![i].blockName.toString(),
                            boldMidTableCell
                        )
                    )
                    cellDealerName.horizontalAlignment = Element.ALIGN_CENTER

                    tableMid.addCell(cellDealerName)

                    val day = PdfPCell(Phrase(campDetailsInfos!![i].startDate, boldMidTableCell))
                    day.horizontalAlignment = Element.ALIGN_CENTER

                    tableMid.addCell(day)
                    val intime = PdfPCell(Phrase(campDetailsInfos!![i].endDate, boldMidTableCell))
                    intime.horizontalAlignment = Element.ALIGN_CENTER


                    tableMid.addCell(intime)
                    val outtime = PdfPCell(Phrase(campDetailsInfos!![i].status, boldMidTableCell))
                    outtime.horizontalAlignment = Element.ALIGN_CENTER

                    tableMid.addCell(outtime)
                    val cellPhoneNumber =
                        PdfPCell(Phrase(campDetailsInfos!![i].address, boldMidTableCell))
                    cellPhoneNumber.horizontalAlignment = Element.ALIGN_CENTER

                    tableMid.addCell(cellPhoneNumber)

                    //    PdfPCell addressIN = new PdfPCell(new Phrase(campDetailsInfos.get(i).getBlockId(), boldMidTableCell));
                    //   tableMid.addCell(addressIN);
                    //  PdfPCell addressOuT = new PdfPCell(new Phrase(campDetailsInfos.get(i).getBlockId(), boldMidTableCell));
                    //  tableMid.addCell(addressOuT);
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
            val cellCustomerSignatureStatic = PdfPCell(Phrase("", boldTableCell))
            cellCustomerSignatureStatic.border = Rectangle.NO_BORDER
            cellCustomerSignatureStatic.horizontalAlignment = Element.ALIGN_LEFT
            tableSignatures.addCell(cellCustomerSignatureStatic)
            val cellEngineerSignatureStatic = PdfPCell(Phrase("", boldTableCell))
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

        imageView = findViewById(R.id.imagepdfView)
        // Path to your PDF file
        val pdfFile = File(getExternalFilesDir(null), targetPdf)
        pdfRendererHelper = PdfRendererHelper(pdfFile)
        val firstPage = pdfRendererHelper!!.renderPage(0)
        imageView!!.setImageBitmap(firstPage)
    }
}


