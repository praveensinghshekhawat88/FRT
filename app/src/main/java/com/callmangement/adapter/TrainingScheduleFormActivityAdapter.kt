package com.callmangement.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.R
import com.callmangement.databinding.ItemTrainingScheduleFormActivityBinding
import com.callmangement.model.training_schedule.ModelTrainingScheduleFormAddItem

class TrainingScheduleFormActivityAdapter(
    private val context: Activity,
    private val list: List<ModelTrainingScheduleFormAddItem>?
) : RecyclerView.Adapter<TrainingScheduleFormActivityAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemTrainingScheduleFormActivityBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_training_schedule_form_activity,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        context.currentFocus

        val imm1 = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm1.hideSoftInputFromWindow(holder.binding.inputName.windowToken, 0)
        holder.binding.inputName.requestFocus()

        val imm2 = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm2.hideSoftInputFromWindow(holder.binding.inputFpsCode.windowToken, 0)

        //holder.binding.inputFpsCode.requestFocus();
        val imm3 = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm3.hideSoftInputFromWindow(holder.binding.inputPhone.windowToken, 0)

        //holder.binding.inputPhone.requestFocus();
        holder.binding.inputName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (list!![position].name == "") {
                    if (s.toString() != "") {
                        list[position].name = s.toString().trim { it <= ' ' }
                    } else {
                        if (list[position].name == "");
                    }
                } else {
                    if (s.toString() != "") {
                        list[position].name = s.toString().trim { it <= ' ' }
                    }
                }
            }
        })

        holder.binding.inputFpsCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (list!![position].fpsCode == "") {
                    if (s.toString() != "") {
                        list[position].fpsCode = s.toString().trim { it <= ' ' }
                    } else {
                        if (list[position].fpsCode == "");
                    }
                } else {
                    if (s.toString() != "") {
                        list[position].fpsCode = s.toString().trim { it <= ' ' }
                    }
                }
            }
        })

        holder.binding.inputPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (list!![position].phone == "") {
                    if (s.toString() != "") {
                        list[position].phone = s.toString().trim { it <= ' ' }
                    } else {
                        if (list[position].phone == "");
                    }
                } else {
                    if (s.toString() != "") {
                        list[position].phone = s.toString().trim { it <= ' ' }
                    }
                }
            }
        })
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class ViewHolder(val binding: ItemTrainingScheduleFormActivityBinding) :
        RecyclerView.ViewHolder(
            binding.root
        )
}
