package com.callmangement.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.R
import com.callmangement.databinding.ItemDialogChallanForDispatchBinding
import com.callmangement.model.inventrory.ModelDisputePartsList

class DialogChallanForDisputeDispatchAdapter(
    private val context: Activity,
    private val list: List<ModelDisputePartsList>
) : RecyclerView.Adapter<DialogChallanForDisputeDispatchAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemDialogChallanForDispatchBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_dialog_challan_for_dispatch,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.binding.tvItemName.text = list[position].itemName
        holder.binding.tvProductCount.text = list[position].item_Qty
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class ViewHolder(var binding: ItemDialogChallanForDispatchBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}
