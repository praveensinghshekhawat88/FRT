package com.callmangement.ui.pos_help.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.callmangement.R;
import com.callmangement.databinding.ItemFaqListBinding;
import com.callmangement.ui.pos_help.activity.FAQDetailActivity;
import com.callmangement.ui.pos_help.model.ModelFAQList;

import java.util.ArrayList;
import java.util.List;

public class FAQListAdapter extends RecyclerView.Adapter<FAQListAdapter.ViewHolder> {
    private final Context context;
    private final List<ModelFAQList> modelFAQList;
    private final List<ImageView> imageViewList = new ArrayList<>();

    public FAQListAdapter(Context context, List<ModelFAQList> modelFAQList) {
        this.context = context;
        this.modelFAQList = modelFAQList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFaqListBinding binding = ItemFaqListBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        imageViewList.add(holder.binding.ivHideShow);

        holder.binding.ivHideShow.setOnClickListener(view -> {
            if (modelFAQList.get(position).isSelectFlag()) {
                imageViewList.get(position).setBackgroundResource(R.drawable.ic_show_icon);
                modelFAQList.get(position).setSelectFlag(false);
                holder.binding.answerLay.setVisibility(View.GONE);
            } else {
                imageViewList.get(position).setBackgroundResource(R.drawable.ic_hide_icon);
                modelFAQList.get(position).setSelectFlag(true);
                holder.binding.answerLay.setVisibility(View.VISIBLE);
            }
        });

        holder.binding.buttonView.setOnClickListener(view -> context.startActivity(new Intent(context, FAQDetailActivity.class)));

    }

    @Override
    public int getItemCount() {
        return modelFAQList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemFaqListBinding binding;

        public ViewHolder(ItemFaqListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
