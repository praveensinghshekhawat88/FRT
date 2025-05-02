package com.callmangement.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.callmangement.R;
import com.callmangement.databinding.ItemViewExpenseActivityBinding;
import com.callmangement.model.expense.ModelExpensesList;
import com.callmangement.ui.reports.ExpenseDetailsActivity;

import java.util.List;

public class ViewExpenseActivityAdapter extends RecyclerView.Adapter<ViewExpenseActivityAdapter.ViewHolder> {
    private final Context context;
    private final List<ModelExpensesList> modelExpensesList;

    public ViewExpenseActivityAdapter(Context context, List<ModelExpensesList> modelExpensesList) {
        this.context = context;
        this.modelExpensesList = modelExpensesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemViewExpenseActivityBinding binding = ItemViewExpenseActivityBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelExpensesList model = modelExpensesList.get(position);
        holder.binding.textName.setText(model.getSeName());
        holder.binding.textDistrictName.setText(model.getDistrict());
        holder.binding.textRemark.setText(model.getRemark());
        holder.binding.textAmount.setText(String.valueOf(model.getTotalExAmount()));
        holder.binding.textExpenseDate.setText(model.getCreatedOnStr());

        if (model.getExpenseStatusID() == 1) {
            holder.binding.textExpenseStatus.setText(model.getExpenseStatus());
            holder.binding.textExpenseStatus.setTextColor(context.getResources().getColor(R.color.colorRedDark));
        } else if (model.getExpenseStatusID() == 2) {
            holder.binding.textExpenseStatus.setText(model.getExpenseStatus());
            holder.binding.textExpenseStatus.setTextColor(context.getResources().getColor(R.color.colorGreenDark));
        }

        if (model.getCompletedOnStr().isEmpty()) {
            holder.binding.expenseCompletedDateLay.setVisibility(View.GONE);
        } else {
            holder.binding.expenseCompletedDateLay.setVisibility(View.VISIBLE);
            holder.binding.textExpenseCompletedDate.setText(model.getCompletedOnStr());
        }

        holder.binding.tvView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ExpenseDetailsActivity.class);
            intent.putExtra("param", model);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return modelExpensesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemViewExpenseActivityBinding binding;
        public ViewHolder(ItemViewExpenseActivityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
