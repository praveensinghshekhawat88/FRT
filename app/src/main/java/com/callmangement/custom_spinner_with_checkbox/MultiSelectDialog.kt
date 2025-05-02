package com.callmangement.custom_spinner_with_checkbox;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.callmangement.R;
import com.callmangement.model.inventrory.ModelPartsList;

import java.util.ArrayList;
import java.util.List;

public class MultiSelectDialog extends AppCompatDialogFragment implements SearchView.OnQueryTextListener, View.OnClickListener {
    public static List<String> selectedIdsForCallback = new ArrayList<>();
    public List<ModelPartsList> mainListOfAdapter = new ArrayList<>();
    private MutliSelectAdapter mutliSelectAdapter;
    private String title;
    private float titleSize = 25.0F;
    private String positiveText = "DONE";
    private String negativeText = "CANCEL";
    private TextView dialogTitle;
    private TextView dialogSubmit;
    private TextView dialogCancel;
    private List<String> previouslySelectedIdsList = new ArrayList<>();
    private List<String> tempPreviouslySelectedIdsList = new ArrayList<>();
    private List<ModelPartsList> tempMainListOfAdapter = new ArrayList<>();
    private SubmitCallbackListener submitCallbackListener;
    private int maxSelectionLimit = 0;
    private int minSelectionLimit = 1;

    public MultiSelectDialog() {

    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().requestFeature(1);
        dialog.getWindow().setFlags(32, 1024);
        dialog.setContentView(R.layout.custom_multi_select);
        dialog.getWindow().setLayout(-1, -1);
        RecyclerViewEmptySupport mrecyclerView = dialog.findViewById(R.id.recycler_view);
        SearchView searchView = dialog.findViewById(R.id.search_view);
        dialogTitle = dialog.findViewById(R.id.title);
        dialogSubmit = dialog.findViewById(R.id.done);
        dialogCancel = dialog.findViewById(R.id.cancel);
        mrecyclerView.setEmptyView(dialog.findViewById(R.id.list_empty1));
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        mrecyclerView.setLayoutManager(layoutManager);
        dialogSubmit.setOnClickListener(this);
        dialogCancel.setOnClickListener(this);
        settingValues();
        mainListOfAdapter = setCheckedIDS(mainListOfAdapter, previouslySelectedIdsList);
        mutliSelectAdapter = new MutliSelectAdapter(mainListOfAdapter, getContext());
        mrecyclerView.setAdapter(mutliSelectAdapter);
        searchView.setOnQueryTextListener(this);
        searchView.onActionViewExpanded();
        searchView.clearFocus();
        return dialog;
    }

    public MultiSelectDialog title(String title) {
        this.title = title;
        return this;
    }

    public MultiSelectDialog titleSize(float titleSize) {
        this.titleSize = titleSize;
        return this;
    }

    public MultiSelectDialog positiveText(@NonNull String message) {
        positiveText = message;
        return this;
    }

    public MultiSelectDialog negativeText(@NonNull String message) {
        negativeText = message;
        return this;
    }

    public MultiSelectDialog preSelectIDsList(List<String> list) {
        previouslySelectedIdsList = list;
        tempPreviouslySelectedIdsList = new ArrayList<>(previouslySelectedIdsList);
        return this;
    }

    public MultiSelectDialog multiSelectList(List<ModelPartsList> list) {
        mainListOfAdapter = list;
        tempMainListOfAdapter = new ArrayList<>(mainListOfAdapter);
        if (maxSelectionLimit == 0) {
            maxSelectionLimit = list.size();
        }
        return this;
    }

    public MultiSelectDialog setMaxSelectionLimit(int limit) {
        maxSelectionLimit = limit;
        return this;
    }

    public MultiSelectDialog setMinSelectionLimit(int limit) {
        minSelectionLimit = limit;
        return this;
    }

    public MultiSelectDialog onSubmit(@NonNull SubmitCallbackListener callback) {
        submitCallbackListener = callback;
        return this;
    }

    private void settingValues() {
        dialogTitle.setText(title);
        dialogTitle.setTextSize(2, titleSize);
        dialogSubmit.setText(positiveText.toUpperCase());
        dialogCancel.setText(negativeText.toUpperCase());
    }

    private List<ModelPartsList> setCheckedIDS(List<ModelPartsList> multiselectdata, List<String> listOfIdsSelected) {
        for(int i = 0; i < multiselectdata.size(); i++) {
            multiselectdata.get(i).setSelectFlag(false);
            for(int j = 0; j < listOfIdsSelected.size(); j++) {
                if (multiselectdata.get(i).getItemId().equals(listOfIdsSelected.get(j)))
                    multiselectdata.get(i).setSelectFlag(true);
            }
        }
        return multiselectdata;
    }

    private ArrayList<ModelPartsList> filter(List<ModelPartsList> models, String query) {
        query = query.toLowerCase();
        ArrayList<ModelPartsList> filteredModelList = new ArrayList<>();
        if (query.equals("") | query.isEmpty()) {
            filteredModelList.addAll(models);
        } else {
            for (ModelPartsList model : models) {
                String name = model.getItemName().toLowerCase();
                if (name.contains(query)) {
                    filteredModelList.add(model);
                }
            }
        }
        return filteredModelList;
    }

    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    public boolean onQueryTextChange(String newText) {
        selectedIdsForCallback = previouslySelectedIdsList;
        mainListOfAdapter = setCheckedIDS(mainListOfAdapter, selectedIdsForCallback);
        List<ModelPartsList> filteredlist = filter(mainListOfAdapter, newText);
        mutliSelectAdapter.setData(filteredlist, newText.toLowerCase(), mutliSelectAdapter);
        return false;
    }

    public void onClick(View view) {
        if (view.getId() == R.id.done) {
            List<String> callBackListOfIds = selectedIdsForCallback;
            String youCan;
            String options;
            String option;
            String message;
            if (callBackListOfIds.size() >= minSelectionLimit) {
                if (callBackListOfIds.size() <= maxSelectionLimit) {
                    tempPreviouslySelectedIdsList = new ArrayList<>(callBackListOfIds);
                    if (submitCallbackListener != null) {
                        submitCallbackListener.onSelected(callBackListOfIds, getSelectNameList(), getSelectedDataString());
                    }
                    dismiss();
                } else {
                    youCan = getResources().getString(R.string.you_can_only_select_upto);
                    options = getResources().getString(R.string.options);
                    option = getResources().getString(R.string.option);
                    message = "";
                    if (maxSelectionLimit > 1) {
                        message = youCan + " " + maxSelectionLimit + " " + options;
                    } else {
                        message = youCan + " " + maxSelectionLimit + " " + option;
                    }

                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                }
            } else {
                youCan = getResources().getString(R.string.please_select_atleast);
                options = getResources().getString(R.string.options);
                option = getResources().getString(R.string.option);
                message = "";
                if (minSelectionLimit > 1) {
                    message = youCan + " " + minSelectionLimit + " " + options;
                } else {
                    message = youCan + " " + minSelectionLimit + " " + option;
                }

                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        }

        if (view.getId() == R.id.cancel) {
            if (submitCallbackListener != null) {
                selectedIdsForCallback.clear();
                selectedIdsForCallback.addAll(tempPreviouslySelectedIdsList);
                submitCallbackListener.onCancel();
            }
            dismiss();
        }

    }

    private String getSelectedDataString() {
        String data = "";
        for(int i = 0; i < tempMainListOfAdapter.size(); ++i) {
            if (this.checkForSelection(tempMainListOfAdapter.get(i).getItemId())) {
                data = data + ", " + tempMainListOfAdapter.get(i).getItemName();
            }
        }

        if (data.length() > 0) {
            return data.substring(1);
        } else {
            return "";
        }
    }

    private List<String> getSelectNameList() {
        List<String> names = new ArrayList<>();

        for(int i = 0; i < tempMainListOfAdapter.size(); ++i) {
            if (this.checkForSelection(tempMainListOfAdapter.get(i).getItemId())) {
                names.add(tempMainListOfAdapter.get(i).getItemName());
            }
        }

        return names;
    }

    private boolean checkForSelection(String id) {
        for(int i = 0; i < selectedIdsForCallback.size(); ++i) {
            if (id.equals(selectedIdsForCallback.get(i))) {
                return true;
            }
        }

        return false;
    }

    public interface SubmitCallbackListener {
        void onSelected(List<String> var1, List<String> var2, String var3);

        void onCancel();
    }
}
