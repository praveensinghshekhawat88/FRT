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
import com.callmangement.databinding.ItemLastComplaintFpsListActivityBinding;
import com.callmangement.model.fps_wise_complaints.ModelFPSDistTehWiseList;
import com.callmangement.ui.complaints_fps_wise.ComplaintsListAccordingToFPSActivity;

import java.util.List;

public class LastComplaintFPSListActivityAdapter extends RecyclerView.Adapter<LastComplaintFPSListActivityAdapter.ViewHolder> {
    private final Context context;
    private final List<ModelFPSDistTehWiseList> modelFPSDistTehWiseList;

    public LastComplaintFPSListActivityAdapter(Context context, List<ModelFPSDistTehWiseList> modelFPSDistTehWiseList) {
        this.context = context;
        this.modelFPSDistTehWiseList = modelFPSDistTehWiseList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLastComplaintFpsListActivityBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_last_complaint_fps_list_activity, parent, false);
        return new ViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelFPSDistTehWiseList model = modelFPSDistTehWiseList.get(position);
        holder.binding.setData(model);

        if (!model.getCustomerNameEng().isEmpty()) {
            holder.binding.textName.setText(model.getCustomerNameEng());
        }
        if (!model.getFpscode().isEmpty()) {
            holder.binding.textFpsCode.setText(model.getFpscode());
        }
        if (!model.getMobileNo().isEmpty()) {
            holder.binding.textMobileNumber.setText(model.getMobileNo());
        }

        if (model.getComplainStatus().equalsIgnoreCase("Resolved")){
            holder.binding.textComplaintStatus.setTextColor(context.getResources().getColor(R.color.colorGreenDark));
        } else if (model.getComplainStatus().equalsIgnoreCase("NotResolve")){
            holder.binding.textComplaintStatus.setTextColor(context.getResources().getColor(R.color.colorRedDark));
        } else {
            holder.binding.textComplaintStatus.setTextColor(context.getResources().getColor(R.color.colorRedDark));
        }

        if (!model.getComplainRegNo().isEmpty() && model.getComplainRegNo() != null){
            holder.binding.layoutComplainRegNumber.setVisibility(View.VISIBLE);
            holder.binding.textComplaintNumber.setText(model.getComplainRegNo());
        } else {
            holder.binding.layoutComplainRegNumber.setVisibility(View.GONE);
        }

        if (!model.getComplainStatus().isEmpty() && model.getComplainStatus() != null){
            holder.binding.layoutComplainStatus.setVisibility(View.VISIBLE);
            holder.binding.textComplaintStatus.setText(model.getComplainStatus());
        }else {
            holder.binding.layoutComplainStatus.setVisibility(View.GONE);
        }

        if (!model.getComplainDesc().isEmpty() && model.getComplainDesc() != null){
            holder.binding.layoutDescription.setVisibility(View.VISIBLE);
            holder.binding.textComplaintDesc.setText(model.getComplainDesc());
        }else {
            holder.binding.layoutDescription.setVisibility(View.GONE);
        }

        if (!model.getComplainRegDate().isEmpty() && model.getComplainRegDate() != null) {
            holder.binding.layoutLastComplainRegDate.setVisibility(View.VISIBLE);
            String[] separator = model.getComplainRegDate().split(" ");
            String date = separator[0];
            String time = separator[1];
            holder.binding.textLastComplaintRegDate.setText(date);
        }else {
            holder.binding.layoutLastComplainRegDate.setVisibility(View.GONE);
        }

        holder.binding.crdItem.setOnClickListener(view -> context.startActivity(new Intent(context, ComplaintsListAccordingToFPSActivity.class).putExtra("fps_code", model.getFpscode())));
    }

    @Override
    public int getItemCount() {
        if (modelFPSDistTehWiseList != null) {
            return modelFPSDistTehWiseList.size();
        }else {
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final ItemLastComplaintFpsListActivityBinding binding;
        public ViewHolder(@NonNull ItemLastComplaintFpsListActivityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
