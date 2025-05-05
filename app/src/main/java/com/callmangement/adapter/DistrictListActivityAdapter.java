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
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ItemAttedanceDetailsActivityBinding;
import com.callmangement.databinding.ItemDistrictListActivityBinding;
import com.callmangement.model.district.ModelDistrictList;
import com.callmangement.ui.attendance.ServiceEngineerListActivity;
import com.callmangement.utils.PrefManager;

import java.util.ArrayList;
import java.util.List;

public class DistrictListActivityAdapter extends RecyclerView.Adapter<DistrictListActivityAdapter.ViewHolder> {
    private final Context context;
    private List<ModelDistrictList> district_List;
    private final PrefManager prefManager;

    public DistrictListActivityAdapter(Context context) {
        this.context = context;
        prefManager = new PrefManager(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDistrictListActivityBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_district_list_activity, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelDistrictList model = district_List.get(position);
        if (!prefManager.getUSER_Change_Language().equals("")) {
            if (prefManager.getUSER_Change_Language().equals("en"))
                holder.binding.textDistrictName.setText(model.getDistrictNameEng());
            else holder.binding.textDistrictName.setText(model.getDistrictNameHi());
        }
//        holder.binding.crdItem.setOnClickListener(view -> {
//            context.startActivity(new Intent(context, ServiceEngineerListActivity.class));
//        });

    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<ModelDistrictList> district_List){
        this.district_List = district_List;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (district_List != null)
            return district_List.size();
        else return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final ItemDistrictListActivityBinding binding;
        public ViewHolder(@NonNull ItemDistrictListActivityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
