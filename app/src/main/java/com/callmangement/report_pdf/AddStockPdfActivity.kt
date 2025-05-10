package com.callmangement.report_pdf

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

class AddStockPdfActivity : PDFCreatorActivity() {
    private var email: String? = ""
    private var name: String? = ""
    private var totalQuantity = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        name = intent.getStringExtra("name")
        email = intent.getStringExtra("email")

        createPDF("" + System.currentTimeMillis(), object : PDFUtilListener {
            override fun pdfGenerationSuccess(savedPDFFile: File) {
//                Toast.makeText(ReportPdfActivity.this, "PDF Created", Toast.LENGTH_SHORT).show();
            }

            override fun pdfGenerationFailure(exception: Exception) {
                Toast.makeText(this@AddStockPdfActivity, "PDF NOT Created", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    override fun getHeaderView(pageIndex: Int): PDFHeaderView {
        val headerView = PDFHeaderView(applicationContext)

        val horizontalView = PDFHorizontalView(applicationContext)

        val pdfTextView = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.HEADER)
        val word = SpannableString("Add Stock")
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
        lineSeparatorView1.setLayout(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                8, 0f
            )
        )
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

        val pdfCompanyNameView = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.H3)
        pdfCompanyNameView.setText("Name        :          $name\nEmail        :          $email")
        pdfBody.addView(pdfCompanyNameView)

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

        val widthPercent = intArrayOf(20, 20, 20, 20)
        val textInTable = arrayOf("SrNo", "Item Name", "Quantity")

        val tableHeader = PDFTableRowView(applicationContext)
        for (s in textInTable) {
            val pdfTextView = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.H3)
            pdfTextView.setText(s)
            pdfTextView.view.setTypeface(pdfTextView.view.typeface, Typeface.BOLD)
            pdfTextView.view.setPaddingRelative(0, 5, 0, 5)
            tableHeader.addToRow(pdfTextView)
        }

        if (Constants.modelAddStock != null && Constants.modelAddStock!!.size > 0) {
            totalQuantity = 0

            var tableRowView1 = PDFTableRowView(applicationContext)
            tableRowView1.view.setPaddingRelative(0, 5, 0, 0)

            var modelAddStock = Constants.modelAddStock!![0]

            totalQuantity = modelAddStock.qty.toInt()

            var pdfTextViewSrNo = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
            pdfTextViewSrNo.setText("1")
            tableRowView1.addToRow(pdfTextViewSrNo)

            var pdfTextViewItemName = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
            pdfTextViewItemName.setText("" + modelAddStock.itemName)
            tableRowView1.addToRow(pdfTextViewItemName)

            var pdfTextViewQuantity = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
            pdfTextViewQuantity.setText("" + modelAddStock.qty)
            tableRowView1.addToRow(pdfTextViewQuantity)


            val tableView = PDFTableView(applicationContext, tableHeader, tableRowView1)

            for (i in 1 until Constants.modelAddStock!!.size) {
                modelAddStock = Constants.modelAddStock!![i]

                totalQuantity += modelAddStock.qty.toInt()

                val srNo = i + 1

                tableRowView1 = PDFTableRowView(applicationContext)
                pdfTextViewSrNo = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
                pdfTextViewSrNo.setText("" + srNo)
                tableRowView1.addToRow(pdfTextViewSrNo)

                pdfTextViewItemName = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
                pdfTextViewItemName.setText("" + modelAddStock.itemName)
                tableRowView1.addToRow(pdfTextViewItemName)

                pdfTextViewQuantity = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
                pdfTextViewQuantity.setText("" + modelAddStock.qty)
                tableRowView1.addToRow(pdfTextViewQuantity)

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

        val tableRowViewBottom = PDFTableRowView(applicationContext)
        tableRowViewBottom.view.setPaddingRelative(0, 5, 0, 5)

        val pdfTextViewSrNo = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.H3)
        pdfTextViewSrNo.setText("Total")
        pdfTextViewSrNo.view.setTypeface(pdfTextViewSrNo.view.typeface, Typeface.BOLD)
        tableRowViewBottom.addToRow(pdfTextViewSrNo)

        val pdfTextViewItemName = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.H3)
        pdfTextViewItemName.setText("")
        tableRowViewBottom.addToRow(pdfTextViewItemName)

        val pdfTextViewReceivedQuantity =
            PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.H3)
        pdfTextViewReceivedQuantity.setText("" + totalQuantity)
        pdfTextViewReceivedQuantity.view.setTypeface(
            pdfTextViewReceivedQuantity.view.typeface,
            Typeface.BOLD
        )
        tableRowViewBottom.addToRow(pdfTextViewReceivedQuantity)
        pdfBody.addView(tableRowViewBottom)

        val lineSeparatorView7 =
            PDFLineSeparatorView(applicationContext).setBackgroundColor(Color.BLACK)
        pdfBody.addView(lineSeparatorView7)

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

        pdfTextViewPage1.setText("" + currentTime)
        pdfTextViewPage1.setLayout(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 0f
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
        if (savedPDFFile == null || !savedPDFFile.exists()) {
            Toast.makeText(this, R.string.text_generated_file_error, Toast.LENGTH_SHORT).show()
            return
        }
        val printAttributeBuilder = PrintAttributes.Builder()
        printAttributeBuilder.setMediaSize(PrintAttributes.MediaSize.ISO_A4)
        printAttributeBuilder.setMinMargins(PrintAttributes.Margins.NO_MARGINS)
        PDFUtil.printPdf(this@AddStockPdfActivity, savedPDFFile, printAttributeBuilder.build())
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
