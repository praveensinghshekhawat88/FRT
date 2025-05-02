package com.callmangement.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.callmangement.R;
import com.callmangement.databinding.ItemCreateChallanDispatchBinding;
import com.callmangement.model.inventrory.ModelAvailableStockPartsList;
import com.callmangement.model.inventrory.ModelPartsList;

import java.util.ArrayList;
import java.util.List;

public class CreateNewChallanforDispatchActivityAdapter extends RecyclerView.Adapter<CreateNewChallanforDispatchActivityAdapter.ViewHolder> {
    private final Activity context;
    private final List<ModelPartsList> list;

    public CreateNewChallanforDispatchActivityAdapter(Activity context, List<ModelPartsList> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCreateChallanDispatchBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_create_challan_dispatch, parent, false);
        return new ViewHolder(binding);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.binding.inputProductAvailableQty.setText(list.get(position).getItem_Qty());
        holder.binding.inputProductCount.setText(list.get(position).getQuantity());
        holder.binding.tvItemName.setText(list.get(position).getItemName());

        if (list.get(position).isSelectFlag())
            holder.binding.ivCheckbox.setBackgroundResource(R.drawable.ic_check_box);
        else holder.binding.ivCheckbox.setBackgroundResource(R.drawable.ic_uncheck_box);

        holder.binding.ivCheckbox.setOnClickListener(view -> {
            list.get(position).setSelectFlag(!list.get(position).isSelectFlag());
            notifyDataSetChanged();
        });

        holder.binding.ivMinus.setOnClickListener(view -> {
            String quantity = holder.binding.inputProductCount.getText().toString().trim();
            if (!quantity.isEmpty() && !quantity.equals("0")) {
                int intQuantity = Integer.parseInt(quantity);
                int updatedQuantity = intQuantity - 1;
                list.get(position).setQuantity(String.valueOf(updatedQuantity));
                holder.binding.inputProductCount.setText(list.get(position).getQuantity());
            }
        });

        holder.binding.ivPlus.setOnClickListener(view -> {
            String quantity = holder.binding.inputProductCount.getText().toString().trim();
            int availableQty = Integer.parseInt(holder.binding.inputProductAvailableQty.getText().toString().trim());
            if (!quantity.isEmpty()) {
                int intQuantity = Integer.parseInt(quantity);
                int updatedQuantity = intQuantity + 1;
                if (updatedQuantity > availableQty) {
                    Toast.makeText(context, context.getResources().getString(R.string.message_dispatch_quantity), Toast.LENGTH_SHORT).show();
                } else {
                    list.get(position).setQuantity(String.valueOf(updatedQuantity));
                    holder.binding.inputProductCount.setText(list.get(position).getQuantity());
                }
            }
        });




        holder.binding.inputProductCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    Log.e("position",position+"");
                    String quantity = holder.binding.inputProductCount.getText().toString().trim();
                    list.get(position).setQuantity(quantity);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
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
        public ItemCreateChallanDispatchBinding binding;
        public ViewHolder(@NonNull ItemCreateChallanDispatchBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
