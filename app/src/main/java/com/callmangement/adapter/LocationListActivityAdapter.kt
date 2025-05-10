package com.callmangement.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.R
import com.callmangement.databinding.ItemLocationListActivityBinding
import com.callmangement.model.attendance.ModelAddLocationData
import com.callmangement.model.attendance.ModelAttendanceData
import java.util.StringTokenizer

class LocationListActivityAdapter(
    private val context: Context,
    private val modelAttendanceData: ModelAttendanceData
) :
    RecyclerView.Adapter<LocationListActivityAdapter.ViewHolder>() {
    private var modelAddLocationData: List<ModelAddLocationData>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemLocationListActivityBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_location_list_activity,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = modelAddLocationData!![position]
        holder.binding.data = model
        holder.binding.textDate.text = formattedFilterDate(model.location_Date_Time)
        holder.binding.textUsername.text = modelAttendanceData.username
        holder.binding.textEmail.text = modelAttendanceData.email
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(modelAddLocationData: List<ModelAddLocationData>?) {
        this.modelAddLocationData = modelAddLocationData
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (modelAddLocationData != null) {
            modelAddLocationData!!.size
        } else {
            0
        }
    }

    class ViewHolder(val binding: ItemLocationListActivityBinding) :
        RecyclerView.ViewHolder(binding.root)

    private fun formattedFilterDate(dateTimeStr: String?): String {
        val tokenizer = StringTokenizer(dateTimeStr, " ")
        val date = tokenizer.nextToken()
        val time = tokenizer.nextToken()
        //String am_pm = tokenizer.nextToken();
        val tokenizerDate = StringTokenizer(date, "-")
        val year = tokenizerDate.nextToken()
        val month = tokenizerDate.nextToken()
        val day = tokenizerDate.nextToken()
        return "$day-$month-$year $time"
    }
}
