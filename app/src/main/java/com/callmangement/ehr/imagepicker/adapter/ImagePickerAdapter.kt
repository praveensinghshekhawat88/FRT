package com.callmangement.ehr.imagepicker.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.ehr.imagepicker.helper.ImageHelper.isGifFormat
import com.callmangement.ehr.imagepicker.listener.OnImageClickListener
import com.callmangement.ehr.imagepicker.listener.OnImageSelectionListener
import com.callmangement.ehr.imagepicker.model.Image
import com.callmangement.ehr.imagepicker.ui.common.BaseRecyclerViewAdapter
import com.callmangement.ehr.imagepicker.ui.imagepicker.ImageLoader
import com.callmangement.R

/**
 * Created by hoanglam on 7/31/16.
 */
class ImagePickerAdapter(
    context: Context,
    imageLoader: ImageLoader,
    selectedImages: MutableList<Image>?,
    private val itemClickListener: OnImageClickListener
) :
    BaseRecyclerViewAdapter<ImagePickerAdapter.ImageViewHolder?>(context, imageLoader) {
    private val images: MutableList<Image> = ArrayList()
    private val selectedImages: MutableList<Image> = ArrayList()
    private var imageSelectionListener: OnImageSelectionListener? = null

    init {
        if (selectedImages != null && !selectedImages.isEmpty()) {
            this.selectedImages.addAll(selectedImages)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val itemView = inflater.inflate(R.layout.imagepicker_item_image, parent, false)
        return ImageViewHolder(itemView)
    }

    override fun onBindViewHolder(
        viewHolder: ImageViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val image = images[position]
        val isSelected = isSelected(image)

        imageLoader.loadImage(image.path, viewHolder.image)

        viewHolder.gifIndicator.visibility =
            if (isGifFormat(image)) View.VISIBLE else View.GONE
        viewHolder.alphaView.alpha = if (isSelected) 0.5f else 0.0f
        viewHolder.container.foreground = if (isSelected)
            ContextCompat.getDrawable(context, R.drawable.imagepicker_ic_selected)
        else
            null

        viewHolder.itemView.setOnClickListener { view ->
            val shouldSelect =
                itemClickListener.onImageClick(view, viewHolder.adapterPosition, !isSelected)
            if (isSelected) {
                removeSelected(image, position)
            } else if (shouldSelect) {
                addSelected(image, position)
            }
        }
    }

    private fun isSelected(image: Image): Boolean {
        for (selectedImage in selectedImages) {
            if (selectedImage.path == image.path) {
                return true
            }
        }
        return false
    }

    fun setOnImageSelectionListener(imageSelectedListener: OnImageSelectionListener?) {
        this.imageSelectionListener = imageSelectedListener
    }

    override fun getItemCount(): Int {
        return images.size
    }


    fun setData(images: List<Image>?) {
        if (images != null) {
            this.images.clear()
            this.images.addAll(images)
        }
        notifyDataSetChanged()
    }

    fun addSelected(image: Image, position: Int) {
        selectedImages.add(image)
        notifyItemChanged(position)
        notifySelectionChanged()
    }

    fun removeSelected(image: Image, position: Int) {
        selectedImages.remove(image)
        notifyItemChanged(position)
        notifySelectionChanged()
    }

    fun removeAllSelected() {
        selectedImages.clear()
        notifyDataSetChanged()
        notifySelectionChanged()
    }

    private fun notifySelectionChanged() {
        if (imageSelectionListener != null) {
            imageSelectionListener!!.onSelectionUpdate(selectedImages)
        }
    }

    fun getSelectedImages(): MutableList<Image> {
        return selectedImages
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val container: FrameLayout = itemView as FrameLayout
        val image: ImageView =
            itemView.findViewById(R.id.image_thumbnail)
        val alphaView: View = itemView.findViewById(R.id.view_alpha)
        val gifIndicator: View =
            itemView.findViewById(R.id.gif_indicator)
    }
}
