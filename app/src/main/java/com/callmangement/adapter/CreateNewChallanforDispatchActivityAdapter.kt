package com.callmangement.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.R
import com.callmangement.databinding.ItemCreateChallanDispatchBinding
import com.callmangement.model.inventrory.ModelPartsList

class CreateNewChallanforDispatchActivityAdapter(
    private val context: Activity,
    private val list: List<ModelPartsList>
) :
    RecyclerView.Adapter<CreateNewChallanforDispatchActivityAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemCreateChallanDispatchBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_create_challan_dispatch,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.binding.inputProductAvailableQty.text = list[position].item_Qty
        holder.binding.inputProductCount.setText(list[position].getQuantity())
        holder.binding.tvItemName.text = list[position].itemName

        if (list[position].isSelectFlag) holder.binding.ivCheckbox.setBackgroundResource(R.drawable.ic_check_box)
        else holder.binding.ivCheckbox.setBackgroundResource(R.drawable.ic_uncheck_box)

        holder.binding.ivCheckbox.setOnClickListener { view: View? ->
            list[position].isSelectFlag = !list[position].isSelectFlag
            notifyDataSetChanged()
        }

        holder.binding.ivMinus.setOnClickListener { view: View? ->
            val quantity =
                holder.binding.inputProductCount.text.toString().trim { it <= ' ' }
            if (!quantity.isEmpty() && quantity != "0") {
                val intQuantity = quantity.toInt()
                val updatedQuantity = intQuantity - 1
                list[position].setQuantity(updatedQuantity.toString())
                holder.binding.inputProductCount.setText(list[position].getQuantity())
            }
        }

        holder.binding.ivPlus.setOnClickListener { view: View? ->
            val quantity =
                holder.binding.inputProductCount.text.toString().trim { it <= ' ' }
            val availableQty =
                holder.binding.inputProductAvailableQty.text.toString().trim { it <= ' ' }.toInt()
            if (!quantity.isEmpty()) {
                val intQuantity = quantity.toInt()
                val updatedQuantity = intQuantity + 1
                if (updatedQuantity > availableQty) {
                    Toast.makeText(
                        context,
                        context.resources.getString(R.string.message_dispatch_quantity),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    list[position].setQuantity(updatedQuantity.toString())
                    holder.binding.inputProductCount.setText(list[position].getQuantity())
                }
            }
        }




        holder.binding.inputProductCount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.length > 0) {
                    Log.e("position", position.toString() + "")
                    val quantity =
                        holder.binding.inputProductCount.text.toString().trim { it <= ' ' }
                    list[position].setQuantity(quantity)
                }
            }

            override fun afterTextChanged(editable: Editable) {
            }
        })
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class ViewHolder(var binding: ItemCreateChallanDispatchBinding) :
        RecyclerView.ViewHolder(binding.root)
}
