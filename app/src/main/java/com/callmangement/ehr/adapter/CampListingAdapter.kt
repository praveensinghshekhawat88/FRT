package com.callmangement.ehr.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.R
import com.callmangement.ehr.api.Constants
import com.callmangement.ehr.models.CampDetailsInfo
import com.callmangement.ehr.support.OnSingleClickListener
import com.callmangement.utils.PrefManager

class CampListingAdapter(

    private val mContext: Context,
    private var campDetailsInfoList: List<CampDetailsInfo>?,
    private val mOnItemCampListClickListener: OnItemCampListClickListener?
) : RecyclerView.Adapter<CampListingAdapter.MyViewHolder>() {
    @SuppressLint("NotifyDataSetChanged")
    fun refreshItem(campDetailsInfoList: List<CampDetailsInfo>?) {
        this.campDetailsInfoList = campDetailsInfoList
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<CampDetailsInfo>?) {
        this.campDetailsInfoList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemview =
            LayoutInflater.from(parent.context).inflate(R.layout.item_camp_listing, parent, false)
        return MyViewHolder(itemview)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: MyViewHolder, @SuppressLint("RecyclerView") position: Int
    ) {
        val campDetailsInfo = campDetailsInfoList!![position]
        holder.txtStatus.setTextColor(mContext.resources.getColor(R.color.teal_200))
        if (campDetailsInfo.tstatusId != null && campDetailsInfo.tstatusId.equals(
                "1", ignoreCase = true
            )
        ) holder.txtStatus.text = "INITIATED"
        if (campDetailsInfo.tstatusId != null && campDetailsInfo.tstatusId.equals(
                "2", ignoreCase = true
            )
        ) holder.txtStatus.text = "MODIFIED"
        if (campDetailsInfo.tstatusId != null && campDetailsInfo.tstatusId.equals(
                "3", ignoreCase = true
            )
        ) holder.txtStatus.text = "ORGANIZED"
        if (campDetailsInfo.tstatusId != null && campDetailsInfo.tstatusId.equals(
                "4", ignoreCase = true
            )
        ) holder.txtStatus.text = "COMPLETED"
        if (campDetailsInfo.tstatusId != null && campDetailsInfo.tstatusId.equals(
                "5", ignoreCase = true
            )
        ) holder.txtStatus.text = "UPLOADED"
        if (campDetailsInfo.tstatusId != null && campDetailsInfo.tstatusId.equals(
                "6", ignoreCase = true
            )
        ) holder.txtStatus.text = "EXPIRED"
        if (campDetailsInfo.tstatusId != null && campDetailsInfo.tstatusId.equals(
                Constants.CAMP_STATUS_ID_COMPLETED, ignoreCase = true
            )
        ) {
            holder.txtStatus.setTextColor(mContext.resources.getColor(R.color.colorGreen))
        }
        if (campDetailsInfo.districtNameEng != null) holder.district.text =
            campDetailsInfo.districtNameEng
        if (campDetailsInfo.startDate != null) holder.date.text = campDetailsInfo.startDate
        if (campDetailsInfo.blockName != null) holder.block.text =
            campDetailsInfo.blockName.toString()
        if (campDetailsInfo.address != null) holder.location.text = campDetailsInfo.address
        holder.buttonOrganise.visibility = View.GONE
        holder.buttonUpload.visibility = View.GONE
        holder.buttonUploadCampPhoto.visibility = View.GONE
        //        holder.txtUploadCount.setText("upload\nreport\n" +"("+ campDetailsInfo.getUplodedCampformCount() +")");
//        holder.txtCampPhotoCount.setText("camp\nphoto\n" +"("+ campDetailsInfo.getUplodedCampformCount() +")");
        holder.buttonDownload.visibility = View.GONE
        holder.buttonViewUploaded.visibility = View.GONE
        holder.buttonComplete.visibility = View.GONE
        holder.buttonDeleteCamp.visibility = View.GONE
        if (campDetailsInfo.tstatusId != null && campDetailsInfo.tstatusId.equals(
                Constants.CAMP_STATUS_ID_INITIATED, ignoreCase = true
            )
        ) {
            val preference = PrefManager(mContext)
            val USER_TYPE = preference.useR_TYPE
            val USER_TYPE_ID = preference.useR_TYPE_ID
            if (USER_TYPE_ID == "4" && USER_TYPE.equals("ServiceEngineer", ignoreCase = true)) {
                holder.buttonDeleteCamp.visibility = View.VISIBLE
                holder.buttonOrganise.visibility = View.VISIBLE
            } else {
                holder.buttonDeleteCamp.visibility = View.VISIBLE
            }
        }
        if (campDetailsInfo.tstatusId != null && campDetailsInfo.tstatusId.equals(
                Constants.CAMP_STATUS_ID_ORGANISED, ignoreCase = true
            )
        ) {
            val preference = PrefManager(mContext)
            val USER_TYPE = preference.useR_TYPE
            val USER_TYPE_ID = preference.useR_TYPE_ID
            if (USER_TYPE_ID == "4" && USER_TYPE.equals("ServiceEngineer", ignoreCase = true)) {
                holder.buttonUpload.visibility = View.VISIBLE
                holder.buttonUploadCampPhoto.visibility = View.VISIBLE
                holder.buttonDownload.visibility = View.VISIBLE
                holder.buttonViewUploaded.visibility = View.VISIBLE
                holder.buttonComplete.visibility = View.VISIBLE
            } else {
                holder.buttonUpload.visibility = View.GONE
                holder.buttonUploadCampPhoto.visibility = View.GONE
                holder.buttonDownload.visibility = View.GONE
                holder.buttonViewUploaded.visibility = View.VISIBLE
                holder.buttonComplete.visibility = View.GONE
            }
        }
        if (campDetailsInfo.tstatusId != null && campDetailsInfo.tstatusId.equals(
                Constants.CAMP_STATUS_ID_COMPLETED, ignoreCase = true
            )
        ) {
            holder.buttonViewUploaded.visibility = View.VISIBLE
        }
        holder.buttonOrganise.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
            //    mOnItemOrganiseClickListener?.onItemClick(campDetailsInfo, position)
                mOnItemCampListClickListener?.onItemClick(
                    campDetailsInfo,
                    position,
                    CampAction.ORGANISE_CAMP
                )
            }
        })
        holder.buttonUpload.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
            //    mOnItemUploadClickListener?.onItemClick(campDetailsInfo, position)
                mOnItemCampListClickListener?.onItemClick(
                    campDetailsInfo,
                    position,
                    CampAction.UPLOAD_REPORT
                )
            }
        })
        holder.buttonUploadCampPhoto.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
            //    onItemUploadCampPhotoClickListener?.onItemClick(campDetailsInfo, position)
                mOnItemCampListClickListener?.onItemClick(
                    campDetailsInfo,
                    position,
                    CampAction.UPLOAD_CAMP_PHOTO
                )
            }
        })
        holder.buttonDownload.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
            //    mOnItemDownloadClickListener?.onItemClick(campDetailsInfo, position)
                mOnItemCampListClickListener?.onItemClick(
                    campDetailsInfo,
                    position,
                    CampAction.DOWNLOAD
                )
            }
        })
        holder.buttonViewUploaded.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                //    mOnItemViewClickListener?.onItemClick(campDetailsInfo, position)
                mOnItemCampListClickListener?.onItemClick(
                    campDetailsInfo,
                    position,
                    CampAction.VIEW_DETAILS
                )
            }
        })
        holder.buttonComplete.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
            //    mOnItemCompleteClickListener?.onItemClick(campDetailsInfo, position)
                mOnItemCampListClickListener?.onItemClick(
                    campDetailsInfo,
                    position,
                    CampAction.COMPLETE_CAMP
                )
            }
        })
        holder.buttonDeleteCamp.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
            //    mOnItemDeleteClickListener?.onItemClick(campDetailsInfo, position)
                mOnItemCampListClickListener?.onItemClick(
                    campDetailsInfo,
                    position,
                    CampAction.DELETE_CAMP
                )
            }
        })
    }

    override fun getItemCount(): Int {
        return if (campDetailsInfoList == null) 0
        else campDetailsInfoList!!.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtStatus: TextView = itemView.findViewById(R.id.txtStatus)
        var district: TextView = itemView.findViewById(R.id.district)
        var date: TextView = itemView.findViewById(R.id.date)
        var block: TextView = itemView.findViewById(R.id.block)
        var location: TextView = itemView.findViewById(R.id.location)
        var txtUploadCount: TextView = itemView.findViewById(R.id.txtUploadCount)
        var txtCampPhotoCount: TextView = itemView.findViewById(R.id.txtCampPhotoCount)
        var buttonDownload: LinearLayout = itemView.findViewById(R.id.buttonDownload)
        var buttonUpload: LinearLayout = itemView.findViewById(R.id.buttonUpload)
        var buttonViewUploaded: LinearLayout = itemView.findViewById(R.id.buttonViewUploaded)
        var buttonDeleteCamp: LinearLayout = itemView.findViewById(R.id.buttonDeleteCamp)
        var buttonOrganise: LinearLayout = itemView.findViewById(R.id.buttonOrganise)
        var buttonComplete: LinearLayout = itemView.findViewById(R.id.buttonComplete)
        var buttonUploadCampPhoto: LinearLayout = itemView.findViewById(R.id.buttonUploadCampPhoto)
    }

    enum class CampAction {
        ORGANISE_CAMP,
        UPLOAD_REPORT,
        UPLOAD_CAMP_PHOTO,
        DOWNLOAD,
        VIEW_DETAILS,
        COMPLETE_CAMP,
        DELETE_CAMP
    }

    // Interface for item click
    interface OnItemCampListClickListener {
        fun onItemClick(campDetailsInfo: CampDetailsInfo?, position: Int, action: CampAction)
    }
}