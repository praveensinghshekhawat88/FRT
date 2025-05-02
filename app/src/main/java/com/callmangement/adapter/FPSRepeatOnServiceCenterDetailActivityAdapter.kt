package com.callmangement.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.callmangement.databinding.ItemFpsRepeatOnServiceCenterDetailActivityBinding;
import com.callmangement.model.fps_repeat_on_service_center.ModelRepeatFpsComplaintsList;
import com.callmangement.ui.reports.FPSRepeatOnServiceCenterDetailActivity;

import java.util.List;

public class FPSRepeatOnServiceCenterDetailActivityAdapter extends RecyclerView.Adapter<FPSRepeatOnServiceCenterDetailActivityAdapter.ViewHolder> {
    private final Context context;
    private final List<ModelRepeatFpsComplaintsList> list;

    public FPSRepeatOnServiceCenterDetailActivityAdapter(Context context, List<ModelRepeatFpsComplaintsList> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFpsRepeatOnServiceCenterDetailActivityBinding binding = ItemFpsRepeatOnServiceCenterDetailActivityBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelRepeatFpsComplaintsList model = list.get(position);
        holder.binding.tvComplaintNumber.setText(model.getComplainRegNo());
        holder.binding.tvFpsCode.setText(model.getFpscode());
        holder.binding.tvDealerName.setText(model.getCustomerName());
        holder.binding.tvDistrict.setText(model.getDistrict());
        holder.binding.tvDateTime.setText(model.getIsSentToServiceCentreOnStr());

        holder.binding.crdItem.setOnClickListener(view -> ((FPSRepeatOnServiceCenterDetailActivity)context).repeatFpsComplaintDetail(model));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final ItemFpsRepeatOnServiceCenterDetailActivityBinding binding;
        public ViewHolder(@NonNull ItemFpsRepeatOnServiceCenterDetailActivityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
