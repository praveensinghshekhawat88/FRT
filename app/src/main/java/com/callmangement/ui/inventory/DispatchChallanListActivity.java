package com.callmangement.ui.inventory;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.callmangement.network.APIService;
import com.callmangement.network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.adapter.DispatchChallanListActivityAdapter;
import com.callmangement.custom.CustomActivity;
import com.callmangement.databinding.ActivityDispatchChallanListBinding;
import com.callmangement.model.inventrory.ModelDispatchInvoice;
import com.callmangement.model.inventrory.ModelPartsDispatchInvoiceList;
import com.callmangement.utils.Constants;
import com.callmangement.utils.PrefManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DispatchChallanListActivity extends CustomActivity implements View.OnClickListener {
    ActivityDispatchChallanListBinding binding;
    private PrefManager prefManager;
    private List<ModelPartsDispatchInvoiceList> list = new ArrayList<>();
    private final String invoiceId = "0";
    private final String districtId = "0";
    private DispatchChallanListActivityAdapter adapter;
    private final Calendar myCalendarFromDate = Calendar.getInstance();
    private final Calendar myCalendarToDate = Calendar.getInstance();
    private final List<String> spinnerList = new ArrayList<>();
    private String filterType = "";
    private int checkFilter = 0;
    private final String myFormat = "yyyy-MM-dd";
    private String fromDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDispatchChallanListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.actionBar.ivThreeDot.setVisibility(View.GONE);
        binding.actionBar.ivBack.setVisibility(View.VISIBLE);
        binding.actionBar.layoutLanguage.setVisibility(View.GONE);
        binding.actionBar.buttonPDF.setVisibility(View.GONE);
        binding.actionBar.textToolbarTitle.setText(getResources().getString(R.string.create_new_challan));
        prefManager = new PrefManager(mContext);
        initView();
    }

    private void initView() {
        onClickListener();
        setUpSpinnerDataList();
    }

    private void onClickListener(){
        binding.spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (++checkFilter > 1) {
                    String item = adapterView.getItemAtPosition(i).toString();
                    if (!item.equalsIgnoreCase("--" + getResources().getString(R.string.select_filter) + "--")) {
                        Objects.requireNonNull(binding.textFromDate.getText()).clear();
                        Objects.requireNonNull(binding.textToDate.getText()).clear();
                        if (item.equalsIgnoreCase(getResources().getString(R.string.today))) {
                            filterType = getResources().getString(R.string.today);
                            binding.layoutDateRange.setVisibility(View.GONE);
                            Calendar calendar = Calendar.getInstance();
                            Date today = calendar.getTime();
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                            String todayDate = sdf.format(today);
                            getPartsDispatcherInvoice(todayDate,todayDate);

                        } else if (item.equalsIgnoreCase(getResources().getString(R.string.yesterday))) {
                            filterType = getResources().getString(R.string.yesterday);
                            binding.layoutDateRange.setVisibility(View.GONE);
                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.DAY_OF_YEAR, -1);
                            Date yesterday = calendar.getTime();
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                            String yesterdayDate = sdf.format(yesterday);
                            getPartsDispatcherInvoice(yesterdayDate,yesterdayDate);

                        } else if (item.equalsIgnoreCase(getResources().getString(R.string.current_month))) {
                            filterType = getResources().getString(R.string.current_month);
                            binding.layoutDateRange.setVisibility(View.GONE);
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                            calendar.set(Calendar.HOUR_OF_DAY, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            Date firstDateOfCurrentMonth = calendar.getTime();
                            String date1 = sdf.format(firstDateOfCurrentMonth);

                            Calendar calendar1 = Calendar.getInstance();
                            Date currentDateOfCurrentMonth = calendar1.getTime();
                            String date2 = sdf.format(currentDateOfCurrentMonth);
                            getPartsDispatcherInvoice(date1,date2);

                        } else if (item.equalsIgnoreCase(getResources().getString(R.string.previous_month))) {
                            filterType = getResources().getString(R.string.previous_month);
                            binding.layoutDateRange.setVisibility(View.GONE);
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                            Calendar aCalendar = Calendar.getInstance();
                            aCalendar.add(Calendar.MONTH, -1);
                            aCalendar.set(Calendar.DATE, 1);
                            Date firstDateOfPreviousMonth = aCalendar.getTime();
                            String date1 = sdf.format(firstDateOfPreviousMonth);

                            aCalendar.set(Calendar.DATE, aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                            Date lastDateOfPreviousMonth = aCalendar.getTime();
                            String date2 = sdf.format(lastDateOfPreviousMonth);
                            getPartsDispatcherInvoice(date1,date2);

                        } else if (item.equalsIgnoreCase(getResources().getString(R.string.custom_filter))) {
                            filterType = getResources().getString(R.string.custom_filter);
                            binding.layoutDateRange.setVisibility(View.VISIBLE);
                        }
                    } else {
                        filterType = "--" + getResources().getString(R.string.select_filter) + "--";
                        Objects.requireNonNull(binding.textFromDate.getText()).clear();
                        Objects.requireNonNull(binding.textToDate.getText()).clear();
                        binding.layoutDateRange.setVisibility(View.GONE);
                        getPartsDispatcherInvoice("","");
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.textFromDate.setOnClickListener(this);
        binding.textToDate.setOnClickListener(this);
        binding.actionBar.ivBack.setOnClickListener(this);
    }

    private void updateLabelFromDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        binding.textFromDate.setText(sdf.format(myCalendarFromDate.getTime()));
        Objects.requireNonNull(binding.textToDate.getText()).clear();
        fromDate = Objects.requireNonNull(binding.textFromDate.getText()).toString().trim();
    }

    private void updateLabelToDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        binding.textToDate.setText(sdf.format(myCalendarToDate.getTime()));
        String toDate = Objects.requireNonNull(binding.textToDate.getText()).toString().trim();
        getPartsDispatcherInvoice(fromDate, toDate);
    }

    private void setUpSpinnerDataList(){
//        filterType = "--" + getResources().getString(R.string.select_filter) + "--";
        filterType = getResources().getString(R.string.today);
        spinnerList.add("--" + getResources().getString(R.string.select_filter) + "--");
        spinnerList.add(getResources().getString(R.string.today));
        spinnerList.add(getResources().getString(R.string.yesterday));
        spinnerList.add(getResources().getString(R.string.current_month));
        spinnerList.add(getResources().getString(R.string.previous_month));
        spinnerList.add(getResources().getString(R.string.custom_filter));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerFilter.setAdapter(dataAdapter);
        binding.spinnerFilter.setSelection(1);
    }

    private void fetchDataByFilterType() {
        if (!filterType.equalsIgnoreCase("--" + getResources().getString(R.string.select_filter) + "--")) {
            Objects.requireNonNull(binding.textFromDate.getText()).clear();
            Objects.requireNonNull(binding.textToDate.getText()).clear();
            if (filterType.equalsIgnoreCase(getResources().getString(R.string.today))) {
                filterType = getResources().getString(R.string.today);
                binding.layoutDateRange.setVisibility(View.GONE);
                Calendar calendar = Calendar.getInstance();
                Date today = calendar.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                String todayDate = sdf.format(today);
                getPartsDispatcherInvoice(todayDate,todayDate);

            } else if (filterType.equalsIgnoreCase(getResources().getString(R.string.yesterday))) {
                filterType = getResources().getString(R.string.yesterday);
                binding.layoutDateRange.setVisibility(View.GONE);
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_YEAR, -1);
                Date yesterday = calendar.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                String yesterdayDate = sdf.format(yesterday);
                getPartsDispatcherInvoice(yesterdayDate,yesterdayDate);

            } else if (filterType.equalsIgnoreCase(getResources().getString(R.string.current_month))) {
                filterType = getResources().getString(R.string.current_month);
                binding.layoutDateRange.setVisibility(View.GONE);
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                Date firstDateOfCurrentMonth = calendar.getTime();
                String date1 = sdf.format(firstDateOfCurrentMonth);

                Calendar calendar1 = Calendar.getInstance();
                Date currentDateOfCurrentMonth = calendar1.getTime();
                String date2 = sdf.format(currentDateOfCurrentMonth);
                getPartsDispatcherInvoice(date1,date2);

            } else if (filterType.equalsIgnoreCase(getResources().getString(R.string.previous_month))) {
                filterType = getResources().getString(R.string.previous_month);
                binding.layoutDateRange.setVisibility(View.GONE);
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                Calendar aCalendar = Calendar.getInstance();
                aCalendar.add(Calendar.MONTH, -1);
                aCalendar.set(Calendar.DATE, 1);
                Date firstDateOfPreviousMonth = aCalendar.getTime();
                String date1 = sdf.format(firstDateOfPreviousMonth);

                aCalendar.set(Calendar.DATE, aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                Date lastDateOfPreviousMonth = aCalendar.getTime();
                String date2 = sdf.format(lastDateOfPreviousMonth);
                getPartsDispatcherInvoice(date1,date2);

            } else if (filterType.equalsIgnoreCase(getResources().getString(R.string.custom_filter))) {
                filterType = getResources().getString(R.string.custom_filter);
                binding.layoutDateRange.setVisibility(View.VISIBLE);
            }
        } else {
            filterType = "--" + getResources().getString(R.string.select_filter) + "--";
            Objects.requireNonNull(binding.textFromDate.getText()).clear();
            Objects.requireNonNull(binding.textToDate.getText()).clear();
            binding.layoutDateRange.setVisibility(View.GONE);
            getPartsDispatcherInvoice("","");
        }
    }

    private void getPartsDispatcherInvoice(String date1, String date2){
        if (Constants.isNetworkAvailable(mContext)) {
            APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            Call<ModelDispatchInvoice> call = service.getPartsDispatcherInvoices(invoiceId, prefManager.getUseR_Id(), districtId, date1, date2);
            showProgress();
            call.enqueue(new Callback<ModelDispatchInvoice>() {
                @Override
                public void onResponse(@NonNull Call<ModelDispatchInvoice> call, @NonNull Response<ModelDispatchInvoice> response) {
                    hideProgress();
                    if (response.isSuccessful()){
                        ModelDispatchInvoice model = response.body();
                        if (Objects.requireNonNull(model).getStatus().equals("200")){
                            list.clear();
                            list = model.getPartsDispatchInvoiceList();
                            if (list.size() > 0) {
                                binding.rvDispatchChallanList.setVisibility(View.VISIBLE);
                                binding.textNoDispatchChallan.setVisibility(View.GONE);
                                Collections.reverse(list);
                                setDispatcherInvoicePartsListAdapter(list);
                            }else {
                                binding.rvDispatchChallanList.setVisibility(View.GONE);
                                binding.textNoDispatchChallan.setVisibility(View.VISIBLE);
                            }
                        } else {
                            binding.rvDispatchChallanList.setVisibility(View.GONE);
                            binding.textNoDispatchChallan.setVisibility(View.VISIBLE);
//                            makeToast(model.getMessage());
                        }
                    } else {
                        makeToast(getResources().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ModelDispatchInvoice> call, @NonNull Throwable t) {
                    hideProgress();
                    makeToast(getResources().getString(R.string.error_message));
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }

    private void setDispatcherInvoicePartsListAdapter(List<ModelPartsDispatchInvoiceList> list){
        adapter = new DispatchChallanListActivityAdapter(mContext, list);
        binding.rvDispatchChallanList.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));
        binding.rvDispatchChallanList.setAdapter(adapter);
    }

    /*private void getRefreshPartsDispatcherInvoice(){
        if (Constants.isNetworkAvailable(mContext)) {
            APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            Call<ModelDispatchInvoice> call = service.getPartsDispatcherInvoices(invoiceId, prefManager.getUseR_Id(), districtId,"","");
            call.enqueue(new Callback<ModelDispatchInvoice>() {
                @Override
                public void onResponse(@NonNull Call<ModelDispatchInvoice> call, @NonNull Response<ModelDispatchInvoice> response) {
                    hideProgress();
                    if (response.isSuccessful()){
                        ModelDispatchInvoice model = response.body();
                        if (Objects.requireNonNull(model).getStatus().equals("200")){
                            list.clear();
                            list = model.getPartsDispatchInvoiceList();
                            if (list.size() > 0) {
                                binding.rvDispatchChallanList.setVisibility(View.VISIBLE);
                                binding.textNoDispatchChallan.setVisibility(View.GONE);
                                Collections.reverse(list);
                                setDispatcherInvoicePartsListAdapter(list);
                            }else {
                                binding.rvDispatchChallanList.setVisibility(View.GONE);
                                binding.textNoDispatchChallan.setVisibility(View.VISIBLE);
                            }
                        } else {
                            binding.rvDispatchChallanList.setVisibility(View.GONE);
                            binding.textNoDispatchChallan.setVisibility(View.VISIBLE);
//                            makeToast(model.getMessage());
                        }
                    } else {
                        makeToast(getResources().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ModelDispatchInvoice> call, @NonNull Throwable t) {
                    hideProgress();
                    makeToast(getResources().getString(R.string.error_message));
                }
            });
        } else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }

    @SuppressLint("SetTextI18n")
    public void dialogDeleteInvoice(String invoiceId) {
        try {
            final Dialog dialog = new Dialog(mContext);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_delete_invoice);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);

            TextView textInvoiceNumber = dialog.findViewById(R.id.textInvoiceNumber);
            TextView textYes = dialog.findViewById(R.id.textYes);
            TextView textNo = dialog.findViewById(R.id.textNo);
            EditText inputRemark = dialog.findViewById(R.id.inputDeleteRemark);

            textInvoiceNumber.setText(""+invoiceId);

            textYes.setOnClickListener(view -> {
                String remark = inputRemark.getText().toString().trim();
                if (!remark.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setMessage(getResources().getString(R.string.are_you_sure_you_want_to_delete_this_invoice))
                            .setCancelable(false)
                            .setPositiveButton(getResources().getString(R.string.yes), (dlg, id) -> {
                                hideDialogKeyboard(mContext,inputRemark);
                                deleteInvoice(invoiceId, remark);
                                dialog.dismiss();
                                dlg.dismiss();
                            })
                            .setNegativeButton(getResources().getString(R.string.no), (dlg, id) -> {
                                dialog.dismiss();
                                dlg.dismiss();
                            });
                    AlertDialog alert = builder.create();
                    alert.setTitle(getResources().getString(R.string.alert));
                    alert.show();
                } else {
                    makeToast(getResources().getString(R.string.please_enter_remark));
                }
            });

            textNo.setOnClickListener(view -> dialog.dismiss());

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteInvoice(String invoiceId, String remark){
        if (Constants.isNetworkAvailable(mContext)) {
            APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);
            Call<ModelInventoryResponse> call = service.deleteInvoice(prefManager.getUseR_Id(), invoiceId, remark);
            showProgress();
            call.enqueue(new Callback<ModelInventoryResponse>() {
                @Override
                public void onResponse(@NonNull Call<ModelInventoryResponse> call, @NonNull Response<ModelInventoryResponse> response) {
                    if (response.isSuccessful()) {
                        ModelInventoryResponse model = response.body();
                        if (Objects.requireNonNull(model).getStatus().equals("200")) {
//                            adapter.notifyItemRemoved(position);
                            getRefreshPartsDispatcherInvoice();
                        } else {
                            hideProgress();
                            makeToast(model.getMessage());
                        }
                    } else {
                        hideProgress();
                        makeToast(getResources().getString(R.string.error));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ModelInventoryResponse> call, @NonNull Throwable t) {
                    hideProgress();
                    makeToast(getResources().getString(R.string.error_message));
                }
            });
        }else {
            makeToast(getResources().getString(R.string.no_internet_connection));
        }
    }*/

    @Override
    protected void onResume() {
        super.onResume();
//        binding.spinnerFilter.setSelection(1);
//        Calendar calendar = Calendar.getInstance();
//        Date today = calendar.getTime();
//        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//        String todayDate = sdf.format(today);
//        getPartsDispatcherInvoice(todayDate,todayDate);
        fetchDataByFilterType();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.iv_back){
            onBackPressed();

        } else if (id == R.id.textFromDate){
            DatePickerDialog.OnDateSetListener dateFromDate = (viewFromData, year, monthOfYear, dayOfMonth) -> {
                myCalendarFromDate.set(Calendar.YEAR, year);
                myCalendarFromDate.set(Calendar.MONTH, monthOfYear);
                myCalendarFromDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelFromDate();
            };
            DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, dateFromDate, myCalendarFromDate
                    .get(Calendar.YEAR), myCalendarFromDate.get(Calendar.MONTH),
                    myCalendarFromDate.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();

        } else if (id == R.id.textToDate){
            DatePickerDialog.OnDateSetListener dateToDate = (viewToDate, year, monthOfYear, dayOfMonth) -> {
                myCalendarToDate.set(Calendar.YEAR, year);
                myCalendarToDate.set(Calendar.MONTH, monthOfYear);
                myCalendarToDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelToDate();
            };
            DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, dateToDate, myCalendarToDate
                    .get(Calendar.YEAR), myCalendarToDate.get(Calendar.MONTH),
                    myCalendarToDate.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        }

    }
}