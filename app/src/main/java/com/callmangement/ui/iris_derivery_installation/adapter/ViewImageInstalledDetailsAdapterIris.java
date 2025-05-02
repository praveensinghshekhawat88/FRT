package com.callmangement.ui.iris_derivery_installation.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.callmangement.R;
import com.callmangement.databinding.ItemIrisInstalledlistlBinding;
import com.callmangement.databinding.ItemViewImagesDtlSecBinding;
import com.callmangement.support.OnSingleClickListener;
import com.callmangement.ui.home.ZoomInZoomOutActivity;
import com.callmangement.ui.iris_derivery_installation.Model.IrisInstalledImagesDtl;
import com.callmangement.utils.Constants;

import java.util.ArrayList;

public class ViewImageInstalledDetailsAdapterIris extends RecyclerView.Adapter<ViewImageInstalledDetailsAdapterIris.MyViewHolder> {

    private final Context mContext;
    private final ArrayList<IrisInstalledImagesDtl> getErrorImagesDatumArrayList;
    private OnItemViewClickListener mOnItemViewClickListener;
    private final boolean isDeliver;
    private final Integer count;

    public ViewImageInstalledDetailsAdapterIris(Context context, ArrayList<IrisInstalledImagesDtl> getErrorImagesDatumArrayList,
                                                boolean isDeliver, Integer count) {
        this.mContext = context;
        this.getErrorImagesDatumArrayList = getErrorImagesDatumArrayList;
        this.isDeliver = isDeliver;
        this.count = count;
    }

    public interface OnItemViewClickListener {
        void onItemClick(IrisInstalledImagesDtl campDocInfo, int position);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemViewImagesDtlSecBinding itemViewImagesDtlSecBinding = ItemViewImagesDtlSecBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(itemViewImagesDtlSecBinding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewImageInstalledDetailsAdapterIris.MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        IrisInstalledImagesDtl getErrorImagesDatum = getErrorImagesDatumArrayList.get(position);
        int photoTypeId = getErrorImagesDatum.getPhotoTypeId();
        String imagePath = null;
        imagePath = getErrorImagesDatum.getImagePath();
        Log.d("phototypeid", "" + photoTypeId);

        if (imagePath != null
                && !imagePath.isEmpty()
                && !imagePath.equalsIgnoreCase("null")) {
            holder.binding.ivChallanImage.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(Constants.API_BASE_URL + imagePath)
                    .placeholder(R.drawable.image_not_fount)
                    .into(holder.binding.ivChallanImage);
            Log.d("imageurl", " ---" + Constants.API_BASE_URL + imagePath);
        }

        holder.binding.ivChallanImage.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (mOnItemViewClickListener != null)
                    mOnItemViewClickListener.onItemClick(getErrorImagesDatum, position);
                mContext.startActivity(new Intent(mContext, ZoomInZoomOutActivity.class).putExtra("image", Constants.API_BASE_URL + getErrorImagesDatum.getImagePath()));
            }
        });

        if (isDeliver) {
            if (photoTypeId <= 5) {
                holder.binding.mainView.setVisibility(View.VISIBLE);
                imagePath = getErrorImagesDatum.getImagePath();
                Log.d("phototypeid", "" + photoTypeId);

                if (imagePath != null
                        && !imagePath.isEmpty()
                        && !imagePath.equalsIgnoreCase("null")) {
                    holder.binding.ivChallanImage.setVisibility(View.VISIBLE);
                    Glide.with(mContext)
                            .load(Constants.API_BASE_URL + imagePath)
                            .placeholder(R.drawable.image_not_fount)
                            .into(holder.binding.ivChallanImage);
                    Log.d("imageurl", " ---" + Constants.API_BASE_URL + imagePath);
                }
                holder.binding.ivChallanImage.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        if (mOnItemViewClickListener != null)
                            mOnItemViewClickListener.onItemClick(getErrorImagesDatum, position);
                        mContext.startActivity(new Intent(mContext, ZoomInZoomOutActivity.class).putExtra("image", Constants.API_BASE_URL + getErrorImagesDatum.getImagePath()));
                    }
                });
            } else {
                holder.binding.mainView.setVisibility(View.GONE);
            }
        } else {
            if (photoTypeId > 5) {
                holder.binding.mainView.setVisibility(View.VISIBLE);
                imagePath = getErrorImagesDatum.getImagePath();
                Log.d("phototypeid", "" + photoTypeId);
                if (imagePath != null
                        && !imagePath.isEmpty()
                        && !imagePath.equalsIgnoreCase("null")) {
                    holder.binding.ivChallanImage.setVisibility(View.VISIBLE);
                    Glide.with(mContext)
                            .load(Constants.API_BASE_URL + imagePath)
                            .placeholder(R.drawable.image_not_fount)
                            .into(holder.binding.ivChallanImage);
                    Log.d("imageurl", " ---" + Constants.API_BASE_URL + imagePath);
                }
                holder.binding.ivChallanImage.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        if (mOnItemViewClickListener != null)
                            mOnItemViewClickListener.onItemClick(getErrorImagesDatum, position);
                        mContext.startActivity(new Intent(mContext, ZoomInZoomOutActivity.class).putExtra("image", Constants.API_BASE_URL + getErrorImagesDatum.getImagePath()));
                    }
                });
            } else {
                holder.binding.mainView.setVisibility(View.GONE);
            }
        }
        // Log.d("imagepath"," ---"+Utils.Baseurl +imagePath);
    }

    @Override
    public int getItemCount() {
        if (getErrorImagesDatumArrayList == null)
            return 0;
        else
            return getErrorImagesDatumArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final ItemViewImagesDtlSecBinding binding;
        public MyViewHolder(ItemViewImagesDtlSecBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
