package com.callmangement.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.databinding.ItemDistributedPosListActivityBinding
import com.callmangement.ui.distributor.activity.DistributedPosListActivity
import com.callmangement.ui.distributor.model.PosDistributionDetail

class DistributedPosListActivityAdapter(
    private val context: Context,
    private val list: List<PosDistributionDetail>
) :
    RecyclerView.Adapter<DistributedPosListActivityAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDistributedPosListActivityBinding.inflate(
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

            holder.binding.tvSubmitFormView.setOnClickListener { view: View? ->
                (context as DistributedPosListActivity).posDistributionFormView(
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

    class ViewHolder(val binding: ItemDistributedPosListActivityBinding) :
        RecyclerView.ViewHolder(binding.root)
}
