package com.callmangement.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.R
import com.callmangement.databinding.ItemDailyReportsActivityBinding
import com.callmangement.model.complaints.ModelComplaintList
import com.callmangement.model.reports.Monthly_Reports_Info
import com.callmangement.ui.reports.DailyReportsDetailsActivity
import com.google.gson.Gson
import java.util.StringTokenizer

class DailyReportsActivityAdapter(private val context: Context) :
    RecyclerView.Adapter<DailyReportsActivityAdapter.ViewHolder>() {
    private val row_index = 0
    private var list: List<List<ModelComplaintList>>? = null
    private var modelComplaintLists: List<ModelComplaintList>? = null
    private var date: String? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setData(
        list: List<List<ModelComplaintList>>?,
        modelComplaintLists: List<ModelComplaintList>?,
        date: String?
    ) {
        this.list = list
        this.modelComplaintLists = modelComplaintLists
        this.date = date
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemDailyReportsActivityBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_daily_reports_activity,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        if (list != null) {
            val resolvedCount = list!![position].size
            val notResolvedPosition = position + 1
            val notResolvedCount = list!![notResolvedPosition].size
            val grandTotal = resolvedCount + notResolvedCount
            val modelData = Monthly_Reports_Info()
            modelData.resolved = resolvedCount
            modelData.not_resolved = notResolvedCount
            modelData.total = grandTotal
            modelData.date = formattedFilterDate(date)
            holder.binding.data = modelData
        }

        holder.binding.resolvedLay.setOnClickListener { v: View? ->
            context.startActivity(
                Intent(
                    context,
                    DailyReportsDetailsActivity::class.java
                )
                    .putExtra("complain_data", Gson().toJson(list!![position]))
            )
        }
        holder.binding.registerLay.setOnClickListener { v: View? ->
            context.startActivity(
                Intent(
                    context,
                    DailyReportsDetailsActivity::class.java
                )
                    .putExtra("complain_data", Gson().toJson(modelComplaintLists))
            )
        }
    }

    private fun formattedFilterDate(datestr: String?): String {
        val tokenizer = StringTokenizer(datestr, "-")
        val year = tokenizer.nextToken()
        val month = tokenizer.nextToken()
        val date = tokenizer.nextToken()
        return "$date-$month-$year"
    }

    override fun getItemCount(): Int {
        return 1
    }


    class ViewHolder(val binding: ItemDailyReportsActivityBinding) :
        RecyclerView.ViewHolder(binding.root)
}
