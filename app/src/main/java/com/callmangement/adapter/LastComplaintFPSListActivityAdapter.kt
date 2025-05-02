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
import com.callmangement.databinding.ItemLastComplaintFpsListActivityBinding
import com.callmangement.model.fps_wise_complaints.ModelFPSDistTehWiseList
import com.callmangement.ui.complaints_fps_wise.ComplaintsListAccordingToFPSActivity

class LastComplaintFPSListActivityAdapter(
    private val context: Context,
    private val modelFPSDistTehWiseList: List<ModelFPSDistTehWiseList>?
) : RecyclerView.Adapter<LastComplaintFPSListActivityAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemLastComplaintFpsListActivityBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_last_complaint_fps_list_activity,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = modelFPSDistTehWiseList!![position]
        holder.binding.data = model

        if (!model.customerNameEng!!.isEmpty()) {
            holder.binding.textName.text = model.customerNameEng
        }
        if (!model.fpscode!!.isEmpty()) {
            holder.binding.textFpsCode.text = model.fpscode
        }
        if (!model.mobileNo!!.isEmpty()) {
            holder.binding.textMobileNumber.text = model.mobileNo
        }

        if (model.complainStatus.equals("Resolved", ignoreCase = true)) {
            holder.binding.textComplaintStatus.setTextColor(context.resources.getColor(R.color.colorGreenDark))
        } else if (model.complainStatus.equals("NotResolve", ignoreCase = true)) {
            holder.binding.textComplaintStatus.setTextColor(context.resources.getColor(R.color.colorRedDark))
        } else {
            holder.binding.textComplaintStatus.setTextColor(context.resources.getColor(R.color.colorRedDark))
        }

        if (!model.complainRegNo!!.isEmpty() && model.complainRegNo != null) {
            holder.binding.layoutComplainRegNumber.visibility = View.VISIBLE
            holder.binding.textComplaintNumber.text = model.complainRegNo
        } else {
            holder.binding.layoutComplainRegNumber.visibility = View.GONE
        }

        if (!model.complainStatus!!.isEmpty() && model.complainStatus != null) {
            holder.binding.layoutComplainStatus.visibility = View.VISIBLE
            holder.binding.textComplaintStatus.text = model.complainStatus
        } else {
            holder.binding.layoutComplainStatus.visibility = View.GONE
        }

        if (!model.complainDesc!!.isEmpty() && model.complainDesc != null) {
            holder.binding.layoutDescription.visibility = View.VISIBLE
            holder.binding.textComplaintDesc.text = model.complainDesc
        } else {
            holder.binding.layoutDescription.visibility = View.GONE
        }

        if (!model.complainRegDate!!.isEmpty() && model.complainRegDate != null) {
            holder.binding.layoutLastComplainRegDate.visibility = View.VISIBLE
            val separator =
                model.complainRegDate!!.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            val date = separator[0]
            val time = separator[1]
            holder.binding.textLastComplaintRegDate.text = date
        } else {
            holder.binding.layoutLastComplainRegDate.visibility = View.GONE
        }

        holder.binding.crdItem.setOnClickListener { view: View? ->
            context.startActivity(
                Intent(
                    context, ComplaintsListAccordingToFPSActivity::class.java
                ).putExtra("fps_code", model.fpscode)
            )
        }
    }

    override fun getItemCount(): Int {
        return modelFPSDistTehWiseList?.size ?: 0
    }

    class ViewHolder(val binding: ItemLastComplaintFpsListActivityBinding) :
        RecyclerView.ViewHolder(
            binding.root
        )
}
