package com.callmangement.ehr.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.callmangement.R
import com.callmangement.ehr.models.CampDocInfo
import com.callmangement.ehr.support.OnSingleClickListener

class CampUploadedImagesListingAdapter(
    private val mContext: Context,
    private val campDocInfoList: List<CampDocInfo>?,
    private val mOnItemViewClickListener: OnItemViewClickListener?
) : RecyclerView.Adapter<CampUploadedImagesListingAdapter.MyViewHolder>() {
    interface OnItemViewClickListener {
        fun onItemClick(campDocInfo: CampDocInfo?, position: Int)
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
        val campDocInfo = campDocInfoList!![position]

        val imagePath = campDocInfo.documentPath

        if (imagePath != null && !imagePath.isEmpty() && !imagePath.equals(
                "null",
                ignoreCase = true
            )
        ) {
            holder.ivChallanImage.visibility = View.VISIBLE
            Glide.with(mContext!!).load(imagePath).placeholder(R.drawable.image_not_fount)
                .into(holder.ivChallanImage)
            Log.d("imageurl", " ---$imagePath")
        }

        holder.ivChallanImage.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                mOnItemViewClickListener?.onItemClick(campDocInfo, position)
            }
        })
    }

    override fun getItemCount(): Int {
        return campDocInfoList?.size ?: 0
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivChallanImage: ImageView = itemView.findViewById(R.id.ivChallanImage)
    }
}