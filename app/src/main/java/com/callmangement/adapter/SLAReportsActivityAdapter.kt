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
import com.callmangement.databinding.ItemSlaReportsActivityBinding;
import com.callmangement.model.reports.SLA_Reports_Info;
import com.callmangement.ui.reports.SLAReportsDetailsActivity;

import java.util.List;

public class SLAReportsActivityAdapter extends RecyclerView.Adapter<SLAReportsActivityAdapter.ViewHolder> {
    private final Context context;
    private List<SLA_Reports_Info> slaReportsInfos;
    private Integer intervalDays = 0;

    public SLAReportsActivityAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSlaReportsActivityBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_sla_reports_activity, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        SLA_Reports_Info model = slaReportsInfos.get(position);
        holder.binding.setData(model);

        holder.binding.crdItem.setOnClickListener(view -> view.getContext().startActivity(new Intent(view.getContext(), SLAReportsDetailsActivity.class)
                .putExtra("districtId", model.getDistrictId())
                .putExtra("intervalDays", intervalDays)));

    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<SLA_Reports_Info> monthlyReportsInfos) {
        this.slaReportsInfos = monthlyReportsInfos;
        notifyDataSetChanged();
    }

    public void setDays(Integer intervalDays) {
        this.intervalDays = intervalDays;
    }

    public Integer getDays() {
        return intervalDays;
    }

    @Override
    public int getItemCount() {
        if (slaReportsInfos != null) {
            return slaReportsInfos.size();
        } else {
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemSlaReportsActivityBinding binding;

        public ViewHolder(@NonNull ItemSlaReportsActivityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}