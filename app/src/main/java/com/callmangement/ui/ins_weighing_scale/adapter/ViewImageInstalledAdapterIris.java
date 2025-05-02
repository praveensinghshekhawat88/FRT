package com.callmangement.ui.ins_weighing_scale.adapter;

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
import com.callmangement.support.OnSingleClickListener;
import com.callmangement.ui.home.ZoomInZoomOutActivity;
import com.callmangement.ui.ins_weighing_scale.model.InstalledDetailed.InstalledImagesDetail;
import com.callmangement.utils.Constants;

import java.util.ArrayList;

public class ViewImageInstalledAdapterIris extends RecyclerView.Adapter<ViewImageInstalledAdapterIris.MyViewHolder> {

    private final Context mContext;
    private final ArrayList<InstalledImagesDetail> getErrorImagesDatumArrayList;
    private OnItemViewClickListener mOnItemViewClickListener;
    private final boolean isDeliver;
    private final Integer count;

    public ViewImageInstalledAdapterIris(Context context, ArrayList<InstalledImagesDetail> getErrorImagesDatumArrayList,
                                         boolean isDeliver, Integer count) {
        this.mContext = context;
        this.getErrorImagesDatumArrayList = getErrorImagesDatumArrayList;
        this.isDeliver = isDeliver;
        this.count = count;
    }

    public interface OnItemViewClickListener {
        void onItemClick(InstalledImagesDetail campDocInfo, int position);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_images_sec, parent, false);
        return new MyViewHolder(itemview);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        InstalledImagesDetail getErrorImagesDatum = getErrorImagesDatumArrayList.get(position);

        int photoTypeId = getErrorImagesDatum.getPhotoTypeId();
        String imagePath = null;

        imagePath = getErrorImagesDatum.getImagePath();
        Log.d("phototypeid",""+photoTypeId);

        if (imagePath != null
                && !imagePath.isEmpty()
                && !imagePath.equalsIgnoreCase("null")) {
            holder.ivChallanImage.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(Constants.API_BASE_URL +imagePath)
                    .placeholder(R.drawable.image_not_fount)
                    .into(holder.ivChallanImage);
            Log.d("imageurl"," ---"+Constants.API_BASE_URL+imagePath);
        }
        holder.ivChallanImage.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (mOnItemViewClickListener != null)
                    mOnItemViewClickListener.onItemClick(getErrorImagesDatum, position);
                mContext.startActivity(new Intent(mContext, ZoomInZoomOutActivity.class).putExtra("image", Constants.API_BASE_URL+ getErrorImagesDatum.getImagePath()));

            }
        });

        if (isDeliver) {
            if (photoTypeId <= 5) {
                holder.mainView.setVisibility(View.VISIBLE);
                imagePath = getErrorImagesDatum.getImagePath();
                Log.d("phototypeid", "" + photoTypeId);


                if (imagePath != null
                        && !imagePath.isEmpty()
                        && !imagePath.equalsIgnoreCase("null")) {
                    holder.ivChallanImage.setVisibility(View.VISIBLE);
                    Glide.with(mContext)
                            .load(Constants.API_BASE_URL + imagePath)
                            .placeholder(R.drawable.image_not_fount)
                            .into(holder.ivChallanImage);
                    Log.d("imageurl", " ---" + Constants.API_BASE_URL + imagePath);
                }
                holder.ivChallanImage.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        if (mOnItemViewClickListener != null)
                            mOnItemViewClickListener.onItemClick(getErrorImagesDatum, position);
                        mContext.startActivity(new Intent(mContext, ZoomInZoomOutActivity.class).putExtra("image", Constants.API_BASE_URL + getErrorImagesDatum.getImagePath()));

                    }
                });
            } else {
                holder.mainView.setVisibility(View.GONE);
            }
        } else {
            if (photoTypeId > 5) {
                holder.mainView.setVisibility(View.VISIBLE);
                imagePath = getErrorImagesDatum.getImagePath();
                Log.d("phototypeid", "" + photoTypeId);


                if (imagePath != null
                        && !imagePath.isEmpty()
                        && !imagePath.equalsIgnoreCase("null")) {
                    holder.ivChallanImage.setVisibility(View.VISIBLE);
                    Glide.with(mContext)
                            .load(Constants.API_BASE_URL + imagePath)
                            .placeholder(R.drawable.image_not_fount)
                            .into(holder.ivChallanImage);
                    Log.d("imageurl", " ---" + Constants.API_BASE_URL + imagePath);
                }
                holder.ivChallanImage.setOnClickListener(new OnSingleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        if (mOnItemViewClickListener != null)
                            mOnItemViewClickListener.onItemClick(getErrorImagesDatum, position);
                        mContext.startActivity(new Intent(mContext, ZoomInZoomOutActivity.class).putExtra("image", Constants.API_BASE_URL + getErrorImagesDatum.getImagePath()));

                    }
                });
            } else {
                holder.mainView.setVisibility(View.GONE);
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
        ImageView ivChallanImage;
        ConstraintLayout mainView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ivChallanImage = itemView.findViewById(R.id.ivChallanImage);
            mainView = itemView.findViewById(R.id.mainView);
        }
    }
}