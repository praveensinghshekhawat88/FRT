package com.callmangement.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.callmangement.R;
import com.callmangement.databinding.ItemSeAvailableStockManagerBinding;
import com.callmangement.databinding.ItemTotalStockListForManagerBinding;
import com.callmangement.model.inventrory.ModelPartsList;

import java.util.ArrayList;
import java.util.List;

public class SEAvailableStockManagerActivityAdapter extends RecyclerView.Adapter<SEAvailableStockManagerActivityAdapter.ViewHolder> {
    private final Context context;
    private final List<ModelPartsList> modelPartsList;
    private final List<ModelPartsList> modelPartsFilterList;

    public SEAvailableStockManagerActivityAdapter(Context context, List<ModelPartsList> modelPartsList) {
        this.context = context;
        this.modelPartsList = modelPartsList;
        this.modelPartsFilterList = new ArrayList<>(modelPartsList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSeAvailableStockManagerBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_se_available_stock_manager, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelPartsList model = modelPartsList.get(position);
        holder.binding.textDistrictName.setText(model.getDistrictName());
        holder.binding.textUsername.setText(model.getSename());
        holder.binding.textItemName.setText(model.getItemName());
        holder.binding.textItemQuantity.setText(model.getItem_Qty());
    }

    @Override
    public int getItemCount() {
        return modelPartsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final ItemSeAvailableStockManagerBinding binding;
        public ViewHolder(@NonNull ItemSeAvailableStockManagerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
