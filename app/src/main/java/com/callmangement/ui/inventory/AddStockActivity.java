package com.callmangement.ui.inventory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.callmangement.network.APIService;
import com.callmangement.network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.adapter.DialogAddStockListAdapter;
import com.callmangement.custom.CustomActivity;
import com.callmangement.custom.SpinnerAdapter;
import com.callmangement.databinding.ActivityAddStockBinding;
import com.callmangement.model.inventrory.ModelAddStock;
import com.callmangement.model.inventrory.ModelPartsList;
import com.callmangement.report_pdf.AddStockPdfActivity;
import com.callmangement.utils.Constants;
import com.callmangement.utils.PrefManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddStockActivity extends CustomActivity implements View.OnClickListener {
    private ActivityAddStockBinding binding;
    private InventoryViewModel inventoryViewModel;
    private PrefManager prefManager;
    private final List<ModelAddStock> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddStockBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.add_stock));
        inventoryViewModel = ViewModelProviders.of(this).get(InventoryViewModel.class);
        prefManager = new PrefManager(mContext);
        initView();
    }

    private void initView() {
        setUpOnClickListener();
        getPartsList();
    }

    private void setUpOnClickListener(){
        binding.actionBar.ivBack.setOnClickListener(this);
        binding.buttonAddLayout.setOnClickListener(this);
        binding.buttonAddStock.setOnClickListener(this);
    }

    private void getPartsList(){
        inventoryViewModel.getPartsList().observe(this, modelParts -> {
            if (modelParts.getStatus().equals("200")){
                List<ModelPartsList> modelProductsList = modelParts.getParts();
                if (modelProductsList != null && modelProductsList.size() > 0) {
                    Collections.reverse(modelProductsList);
                    ModelPartsList l = new ModelPartsList();
                    l.setItemId(String.valueOf(-1));
                    l.setItemName("--" + getResources().getString(R.string.select_products) + "--");
                    modelProductsList.add(l);
                    Collections.reverse(modelProductsList);
                    Constants.modelProductLists = modelProductsList;
                    setUpPartsListAdapter();
                }
            }
        });
    }

    private void setUpPartsListAdapter(){
        list.clear();
        list.add(new ModelAddStock("", "","", (List<ModelPartsList>) Constants.modelProductLists));
        setUpPartsList();
    }

    private void setUpPartsList(){
        binding.partsListLay.removeAllViews();

        for (int i = 0; i < list.size(); i++) {

            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.item_add_stock_activity, null, true);

            RelativeLayout topLay = itemView.findViewById(R.id.rl_product);
            Spinner spinnerItemName = itemView.findViewById(R.id.spinnerItemName);
            TextView tvSpinner = itemView.findViewById(R.id.tv_spinner);
            EditText inputItemQuantity = itemView.findViewById(R.id.inputItemQuantity);
            ImageView buttonRemoveLayout = itemView.findViewById(R.id.buttonRemoveLayout);

            if (list.get(i).isFlag())
                spinnerItemName.setVisibility(View.GONE);
            else spinnerItemName.setVisibility(View.VISIBLE);

            tvSpinner.setText(list.get(i).getItemName());
            list.get(i).setRelativeLayout(topLay);
            spinnerItemName.setAdapter(new SpinnerAdapter(mContext,list.get(i).getSpinnerItemList()));
            spinnerItemName.setSelection(list.get(i).getSpinnerSelectedIndex());

            if (list.get(i).isItemSelectFlag()) {
                inputItemQuantity.setText(list.get(i).getQty());
                inputItemQuantity.setEnabled(true);
            }

            getCurrentFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(inputItemQuantity.getWindowToken(), 0);
            inputItemQuantity.requestFocus();

            spinnerItemName.setTag(1000 + i);
            inputItemQuantity.setTag(2000 + i);
            buttonRemoveLayout.setTag(3000 + i);

            spinnerItemName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int spinnerPosition, long id) {
                    final int tag = (Integer) spinnerItemName.getTag() - 1000;
                    if (spinnerPosition != 0){
                        list.get(tag).setItemSelectFlag(true);
                        list.get(tag).setSpinnerSelectedIndex(spinnerPosition);
                        list.get(tag).setId(list.get(tag).getSpinnerItemList().get(spinnerPosition).getItemId());
                        list.get(tag).setItemName(list.get(tag).getSpinnerItemList().get(spinnerPosition).getItemName());

                    } else {
                        list.get(tag).setItemSelectFlag(false);
                        list.get(tag).setSpinnerSelectedIndex(spinnerPosition);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            inputItemQuantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }
                @Override
                public void afterTextChanged(Editable s) {
                    final int tag = (Integer) inputItemQuantity.getTag() - 2000;
                    if (!s.toString().equals("")) {
                        list.get(tag).setQty(s.toString());
                        list.get(tag).setItemSelectFlag(true);
                    } else {
                        list.get(tag).setQty(s.toString());
                        if (list.get(tag).getQty().equals(""))
                            list.get(tag).setItemSelectFlag(false);
                    }
                }
            });

            buttonRemoveLayout.setOnClickListener(view -> {
                final int tag = (Integer) buttonRemoveLayout.getTag() - 3000;
                if (list.size() > 1) {
                    int spinnerPosition = list.get(tag).getSpinnerSelectedIndex();
                    list.get(tag).getSpinnerItemList().get(spinnerPosition).setVisibleItemFlag(false);
                    list.remove(tag);
                    setUpPartsList();
                }
            });
            binding.partsListLay.addView(itemView);
        }
    }

    private void addPartsInStock() {
        if (Constants.isNetworkAvailable(mContext)) {
            JSONArray jsonArray = new JSONArray();
            for (ModelAddStock model : list) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("itemId", model.getId());
                    jsonObject.put("item_Qty", model.getQty());
                    jsonArray.put(jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (jsonArray.length() > 0) {
                RequestBody jsonArrBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), String.valueOf(jsonArray));
                APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
                showProgress();
                Call<ResponseBody> call = service.updatePartsStock("0", prefManager.getUseR_Id(), "1", "", jsonArrBody);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        hideProgress();
                        try {
                            if (response.isSuccessful()) {
                                if (response.code() == 200) {
                                    if (response.body() != null) {
                                        String responseStr = response.body().string();
                                        JSONObject jsonObject = new JSONObject(responseStr);
                                        String status = jsonObject.optString("status");
                                        String message = jsonObject.optString("message");
                                        if (status.equals("200")) {
//                                            makeToast(getResources().getString(R.string.stock_added_successfully));
                                            Constants.modelAddStock = list;
                                            startActivity(new Intent(mContext, AddStockPdfActivity.class)
                                                    .putExtra("name", prefManager.getUseR_NAME())
                                                    .putExtra("email", prefManager.getUseR_EMAIL()));
                                            finish();
                                        } else makeToast(message);
                                    }
                                }
                            } else {
                                makeToast(getResources().getString(R.string.error));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        hideProgress();
                        makeToast(getResources().getString(R.string.error_message));
                    }
                });
            } else {
                makeToast(getResources().getString(R.string.please_select_item_or_quantity));
            }
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }

    private void addItem() {
        if (list.size() > 0) {
            int lastIndex = list.size() - 1;
            if (!list.get(lastIndex).getQty().equals("") && list.get(lastIndex).getSpinnerSelectedIndex() != 0) {
                for (int i = 0; i < list.size(); i++) {
                    int spinnerPosition = list.get(i).getSpinnerSelectedIndex();
                    list.get(i).setId(list.get(i).getSpinnerItemList().get(spinnerPosition).getItemId());
                    list.get(i).setItemName(list.get(i).getSpinnerItemList().get(spinnerPosition).getItemName());
                    list.get(i).getSpinnerItemList().get(spinnerPosition).setVisibleItemFlag(true);
                    list.get(i).setFlag(true);
                }
                list.add(new ModelAddStock("", "", "", (List<ModelPartsList>) Constants.modelProductLists));
                setUpPartsList();
            } else
                makeToast(getResources().getString(R.string.please_select_item_or_quantity));
        }
    }

    private void dialogAddStockList(List<ModelAddStock> list) {
        try {
            final Dialog dialog = new Dialog(mContext);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_add_stock_list);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);

            RecyclerView rvStockList = dialog.findViewById(R.id.rv_stock_list);
            TextView textCancel = dialog.findViewById(R.id.textCancel);
            TextView textConfirm = dialog.findViewById(R.id.textConfirm);

            rvStockList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
            rvStockList.setAdapter(new DialogAddStockListAdapter(mContext, list));

            textCancel.setOnClickListener(view -> dialog.dismiss());

            textConfirm.setOnClickListener(view -> {
                dialog.dismiss();
                addPartsInStock();
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back){
            onBackPressed();
        } else if (id == R.id.buttonAddLayout) {
            addItem();
        } else if (id == R.id.buttonAddStock) {
            if (list.size() > 0) {
                int lastIndex = list.size() - 1;
                if (!list.get(lastIndex).getQty().equals("") && list.get(lastIndex).getSpinnerSelectedIndex() != 0) {
                    dialogAddStockList(list);
                } else
                    Toast.makeText(mContext, getResources().getString(R.string.please_select_item_or_quantity), Toast.LENGTH_SHORT).show();
            }
        }
    }
}