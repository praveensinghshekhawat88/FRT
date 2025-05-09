package com.callmangement.ui.closed_guard_delivery.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.databinding.ItemClosedGuardDeliveryListBinding
import com.callmangement.ui.closed_guard_delivery.ClosedGuardDetailActivity
import com.callmangement.ui.closed_guard_delivery.model.ClosedGuardDeliveryListResponse

class ClosedGuardDeliveryListAdapter() :
    RecyclerView.Adapter<ClosedGuardDeliveryListAdapter.MyViewHolder>() {

    private var irisInsDataArrayList: ArrayList<ClosedGuardDeliveryListResponse.Datum?> =
        ArrayList()
    lateinit var context: Context

    constructor(
        irisInsDataArrayList: ArrayList<ClosedGuardDeliveryListResponse.Datum?>,
        context: Context
    ) : this() {
        this.irisInsDataArrayList = irisInsDataArrayList
        this.context = context
    }

    // data is passed into the constructor
    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemClosedGuardDeliveryListBinding = ItemClosedGuardDeliveryListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(itemClosedGuardDeliveryListBinding)
    }

    // binds the data to the TextView in each row
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = irisInsDataArrayList[position]

        holder.binding.tvDealername.text = model!!.dealerName
        holder.binding.tvFpscode.text = model.fpscode
        holder.binding.tvDealermobno.text = model.dealerMobileNo
        //    holder.binding.tvTicketno.setText(model.getTicketNo());
        holder.binding.txtIrisno.text = model.serialNo
        holder.binding.txtBlockName.text = model.blockName

        holder.binding.rlItem.setOnClickListener {
            val intent = Intent(context, ClosedGuardDetailActivity::class.java)
            intent.putExtra("param", model)
            context.startActivity(intent)
        }
    }

    // total number of rows
    override fun getItemCount(): Int {
        return irisInsDataArrayList.size
    }

    inner class MyViewHolder(val binding: ItemClosedGuardDeliveryListBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {

            binding.rlItem.setOnClickListener {

                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val intent = Intent(context, ClosedGuardDetailActivity::class.java)
                    intent.putExtra("param", irisInsDataArrayList[position])
                    context.startActivity(intent)
                }

            }
        }

        override fun onClick(v: View) {
        }
    }
}

