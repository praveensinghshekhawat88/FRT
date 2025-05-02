package com.callmangement.ui.errors.adapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.callmangement.R;
import com.callmangement.support.OnSingleClickListener;
import com.callmangement.ui.errors.model.GetErrorImagesDatum;
import com.callmangement.ui.home.ZoomInZoomOutActivity;
import com.callmangement.utils.Constants;

import java.util.ArrayList;
public class ViewImagesListingAdapter extends RecyclerView.Adapter<ViewImagesListingAdapter.MyViewHolder> {
    private final Context mContext;
    private final ArrayList<GetErrorImagesDatum> getErrorImagesDatumArrayList;
    private final OnItemViewClickListener mOnItemViewClickListener;
    public ViewImagesListingAdapter(Context context, ArrayList<GetErrorImagesDatum> getErrorImagesDatumArrayList,
                                    OnItemViewClickListener mOnItemViewClickListener) {
        this.mContext = context;
        this.getErrorImagesDatumArrayList = getErrorImagesDatumArrayList;
        this.mOnItemViewClickListener = mOnItemViewClickListener;
    }
    public interface OnItemViewClickListener {
        void onItemClick(GetErrorImagesDatum campDocInfo, int position);
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_images, parent, false);
        return new MyViewHolder(itemview);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        GetErrorImagesDatum getErrorImagesDatum = getErrorImagesDatumArrayList.get(position);
        String imagePath = getErrorImagesDatum.getImagePath();
        if (imagePath != null
                && !imagePath.isEmpty()
                && !imagePath.equalsIgnoreCase("null")) {
            holder.ivChallanImage.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(Constants.API_BASE_URL +imagePath)
                    .placeholder(R.drawable.image_not_fount)
                    .into(holder.ivChallanImage);
          //  Log.d("imageurl"," ---"+Constants.API_BASE_URL+imagePath);
        }
        holder.ivChallanImage.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (mOnItemViewClickListener != null)
                    mOnItemViewClickListener.onItemClick(getErrorImagesDatum, position);
           //   mContext.startActivity(new Intent(mContext, ZoomInZoomOutActivity.class).putExtra("image", Constants.API_BASE_URL+""+ getErrorImagesDatum.getImagePath()));

            }
        });


    }
    @Override
    public int getItemCount() {
        if (getErrorImagesDatumArrayList == null)
            return 0;
        else
            return getErrorImagesDatumArrayList.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivChallanImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ivChallanImage = itemView.findViewById(R.id.ivChallanImage);
        }
    }
}