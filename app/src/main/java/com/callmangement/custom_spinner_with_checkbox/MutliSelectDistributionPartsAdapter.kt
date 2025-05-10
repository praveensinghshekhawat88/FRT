package com.callmangement.custom_spinner_with_checkbox

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.R
import com.callmangement.databinding.MultiSelectItemBinding
import com.callmangement.model.inventrory.ModelPartsList

class MutliSelectDistributionPartsAdapter(
    private val context: Activity,
    private val list: List<ModelPartsList?>
) :
    RecyclerView.Adapter<MutliSelectDistributionPartsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<MultiSelectItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.multi_select_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {

        val model = list[position]!!

        holder.binding.dialogItemName.text = model.itemName
        Log.d("listtttt", "" + model.itemName)

        if (model.isSelectFlag) holder.binding.ivCheckBox.setBackgroundResource(R.drawable.ic_checkbox_select)
        else holder.binding.ivCheckBox.setBackgroundResource(R.drawable.ic_checkbox_unselect)


        holder.itemView.setOnClickListener { view: View? ->
            if (model.isSelectFlag) {
                holder.binding.ivCheckBox.setBackgroundResource(R.drawable.ic_checkbox_unselect)
                model.isSelectFlag = false
                Log.d("getItemName", " " + model.itemName)
            } else {
                holder.binding.ivCheckBox.setBackgroundResource(R.drawable.ic_checkbox_select)
                model.isSelectFlag = true
                Log.d("getItemName", " " + model.itemName)
            }
        }
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    class ViewHolder(val binding: MultiSelectItemBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}
