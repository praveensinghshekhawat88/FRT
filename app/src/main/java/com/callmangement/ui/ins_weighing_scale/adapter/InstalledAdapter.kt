package com.callmangement.ui.ins_weighing_scale.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.databinding.ItemInstalledlistlBinding
import com.callmangement.ui.ins_weighing_scale.activity.InstalledDetail
import com.callmangement.ui.ins_weighing_scale.model.Installed.InstalledDatum

class InstalledAdapter(
    private val irisInsDataArrayList: ArrayList<InstalledDatum>,
    private val context: Context
) :
    RecyclerView.Adapter<InstalledAdapter.ViewHolder>() {
    // data is passed into the constructor
    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_installedlistl, parent, false);
        //    return new ViewHolder(view);
        val itemInstalledlistlBinding = ItemInstalledlistlBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemInstalledlistlBinding)
    }

    // binds the data to the TextView in each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = irisInsDataArrayList[position]
        holder.binding.tvDealername.text = model.dealerName
        holder.binding.tvFpscode.text = model.fpscode
        holder.binding.txtBlockName.text = model.blockName
        holder.binding.tvDealermobno.text = model.dealerMobileNo
        holder.binding.tvTicketno.text = model.ticketNo
        holder.binding.tvWeightscaleno.text = model.weighingScaleSerialNo
        /* if (status.equals("Yes") & status.length() != 0) {
            holder.tv_status.setTextColor(Color.parseColor("#43A047"));
            holder.tv_status.setText(status);
        } else if (status.equals("No") & status.length() != 0) {
            holder.tv_status.setTextColor(Color.parseColor("#F44336"));
            holder.tv_status.setText(status);
        } else {
        }*/
        holder.binding.rlItem.setOnClickListener { view: View? ->
            val intent = Intent(context, InstalledDetail::class.java)
            intent.putExtra("param", model)
            context.startActivity(intent)
        }
    }

    // total number of rows
    override fun getItemCount(): Int {
        return irisInsDataArrayList.size
    }

    // stores and recycles views as they are scrolled off screen
    class ViewHolder(val binding: ItemInstalledlistlBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        override fun onClick(view: View) {
        }
    }
}
