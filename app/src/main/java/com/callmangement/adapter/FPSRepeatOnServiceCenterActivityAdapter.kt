package com.callmangement.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ItemFpsRepeatOnServiceCenterActivityBinding;
import com.callmangement.model.fps_repeat_on_service_center.ModelRepeatFpsComplaintsList;
import com.callmangement.ui.distributor.activity.UploadImageActivity;
import com.callmangement.ui.reports.FPSRepeatOnServiceCenterActivity;

import java.util.List;

public class FPSRepeatOnServiceCenterActivityAdapter extends RecyclerView.Adapter<FPSRepeatOnServiceCenterActivityAdapter.ViewHolder> {
    private final Context context;
    private final List<ModelRepeatFpsComplaintsList> list;

    public FPSRepeatOnServiceCenterActivityAdapter(Context context, List<ModelRepeatFpsComplaintsList> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFpsRepeatOnServiceCenterActivityBinding binding = ItemFpsRepeatOnServiceCenterActivityBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelRepeatFpsComplaintsList model = list.get(position);
        holder.binding.tvFpsCode.setText(model.getFpscode());
        holder.binding.tvDealerName.setText(model.getCustomerName());
        holder.binding.tvDistrict.setText(model.getDistrict());
        holder.binding.tvFpsRepeatOnServiceCenter.setText(""+model.getCntReptOnSerCenter());

        holder.binding.tvView.setOnClickListener(view -> {
            ((FPSRepeatOnServiceCenterActivity)context).fpsRepeatOnServiceCenterView(model);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final ItemFpsRepeatOnServiceCenterActivityBinding binding;
        public ViewHolder(@NonNull ItemFpsRepeatOnServiceCenterActivityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
