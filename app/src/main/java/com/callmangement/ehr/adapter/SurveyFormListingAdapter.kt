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
import com.callmangement.ehr.models.SurveyFormDetailsInfo
import com.callmangement.ehr.support.OnSingleClickListener
import java.text.ParseException
import java.text.SimpleDateFormat

class SurveyFormListingAdapter(
    private val mContext: Context,
    private var surveyFormDetailsInfoList: List<SurveyFormDetailsInfo>?,
    private val mOnItemUploadClickListener: OnItemUploadClickListener?,
    private val mOnItemDownloadClickListener: OnItemDownloadClickListener?,
    private val mOnItemViewClickListener: OnItemViewClickListener?,
    private val mOnItemDeleteClickListener: OnItemDeleteClickListener?,
    private val mOnItemEditClickListener: OnItemEditClickListener?
) :
    RecyclerView.Adapter<SurveyFormListingAdapter.MyViewHolder>() {
    @SuppressLint("NotifyDataSetChanged")
    fun refreshItem(campDetailsInfoList: List<SurveyFormDetailsInfo?>?) {
        this.surveyFormDetailsInfoList = surveyFormDetailsInfoList
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<SurveyFormDetailsInfo>?) {
        this.surveyFormDetailsInfoList = list
        notifyDataSetChanged()
    }

    interface OnItemUploadClickListener {
        fun onItemClick(surveyFormDetailsInfo: SurveyFormDetailsInfo?, position: Int)
    }

    interface OnItemEditClickListener {
        fun onItemClick(surveyFormDetailsInfo: SurveyFormDetailsInfo?, position: Int)
    }

    interface OnItemDownloadClickListener {
        fun onItemClick(surveyFormDetailsInfo: SurveyFormDetailsInfo?, position: Int)
    }

    interface OnItemViewClickListener {
        fun onItemClick(surveyFormDetailsInfo: SurveyFormDetailsInfo?, position: Int)
    }

    interface OnItemDeleteClickListener {
        fun onItemClick(surveyFormDetailsInfo: SurveyFormDetailsInfo?, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemview = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_survey_form_listing, parent, false)
        return MyViewHolder(itemview)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: MyViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val surveyFormDetailsInfo = surveyFormDetailsInfoList!![position]

        holder.txtStatus.setTextColor(mContext!!.resources.getColor(R.color.teal_200))

        if (surveyFormDetailsInfo.statusId != null && surveyFormDetailsInfo.statusId.equals(
                "1",
                ignoreCase = true
            )
        ) holder.txtStatus.text =
            "PENDING"
        if (surveyFormDetailsInfo.statusId != null && surveyFormDetailsInfo.statusId.equals(
                "2",
                ignoreCase = true
            )
        ) holder.txtStatus.text =
            "UPLOADED"
        if (surveyFormDetailsInfo.statusId != null && surveyFormDetailsInfo.statusId.equals(
                "3",
                ignoreCase = true
            )
        ) holder.txtStatus.text =
            "DELETED"

        if (surveyFormDetailsInfo.statusId != null && surveyFormDetailsInfo.statusId.equals(
                Constants.SURVEY_STATUS_ID_COMPLETED, ignoreCase = true
            )
        ) {
            holder.txtStatus.setTextColor(mContext!!.resources.getColor(R.color.colorGreen))
        }

        if (surveyFormDetailsInfo.customerName != null) holder.customer_name.text =
            surveyFormDetailsInfo.customerName

        if (surveyFormDetailsInfo.bill_ChallanNo != null) holder.bill_challan_no.text =
            surveyFormDetailsInfo.bill_ChallanNo

        if (surveyFormDetailsInfo.address != null) holder.customer_address.text =
            surveyFormDetailsInfo.address

        if (surveyFormDetailsInfo.typeOfCall != null) holder.type_of_call.text =
            surveyFormDetailsInfo.typeOfCall

        if (surveyFormDetailsInfo.installationDateStr != null) {
            val input = SimpleDateFormat("dd-MM-yyyy")
            val output = SimpleDateFormat("yyyy-MM-dd")
            try {
                val oneWayTripDate =
                    input.parse(surveyFormDetailsInfo.installationDateStr) // parse input
                holder.date_of_installation.text = output.format(oneWayTripDate) // format output
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }

        if (surveyFormDetailsInfo.mobileNumber != null) holder.mobile_number.text =
            surveyFormDetailsInfo.mobileNumber

        if (surveyFormDetailsInfo.customer_Remark != null) holder.customer_remarks.text =
            surveyFormDetailsInfo.customer_Remark

        holder.buttonUpload.visibility = View.GONE
        holder.buttonEdit.visibility = View.GONE
        holder.buttonDownload.visibility = View.GONE
        holder.buttonViewUploaded.visibility = View.GONE
        holder.buttonDeleteCamp.visibility = View.GONE

        if (surveyFormDetailsInfo.statusId != null && surveyFormDetailsInfo.statusId.equals(
                Constants.SURVEY_STATUS_ID_INITIATED, ignoreCase = true
            )
        ) {
            holder.buttonDeleteCamp.visibility = View.VISIBLE
        }

        holder.buttonDownload.visibility = View.VISIBLE
        holder.buttonEdit.visibility = View.VISIBLE
        holder.buttonViewUploaded.visibility = View.VISIBLE
        holder.buttonUpload.visibility = View.VISIBLE

        holder.buttonUpload.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                mOnItemUploadClickListener?.onItemClick(surveyFormDetailsInfo, position)
            }
        })

        holder.buttonDownload.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                mOnItemDownloadClickListener?.onItemClick(surveyFormDetailsInfo, position)
            }
        })

        holder.buttonViewUploaded.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                mOnItemViewClickListener?.onItemClick(surveyFormDetailsInfo, position)
            }
        })

        holder.buttonDeleteCamp.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                mOnItemDeleteClickListener?.onItemClick(surveyFormDetailsInfo, position)
            }
        })

        holder.buttonEdit.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View?) {
                mOnItemEditClickListener?.onItemClick(surveyFormDetailsInfo, position)
            }
        })
    }

    override fun getItemCount(): Int {
        return if (surveyFormDetailsInfoList == null) 0
        else surveyFormDetailsInfoList!!.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtStatus: TextView = itemView.findViewById(R.id.txtStatus)
        var customer_name: TextView = itemView.findViewById(R.id.customer_name)
        var bill_challan_no: TextView = itemView.findViewById(R.id.bill_challan_no)
        var customer_address: TextView = itemView.findViewById(R.id.customer_address)
        var type_of_call: TextView = itemView.findViewById(R.id.type_of_call)
        var date_of_installation: TextView =
            itemView.findViewById(R.id.date_of_installation)
        var mobile_number: TextView = itemView.findViewById(R.id.mobile_number)
        var customer_remarks: TextView = itemView.findViewById(R.id.customer_remarks)
        var buttonDownload: LinearLayout = itemView.findViewById(R.id.buttonDownload)
        var buttonEdit: LinearLayout = itemView.findViewById(R.id.buttonEdit)
        var buttonUpload: LinearLayout = itemView.findViewById(R.id.buttonUpload)
        var buttonViewUploaded: LinearLayout = itemView.findViewById(R.id.buttonViewUploaded)
        var buttonDeleteCamp: LinearLayout = itemView.findViewById(R.id.buttonDeleteCamp)
    }
}