package com.callmangement.ehr.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.callmangement.R
import com.callmangement.ehr.models.SurveyFormDocInfo
import com.callmangement.ehr.support.OnSingleClickListener

class SurveyFormUploadedImagesListingAdapter(
    private val mContext: Context,
    private val surveyFormDocInfos: List<SurveyFormDocInfo>?,
    private val mOnItemViewClickListener: OnItemViewClickListener?
) : RecyclerView.Adapter<SurveyFormUploadedImagesListingAdapter.MyViewHolder>() {
    interface OnItemViewClickListener {
        fun onItemClick(surveyFormDocInfo: SurveyFormDocInfo?, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemview = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_uploaded_view_images, parent, false)
        return MyViewHolder(itemview)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: MyViewHolder, @SuppressLint("RecyclerView") position: Int
    ) {
        val surveyFormDocInfo = surveyFormDocInfos!![position]

        val imagePath = surveyFormDocInfo.documentPath

        if (imagePath != null && !imagePath.isEmpty() && !imagePath.equals(
                "null",
                ignoreCase = true
            )
        ) {
            holder.ivChallanImage.visibility = View.VISIBLE
            Glide.with(mContext!!).load(imagePath).placeholder(R.drawable.image_not_fount)
                .into(holder.ivChallanImage)
        }

        holder.ivChallanImage.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                mOnItemViewClickListener?.onItemClick(surveyFormDocInfo, position)
            }
        })
    }

    override fun getItemCount(): Int {
        return surveyFormDocInfos?.size ?: 0
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivChallanImage: ImageView = itemView.findViewById(R.id.ivChallanImage)
    }
}