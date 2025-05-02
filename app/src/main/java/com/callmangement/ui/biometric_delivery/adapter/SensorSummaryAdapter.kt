package com.callmangement.ui.biometric_delivery.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.databinding.ItemSensorSummaryBinding
import com.callmangement.ui.biometric_delivery.model.SensorSummaryResponse

class SensorSummaryAdapter(private val sensorSummaryList: ArrayList<SensorSummaryResponse.Data>) :
    RecyclerView.Adapter<SensorSummaryAdapter.MyViewHolder?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemSensorSummaryBinding =
            ItemSensorSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemSensorSummaryBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = sensorSummaryList[position]



        holder.binding.txtDistrictName.text = model.district
        holder.binding.txtTotalDistributed.text = model.total_Distributed_BiometricSensor
        holder.binding.txtTotalMapped.text = model.total_Mapped_BiometricSensor
        holder.binding.txttotalL0Machine.text = model.total_L0_Machine
        holder.binding.txtPending.text = model.total_Pending
    }

    override fun getItemCount(): Int {
        return sensorSummaryList.size
    }

    class MyViewHolder(val binding: ItemSensorSummaryBinding) : RecyclerView.ViewHolder(
        binding.root
    ), View.OnClickListener {
        init {
            binding.root.setOnClickListener { v: View? -> }
        }

        override fun onClick(v: View) {
        }
    }
}
