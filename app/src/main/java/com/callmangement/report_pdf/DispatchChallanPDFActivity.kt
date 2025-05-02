package com.callmangement.report_pdf

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
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
import androidx.core.content.FileProvider
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
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.Locale

class DispatchChallanPDFActivity : PDFCreatorActivity() {
    private var invoiceId: String? = ""
    private var dispatchFrom: String? = ""
    private var email: String? = ""
    private var dispatchTo: String? = ""
    private var username: String? = ""
    private var datetime: String? = ""
    private var courierName: String? = ""
    private var courierTrackingNo: String? = ""
    private var dispatchedTotalQty = 0
    private var receivedTotalQty = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }
        invoiceId = intent.getStringExtra("invoiceId")
        dispatchFrom = intent.getStringExtra("dispatchFrom")
        email = intent.getStringExtra("email")
        dispatchTo = intent.getStringExtra("dispatchTo")
        username = intent.getStringExtra("username")
        datetime = intent.getStringExtra("datetime")
        courierName = intent.getStringExtra("courierName")
        courierTrackingNo = intent.getStringExtra("courierTrackingNo")

        createPDF("" + System.currentTimeMillis(), object : PDFUtilListener {
            override fun pdfGenerationSuccess(savedPDFFile: File) {
                copyAssets(savedPDFFile)
                //                Toast.makeText(ReportPdfActivity.this, "PDF Created", Toast.LENGTH_SHORT).show();
            }

            override fun pdfGenerationFailure(exception: Exception) {
                Toast.makeText(
                    this@DispatchChallanPDFActivity,
                    "PDF NOT Created",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun copyAssets(savedPDFFile: File) {
        val filename = "challan.pdf"
        var `in`: InputStream? = null
        var out: OutputStream? = null
        try {
            `in` = FileInputStream(savedPDFFile)
            val outFile = File(getExternalFilesDir(null), filename)
            out = FileOutputStream(outFile)
            copyFile(`in`, out)
        } catch (e: IOException) {
            //  Log.e("tag", "Failed to copy file: " + savedPDFFile.getName(), e);
        } finally {
            if (`in` != null) {
                try {
                    `in`.close()
                } catch (e: IOException) {
                    // NOOP
                }
            }
            if (out != null) {
                try {
                    out.close()
                } catch (e: IOException) {
                    // NOOP
                }
            }
        }

        val outputFile = File(getExternalFilesDir(null), filename)
        val uri = FileProvider.getUriForFile(
            this@DispatchChallanPDFActivity,
            this@DispatchChallanPDFActivity.packageName + ".provider",
            outputFile
        )

        val share = Intent()
        share.setAction(Intent.ACTION_SEND)
        share.setType("application/*")
        share.putExtra(Intent.EXTRA_STREAM, uri)
        share.setPackage("com.whatsapp")
        try {
            startActivity(share)
        } catch (ex: ActivityNotFoundException) {
            ex.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun copyFile(`in`: InputStream, out: OutputStream) {
        val buffer = ByteArray(1024)
        var read: Int
        while ((`in`.read(buffer).also { read = it }) != -1) {
            out.write(buffer, 0, read)
        }
    }

    override fun getHeaderView(pageIndex: Int): PDFHeaderView {
        val headerView = PDFHeaderView(applicationContext)

        val horizontalView = PDFHorizontalView(applicationContext)

        val pdfTextView = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.HEADER)
        val word = SpannableString("CHALLAN")
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
        if (!courierTrackingNo!!.isEmpty() && !courierName!!.isEmpty()) {
            pdfCompanyNameView.setText("Invoice Id                           :          $invoiceId\nCourier Tracking No.        :          $courierTrackingNo\nCourier Name                    :          $courierName\nDispatchFrom                   :          $dispatchFrom\nEmail                                  :          $email\nDispatchTo                        :          $dispatchTo\nUserName                          :          $username\nDispatch Date                   :          $datetime")
        } else {
            pdfCompanyNameView.setText("Invoice Id                           :          $invoiceId\nDispatchFrom                   :          $dispatchFrom\nEmail                                  :          $email\nDispatchTo                        :          $dispatchTo\nUserName                          :          $username\nDispatch Date                   :          $datetime")
        }

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

        val widthPercent = intArrayOf(20, 20, 20, 20, 20)
        val textInTable = arrayOf("SrNo", "Item Name", "Dispatched Qty.", "Received Qty.")

        val tableHeader = PDFTableRowView(applicationContext)
        for (s in textInTable) {
            val pdfTextView = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.H3)
            pdfTextView.setText(s)
            pdfTextView.view.setTypeface(pdfTextView.view.typeface, Typeface.BOLD)
            pdfTextView.view.setPaddingRelative(0, 5, 0, 5)
            tableHeader.addToRow(pdfTextView)
        }

        if (Constants.modelPartsDispatchInvoiceList != null && Constants.modelPartsDispatchInvoiceList!!.size > 0) {
            dispatchedTotalQty = 0
            receivedTotalQty = 0

            var tableRowView1 = PDFTableRowView(applicationContext)
            tableRowView1.view.setPaddingRelative(0, 5, 0, 0)

            var modelPartsDispatchInvoiceList = Constants.modelPartsDispatchInvoiceList!![0]

            dispatchedTotalQty = modelPartsDispatchInvoiceList.dispatch_Item_Qty!!.toInt()
            receivedTotalQty = modelPartsDispatchInvoiceList.received_Item_Qty!!.toInt()

            var pdfTextViewSrNo = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
            pdfTextViewSrNo.setText("1")
            tableRowView1.addToRow(pdfTextViewSrNo)

            var pdfTextViewItemName = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
            pdfTextViewItemName.setText("" + modelPartsDispatchInvoiceList.itemName)
            tableRowView1.addToRow(pdfTextViewItemName)

            var pdfTextViewDispatchedQuantity =
                PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
            pdfTextViewDispatchedQuantity.setText("" + modelPartsDispatchInvoiceList.dispatch_Item_Qty)
            tableRowView1.addToRow(pdfTextViewDispatchedQuantity)

            var pdfTextViewReceivedQuantity =
                PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
            pdfTextViewReceivedQuantity.setText("" + modelPartsDispatchInvoiceList.received_Item_Qty)
            tableRowView1.addToRow(pdfTextViewReceivedQuantity)


            val tableView = PDFTableView(applicationContext, tableHeader, tableRowView1)

            for (i in 1 until Constants.modelPartsDispatchInvoiceList!!.size) {
                modelPartsDispatchInvoiceList = Constants.modelPartsDispatchInvoiceList!![i]

                dispatchedTotalQty += modelPartsDispatchInvoiceList.dispatch_Item_Qty!!.toInt()
                receivedTotalQty += modelPartsDispatchInvoiceList.received_Item_Qty!!.toInt()

                val srNo = i + 1

                tableRowView1 = PDFTableRowView(applicationContext)
                pdfTextViewSrNo = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
                pdfTextViewSrNo.setText("" + srNo)
                tableRowView1.addToRow(pdfTextViewSrNo)

                pdfTextViewItemName = PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
                pdfTextViewItemName.setText("" + modelPartsDispatchInvoiceList.itemName)
                tableRowView1.addToRow(pdfTextViewItemName)

                pdfTextViewDispatchedQuantity =
                    PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
                pdfTextViewDispatchedQuantity.setText("" + modelPartsDispatchInvoiceList.dispatch_Item_Qty)
                tableRowView1.addToRow(pdfTextViewDispatchedQuantity)

                pdfTextViewReceivedQuantity =
                    PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.P)
                pdfTextViewReceivedQuantity.setText("" + modelPartsDispatchInvoiceList.received_Item_Qty)
                tableRowView1.addToRow(pdfTextViewReceivedQuantity)

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

        val pdfTextViewDispatchedQuantity =
            PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.H3)
        pdfTextViewDispatchedQuantity.setText("" + dispatchedTotalQty)
        pdfTextViewDispatchedQuantity.view.setTypeface(
            pdfTextViewDispatchedQuantity.view.typeface,
            Typeface.BOLD
        )
        tableRowViewBottom.addToRow(pdfTextViewDispatchedQuantity)

        val pdfTextViewReceivedQuantity =
            PDFTextView(applicationContext, PDFTextView.PDF_TEXT_SIZE.H3)
        pdfTextViewReceivedQuantity.setText("" + receivedTotalQty)
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

        //        PDFTextView pdfTextViewPage1 = new PDFTextView(getApplicationContext(), PDFTextView.PDF_TEXT_SIZE.SMALL);
        pdfTextViewPage.setText(String.format(Locale.getDefault(), "Page: %d", pageIndex + 1))
        pdfTextViewPage.setLayout(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 0f
            )
        )
        pdfTextViewPage.view.gravity = Gravity.CENTER_HORIZONTAL

        /*pdfTextViewPage1.setText(""+ DateTimeUtils.getCurrentTime());
        pdfTextViewPage1.setLayout(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 0));
        pdfTextViewPage1.getView().setGravity(Gravity.LEFT);*/
        footerView.addView(pdfTextViewPage)

        //        footerView.addView(pdfTextViewPage1);
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

    override fun onNextClicked(savedPDFFile: File) {
//        Uri pdfUri = Uri.fromFile(savedPDFFile);
//        Intent intentPdfViewer = new Intent(DispatchChallanPDFActivity.this, PdfViewerActivity.class);
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
            this@DispatchChallanPDFActivity,
            savedPDFFile,
            printAttributeBuilder.build()
        )
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
