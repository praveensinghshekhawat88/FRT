package com.callmangement.ehr.imagepicker.ui.common

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.ehr.imagepicker.ui.imagepicker.ImageLoader

/**
 * Created by hoanglam on 8/17/17.
 */
abstract class BaseRecyclerViewAdapter<T : RecyclerView.ViewHolder?>(
    @JvmField val context: Context,
    @JvmField val imageLoader: ImageLoader
) :
    RecyclerView.Adapter<T>() {
    @JvmField
    val inflater: LayoutInflater = LayoutInflater.from(context)
}
