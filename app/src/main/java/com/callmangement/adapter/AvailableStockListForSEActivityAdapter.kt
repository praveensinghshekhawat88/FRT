package com.callmangement.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.callmangement.R;
import com.callmangement.databinding.ItemAvailableStockListForSeBinding;
import com.callmangement.model.complaints.ModelComplaintList;
import com.callmangement.model.inventrory.ModelPartsList;

import java.util.ArrayList;
import java.util.List;

public class AvailableStockListForSEActivityAdapter extends RecyclerView.Adapter<AvailableStockListForSEActivityAdapter.ViewHolder> {
    private final Context context;
    private final List<ModelPartsList> modelPartsList;
    private final List<ModelPartsList> modelPartsFilterList;

    public AvailableStockListForSEActivityAdapter(Context context, List<ModelPartsList> modelPartsList) {
        this.context = context;
        this.modelPartsList = modelPartsList;
        this.modelPartsFilterList = new ArrayList<>(modelPartsList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAvailableStockListForSeBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_available_stock_list_for_se, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelPartsList model = modelPartsList.get(position);
        holder.binding.textItemName.setText(model.getItemName());
        holder.binding.textItemQuantity.setText(model.getItem_Qty());
    }

    @Override
    public int getItemCount() {
        return modelPartsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final ItemAvailableStockListForSeBinding binding;
        public ViewHolder(@NonNull ItemAvailableStockListForSeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
