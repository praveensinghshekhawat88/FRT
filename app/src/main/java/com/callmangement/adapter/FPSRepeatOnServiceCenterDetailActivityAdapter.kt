package com.callmangement.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.databinding.ItemFpsRepeatOnServiceCenterDetailActivityBinding
import com.callmangement.model.fps_repeat_on_service_center.ModelRepeatFpsComplaintsList
import com.callmangement.ui.reports.FPSRepeatOnServiceCenterDetailActivity

class FPSRepeatOnServiceCenterDetailActivityAdapter(
    private val context: Context,
    private val list: List<ModelRepeatFpsComplaintsList>
) :
    RecyclerView.Adapter<FPSRepeatOnServiceCenterDetailActivityAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFpsRepeatOnServiceCenterDetailActivityBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]
        holder.binding.tvComplaintNumber.text = model.complainRegNo
        holder.binding.tvFpsCode.text = model.fpscode
        holder.binding.tvDealerName.text = model.customerName
        holder.binding.tvDistrict.text = model.district
        holder.binding.tvDateTime.text = model.isSentToServiceCentreOnStr

        holder.binding.crdItem.setOnClickListener { view: View? ->
            (context as FPSRepeatOnServiceCenterDetailActivity).repeatFpsComplaintDetail(
                model
            )
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(val binding: ItemFpsRepeatOnServiceCenterDetailActivityBinding) :
        RecyclerView.ViewHolder(binding.root)
}
