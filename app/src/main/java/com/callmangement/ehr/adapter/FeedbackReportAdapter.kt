package com.callmangement.ehr.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.databinding.ItemFeedbacklistbydcBinding
import com.callmangement.ehr.ehrActivities.FeedBackReportDetail
import com.callmangement.ehr.models.FeedbackbyDCListDatum

class FeedbackReportAdapter(
    private val feedbackbyDCListDatumArrayList: ArrayList<FeedbackbyDCListDatum>,
    private val context: Context
) :
    RecyclerView.Adapter<FeedbackReportAdapter.ViewHolder>() {
    // data is passed into the constructor
    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_installedlistl, parent, false);
        //    return new ViewHolder(view);

        val itemFeedbacklistbydcBinding = ItemFeedbacklistbydcBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemFeedbacklistbydcBinding)
    }

    // binds the data to the TextView in each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = feedbackbyDCListDatumArrayList[position]

        // holder.binding.tvDealername.setText(model.getDealerName());
        holder.binding.tvFpscode.text = model.fpscode
        holder.binding.dis.text = model.districtName
        // holder.binding.tvDealermobno.setText(model.getDealerMobileNo());
        holder.binding.tvFeedbackid.text = model.feedBackId.toString()
        holder.binding.tvFeedbackby.text = model.feedBackBy

        /* if (status.equals("Yes") & status.length() != 0) {
            holder.tv_status.setTextColor(Color.parseColor("#43A047"));
            holder.tv_status.setText(status);
        } else if (status.equals("No") & status.length() != 0) {
            holder.tv_status.setTextColor(Color.parseColor("#F44336"));
            holder.tv_status.setText(status);
        } else {
        }*/
        holder.binding.rlItem.setOnClickListener { view: View? ->
            val intent = Intent(context, FeedBackReportDetail::class.java)
            intent.putExtra("paramm", model)
            context.startActivity(intent)
        }
    }

    // total number of rows
    override fun getItemCount(): Int {
        return feedbackbyDCListDatumArrayList.size
    }

    // stores and recycles views as they are scrolled off screen
    class ViewHolder(val binding: ItemFeedbacklistbydcBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        override fun onClick(view: View) {
        }
    }
}
