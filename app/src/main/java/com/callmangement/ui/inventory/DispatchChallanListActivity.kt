package com.callmangement.ui.inventory

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.recyclerview.widget.LinearLayoutManager
import com.callmangement.R
import com.callmangement.adapter.DispatchChallanListActivityAdapter
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityDispatchChallanListBinding
import com.callmangement.model.inventrory.ModelDispatchInvoice
import com.callmangement.model.inventrory.ModelPartsDispatchInvoiceList
import com.callmangement.network.APIService
import com.callmangement.network.RetrofitInstance
import com.callmangement.utils.Constants.isNetworkAvailable
import com.callmangement.utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Locale
import java.util.Objects

class DispatchChallanListActivity : CustomActivity(), View.OnClickListener {
    var binding: ActivityDispatchChallanListBinding? = null
    private var prefManager: PrefManager? = null
    private var list: MutableList<ModelPartsDispatchInvoiceList?> = ArrayList()
    private val invoiceId = "0"
    private val districtId = "0"
    private var adapter: DispatchChallanListActivityAdapter? = null
    private val myCalendarFromDate: Calendar = Calendar.getInstance()
    private val myCalendarToDate: Calendar = Calendar.getInstance()
    private val spinnerList: MutableList<String> = ArrayList()
    private var filterType = ""
    private var checkFilter = 0
    private val myFormat = "yyyy-MM-dd"
    private var fromDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDispatchChallanListBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.buttonPDF.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.create_new_challan)
        prefManager = PrefManager(mContext!!)
        initView()
    }

    private fun initView() {
        onClickListener()
        setUpSpinnerDataList()
    }

    private fun onClickListener() {
        binding!!.spinnerFilter.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>,
                    view: View,
                    i: Int,
                    l: Long
                ) {
                    if (++checkFilter > 1) {
                        val item = adapterView.getItemAtPosition(i).toString()
                        if (!item.equals(
                                "--" + resources.getString(R.string.select_filter) + "--",
                                ignoreCase = true
                            )
                        ) {
                            binding!!.textFromDate.text!!.clear()
                            binding!!.textToDate.text!!.clear()
                            if (item.equals(
                                    resources.getString(R.string.today),
                                    ignoreCase = true
                                )
                            ) {
                                filterType = resources.getString(R.string.today)
                                binding!!.layoutDateRange.visibility = View.GONE
                                val calendar = Calendar.getInstance()
                                val today = calendar.time
                                val sdf =
                                    SimpleDateFormat(myFormat, Locale.US)
                                val todayDate = sdf.format(today)
                                getPartsDispatcherInvoice(todayDate, todayDate)
                            } else if (item.equals(
                                    resources.getString(R.string.yesterday),
                                    ignoreCase = true
                                )
                            ) {
                                filterType = resources.getString(R.string.yesterday)
                                binding!!.layoutDateRange.visibility = View.GONE
                                val calendar = Calendar.getInstance()
                                calendar.add(Calendar.DAY_OF_YEAR, -1)
                                val yesterday = calendar.time
                                val sdf =
                                    SimpleDateFormat(myFormat, Locale.US)
                                val yesterdayDate = sdf.format(yesterday)
                                getPartsDispatcherInvoice(yesterdayDate, yesterdayDate)
                            } else if (item.equals(
                                    resources.getString(R.string.current_month),
                                    ignoreCase = true
                                )
                            ) {
                                filterType = resources.getString(R.string.current_month)
                                binding!!.layoutDateRange.visibility = View.GONE
                                val sdf =
                                    SimpleDateFormat(myFormat, Locale.US)

                                val calendar = Calendar.getInstance()
                                calendar[Calendar.DAY_OF_MONTH] =
                                    calendar.getActualMinimum(Calendar.DAY_OF_MONTH)
                                calendar[Calendar.HOUR_OF_DAY] = 0
                                calendar[Calendar.MINUTE] = 0
                                calendar[Calendar.SECOND] = 0
                                calendar[Calendar.MILLISECOND] = 0
                                val firstDateOfCurrentMonth = calendar.time
                                val date1 = sdf.format(firstDateOfCurrentMonth)

                                val calendar1 = Calendar.getInstance()
                                val currentDateOfCurrentMonth = calendar1.time
                                val date2 = sdf.format(currentDateOfCurrentMonth)
                                getPartsDispatcherInvoice(date1, date2)
                            } else if (item.equals(
                                    resources.getString(R.string.previous_month),
                                    ignoreCase = true
                                )
                            ) {
                                filterType = resources.getString(R.string.previous_month)
                                binding!!.layoutDateRange.visibility = View.GONE
                                val sdf =
                                    SimpleDateFormat(myFormat, Locale.US)
                                val aCalendar = Calendar.getInstance()
                                aCalendar.add(Calendar.MONTH, -1)
                                aCalendar[Calendar.DATE] = 1
                                val firstDateOfPreviousMonth = aCalendar.time
                                val date1 = sdf.format(firstDateOfPreviousMonth)

                                aCalendar[Calendar.DATE] =
                                    aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                                val lastDateOfPreviousMonth = aCalendar.time
                                val date2 = sdf.format(lastDateOfPreviousMonth)
                                getPartsDispatcherInvoice(date1, date2)
                            } else if (item.equals(
                                    resources.getString(R.string.custom_filter),
                                    ignoreCase = true
                                )
                            ) {
                                filterType = resources.getString(R.string.custom_filter)
                                binding!!.layoutDateRange.visibility = View.VISIBLE
                            }
                        } else {
                            filterType = "--" + resources.getString(R.string.select_filter) + "--"
                            binding!!.textFromDate.text!!.clear()
                            binding!!.textToDate.text!!.clear()
                            binding!!.layoutDateRange.visibility = View.GONE
                            getPartsDispatcherInvoice("", "")
                        }
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            }

        binding!!.textFromDate.setOnClickListener(this)
        binding!!.textToDate.setOnClickListener(this)
        binding!!.actionBar.ivBack.setOnClickListener(this)
    }

    private fun updateLabelFromDate() {
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding!!.textFromDate.setText(sdf.format(myCalendarFromDate.time))
        binding!!.textToDate.text!!.clear()
        fromDate = Objects.requireNonNull(binding!!.textFromDate.text).toString().trim { it <= ' ' }
    }

    private fun updateLabelToDate() {
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding!!.textToDate.setText(sdf.format(myCalendarToDate.time))
        val toDate = Objects.requireNonNull(binding!!.textToDate.text).toString().trim { it <= ' ' }
        getPartsDispatcherInvoice(fromDate, toDate)
    }

    private fun setUpSpinnerDataList() {
//        filterType = "--" + getResources().getString(R.string.select_filter) + "--";
        filterType = resources.getString(R.string.today)
        spinnerList.add("--" + resources.getString(R.string.select_filter) + "--")
        spinnerList.add(resources.getString(R.string.today))
        spinnerList.add(resources.getString(R.string.yesterday))
        spinnerList.add(resources.getString(R.string.current_month))
        spinnerList.add(resources.getString(R.string.previous_month))
        spinnerList.add(resources.getString(R.string.custom_filter))

        val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerList)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding!!.spinnerFilter.adapter = dataAdapter
        binding!!.spinnerFilter.setSelection(1)
    }

    private fun fetchDataByFilterType() {
        if (!filterType.equals(
                "--" + resources.getString(R.string.select_filter) + "--",
                ignoreCase = true
            )
        ) {
            binding!!.textFromDate.text!!.clear()
            binding!!.textToDate.text!!.clear()
            if (filterType.equals(resources.getString(R.string.today), ignoreCase = true)) {
                filterType = resources.getString(R.string.today)
                binding!!.layoutDateRange.visibility = View.GONE
                val calendar = Calendar.getInstance()
                val today = calendar.time
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                val todayDate = sdf.format(today)
                getPartsDispatcherInvoice(todayDate, todayDate)
            } else if (filterType.equals(
                    resources.getString(R.string.yesterday),
                    ignoreCase = true
                )
            ) {
                filterType = resources.getString(R.string.yesterday)
                binding!!.layoutDateRange.visibility = View.GONE
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                val yesterday = calendar.time
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                val yesterdayDate = sdf.format(yesterday)
                getPartsDispatcherInvoice(yesterdayDate, yesterdayDate)
            } else if (filterType.equals(
                    resources.getString(R.string.current_month),
                    ignoreCase = true
                )
            ) {
                filterType = resources.getString(R.string.current_month)
                binding!!.layoutDateRange.visibility = View.GONE
                val sdf = SimpleDateFormat(myFormat, Locale.US)

                val calendar = Calendar.getInstance()
                calendar[Calendar.DAY_OF_MONTH] =
                    calendar.getActualMinimum(Calendar.DAY_OF_MONTH)
                calendar[Calendar.HOUR_OF_DAY] = 0
                calendar[Calendar.MINUTE] = 0
                calendar[Calendar.SECOND] = 0
                calendar[Calendar.MILLISECOND] = 0
                val firstDateOfCurrentMonth = calendar.time
                val date1 = sdf.format(firstDateOfCurrentMonth)

                val calendar1 = Calendar.getInstance()
                val currentDateOfCurrentMonth = calendar1.time
                val date2 = sdf.format(currentDateOfCurrentMonth)
                getPartsDispatcherInvoice(date1, date2)
            } else if (filterType.equals(
                    resources.getString(R.string.previous_month),
                    ignoreCase = true
                )
            ) {
                filterType = resources.getString(R.string.previous_month)
                binding!!.layoutDateRange.visibility = View.GONE
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                val aCalendar = Calendar.getInstance()
                aCalendar.add(Calendar.MONTH, -1)
                aCalendar[Calendar.DATE] = 1
                val firstDateOfPreviousMonth = aCalendar.time
                val date1 = sdf.format(firstDateOfPreviousMonth)

                aCalendar[Calendar.DATE] =
                    aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                val lastDateOfPreviousMonth = aCalendar.time
                val date2 = sdf.format(lastDateOfPreviousMonth)
                getPartsDispatcherInvoice(date1, date2)
            } else if (filterType.equals(
                    resources.getString(R.string.custom_filter),
                    ignoreCase = true
                )
            ) {
                filterType = resources.getString(R.string.custom_filter)
                binding!!.layoutDateRange.visibility = View.VISIBLE
            }
        } else {
            filterType = "--" + resources.getString(R.string.select_filter) + "--"
            binding!!.textFromDate.text!!.clear()
            binding!!.textToDate.text!!.clear()
            binding!!.layoutDateRange.visibility = View.GONE
            getPartsDispatcherInvoice("", "")
        }
    }

    private fun getPartsDispatcherInvoice(date1: String, date2: String) {
        if (isNetworkAvailable(mContext!!)) {
            val service = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            val call = service.getPartsDispatcherInvoices(
                invoiceId,
                prefManager!!.useR_Id,
                districtId,
                date1,
                date2
            )
            showProgress()
            call.enqueue(object : Callback<ModelDispatchInvoice?> {
                override fun onResponse(
                    call: Call<ModelDispatchInvoice?>,
                    response: Response<ModelDispatchInvoice?>
                ) {
                    hideProgress()
                    if (response.isSuccessful) {
                        val model = response.body()
                        if (model!!.status == "200") {
                            list.clear()
                            list = model!!.partsDispatchInvoiceList
                            if (list.size > 0) {
                                binding!!.rvDispatchChallanList.visibility = View.VISIBLE
                                binding!!.textNoDispatchChallan.visibility = View.GONE
                                Collections.reverse(list)
                                setDispatcherInvoicePartsListAdapter(list)
                            } else {
                                binding!!.rvDispatchChallanList.visibility = View.GONE
                                binding!!.textNoDispatchChallan.visibility = View.VISIBLE
                            }
                        } else {
                            binding!!.rvDispatchChallanList.visibility = View.GONE
                            binding!!.textNoDispatchChallan.visibility = View.VISIBLE
                            //                            makeToast(model.getMessage());
                        }
                    } else {
                        makeToast(resources.getString(R.string.error))
                    }
                }

                override fun onFailure(call: Call<ModelDispatchInvoice?>, t: Throwable) {
                    hideProgress()
                    makeToast(resources.getString(R.string.error_message))
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }

    private fun setDispatcherInvoicePartsListAdapter(list: List<ModelPartsDispatchInvoiceList?>) {
        adapter = DispatchChallanListActivityAdapter(mContext!!, list)
        binding!!.rvDispatchChallanList.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        binding!!.rvDispatchChallanList.adapter = adapter
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
    override fun onResume() {
        super.onResume()
        //        binding.spinnerFilter.setSelection(1);
//        Calendar calendar = Calendar.getInstance();
//        Date today = calendar.getTime();
//        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//        String todayDate = sdf.format(today);
//        getPartsDispatcherInvoice(todayDate,todayDate);
        fetchDataByFilterType()
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.iv_back) {
            onBackPressed()
        } else if (id == R.id.textFromDate) {
            val dateFromDate =
                OnDateSetListener { viewFromData: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    myCalendarFromDate[Calendar.YEAR] =
                        year
                    myCalendarFromDate[Calendar.MONTH] = monthOfYear
                    myCalendarFromDate[Calendar.DAY_OF_MONTH] = dayOfMonth
                    updateLabelFromDate()
                }
            val datePickerDialog = DatePickerDialog(
                mContext!!, dateFromDate,
                myCalendarFromDate[Calendar.YEAR],
                myCalendarFromDate[Calendar.MONTH],
                myCalendarFromDate[Calendar.DAY_OF_MONTH]
            )
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        } else if (id == R.id.textToDate) {
            val dateToDate =
                OnDateSetListener { viewToDate: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    myCalendarToDate[Calendar.YEAR] =
                        year
                    myCalendarToDate[Calendar.MONTH] = monthOfYear
                    myCalendarToDate[Calendar.DAY_OF_MONTH] = dayOfMonth
                    updateLabelToDate()
                }
            val datePickerDialog = DatePickerDialog(
                mContext!!, dateToDate, myCalendarToDate[Calendar.YEAR],
                myCalendarToDate[Calendar.MONTH],
                myCalendarToDate[Calendar.DAY_OF_MONTH]
            )
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis() - 1000
            datePickerDialog.show()
        }
    }
}