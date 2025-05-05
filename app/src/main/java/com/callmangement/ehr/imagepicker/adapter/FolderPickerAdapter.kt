package com.callmangement.ehr.imagepicker.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.ehr.imagepicker.listener.OnFolderClickListener
import com.callmangement.ehr.imagepicker.model.Folder
import com.callmangement.ehr.imagepicker.ui.common.BaseRecyclerViewAdapter
import com.callmangement.ehr.imagepicker.ui.imagepicker.ImageLoader
import com.callmangement.R

/**
 * Created by boss1088 on 8/22/16.
 */
class FolderPickerAdapter(
    context: Context,
    imageLoader: ImageLoader,
    private val itemClickListener: OnFolderClickListener
) :
    BaseRecyclerViewAdapter<FolderPickerAdapter.FolderViewHolder?>(context, imageLoader) {
    private val folders: MutableList<Folder?> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val itemView = inflater.inflate(R.layout.imagepicker_item_folder, parent, false)
        return FolderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folder = folders[position]

        imageLoader.loadImage(folder!!.images[0].path, holder.image)

        holder.name.text = folder.folderName

        val count = folder.images.size
        holder.count.text = String.format(
            if (count > 1)
                context.getString(R.string.imagepicker_photo_count_multiple)
            else
                context.getString(R.string.imagepicker_photo_count_single),
            count
        )

        holder.itemView.setOnClickListener {
            itemClickListener.onFolderClick(
                folder
            )
        }
    }

    fun setData(folders: Collection<Folder?>?) {
        if (folders != null) {
            this.folders.clear()
            this.folders.addAll(folders)
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return folders.size
    }

    class FolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView =
            itemView.findViewById(R.id.image_folder_thumbnail)
        val name: TextView = itemView.findViewById(R.id.text_folder_name)
        val count: TextView = itemView.findViewById(R.id.text_photo_count)
    }
}
