package com.callmangement.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.R
import com.callmangement.databinding.ItemAvailableStockListForSeBinding
import com.callmangement.model.inventrory.ModelPartsList

class AvailableStockListForSEActivityAdapter(
    private val context: Context,
    private val modelPartsList: List<ModelPartsList?>
) :
    RecyclerView.Adapter<AvailableStockListForSEActivityAdapter.ViewHolder>() {
    private val modelPartsFilterList: List<ModelPartsList?> =
        ArrayList(modelPartsList)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemAvailableStockListForSeBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_available_stock_list_for_se,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = modelPartsList[position]!!
        holder.binding.textItemName.text = model.itemName
        holder.binding.textItemQuantity.text = model.item_Qty
    }

    override fun getItemCount(): Int {
        return modelPartsList.size
    }

    class ViewHolder(val binding: ItemAvailableStockListForSeBinding) :
        RecyclerView.ViewHolder(binding.root)
}
