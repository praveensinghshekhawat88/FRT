package com.callmangement.ui.complaint

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.callmangement.R
import com.callmangement.adapter.ChallanUploadListAdapter
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityEditChallanComplaintListBinding
import com.callmangement.model.complaints.ModelChallanUploadComplaint
import com.callmangement.model.complaints.ModelComplaintList
import com.callmangement.model.district.ModelDistrictList
import com.callmangement.model.tehsil.ModelTehsilList
import com.callmangement.utils.PrefManager
import java.util.Collections
import java.util.Objects

class EditChallanComplaintListActivity : CustomActivity() {
    private var binding: ActivityEditChallanComplaintListBinding? = null
    private val mContext: Context? = null
    private val REQUEST_CODE = 1
    private var adapter: ChallanUploadListAdapter? = null
    private var prefManager: PrefManager? = null
    private val tehsilList: MutableList<ModelTehsilList?> = ArrayList()
    private var tehsil_List_main: List<ModelTehsilList>? = ArrayList()
    private var districtList: List<ModelDistrictList>? = ArrayList()
    private val checkTehsil = 0
    private val checkDistrict = 0
    private var tehsilNameEng = ""
    private var districtNameEng = ""
    private var modelComplaintList: MutableList<ModelComplaintList>? = ArrayList()
    private var viewModel: ComplaintViewModel? = null
    private var filterType: String? = ""
    private val complainStatusId = ""
    private var complainRegNo = ""
    private var districtId: String? = "0"
    private val myFormat = "yyyy-MM-dd"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_edit_challan_complaint_list)

        mContext = this@EditChallanComplaintListActivity

        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.buttonPDF.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.complaints)
        viewModel = ViewModelProviders.of(this).get(
            ComplaintViewModel::class.java
        )

        districtId = intent.getStringExtra("districtId")
        filterType = intent.getStringExtra("filter_type")
        tehsil_List_main = intent.getSerializableExtra("tehsil_list") as List<ModelTehsilList>?
        districtList = intent.getSerializableExtra("district_list") as List<ModelDistrictList>?
        prefManager = PrefManager(mContext)
        binding!!.textNoComplaint.visibility = View.GONE
        tehsilNameEng = "--" + resources.getString(R.string.tehsil) + "--"
        districtNameEng = "--" + resources.getString(R.string.district) + "--"


        initView()
        //    fetchDataByFilterType();
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initView() {
        //    tehsilList.addAll(tehsil_List_main);


//        if (prefManager.getUSER_TYPE_ID().equals("4") && prefManager.getUSER_TYPE().equalsIgnoreCase("ServiceEngineer")) { // for service engineer
//            binding.rlTehsil.setVisibility(View.VISIBLE);
//            binding.rlDistrict.setVisibility(View.GONE);
//            if (tehsilList.size() > 0) {
//                Collections.reverse(tehsilList);
//                ModelTehsilList l = new ModelTehsilList();
//                l.setTehsilId(String.valueOf(-1));
//                l.setTehsilNameEng("--" + getResources().getString(R.string.tehsil) + "--");
//                tehsilList.add(l);
//                Collections.reverse(tehsilList);
//
//                ArrayAdapter<ModelTehsilList> dataAdapter = new ArrayAdapter<>(mContext, R.layout.spinner_item, tehsilList);
//                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                binding.spinnerTehsil.setAdapter(dataAdapter);
//            }
//
//        }
//        else if (prefManager.getUSER_TYPE_ID().equalsIgnoreCase("6") && prefManager.getUSER_TYPE().equalsIgnoreCase("DSO")) { // for dso
//            binding.rlTehsil.setVisibility(View.VISIBLE);
//            binding.rlDistrict.setVisibility(View.GONE);
//            if (tehsilList.size() != 0) {
//                Collections.reverse(tehsilList);
//                ModelTehsilList l = new ModelTehsilList();
//                l.setTehsilId(String.valueOf(-1));
//                l.setTehsilNameEng("--" + getResources().getString(R.string.tehsil) + "--");
//                tehsilList.add(l);
//                Collections.reverse(tehsilList);
//
//                ArrayAdapter<ModelTehsilList> dataAdapter = new ArrayAdapter<>(mContext, R.layout.spinner_item, tehsilList);
//                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                binding.spinnerTehsil.setAdapter(dataAdapter);
//            }
//
//        }
//        else if (prefManager.getUSER_TYPE_ID().equals("2") && prefManager.getUSER_TYPE().equalsIgnoreCase("Manager")) { // for manager
//            binding.rlTehsil.setVisibility(View.VISIBLE);
//            binding.rlDistrict.setVisibility(View.VISIBLE);
//            updateTehsilByDistrictId(districtId);
//            if (districtList != null && districtList.size() > 0) {
//                ArrayAdapter<ModelDistrictList> dataAdapter = new ArrayAdapter<>(mContext, R.layout.spinner_item, districtList);
//                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                binding.spinnerDistrict.setAdapter(dataAdapter);
//
//                if (!districtId.equalsIgnoreCase("0")) {
//                    for (int i = 0; i < districtList.size(); i++) {
//                        if (districtList.get(i).getDistrictId().equals(districtId)) {
//                            binding.spinnerDistrict.setSelection(i);
//                        }
//                    }
//                }
//
//            }
//        }
//        else if (prefManager.getUSER_TYPE_ID().equals("5") && prefManager.getUSER_TYPE().equalsIgnoreCase("ServiceCentre")) { // for service center
//            binding.rlTehsil.setVisibility(View.VISIBLE);
//            binding.rlDistrict.setVisibility(View.VISIBLE);
//            updateTehsilByDistrictId(districtId);
//            if (districtList != null && districtList.size() > 0) {
//                ArrayAdapter<ModelDistrictList> dataAdapter = new ArrayAdapter<>(mContext, R.layout.spinner_item, districtList);
//                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                binding.spinnerDistrict.setAdapter(dataAdapter);
//
//                if (!districtId.equalsIgnoreCase("0")) {
//                    for (int i = 0; i < districtList.size(); i++) {
//                        if (districtList.get(i).getDistrictId().equals(districtId)) {
//                            binding.spinnerDistrict.setSelection(i);
//                        }
//                    }
//                }
//
//            }
//        }
//        else if (prefManager.getUSER_TYPE_ID().equals("1") && prefManager.getUSER_TYPE().equalsIgnoreCase("Admin")) { // for Admin
//            binding.rlTehsil.setVisibility(View.VISIBLE);
//            binding.rlDistrict.setVisibility(View.VISIBLE);
//            updateTehsilByDistrictId(districtId);
//            if (districtList != null && districtList.size() > 0) {
//                ArrayAdapter<ModelDistrictList> dataAdapter = new ArrayAdapter<>(mContext, R.layout.spinner_item, districtList);
//                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                binding.spinnerDistrict.setAdapter(dataAdapter);
//
//                if (!districtId.equalsIgnoreCase("0")) {
//                    for (int i = 0; i < districtList.size(); i++) {
//                        if (districtList.get(i).getDistrictId().equals(districtId)) {
//                            binding.spinnerDistrict.setSelection(i);
//                        }
//                    }
//                }
//            }
//        }


        adapter = ChallanUploadListAdapter(mContext)
        adapter!!.notifyDataSetChanged()
        binding!!.rvComplaintPending.setHasFixedSize(true)
        binding!!.rvComplaintPending.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        binding!!.rvComplaintPending.adapter = adapter

        //        binding.spinnerTehsil.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if (++checkTehsil > 1) {
//                    tehsilNameEng = tehsilList.get(i).getTehsilNameEng();
//                    setDataIntoAdapterByTehsil(tehsilNameEng);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//
//        binding.spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                checkTehsil = 0;
//                if (++checkDistrict > 1) {
//                    districtNameEng = districtList.get(i).getDistrictNameEng();
//                    districtId = districtList.get(i).getDistrictId();
//                    fetchDataByFilterType();
//                    updateTehsilByDistrict(districtNameEng);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

//        binding.inputSearch.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                adapter.getFilter().filter(charSequence);
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
        binding!!.actionBar.ivBack.setOnClickListener { view: View? -> onBackPressed() }

        binding!!.btnSubmit.setOnClickListener {
            complainRegNo =
                Objects.requireNonNull(binding!!.inputSearch.text).toString()
                    .trim { it <= ' ' }
            if (complainRegNo.isEmpty()) {
                makeToast(resources.getString(R.string.please_input_complaint_number))
            } else {
                fetchComplaintList()
            }
        }
    }

    //    private void fetchDataByFilterType() {
    //        if (!filterType.equalsIgnoreCase("--" + getResources().getString(R.string.select_filter) + "--")) {
    //            if (filterType.equalsIgnoreCase(getResources().getString(R.string.today))) {
    //                filterType = getResources().getString(R.string.today);
    //                Calendar calendar = Calendar.getInstance();
    //                Date today = calendar.getTime();
    //                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
    //                String todayDate = sdf.format(today);
    //                fetchComplaintListBySelect(todayDate);
    //
    //            } else if (filterType.equalsIgnoreCase(getResources().getString(R.string.yesterday))) {
    //                filterType = getResources().getString(R.string.yesterday);
    //                Calendar calendar = Calendar.getInstance();
    //                calendar.add(Calendar.DAY_OF_YEAR, -1);
    //                Date yesterday = calendar.getTime();
    //                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
    //                String yesterdayDate = sdf.format(yesterday);
    //                fetchComplaintListBySelect(yesterdayDate);
    //
    //            } else if (filterType.equalsIgnoreCase(getResources().getString(R.string.current_month))) {
    //                filterType = getResources().getString(R.string.current_month);
    //                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
    //
    //                Calendar calendar = Calendar.getInstance();
    //                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
    //                calendar.set(Calendar.HOUR_OF_DAY, 0);
    //                calendar.set(Calendar.MINUTE, 0);
    //                calendar.set(Calendar.SECOND, 0);
    //                calendar.set(Calendar.MILLISECOND, 0);
    //                Date firstDateOfCurrentMonth = calendar.getTime();
    //                String date1 = sdf.format(firstDateOfCurrentMonth);
    //
    //                Calendar calendar1 = Calendar.getInstance();
    //                Date currentDateOfCurrentMonth = calendar1.getTime();
    //                String date2 = sdf.format(currentDateOfCurrentMonth);
    //
    //                fetchComplaintListByCurrentMonthAndPreviousMonth(date1, date2);
    //
    //            } else if (filterType.equalsIgnoreCase(getResources().getString(R.string.previous_month))) {
    //                filterType = getResources().getString(R.string.previous_month);
    //                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
    //                Calendar aCalendar = Calendar.getInstance();
    //                aCalendar.add(Calendar.MONTH, -1);
    //                aCalendar.set(Calendar.DATE, 1);
    //                Date firstDateOfPreviousMonth = aCalendar.getTime();
    //                String date1 = sdf.format(firstDateOfPreviousMonth);
    //
    //                aCalendar.set(Calendar.DATE, aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
    //                Date lastDateOfPreviousMonth = aCalendar.getTime();
    //                String date2 = sdf.format(lastDateOfPreviousMonth);
    //
    //                fetchComplaintListByCurrentMonthAndPreviousMonth(date1, date2);
    //
    //
    //            } else if (filterType.equalsIgnoreCase(getResources().getString(R.string.custom_filter))) {
    //                filterType = getResources().getString(R.string.custom_filter);
    //                fetchComplaintListByDateRange(prefManager.getFROM_DATE(), prefManager.getTO_DATE());
    //            }
    //        } else {
    //            filterType = "--" + getResources().getString(R.string.select_filter) + "--";
    //            fetchComplaintList();
    //        }
    //    }
    private fun fetchComplaintList() {
        modelComplaintList!!.clear()
        isLoading
        viewModel!!.getModelEditChallanComplaintMutableLiveData(
            prefManager!!.useR_Id.toString(),
            districtId!!,
            complainRegNo,
            ""
        ).observe(
            this
        ) { modelComplaint: ModelChallanUploadComplaint? ->
            isLoading
            if (modelComplaint!!.status == "200") {
                modelComplaintList = modelComplaint.complaint_List
                setDataIntoAdapter(modelComplaintList)
            }
        }
    }

    private val isLoading: Unit
        get() {
            viewModel!!.isLoading.observe(
                this
            ) { aBoolean: Boolean ->
                if (aBoolean) {
                    showProgress(resources.getString(R.string.please_wait))
                } else {
                    hideProgress()
                }
            }
        }

    @SuppressLint("SetTextI18n")
    private fun setDataIntoAdapter(modelComplaintList: List<ModelComplaintList>?) {
        if (modelComplaintList != null && modelComplaintList.size > 0) {
            binding!!.textTotalComplaint.text = "" + modelComplaintList.size
            binding!!.rvComplaintPending.visibility = View.VISIBLE
            binding!!.textNoComplaint.visibility = View.GONE
            adapter!!.setData(modelComplaintList)
        } else {
            binding!!.textTotalComplaint.text = "0"
            binding!!.rvComplaintPending.visibility = View.GONE
            binding!!.textNoComplaint.visibility = View.VISIBLE
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setDataIntoAdapterByTehsil(tehsilNameEng: String) {
        val complaintList: MutableList<ModelComplaintList> = ArrayList()
        if (modelComplaintList != null && modelComplaintList!!.size > 0) {
            for (i in modelComplaintList!!.indices) {
                if (tehsilNameEng.equals(
                        "--" + resources.getString(R.string.tehsil) + "--",
                        ignoreCase = true
                    )
                ) {
                    if (districtNameEng.equals(
                            "--" + resources.getString(R.string.district) + "--",
                            ignoreCase = true
                        )
                    ) {
                        complaintList.add(modelComplaintList!![i])
                    } else {
                        if (modelComplaintList!![i].district != null) {
                            if (modelComplaintList!![i].district.equals(
                                    districtNameEng,
                                    ignoreCase = true
                                )
                            ) {
                                complaintList.add(modelComplaintList!![i])
                            }
                        }
                    }
                } else {
                    if (modelComplaintList!![i].tehsil != null) {
                        if (modelComplaintList!![i].tehsil.equals(
                                tehsilNameEng,
                                ignoreCase = true
                            )
                        ) {
                            complaintList.add(modelComplaintList!![i])
                        }
                    }
                }
            }
        }

        if (complaintList.size > 0) {
            binding!!.textTotalComplaint.text = "" + complaintList.size
            binding!!.rvComplaintPending.visibility = View.VISIBLE
            binding!!.textNoComplaint.visibility = View.GONE
            adapter!!.setData(complaintList)
        } else {
            binding!!.textTotalComplaint.text = "0"
            binding!!.rvComplaintPending.visibility = View.GONE
            binding!!.textNoComplaint.visibility = View.VISIBLE
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setDataIntoAdapterByDistrict(districtNameEng: String) {
        val complaintList: MutableList<ModelComplaintList> = ArrayList()
        if (modelComplaintList != null && modelComplaintList!!.size > 0) {
            for (i in modelComplaintList!!.indices) {
                if (districtNameEng.equals(
                        "--" + resources.getString(R.string.district) + "--",
                        ignoreCase = true
                    )
                ) {
                    complaintList.add(modelComplaintList!![i])
                } else {
                    if (modelComplaintList!![i].district != null) {
                        if (modelComplaintList!![i].district.equals(
                                districtNameEng,
                                ignoreCase = true
                            )
                        ) {
                            complaintList.add(modelComplaintList!![i])
                        }
                    }
                }
            }
        }

        if (complaintList.size > 0) {
            binding!!.textTotalComplaint.text = "" + complaintList.size
            binding!!.rvComplaintPending.visibility = View.VISIBLE
            binding!!.textNoComplaint.visibility = View.GONE
            adapter!!.setData(complaintList)
        } else {
            binding!!.textTotalComplaint.text = "0"
            binding!!.rvComplaintPending.visibility = View.GONE
            binding!!.textNoComplaint.visibility = View.VISIBLE
        }
    }

    private fun updateTehsilByDistrict(district: String) {
        tehsilList.clear()
        if (tehsil_List_main != null && tehsil_List_main!!.size > 0) {
            for (i in tehsil_List_main!!.indices) {
                if (district.equals(
                        "--" + resources.getString(R.string.district) + "--",
                        ignoreCase = true
                    )
                ) {
                    tehsilList.add(tehsil_List_main!![i])
                } else {
                    if (tehsil_List_main!![i].districtNameEng != null) {
                        if (tehsil_List_main!![i].districtNameEng.equals(
                                district,
                                ignoreCase = true
                            )
                        ) {
                            tehsilList.add(tehsil_List_main!![i])
                        }
                    }
                }
            }
        }
        if (tehsilList.size > 0) {
            Collections.reverse(tehsilList)
            val l = ModelTehsilList()
            l.tehsilId = (-1).toString()
            l.tehsilNameEng = "--" + resources.getString(R.string.tehsil) + "--"
            tehsilList.add(l)
            Collections.reverse(tehsilList)

            val dataAdapter = ArrayAdapter(mContext, R.layout.spinner_item, tehsilList)
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.spinnerTehsil.adapter = dataAdapter
        }
    }

    private fun updateTehsilByDistrictId(districtId: String) {
        tehsilList.clear()
        if (tehsil_List_main != null && tehsil_List_main!!.size > 0) {
            for (i in tehsil_List_main!!.indices) {
                if (districtId.equals("0", ignoreCase = true)) {
                    tehsilList.add(tehsil_List_main!![i])
                } else {
                    if (tehsil_List_main!![i].fk_DistrictId != null) {
                        if (tehsil_List_main!![i].fk_DistrictId.equals(
                                districtId,
                                ignoreCase = true
                            )
                        ) {
                            tehsilList.add(tehsil_List_main!![i])
                        }
                    }
                }
            }
        }

        if (tehsilList.size > 0) {
            Collections.reverse(tehsilList)
            val l = ModelTehsilList()
            l.tehsilId = (-1).toString()
            l.tehsilNameEng = "--" + resources.getString(R.string.tehsil) + "--"
            tehsilList.add(l)
            Collections.reverse(tehsilList)

            val dataAdapter = ArrayAdapter(mContext, R.layout.spinner_item, tehsilList)
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding!!.spinnerTehsil.adapter = dataAdapter
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //        fetchDataByFilterType();
                finish()
            }
        }
    }
}
