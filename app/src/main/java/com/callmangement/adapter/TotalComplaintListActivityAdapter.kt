package com.callmangement.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.R
import com.callmangement.databinding.ItemTotalComplaintListBinding
import com.callmangement.model.complaints.ModelComplaintList
import com.callmangement.ui.complaint.ComplaintDetailActivity
import java.util.Locale

class TotalComplaintListActivityAdapter(private val context: Context) :
    RecyclerView.Adapter<TotalComplaintListActivityAdapter.ViewHolder>(), Filterable {
    private var row_index = 0
    var modelComplaintList: MutableList<ModelComplaintList>? = null
    private var modelComplaintFilterList: MutableList<ModelComplaintList>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemTotalComplaintListBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_total_complaint_list,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val model = modelComplaintList!![position]
        holder.binding.data = model

        holder.binding.textComplaintStatus.text =
            context.resources.getString(R.string.complaint_status) + " : "

        holder.binding.textComplaintNumber.text =
            context.resources.getString(R.string.c_no) + " : " + model.complainRegNo
        holder.binding.textDis.text = model.district
        holder.binding.textName.text =
            context.resources.getString(R.string.name) + " : " + model.customerName + " " + "(" + context.resources.getString(
                R.string.fps_code
            ) + " : " + model.fpscode + ")"
        holder.binding.textPhoneNumber.text =
            context.resources.getString(R.string.mobile_no) + " : " + model.mobileNo

        Log.d("textComplaintStatusValue", "---" + model.complainStatusId)

        if (model.complainStatusId == "1") {
            holder.binding.textComplaintStatusValue.text = model.complainStatus
            holder.binding.textComplaintStatusValue.setTextColor(context.resources.getColor(R.color.colorCopyButton))
        } else if (model.complainStatusId == "2") {
            holder.binding.textComplaintStatusValue.text = model.complainStatus
            holder.binding.textComplaintStatusValue.setTextColor(context.resources.getColor(R.color.colorRedDark))
        } else if (model.complainStatusId == "3") {
            holder.binding.textComplaintStatusValue.text = model.complainStatus
            holder.binding.textComplaintStatusValue.setTextColor(context.resources.getColor(R.color.colorGreenDark))
        } else if (model.complainStatusId == "4") {
            holder.binding.textComplaintStatusValue.text = model.complainStatus
            holder.binding.textComplaintStatusValue.setTextColor(context.resources.getColor(R.color.colorBrown))
        } else if (model.complainStatusId == "6") {
            holder.binding.textComplaintStatusValue.text = model.complainStatus // Default value
            holder.binding.textComplaintStatusValue.setTextColor(context.resources.getColor(R.color.blue_700))
        } else {
            holder.binding.textComplaintStatusValue.text = model.complainStatus // Default value
            holder.binding.textComplaintStatusValue.setTextColor(context.resources.getColor(R.color.deep_purple_300)) // Default color
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

        holder.binding.listItem.setOnClickListener { view: View ->
            row_index = position
            notifyDataSetChanged()
            view.context.startActivity(
                Intent(
                    view.context,
                    ComplaintDetailActivity::class.java
                ).putExtra("param", model).putExtra("param2", "resolved")
            )
        }

        if (row_index == position) {
            holder.binding.listItem.background =
                context.getDrawable(R.drawable.shape_rect_background_dark_blue_layout)
            holder.binding.textName.setTextColor(context.resources.getColor(R.color.white))
            holder.binding.textPhoneNumber.setTextColor(context.resources.getColor(R.color.white))
            holder.binding.textComplaintNumber.setTextColor(context.resources.getColor(R.color.white))
            holder.binding.textTehsil.setTextColor(context.resources.getColor(R.color.white))
            holder.binding.textDis.setTextColor(context.resources.getColor(R.color.white))
            holder.binding.textComplaintRegDate.setTextColor(context.resources.getColor(R.color.white))
            holder.binding.textComplaintResolveDate.setTextColor(context.resources.getColor(R.color.white))
            holder.binding.textComplaintStatus.setTextColor(context.resources.getColor(R.color.white))
        } else {
            holder.binding.listItem.background =
                context.getDrawable(R.drawable.shape_rect_background_white_layout)
            holder.binding.textName.setTextColor(context.resources.getColor(R.color.grayDark))
            holder.binding.textPhoneNumber.setTextColor(context.resources.getColor(R.color.grayDark))
            holder.binding.textComplaintNumber.setTextColor(context.resources.getColor(R.color.colorActionBar))
            holder.binding.textTehsil.setTextColor(context.resources.getColor(R.color.blue_700))
            holder.binding.textComplaintRegDate.setTextColor(context.resources.getColor(R.color.grayDark))
            holder.binding.textComplaintResolveDate.setTextColor(context.resources.getColor(R.color.grayDark))
            holder.binding.textComplaintStatus.setTextColor(context.resources.getColor(R.color.grayDark))
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    fun setData(modelComplaintList: List<ModelComplaintList>?) {
        this.modelComplaintList = ArrayList(modelComplaintList) // correct way
        this.modelComplaintFilterList = ArrayList(modelComplaintList)
        notifyDataSetChanged()
    }

    fun addData(newItems: List<ModelComplaintList>) {
        val startPosition = modelComplaintList!!.size
        modelComplaintList!!.addAll(newItems)
        modelComplaintFilterList!!.addAll(newItems)
        notifyItemRangeInserted(startPosition, newItems.size)
    }


    override fun getItemCount(): Int {
        return if (modelComplaintList != null) {
            modelComplaintList!!.size
        } else {
            0
        }
    }

    class ViewHolder(val binding: ItemTotalComplaintListBinding) : RecyclerView.ViewHolder(
        binding.root
    )


    override fun getFilter(): Filter {
        return Searched_Filter
    }

    private val Searched_Filter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList = ArrayList<ModelComplaintList>()
            if (constraint == null || constraint.length == 0) {
                filteredList.addAll(modelComplaintFilterList!!)
            } else {
                val filterPattern =
                    constraint.toString().lowercase(Locale.getDefault()).trim { it <= ' ' }
                for (item in modelComplaintFilterList!!) {
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

        @SuppressLint("NotifyDataSetChanged")
        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            if (results.values != null) {
                modelComplaintList!!.clear()
                modelComplaintList!!.addAll(results.values as ArrayList<ModelComplaintList>)
                notifyDataSetChanged()
            }
        }
    }
}
