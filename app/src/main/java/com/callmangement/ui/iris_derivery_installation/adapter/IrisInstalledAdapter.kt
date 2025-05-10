package com.callmangement.ui.iris_derivery_installation.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.databinding.ItemIrisInstalledlistlBinding
import com.callmangement.ui.iris_derivery_installation.IrisInstalledDetailActivity
import com.callmangement.ui.iris_derivery_installation.Model.IrisDeliveryListResponse

class IrisInstalledAdapter(
    private val irisInsDataArrayList: ArrayList<IrisDeliveryListResponse.Datum?>,
    var context: Context
) :
    RecyclerView.Adapter<IrisInstalledAdapter.MyViewHolder>() {
    // data is passed into the constructor
    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemIrisInstalledlistlBinding = ItemIrisInstalledlistlBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(itemIrisInstalledlistlBinding)
    }

    // binds the data to the TextView in each row
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = irisInsDataArrayList[position]

        holder.binding.tvDealername.text = model!!.dealerName
        holder.binding.tvFpscode.text = model.fpscode
        holder.binding.tvDealermobno.text = model.dealerMobileNo
        holder.binding.tvTicketno.text = model.ticketNo
        holder.binding.txtIrisno.text = model.serialNo
        holder.binding.txtBlockName.text = model.blockName

        holder.binding.rlItem.setOnClickListener {
            val intent = Intent(context, IrisInstalledDetailActivity::class.java)
            intent.putExtra("param", model)
            context.startActivity(intent)
        }
    }

    // total number of rows
    override fun getItemCount(): Int {
        return irisInsDataArrayList.size
    }

    class MyViewHolder(val binding: ItemIrisInstalledlistlBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            binding.root.setOnClickListener { v: View? -> }
        }

        override fun onClick(v: View) {
        }
    }
}

