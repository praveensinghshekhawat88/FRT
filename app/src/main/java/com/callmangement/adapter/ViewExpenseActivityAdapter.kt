package com.callmangement.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.R
import com.callmangement.databinding.ItemViewExpenseActivityBinding
import com.callmangement.model.expense.ModelExpensesList
import com.callmangement.ui.reports.ExpenseDetailsActivity

class ViewExpenseActivityAdapter(
    private val context: Context,
    private val modelExpensesList: List<ModelExpensesList>
) :
    RecyclerView.Adapter<ViewExpenseActivityAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemViewExpenseActivityBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = modelExpensesList[position]
        holder.binding.textName.text = model.seName
        holder.binding.textDistrictName.text = model.district
        holder.binding.textRemark.text = model.remark
        holder.binding.textAmount.text = model.totalExAmount.toString()
        holder.binding.textExpenseDate.text = model.createdOnStr

        if (model.expenseStatusID == 1) {
            holder.binding.textExpenseStatus.text = model.expenseStatus
            holder.binding.textExpenseStatus.setTextColor(context.resources.getColor(R.color.colorRedDark))
        } else if (model.expenseStatusID == 2) {
            holder.binding.textExpenseStatus.text = model.expenseStatus
            holder.binding.textExpenseStatus.setTextColor(context.resources.getColor(R.color.colorGreenDark))
        }

        if (model.completedOnStr!!.isEmpty()) {
            holder.binding.expenseCompletedDateLay.visibility = View.GONE
        } else {
            holder.binding.expenseCompletedDateLay.visibility = View.VISIBLE
            holder.binding.textExpenseCompletedDate.text = model.completedOnStr
        }

        holder.binding.tvView.setOnClickListener { view: View? ->
            val intent = Intent(context, ExpenseDetailsActivity::class.java)
            intent.putExtra("param", model)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return modelExpensesList.size
    }

    class ViewHolder(val binding: ItemViewExpenseActivityBinding) :
        RecyclerView.ViewHolder(binding.root)
}
