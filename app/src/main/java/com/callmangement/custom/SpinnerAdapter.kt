package com.callmangement.custom

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.callmangement.R
import com.callmangement.model.inventrory.ModelPartsList

class SpinnerAdapter(var context: Activity, var list: List<ModelPartsList>) : BaseAdapter() {
    var inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return null!!
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(position: Int, view: View, parent: ViewGroup): View {
        var view = view
        view = inflater.inflate(R.layout.spinner_dropdown_item, null)
        val tvName = view.findViewById<TextView>(R.id.textSpinner)
        if (!list[position].isVisibleItemFlag) tvName.visibility = View.VISIBLE
        else {
            tvName.visibility = View.GONE
            tvName.maxHeight = 0
            tvName.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
        tvName.text = list[position].itemName
        return view
    }
}
