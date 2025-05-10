package com.callmangement.ui.iris_derivery_installation

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.print.PrintAttributes
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.callmangement.R
import com.callmangement.support.pdfcreator.activity.PDFCreatorActivity
import com.callmangement.support.pdfcreator.utils.PDFUtil
import com.callmangement.support.pdfcreator.utils.PDFUtil.PDFUtilListener
import com.callmangement.support.pdfcreator.views.PDFBody
import com.callmangement.support.pdfcreator.views.PDFFooterView
import com.callmangement.support.pdfcreator.views.PDFHeaderView
import com.callmangement.support.pdfcreator.views.PDFTableView
import com.callmangement.support.pdfcreator.views.PDFTableView.PDFTableRowView
import com.callmangement.support.pdfcreator.views.basic.PDFHorizontalView
import com.callmangement.support.pdfcreator.views.basic.PDFImageView
import com.callmangement.support.pdfcreator.views.basic.PDFLineSeparatorView
import com.callmangement.support.pdfcreator.views.basic.PDFTextView
import com.callmangement.utils.Constants
import com.callmangement.utils.DateTimeUtils.currentTime
import java.io.File
import java.util.Locale

class IrisInstallationPendingPdfActivity : PDFCreatorActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }
        createPDF("" + System.currentTimeMillis(), object : PDFUtilListener {
            override fun pdfGenerationSuccess(savedPDFFile: File) {
                //Toast.makeText(ReportPdfActivity.this, "PDF Created", Toast.LENGTH_SHORT).show();
            }

            override fun pdfGenerationFailure(exception: Exception) {
                Toast.makeText(
                    this@IrisInstallationPendingPdfActivity,
                    "PDF NOT Created",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun getHeaderView(pageIndex: Int): PDFHeaderView {
        val headerView = PDFHeaderView(applicationContext)
        val horizontalView = PDFHorizontalView(applicationContext)
        val pdfTextView = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.HEADER)
        val word = SpannableString("Iris Installation Pending List")
        word.setSpan(
            ForegroundColorSpan(Color.DKGRAY),
            0,
            word.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        pdfTextView.setText(word)
        pdfTextView.setLayout(
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT, 1f
            )
        )
        pdfTextView.view.gravity = Gravity.CENTER_HORIZONTAL
        pdfTextView.view.setTypeface(pdfTextView.view.typeface, Typeface.BOLD)
        horizontalView.addView(pdfTextView)
        headerView.addView(horizontalView)
        val lineSeparatorView1 =
            PDFLineSeparatorView(applicationContext).setBackgroundColor(Color.WHITE)
        headerView.addView(lineSeparatorView1)
        return headerView
    }

    override fun getBodyViews(): PDFBody {
        val pdfBody = PDFBody()

        val lineSeparatorView1 =
            PDFLineSeparatorView(applicationContext).setBackgroundColor(Color.WHITE)
        lineSeparatorView1.setLayout(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                8, 0f
            )
        )
        pdfBody.addView(lineSeparatorView1)


        val lineSeparatorView2 =
            PDFLineSeparatorView(applicationContext).setBackgroundColor(Color.WHITE)
        lineSeparatorView2.setLayout(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                8, 0f
            )
        )
        pdfBody.addView(lineSeparatorView2)

        val lineSeparatorView3 =
            PDFLineSeparatorView(applicationContext).setBackgroundColor(Color.WHITE)
        lineSeparatorView3.setLayout(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                8, 0f
            )
        )
        pdfBody.addView(lineSeparatorView3)

        val lineSeparatorView4 =
            PDFLineSeparatorView(applicationContext).setBackgroundColor(Color.BLACK)
        pdfBody.addView(lineSeparatorView4)

        val widthPercent = intArrayOf(15, 25, 20, 15, 15) // Sum should be equal to 100%
        val textInTable = arrayOf("FPS Code", "Name", "Mobile No.", "District", "Block")

        val tableHeader = PDFTableRowView(applicationContext)
        for (s in textInTable) {
            val pdfTextView = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.H3)
            pdfTextView.setText(s)
            pdfTextView.view.setTypeface(pdfTextView.view.typeface, Typeface.BOLD)
            pdfTextView.view.setPaddingRelative(0, 5, 0, 5)
            tableHeader.addToRow(pdfTextView)
        }

        if (Constants.irisInsPendingArrayList != null && Constants.irisInsPendingArrayList.size > 0) {
            var tableRowView1 = PDFTableRowView(applicationContext)
            tableRowView1.view.setPaddingRelative(0, 5, 0, 0)

            var data = Constants.irisInsPendingArrayList[0]

            var pdfTextViewFPSCode = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
            pdfTextViewFPSCode.setText(data.fpscode)
            tableRowView1.addToRow(pdfTextViewFPSCode)

            var pdfTextViewTicketNo = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
            pdfTextViewTicketNo.setText(data.dealerName)
            tableRowView1.addToRow(pdfTextViewTicketNo)

            var pdfTextViewDistDate = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
            pdfTextViewDistDate.setText(data.dealerMobileNo)
            tableRowView1.addToRow(pdfTextViewDistDate)

            var pdfTextViewPhotoUpload =
                PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
            pdfTextViewPhotoUpload.setText(data.districtName)
            tableRowView1.addToRow(pdfTextViewPhotoUpload)

            var pdfTextViewFormUpload = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
            pdfTextViewFormUpload.setText(data.blockName)
            tableRowView1.addToRow(pdfTextViewFormUpload)

            val tableView = PDFTableView(applicationContext, tableHeader, tableRowView1)

            for (i in 1 until Constants.irisInsPendingArrayList.size) {
                data = Constants.irisInsPendingArrayList[i]

                tableRowView1 = PDFTableRowView(applicationContext)
                pdfTextViewFPSCode = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
                pdfTextViewFPSCode.setText(data.fpscode)
                tableRowView1.addToRow(pdfTextViewFPSCode)

                pdfTextViewTicketNo = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
                pdfTextViewTicketNo.setText(data.dealerName)
                tableRowView1.addToRow(pdfTextViewTicketNo)

                pdfTextViewDistDate = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
                pdfTextViewDistDate.setText(data.dealerMobileNo)
                tableRowView1.addToRow(pdfTextViewDistDate)

                pdfTextViewPhotoUpload =
                    PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
                pdfTextViewPhotoUpload.setText(data.districtName)
                tableRowView1.addToRow(pdfTextViewPhotoUpload)

                pdfTextViewFormUpload = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
                pdfTextViewFormUpload.setText(data.blockName)
                tableRowView1.addToRow(pdfTextViewFormUpload)

                tableView.addRow(tableRowView1)
            }

            tableView.setColumnWidth(*widthPercent)
            pdfBody.addView(tableView)
        }

        val lineSeparatorView5 =
            PDFLineSeparatorView(applicationContext).setBackgroundColor(Color.WHITE)
        lineSeparatorView5.setLayout(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                8, 0f
            )
        )
        pdfBody.addView(lineSeparatorView5)

        val lineSeparatorView6 =
            PDFLineSeparatorView(applicationContext).setBackgroundColor(Color.BLACK)
        pdfBody.addView(lineSeparatorView6)

        return pdfBody
    }

    @SuppressLint("RtlHardcoded")
    override fun getFooterView(pageIndex: Int): PDFFooterView {
        val footerView = PDFFooterView(applicationContext)
        val pdfTextViewPage = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.SMALL)
        val pdfTextViewPage1 = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.SMALL)

        pdfTextViewPage.setText(String.format(Locale.getDefault(), "Page: %d", pageIndex + 1))
        pdfTextViewPage.setLayout(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 0f
            )
        )
        pdfTextViewPage.view.gravity = Gravity.CENTER_HORIZONTAL

        pdfTextViewPage1.setText(currentTime)
        pdfTextViewPage1.setLayout(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 0f
            )
        )
        pdfTextViewPage1.view.gravity = Gravity.LEFT

        footerView.addView(pdfTextViewPage)
        footerView.addView(pdfTextViewPage1)

        return footerView
    }

    override fun getWatermarkView(forPage: Int): PDFImageView? {
        val pdfImageView = PDFImageView(applicationContext)
        val childLayoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            200, Gravity.CENTER
        )
        pdfImageView.setLayout(childLayoutParams)

        pdfImageView.setImageResource(R.drawable.app_logo)
        pdfImageView.setImageScale(ImageView.ScaleType.FIT_CENTER)
        pdfImageView.view.alpha = 0.3f

        return pdfImageView
    }

    override fun onNextClicked(savedPDFFile: File?) {
//        Uri pdfUri = Uri.fromFile(savedPDFFile);
//        Intent intentPdfViewer = new Intent(ReportPdfActivity.this, PdfViewerActivity.class);
//        intentPdfViewer.putExtra(PdfViewerActivity.PDF_FILE_URI, pdfUri);
//        startActivity(intentPdfViewer);

        if (savedPDFFile == null || !savedPDFFile.exists()) {
            Toast.makeText(this, R.string.text_generated_file_error, Toast.LENGTH_SHORT).show()
            return
        }
        val printAttributeBuilder = PrintAttributes.Builder()
        printAttributeBuilder.setMediaSize(PrintAttributes.MediaSize.ISO_A4)
        printAttributeBuilder.setMinMargins(PrintAttributes.Margins.NO_MARGINS)
        PDFUtil.printPdf(
            this@IrisInstallationPendingPdfActivity,
            savedPDFFile,
            printAttributeBuilder.build()
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}





