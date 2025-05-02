package com.callmangement.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.R
import com.callmangement.databinding.ItemSlaReportsDetailsActivityBinding
import com.callmangement.model.reports.ModelSLAReportDetailsList
import com.callmangement.utils.PrefManager
import java.util.Locale

class SLAReportsDetailsActivityAdapter(private val context: Context) :
    RecyclerView.Adapter<SLAReportsDetailsActivityAdapter.ViewHolder>(), Filterable {
    private var modelComplaintList: MutableList<ModelSLAReportDetailsList>? = null
    private var filterModelComplaintList: List<ModelSLAReportDetailsList>? = null
    private var prefManager: PrefManager? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemSlaReportsDetailsActivityBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_sla_reports_details_activity,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        prefManager = PrefManager(context)
        val model = modelComplaintList!![position]
        holder.binding.data = model

        holder.binding.textComplaintStatus.text =
            context.resources.getString(R.string.current_status) + " : "

        holder.binding.textComplaintNumber.text =
            context.resources.getString(R.string.c_no) + " : " + model.complainRegNo
        holder.binding.textName.text =
            context.resources.getString(R.string.name) + " : " + model.customerName + " " + "(" + context.resources.getString(
                R.string.fps_code
            ) + " : " + model.fpscode + ")"
        holder.binding.textPhoneNumber.text =
            context.resources.getString(R.string.mobile_no) + " : " + model.mobileNo

        if (model.complainStatusId != null && model.complainStatusId == "1") {
            holder.binding.textComplaintStatusValue.text = model.complainStatus
            holder.binding.textComplaintStatusValue.setTextColor(context.resources.getColor(R.color.colorCopyButton))
        } else if (model.complainStatusId != null && model.complainStatusId == "2") {
            holder.binding.textComplaintStatusValue.text = model.complainStatus
            holder.binding.textComplaintStatusValue.setTextColor(context.resources.getColor(R.color.colorRedDark))
        } else if (model.complainStatusId != null && model.complainStatusId == "3") {
            holder.binding.textComplaintStatusValue.text = model.complainStatus
            holder.binding.textComplaintStatusValue.setTextColor(context.resources.getColor(R.color.colorGreenDark))
        } else {
            holder.binding.textComplaintStatusValue.text = model.complainStatus
            holder.binding.textComplaintStatusValue.setTextColor(context.resources.getColor(R.color.deep_purple_300))
        }

        if (model.complainRegDateStr != null && !model.complainRegDateStr!!.isEmpty() && !model.complainRegDateStr.equals(
                "null",
                ignoreCase = true
            )
        ) {
            holder.binding.textComplaintRegDate.visibility = View.VISIBLE
            holder.binding.textComplaintRegDate.text =
                context.resources.getString(R.string.reg_date) + " : " + model.complainRegDateStr
        } else {
            holder.binding.textComplaintRegDate.visibility = View.GONE
        }

        if (model.sermarkDateStr != null && !model.sermarkDateStr!!.isEmpty() && !model.sermarkDateStr.equals(
                "null",
                ignoreCase = true
            )
        ) {
            holder.binding.textComplaintResolveDate.visibility = View.VISIBLE
            if (model.complainStatusId == "3") {
                holder.binding.textComplaintResolveDate.text =
                    context.resources.getString(R.string.resolved_date) + " : " + model.sermarkDateStr
            } else if (model.complainStatusId == "1") {
                holder.binding.textComplaintResolveDate.text =
                    context.resources.getString(R.string.send_to_se_date) + " : " + model.sermarkDateStr
            }
        } else {
            holder.binding.textComplaintResolveDate.visibility = View.GONE
        }

        if (prefManager!!.uSER_TYPE_ID.equals(
                "6",
                ignoreCase = true
            ) && prefManager!!.uSER_TYPE.equals("DSO", ignoreCase = true)
        ) {
            holder.binding.textResoleDays.visibility = View.GONE
        } else {
            holder.binding.textResoleDays.visibility = View.VISIBLE
            holder.binding.textResoleDays.text = model.resolveTime.toString() + " Days"
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(modelComplaintList: MutableList<ModelSLAReportDetailsList>?) {
        this.modelComplaintList = modelComplaintList
        this.filterModelComplaintList = ArrayList(modelComplaintList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (modelComplaintList != null) {
            modelComplaintList!!.size
        } else {
            0
        }
    }

    class ViewHolder(val binding: ItemSlaReportsDetailsActivityBinding) : RecyclerView.ViewHolder(
        binding.root
    )

    override fun getFilter(): Filter {
        return Searched_Filter
    }

    private val Searched_Filter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList = ArrayList<ModelSLAReportDetailsList>()
            if (constraint == null || constraint.length == 0) {
                filteredList.addAll(filterModelComplaintList!!)
            } else {
                val filterPattern =
                    constraint.toString().lowercase(Locale.getDefault()).trim { it <= ' ' }
                for (item in filterModelComplaintList!!) {
                    if (item.complainRegNo!!.lowercase(Locale.getDefault())
                            .contains(filterPattern) || item.customerName!!.lowercase(
                            Locale.getDefault()
                        ).contains(filterPattern) || item.fpscode!!.lowercase(
                            Locale.getDefault()
                        ).contains(filterPattern)
                    ) {
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            if (results.values != null) {
                modelComplaintList!!.clear()
                modelComplaintList!!.addAll(results.values as ArrayList<ModelSLAReportDetailsList>)
                notifyDataSetChanged()
            }
        }
    }
}
