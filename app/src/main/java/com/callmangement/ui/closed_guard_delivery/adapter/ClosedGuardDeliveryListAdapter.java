package com.callmangement.ui.closed_guard_delivery.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.callmangement.databinding.ItemClosedGuardDeliveryListBinding;
import com.callmangement.databinding.ItemIrisInstalledlistlBinding;
import com.callmangement.ui.closed_guard_delivery.ClosedGuardDetailActivity;
import com.callmangement.ui.closed_guard_delivery.model.ClosedGuardDeliveryListResponse;
import com.callmangement.ui.iris_derivery_installation.IrisInstalledDetailActivity;
import com.callmangement.ui.iris_derivery_installation.Model.IrisDeliveryListResponse;

import java.util.ArrayList;

public class ClosedGuardDeliveryListAdapter extends RecyclerView.Adapter<ClosedGuardDeliveryListAdapter.MyViewHolder> {
    private final ArrayList<ClosedGuardDeliveryListResponse.Datum> irisInsDataArrayList;
    Context context;

    public ClosedGuardDeliveryListAdapter(ArrayList<ClosedGuardDeliveryListResponse.Datum> irisInsDataArrayList, Context context) {
        this.irisInsDataArrayList = irisInsDataArrayList;
        this.context = context;
    }

    // data is passed into the constructor
    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ClosedGuardDeliveryListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemClosedGuardDeliveryListBinding itemClosedGuardDeliveryListBinding = ItemClosedGuardDeliveryListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(itemClosedGuardDeliveryListBinding);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ClosedGuardDeliveryListAdapter.MyViewHolder holder, int position) {

        ClosedGuardDeliveryListResponse.Datum model = irisInsDataArrayList.get(position);

        holder.binding.tvDealername.setText(model.getDealerName());
        holder.binding.tvFpscode.setText(model.getFpscode());
        holder.binding.tvDealermobno.setText(model.getDealerMobileNo());
    //    holder.binding.tvTicketno.setText(model.getTicketNo());
        holder.binding.txtIrisno.setText(model.getSerialNo());
        holder.binding.txtBlockName.setText(model.getBlockName());

        holder.binding.rlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ClosedGuardDetailActivity.class);
                intent.putExtra("param", model);
                context.startActivity(intent);
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return irisInsDataArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ItemClosedGuardDeliveryListBinding binding;

        public MyViewHolder(ItemClosedGuardDeliveryListBinding binding) {
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

