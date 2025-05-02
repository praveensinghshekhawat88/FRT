package com.callmangement.ui.pos_issue.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.callmangement.databinding.ItemViewIssuesBinding;

public class ViewIssuesActivityAdapter extends RecyclerView.Adapter<ViewIssuesActivityAdapter.ViewHolder> {
    private final Context context;

    public ViewIssuesActivityAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemViewIssuesBinding binding = ItemViewIssuesBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final ItemViewIssuesBinding binding;
        public ViewHolder(ItemViewIssuesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
