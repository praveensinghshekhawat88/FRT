package com.callmangement.EHR.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.callmangement.EHR.models.CampDocInfo;
import com.callmangement.EHR.support.OnSingleClickListener;
import com.callmangement.R;

import java.util.List;

public class CampUploadedImagesListingAdapter extends RecyclerView.Adapter<CampUploadedImagesListingAdapter.MyViewHolder> {

    private final Context mContext;
    private final List<CampDocInfo> campDocInfoList;
    private final OnItemViewClickListener mOnItemViewClickListener;

    public CampUploadedImagesListingAdapter(Context context, List<CampDocInfo> campDocInfoList,
                                            OnItemViewClickListener mOnItemViewClickListener) {
        this.mContext = context;
        this.campDocInfoList = campDocInfoList;
        this.mOnItemViewClickListener = mOnItemViewClickListener;
    }

    public interface OnItemViewClickListener {
        void onItemClick(CampDocInfo campDocInfo, int position);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_uploaded_view_images, parent, false);
        return new MyViewHolder(itemview);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        CampDocInfo campDocInfo = campDocInfoList.get(position);

        String imagePath = campDocInfo.getDocumentPath();

        if (imagePath != null
                && !imagePath.isEmpty()
                && !imagePath.equalsIgnoreCase("null")) {
            holder.ivChallanImage.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(imagePath)
                    .placeholder(R.drawable.image_not_fount)
                    .into(holder.ivChallanImage);
            Log.d("imageurl"," ---"+imagePath);
        }

        holder.ivChallanImage.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (mOnItemViewClickListener != null)
                    mOnItemViewClickListener.onItemClick(campDocInfo, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (campDocInfoList == null)
            return 0;
        else
            return campDocInfoList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivChallanImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ivChallanImage = itemView.findViewById(R.id.ivChallanImage);
        }
    }
}