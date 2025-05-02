package com.callmangement.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.R
import com.callmangement.databinding.ItemPosDistributionReportActivityBinding
import com.callmangement.ui.distributor.activity.PosDistributionReportActivity
import com.callmangement.ui.distributor.model.PosDistributionDetail

class PosDistributionReportActivityAdapter(
    private val context: Context,
    private val list: List<PosDistributionDetail>
) : RecyclerView.Adapter<PosDistributionReportActivityAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPosDistributionReportActivityBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        try {
            holder.binding.tvFpsCode.text = list[position].fpscode
            holder.binding.tvDealerName.text = list[position].dealerName
            holder.binding.tvDistrict.text = list[position].districtName

            if (list[position].isFormUploaded.equals("true", ignoreCase = true)) {
                holder.binding.tvFormUploadedStatus.text =
                    context.resources.getString(R.string.uploaded)
                holder.binding.tvFormUploadedStatus.setTextColor(context.resources.getColor(R.color.colorGreenDark))
            } else {
                holder.binding.tvFormUploadedStatus.text =
                    context.resources.getString(R.string.not_uploaded)
                holder.binding.tvFormUploadedStatus.setTextColor(context.resources.getColor(R.color.colorRedDark))
            }

            if (list[position].isPhotoUploaded.equals("true", ignoreCase = true)) {
                holder.binding.tvPhotoUploadedStatus.text =
                    context.resources.getString(R.string.uploaded)
                holder.binding.tvPhotoUploadedStatus.setTextColor(context.resources.getColor(R.color.colorGreenDark))
            } else {
                holder.binding.tvPhotoUploadedStatus.text =
                    context.resources.getString(R.string.not_uploaded)
                holder.binding.tvPhotoUploadedStatus.setTextColor(context.resources.getColor(R.color.colorRedDark))
            }

            holder.binding.tvSubmitFormView.setOnClickListener { view: View? ->
                (context as PosDistributionReportActivity).posDistributionFormView(
                    list[position]
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(val binding: ItemPosDistributionReportActivityBinding) :
        RecyclerView.ViewHolder(
            binding.root
        )
}
