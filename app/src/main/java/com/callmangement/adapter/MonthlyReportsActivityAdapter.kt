package com.callmangement.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.callmangement.R;
import com.callmangement.databinding.ItemMonthlyReportsActivityBinding;
import com.callmangement.databinding.ItemProgressBarPaginationBinding;
import com.callmangement.model.complaints.ModelComplaintList;
import com.callmangement.model.reports.MonthReportModel;
import com.callmangement.model.reports.Monthly_Reports_Info;
import com.callmangement.ui.reports.DailyReportsDetailsActivity;
import com.callmangement.utils.DateTimeUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MonthlyReportsActivityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private List<MonthReportModel> list;
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;
    private boolean showLoader = false;

    public MonthlyReportsActivityAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //     ItemMonthlyReportsActivityBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_monthly_reports_activity, parent, false);
        //     return new ViewHolder(binding);
        if (viewType == VIEW_TYPE_LOADING) {
            ItemProgressBarPaginationBinding binding =
                    DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_progress_bar_pagination, parent, false);
            return new LoaderViewHolder(binding);
        } else {
            ItemMonthlyReportsActivityBinding binding =
                    DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_monthly_reports_activity, parent, false);
            return new ItemViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if (holder instanceof ItemViewHolder) {

            List<List<ModelComplaintList>> resolvedNotResolvedList = getResolvedNotResolvedList(list.get(position).getDate(), list.get(position).getList());
            Monthly_Reports_Info model = new Monthly_Reports_Info();
            model.setDate(list.get(position).getDate());
            model.setResolved(resolvedNotResolvedList.get(0).size());
            model.setNot_resolved(resolvedNotResolvedList.get(1).size());
            int total = resolvedNotResolvedList.get(0).size() + resolvedNotResolvedList.get(1).size();
            model.setTotal(total);
            ((ItemViewHolder) holder).binding.setData(model);

            ((ItemViewHolder) holder).binding.resolvedLay.setOnClickListener(v -> {
                List<List<ModelComplaintList>> resolvedList = getResolvedNotResolvedList(list.get(position).getDate(), list.get(position).getList());
                context.startActivity(new Intent(context, DailyReportsDetailsActivity.class)
                        .putExtra("complain_data", new Gson().toJson(resolvedList.get(0))));
            });

            ((ItemViewHolder) holder).binding.registerLay.setOnClickListener(v -> {
                context.startActivity(new Intent(context, DailyReportsDetailsActivity.class)
                        .putExtra("complain_data", new Gson().toJson(list.get(position).getList())));
            });
        }
    }

    private List<List<ModelComplaintList>> getResolvedNotResolvedList(String date, List<ModelComplaintList> totalList) {
        List<ModelComplaintList> resolvedList = new ArrayList<>();
        List<ModelComplaintList> notResolvedList = new ArrayList<>();
        List<List<ModelComplaintList>> resolveAndNotResolvedList = new ArrayList<>();
        try {
            if (totalList != null) {
                if (totalList.size() > 0) {
                    for (ModelComplaintList model : totalList) {
                        if (model.getComplainStatusId().equals("3") && DateTimeUtils.getTimeStamp(date) == DateTimeUtils.getTimeStamp(model.getSermarkDateStr()))
                            resolvedList.add(model);
                        else notResolvedList.add(model);
                    }
                }
            }
            resolveAndNotResolvedList.add(resolvedList);
            resolveAndNotResolvedList.add(notResolvedList);
            return resolveAndNotResolvedList;
        } catch (Exception e) {
            e.printStackTrace();
            return resolveAndNotResolvedList;
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<MonthReportModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

//    public void showLoader(boolean show) {
//        this.showLoader = show;
//        notifyDataSetChanged();
//    }

    public void showLoader(boolean show) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            new Handler(Looper.getMainLooper()).post(() -> {
                this.showLoader = show;
                notifyDataSetChanged();
            });
        } else {
            this.showLoader = show;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size() + (showLoader ? 1 : 0);
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (list != null && position == list.size() && showLoader) {
            return VIEW_TYPE_LOADING;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ItemMonthlyReportsActivityBinding binding;

        public ItemViewHolder(@NonNull ItemMonthlyReportsActivityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public static class LoaderViewHolder extends RecyclerView.ViewHolder {
        public LoaderViewHolder(@NonNull ItemProgressBarPaginationBinding binding) {
            super(binding.getRoot());
        }
    }
}