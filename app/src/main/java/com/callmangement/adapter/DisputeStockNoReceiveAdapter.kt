package com.callmangement.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.R
import com.callmangement.databinding.ItemDisputeStockNoReceiveBinding
import com.callmangement.model.inventrory.ModelDisputePartsList

class DisputeStockNoReceiveAdapter(
    private val context: Activity,
    private val modelDisputePartsList: List<ModelDisputePartsList>
) : RecyclerView.Adapter<DisputeStockNoReceiveAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemDisputeStockNoReceiveBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_dispute_stock_no_receive,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = modelDisputePartsList[position]
        holder.binding.tvItemName.text = model.itemName
        holder.binding.inputQuantity.setText(model.item_Qty)
    }

    override fun getItemCount(): Int {
        return modelDisputePartsList.size
    }

    class ViewHolder(val binding: ItemDisputeStockNoReceiveBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}
