package com.callmangement.ui.biometric_delivery.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.callmangement.databinding.ItemSensorDistributedListBinding;
import com.callmangement.ui.biometric_delivery.model.SensorDistributionDetailsListResp;

import java.util.ArrayList;

public class SensorDistributionListAdapter extends RecyclerView.Adapter<SensorDistributionListAdapter.MyViewHolder> {
    private final ArrayList<SensorDistributionDetailsListResp.Data> sensorDistributionList;

    public SensorDistributionListAdapter(ArrayList<SensorDistributionDetailsListResp.Data> weighInsDataArrayList) {
        this.sensorDistributionList = weighInsDataArrayList;
    }

    @Override
    public SensorDistributionListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ItemSensorDistributedListBinding itemSensorDistributedListBinding = ItemSensorDistributedListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SensorDistributionListAdapter.MyViewHolder(itemSensorDistributedListBinding);
    }

    @Override
    public void onBindViewHolder(SensorDistributionListAdapter.MyViewHolder holder, int position) {

        SensorDistributionDetailsListResp.Data model = sensorDistributionList.get(position);;

        holder.binding.txtDistrictName.setText(model.getDistrict());
        holder.binding.txtFpsCode.setText(model.getFpscode());
    //    holder.binding.txtMappedDate.setText(model.getBiometrictMappedOnStr());
        holder.binding.txtDeviceCode.setText(model.getDeviceCode());
        holder.binding.txtStatus.setText(model.getBiometricSensorStatus());

    }

    @Override
    public int getItemCount() {
        return sensorDistributionList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ItemSensorDistributedListBinding binding;

        public MyViewHolder(ItemSensorDistributedListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(v -> {
            });
        }

        @Override
        public void onClick(View v) {
        }
    }

}
