package com.callmangement.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.R
import com.callmangement.databinding.ItemDistrictListActivityBinding
import com.callmangement.model.district.ModelDistrictList
import com.callmangement.utils.PrefManager

class DistrictListActivityAdapter(private val context: Context) :
    RecyclerView.Adapter<DistrictListActivityAdapter.ViewHolder>() {
    private var district_List: List<ModelDistrictList?>? = null
    private val prefManager = PrefManager(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemDistrictListActivityBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_district_list_activity,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = district_List!![position]
        if (prefManager.USER_Change_Language != "") {
            if (prefManager.USER_Change_Language == "en") holder.binding.textDistrictName.text =
                model!!.districtNameEng
            else holder.binding.textDistrictName.text = model!!.districtNameHi
        }

        //        holder.binding.crdItem.setOnClickListener(view -> {
//            context.startActivity(new Intent(context, ServiceEngineerListActivity.class));
//        });
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(district_List: List<ModelDistrictList?>?) {
        this.district_List = district_List
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (district_List != null) district_List!!.size
        else 0
    }

    class ViewHolder(val binding: ItemDistrictListActivityBinding) :
        RecyclerView.ViewHolder(binding.root)
}
