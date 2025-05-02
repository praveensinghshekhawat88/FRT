package com.callmangement.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.callmangement.R;
import com.callmangement.databinding.ItemDispatchChallanListActivityBinding;
import com.callmangement.databinding.ItemDispatchChallanListDetailsActivityBinding;
import com.callmangement.model.inventrory.ModelPartsDispatchInvoiceList;
import com.callmangement.ui.inventory.DispatchChallanPartsListDetailsActivity;

import java.util.List;

public class DispatchChallanPartsListDetailsActivityAdapter extends RecyclerView.Adapter<DispatchChallanPartsListDetailsActivityAdapter.ViewHolder> {
    private final Activity context;
    private final List<ModelPartsDispatchInvoiceList> list;

    public DispatchChallanPartsListDetailsActivityAdapter(Activity context, List<ModelPartsDispatchInvoiceList> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDispatchChallanListDetailsActivityBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_dispatch_challan_list_details_activity, parent, false);
        return new ViewHolder(binding);
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ModelPartsDispatchInvoiceList model = list.get(position);
        holder.binding.tvItemName.setText(model.getItemName());
        holder.binding.inputDispatchQty.setText(model.getDispatch_Item_Qty());
        holder.binding.inputReceiveQty.setText(model.getReceived_Item_Qty());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ItemDispatchChallanListDetailsActivityBinding binding;
        public ViewHolder(@NonNull ItemDispatchChallanListDetailsActivityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
