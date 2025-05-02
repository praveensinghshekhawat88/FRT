package com.callmangement.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.callmangement.R;
import com.callmangement.databinding.ItemComplaintsListAccordingToFpsActivityBinding;
import com.callmangement.model.fps_wise_complaints.ModelFPSComplaintList;

import java.util.List;

public class ComplaintsListAccordingToFPSActivityAdapter extends RecyclerView.Adapter<ComplaintsListAccordingToFPSActivityAdapter.ViewHolder> {
    private final Context context;
    private final List<ModelFPSComplaintList> modelFPSComplaintLists;

    public ComplaintsListAccordingToFPSActivityAdapter(Context mContext, List<ModelFPSComplaintList> modelFPSComplaintLists) {
        this.context = mContext;
        this.modelFPSComplaintLists = modelFPSComplaintLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemComplaintsListAccordingToFpsActivityBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_complaints_list_according_to_fps_activity, parent, false);
        return new ViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelFPSComplaintList model = modelFPSComplaintLists.get(position);
        holder.binding.setData(model);

        holder.binding.textComplaintNumber.setText(context.getResources().getString(R.string.c_no)+" : "+model.getComplainRegNo());
        holder.binding.textName.setText(model.getCustomerName()+" "+"( "+context.getResources().getString(R.string.fps_code)+" : "+model.getFpscode()+" )");
        holder.binding.textPhoneNumber.setText(model.getMobileNo());
        holder.binding.textComplaintDesc.setText(model.getComplainDesc());

        Log.d("ComplainStatusId","---"+model.getComplainStatusId());

        if (model.getComplainStatusId().equals("1")){
            holder.binding.textComplaintStatusValue.setText(model.getComplainStatus());
            holder.binding.textComplaintStatusValue.setTextColor(context.getResources().getColor(R.color.colorCopyButton));

        } else if (model.getComplainStatusId().equals("2")){
            holder.binding.textComplaintStatusValue.setText(model.getComplainStatus());
            holder.binding.textComplaintStatusValue.setTextColor(context.getResources().getColor(R.color.colorRedDark));

        } else if (model.getComplainStatusId().equals("3")){
            holder.binding.textComplaintStatusValue.setText(model.getComplainStatus());
            holder.binding.textComplaintStatusValue.setTextColor(context.getResources().getColor(R.color.colorGreenDark));
        } else {
            holder.binding.textComplaintStatusValue.setText(model.getComplainStatus());
            holder.binding.textComplaintStatusValue.setTextColor(context.getResources().getColor(R.color.deep_purple_300));
        }

        if (model.getComplainRegDateStr()!=null && !model.getComplainRegDateStr().isEmpty() && !model.getComplainRegDateStr().equalsIgnoreCase("null")){
            holder.binding.layoutCompRegDate.setVisibility(View.VISIBLE);
            holder.binding.textComplaintRegDate.setText(model.getComplainRegDateStr());
        }else {
            holder.binding.layoutCompRegDate.setVisibility(View.GONE);
        }

        if (model.getSermarkDateStr()!=null && !model.getSermarkDateStr().isEmpty() && !model.getSermarkDateStr().equalsIgnoreCase("null")){
            holder.binding.layoutCompResolvedDate.setVisibility(View.VISIBLE);
            if (model.getComplainStatusId().equals("3")) {
                holder.binding.textResolvedDate.setText(context.getResources().getString(R.string.resolved_date));
                holder.binding.textComplaintResolveDate.setText(model.getSermarkDateStr());
            } else if (model.getComplainStatusId().equals("1")){
                holder.binding.textResolvedDate.setText(context.getResources().getString(R.string.send_to_se_date));
                holder.binding.textComplaintResolveDate.setText(model.getSermarkDateStr());
            }
        } else {
            holder.binding.layoutCompResolvedDate.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        if (modelFPSComplaintLists != null) {
            return modelFPSComplaintLists.size();
        }else {
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final ItemComplaintsListAccordingToFpsActivityBinding binding;
        public ViewHolder(@NonNull ItemComplaintsListAccordingToFpsActivityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
