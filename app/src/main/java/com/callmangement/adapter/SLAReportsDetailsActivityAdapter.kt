package com.callmangement.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.callmangement.R;
import com.callmangement.databinding.ItemDailyReportsDetailsActivityBinding;
import com.callmangement.databinding.ItemSlaReportsDetailsActivityBinding;
import com.callmangement.model.complaints.ModelComplaintList;
import com.callmangement.model.reports.ModelSLAReportDetailsList;
import com.callmangement.ui.inventory.AddStockActivity;
import com.callmangement.utils.PrefManager;

import java.util.ArrayList;
import java.util.List;

public class SLAReportsDetailsActivityAdapter extends RecyclerView.Adapter<SLAReportsDetailsActivityAdapter.ViewHolder> implements Filterable {
    private final Context context;
    private List<ModelSLAReportDetailsList> modelComplaintList;
    private List<ModelSLAReportDetailsList> filterModelComplaintList;
    private PrefManager prefManager;

    public SLAReportsDetailsActivityAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSlaReportsDetailsActivityBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_sla_reports_details_activity, parent, false);
        return new ViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        prefManager = new PrefManager(context);
        ModelSLAReportDetailsList model = modelComplaintList.get(position);
        holder.binding.setData(model);

        holder.binding.textComplaintStatus.setText(context.getResources().getString(R.string.current_status)+" : ");

        holder.binding.textComplaintNumber.setText(context.getResources().getString(R.string.c_no)+" : "+model.getComplainRegNo());
        holder.binding.textName.setText(context.getResources().getString(R.string.name)+" : "+model.getCustomerName()+" "+"("+context.getResources().getString(R.string.fps_code)+" : "+model.getFpscode()+")");
        holder.binding.textPhoneNumber.setText(context.getResources().getString(R.string.mobile_no)+" : "+model.getMobileNo());

        if (model.getComplainStatusId() != null && model.getComplainStatusId().equals("1")){
            holder.binding.textComplaintStatusValue.setText(model.getComplainStatus());
            holder.binding.textComplaintStatusValue.setTextColor(context.getResources().getColor(R.color.colorCopyButton));

        } else if (model.getComplainStatusId() != null && model.getComplainStatusId().equals("2")){
            holder.binding.textComplaintStatusValue.setText(model.getComplainStatus());
            holder.binding.textComplaintStatusValue.setTextColor(context.getResources().getColor(R.color.colorRedDark));

        } else if (model.getComplainStatusId() != null && model.getComplainStatusId().equals("3")){
            holder.binding.textComplaintStatusValue.setText(model.getComplainStatus());
            holder.binding.textComplaintStatusValue.setTextColor(context.getResources().getColor(R.color.colorGreenDark));
        }else {
            holder.binding.textComplaintStatusValue.setText(model.getComplainStatus());
            holder.binding.textComplaintStatusValue.setTextColor(context.getResources().getColor(R.color.deep_purple_300));
        }

        if (model.getComplainRegDateStr() != null && !model.getComplainRegDateStr().isEmpty() && !model.getComplainRegDateStr().equalsIgnoreCase("null")){
            holder.binding.textComplaintRegDate.setVisibility(View.VISIBLE);
            holder.binding.textComplaintRegDate.setText(context.getResources().getString(R.string.reg_date)+" : "+model.getComplainRegDateStr());
        } else {
            holder.binding.textComplaintRegDate.setVisibility(View.GONE);
        }

        if (model.getSermarkDateStr() != null && !model.getSermarkDateStr().isEmpty() && !model.getSermarkDateStr().equalsIgnoreCase("null")){
            holder.binding.textComplaintResolveDate.setVisibility(View.VISIBLE);
            if (model.getComplainStatusId().equals("3")) {
                holder.binding.textComplaintResolveDate.setText(context.getResources().getString(R.string.resolved_date) + " : " + model.getSermarkDateStr());
            } else if (model.getComplainStatusId().equals("1")) {
                holder.binding.textComplaintResolveDate.setText(context.getResources().getString(R.string.send_to_se_date) + " : " + model.getSermarkDateStr());
            }
        } else {
            holder.binding.textComplaintResolveDate.setVisibility(View.GONE);
        }

        if (prefManager.getUSER_TYPE_ID().equalsIgnoreCase("6") && prefManager.getUSER_TYPE().equalsIgnoreCase("DSO")){
            holder.binding.textResoleDays.setVisibility(View.GONE);
        } else {
            holder.binding.textResoleDays.setVisibility(View.VISIBLE);
            holder.binding.textResoleDays.setText(model.getResolveTime() + " Days");
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<ModelSLAReportDetailsList> modelComplaintList){
        this.modelComplaintList = modelComplaintList;
        this.filterModelComplaintList = new ArrayList<>(modelComplaintList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (modelComplaintList != null){
            return modelComplaintList.size();
        } else {
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final ItemSlaReportsDetailsActivityBinding binding;

        public ViewHolder(@NonNull ItemSlaReportsDetailsActivityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @Override
    public Filter getFilter() {
        return Searched_Filter;
    }

    private final Filter Searched_Filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<ModelSLAReportDetailsList> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(filterModelComplaintList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (ModelSLAReportDetailsList item : filterModelComplaintList) {
                    if (item.getComplainRegNo().toLowerCase().contains(filterPattern) || item.getCustomerName().toLowerCase().contains(filterPattern) || item.getFpscode().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                modelComplaintList.clear();
                modelComplaintList.addAll((ArrayList) results.values);
                notifyDataSetChanged();
            }
        }
    };

}
