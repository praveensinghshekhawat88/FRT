package com.callmangement.ui.distributor.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.databinding.ItemUploadImageActivityBinding
import com.callmangement.ui.distributor.activity.UploadImageActivity
import com.callmangement.ui.distributor.model.PosDistributionDetail

class UploadImageActivityAdapter(
    private val context: Context,
    private val list: List<PosDistributionDetail>
) :
    RecyclerView.Adapter<UploadImageActivityAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUploadImageActivityBinding.inflate(
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

            if (list[position].isFormUploaded.equals("true", ignoreCase = true)
            ) holder.binding.tvSubmitForm.visibility =
                View.GONE
            else holder.binding.tvSubmitForm.visibility = View.VISIBLE

            if (list[position].isPhotoUploaded.equals("true", ignoreCase = true)
            ) holder.binding.tvUpload.visibility =
                View.GONE
            else holder.binding.tvUpload.visibility = View.VISIBLE

            holder.binding.tvUpload.setOnClickListener { view: View? ->
                (context as UploadImageActivity).uploadImage(
                    list[position].fpscode,
                    list[position].tranId.toString(), list[position].districtId.toString(), "1"
                )
            }

            holder.binding.tvSubmitForm.setOnClickListener { view: View? ->
                (context as UploadImageActivity).uploadImage(
                    list[position].fpscode,
                    list[position].tranId.toString(), list[position].districtId.toString(), "2"
                )
            }

            holder.binding.tvSubmitFormView.setOnClickListener { view: View? ->
                (context as UploadImageActivity).posDistributionFormView(
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

    class ViewHolder(val binding: ItemUploadImageActivityBinding) :
        RecyclerView.ViewHolder(binding.root)
}
