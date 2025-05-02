package com.callmangement.ui.biometric_delivery.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.databinding.ItemSensorDistributedListBinding
import com.callmangement.ui.biometric_delivery.model.SensorDistributionDetailsListResp

class SensorDistributionListAdapter(private val sensorDistributionList: ArrayList<SensorDistributionDetailsListResp.Data>) :
    RecyclerView.Adapter<SensorDistributionListAdapter.MyViewHolder?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemSensorDistributedListBinding = ItemSensorDistributedListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(itemSensorDistributedListBinding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = sensorDistributionList[position]



        holder.binding.txtDistrictName.text = model.district
        holder.binding.txtFpsCode.text = model.fpscode
        //    holder.binding.txtMappedDate.setText(model.getBiometrictMappedOnStr());
        holder.binding.txtDeviceCode.text = model.deviceCode
        holder.binding.txtStatus.text = model.biometricSensorStatus
    }

    override fun getItemCount(): Int {
        return sensorDistributionList.size
    }

    class MyViewHolder(val binding: ItemSensorDistributedListBinding) : RecyclerView.ViewHolder(
        binding.root
    ), View.OnClickListener {
        init {
            binding.root.setOnClickListener { v: View? -> }
        }

        override fun onClick(v: View) {
        }
    }
}
