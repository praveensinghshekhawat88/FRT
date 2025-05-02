package com.callmangement.EHR.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.callmangement.EHR.ehrActivities.FeedBackListDetail;
import com.callmangement.EHR.models.FeedbackbyDCListDatum;
import com.callmangement.databinding.ItemFeedbacklistbydcBinding;
import com.callmangement.databinding.ItemInstalledlistlBinding;
import com.callmangement.ui.ins_weighing_scale.activity.InstalledDetail;
import com.callmangement.ui.ins_weighing_scale.model.Installed.InstalledDatum;

import java.io.Serializable;
import java.util.ArrayList;

public class FeedbackListAdapter extends RecyclerView.Adapter<FeedbackListAdapter.ViewHolder> {
    private final ArrayList<FeedbackbyDCListDatum> feedbackbyDCListDatumArrayList;
    private final Context context;
    public FeedbackListAdapter(ArrayList<FeedbackbyDCListDatum> feedbackbyDCListDatumArrayList, Context context) {
        this.feedbackbyDCListDatumArrayList = feedbackbyDCListDatumArrayList;
        this.context = context;
    }

    // data is passed into the constructor
    // inflates the row layout from xml when needed
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

    //    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_installedlistl, parent, false);
    //    return new ViewHolder(view);

        ItemFeedbacklistbydcBinding itemFeedbacklistbydcBinding = ItemFeedbacklistbydcBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemFeedbacklistbydcBinding);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        FeedbackbyDCListDatum model = feedbackbyDCListDatumArrayList.get(position);

       // holder.binding.tvDealername.setText(model.getDealerName());
      holder.binding.tvFpscode.setText(model.getFpscode());
      holder.binding.dis.setText(model.getDistrictName());
       // holder.binding.tvDealermobno.setText(model.getDealerMobileNo());
      holder.binding.tvFeedbackid.setText(String.valueOf(model.getFeedBackId()));
        holder.binding.tvFeedbackby.setText(model.getFeedBackBy());

       /* if (status.equals("Yes") & status.length() != 0) {
            holder.tv_status.setTextColor(Color.parseColor("#43A047"));
            holder.tv_status.setText(status);
        } else if (status.equals("No") & status.length() != 0) {
            holder.tv_status.setTextColor(Color.parseColor("#F44336"));
            holder.tv_status.setText(status);
        } else {
        }*/

        holder.binding.rlItem.setOnClickListener(view -> {
           Intent intent = new Intent(context, FeedBackListDetail.class);
            intent.putExtra("paramm",  model);
            context.startActivity(intent);
        });

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return feedbackbyDCListDatumArrayList.size();
    }
    // stores and recycles views as they are scrolled off screen
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ItemFeedbacklistbydcBinding binding;
        public ViewHolder(ItemFeedbacklistbydcBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        public void onClick(View view) {
        }
    }
}
