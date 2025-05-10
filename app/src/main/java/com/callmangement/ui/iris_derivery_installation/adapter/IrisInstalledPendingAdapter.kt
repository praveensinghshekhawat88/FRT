package com.callmangement.ui.iris_derivery_installation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.databinding.ItemIrisInstallationPendingBinding
import com.callmangement.ui.iris_derivery_installation.Model.IrisInstallationPendingListResp

class IrisInstalledPendingAdapter(
    private val irisInsDataArrayList: ArrayList<IrisInstallationPendingListResp.Datum>,
    private val context: Context
) :
    RecyclerView.Adapter<IrisInstalledPendingAdapter.MyViewHolder>() {
    // data is passed into the constructor
    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemIrisInstallationPendingBinding = ItemIrisInstallationPendingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(itemIrisInstallationPendingBinding)
    }

    // binds the data to the TextView in each row
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = irisInsDataArrayList[position]

        holder.binding.tvDealername.text = model.dealerName
        holder.binding.tvFpscode.text = model.fpscode
        holder.binding.tvDealermobno.text = model.dealerMobileNo
        holder.binding.tvTicketno.text = model.ticketNo
        holder.binding.txtDistrict.text = model.districtName
        holder.binding.txtBlockName.text = model.blockName
    }

    // total number of rows
    override fun getItemCount(): Int {
        return irisInsDataArrayList.size
    }

    class MyViewHolder(val binding: ItemIrisInstallationPendingBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            binding.root.setOnClickListener { v: View? -> }
        }

        override fun onClick(v: View) {
        }
    }
}


