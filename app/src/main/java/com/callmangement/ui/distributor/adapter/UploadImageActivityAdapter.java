package com.callmangement.ui.distributor.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.callmangement.databinding.ItemUploadImageActivityBinding;
import com.callmangement.ui.distributor.activity.UploadImageActivity;
import com.callmangement.ui.distributor.model.PosDistributionDetail;
import java.util.List;

public class UploadImageActivityAdapter extends RecyclerView.Adapter<UploadImageActivityAdapter.ViewHolder> {
    private final List<PosDistributionDetail> list;
    private final Context context;

    public UploadImageActivityAdapter(Context context, List<PosDistributionDetail> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUploadImageActivityBinding binding = ItemUploadImageActivityBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try{
            holder.binding.tvFpsCode.setText(list.get(position).getFpscode());
            holder.binding.tvDealerName.setText(list.get(position).getDealerName());
            holder.binding.tvDistrict.setText(list.get(position).getDistrictName());

            if (list.get(position).getIsFormUploaded().equalsIgnoreCase("true"))
                holder.binding.tvSubmitForm.setVisibility(View.GONE);
            else holder.binding.tvSubmitForm.setVisibility(View.VISIBLE);

            if (list.get(position).getIsPhotoUploaded().equalsIgnoreCase("true"))
                holder.binding.tvUpload.setVisibility(View.GONE);
            else holder.binding.tvUpload.setVisibility(View.VISIBLE);

            holder.binding.tvUpload.setOnClickListener(view -> ((UploadImageActivity)context).uploadImage(list.get(position).getFpscode()
                    ,list.get(position).getTranId().toString(),list.get(position).getDistrictId().toString(),"1"));

            holder.binding.tvSubmitForm.setOnClickListener(view -> ((UploadImageActivity)context).uploadImage(list.get(position).getFpscode()
                    ,list.get(position).getTranId().toString(),list.get(position).getDistrictId().toString(),"2"));

            holder.binding.tvSubmitFormView.setOnClickListener(view -> {
                ((UploadImageActivity)context).posDistributionFormView(list.get(position));
            });

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemUploadImageActivityBinding binding;
        public ViewHolder(ItemUploadImageActivityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
