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
import com.callmangement.EHR.models.SurveyFormDetailsInfo;
import com.callmangement.EHR.support.OnSingleClickListener;
import com.callmangement.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SurveyFormListingAdapter extends RecyclerView.Adapter<SurveyFormListingAdapter.MyViewHolder> {

    private final Context mContext;
    private List<SurveyFormDetailsInfo> surveyFormDetailsInfoList;
    private final OnItemUploadClickListener mOnItemUploadClickListener;
    private final OnItemDownloadClickListener mOnItemDownloadClickListener;
    private final OnItemViewClickListener mOnItemViewClickListener;
    private final OnItemDeleteClickListener mOnItemDeleteClickListener;
    private final OnItemEditClickListener mOnItemEditClickListener;

    public SurveyFormListingAdapter(Context context, List<SurveyFormDetailsInfo> surveyFormDetailsInfoList,
                                    OnItemUploadClickListener onItemUploadClickListener,
                                    OnItemDownloadClickListener onItemDownloadClickListener,
                                    OnItemViewClickListener mOnItemViewClickListener,
                                    OnItemDeleteClickListener mOnItemDeleteClickListener,
                                    OnItemEditClickListener mOnItemEditClickListener) {
        this.mContext = context;
        this.surveyFormDetailsInfoList = surveyFormDetailsInfoList;
        this.mOnItemUploadClickListener = onItemUploadClickListener;
        this.mOnItemDownloadClickListener = onItemDownloadClickListener;
        this.mOnItemViewClickListener = mOnItemViewClickListener;
        this.mOnItemDeleteClickListener = mOnItemDeleteClickListener;
        this.mOnItemEditClickListener = mOnItemEditClickListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void refreshItem(List<SurveyFormDetailsInfo> campDetailsInfoList) {
        this.surveyFormDetailsInfoList = surveyFormDetailsInfoList;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<SurveyFormDetailsInfo> list) {
        this.surveyFormDetailsInfoList = list;
        notifyDataSetChanged();
    }

    public interface OnItemUploadClickListener {
        void onItemClick(SurveyFormDetailsInfo surveyFormDetailsInfo, int position);
    }

    public interface OnItemEditClickListener {
        void onItemClick(SurveyFormDetailsInfo surveyFormDetailsInfo, int position);
    }

    public interface OnItemDownloadClickListener {
        void onItemClick(SurveyFormDetailsInfo surveyFormDetailsInfo, int position);
    }

    public interface OnItemViewClickListener {
        void onItemClick(SurveyFormDetailsInfo surveyFormDetailsInfo, int position);
    }

    public interface OnItemDeleteClickListener {
        void onItemClick(SurveyFormDetailsInfo surveyFormDetailsInfo, int position);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_survey_form_listing, parent, false);
        return new MyViewHolder(itemview);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        SurveyFormDetailsInfo surveyFormDetailsInfo = surveyFormDetailsInfoList.get(position);

        holder.txtStatus.setTextColor(mContext.getResources().getColor(R.color.teal_200));

        if (surveyFormDetailsInfo.getStatusId() != null && surveyFormDetailsInfo.getStatusId().equalsIgnoreCase("1"))
            holder.txtStatus.setText("PENDING");
        if (surveyFormDetailsInfo.getStatusId() != null && surveyFormDetailsInfo.getStatusId().equalsIgnoreCase("2"))
            holder.txtStatus.setText("UPLOADED");
        if (surveyFormDetailsInfo.getStatusId() != null && surveyFormDetailsInfo.getStatusId().equalsIgnoreCase("3"))
            holder.txtStatus.setText("DELETED");

        if (surveyFormDetailsInfo.getStatusId() != null && surveyFormDetailsInfo.getStatusId().equalsIgnoreCase(Constants.SURVEY_STATUS_ID_COMPLETED)) {
            holder.txtStatus.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
        }

        if (surveyFormDetailsInfo.getCustomerName() != null)
            holder.customer_name.setText(surveyFormDetailsInfo.getCustomerName());

        if (surveyFormDetailsInfo.getBill_ChallanNo() != null)
            holder.bill_challan_no.setText(surveyFormDetailsInfo.getBill_ChallanNo());

        if (surveyFormDetailsInfo.getAddress() != null)
            holder.customer_address.setText(surveyFormDetailsInfo.getAddress());

        if (surveyFormDetailsInfo.getTypeOfCall() != null)
            holder.type_of_call.setText(surveyFormDetailsInfo.getTypeOfCall());

        if (surveyFormDetailsInfo.getInstallationDateStr() != null) {
            SimpleDateFormat input = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date oneWayTripDate = input.parse(surveyFormDetailsInfo.getInstallationDateStr());                 // parse input
                holder.date_of_installation.setText(output.format(oneWayTripDate));    // format output
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (surveyFormDetailsInfo.getMobileNumber() != null)
            holder.mobile_number.setText(surveyFormDetailsInfo.getMobileNumber());

        if (surveyFormDetailsInfo.getCustomer_Remark() != null)
            holder.customer_remarks.setText(surveyFormDetailsInfo.getCustomer_Remark());

        holder.buttonUpload.setVisibility(View.GONE);
        holder.buttonEdit.setVisibility(View.GONE);
        holder.buttonDownload.setVisibility(View.GONE);
        holder.buttonViewUploaded.setVisibility(View.GONE);
        holder.buttonDeleteCamp.setVisibility(View.GONE);

        if (surveyFormDetailsInfo.getStatusId() != null && surveyFormDetailsInfo.getStatusId().equalsIgnoreCase(Constants.SURVEY_STATUS_ID_INITIATED)) {
            holder.buttonDeleteCamp.setVisibility(View.VISIBLE);
        }

        holder.buttonDownload.setVisibility(View.VISIBLE);
        holder.buttonEdit.setVisibility(View.VISIBLE);
        holder.buttonViewUploaded.setVisibility(View.VISIBLE);
        holder.buttonUpload.setVisibility(View.VISIBLE);

        holder.buttonUpload.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (mOnItemUploadClickListener != null)
                    mOnItemUploadClickListener.onItemClick(surveyFormDetailsInfo, position);
            }
        });

        holder.buttonDownload.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (mOnItemDownloadClickListener != null)
                    mOnItemDownloadClickListener.onItemClick(surveyFormDetailsInfo, position);
            }
        });

        holder.buttonViewUploaded.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (mOnItemViewClickListener != null)
                    mOnItemViewClickListener.onItemClick(surveyFormDetailsInfo, position);
            }
        });

        holder.buttonDeleteCamp.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (mOnItemDeleteClickListener != null)
                    mOnItemDeleteClickListener.onItemClick(surveyFormDetailsInfo, position);
            }
        });

        holder.buttonEdit.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (mOnItemEditClickListener != null)
                    mOnItemEditClickListener.onItemClick(surveyFormDetailsInfo, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (surveyFormDetailsInfoList == null)
            return 0;
        else
            return surveyFormDetailsInfoList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtStatus, customer_name, bill_challan_no, customer_address, type_of_call,
                date_of_installation, mobile_number, customer_remarks;
        public LinearLayout buttonDownload, buttonEdit, buttonUpload, buttonViewUploaded, buttonDeleteCamp;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtStatus = itemView.findViewById(R.id.txtStatus);
            customer_name = itemView.findViewById(R.id.customer_name);
            bill_challan_no = itemView.findViewById(R.id.bill_challan_no);
            customer_address = itemView.findViewById(R.id.customer_address);
            type_of_call = itemView.findViewById(R.id.type_of_call);
            date_of_installation = itemView.findViewById(R.id.date_of_installation);
            mobile_number = itemView.findViewById(R.id.mobile_number);
            customer_remarks = itemView.findViewById(R.id.customer_remarks);

            buttonUpload = itemView.findViewById(R.id.buttonUpload);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDownload = itemView.findViewById(R.id.buttonDownload);
            buttonViewUploaded = itemView.findViewById(R.id.buttonViewUploaded);
            buttonDeleteCamp = itemView.findViewById(R.id.buttonDeleteCamp);
        }
    }
}