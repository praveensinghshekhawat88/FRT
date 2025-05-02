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
import com.callmangement.databinding.ItemReceiveMaterialListBinding
import com.callmangement.model.inventrory.ModelPartsDispatchInvoiceList
import com.callmangement.ui.inventory.ReceiveMaterialListDetailsActivity

class ReceiveMaterialListActivityAdapter(
    private val context: Context,
    private val list: List<ModelPartsDispatchInvoiceList>
) : RecyclerView.Adapter<ReceiveMaterialListActivityAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemReceiveMaterialListBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_receive_material_list,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val separator =
            list[position].dispatchDateStr!!.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
        val dateStr = separator[0]
        holder.binding.textInvoiceNumber.text = list[position].invoiceId
        holder.binding.textInvoiceDate.text = list[position].dispatchDateStr
        holder.binding.tvSenderName.text = "Send by " + list[position].dispatcherName
        holder.binding.textRemarkStr.text =
            list[position].dispatcherName + " " + context.resources.getString(R.string.remark)
        //        holder.binding.textRemark.setText(""+list.get(position).getDispatcherRemarks());
        holder.binding.textCourierName.text = list[position].courierName
        holder.binding.textCourierTrackingNumber.text = list[position].courierTrackingNo

        if (!list[position].courierName!!.isEmpty()) {
            holder.binding.layoutCourierName.visibility = View.VISIBLE
            holder.binding.textCourierName.text = list[position].courierName
        } else {
            holder.binding.layoutCourierName.visibility = View.GONE
        }

        if (!list[position].courierTrackingNo!!.isEmpty()) {
            holder.binding.layoutCourierTrackingNumber.visibility = View.VISIBLE
            holder.binding.textCourierTrackingNumber.text = list[position].courierTrackingNo
        } else {
            holder.binding.layoutCourierTrackingNumber.visibility = View.GONE
        }

        /*if (list.get(position).getItemStockStatusId().equals("7")){
            holder.binding.layoutDisputeStatus.setVisibility(View.VISIBLE);
            holder.binding.textDisputeStatus.setText(list.get(position).getItemStockStatus());
            holder.binding.textDisputeStatus.setTextColor(context.getResources().getColor(R.color.colorRedDark));
        }else {
            holder.binding.layoutDisputeStatus.setVisibility(View.GONE);
        }*/
        if (list[position].isReceived.equals("true", ignoreCase = true)) {
            holder.binding.layoutReceivedDate.visibility = View.VISIBLE
            holder.binding.textReceivedDate.text = list[position].receivedDateStr
            holder.binding.textInvoiceStatus.text = "Received"
            holder.binding.textInvoiceStatus.setTextColor(context.resources.getColor(R.color.colorGreenDark))

            holder.binding.buttonReceive.visibility = View.GONE
            holder.binding.buttonView.visibility = View.VISIBLE
        } else if (list[position].isReceived.equals("false", ignoreCase = true)) {
            holder.binding.layoutReceivedDate.visibility = View.GONE
            holder.binding.textInvoiceStatus.text = "Not Received"
            holder.binding.textInvoiceStatus.setTextColor(context.resources.getColor(R.color.colorRedDark))

            holder.binding.buttonReceive.visibility = View.VISIBLE
            holder.binding.buttonView.visibility = View.GONE
        }

        holder.binding.buttonReceive.setOnClickListener { view: View? ->
            val intent = Intent(context, ReceiveMaterialListDetailsActivity::class.java)
            intent.putExtra("invoice_id", list[position].invoiceId)
            intent.putExtra("dispatch_id", list[position].dispatchId)
            intent.putExtra("date", dateStr)
            intent.putExtra("param", list[position])
            context.startActivity(intent)
        }

        holder.binding.buttonView.setOnClickListener { view: View? ->
            val intent = Intent(context, ReceiveMaterialListDetailsActivity::class.java)
            intent.putExtra("invoice_id", list[position].invoiceId)
            intent.putExtra("dispatch_id", list[position].dispatchId)
            intent.putExtra("date", dateStr)
            intent.putExtra("param", list[position])
            context.startActivity(intent)
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(val binding: ItemReceiveMaterialListBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}
