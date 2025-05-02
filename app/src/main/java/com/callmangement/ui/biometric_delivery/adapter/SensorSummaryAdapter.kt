package com.callmangement.ui.biometric_delivery.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.callmangement.databinding.ItemSensorSummaryBinding;
import com.callmangement.ui.biometric_delivery.model.SensorSummaryResponse;
import com.callmangement.utils.PrefManager;
import java.util.ArrayList;

public class SensorSummaryAdapter extends RecyclerView.Adapter<SensorSummaryAdapter.MyViewHolder> {
    private final ArrayList<SensorSummaryResponse.Data> sensorSummaryList;

    public SensorSummaryAdapter(ArrayList<SensorSummaryResponse.Data> weighInsDataArrayList) {
        this.sensorSummaryList = weighInsDataArrayList;
    }

    @Override
    public SensorSummaryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ItemSensorSummaryBinding itemSensorSummaryBinding = ItemSensorSummaryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SensorSummaryAdapter.MyViewHolder(itemSensorSummaryBinding);
    }

    @Override
    public void onBindViewHolder(SensorSummaryAdapter.MyViewHolder holder, int position) {

        SensorSummaryResponse.Data model = sensorSummaryList.get(position);;

        holder.binding.txtDistrictName.setText(model.getDistrict());
        holder.binding.txtTotalDistributed.setText(model.getTotal_Distributed_BiometricSensor());
        holder.binding.txtTotalMapped.setText(model.getTotal_Mapped_BiometricSensor());
        holder.binding.txttotalL0Machine.setText(model.getTotal_L0_Machine());
        holder.binding.txtPending.setText(model.getTotal_Pending());

    }

    @Override
    public int getItemCount() {
        return sensorSummaryList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ItemSensorSummaryBinding binding;

        public MyViewHolder(ItemSensorSummaryBinding binding) {
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
