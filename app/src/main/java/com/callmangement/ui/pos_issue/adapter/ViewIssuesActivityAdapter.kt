package com.callmangement.ui.pos_issue.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.databinding.ItemViewIssuesBinding

class ViewIssuesActivityAdapter(private val context: Context) :
    RecyclerView.Adapter<ViewIssuesActivityAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemViewIssuesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    }

    override fun getItemCount(): Int {
        return 5
    }

    class ViewHolder(private val binding: ItemViewIssuesBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}
