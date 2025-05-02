package com.callmangement.custom_spinner_with_checkbox

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.R
import com.callmangement.custom_spinner_with_checkbox.MutliSelectAdapter.MultiSelectDialogViewHolder
import com.callmangement.model.inventrory.ModelPartsList
import java.util.Locale

class MutliSelectAdapter internal constructor(dataSet: List<ModelPartsList>, context: Context?) :
    RecyclerView.Adapter<MultiSelectDialogViewHolder>() {
    private var mDataSet: List<ModelPartsList> = ArrayList()
    private var mSearchQuery = ""
    private val mContext: Context?

    init {
        this.mDataSet = dataSet
        this.mContext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiSelectDialogViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.multi_select_item, parent, false)
        return MultiSelectDialogViewHolder(view)
    }

    override fun onBindViewHolder(holder: MultiSelectDialogViewHolder, position: Int) {
        if (mSearchQuery != "" && mSearchQuery.length > 1) {
            setHighlightedText(position, holder.dialog_name_item)
        } else {
            holder.dialog_name_item.text = mDataSet[position].itemName
        }

        if (mDataSet[position].isSelectFlag) {
            if (!MultiSelectDialog.Companion.selectedIdsForCallback.contains(mDataSet[position].itemId)) {
                MultiSelectDialog.Companion.selectedIdsForCallback.add(mDataSet[position].itemId)
            }
        }

        if (checkForSelection(mDataSet[position].itemId)) {
            holder.dialog_item_checkbox.isChecked = true
        } else {
            holder.dialog_item_checkbox.isChecked = false
        }

        holder.main_container.setOnClickListener { view: View? ->
            if (!holder.dialog_item_checkbox.isChecked) {
                MultiSelectDialog.Companion.selectedIdsForCallback.add(mDataSet[holder.adapterPosition].itemId)
                holder.dialog_item_checkbox.isChecked = true
                mDataSet[holder.adapterPosition].isSelectFlag = true
                notifyItemChanged(holder.adapterPosition)
            } else {
                removeFromSelection(mDataSet[holder.adapterPosition].itemId)
                holder.dialog_item_checkbox.isChecked = false
                mDataSet[holder.adapterPosition].isSelectFlag = false
                notifyItemChanged(holder.adapterPosition)
            }
        }
    }

    private fun setHighlightedText(position: Int, textview: TextView) {
        val name = mDataSet[position].itemName
        val str = SpannableString(name)
        val endLength =
            name!!.lowercase(Locale.getDefault()).indexOf(mSearchQuery) + mSearchQuery.length
        val highlightedColor = ColorStateList(
            arrayOf(intArrayOf()), intArrayOf(
                ContextCompat.getColor(
                    mContext!!, R.color.colorOrange
                )
            )
        )
        val textAppearanceSpan =
            TextAppearanceSpan(null, Typeface.NORMAL, -1, highlightedColor, null)
        str.setSpan(
            textAppearanceSpan,
            name.lowercase(Locale.getDefault()).indexOf(mSearchQuery),
            endLength,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textview.text = str
    }

    private fun removeFromSelection(id: String?) {
        for (i in MultiSelectDialog.Companion.selectedIdsForCallback.indices) {
            if (id == MultiSelectDialog.Companion.selectedIdsForCallback.get(i)) {
                MultiSelectDialog.Companion.selectedIdsForCallback.removeAt(i)
            }
        }
    }


    private fun checkForSelection(id: String?): Boolean {
        for (i in MultiSelectDialog.Companion.selectedIdsForCallback.indices) {
            if (id == MultiSelectDialog.Companion.selectedIdsForCallback.get(i)) {
                return true
            }
        }
        return false
    }


    /*//get selected name string seperated by coma
    public String getDataString() {
        String data = "";
        for (int i = 0; i < mDataSet.size(); i++) {
            if (checkForSelection(mDataSet.get(i).getId())) {
                data = data + ", " + mDataSet.get(i).getName();
            }
        }
        if (data.length() > 0) {
            return data.substring(1);
        } else {
            return "";
        }
    }
    //get selected name ararylist
    public ArrayList<String> getSelectedNameList() {
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < mDataSet.size(); i++) {
            if (checkForSelection(mDataSet.get(i).getId())) {
                names.add(mDataSet.get(i).getName());
            }
        }
        //  return names.toArray(new String[names.size()]);
        return names;
    }*/
    override fun getItemCount(): Int {
        return mDataSet.size
    }

    fun setData(
        data: List<ModelPartsList>,
        query: String,
        mutliSelectAdapter: MutliSelectAdapter?
    ) {
        this.mDataSet = data
        this.mSearchQuery = query
        mutliSelectAdapter!!.notifyDataSetChanged()
    }

    inner class MultiSelectDialogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dialog_name_item: TextView = view.findViewById<View>(R.id.dialog_item_name) as TextView
        val dialog_item_checkbox: AppCompatCheckBox =
            view.findViewById<View>(R.id.dialog_item_checkbox) as AppCompatCheckBox
        val main_container: LinearLayout =
            view.findViewById<View>(R.id.main_container) as LinearLayout
    }
}
