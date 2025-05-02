package com.callmangement.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.callmangement.R;
import com.callmangement.databinding.ItemDialogAddStockListBinding;
import com.callmangement.model.inventrory.ModelAddStock;
import java.util.List;

public class DialogAddStockListAdapter extends RecyclerView.Adapter<DialogAddStockListAdapter.ViewHolder> {
    private final Activity context;
    private final List<ModelAddStock> list;

    public DialogAddStockListAdapter(Activity context, List<ModelAddStock> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDialogAddStockListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_dialog_add_stock_list, parent, false);
        return new ViewHolder(binding);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.binding.tvItemName.setText(list.get(position).getItemName());
        holder.binding.tvQuantity.setText(list.get(position).getQty());
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
        public ItemDialogAddStockListBinding binding;
        public ViewHolder(@NonNull ItemDialogAddStockListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
