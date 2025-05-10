package com.callmangement.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.databinding.ItemFpsRepeatOnServiceCenterActivityBinding
import com.callmangement.model.fps_repeat_on_service_center.ModelRepeatFpsComplaintsList
import com.callmangement.ui.reports.FPSRepeatOnServiceCenterActivity

class FPSRepeatOnServiceCenterActivityAdapter(
    private val context: Context,
    private val list: List<ModelRepeatFpsComplaintsList>
) :
    RecyclerView.Adapter<FPSRepeatOnServiceCenterActivityAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFpsRepeatOnServiceCenterActivityBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]
        holder.binding.tvFpsCode.text = model.fpscode
        holder.binding.tvDealerName.text = model.customerName
        holder.binding.tvDistrict.text = model.district
        holder.binding.tvFpsRepeatOnServiceCenter.text = "" + model.cntReptOnSerCenter

        holder.binding.tvView.setOnClickListener { view: View? ->
            (context as FPSRepeatOnServiceCenterActivity).fpsRepeatOnServiceCenterView(model)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(val binding: ItemFpsRepeatOnServiceCenterActivityBinding) :
        RecyclerView.ViewHolder(binding.root)
}
