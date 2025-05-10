package com.callmangement.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.R
import com.callmangement.databinding.ItemDispatchChallanListDetailsActivityBinding
import com.callmangement.model.inventrory.ModelPartsDispatchInvoiceList

class DispatchChallanPartsListDetailsActivityAdapter(
    private val context: Activity,
    private val list: List<ModelPartsDispatchInvoiceList?>
) :
    RecyclerView.Adapter<DispatchChallanPartsListDetailsActivityAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemDispatchChallanListDetailsActivityBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_dispatch_challan_list_details_activity,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val model = list[position]!!
        holder.binding.tvItemName.text = model.itemName
        holder.binding.inputDispatchQty.setText(model.dispatch_Item_Qty)
        holder.binding.inputReceiveQty.setText(model.received_Item_Qty)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class ViewHolder(var binding: ItemDispatchChallanListDetailsActivityBinding) :
        RecyclerView.ViewHolder(binding.root)
}
