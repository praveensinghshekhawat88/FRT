package com.callmangement.EHR.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.callmangement.EHR.api.Constants;
import com.callmangement.EHR.models.CampDetailsInfo;
import com.callmangement.EHR.support.OnSingleClickListener;
import com.callmangement.EHR.support.Preference;
import com.callmangement.R;
import com.callmangement.utils.PrefManager;

import java.util.List;

public class CampListingAdapter extends RecyclerView.Adapter<CampListingAdapter.MyViewHolder> {

    private final Context mContext;
    private List<CampDetailsInfo> campDetailsInfoList;
    private final OnItemOrganiseClickListener mOnItemOrganiseClickListener;
    private final OnItemUploadClickListener mOnItemUploadClickListener;
    private final OnItemUploadCampPhotoClickListener onItemUploadCampPhotoClickListener;
    private final OnItemDownloadClickListener mOnItemDownloadClickListener;
    private final OnItemViewClickListener mOnItemViewClickListener;
    private final OnItemCompleteClickListener mOnItemCompleteClickListener;
    private final OnItemDeleteClickListener mOnItemDeleteClickListener;

    public CampListingAdapter(Context context, List<CampDetailsInfo> campDetailsInfoList,
                              OnItemOrganiseClickListener onItemOrganiseClickListener,
                              OnItemUploadClickListener onItemUploadClickListener,
                              OnItemUploadCampPhotoClickListener onItemUploadCampPhotoClickListener,
                              OnItemDownloadClickListener onItemDownloadClickListener,
                              OnItemViewClickListener mOnItemViewClickListener,
                              OnItemCompleteClickListener mOnItemCompleteClickListener,
                              OnItemDeleteClickListener mOnItemDeleteClickListener) {
        this.mContext = context;
        this.campDetailsInfoList = campDetailsInfoList;
        this.mOnItemOrganiseClickListener = onItemOrganiseClickListener;
        this.mOnItemUploadClickListener = onItemUploadClickListener;
        this.onItemUploadCampPhotoClickListener = onItemUploadCampPhotoClickListener;
        this.mOnItemDownloadClickListener = onItemDownloadClickListener;
        this.mOnItemViewClickListener = mOnItemViewClickListener;
        this.mOnItemCompleteClickListener = mOnItemCompleteClickListener;
        this.mOnItemDeleteClickListener = mOnItemDeleteClickListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void refreshItem(List<CampDetailsInfo> campDetailsInfoList) {
        this.campDetailsInfoList = campDetailsInfoList;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<CampDetailsInfo> list) {
        this.campDetailsInfoList = list;
        notifyDataSetChanged();
    }

    public interface OnItemOrganiseClickListener {
        void onItemClick(CampDetailsInfo campDetailsInfo, int position);
    }

    public interface OnItemUploadClickListener {
        void onItemClick(CampDetailsInfo campDetailsInfo, int position);
    }

    public interface OnItemUploadCampPhotoClickListener {
        void onItemClick(CampDetailsInfo campDetailsInfo, int position);
    }

    public interface OnItemDownloadClickListener {
        void onItemClick(CampDetailsInfo campDetailsInfo, int position);
    }

    public interface OnItemViewClickListener {
        void onItemClick(CampDetailsInfo campDetailsInfo, int position);
    }

    public interface OnItemCompleteClickListener {
        void onItemClick(CampDetailsInfo campDetailsInfo, int position);
    }

    public interface OnItemDeleteClickListener {
        void onItemClick(CampDetailsInfo campDetailsInfo, int position);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_camp_listing, parent, false);
        return new MyViewHolder(itemview);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        CampDetailsInfo campDetailsInfo = campDetailsInfoList.get(position);
        holder.txtStatus.setTextColor(mContext.getResources().getColor(R.color.teal_200));
        if (campDetailsInfo.getTstatusId() != null && campDetailsInfo.getTstatusId().equalsIgnoreCase("1"))
            holder.txtStatus.setText("INITIATED");
        if (campDetailsInfo.getTstatusId() != null && campDetailsInfo.getTstatusId().equalsIgnoreCase("2"))
            holder.txtStatus.setText("MODIFIED");
        if (campDetailsInfo.getTstatusId() != null && campDetailsInfo.getTstatusId().equalsIgnoreCase("3"))
            holder.txtStatus.setText("ORGANIZED");
        if (campDetailsInfo.getTstatusId() != null && campDetailsInfo.getTstatusId().equalsIgnoreCase("4"))
            holder.txtStatus.setText("COMPLETED");
        if (campDetailsInfo.getTstatusId() != null && campDetailsInfo.getTstatusId().equalsIgnoreCase("5"))
            holder.txtStatus.setText("UPLOADED");
        if (campDetailsInfo.getTstatusId() != null && campDetailsInfo.getTstatusId().equalsIgnoreCase("6"))
            holder.txtStatus.setText("EXPIRED");
        if (campDetailsInfo.getTstatusId() != null && campDetailsInfo.getTstatusId().equalsIgnoreCase(Constants.CAMP_STATUS_ID_COMPLETED)) {
            holder.txtStatus.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
        }
        if (campDetailsInfo.getDistrictNameEng() != null)
            holder.district.setText(campDetailsInfo.getDistrictNameEng());
        if (campDetailsInfo.getStartDate() != null)
            holder.date.setText(campDetailsInfo.getStartDate());
        if (campDetailsInfo.getBlockName() != null)
            holder.block.setText(campDetailsInfo.getBlockName().toString());
        if (campDetailsInfo.getAddress() != null)
            holder.location.setText(campDetailsInfo.getAddress());
        holder.buttonOrganise.setVisibility(View.GONE);
        holder.buttonUpload.setVisibility(View.GONE);
        holder.buttonUploadCampPhoto.setVisibility(View.GONE);
//        holder.txtUploadCount.setText("upload\nreport\n" +"("+ campDetailsInfo.getUplodedCampformCount() +")");
//        holder.txtCampPhotoCount.setText("camp\nphoto\n" +"("+ campDetailsInfo.getUplodedCampformCount() +")");
        holder.buttonDownload.setVisibility(View.GONE);
        holder.buttonViewUploaded.setVisibility(View.GONE);
        holder.buttonComplete.setVisibility(View.GONE);
        holder.buttonDeleteCamp.setVisibility(View.GONE);
        if (campDetailsInfo.getTstatusId() != null && campDetailsInfo.getTstatusId().equalsIgnoreCase(Constants.CAMP_STATUS_ID_INITIATED)) {
            PrefManager preference = new PrefManager(mContext);
           String USER_TYPE = preference.getUSER_TYPE();
            String USER_TYPE_ID = preference.getUSER_TYPE_ID();
            if (USER_TYPE_ID.equals("4") && USER_TYPE.equalsIgnoreCase("ServiceEngineer")) {
                holder.buttonDeleteCamp.setVisibility(View.VISIBLE);
                holder.buttonOrganise.setVisibility(View.VISIBLE);
            } else {
                holder.buttonDeleteCamp.setVisibility(View.VISIBLE);
            }
        }
        if (campDetailsInfo.getTstatusId() != null && campDetailsInfo.getTstatusId().equalsIgnoreCase(Constants.CAMP_STATUS_ID_ORGANISED)) {
            PrefManager   preference = new PrefManager(mContext);
            String USER_TYPE = preference.getUSER_TYPE();
            String USER_TYPE_ID = preference.getUSER_TYPE_ID();
            if (USER_TYPE_ID.equals("4") && USER_TYPE.equalsIgnoreCase("ServiceEngineer")) {
                holder.buttonUpload.setVisibility(View.VISIBLE);
                holder.buttonUploadCampPhoto.setVisibility(View.VISIBLE);
                holder.buttonDownload.setVisibility(View.VISIBLE);
                holder.buttonViewUploaded.setVisibility(View.VISIBLE);
                holder.buttonComplete.setVisibility(View.VISIBLE);
            } else {
                holder.buttonUpload.setVisibility(View.GONE);
                holder.buttonUploadCampPhoto.setVisibility(View.GONE);
                holder.buttonDownload.setVisibility(View.GONE);
                holder.buttonViewUploaded.setVisibility(View.VISIBLE);
                holder.buttonComplete.setVisibility(View.GONE);
            }
        }
        if (campDetailsInfo.getTstatusId() != null && campDetailsInfo.getTstatusId().equalsIgnoreCase(Constants.CAMP_STATUS_ID_COMPLETED)) {
            holder.buttonViewUploaded.setVisibility(View.VISIBLE);
        }
        holder.buttonOrganise.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (mOnItemOrganiseClickListener != null)
                    mOnItemOrganiseClickListener.onItemClick(campDetailsInfo, position);
            }
        });
        holder.buttonUpload.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (mOnItemUploadClickListener != null)
                    mOnItemUploadClickListener.onItemClick(campDetailsInfo, position);
            }
        });
        holder.buttonUploadCampPhoto.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (onItemUploadCampPhotoClickListener != null)
                    onItemUploadCampPhotoClickListener.onItemClick(campDetailsInfo, position);
            }
        });
        holder.buttonDownload.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (mOnItemDownloadClickListener != null)
                    mOnItemDownloadClickListener.onItemClick(campDetailsInfo, position);
            }
        });
        holder.buttonViewUploaded.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (mOnItemViewClickListener != null)
                    mOnItemViewClickListener.onItemClick(campDetailsInfo, position);
            }
        });
        holder.buttonComplete.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (mOnItemCompleteClickListener != null)
                    mOnItemCompleteClickListener.onItemClick(campDetailsInfo, position);
            }
        });
        holder.buttonDeleteCamp.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (mOnItemDeleteClickListener != null)
                    mOnItemDeleteClickListener.onItemClick(campDetailsInfo, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (campDetailsInfoList == null)
            return 0;
        else
            return campDetailsInfoList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtStatus, district, date, block, location;
        public TextView txtUploadCount, txtCampPhotoCount;
        public LinearLayout buttonDownload, buttonUpload, buttonViewUploaded, buttonDeleteCamp, buttonOrganise, buttonComplete, buttonUploadCampPhoto;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtStatus = itemView.findViewById(R.id.txtStatus);
            district = itemView.findViewById(R.id.district);
            date = itemView.findViewById(R.id.date);
            block = itemView.findViewById(R.id.block);
            location = itemView.findViewById(R.id.location);
            txtUploadCount = itemView.findViewById(R.id.txtUploadCount);
            txtCampPhotoCount = itemView.findViewById(R.id.txtCampPhotoCount);

            buttonOrganise = itemView.findViewById(R.id.buttonOrganise);
            buttonDownload = itemView.findViewById(R.id.buttonDownload);
            buttonUpload = itemView.findViewById(R.id.buttonUpload);

            buttonViewUploaded = itemView.findViewById(R.id.buttonViewUploaded);
            buttonComplete = itemView.findViewById(R.id.buttonComplete);
            buttonUploadCampPhoto = itemView.findViewById(R.id.buttonUploadCampPhoto);
            buttonDeleteCamp = itemView.findViewById(R.id.buttonDeleteCamp);
        }
    }
}