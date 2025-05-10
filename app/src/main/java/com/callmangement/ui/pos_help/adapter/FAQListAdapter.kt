package com.callmangement.ui.pos_help.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.R
import com.callmangement.databinding.ItemFaqListBinding
import com.callmangement.ui.pos_help.activity.FAQDetailActivity
import com.callmangement.ui.pos_help.model.ModelFAQList

class FAQListAdapter(private val context: Context, private val modelFAQList: List<ModelFAQList>) :
    RecyclerView.Adapter<FAQListAdapter.ViewHolder>() {
    private val imageViewList: MutableList<ImageView> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFaqListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        imageViewList.add(holder.binding.ivHideShow)

        holder.binding.ivHideShow.setOnClickListener { view: View? ->
            if (modelFAQList[position].isSelectFlag) {
                imageViewList[position].setBackgroundResource(R.drawable.ic_show_icon)
                modelFAQList[position].isSelectFlag = false
                holder.binding.answerLay.visibility = View.GONE
            } else {
                imageViewList[position].setBackgroundResource(R.drawable.ic_hide_icon)
                modelFAQList[position].isSelectFlag = true
                holder.binding.answerLay.visibility = View.VISIBLE
            }
        }

        holder.binding.buttonView.setOnClickListener { view: View? ->
            context.startActivity(
                Intent(
                    context,
                    FAQDetailActivity::class.java
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return modelFAQList.size
    }

    class ViewHolder(val binding: ItemFaqListBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}
