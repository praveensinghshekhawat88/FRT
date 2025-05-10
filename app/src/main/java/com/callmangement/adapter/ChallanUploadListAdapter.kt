package com.callmangement.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.R
import com.callmangement.databinding.ItemChallanUploadListBinding
import com.callmangement.model.complaints.ModelComplaintList
import com.callmangement.ui.complaint.ChallanUploadActivity
import com.callmangement.utils.PrefManager
import java.util.Locale

class ChallanUploadListAdapter(private val context: Context) :
    RecyclerView.Adapter<ChallanUploadListAdapter.ViewHolder>(),
    Filterable {
    private var row_index = 0
    private val REQUEST_CODE = 1
    private var modelComplaintList: MutableList<ModelComplaintList>? = null
    private var modelComplaintFilterList: MutableList<ModelComplaintList>? = null
    private val prefManager = PrefManager(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemChallanUploadListBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_challan_upload_list,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val model = modelComplaintList!![position]
        holder.binding.data = model

        holder.binding.textComplaintStatus.text =
            context.resources.getString(R.string.complaint_status) + " : "

        holder.binding.textComplaintNumber.text =
            context.resources.getString(R.string.c_no) + " : " + model.complainRegNo
        holder.binding.textName.text =
            context.resources.getString(R.string.name) + " : " + model.customerName + " " + "(" + context.resources.getString(
                R.string.fps_code
            ) + " : " + model.fpscode + ")"
        holder.binding.textPhoneNumber.text =
            context.resources.getString(R.string.mobile_no) + " : " + model.mobileNo
        holder.binding.textServiceCenterCount.text =
            context.resources.getString(R.string.count_on_service_center) + " : " + model.cntReptOnSerCenter
        holder.binding.textDis.text = model.district

        holder.binding.textComplaintStatusValue.text = model.complainStatus
        holder.binding.textComplaintStatusValue.setTextColor(context.resources.getColor(R.color.colorRedDark))

        if (model.complainRegDateStr != null && !model.complainRegDateStr!!.isEmpty() && !model.complainRegDateStr.equals(
                "null",
                ignoreCase = true
            )
        ) {
            holder.binding.textComplaintRegDate.visibility = View.VISIBLE
            holder.binding.textComplaintRegDate.text =
                context.resources.getString(R.string.reg_date) + " : " + model.complainRegDateStr
        } else {
            holder.binding.textComplaintRegDate.visibility = View.GONE
        }

        holder.binding.listItem.setOnClickListener { view: View ->
            row_index = position
            notifyDataSetChanged()
            val intent = Intent(view.context, ChallanUploadActivity::class.java)
            intent.putExtra("param", model)
            intent.putExtra("param2", "pending")
            prefManager.cOMPLAINT_POSITION = position
            (view.context as Activity).startActivityForResult(intent, REQUEST_CODE)
        }

        if (row_index == position) {
            holder.binding.listItem.background =
                context.getDrawable(R.drawable.shape_rect_background_dark_blue_layout)
            holder.binding.textName.setTextColor(context.resources.getColor(R.color.white))
            holder.binding.textPhoneNumber.setTextColor(context.resources.getColor(R.color.white))
            holder.binding.textComplaintNumber.setTextColor(context.resources.getColor(R.color.white))
            holder.binding.textDis.setTextColor(context.resources.getColor(R.color.white))
            holder.binding.textTehsil.setTextColor(context.resources.getColor(R.color.white))
            holder.binding.textComplaintRegDate.setTextColor(context.resources.getColor(R.color.white))
            holder.binding.textComplaintStatus.setTextColor(context.resources.getColor(R.color.white))
            holder.binding.textServiceCenterCount.setTextColor(context.resources.getColor(R.color.white))
        } else {
            holder.binding.listItem.background =
                context.getDrawable(R.drawable.shape_rect_background_white_layout)
            holder.binding.textName.setTextColor(context.resources.getColor(R.color.grayDark))
            holder.binding.textPhoneNumber.setTextColor(context.resources.getColor(R.color.grayDark))
            holder.binding.textComplaintNumber.setTextColor(context.resources.getColor(R.color.colorActionBar))
            holder.binding.textDis.setTextColor(context.resources.getColor(R.color.grayDark))
            holder.binding.textTehsil.setTextColor(context.resources.getColor(R.color.blue_700))
            holder.binding.textComplaintRegDate.setTextColor(context.resources.getColor(R.color.grayDark))
            holder.binding.textComplaintStatus.setTextColor(context.resources.getColor(R.color.grayDark))
            holder.binding.textServiceCenterCount.setTextColor(context.resources.getColor(R.color.grayDark))
        }
    }


    /* @SuppressLint("NotifyDataSetChanged")
    public void setData(List<ModelComplaintList> modelComplaintList){
        this.modelComplaintList = modelComplaintList;
        this.modelComplaintFilterList = new ArrayList<>(modelComplaintList);
        notifyDataSetChanged();
    }*/
    @SuppressLint("NotifyDataSetChanged")
    fun setData(modelComplaintList: List<ModelComplaintList>) {
        this.modelComplaintList = ArrayList(modelComplaintList) // correct way
        this.modelComplaintFilterList = ArrayList(modelComplaintList)
        notifyDataSetChanged()
    }

    fun addData(newItems: List<ModelComplaintList>) {
        val startPosition = modelComplaintList!!.size
        modelComplaintList!!.addAll(newItems)
        modelComplaintFilterList!!.addAll(newItems)
        notifyItemRangeInserted(startPosition, newItems.size)
    }


    override fun getItemCount(): Int {
        return if (modelComplaintList != null) {
            modelComplaintList!!.size
        } else {
            0
        }
    }

    class ViewHolder(var binding: ItemChallanUploadListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getFilter(): Filter {
        return Searched_Filter
    }

    private val Searched_Filter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList = ArrayList<ModelComplaintList>()
            if (constraint == null || constraint.length == 0) {
                filteredList.addAll(modelComplaintFilterList!!)
            } else {
                val filterPattern =
                    constraint.toString().lowercase(Locale.getDefault()).trim { it <= ' ' }
                for (item in modelComplaintFilterList!!) {
                    if (item.complainRegNo!!.lowercase(Locale.getDefault())
                            .contains(filterPattern) || item.customerName!!.lowercase(
                            Locale.getDefault()
                        ).contains(filterPattern) || item.fpscode!!.lowercase(
                            Locale.getDefault()
                        ).contains(filterPattern)
                    ) {
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            if (results.values != null) {
                modelComplaintList!!.clear()
                modelComplaintList!!.addAll(results.values as Collection<ModelComplaintList>)
                notifyDataSetChanged()
            }
        }
    }
}

