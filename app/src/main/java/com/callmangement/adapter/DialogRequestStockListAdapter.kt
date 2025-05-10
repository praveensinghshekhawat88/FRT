package com.callmangement.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.R
import com.callmangement.databinding.ItemDialogAddStockListBinding
import com.callmangement.model.inventrory.ModelRequestStock

class DialogRequestStockListAdapter(
    private val context: Activity,
    private val list: List<ModelRequestStock>
) :
    RecyclerView.Adapter<DialogRequestStockListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemDialogAddStockListBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_dialog_request_stock_list,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.binding.tvItemName.text = list[position].itemName
        holder.binding.tvQuantity.text = list[position].qty
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class ViewHolder(var binding: ItemDialogAddStockListBinding) :
        RecyclerView.ViewHolder(binding.root)
}
