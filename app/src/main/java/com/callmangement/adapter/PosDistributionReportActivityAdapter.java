package com.callmangement.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.callmangement.R;
import com.callmangement.databinding.ItemPosDistributionReportActivityBinding;
import com.callmangement.ui.distributor.activity.PosDistributionReportActivity;
import com.callmangement.ui.distributor.model.PosDistributionDetail;

import java.util.List;

public class PosDistributionReportActivityAdapter extends RecyclerView.Adapter<PosDistributionReportActivityAdapter.ViewHolder> {
    private final List<PosDistributionDetail> list;
    private final Context context;

    public PosDistributionReportActivityAdapter(Context context, List<PosDistributionDetail> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPosDistributionReportActivityBinding binding = ItemPosDistributionReportActivityBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try{
            holder.binding.tvFpsCode.setText(list.get(position).getFpscode());
            holder.binding.tvDealerName.setText(list.get(position).getDealerName());
            holder.binding.tvDistrict.setText(list.get(position).getDistrictName());

            if (list.get(position).isFormUploaded().equalsIgnoreCase("true")) {
                holder.binding.tvFormUploadedStatus.setText(context.getResources().getString(R.string.uploaded));
                holder.binding.tvFormUploadedStatus.setTextColor(context.getResources().getColor(R.color.colorGreenDark));
            } else {
                holder.binding.tvFormUploadedStatus.setText(context.getResources().getString(R.string.not_uploaded));
                holder.binding.tvFormUploadedStatus.setTextColor(context.getResources().getColor(R.color.colorRedDark));
            }

            if (list.get(position).isPhotoUploaded().equalsIgnoreCase("true")) {
                holder.binding.tvPhotoUploadedStatus.setText(context.getResources().getString(R.string.uploaded));
                holder.binding.tvPhotoUploadedStatus.setTextColor(context.getResources().getColor(R.color.colorGreenDark));
            } else {
                holder.binding.tvPhotoUploadedStatus.setText(context.getResources().getString(R.string.not_uploaded));
                holder.binding.tvPhotoUploadedStatus.setTextColor(context.getResources().getColor(R.color.colorRedDark));
            }

            holder.binding.tvSubmitFormView.setOnClickListener(view -> {
                ((PosDistributionReportActivity)context).posDistributionFormView(list.get(position));
            });

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemPosDistributionReportActivityBinding binding;
        public ViewHolder(ItemPosDistributionReportActivityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
