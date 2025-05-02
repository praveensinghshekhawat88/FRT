package com.callmangement.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.callmangement.R;
import com.callmangement.databinding.ItemDailyReportsActivityBinding;
import com.callmangement.model.complaints.ModelComplaintList;
import com.callmangement.model.reports.Monthly_Reports_Info;
import com.callmangement.ui.reports.DailyReportsDetailsActivity;
import com.google.gson.Gson;

import java.util.List;
import java.util.StringTokenizer;

public class DailyReportsActivityAdapter extends RecyclerView.Adapter<DailyReportsActivityAdapter.ViewHolder>{
    private final Context context;
    private final int row_index = 0;
    private List<List<ModelComplaintList>> list;
    private List<ModelComplaintList> modelComplaintLists;
    private String date;

    public DailyReportsActivityAdapter(Context context) {
        this.context = context;
    }
    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<List<ModelComplaintList>> list,List<ModelComplaintList> modelComplaintLists, String date){
        this.list = list;
        this.modelComplaintLists = modelComplaintLists;
        this.date = date;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDailyReportsActivityBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_daily_reports_activity, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        if (list != null) {
            int resolvedCount = list.get(position).size();
            int notResolvedPosition = position + 1;
            int notResolvedCount = list.get(notResolvedPosition).size();
            int grandTotal = resolvedCount + notResolvedCount;
            Monthly_Reports_Info modelData = new Monthly_Reports_Info();
            modelData.setResolved(resolvedCount);
            modelData.setNot_resolved(notResolvedCount);
            modelData.setTotal(grandTotal);
            modelData.setDate(formattedFilterDate(date));
            holder.binding.setData(modelData);
        }

        holder.binding.resolvedLay.setOnClickListener(v -> context.startActivity(new Intent(context, DailyReportsDetailsActivity.class)
                .putExtra("complain_data",new Gson().toJson(list.get(position)))));
        holder.binding.registerLay.setOnClickListener(v -> {
            context.startActivity(new Intent(context, DailyReportsDetailsActivity.class)
                    .putExtra("complain_data",new Gson().toJson(modelComplaintLists)));
        });
    }

    private String formattedFilterDate(String datestr){
        StringTokenizer tokenizer = new StringTokenizer(datestr,"-");
        String year = tokenizer.nextToken();
        String month = tokenizer.nextToken();
        String date = tokenizer.nextToken();
        return date+"-"+month+"-"+year;
    }

    @Override
    public int getItemCount() {
        return 1;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final ItemDailyReportsActivityBinding binding;
        public ViewHolder(@NonNull ItemDailyReportsActivityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
