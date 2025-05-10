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
import com.callmangement.model.reports.SLA_Reports_Info
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
import com.callmangement.utils.DateTimeUtils.currentTime
import java.io.File
import java.util.Locale

class SLAReportPdfActivity : PDFCreatorActivity() {
    private var sla_reports_infoArrayList: ArrayList<SLA_Reports_Info>? = ArrayList()
    private var ResolveInDays = 0
    private var district: String? = null
    private var title: String? = null
    private var name: String? = null
    private var email: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        sla_reports_infoArrayList =
            intent.getSerializableExtra("param") as ArrayList<SLA_Reports_Info>?
        ResolveInDays = intent.getIntExtra("ResolveInDays", 0)
        title = intent.getStringExtra("title")
        district = intent.getStringExtra("district")
        name = intent.getStringExtra("name")
        email = intent.getStringExtra("email")

        createPDF("" + System.currentTimeMillis(), object : PDFUtilListener {
            override fun pdfGenerationSuccess(savedPDFFile: File) {
                //Toast.makeText(ReportPdfActivity.this, "PDF Created", Toast.LENGTH_SHORT).show();
            }

            override fun pdfGenerationFailure(exception: Exception) {
                Toast.makeText(this@SLAReportPdfActivity, "PDF NOT Created", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    override fun getHeaderView(pageIndex: Int): PDFHeaderView {
        val headerView = PDFHeaderView(applicationContext)

        val horizontalView = PDFHorizontalView(applicationContext)

        val pdfTextView = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.HEADER)
        val word = SpannableString("" + title)
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

        val pdfCompanyNameView = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.H3)
        pdfCompanyNameView.setText("Name       :     $name\nEmail       :     $email\nDistrict     :    $district")
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

        val widthPercent = intArrayOf(20, 20, 20, 20) // Sum should be equal to 100%
        val textInTable = arrayOf("District", "Grand Total", "Resolve Days")

        //        PDFTextView pdfTableTitleView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.P);
//        pdfTableTitleView.setText("Table Example");
//        pdfBody.addView(pdfTableTitleView);

//        final PDFPageBreakView pdfPageBreakView = new PDFPageBreakView(getApplicationContext());
//        pdfBody.addView(pdfPageBreakView);
        val tableHeader = PDFTableRowView(applicationContext)
        for (s in textInTable) {
            val pdfTextView = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.H3)
            pdfTextView.setText(s)
            pdfTextView.view.setTypeface(pdfTextView.view.typeface, Typeface.BOLD)
            pdfTextView.view.setPaddingRelative(0, 5, 0, 5)
            tableHeader.addToRow(pdfTextView)
        }

        if (sla_reports_infoArrayList != null && sla_reports_infoArrayList!!.size > 0) {
            var tableRowView1 = PDFTableRowView(applicationContext)
            tableRowView1.view.setPaddingRelative(0, 5, 0, 0)

            var sla_reports_info = sla_reports_infoArrayList!![0]

            var pdfTextViewDistrict = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
            pdfTextViewDistrict.setText(sla_reports_info.district)
            tableRowView1.addToRow(pdfTextViewDistrict)

            var pdfTextViewComplaintCount =
                PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
            pdfTextViewComplaintCount.setText("" + sla_reports_info.complaintCount)
            tableRowView1.addToRow(pdfTextViewComplaintCount)

            var pdfTextViewResolveInDays =
                PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
            pdfTextViewResolveInDays.setText("" + ResolveInDays)
            tableRowView1.addToRow(pdfTextViewResolveInDays)

            val tableView = PDFTableView(applicationContext, tableHeader, tableRowView1)

            for (i in 1 until sla_reports_infoArrayList!!.size) {
                sla_reports_info = sla_reports_infoArrayList!![i]

                tableRowView1 = PDFTableRowView(applicationContext)
                pdfTextViewDistrict = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
                pdfTextViewDistrict.setText(sla_reports_info.district)
                tableRowView1.addToRow(pdfTextViewDistrict)

                pdfTextViewComplaintCount =
                    PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
                pdfTextViewComplaintCount.setText("" + sla_reports_info.complaintCount)
                tableRowView1.addToRow(pdfTextViewComplaintCount)

                pdfTextViewResolveInDays =
                    PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
                pdfTextViewResolveInDays.setText("" + ResolveInDays)
                tableRowView1.addToRow(pdfTextViewResolveInDays)

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

        //        PDFTextView pdfIconLicenseView = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.H3);
//        Spanned icon8Link = HtmlCompat.fromHtml("Icon from <a href='https://icons8.com'>https://icons8.com</a>", HtmlCompat.FROM_HTML_MODE_LEGACY);
//        pdfIconLicenseView.getView().setText(icon8Link);
//        pdfBody.addView(pdfIconLicenseView);
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
//        Intent intentPdfViewer = new Intent(SLAReportPdfActivity.this, PdfViewerActivity.class);
//        intentPdfViewer.putExtra(PdfViewerActivity.PDF_FILE_URI, pdfUri);
//        startActivity(intentPdfViewer);

        if (savedPDFFile == null || !savedPDFFile.exists()) {
            Toast.makeText(this, R.string.text_generated_file_error, Toast.LENGTH_SHORT).show()
            return
        }
        val printAttributeBuilder = PrintAttributes.Builder()
        printAttributeBuilder.setMediaSize(PrintAttributes.MediaSize.ISO_A4)
        printAttributeBuilder.setMinMargins(PrintAttributes.Margins.NO_MARGINS)
        PDFUtil.printPdf(this@SLAReportPdfActivity, savedPDFFile, printAttributeBuilder.build())
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}