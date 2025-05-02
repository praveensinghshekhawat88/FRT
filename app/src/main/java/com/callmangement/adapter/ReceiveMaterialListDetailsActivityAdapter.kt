package com.callmangement.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.callmangement.R;
import com.callmangement.databinding.ItemMaterialListDetailBinding;
import com.callmangement.ui.model.inventory.receive_invoice_parts.PartsDispatchInvoice;

import java.util.List;

public class ReceiveMaterialListDetailsActivityAdapter extends RecyclerView.Adapter<ReceiveMaterialListDetailsActivityAdapter.ViewHolder> {
    private final Context context;
    private final List<PartsDispatchInvoice> list;

    public ReceiveMaterialListDetailsActivityAdapter(Context context, List<PartsDispatchInvoice> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMaterialListDetailBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_material_list_detail, parent, false);
        return new ViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PartsDispatchInvoice model = list.get(position);
        holder.binding.tvReceiveItemName.setText(list.get(position).getItemName() /* + "\nDispatch Qty. - "+list.get(position).getDispatchItemQty()*/);
        holder.binding.textDispatchQty.setText(String.valueOf(list.get(position).getDispatchItemQty()));
        list.get(position).setQuantity(String.valueOf(list.get(position).getDispatchItemQty()));

        if (list.get(position).getIsReceived().equals(true)){
            holder.binding.ivMinus.setEnabled(false);
            holder.binding.ivPlus.setEnabled(false);
            holder.binding.etReceiveItemQuanity.setText(String.valueOf(list.get(position).getReceivedItemQty()));

        } else if (list.get(position).getIsReceived().equals(false)){
            holder.binding.ivMinus.setEnabled(false);
            holder.binding.ivPlus.setEnabled(false);
            holder.binding.etReceiveItemQuanity.setText(String.valueOf(list.get(position).getDispatchItemQty()));
        }

        holder.binding.receiveItemCheckbox.setOnCheckedChangeListener((compoundButton, checked) -> {
            list.get(position).setSelectFlag(checked);
        });

        holder.binding.ivMinus.setOnClickListener(view -> {
            String quantity = holder.binding.etReceiveItemQuanity.getText().toString().trim();
            if (!quantity.isEmpty() && !quantity.equals("0")) {
                int intQuanity = Integer.parseInt(quantity);
                int updatedQuanity = intQuanity - 1;
                list.get(position).setQuantity(String.valueOf(updatedQuanity));
                holder.binding.etReceiveItemQuanity.setText(list.get(position).getQuanity());
            }
        });

        holder.binding.ivPlus.setOnClickListener(view -> {
            String quantity = holder.binding.etReceiveItemQuanity.getText().toString().trim();
            if (!quantity.isEmpty()) {
                int intQuantity = Integer.parseInt(quantity);
                if (intQuantity < list.get(position).getDispatchItemQty()){
                    int updatedQuantity = intQuantity + 1;
                    list.get(position).setQuantity(String.valueOf(updatedQuantity));
                    holder.binding.etReceiveItemQuanity.setText(list.get(position).getQuanity());
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.message_received_quantity), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemMaterialListDetailBinding binding;

        public ViewHolder(@NonNull ItemMaterialListDetailBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
