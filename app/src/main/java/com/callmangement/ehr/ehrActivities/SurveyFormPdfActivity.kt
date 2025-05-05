package com.callmangement.ehr.ehrActivities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.content.FileProvider
import com.callmangement.R
import com.callmangement.databinding.ActivitySurveyFormBinding
import com.callmangement.ehr.models.SurveyFormDetailsInfo
import com.callmangement.ehr.support.CustomBorder
import com.callmangement.ehr.support.LineDash
import com.callmangement.ehr.support.OnSingleClickListener
import com.callmangement.ehr.support.SolidLine
import com.callmangement.utils.PrefManager
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.Image
import com.itextpdf.text.PageSize
import com.itextpdf.text.Paragraph
import com.itextpdf.text.Phrase
import com.itextpdf.text.Rectangle
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.Arrays

class SurveyFormPdfActivity : BaseActivity() {
    private var mActivity: Activity? = null
    private var binding: ActivitySurveyFormBinding? = null
    private var preference: PrefManager? = null
    private val targetPdf = "survey form.pdf"
    private var surveyFormDetailsInfo: SurveyFormDetailsInfo? = null
    private var imageView: ImageView? = null
    private var pdfRendererHelper: PdfRendererHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivitySurveyFormBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        init()
    }

    private fun init() {
        mActivity = this
        preference = PrefManager(mActivity!!)

        val bundle = intent.extras
        surveyFormDetailsInfo =
            bundle!!.getSerializable("surveyFormDetailsInfo") as SurveyFormDetailsInfo?

        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.buttonPDF.visibility = View.VISIBLE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.survey_form)

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

            val boldInstall = Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.BOLD)
            val pInstall = Paragraph("Installation Cum Service and Supply Report", boldInstall)
            pInstall.alignment = Element.ALIGN_CENTER
            document.add(pInstall)

            val boldBalaji = Font(Font.FontFamily.TIMES_ROMAN, 16f, Font.BOLD)
            val pBalaji = Paragraph("Balaji Info Lube", boldBalaji)
            pBalaji.alignment = Element.ALIGN_CENTER
            document.add(pBalaji)

            val boldContact = Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.NORMAL)
            val pContact = Paragraph(
                "Contact: (0291) 2640964, 9950387164  e-mail: balajilubes@gmail.com",
                boldContact
            )
            pContact.alignment = Element.ALIGN_CENTER
            pContact.spacingAfter = 5f
            document.add(pContact)

            val boldGSTNumber = Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.NORMAL)
            val pGSTNumberStatic = Paragraph(
                "GSTIN NO - " + (if ((surveyFormDetailsInfo != null && surveyFormDetailsInfo!!.gstin_No != null)) surveyFormDetailsInfo!!.gstin_No else ""),
                boldGSTNumber
            )
            pGSTNumberStatic.alignment = Element.ALIGN_LEFT
            pGSTNumberStatic.spacingBefore = 10f
            document.add(pGSTNumberStatic)

            val boldTableCell = Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.BOLD)

            val table = PdfPTable(4)
            table.widthPercentage = 100f
            table.setWidths(intArrayOf(1, 1, 1, 1))
            val cellCustomerNameStatic = PdfPCell(Phrase("CUSTOMER NAME", boldTableCell))
            table.addCell(cellCustomerNameStatic)
            val cellCustomerName =
                PdfPCell(Phrase(if ((surveyFormDetailsInfo != null && surveyFormDetailsInfo!!.customerName != null)) surveyFormDetailsInfo!!.customerName else ""))
            table.addCell(cellCustomerName)
            val cellBillChallanNoStatic = PdfPCell(Phrase("BILL/ CHALLAN NO.", boldTableCell))
            table.addCell(cellBillChallanNoStatic)
            val cellBillChallanNo =
                PdfPCell(Phrase(if ((surveyFormDetailsInfo != null && surveyFormDetailsInfo!!.ticketNo != null)) surveyFormDetailsInfo!!.ticketNo else ""))
            table.addCell(cellBillChallanNo)

            val cellCustomerAddressStatic = PdfPCell(Phrase("CUSTOMER ADDRESS", boldTableCell))
            table.addCell(cellCustomerAddressStatic)
            val cellCustomerAddress =
                PdfPCell(Phrase(if ((surveyFormDetailsInfo != null && surveyFormDetailsInfo!!.address != null)) surveyFormDetailsInfo!!.address else ""))
            table.addCell(cellCustomerAddress)
            val cellBillRemarksStatic = PdfPCell(
                Phrase(
                    "BILL REMARKS IF ANY : " + (if ((surveyFormDetailsInfo != null && surveyFormDetailsInfo!!.billRemark != null)) surveyFormDetailsInfo!!.billRemark else ""),
                    boldTableCell
                )
            )
            cellBillRemarksStatic.colspan = 2
            cellBillRemarksStatic.rowspan = 2
            table.addCell(cellBillRemarksStatic)

            val cellPointOfContactStatic = PdfPCell(Phrase("POINT OF CONTACT", boldTableCell))
            table.addCell(cellPointOfContactStatic)
            val cellPointOfContact =
                PdfPCell(Phrase(if ((surveyFormDetailsInfo != null && surveyFormDetailsInfo!!.mobileNumber != null)) "1" + surveyFormDetailsInfo!!.mobileNumber else ""))
            table.addCell(cellPointOfContact)

            val cellMobileNoStatic = PdfPCell(Phrase("MOBILE NO.", boldTableCell))
            table.addCell(cellMobileNoStatic)
            val cellMobileNo =
                PdfPCell(Phrase(if ((surveyFormDetailsInfo != null && surveyFormDetailsInfo!!.mobileNumber != null)) surveyFormDetailsInfo!!.mobileNumber else ""))
            table.addCell(cellMobileNo)
            val cellDateOfInstallationStatic =
                PdfPCell(Phrase("DATE OF INSTALLATION", boldTableCell))
            table.addCell(cellDateOfInstallationStatic)
            val cellDateOfInstallation =
                PdfPCell(Phrase(if ((surveyFormDetailsInfo != null && surveyFormDetailsInfo!!.installationDateStr != null)) surveyFormDetailsInfo!!.installationDateStr else ""))
            table.addCell(cellDateOfInstallation)

            val pSpace = Paragraph("", boldContact)
            pSpace.alignment = Element.ALIGN_CENTER
            pSpace.spacingAfter = 5f
            document.add(pSpace)

            document.add(table)

            val tableTypeOfCall = PdfPTable(6)
            tableTypeOfCall.widthPercentage = 100f
            tableTypeOfCall.spacingBefore = 20f
            tableTypeOfCall.spacingAfter = 10f
            val cellTypeOfCallStatic = PdfPCell(Phrase("Type Of Call", boldTableCell))
            cellTypeOfCallStatic.border = Rectangle.NO_BORDER
            tableTypeOfCall.addCell(cellTypeOfCallStatic)

            val cellTypeOfCallSupplyStatic = PdfPCell()
            val cellTypeOfCallWarrantyStatic = PdfPCell()
            val cellTypeOfCallInstallationStatic = PdfPCell()
            val cellTypeOfCallPayableStatic = PdfPCell()
            val cellTypeOfCallCourtesyStatic = PdfPCell()

            val imageChkBoxUnchecked: Image?
            val imageChkBoxChecked: Image?
            try {
                val ims = assets.open("check_box_unchecked_in_pdf.png")
                val bmp = BitmapFactory.decodeStream(ims)
                val stream = ByteArrayOutputStream()
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
                imageChkBoxUnchecked = Image.getInstance(stream.toByteArray())
                imageChkBoxUnchecked.scaleAbsolute(20f, 20f)
                imageChkBoxUnchecked.alignment = Element.ALIGN_CENTER
            } catch (ex: Exception) {
                return
            }

            try {
                val ims = assets.open("check_box_checked_in_pdf.png")
                val bmp = BitmapFactory.decodeStream(ims)
                val stream = ByteArrayOutputStream()
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
                imageChkBoxChecked = Image.getInstance(stream.toByteArray())
                imageChkBoxChecked.scaleAbsolute(20f, 20f)
                imageChkBoxChecked.alignment = Element.ALIGN_CENTER
            } catch (ex: Exception) {
                return
            }

            val pTypeOfCallSupplyStatic = Paragraph("Supply")
            pTypeOfCallSupplyStatic.alignment = Element.ALIGN_CENTER
            cellTypeOfCallSupplyStatic.border = Rectangle.NO_BORDER


            val pTypeOfCallWarrantyStatic = Paragraph("Warranty")
            pTypeOfCallWarrantyStatic.alignment = Element.ALIGN_CENTER
            cellTypeOfCallWarrantyStatic.border = Rectangle.NO_BORDER
            cellTypeOfCallWarrantyStatic.horizontalAlignment = Element.ALIGN_RIGHT
            cellTypeOfCallWarrantyStatic.verticalAlignment = Element.ALIGN_MIDDLE


            val pTypeOfCallInstallationStatic = Paragraph("Installation")
            pTypeOfCallInstallationStatic.alignment = Element.ALIGN_CENTER
            cellTypeOfCallInstallationStatic.border = Rectangle.NO_BORDER
            cellTypeOfCallInstallationStatic.horizontalAlignment =
                Element.ALIGN_RIGHT
            cellTypeOfCallInstallationStatic.verticalAlignment =
                Element.ALIGN_MIDDLE


            val pTypeOfCallPayableStatic = Paragraph("Payable")
            pTypeOfCallPayableStatic.alignment = Element.ALIGN_CENTER
            cellTypeOfCallPayableStatic.border = Rectangle.NO_BORDER
            cellTypeOfCallPayableStatic.horizontalAlignment = Element.ALIGN_RIGHT
            cellTypeOfCallPayableStatic.verticalAlignment = Element.ALIGN_MIDDLE


            val pTypeOfCallCourtesyStatic = Paragraph("Courtesy")
            pTypeOfCallCourtesyStatic.alignment = Element.ALIGN_CENTER
            cellTypeOfCallCourtesyStatic.border = Rectangle.NO_BORDER
            cellTypeOfCallCourtesyStatic.horizontalAlignment = Element.ALIGN_RIGHT
            cellTypeOfCallCourtesyStatic.verticalAlignment = Element.ALIGN_MIDDLE


            if (surveyFormDetailsInfo != null && surveyFormDetailsInfo!!.typeOfCallId != null) {
                if (surveyFormDetailsInfo!!.typeOfCallId!!.contains(",")) {
                    val result = surveyFormDetailsInfo!!.typeOfCallId!!.split(",".toRegex())
                        .dropLastWhile { it.isEmpty() }.toTypedArray()
                    val typeOfCallIdList = ArrayList(Arrays.asList(*result))

                    if (typeOfCallIdList.contains("1")) {
                        if (imageChkBoxChecked != null) cellTypeOfCallSupplyStatic.addElement(
                            imageChkBoxChecked
                        )
                    } else {
                        if (imageChkBoxUnchecked != null) cellTypeOfCallSupplyStatic.addElement(
                            imageChkBoxUnchecked
                        )
                    }

                    if (typeOfCallIdList.contains("2")) {
                        if (imageChkBoxChecked != null) cellTypeOfCallWarrantyStatic.addElement(
                            imageChkBoxChecked
                        )
                    } else {
                        if (imageChkBoxUnchecked != null) cellTypeOfCallWarrantyStatic.addElement(
                            imageChkBoxUnchecked
                        )
                    }

                    if (typeOfCallIdList.contains("3")) {
                        if (imageChkBoxChecked != null) cellTypeOfCallInstallationStatic.addElement(
                            imageChkBoxChecked
                        )
                    } else {
                        if (imageChkBoxUnchecked != null) cellTypeOfCallInstallationStatic.addElement(
                            imageChkBoxUnchecked
                        )
                    }

                    if (typeOfCallIdList.contains("4")) {
                        if (imageChkBoxChecked != null) cellTypeOfCallPayableStatic.addElement(
                            imageChkBoxChecked
                        )
                    } else {
                        if (imageChkBoxUnchecked != null) cellTypeOfCallPayableStatic.addElement(
                            imageChkBoxUnchecked
                        )
                    }

                    if (typeOfCallIdList.contains("5")) {
                        if (imageChkBoxChecked != null) cellTypeOfCallCourtesyStatic.addElement(
                            imageChkBoxChecked
                        )
                    } else {
                        if (imageChkBoxUnchecked != null) cellTypeOfCallCourtesyStatic.addElement(
                            imageChkBoxUnchecked
                        )
                    }
                } else {
                    if (surveyFormDetailsInfo!!.typeOfCallId!!.length > 0) {
                        if (surveyFormDetailsInfo!!.typeOfCallId.equals("1", ignoreCase = true)) {
                            if (imageChkBoxChecked != null) cellTypeOfCallSupplyStatic.addElement(
                                imageChkBoxChecked
                            )
                        } else {
                            if (imageChkBoxUnchecked != null) cellTypeOfCallSupplyStatic.addElement(
                                imageChkBoxUnchecked
                            )
                        }

                        if (surveyFormDetailsInfo!!.typeOfCallId.equals("2", ignoreCase = true)) {
                            if (imageChkBoxChecked != null) cellTypeOfCallWarrantyStatic.addElement(
                                imageChkBoxChecked
                            )
                        } else {
                            if (imageChkBoxUnchecked != null) cellTypeOfCallWarrantyStatic.addElement(
                                imageChkBoxUnchecked
                            )
                        }

                        if (surveyFormDetailsInfo!!.typeOfCallId.equals("3", ignoreCase = true)) {
                            if (imageChkBoxChecked != null) cellTypeOfCallInstallationStatic.addElement(
                                imageChkBoxChecked
                            )
                        } else {
                            if (imageChkBoxUnchecked != null) cellTypeOfCallInstallationStatic.addElement(
                                imageChkBoxUnchecked
                            )
                        }

                        if (surveyFormDetailsInfo!!.typeOfCallId.equals("4", ignoreCase = true)) {
                            if (imageChkBoxChecked != null) cellTypeOfCallPayableStatic.addElement(
                                imageChkBoxChecked
                            )
                        } else {
                            if (imageChkBoxUnchecked != null) cellTypeOfCallPayableStatic.addElement(
                                imageChkBoxUnchecked
                            )
                        }

                        if (surveyFormDetailsInfo!!.typeOfCallId.equals("5", ignoreCase = true)) {
                            if (imageChkBoxChecked != null) cellTypeOfCallCourtesyStatic.addElement(
                                imageChkBoxChecked
                            )
                        } else {
                            if (imageChkBoxUnchecked != null) cellTypeOfCallCourtesyStatic.addElement(
                                imageChkBoxUnchecked
                            )
                        }
                    } else {
                        if (imageChkBoxUnchecked != null) cellTypeOfCallSupplyStatic.addElement(
                            imageChkBoxUnchecked
                        )
                        if (imageChkBoxUnchecked != null) cellTypeOfCallWarrantyStatic.addElement(
                            imageChkBoxUnchecked
                        )
                        if (imageChkBoxUnchecked != null) cellTypeOfCallInstallationStatic.addElement(
                            imageChkBoxUnchecked
                        )
                        if (imageChkBoxUnchecked != null) cellTypeOfCallPayableStatic.addElement(
                            imageChkBoxUnchecked
                        )
                        if (imageChkBoxUnchecked != null) cellTypeOfCallCourtesyStatic.addElement(
                            imageChkBoxUnchecked
                        )
                    }
                }
            }

            cellTypeOfCallSupplyStatic.addElement(pTypeOfCallSupplyStatic)
            tableTypeOfCall.addCell(cellTypeOfCallSupplyStatic)

            cellTypeOfCallWarrantyStatic.addElement(pTypeOfCallWarrantyStatic)
            tableTypeOfCall.addCell(cellTypeOfCallWarrantyStatic)

            cellTypeOfCallInstallationStatic.addElement(pTypeOfCallInstallationStatic)
            tableTypeOfCall.addCell(cellTypeOfCallInstallationStatic)

            cellTypeOfCallPayableStatic.addElement(pTypeOfCallPayableStatic)
            tableTypeOfCall.addCell(cellTypeOfCallPayableStatic)


            cellTypeOfCallCourtesyStatic.addElement(pTypeOfCallCourtesyStatic)
            tableTypeOfCall.addCell(cellTypeOfCallCourtesyStatic)

            document.add(tableTypeOfCall)


            val tableItemsDetails = PdfPTable(1)
            tableItemsDetails.widthPercentage = 80f
            tableItemsDetails.spacingBefore = 20f
            val cellItemsDetailsStatic =
                PdfPCell(Phrase("Items Detail :-" + (if ((surveyFormDetailsInfo != null && surveyFormDetailsInfo!!.itemDetail != null)) surveyFormDetailsInfo!!.itemDetail else "")))
            tableItemsDetails.addCell(cellItemsDetailsStatic)

            val solid: LineDash = SolidLine()

            val cellPurchaseOrderDetails = PdfPCell(
                Phrase(
                    """
                    PURCHASE ORDER DETAILS :-
                    ${if ((surveyFormDetailsInfo != null && surveyFormDetailsInfo!!.purchaseOrderDtl != null)) surveyFormDetailsInfo!!.purchaseOrderDtl else ""}
                    """.trimIndent()
                )
            )
            cellPurchaseOrderDetails.border = Rectangle.ALIGN_BOTTOM
            cellPurchaseOrderDetails.cellEvent = CustomBorder(solid, solid, null, null)
            tableItemsDetails.addCell(cellPurchaseOrderDetails)

            val cellSpecificationOfMachineInstalled = PdfPCell(
                Phrase(
                    """
                    SPECIFICATION OF MACHINE INSTALLED :-
                    ${if ((surveyFormDetailsInfo != null && surveyFormDetailsInfo!!.installedMachineSpecification != null)) surveyFormDetailsInfo!!.installedMachineSpecification else ""}
                    """.trimIndent()
                )
            )
            cellSpecificationOfMachineInstalled.border = Rectangle.ALIGN_BOTTOM
            cellSpecificationOfMachineInstalled.cellEvent = CustomBorder(solid, solid, null, null)
            tableItemsDetails.addCell(cellSpecificationOfMachineInstalled)

            val cellAnyAccessory = PdfPCell(
                Phrase(
                    """
                    ANY ACCESSORY :-
                    ${if ((surveyFormDetailsInfo != null && surveyFormDetailsInfo!!.accessesory != null)) surveyFormDetailsInfo!!.accessesory else ""}
                    """.trimIndent()
                )
            )
            cellAnyAccessory.border = Rectangle.ALIGN_BOTTOM
            cellAnyAccessory.cellEvent = CustomBorder(solid, solid, null, null)
            tableItemsDetails.addCell(cellAnyAccessory)

            document.add(tableItemsDetails)

            val tableInstallationDone = PdfPTable(1)
            tableInstallationDone.widthPercentage = 80f
            val cellInstallationDoneStatic =
                PdfPCell(Phrase("INSTALLATION DONE :-" + (if ((surveyFormDetailsInfo != null && surveyFormDetailsInfo!!.installationDone != null)) surveyFormDetailsInfo!!.installationDone else "")))
            tableInstallationDone.addCell(cellInstallationDoneStatic)

            document.add(tableInstallationDone)


            val boldCustomerRemarks = Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.BOLD)
            val pCustomerRemarksStatic = Paragraph("Customer Remark : ", boldCustomerRemarks)
            pCustomerRemarksStatic.alignment = Element.ALIGN_LEFT
            pCustomerRemarksStatic.spacingBefore = 10f
            document.add(pCustomerRemarksStatic)

            val pCustomerRemarks =
                Paragraph(if ((surveyFormDetailsInfo != null && surveyFormDetailsInfo!!.customer_Remark != null)) surveyFormDetailsInfo!!.customer_Remark else "")
            pCustomerRemarks.alignment = Element.ALIGN_LEFT
            document.add(pCustomerRemarks)

            val tableNames = PdfPTable(2)
            tableNames.widthPercentage = 100f
            tableNames.spacingBefore = 40f
            val cellCustomerNameAtBottomStatic =
                PdfPCell(Phrase(if ((surveyFormDetailsInfo != null && surveyFormDetailsInfo!!.customerName != null)) surveyFormDetailsInfo!!.customerName else ""))
            cellCustomerNameAtBottomStatic.border = Rectangle.NO_BORDER
            cellCustomerNameAtBottomStatic.horizontalAlignment =
                Element.ALIGN_LEFT
            tableNames.addCell(cellCustomerNameAtBottomStatic)
            val cellEngineerNameStatic =
                PdfPCell(Phrase(if ((surveyFormDetailsInfo != null && surveyFormDetailsInfo!!.engineerName != null)) surveyFormDetailsInfo!!.engineerName else ""))
            cellEngineerNameStatic.border = Rectangle.NO_BORDER
            cellEngineerNameStatic.horizontalAlignment = Element.ALIGN_RIGHT
            tableNames.addCell(cellEngineerNameStatic)

            document.add(tableNames)


            val tableSignaturesLines = PdfPTable(2)
            tableSignaturesLines.widthPercentage = 100f
            val cellCustomerSignatureLinesStatic = PdfPCell(Phrase("___________________________"))
            cellCustomerSignatureLinesStatic.border = Rectangle.NO_BORDER
            cellCustomerSignatureLinesStatic.horizontalAlignment =
                Element.ALIGN_LEFT
            tableSignaturesLines.addCell(cellCustomerSignatureLinesStatic)
            val cellEngineerSignatureLinesStatic = PdfPCell(Phrase("___________________________"))
            cellEngineerSignatureLinesStatic.border = Rectangle.NO_BORDER
            cellEngineerSignatureLinesStatic.horizontalAlignment =
                Element.ALIGN_RIGHT
            tableSignaturesLines.addCell(cellEngineerSignatureLinesStatic)

            document.add(tableSignaturesLines)


            val tableSignatures = PdfPTable(2)
            tableSignatures.widthPercentage = 100f
            val cellCustomerSignatureStatic =
                PdfPCell(Phrase("Customer Name, Sign & Stamp", boldTableCell))
            cellCustomerSignatureStatic.border = Rectangle.NO_BORDER
            cellCustomerSignatureStatic.horizontalAlignment = Element.ALIGN_LEFT
            tableSignatures.addCell(cellCustomerSignatureStatic)
            val cellEngineerSignatureStatic =
                PdfPCell(Phrase("Engineer Name & Sign", boldTableCell))
            cellEngineerSignatureStatic.border = Rectangle.NO_BORDER
            cellEngineerSignatureStatic.horizontalAlignment = Element.ALIGN_RIGHT
            tableSignatures.addCell(cellEngineerSignatureStatic)
            document.add(tableSignatures)
            val boldHeadOffice = Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.NORMAL)
            val pHeadOffice = Paragraph(
                "Head Office: - 21, Residency Road, Opp. Manidhari Brain Hospital, Jodhpur",
                boldHeadOffice
            )
            pHeadOffice.spacingBefore = 20f
            pHeadOffice.alignment = Element.ALIGN_CENTER
            document.add(pHeadOffice)
            val boldTel = Font(Font.FontFamily.TIMES_ROMAN, 12f, Font.NORMAL)
            val pTel = Paragraph("Tel: (0291) - 2640964, Fax +91-291-2654117", boldTel)
            pTel.alignment = Element.ALIGN_CENTER
            document.add(pTel)
            val boldStatement = Font(Font.FontFamily.TIMES_ROMAN, 10f, Font.NORMAL)
            val pStatement =
                Paragraph("\"We'll bend over backward to meet your needs\"", boldStatement)
            pStatement.alignment = Element.ALIGN_CENTER
            pStatement.spacingAfter = 5f
            document.add(pStatement)

            document.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        viewPDF()
    }

    private fun viewPDF() {
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


        imageView = findViewById(R.id.imagepdfView)

        // Path to your PDF file
        val pdfFile = File(getExternalFilesDir(null), targetPdf)

        pdfRendererHelper = PdfRendererHelper(pdfFile)
        val firstPage = pdfRendererHelper!!.renderPage(0)
        imageView!!.setImageBitmap(firstPage)
    }
}
