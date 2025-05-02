package com.callmangement.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.callmangement.R;
import com.callmangement.databinding.ItemDialogChallanForDispatchBinding;
import com.callmangement.model.inventrory.ModelDisputePartsList;
import com.callmangement.model.inventrory.ModelPartsList;

import java.util.List;

public class DialogChallanForDisputeDispatchAdapter extends RecyclerView.Adapter<DialogChallanForDisputeDispatchAdapter.ViewHolder> {
    private final Activity context;
    private final List<ModelDisputePartsList> list;

    public DialogChallanForDisputeDispatchAdapter(Activity context, List<ModelDisputePartsList> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDialogChallanForDispatchBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_dialog_challan_for_dispatch, parent, false);
        return new ViewHolder(binding);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.binding.tvItemName.setText(list.get(position).getItemName());
        holder.binding.tvProductCount.setText(list.get(position).getItem_Qty());
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
        public ItemDialogChallanForDispatchBinding binding;
        public ViewHolder(@NonNull ItemDialogChallanForDispatchBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
