package com.callmangement.ui.ins_weighing_scale.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.callmangement.R
import com.callmangement.support.OnSingleClickListener
import com.callmangement.ui.home.ZoomInZoomOutActivity
import com.callmangement.ui.ins_weighing_scale.model.InstalledDetailed.InstalledImagesDetail
import com.callmangement.utils.Constants

class ViewImageInstalledAdapterIris(
    private val mContext: Context,
    private val getErrorImagesDatumArrayList: ArrayList<InstalledImagesDetail>?,
    private val isDeliver: Boolean,
    private val count: Int
) : RecyclerView.Adapter<ViewImageInstalledAdapterIris.MyViewHolder>() {
    private val mOnItemViewClickListener: OnItemViewClickListener? = null

    interface OnItemViewClickListener {
        fun onItemClick(campDocInfo: InstalledImagesDetail?, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemview = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view_images_sec, parent, false)
        return MyViewHolder(itemview)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: MyViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val getErrorImagesDatum = getErrorImagesDatumArrayList!![position]

        val photoTypeId = getErrorImagesDatum.photoTypeId
        var imagePath: String? = null

        imagePath = getErrorImagesDatum.imagePath
        Log.d("phototypeid", "" + photoTypeId)

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

        if (isDeliver) {
            if (photoTypeId <= 5) {
                holder.mainView.visibility = View.VISIBLE
                imagePath = getErrorImagesDatum.imagePath
                Log.d("phototypeid", "" + photoTypeId)


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
                            Intent(
                                mContext,
                                ZoomInZoomOutActivity::class.java
                            ).putExtra(
                                "image",
                                Constants.API_BASE_URL + getErrorImagesDatum.imagePath
                            )
                        )
                    }
                })
            } else {
                holder.mainView.visibility = View.GONE
            }
        } else {
            if (photoTypeId > 5) {
                holder.mainView.visibility = View.VISIBLE
                imagePath = getErrorImagesDatum.imagePath
                Log.d("phototypeid", "" + photoTypeId)


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
                            Intent(
                                mContext,
                                ZoomInZoomOutActivity::class.java
                            ).putExtra(
                                "image",
                                Constants.API_BASE_URL + getErrorImagesDatum.imagePath
                            )
                        )
                    }
                })
            } else {
                holder.mainView.visibility = View.GONE
            }
        }

        // Log.d("imagepath"," ---"+Utils.Baseurl +imagePath);
    }

    override fun getItemCount(): Int {
        return getErrorImagesDatumArrayList?.size ?: 0
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ivChallanImage: ImageView =
            itemView.findViewById(R.id.ivChallanImage)
        var mainView: ConstraintLayout = itemView.findViewById(R.id.mainView)
    }
}