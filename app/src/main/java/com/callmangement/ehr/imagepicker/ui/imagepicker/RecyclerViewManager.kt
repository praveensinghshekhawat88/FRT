package com.callmangement.ehr.imagepicker.ui.imagepicker

import android.content.Context
import android.content.res.Configuration
import android.os.Parcelable
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.ehr.imagepicker.adapter.FolderPickerAdapter
import com.callmangement.ehr.imagepicker.adapter.ImagePickerAdapter
import com.callmangement.ehr.imagepicker.listener.OnBackAction
import com.callmangement.ehr.imagepicker.listener.OnFolderClickListener
import com.callmangement.ehr.imagepicker.listener.OnImageClickListener
import com.callmangement.ehr.imagepicker.listener.OnImageSelectionListener
import com.callmangement.ehr.imagepicker.model.Config
import com.callmangement.ehr.imagepicker.model.Folder
import com.callmangement.ehr.imagepicker.model.Image
import com.callmangement.ehr.imagepicker.widget.GridSpacingItemDecoration
import com.callmangement.R

/**
 * Created by hoanglam on 8/17/17.
 */
class RecyclerViewManager(
    private val recyclerView: RecyclerView,
    private val config: Config,
    orientation: Int
) {
    private val context: Context = recyclerView.context

    private var layoutManager: GridLayoutManager? = null
    private var itemOffsetDecoration: GridSpacingItemDecoration? = null

    private var imageAdapter: ImagePickerAdapter? = null
    private var folderAdapter: FolderPickerAdapter? = null

    private var imageColumns = 0
    private var folderColumns = 0

    private val imageLoader: ImageLoader

    private var foldersState: Parcelable? = null
    private var title: String? = null
    private var isShowingFolder: Boolean


    init {
        changeOrientation(orientation)
        imageLoader = ImageLoader()
        isShowingFolder = config.isFolderMode
    }

    fun setupAdapters(
        imageClickListener: OnImageClickListener?,
        folderClickListener: OnFolderClickListener
    ) {
        var selectedImages: MutableList<Image>? = null
        if (config.isMultipleMode && !config.selectedImages!!.isEmpty()) {
            selectedImages = config.selectedImages!!
        }

        imageAdapter = ImagePickerAdapter(context, imageLoader, selectedImages!!, imageClickListener!!)
        folderAdapter = FolderPickerAdapter(
            context,
            imageLoader,
            object : OnFolderClickListener {
                override fun onFolderClick(folder: Folder?) {
                    foldersState = recyclerView.layoutManager!!.onSaveInstanceState()
                    folderClickListener.onFolderClick(folder)
                }
            }
        )

    }

    /**
     * Set item size, column size base on the screen orientation
     */
    fun changeOrientation(orientation: Int) {
        imageColumns = if (orientation == Configuration.ORIENTATION_PORTRAIT) 3 else 5
        folderColumns = if (orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 4

        val columns = if (isShowingFolder) folderColumns else imageColumns
        layoutManager = GridLayoutManager(context, columns)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        setItemDecoration(columns)
    }

    private fun setItemDecoration(columns: Int) {
        if (itemOffsetDecoration != null) {
            recyclerView.removeItemDecoration(itemOffsetDecoration!!)
        }
        itemOffsetDecoration = GridSpacingItemDecoration(
            columns,
            context.resources.getDimensionPixelSize(R.dimen.imagepicker_item_padding),
            false
        )
        recyclerView.addItemDecoration(itemOffsetDecoration!!)
        layoutManager!!.spanCount = columns
    }


    fun setOnImageSelectionListener(imageSelectionListener: OnImageSelectionListener?) {
        checkAdapterIsInitialized()
        imageAdapter!!.setOnImageSelectionListener(imageSelectionListener)
    }

    val selectedImages: MutableList<Image>?
        get() {
            checkAdapterIsInitialized()
            return imageAdapter!!.getSelectedImages()
        }

    private fun checkAdapterIsInitialized() {
        checkNotNull(imageAdapter) { "Must call setupAdapters first!" }
    }

    fun selectImage(): Boolean {
        if (config.isMultipleMode) {
            if (imageAdapter!!.getSelectedImages().size >= config.maxSize) {
                val message = String.format(
                    context.getString(R.string.imagepicker_msg_limit_images),
                    config.maxSize
                )
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                return false
            }
        } else {
            if (imageAdapter!!.itemCount > 0) {
                imageAdapter!!.removeAllSelected()
            }
        }
        return true
    }

    fun handleBack(action: OnBackAction) {
        if (config.isFolderMode && !isShowingFolder) {
            setFolderAdapter(null)
            action.onBackToFolder()
            return
        }
        action.onFinishImagePicker()
    }

    fun setImageAdapter(images: List<Image>?, title: String?) {
        imageAdapter!!.setData(images)
        setItemDecoration(imageColumns)
        recyclerView.adapter = imageAdapter
        this.title = title
        isShowingFolder = false
    }

    fun setFolderAdapter(folders: List<Folder?>?) {
        folderAdapter!!.setData(folders)
        setItemDecoration(folderColumns)
        recyclerView.adapter = folderAdapter
        isShowingFolder = true

        if (foldersState != null) {
            layoutManager!!.spanCount = folderColumns
            recyclerView.layoutManager!!.onRestoreInstanceState(foldersState)
        }
    }

    fun getTitle(): String? {
        return if (isShowingFolder) {
            config.folderTitle
        } else if (config.isFolderMode) {
            title
        } else {
            config.imageTitle
        }
    }

    val isShowDoneButton: Boolean
        get() = config.isMultipleMode && imageAdapter!!.getSelectedImages().size > 0
}
