package com.callmangement.custom_spinner_with_checkbox;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.callmangement.R;
import com.callmangement.databinding.MultiSelectItemBinding;
import com.callmangement.model.inventrory.ModelPartsList;

import java.util.ArrayList;
import java.util.List;

public class MutliSelectDistributionPartsAdapter extends RecyclerView.Adapter<MutliSelectDistributionPartsAdapter.ViewHolder> {
    private Activity context;
    private final List<ModelPartsList> list;

    public MutliSelectDistributionPartsAdapter(Activity context, List<ModelPartsList> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MultiSelectItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.multi_select_item, parent, false);
        return new ViewHolder(binding);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.binding.dialogItemName.setText(list.get(position).getItemName());
        Log.d("listtttt", "" + list.get(position).getItemName());

        if (list.get(position).isSelectFlag())
            holder.binding.ivCheckBox.setBackgroundResource(R.drawable.ic_checkbox_select);
        else
            holder.binding.ivCheckBox.setBackgroundResource(R.drawable.ic_checkbox_unselect);


        holder.itemView.setOnClickListener(view -> {
            if (list.get(position).isSelectFlag()) {
                holder.binding.ivCheckBox.setBackgroundResource(R.drawable.ic_checkbox_unselect);
                list.get(position).setSelectFlag(false);
                Log.d("getItemName", " " + list.get(position).getItemName());
            } else {
                holder.binding.ivCheckBox.setBackgroundResource(R.drawable.ic_checkbox_select);
                list.get(position).setSelectFlag(true);
                Log.d("getItemName", " " + list.get(position).getItemName());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        } else {
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final MultiSelectItemBinding binding;

        public ViewHolder(@NonNull MultiSelectItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
