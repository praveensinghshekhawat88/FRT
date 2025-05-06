package com.callmangement.support.rackmonthpicker

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.StateListDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.R
import com.callmangement.support.rackmonthpicker.MonthAdapter.MonthHolder
import java.text.DateFormatSymbols
import java.util.Calendar
import java.util.Locale

/**
 * Created by kristiawan on 9/8/2017.
 */
class MonthAdapter(private val context: Context, private val listener: OnSelectedListener) :
    RecyclerView.Adapter<MonthHolder>() {
    private var months: Array<String>
    private var selectedItem = -1
    private var color = 0
    private var monthType: MonthType

    init {
        months = DateFormatSymbols(Locale.ENGLISH).shortMonths
        monthType = MonthType.TEXT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthHolder {
        val monthHolder = MonthHolder(
            LayoutInflater.from(context).inflate(R.layout.item_view_month, parent, false)
        )
        return monthHolder
    }

    override fun onBindViewHolder(holder: MonthHolder, position: Int) {
        if (monthType == MonthType.NUMBER) {
            holder.textViewMonth.text = (position + 1).toString() + ""
        } else {
            holder.textViewMonth.text = months[position]
        }

        //        holder.textViewMonth.setTextColor(selectedItem == position ? Color.WHITE : Color.BLACK);
        holder.itemView.isSelected = if (selectedItem == position) true else false
    }

    override fun getItemCount(): Int {
        return months.size
    }

    /**
     * change format by localization
     *
     * @param locale
     */
    fun setLocale(locale: Locale?) {
        months = DateFormatSymbols(locale).shortMonths
        notifyDataSetChanged()
    }

    fun setMonthType(monthType: MonthType) {
        this.monthType = monthType
    }

    fun setSelectedItem(index: Int) {
        if (index < 12 || index > -1) {
            selectedItem = index
            notifyItemChanged(selectedItem)
        }
    }

    fun setBackgroundMonth(color: Int) {
        this.color = color
    }

    fun setColor(color: Int) {
        this.color = color
        notifyDataSetChanged()
    }

    val month: Int
        get() = selectedItem + 1

    val startDate: Int
        get() = 1

    val endDate: Int
        get() {
            val cal = Calendar.getInstance()
            cal[Calendar.MONTH] = selectedItem
            cal[Calendar.DAY_OF_MONTH] = selectedItem + 1
            val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
            return maxDay
        }

    val shortMonth: String
        get() = if (monthType == MonthType.NUMBER) {
            (selectedItem + 1).toString() + ""
        } else {
            months[selectedItem]
        }

    inner class MonthHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var layoutMain: LinearLayout = itemView.findViewById(R.id.main_layout)
        var textViewMonth: TextView = itemView.findViewById(R.id.text_month)

        init {
            if (color != 0) setMonthBackgroundSelected(color)

            //            itemView.setClickable(true);
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
//            notifyItemChanged(selectedItem);
            selectedItem = adapterPosition
            //            notifyItemChanged(selectedItem);
            notifyDataSetChanged()
            listener.onContentSelected()
        }

        private fun setMonthBackgroundSelected(color: Int) {
            val layerDrawable =
                ContextCompat.getDrawable(context, R.drawable.month_selected) as LayerDrawable?
            val gradientDrawable = layerDrawable!!.getDrawable(1) as GradientDrawable
            gradientDrawable.setColor(color)
            layerDrawable.setDrawableByLayerId(1, gradientDrawable)

            val states = StateListDrawable()
            states.addState(intArrayOf(android.R.attr.state_selected), gradientDrawable)
            states.addState(intArrayOf(android.R.attr.state_pressed), gradientDrawable)
            states.addState(
                intArrayOf(),
                ContextCompat.getDrawable(context, R.drawable.month_default)
            )
            layoutMain.background = states
        }
    }

    interface OnSelectedListener {
        fun onContentSelected()
    }
}
