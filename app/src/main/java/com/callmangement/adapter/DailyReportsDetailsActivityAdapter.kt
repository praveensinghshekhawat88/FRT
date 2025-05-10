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
import com.callmangement.databinding.ItemDailyReportsDetailsActivityBinding
import com.callmangement.model.complaints.ModelComplaintList
import java.util.Locale

class DailyReportsDetailsActivityAdapter(
    private val context: Context,
    list: MutableList<ModelComplaintList>?
) :
    RecyclerView.Adapter<DailyReportsDetailsActivityAdapter.ViewHolder>(),
    Filterable {
    private val list: List<ModelComplaintList>? = list
    private val modelComplaintFilterList: List<ModelComplaintList> =
        ArrayList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemDailyReportsDetailsActivityBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_daily_reports_details_activity,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list!![position]
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

        if (model.complainStatusId == "1") {
            holder.binding.textComplaintStatusValue.text = model.complainStatus
            holder.binding.textComplaintStatusValue.setTextColor(context.resources.getColor(R.color.colorCopyButton))
        } else if (model.complainStatusId == "2") {
            holder.binding.textComplaintStatusValue.text = model.complainStatus
            holder.binding.textComplaintStatusValue.setTextColor(context.resources.getColor(R.color.colorRedDark))
        } else if (model.complainStatusId == "3") {
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
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    class ViewHolder(val binding: ItemDailyReportsDetailsActivityBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getFilter(): Filter {
        return Searched_Filter
    }

    private val Searched_Filter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList = ArrayList<ModelComplaintList>()
            if (constraint == null || constraint.length == 0) {
                filteredList.addAll(modelComplaintFilterList)
            } else {
                val filterPattern =
                    constraint.toString().lowercase(Locale.getDefault()).trim { it <= ' ' }
                for (item in modelComplaintFilterList) {
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
                list!!.clear()
                list.addAll(results.values as Collection<ModelComplaintList>)
                notifyDataSetChanged()
            }
        }
    }
}
