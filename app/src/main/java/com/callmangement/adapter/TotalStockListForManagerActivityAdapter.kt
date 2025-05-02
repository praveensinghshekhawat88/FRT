package com.callmangement.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.R
import com.callmangement.databinding.ItemTotalStockListForManagerBinding
import com.callmangement.model.inventrory.ModelPartsList

class TotalStockListForManagerActivityAdapter(
    private val context: Context,
    private val modelPartsList: List<ModelPartsList>
) : RecyclerView.Adapter<TotalStockListForManagerActivityAdapter.ViewHolder>() {
    private val modelPartsFilterList: List<ModelPartsList> = ArrayList(modelPartsList)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemTotalStockListForManagerBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_total_stock_list_for_manager,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = modelPartsList[position]
        holder.binding.textItemName.text = model.itemName
        holder.binding.textItemQuantity.text = model.item_Qty
    }

    override fun getItemCount(): Int {
        return modelPartsList.size
    }

    class ViewHolder(val binding: ItemTotalStockListForManagerBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}
