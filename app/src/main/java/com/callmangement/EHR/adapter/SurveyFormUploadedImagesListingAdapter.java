package com.callmangement.EHR.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.callmangement.EHR.models.SurveyFormDocInfo;
import com.callmangement.EHR.support.OnSingleClickListener;
import com.callmangement.R;

import java.util.List;

public class SurveyFormUploadedImagesListingAdapter extends RecyclerView.Adapter<SurveyFormUploadedImagesListingAdapter.MyViewHolder> {

    private final Context mContext;
    private final List<SurveyFormDocInfo> surveyFormDocInfos;
    private final OnItemViewClickListener mOnItemViewClickListener;

    public SurveyFormUploadedImagesListingAdapter(Context context, List<SurveyFormDocInfo> surveyFormDocInfos,
                                                  OnItemViewClickListener mOnItemViewClickListener) {
        this.mContext = context;
        this.surveyFormDocInfos = surveyFormDocInfos;
        this.mOnItemViewClickListener = mOnItemViewClickListener;
    }

    public interface OnItemViewClickListener {
        void onItemClick(SurveyFormDocInfo surveyFormDocInfo, int position);
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
        SurveyFormDocInfo surveyFormDocInfo = surveyFormDocInfos.get(position);

        String imagePath = surveyFormDocInfo.getDocumentPath();

        if (imagePath != null
                && !imagePath.isEmpty()
                && !imagePath.equalsIgnoreCase("null")) {
            holder.ivChallanImage.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(imagePath)
                    .placeholder(R.drawable.image_not_fount)
                    .into(holder.ivChallanImage);

        }
        
        holder.ivChallanImage.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (mOnItemViewClickListener != null)
                    mOnItemViewClickListener.onItemClick(surveyFormDocInfo, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (surveyFormDocInfos == null)
            return 0;
        else
            return surveyFormDocInfos.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivChallanImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ivChallanImage = itemView.findViewById(R.id.ivChallanImage);
        }
    }
}