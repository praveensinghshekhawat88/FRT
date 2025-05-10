package com.callmangement.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.R
import com.callmangement.databinding.ItemMonthlyReportsActivityBinding
import com.callmangement.databinding.ItemProgressBarPaginationBinding
import com.callmangement.model.complaints.ModelComplaintList
import com.callmangement.model.reports.MonthReportModel
import com.callmangement.model.reports.Monthly_Reports_Info
import com.callmangement.ui.reports.DailyReportsDetailsActivity
import com.callmangement.utils.DateTimeUtils.getTimeStamp
import com.google.gson.Gson

class MonthlyReportsActivityAdapter(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var list: List<MonthReportModel>? = null
    private var showLoader = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //     ItemMonthlyReportsActivityBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_monthly_reports_activity, parent, false);
        //     return new ViewHolder(binding);
        if (viewType == VIEW_TYPE_LOADING) {
            val binding =
                DataBindingUtil.inflate<ItemProgressBarPaginationBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_progress_bar_pagination,
                    parent,
                    false
                )
            return LoaderViewHolder(binding)
        } else {
            val binding =
                DataBindingUtil.inflate<ItemMonthlyReportsActivityBinding>(
                    LayoutInflater.from(
                        parent.context
                    ), R.layout.item_monthly_reports_activity, parent, false
                )
            return ItemViewHolder(binding)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        if (holder is ItemViewHolder) {
            val resolvedNotResolvedList = getResolvedNotResolvedList(
                list!![position].date!!, list!![position].list
            )
            val model = Monthly_Reports_Info()
            model.date = list!![position].date
            model.resolved = resolvedNotResolvedList[0].size
            model.not_resolved = resolvedNotResolvedList[1].size
            val total = resolvedNotResolvedList[0].size + resolvedNotResolvedList[1].size
            model.total = total
            holder.binding.data = model

            holder.binding.resolvedLay.setOnClickListener { v: View? ->
                val resolvedList = getResolvedNotResolvedList(
                    list!![position].date!!, list!![position].list
                )
                context.startActivity(
                    Intent(context, DailyReportsDetailsActivity::class.java)
                        .putExtra("complain_data", Gson().toJson(resolvedList[0]))
                )
            }

            holder.binding.registerLay.setOnClickListener { v: View? ->
                context.startActivity(
                    Intent(
                        context,
                        DailyReportsDetailsActivity::class.java
                    )
                        .putExtra("complain_data", Gson().toJson(list!![position].list))
                )
            }
        }
    }

    private fun getResolvedNotResolvedList(
        date: String,
        totalList: MutableList<ModelComplaintList>?
    ): List<List<ModelComplaintList>> {
        val resolvedList: MutableList<ModelComplaintList> = ArrayList()
        val notResolvedList: MutableList<ModelComplaintList> = ArrayList()
        val resolveAndNotResolvedList: MutableList<List<ModelComplaintList>> = ArrayList()
        try {
            if (totalList != null) {
                if (totalList.size > 0) {
                    for (model in totalList) {
                        if (model.complainStatusId == "3" && getTimeStamp(date) == getTimeStamp(
                                model.sermarkDateStr!!
                            )
                        ) resolvedList.add(model)
                        else notResolvedList.add(model)
                    }
                }
            }
            resolveAndNotResolvedList.add(resolvedList)
            resolveAndNotResolvedList.add(notResolvedList)
            return resolveAndNotResolvedList
        } catch (e: Exception) {
            e.printStackTrace()
            return resolveAndNotResolvedList
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<MonthReportModel>?) {
        this.list = list
        notifyDataSetChanged()
    }

    //    public void showLoader(boolean show) {
    //        this.showLoader = show;
    //        notifyDataSetChanged();
    //    }
    fun showLoader(show: Boolean) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Handler(Looper.getMainLooper()).post {
                this.showLoader = show
                notifyDataSetChanged()
            }
        } else {
            this.showLoader = show
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return if (list != null) {
            list!!.size + (if (showLoader) 1 else 0)
        } else {
            0
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (list != null && position == list!!.size && showLoader) {
            VIEW_TYPE_LOADING
        } else {
            VIEW_TYPE_ITEM
        }
    }

    class ItemViewHolder(val binding: ItemMonthlyReportsActivityBinding) :
        RecyclerView.ViewHolder(binding.root)

    class LoaderViewHolder(binding: ItemProgressBarPaginationBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_LOADING = 1
    }
}