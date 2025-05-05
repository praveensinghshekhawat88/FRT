package com.callmangement.ehr.ehrActivities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.content.FileProvider
import com.callmangement.R
import com.callmangement.databinding.ActivityCampDailyWorkReportPdfBinding
import com.callmangement.ehr.models.DealersInfo
import com.callmangement.ehr.support.OnSingleClickListener
import com.callmangement.utils.PrefManager
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

class CampDailyWorkReportPdfActivity : BaseActivity() {
    private var mActivity: Activity? = null
    private var binding: ActivityCampDailyWorkReportPdfBinding? = null
    private var dealersInfoArrayList: ArrayList<DealersInfo>? = null
    private var TrainingNo = ""
    private val targetPdf = "Camp Daily Work Report.pdf"
    private var imageView: ImageView? = null
    private var pdfRendererHelper: PdfRendererHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivityCampDailyWorkReportPdfBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        init()
    }

    private fun init() {
        mActivity = this

        val bundle = intent.extras
        dealersInfoArrayList = bundle!!.getSerializable("mylist") as ArrayList<DealersInfo>?
        TrainingNo = bundle.getString("TrainingNo", "")
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.buttonPDF.visibility = View.VISIBLE
        binding!!.actionBar.textToolbarTitle.text =
            resources.getString(R.string.camp_daily_work_report)
        setUpData()
        setClickListener()
        createPdf(targetPdf)
    }

    private fun setUpData() {
    }

    private fun setClickListener() {
        binding!!.actionBar.ivBack.setOnClickListener { view: View? -> onBackPressed() }
        binding!!.actionBar.buttonPDF.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                val file = File(getExternalFilesDir(null), targetPdf)
                val pdfURi = FileProvider.getUriForFile(
                    mActivity!!,
                    mActivity!!.packageName + ".provider",
                    file,
                    targetPdf
                )
                val share = Intent()
                share.setAction(Intent.ACTION_SEND)
                share.setType("application/pdf")
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                share.putExtra(Intent.EXTRA_STREAM, pdfURi)
                startActivity(share)
            }
        })
    }

    private fun createPdf(pdfFileName: String) {
        val filePath = File(getExternalFilesDir(null), pdfFileName)
        val document = Document(PageSize.A4)
        try {
            PdfWriter.getInstance(document, FileOutputStream(filePath))
            document.open()
            val boldHeading = Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.BOLD)
            val pHeading = Paragraph("Camp Daily Report", boldHeading)
            pHeading.spacingAfter = 5f
            pHeading.alignment = Element.ALIGN_CENTER
            document.add(pHeading)
            val boldTableCell = Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.BOLD)
            val tableTop1 = PdfPTable(3)
            tableTop1.widthPercentage = 100f
            tableTop1.spacingBefore = 20f
            val cellDateStaticTop = PdfPCell(Phrase("Date __________________", boldTableCell))
            cellDateStaticTop.border = Rectangle.NO_BORDER
            tableTop1.addCell(cellDateStaticTop)
            val cellBlockStatic = PdfPCell(Phrase("Block __________________", boldTableCell))
            cellBlockStatic.border = Rectangle.NO_BORDER
            tableTop1.addCell(cellBlockStatic)
            val cellDistrictStatic = PdfPCell(Phrase("District __________________", boldTableCell))
            cellDistrictStatic.border = Rectangle.NO_BORDER
            tableTop1.addCell(cellDistrictStatic)
            document.add(tableTop1)
            val tableTop2 = PdfPTable(3)
            tableTop2.widthPercentage = 100f
            tableTop2.spacingBefore = 20f
            val cellCampNoStatic = PdfPCell(Phrase("Camp No $TrainingNo", boldTableCell))
            cellCampNoStatic.colspan = 3
            cellCampNoStatic.border = Rectangle.NO_BORDER
            tableTop2.addCell(cellCampNoStatic)
            document.add(tableTop2)
            val tableTop3 = PdfPTable(3)
            tableTop3.widthPercentage = 100f
            tableTop3.spacingBefore = 20f
            val cellDistrictCoordinatorStatic =
                PdfPCell(Phrase("District Coordinator __________________", boldTableCell))
            cellDistrictCoordinatorStatic.colspan = 3
            cellDistrictCoordinatorStatic.border = Rectangle.NO_BORDER
            tableTop3.addCell(cellDistrictCoordinatorStatic)
            document.add(tableTop3)
            val boldMidTableCell = Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.NORMAL)
            val tableMid = PdfPTable(6)
            tableMid.widthPercentage = 100f
            tableMid.spacingBefore = 40f
            tableMid.setWidths(intArrayOf(1, 1, 1, 2, 2, 2))
            val cellSrNoStatic = PdfPCell(Phrase("Sr No", boldTableCell))
            tableMid.addCell(cellSrNoStatic)
            val cellDateStatic = PdfPCell(Phrase("Date", boldTableCell))
            tableMid.addCell(cellDateStatic)
            val cellFPSStatic = PdfPCell(Phrase("FPS", boldTableCell))
            tableMid.addCell(cellFPSStatic)
            val cellDealerNameStatic = PdfPCell(Phrase("Dealer Name", boldTableCell))
            tableMid.addCell(cellDealerNameStatic)
            val cellPhoneNumberStatic = PdfPCell(Phrase("Phone Number", boldTableCell))
            tableMid.addCell(cellPhoneNumberStatic)
            val cellSignatureStatic = PdfPCell(Phrase("Signature", boldTableCell))
            tableMid.addCell(cellSignatureStatic)
            if (dealersInfoArrayList != null && dealersInfoArrayList!!.size > 0) {
                for (i in dealersInfoArrayList!!.indices) {
                    val cellEmpty = PdfPCell(Phrase("\n", boldMidTableCell))
                    tableMid.addCell(cellEmpty)
                    tableMid.addCell(cellEmpty)
                    val cellFPS =
                        PdfPCell(Phrase(dealersInfoArrayList!![i].fpscode, boldMidTableCell))
                    tableMid.addCell(cellFPS)
                    val cellDealerName = PdfPCell(
                        Phrase(
                            dealersInfoArrayList!![i].customerNameEng,
                            boldMidTableCell
                        )
                    )
                    tableMid.addCell(cellDealerName)
                    val cellPhoneNumber =
                        PdfPCell(Phrase(dealersInfoArrayList!![i].mobileNo, boldMidTableCell))
                    tableMid.addCell(cellPhoneNumber)
                    tableMid.addCell(cellEmpty)
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
        imageView = findViewById(R.id.imagepdfView)
        // Path to your PDF file
        val pdfFile = File(getExternalFilesDir(null), targetPdf)
        pdfRendererHelper = PdfRendererHelper(pdfFile)
        val firstPage = pdfRendererHelper!!.renderPage(0)
        imageView!!.setImageBitmap(firstPage)
    }
}
