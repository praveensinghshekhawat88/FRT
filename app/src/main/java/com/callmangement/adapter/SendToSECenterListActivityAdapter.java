package com.callmangement.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.callmangement.databinding.ItemSendToSeCenterListBinding;
import com.callmangement.model.complaints.ModelComplaintList;
import com.callmangement.ui.complaint.ComplaintDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class SendToSECenterListActivityAdapter extends RecyclerView.Adapter<SendToSECenterListActivityAdapter.ViewHolder> implements Filterable {
    private final Context context;
    private int row_index = 0;
    private final int REQUEST_CODE = 1;
    List<ModelComplaintList> modelComplaintList;
    private List<ModelComplaintList> modelComplaintFilterList;

    public SendToSECenterListActivityAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSendToSeCenterListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_send_to_se_center_list, parent, false);
        return new ViewHolder(binding);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ModelComplaintList model = modelComplaintList.get(position);
        holder.binding.setData(model);

        holder.binding.textComplaintStatus.setText(context.getResources().getString(R.string.complaint_status)+" : ");

        holder.binding.textComplaintNumber.setText(context.getResources().getString(R.string.c_no) + " : " + model.getComplainRegNo());
        holder.binding.textName.setText(context.getResources().getString(R.string.name) + " : " + model.getCustomerName() + " " + "(" + context.getResources().getString(R.string.fps_code) + " : " + model.getFpscode() + ")");
        holder.binding.textPhoneNumber.setText(context.getResources().getString(R.string.mobile_no) + " : " + model.getMobileNo());
        holder.binding.textDis.setText(model.getDistrict());

        holder.binding.textComplaintStatusValue.setText(model.getComplainStatus());
        holder.binding.textComplaintStatusValue.setTextColor(context.getResources().getColor(R.color.colorRedDark));

        if (model.getComplainRegDateStr()!=null && !model.getComplainRegDateStr().isEmpty() && !model.getComplainRegDateStr().equalsIgnoreCase("null")){
            holder.binding.textComplaintRegDate.setVisibility(View.VISIBLE);
            holder.binding.textComplaintRegDate.setText(context.getResources().getString(R.string.reg_date)+" : "+model.getComplainRegDateStr());
        }else {
            holder.binding.textComplaintRegDate.setVisibility(View.GONE);
        }

        if (model.getSermarkDateStr() != null && !model.getSermarkDateStr().isEmpty() && !model.getSermarkDateStr().equalsIgnoreCase("null")) {
            holder.binding.textComplaintResolveDate.setVisibility(View.VISIBLE);
            holder.binding.textComplaintResolveDate.setText(context.getResources().getString(R.string.send_to_se_date) + " : " + model.getSermarkDateStr());
        } else {
            holder.binding.textComplaintResolveDate.setVisibility(View.GONE);
        }

        holder.binding.listItem.setOnClickListener(view -> {
            row_index = position;
            notifyDataSetChanged();
            Intent intent = new Intent(view.getContext(), ComplaintDetailActivity.class);
            intent.putExtra("param", model);
            intent.putExtra("param2","service_center");
            ((Activity) view.getContext()).startActivityForResult(intent, REQUEST_CODE);
//            view.getContext().startActivity(new Intent(view.getContext(), ComplaintDetailActivity.class).putExtra("param", model).putExtra("param2", "service_center"));
        });

        if (row_index == position) {
            holder.binding.listItem.setBackground(context.getDrawable(R.drawable.shape_rect_background_dark_blue_layout));
            holder.binding.textName.setTextColor(context.getResources().getColor(R.color.white));
            holder.binding.textPhoneNumber.setTextColor(context.getResources().getColor(R.color.white));
            holder.binding.textComplaintNumber.setTextColor(context.getResources().getColor(R.color.white));
            holder.binding.textTehsil.setTextColor(context.getResources().getColor(R.color.white));
            holder.binding.textComplaintRegDate.setTextColor(context.getResources().getColor(R.color.white));
            holder.binding.textComplaintResolveDate.setTextColor(context.getResources().getColor(R.color.white));
            holder.binding.textComplaintStatus.setTextColor(context.getResources().getColor(R.color.white));
        } else {
            holder.binding.listItem.setBackground(context.getDrawable(R.drawable.shape_rect_background_white_layout));
            holder.binding.textName.setTextColor(context.getResources().getColor(R.color.grayDark));
            holder.binding.textPhoneNumber.setTextColor(context.getResources().getColor(R.color.grayDark));
            holder.binding.textComplaintNumber.setTextColor(context.getResources().getColor(R.color.colorActionBar));
            holder.binding.textTehsil.setTextColor(context.getResources().getColor(R.color.blue_700));
            holder.binding.textComplaintRegDate.setTextColor(context.getResources().getColor(R.color.grayDark));
            holder.binding.textComplaintResolveDate.setTextColor(context.getResources().getColor(R.color.grayDark));
            holder.binding.textComplaintStatus.setTextColor(context.getResources().getColor(R.color.grayDark));
        }
    }

   /* @SuppressLint("NotifyDataSetChanged")
    public void setData(List<ModelComplaintList> modelComplaintList) {
        this.modelComplaintList = modelComplaintList;
        this.modelComplaintFilterList = new ArrayList<>(modelComplaintList);
        notifyDataSetChanged();
    }*/
   @SuppressLint("NotifyDataSetChanged")
   public void setData(List<ModelComplaintList> modelComplaintList) {
       this.modelComplaintList = new ArrayList<>(modelComplaintList); // correct way
       this.modelComplaintFilterList = new ArrayList<>(modelComplaintList);
       notifyDataSetChanged();
   }

    public void addData(List<ModelComplaintList> newItems) {
        int startPosition = modelComplaintList.size();
        this.modelComplaintList.addAll(newItems);
        this.modelComplaintFilterList.addAll(newItems);
        notifyItemRangeInserted(startPosition, newItems.size());
    }

    @Override
    public int getItemCount() {
        if (modelComplaintList != null) {
            return modelComplaintList.size();
        } else {
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemSendToSeCenterListBinding binding;

        public ViewHolder(@NonNull ItemSendToSeCenterListBinding binding) {
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
            ArrayList<ModelComplaintList> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(modelComplaintFilterList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (ModelComplaintList item : modelComplaintFilterList) {
                    if (item.getComplainRegNo().toLowerCase().contains(filterPattern) || item.getCustomerName().toLowerCase().contains(filterPattern) || item.getFpscode().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @SuppressLint("NotifyDataSetChanged")
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