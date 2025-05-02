package com.callmangement.ui.ins_weighing_scale.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.callmangement.databinding.ItemInstalledlistlBinding;
import com.callmangement.ui.ins_weighing_scale.activity.InstalledDetail;
import com.callmangement.ui.ins_weighing_scale.model.Installed.InstalledDatum;

import java.util.ArrayList;

public class InstalledAdapter extends RecyclerView.Adapter<InstalledAdapter.ViewHolder> {
    private final ArrayList<InstalledDatum> irisInsDataArrayList;
    private final Context context;

    public InstalledAdapter(ArrayList<InstalledDatum> irisInsDataArrayList, Context context) {
        this.irisInsDataArrayList = irisInsDataArrayList;
        this.context = context;
    }

    // data is passed into the constructor
    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    //    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_installedlistl, parent, false);
    //    return new ViewHolder(view);
        ItemInstalledlistlBinding itemInstalledlistlBinding = ItemInstalledlistlBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemInstalledlistlBinding);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        InstalledDatum model = irisInsDataArrayList.get(position);
        holder.binding.tvDealername.setText(model.getDealerName());
        holder.binding.tvFpscode.setText(model.getFpscode());
        holder.binding.txtBlockName.setText(model.getBlockName());
        holder.binding.tvDealermobno.setText(model.getDealerMobileNo());
        holder.binding.tvTicketno.setText(model.getTicketNo());
        holder.binding.tvWeightscaleno.setText(model.getWeighingScaleSerialNo());
       /* if (status.equals("Yes") & status.length() != 0) {
            holder.tv_status.setTextColor(Color.parseColor("#43A047"));
            holder.tv_status.setText(status);
        } else if (status.equals("No") & status.length() != 0) {
            holder.tv_status.setTextColor(Color.parseColor("#F44336"));
            holder.tv_status.setText(status);
        } else {
        }*/
        holder.binding.rlItem.setOnClickListener(view -> {
            Intent intent = new Intent(context, InstalledDetail.class);
            intent.putExtra("param", model);
            context.startActivity(intent);
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return irisInsDataArrayList.size();
    }

    // stores and recycles views as they are scrolled off screen
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ItemInstalledlistlBinding binding;
        public ViewHolder(ItemInstalledlistlBinding binding) {

            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        public void onClick(View view) {
        }
    }
}
