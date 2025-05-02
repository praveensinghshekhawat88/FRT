package com.callmangement.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.callmangement.R;
import com.callmangement.databinding.ItemDisputeStockNoReceiveBinding;
import com.callmangement.model.inventrory.ModelDisputePartsList;
import com.callmangement.model.inventrory.ModelPartsDispatchInvoiceList;

import java.util.List;

public class DisputeStockNoReceiveAdapter extends RecyclerView.Adapter<DisputeStockNoReceiveAdapter.ViewHolder> {
    private final Activity context;
    private final List<ModelDisputePartsList> modelDisputePartsList;

    public DisputeStockNoReceiveAdapter(Activity context, List<ModelDisputePartsList> modelDisputePartsList) {
        this.context = context;
        this.modelDisputePartsList = modelDisputePartsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDisputeStockNoReceiveBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_dispute_stock_no_receive, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelDisputePartsList model = modelDisputePartsList.get(position);
        holder.binding.tvItemName.setText(model.getItemName());
        holder.binding.inputQuantity.setText(model.getItem_Qty());
    }

    @Override
    public int getItemCount() {
        return modelDisputePartsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final ItemDisputeStockNoReceiveBinding binding;
        public ViewHolder(@NonNull ItemDisputeStockNoReceiveBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
