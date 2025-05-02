package com.callmangement.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.callmangement.R;
import com.callmangement.databinding.ItemTrainingScheduleListForSeActivityBinding;
import com.callmangement.model.training_schedule.ModelTrainingScheduleList;
import com.callmangement.ui.training_schedule.TrainingScheduleFormActivity;

import java.util.List;

public class TrainingScheduleListForSEActivityAdapter extends RecyclerView.Adapter<TrainingScheduleListForSEActivityAdapter.ViewHolder> {
    private final Context context;
    private List<ModelTrainingScheduleList> list;

    public TrainingScheduleListForSEActivityAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTrainingScheduleListForSeActivityBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_training_schedule_list_for_se_activity, parent, false);
        return new ViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelTrainingScheduleList model = list.get(position);
        holder.binding.setData(model);
        holder.binding.crdItem.setOnClickListener(view ->
            context.startActivity(new Intent(context, TrainingScheduleFormActivity.class)));

    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<ModelTrainingScheduleList> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (list!=null){
            return list.size();
        }else {
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final ItemTrainingScheduleListForSeActivityBinding binding;
        public ViewHolder(@NonNull ItemTrainingScheduleListForSeActivityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
