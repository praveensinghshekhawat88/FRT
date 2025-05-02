package com.callmangement.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.R
import com.callmangement.databinding.ItemMaterialListDetailBinding
import com.callmangement.ui.model.inventory.receive_invoice_parts.PartsDispatchInvoice

class ReceiveMaterialListDetailsActivityAdapter(
    private val context: Context,
    private val list: List<PartsDispatchInvoice>
) : RecyclerView.Adapter<ReceiveMaterialListDetailsActivityAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemMaterialListDetailBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_material_list_detail,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]
        holder.binding.tvReceiveItemName.text = list[position].itemName
        holder.binding.textDispatchQty.text = list[position].dispatchItemQty.toString()
        list[position].setQuantity(list[position].dispatchItemQty.toString())

        if (list[position].isReceived == true) {
            holder.binding.ivMinus.isEnabled = false
            holder.binding.ivPlus.isEnabled = false
            holder.binding.etReceiveItemQuanity.setText(list[position].receivedItemQty.toString())
        } else if (list[position].isReceived == false) {
            holder.binding.ivMinus.isEnabled = false
            holder.binding.ivPlus.isEnabled = false
            holder.binding.etReceiveItemQuanity.setText(list[position].dispatchItemQty.toString())
        }

        holder.binding.receiveItemCheckbox.setOnCheckedChangeListener { compoundButton: CompoundButton?, checked: Boolean ->
            list[position].isSelectFlag = checked
        }

        holder.binding.ivMinus.setOnClickListener { view: View? ->
            val quantity = holder.binding.etReceiveItemQuanity.text.toString().trim { it <= ' ' }
            if (!quantity.isEmpty() && quantity != "0") {
                val intQuanity = quantity.toInt()
                val updatedQuanity = intQuanity - 1
                list[position].setQuantity(updatedQuanity.toString())
                holder.binding.etReceiveItemQuanity.setText(list[position].quanity)
            }
        }

        holder.binding.ivPlus.setOnClickListener { view: View? ->
            val quantity = holder.binding.etReceiveItemQuanity.text.toString().trim { it <= ' ' }
            if (!quantity.isEmpty()) {
                val intQuantity = quantity.toInt()
                if (intQuantity < list[position].dispatchItemQty) {
                    val updatedQuantity = intQuantity + 1
                    list[position].setQuantity(updatedQuantity.toString())
                    holder.binding.etReceiveItemQuanity.setText(list[position].quanity)
                } else {
                    Toast.makeText(
                        context,
                        context.resources.getString(R.string.message_received_quantity),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(val binding: ItemMaterialListDetailBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}
