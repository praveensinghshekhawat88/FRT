package com.callmangement.ui.iris_derivery_installation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.callmangement.databinding.ItemIrisInstallationPendingBinding;
import com.callmangement.ui.iris_derivery_installation.Model.IrisInstallationPendingListResp;

import java.util.ArrayList;

public class IrisInstalledPendingAdapter extends RecyclerView.Adapter<IrisInstalledPendingAdapter.MyViewHolder> {
    private final ArrayList<IrisInstallationPendingListResp.Datum> irisInsDataArrayList;
    private final Context context;

    public IrisInstalledPendingAdapter(ArrayList<IrisInstallationPendingListResp.Datum> irisInsDataArrayList, Context context) {
        this.irisInsDataArrayList = irisInsDataArrayList;
        this.context = context;
    }

    // data is passed into the constructor
    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public IrisInstalledPendingAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemIrisInstallationPendingBinding itemIrisInstallationPendingBinding = ItemIrisInstallationPendingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(itemIrisInstallationPendingBinding);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(IrisInstalledPendingAdapter.MyViewHolder holder, int position) {

        IrisInstallationPendingListResp.Datum model = irisInsDataArrayList.get(position);

        holder.binding.tvDealername.setText(model.getDealerName());
        holder.binding.tvFpscode.setText(model.getFpscode());
        holder.binding.tvDealermobno.setText(model.getDealerMobileNo());
        holder.binding.tvTicketno.setText(model.getTicketNo());
        holder.binding.txtDistrict.setText(model.getDistrictName());
        holder.binding.txtBlockName.setText(model.getBlockName());

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return irisInsDataArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ItemIrisInstallationPendingBinding binding;

        public MyViewHolder(ItemIrisInstallationPendingBinding binding) {
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


