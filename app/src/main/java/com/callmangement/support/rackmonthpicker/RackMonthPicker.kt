package com.callmangement.support.rackmonthpicker

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.R
import com.callmangement.support.rackmonthpicker.MonthAdapter.OnSelectedListener
import com.callmangement.support.rackmonthpicker.listener.DateMonthDialogListener
import com.callmangement.support.rackmonthpicker.listener.OnCancelMonthDialogListener
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Created by kristiawan on 31/12/16.
 */
class RackMonthPicker {
    private var mAlertDialog: AlertDialog? = null
    private var builder: Builder
    private var context: Context
    private var mPositiveButton: Button? = null
    private var mNegativeButton: Button? = null
    private var dateMonthDialogListener: DateMonthDialogListener? = null
    private var onCancelMonthDialogListener: OnCancelMonthDialogListener? = null
    private var isBuild = false

    constructor(context: Context) {
        this.context = context
        builder = Builder()
    }

    constructor(activity: Activity) {
        this.context = activity
        builder = Builder()
    }

    fun show() {
        if (isBuild) {
            mAlertDialog!!.show()
            builder.setDefault()
        } else {
            builder.build()
            isBuild = true
        }
    }

    /**
     * set action callback when positive button clicked
     *
     * @param dateMonthDialogListener
     * @return
     */
    fun setPositiveButton(dateMonthDialogListener: DateMonthDialogListener?): RackMonthPicker {
        this.dateMonthDialogListener = dateMonthDialogListener
        mPositiveButton!!.setOnClickListener(builder.positiveButtonClick())
        return this
    }

    /**
     * set action callback when negative button clicked
     *
     * @param onCancelMonthDialogListener
     * @return
     */
    fun setNegativeButton(onCancelMonthDialogListener: OnCancelMonthDialogListener?): RackMonthPicker {
        this.onCancelMonthDialogListener = onCancelMonthDialogListener
        mNegativeButton!!.setOnClickListener(builder.negativeButtonClick())
        return this
    }

    /**
     * change text positive button
     *
     * @param text
     * @return
     */
    fun setPositiveText(text: String?): RackMonthPicker {
        mPositiveButton!!.text = text
        return this
    }

    /**
     * change text negative button
     *
     * @param text
     * @return
     */
    fun setNegativeText(text: String?): RackMonthPicker {
        mNegativeButton!!.text = text
        return this
    }

    /**
     * set localization show month
     *
     * @param locale
     * @return
     */
    fun setLocale(locale: Locale?): RackMonthPicker {
        builder.setLocale(locale)
        return this
    }

    /**
     * change default selected month (1 - 12)
     *
     * @param month
     * @return
     */
    fun setSelectedMonth(month: Int): RackMonthPicker {
        builder.setSelectedMonth(month)
        return this
    }

    /**
     * change default selected year
     *
     * @param year
     * @return
     */
    fun setSelectedYear(year: Int): RackMonthPicker {
        builder.setSelectedYear(year)
        return this
    }

    /**
     * change color theme
     *
     * @param color
     * @return
     */
    fun setColorTheme(color: Int): RackMonthPicker {
        builder.setColorTheme(color)
        return this
    }

    fun setMonthType(monthType: MonthType): RackMonthPicker {
        builder.setMonthType(monthType)
        return this
    }

    fun dismiss() {
        mAlertDialog!!.dismiss()
    }

    private inner class Builder : OnSelectedListener {
        private val monthAdapter: MonthAdapter
        private val mTitleView: TextView
        private val mYear: TextView
        private var year: Int
        private var month: Int
        private val alertBuilder =
            AlertDialog.Builder(context)
        private val contentView: View =
            LayoutInflater.from(context).inflate(R.layout.dialog_month_picker, null)

        init {
            contentView.isFocusable = true
            contentView.isFocusableInTouchMode = true

            mTitleView = contentView.findViewById<View>(R.id.title) as TextView
            mYear = contentView.findViewById<View>(R.id.text_year) as TextView

            val next = contentView.findViewById<View>(R.id.btn_next) as ImageView
            next.setOnClickListener(nextButtonClick())

            val previous = contentView.findViewById<View>(R.id.btn_previous) as ImageView
            previous.setOnClickListener(previousButtonClick())

            mPositiveButton = contentView.findViewById<View>(R.id.btn_p) as Button
            mNegativeButton = contentView.findViewById<View>(R.id.btn_n) as Button

            monthAdapter = MonthAdapter(context, this)

            val recyclerView = contentView.findViewById<View>(R.id.recycler_view) as RecyclerView
            recyclerView.layoutManager = GridLayoutManager(context, 4)
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = monthAdapter

            val date = Date()
            val cal = Calendar.getInstance()
            cal.time = date
            year = cal[Calendar.YEAR]
            month = cal[Calendar.MONTH]

            setColorTheme(
                getColorByThemeAttr(
                    context,
                    android.R.attr.colorPrimary,
                    R.color.color_primary
                )
            )
        }

        fun getColorByThemeAttr(context: Context, attr: Int, defaultColor: Int): Int {
            val typedValue = TypedValue()
            val theme = context.theme
            val got = theme.resolveAttribute(attr, typedValue, true)
            return if (got) typedValue.data else defaultColor
        }

        //set default config
        fun setDefault() {
            val date = Date()
            val cal = Calendar.getInstance()
            cal.time = date
            year = cal[Calendar.YEAR]
            month = cal[Calendar.MONTH]

            monthAdapter.setSelectedItem(month)
            mTitleView.text = monthAdapter.shortMonth + ", " + year
            monthAdapter.notifyDataSetChanged()
            mYear.text = year.toString() + ""
        }

        fun setLocale(locale: Locale?) {
            monthAdapter.setLocale(locale)
        }

        fun setSelectedMonth(index: Int) {
            monthAdapter.setSelectedItem(index)
            mTitleView.text = monthAdapter.shortMonth + ", " + year
        }

        fun setSelectedYear(year: Int) {
            this.year = year
            mYear.text = year.toString() + ""
            mTitleView.text = monthAdapter.shortMonth + ", " + year
        }

        fun setColorTheme(color: Int) {
            val linearToolbar = contentView.findViewById<View>(R.id.linear_toolbar) as LinearLayout
            linearToolbar.setBackgroundColor(color)

            monthAdapter.setBackgroundMonth(color)
            mPositiveButton!!.setTextColor(color)
            mNegativeButton!!.setTextColor(color)
        }

        fun setMonthType(monthType: MonthType) {
            monthAdapter.setMonthType(monthType)
        }

        fun build() {
            monthAdapter.setSelectedItem(month)
            mTitleView.text = monthAdapter.shortMonth + ", " + year
            mYear.text = year.toString() + ""

            mAlertDialog = alertBuilder.create()
            mAlertDialog!!.show()
            mAlertDialog!!.window!!.clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
            )
            mAlertDialog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MASK_STATE)
            mAlertDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            mAlertDialog!!.window!!.setBackgroundDrawableResource(R.drawable.material_dialog_window)
            mAlertDialog!!.window!!.setContentView(contentView)
        }

        fun nextButtonClick(): View.OnClickListener {
            return View.OnClickListener {
                year++
                mYear.text = year.toString() + ""
                mTitleView.text = monthAdapter.shortMonth + ", " + year
            }
        }

        fun previousButtonClick(): View.OnClickListener {
            return View.OnClickListener {
                year--
                mYear.text = year.toString() + ""
                mTitleView.text = monthAdapter.shortMonth + ", " + year
            }
        }

        fun positiveButtonClick(): View.OnClickListener {
            return View.OnClickListener {
                dateMonthDialogListener!!.onDateMonth(
                    monthAdapter.month,
                    monthAdapter.startDate,
                    monthAdapter.endDate,
                    year, mTitleView.text.toString()
                )
                mAlertDialog!!.dismiss()
            }
        }

        fun negativeButtonClick(): View.OnClickListener {
            return View.OnClickListener {
                onCancelMonthDialogListener!!.onCancel(
                    mAlertDialog
                )
            }
        }

        override fun onContentSelected() {
            mTitleView.text = monthAdapter.shortMonth + ", " + year
        }
    }
}
