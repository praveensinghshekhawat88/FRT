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
import com.callmangement.databinding.ItemTrainingScheduleListActivityBinding;
import com.callmangement.model.training_schedule.ModelTrainingScheduleList;
import com.callmangement.ui.training_schedule.UpdateTrainingScheduleActivity;
import java.util.List;

public class TrainingScheduleListActivityAdapter extends RecyclerView.Adapter<TrainingScheduleListActivityAdapter.ViewHolder> {
    private final Context context;
    private List<ModelTrainingScheduleList> list;

    public TrainingScheduleListActivityAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTrainingScheduleListActivityBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_training_schedule_list_activity, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelTrainingScheduleList model = list.get(position);
        holder.binding.setData(model);
        holder.binding.crdItem.setOnClickListener(view -> context.startActivity(new Intent(context, UpdateTrainingScheduleActivity.class).putExtra("param", model)));

    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<ModelTrainingScheduleList> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (list != null){
            return list.size();
        }else {
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final ItemTrainingScheduleListActivityBinding binding;
        public ViewHolder(@NonNull ItemTrainingScheduleListActivityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
