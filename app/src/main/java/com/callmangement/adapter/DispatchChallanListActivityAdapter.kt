package com.callmangement.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.R
import com.callmangement.databinding.ItemDispatchChallanListActivityBinding
import com.callmangement.model.inventrory.ModelPartsDispatchInvoiceList
import com.callmangement.ui.inventory.DispatchChallanPartsListDetailsActivity

class DispatchChallanListActivityAdapter(
    private val context: Activity,
    private val list: List<ModelPartsDispatchInvoiceList>
) : RecyclerView.Adapter<DispatchChallanListActivityAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemDispatchChallanListActivityBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_dispatch_challan_list_activity,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val model = list[position]

        holder.binding.textInvoiceNumber.text = model.invoiceId
        holder.binding.textDispatchFrom.text = model.dispatcherName
        holder.binding.textDispatchTo.text = model.districtNameEng
        holder.binding.textUsername.text = model.reciverName
        holder.binding.textInvoiceDate.text = model.dispatchDateStr
        if (!model.courierName!!.isEmpty()) {
            holder.binding.layoutCourierName.visibility = View.VISIBLE
            holder.binding.textCourierName.text = model.courierName
        } else {
            holder.binding.layoutCourierName.visibility = View.GONE
        }
        if (!model.courierTrackingNo!!.isEmpty()) {
            holder.binding.layoutCourierTrackingNumber.visibility = View.VISIBLE
            holder.binding.textCourierTrackingNumber.text = model.courierTrackingNo
        } else {
            holder.binding.layoutCourierTrackingNumber.visibility = View.GONE
        }

        if (model.isReceived.equals("true", ignoreCase = true)) {
            holder.binding.layoutReceivedStatus.visibility = View.VISIBLE
            holder.binding.layoutReceivedDate.visibility = View.VISIBLE
            holder.binding.textReceivedStatus.text = "Received"
            holder.binding.textReceivedStatus.setTextColor(context.resources.getColor(R.color.colorGreenDark))
            holder.binding.textReceivedDate.text = model.receivedDateStr
        } else {
            holder.binding.layoutReceivedStatus.visibility = View.VISIBLE
            holder.binding.layoutReceivedDate.visibility = View.GONE
            holder.binding.textReceivedStatus.text = "Not Received"
            holder.binding.textReceivedStatus.setTextColor(context.resources.getColor(R.color.colorRedDark))
        }

        if (model.isSubmitted.equals("true", ignoreCase = true)) {
            holder.binding.ivDelete.visibility = View.GONE
            holder.binding.buttonDispatch.visibility = View.GONE
            holder.binding.buttonView.visibility = View.VISIBLE
            holder.binding.textInvoiceStatus.text = "Dispatched"
            holder.binding.textInvoiceStatus.setTextColor(context.resources.getColor(R.color.colorGreenDark))
        } else {
            holder.binding.ivDelete.visibility = View.VISIBLE
            holder.binding.buttonDispatch.visibility = View.VISIBLE
            holder.binding.buttonView.visibility = View.GONE
            holder.binding.textInvoiceStatus.text = "Saved"
            holder.binding.textInvoiceStatus.setTextColor(context.resources.getColor(R.color.colorRedDark))
        }

        /*if (model.getItemStockStatusId().equals("2")){
            holder.binding.layoutDisputeStatus.setVisibility(View.VISIBLE);
            holder.binding.textDisputeItemStatus.setText(model.getItemStockStatus());
            holder.binding.textDisputeItemStatus.setTextColor(context.getResources().getColor(R.color.colorGreenDark));
        } else if (model.getItemStockStatusId().equals("7")){
            holder.binding.layoutDisputeStatus.setVisibility(View.VISIBLE);
            holder.binding.textDisputeItemStatus.setText(model.getItemStockStatus());
            holder.binding.textDisputeItemStatus.setTextColor(context.getResources().getColor(R.color.colorRedDark));
        }else if (model.getItemStockStatusId().equals("8")){
            holder.binding.layoutDisputeStatus.setVisibility(View.VISIBLE);
            holder.binding.textDisputeItemStatus.setText(model.getItemStockStatus());
            holder.binding.textDisputeItemStatus.setTextColor(context.getResources().getColor(R.color.colorRedDark));
        }else if (model.getItemStockStatusId().equals("9")){
            holder.binding.layoutDisputeStatus.setVisibility(View.VISIBLE);
            holder.binding.textDisputeItemStatus.setText(model.getItemStockStatus());
            holder.binding.textDisputeItemStatus.setTextColor(context.getResources().getColor(R.color.colorGreenDark));
        }else {
            holder.binding.layoutDisputeStatus.setVisibility(View.GONE);
        }*/
        holder.binding.buttonView.setOnClickListener { view: View? ->
            context.startActivity(
                Intent(
                    context, DispatchChallanPartsListDetailsActivity::class.java
                ).putExtra("param", model)
            )
        }

        //        holder.binding.buttonDispatch.setOnClickListener(view -> context.startActivity(new Intent(context, DispatchChallanPartsListDetailsActivity.class).putExtra("param", model)));
        /*holder.binding.ivDelete.setOnClickListener(view -> {
            if (context instanceof DispatchChallanListActivity) {
                ((DispatchChallanListActivity)context).dialogDeleteInvoice(model.getInvoiceId());
            }
        });*/
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class ViewHolder(var binding: ItemDispatchChallanListActivityBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}

