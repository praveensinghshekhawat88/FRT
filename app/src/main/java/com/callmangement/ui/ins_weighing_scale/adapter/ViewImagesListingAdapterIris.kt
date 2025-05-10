package com.callmangement.ui.ins_weighing_scale.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.callmangement.R
import com.callmangement.model.WeighingDeliveryDetail.weighingDeliveryImagesDetail
import com.callmangement.support.OnSingleClickListener
import com.callmangement.ui.home.ZoomInZoomOutActivity
import com.callmangement.utils.Constants

class ViewImagesListingAdapterIris(
    private val mContext: Context,
    private val getErrorImagesDatumArrayList: ArrayList<weighingDeliveryImagesDetail>?
) : RecyclerView.Adapter<ViewImagesListingAdapterIris.MyViewHolder>() {
    private val mOnItemViewClickListener: OnItemViewClickListener? = null


    interface OnItemViewClickListener {
        fun onItemClick(campDocInfo: weighingDeliveryImagesDetail?, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemview =
            LayoutInflater.from(parent.context).inflate(R.layout.item_view_images, parent, false)
        return MyViewHolder(itemview)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: MyViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val getErrorImagesDatum = getErrorImagesDatumArrayList!![position]

        val imagePath = getErrorImagesDatum.imagePath


        if (imagePath != null && !imagePath.isEmpty()
            && !imagePath.equals("null", ignoreCase = true)
        ) {
            holder.ivChallanImage.visibility = View.VISIBLE
            Glide.with(mContext)
                .load(Constants.API_BASE_URL + imagePath)
                .placeholder(R.drawable.image_not_fount)
                .into(holder.ivChallanImage)
            Log.d("imageurl", " ---" + Constants.API_BASE_URL + imagePath)
        }
        holder.ivChallanImage.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View) {
                mOnItemViewClickListener?.onItemClick(getErrorImagesDatum, position)
                mContext.startActivity(
                    Intent(mContext, ZoomInZoomOutActivity::class.java).putExtra(
                        "image",
                        Constants.API_BASE_URL + getErrorImagesDatum.imagePath
                    )
                )
            }
        })
    }

    override fun getItemCount(): Int {
        return getErrorImagesDatumArrayList?.size ?: 0
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivChallanImage: ImageView =
            itemView.findViewById(R.id.ivChallanImage)
    }
}