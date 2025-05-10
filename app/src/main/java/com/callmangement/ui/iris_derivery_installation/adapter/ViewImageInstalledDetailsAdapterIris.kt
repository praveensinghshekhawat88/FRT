package com.callmangement.ui.iris_derivery_installation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.callmangement.R
import com.callmangement.databinding.ItemViewImagesDtlSecBinding
import com.callmangement.support.OnSingleClickListener
import com.callmangement.ui.home.ZoomInZoomOutActivity
import com.callmangement.ui.iris_derivery_installation.Model.IrisInstalledImagesDtl
import com.callmangement.utils.Constants

class ViewImageInstalledDetailsAdapterIris(
    private val mContext: Context,
    private val getErrorImagesDatumArrayList: ArrayList<IrisInstalledImagesDtl>?,
    private val isDeliver: Boolean,
    private val count: Int
) : RecyclerView.Adapter<ViewImageInstalledDetailsAdapterIris.MyViewHolder>() {
    private val mOnItemViewClickListener: OnItemViewClickListener? = null

    interface OnItemViewClickListener {
        fun onItemClick(campDocInfo: IrisInstalledImagesDtl?, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemViewImagesDtlSecBinding =
            ItemViewImagesDtlSecBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemViewImagesDtlSecBinding)
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
            holder.binding.ivChallanImage.visibility = View.VISIBLE
            Glide.with(mContext)
                .load(Constants.API_BASE_URL + imagePath)
                .placeholder(R.drawable.image_not_fount)
                .into(holder.binding.ivChallanImage)
            Log.d("imageurl", " ---" + Constants.API_BASE_URL + imagePath)
        }

        holder.binding.ivChallanImage.setOnClickListener(object : OnSingleClickListener() {
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
                holder.binding.mainView.visibility = View.VISIBLE
                imagePath = getErrorImagesDatum.imagePath
                Log.d("phototypeid", "" + photoTypeId)

                if (imagePath != null && !imagePath.isEmpty()
                    && !imagePath.equals("null", ignoreCase = true)
                ) {
                    holder.binding.ivChallanImage.visibility = View.VISIBLE
                    Glide.with(mContext)
                        .load(Constants.API_BASE_URL + imagePath)
                        .placeholder(R.drawable.image_not_fount)
                        .into(holder.binding.ivChallanImage)
                    Log.d("imageurl", " ---" + Constants.API_BASE_URL + imagePath)
                }
                holder.binding.ivChallanImage.setOnClickListener(object : OnSingleClickListener() {
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
                holder.binding.mainView.visibility = View.GONE
            }
        } else {
            if (photoTypeId > 5) {
                holder.binding.mainView.visibility = View.VISIBLE
                imagePath = getErrorImagesDatum.imagePath
                Log.d("phototypeid", "" + photoTypeId)
                if (imagePath != null && !imagePath.isEmpty()
                    && !imagePath.equals("null", ignoreCase = true)
                ) {
                    holder.binding.ivChallanImage.visibility = View.VISIBLE
                    Glide.with(mContext)
                        .load(Constants.API_BASE_URL + imagePath)
                        .placeholder(R.drawable.image_not_fount)
                        .into(holder.binding.ivChallanImage)
                    Log.d("imageurl", " ---" + Constants.API_BASE_URL + imagePath)
                }
                holder.binding.ivChallanImage.setOnClickListener(object : OnSingleClickListener() {
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
                holder.binding.mainView.visibility = View.GONE
            }
        }
        // Log.d("imagepath"," ---"+Utils.Baseurl +imagePath);
    }

    override fun getItemCount(): Int {
        return getErrorImagesDatumArrayList?.size ?: 0
    }

    class MyViewHolder(val binding: ItemViewImagesDtlSecBinding) :
        RecyclerView.ViewHolder(binding.root)
}
