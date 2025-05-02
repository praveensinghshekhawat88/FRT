package com.callmangement.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.R
import com.callmangement.databinding.ItemSlaReportsActivityBinding
import com.callmangement.model.reports.SLA_Reports_Info
import com.callmangement.ui.reports.SLAReportsDetailsActivity

class SLAReportsActivityAdapter(private val context: Context) :
    RecyclerView.Adapter<SLAReportsActivityAdapter.ViewHolder>() {
    private var slaReportsInfos: List<SLA_Reports_Info>? = null
    var days: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemSlaReportsActivityBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_sla_reports_activity,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val model = slaReportsInfos!![position]
        holder.binding.data = model

        holder.binding.crdItem.setOnClickListener { view: View ->
            view.context.startActivity(
                Intent(view.context, SLAReportsDetailsActivity::class.java)
                    .putExtra("districtId", model.districtId)
                    .putExtra("intervalDays", days)
            )
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(monthlyReportsInfos: List<SLA_Reports_Info>?) {
        this.slaReportsInfos = monthlyReportsInfos
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (slaReportsInfos != null) {
            slaReportsInfos!!.size
        } else {
            0
        }
    }

    class ViewHolder(val binding: ItemSlaReportsActivityBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}