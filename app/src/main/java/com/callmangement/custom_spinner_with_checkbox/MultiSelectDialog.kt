package com.callmangement.custom_spinner_with_checkbox

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.R
import com.callmangement.model.inventrory.ModelPartsList
import java.util.Locale

class MultiSelectDialog : AppCompatDialogFragment(), SearchView.OnQueryTextListener,
    View.OnClickListener {
    var mainListOfAdapter: List<ModelPartsList> = ArrayList()
    private var mutliSelectAdapter: MutliSelectAdapter? = null
    private var title: String? = null
    private var titleSize = 25.0f
    private var positiveText = "DONE"
    private var negativeText = "CANCEL"
    private var dialogTitle: TextView? = null
    private var dialogSubmit: TextView? = null
    private var dialogCancel: TextView? = null
    private var previouslySelectedIdsList: MutableList<String> = ArrayList()
    private var tempPreviouslySelectedIdsList: List<String> = ArrayList()
    private var tempMainListOfAdapter: List<ModelPartsList> = ArrayList()
    private var submitCallbackListener: SubmitCallbackListener? = null
    private var maxSelectionLimit = 0
    private var minSelectionLimit = 1

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireActivity())
        dialog.window!!.requestFeature(1)
        dialog.window!!.setFlags(32, 1024)
        dialog.setContentView(R.layout.custom_multi_select)
        dialog.window!!.setLayout(-1, -1)
        val mrecyclerView = dialog.findViewById<RecyclerViewEmptySupport>(R.id.recycler_view)
        val searchView = dialog.findViewById<SearchView>(R.id.search_view)
        dialogTitle = dialog.findViewById(R.id.title)
        dialogSubmit = dialog.findViewById(R.id.done)
        dialogCancel = dialog.findViewById(R.id.cancel)
        mrecyclerView.setEmptyView(dialog.findViewById(R.id.list_empty1))
        val layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        mrecyclerView.layoutManager = layoutManager
        dialogSubmit!!.setOnClickListener(this)
        dialogCancel!!.setOnClickListener(this)
        settingValues()
        mainListOfAdapter = setCheckedIDS(mainListOfAdapter, previouslySelectedIdsList)
        mutliSelectAdapter = MutliSelectAdapter(mainListOfAdapter, requireContext())
        mrecyclerView.adapter = mutliSelectAdapter
        searchView.setOnQueryTextListener(this)
        searchView.onActionViewExpanded()
        searchView.clearFocus()
        return dialog
    }

    fun title(title: String?): MultiSelectDialog {
        this.title = title
        return this
    }

    fun titleSize(titleSize: Float): MultiSelectDialog {
        this.titleSize = titleSize
        return this
    }

    fun positiveText(message: String): MultiSelectDialog {
        positiveText = message
        return this
    }

    fun negativeText(message: String): MultiSelectDialog {
        negativeText = message
        return this
    }

    fun preSelectIDsList(list: MutableList<String>): MultiSelectDialog {
        previouslySelectedIdsList = list
        tempPreviouslySelectedIdsList = ArrayList(previouslySelectedIdsList)
        return this
    }

    fun multiSelectList(list: List<ModelPartsList>): MultiSelectDialog {
        mainListOfAdapter = list
        tempMainListOfAdapter = ArrayList(mainListOfAdapter)
        if (maxSelectionLimit == 0) {
            maxSelectionLimit = list.size
        }
        return this
    }

    fun setMaxSelectionLimit(limit: Int): MultiSelectDialog {
        maxSelectionLimit = limit
        return this
    }

    fun setMinSelectionLimit(limit: Int): MultiSelectDialog {
        minSelectionLimit = limit
        return this
    }

    fun onSubmit(callback: SubmitCallbackListener): MultiSelectDialog {
        submitCallbackListener = callback
        return this
    }

    private fun settingValues() {
        dialogTitle!!.text = title
        dialogTitle!!.setTextSize(2, titleSize)
        dialogSubmit!!.text = positiveText.uppercase(Locale.getDefault())
        dialogCancel!!.text = negativeText.uppercase(Locale.getDefault())
    }

    private fun setCheckedIDS(
        multiselectdata: List<ModelPartsList>,
        listOfIdsSelected: List<String>
    ): List<ModelPartsList> {
        for (i in multiselectdata.indices) {
            multiselectdata[i].isSelectFlag = false
            for (j in listOfIdsSelected.indices) {
                if (multiselectdata[i].itemId == listOfIdsSelected[j]) multiselectdata[i].isSelectFlag =
                    true
            }
        }
        return multiselectdata
    }

    private fun filter(models: List<ModelPartsList>, query: String): ArrayList<ModelPartsList> {
        var query = query
        query = query.lowercase(Locale.getDefault())
        val filteredModelList = ArrayList<ModelPartsList>()
        if ((query == "") or query.isEmpty()) {
            filteredModelList.addAll(models)
        } else {
            for (model in models) {
                val name = model.itemName!!.lowercase(Locale.getDefault())
                if (name.contains(query)) {
                    filteredModelList.add(model)
                }
            }
        }
        return filteredModelList
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        selectedIdsForCallback = previouslySelectedIdsList
        mainListOfAdapter = setCheckedIDS(mainListOfAdapter, selectedIdsForCallback)
        val filteredlist: List<ModelPartsList> = filter(mainListOfAdapter, newText)
        mutliSelectAdapter!!.setData(
            filteredlist, newText.lowercase(Locale.getDefault()),
            mutliSelectAdapter!!
        )
        return false
    }

    override fun onClick(view: View) {
        if (view.id == R.id.done) {
            val callBackListOfIds: List<String> = selectedIdsForCallback
            val youCan: String
            val options: String
            val option: String
            var message: String
            if (callBackListOfIds.size >= minSelectionLimit) {
                if (callBackListOfIds.size <= maxSelectionLimit) {
                    tempPreviouslySelectedIdsList = ArrayList(callBackListOfIds)
                    if (submitCallbackListener != null) {
                        submitCallbackListener!!.onSelected(
                            callBackListOfIds,
                            selectNameList,
                            selectedDataString
                        )
                    }
                    dismiss()
                } else {
                    youCan = resources.getString(R.string.you_can_only_select_upto)
                    options = resources.getString(R.string.options)
                    option = resources.getString(R.string.option)
                    message = ""
                    message = if (maxSelectionLimit > 1) {
                        "$youCan $maxSelectionLimit $options"
                    } else {
                        "$youCan $maxSelectionLimit $option"
                    }

                    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
                }
            } else {
                youCan = resources.getString(R.string.please_select_atleast)
                options = resources.getString(R.string.options)
                option = resources.getString(R.string.option)
                message = ""
                message = if (minSelectionLimit > 1) {
                    "$youCan $minSelectionLimit $options"
                } else {
                    "$youCan $minSelectionLimit $option"
                }

                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
            }
        }

        if (view.id == R.id.cancel) {
            if (submitCallbackListener != null) {
                selectedIdsForCallback.clear()
                selectedIdsForCallback.addAll(tempPreviouslySelectedIdsList)
                submitCallbackListener!!.onCancel()
            }
            dismiss()
        }
    }

    private val selectedDataString: String
        get() {
            var data = ""
            for (i in tempMainListOfAdapter.indices) {
                if (this.checkForSelection(tempMainListOfAdapter[i].itemId!!)) {
                    data = data + ", " + tempMainListOfAdapter[i].itemName
                }
            }

            return if (data.length > 0) {
                data.substring(1)
            } else {
                ""
            }
        }

    private val selectNameList: List<String?>
        get() {
            val names: MutableList<String?> =
                ArrayList()

            for (i in tempMainListOfAdapter.indices) {
                if (this.checkForSelection(tempMainListOfAdapter[i].itemId!!)) {
                    names.add(tempMainListOfAdapter[i].itemName)
                }
            }

            return names
        }

    private fun checkForSelection(id: String): Boolean {
        for (i in selectedIdsForCallback.indices) {
            if (id == selectedIdsForCallback[i]) {
                return true
            }
        }

        return false
    }

    interface SubmitCallbackListener {
        fun onSelected(var1: List<String>?, var2: List<String?>?, var3: String?)

        fun onCancel()
    }

    companion object {
        var selectedIdsForCallback: MutableList<String> = ArrayList()
    }
}
