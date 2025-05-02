package com.callmangement.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.R
import com.callmangement.databinding.ItemTrainingScheduleListActivityBinding
import com.callmangement.model.training_schedule.ModelTrainingScheduleList
import com.callmangement.ui.training_schedule.UpdateTrainingScheduleActivity

class TrainingScheduleListActivityAdapter(private val context: Context) :
    RecyclerView.Adapter<TrainingScheduleListActivityAdapter.ViewHolder>() {
    private var list: List<ModelTrainingScheduleList>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemTrainingScheduleListActivityBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_training_schedule_list_activity,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list!![position]
        holder.binding.data = model
        holder.binding.crdItem.setOnClickListener { view: View? ->
            context.startActivity(
                Intent(
                    context, UpdateTrainingScheduleActivity::class.java
                ).putExtra("param", model)
            )
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<ModelTrainingScheduleList>?) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (list != null) {
            list!!.size
        } else {
            0
        }
    }

    class ViewHolder(val binding: ItemTrainingScheduleListActivityBinding) :
        RecyclerView.ViewHolder(
            binding.root
        )
}
