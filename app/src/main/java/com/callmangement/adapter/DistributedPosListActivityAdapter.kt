package com.callmangement.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.callmangement.databinding.ItemDistributedPosListActivityBinding;
import com.callmangement.ui.distributor.activity.DistributedPosListActivity;
import com.callmangement.ui.distributor.model.PosDistributionDetail;

import java.util.List;

public class DistributedPosListActivityAdapter extends RecyclerView.Adapter<DistributedPosListActivityAdapter.ViewHolder> {
    private final List<PosDistributionDetail> list;
    private final Context context;

    public DistributedPosListActivityAdapter(Context context, List<PosDistributionDetail> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDistributedPosListActivityBinding binding = ItemDistributedPosListActivityBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try{
            holder.binding.tvFpsCode.setText(list.get(position).getFpscode());
            holder.binding.tvDealerName.setText(list.get(position).getDealerName());
            holder.binding.tvDistrict.setText(list.get(position).getDistrictName());

            holder.binding.tvSubmitFormView.setOnClickListener(view -> ((DistributedPosListActivity)context).posDistributionFormView(list.get(position)));

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemDistributedPosListActivityBinding binding;
        public ViewHolder(ItemDistributedPosListActivityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
