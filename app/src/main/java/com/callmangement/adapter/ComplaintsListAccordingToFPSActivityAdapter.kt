package com.callmangement.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.R
import com.callmangement.databinding.ItemComplaintsListAccordingToFpsActivityBinding
import com.callmangement.model.fps_wise_complaints.ModelFPSComplaintList

class ComplaintsListAccordingToFPSActivityAdapter(
    private val context: Context,
    private val modelFPSComplaintLists: List<ModelFPSComplaintList>?
) :
    RecyclerView.Adapter<ComplaintsListAccordingToFPSActivityAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemComplaintsListAccordingToFpsActivityBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_complaints_list_according_to_fps_activity,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = modelFPSComplaintLists!![position]
        holder.binding.data = model

        holder.binding.textComplaintNumber.text =
            context.resources.getString(R.string.c_no) + " : " + model.complainRegNo
        holder.binding.textName.text =
            model.customerName + " " + "( " + context.resources.getString(R.string.fps_code) + " : " + model.fpscode + " )"
        holder.binding.textPhoneNumber.text = model.mobileNo
        holder.binding.textComplaintDesc.text = model.complainDesc

        Log.d("ComplainStatusId", "---" + model.complainStatusId)

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
            holder.binding.layoutCompRegDate.visibility = View.VISIBLE
            holder.binding.textComplaintRegDate.text = model.complainRegDateStr
        } else {
            holder.binding.layoutCompRegDate.visibility = View.GONE
        }

        if (model.sermarkDateStr != null && !model.sermarkDateStr!!.isEmpty() && !model.sermarkDateStr.equals(
                "null",
                ignoreCase = true
            )
        ) {
            holder.binding.layoutCompResolvedDate.visibility = View.VISIBLE
            if (model.complainStatusId == "3") {
                holder.binding.textResolvedDate.text =
                    context.resources.getString(R.string.resolved_date)
                holder.binding.textComplaintResolveDate.text = model.sermarkDateStr
            } else if (model.complainStatusId == "1") {
                holder.binding.textResolvedDate.text =
                    context.resources.getString(R.string.send_to_se_date)
                holder.binding.textComplaintResolveDate.text = model.sermarkDateStr
            }
        } else {
            holder.binding.layoutCompResolvedDate.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return modelFPSComplaintLists?.size ?: 0
    }

    class ViewHolder(val binding: ItemComplaintsListAccordingToFpsActivityBinding) :
        RecyclerView.ViewHolder(binding.root)
}
