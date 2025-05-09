package com.callmangement.ui.reports

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.callmangement.network.APIService
import com.callmangement.network.RetrofitInstance
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityGraphBinding
import com.callmangement.model.complaints.ModelComplaint
import com.callmangement.model.complaints.ModelComplaintList
import com.callmangement.model.district.ModelDistrict
import com.callmangement.model.district.ModelDistrictList
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.callmangement.ui.complaint.ComplaintViewModel
import com.callmangement.utils.DateTimeUtils
import com.callmangement.utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Locale

class GraphActivity : CustomActivity() {

    
    private var binding: ActivityGraphBinding? = null
    private var prefManager: PrefManager? = null
    private var viewModel: ComplaintViewModel? = null
    private var complaintList: List<ModelComplaintList>? = null
    private val myFormat = "yyyy-MM-dd"
    private val complainStatusId = "0"
    private var districtId = "0"
    private var checkDistrict = 0
    private var districtNameEng = ""
    private var district_List: MutableList<ModelDistrictList?>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_graph)

        mContext = this

        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.graph)
        viewModel = ViewModelProviders.of(this).get(
            ComplaintViewModel::class.java
        )
        prefManager = PrefManager(mContext!!)
        if (prefManager!!.useR_TYPE_ID == "4" && prefManager!!.useR_TYPE.equals(
                "ServiceEngineer",
                ignoreCase = true
            )
        ) { // for service engineer
            districtId = prefManager!!.useR_DistrictId!!
            binding!!.rlDistrict.visibility = View.GONE
        } else if (prefManager!!.useR_TYPE_ID.equals(
                "6",
                ignoreCase = true
            ) && prefManager!!.useR_TYPE.equals("DSO", ignoreCase = true)
        ) { // for dso
            districtId = prefManager!!.useR_DistrictId!!
            binding!!.rlDistrict.visibility = View.GONE
        } else {
            districtList()
            binding!!.rlDistrict.visibility = View.VISIBLE
        }
        initView()
        fetchComplaintList()
    }

    private fun initView() {
        binding!!.spinnerDistrict.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    adapterView: AdapterView<*>?,
                    view: View,
                    i: Int,
                    l: Long
                ) {
                    if (++checkDistrict > 1) {
                        districtNameEng = district_List!![i]!!.districtNameEng!!
                        districtId = district_List!![i]!!.districtId!!
                        if (districtNameEng.equals(
                                "--" + resources.getString(R.string.district) + "--",
                                ignoreCase = true
                            )
                        ) {
                            fetchComplaintListByDistrictWise("0")
                        } else {
                            fetchComplaintListByDistrictWise(districtId)
                        }
                    }
                }

                override fun onNothingSelected(adapterView: AdapterView<*>?) {
                }
            }

        binding!!.actionBar.ivBack.setOnClickListener { view: View? -> onBackPressed() }
    }

    private fun fetchComplaintList() {
        val sdf = SimpleDateFormat(myFormat, Locale.US)

        val calendarToday = Calendar.getInstance()
        val today = calendarToday.time
        val todayDate = sdf.format(today)

        val calendarLastThreeMonth = Calendar.getInstance()
        calendarLastThreeMonth.add(Calendar.MONTH, -2)
        calendarLastThreeMonth[Calendar.DATE] = 1
        val lastThreeMonthDate = calendarLastThreeMonth.time
        val lastThreeMonthDateStr = sdf.format(lastThreeMonthDate)

        val fromDate = lastThreeMonthDateStr
        val toDate = todayDate

        showProgress()
        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        val call = service.getComplaintListDistStatusDateWise(
            prefManager!!.useR_Id.toString(),
            districtId,
            complainStatusId,
            fromDate,
            toDate,
            "0",
            "0",
            "0",
            ""
        )
        call.enqueue(object : Callback<ModelComplaint?> {
            override fun onResponse(
                call: Call<ModelComplaint?>,
                response: Response<ModelComplaint?>
            ) {
                hideProgress()
                if (response.isSuccessful) {
                    val modelComplaint = response.body()
                    if (modelComplaint!!.status == "200") {
                        complaintList = modelComplaint!!.complaint_List
                        val sortedList: List<ModelComplaintList> = ArrayList(complaintList)
                        for (model in sortedList) {
                            model.registrationComplainDateTimeStamp =
                                DateTimeUtils.getTimeStamp(model.complainRegDateStr)
                        }
                        Collections.sort(
                            sortedList,
                            Comparator.comparingLong { obj: ModelComplaintList -> obj.registrationComplainDateTimeStamp })
                        totalComplaintBarGraph(sortedList)
                        val listMonthName: MutableList<String> = ArrayList()
                        for (modelMonth in sortedList) {
//                            ModelComplaintRegistrationDate monthDetailModel = modelMonth.getComplainRegDate();
//                            String monthName = monthDetailModel.getMonth();

                            val monthDetailModel = modelMonth.complainRegDateStr
                            val mnt_list =
                                monthDetailModel.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                                    .toTypedArray()

                            val date = mnt_list[0]
                            val month = mnt_list[1].toInt()
                            val year = mnt_list[2]

                            val monthString = DateFormatSymbols().months[month - 1]

                            val monthName = monthString
                            if (listMonthName.size == 0) listMonthName.add(monthName)
                            else {
                                var existFlag = false
                                for (i in listMonthName.indices) {
                                    if (monthName.equals(listMonthName[i], ignoreCase = true)) {
                                        existFlag = true
                                        break
                                    }
                                }
                                if (!existFlag) listMonthName.add(monthName)
                            }
                        }

                        try {
                            binding!!.textFirstMonth.text = listMonthName[0]
                            binding!!.textSecondMonth.text = listMonthName[1]
                            binding!!.textCurrentMonth.text = listMonthName[2]
                        } catch (e: Exception) {
                            e.printStackTrace()
                            binding!!.barGraphTotalComplaints.visibility = View.GONE
                        }
                    } else {
                        makeToast(modelComplaint!!.message)
                    }
                } else {
                    makeToast(resources.getString(R.string.error))
                }
            }

            override fun onFailure(call: Call<ModelComplaint?>, t: Throwable) {
                hideProgress()
                makeToast(resources.getString(R.string.error_message))
            }
        })
    }

    private fun fetchComplaintListByDistrictWise(districtId: String) {
        val sdf = SimpleDateFormat(myFormat, Locale.US)

        val calendarToday = Calendar.getInstance()
        val today = calendarToday.time
        val todayDate = sdf.format(today)

        val calendarLastThreeMonth = Calendar.getInstance()
        calendarLastThreeMonth.add(Calendar.MONTH, -2)
        calendarLastThreeMonth[Calendar.DATE] = 1
        val lastThreeMonthDate = calendarLastThreeMonth.time
        val lastThreeMonthDateStr = sdf.format(lastThreeMonthDate)

        val fromDate = lastThreeMonthDateStr
        val toDate = todayDate

        showProgress()
        val service = RetrofitInstance.getRetrofitInstance().create(
            APIService::class.java
        )
        val call = service.getComplaintListDistStatusDateWise(
            prefManager!!.useR_Id.toString(),
            districtId,
            complainStatusId,
            fromDate,
            toDate,
            "0",
            "0",
            "0",
            ""
        )
        call.enqueue(object : Callback<ModelComplaint?> {
            @RequiresApi(api = Build.VERSION_CODES.N)
            override fun onResponse(
                call: Call<ModelComplaint?>,
                response: Response<ModelComplaint?>
            ) {
                hideProgress()
                if (response.isSuccessful) {
                    val modelComplaint = response.body()
                    if (modelComplaint!!.status == "200") {
                        complaintList = modelComplaint!!.complaint_List
                        val sortedList: List<ModelComplaintList> = ArrayList(complaintList)
                        for (model in sortedList) {
                            model.registrationComplainDateTimeStamp =
                                DateTimeUtils.getTimeStamp(model.complainRegDateStr)
                        }

                        Collections.sort(
                            sortedList,
                            Comparator.comparingLong { obj: ModelComplaintList -> obj.registrationComplainDateTimeStamp })
                        totalComplaintBarGraph(sortedList)

                        val listMonthName: MutableList<String> = ArrayList()
                        for (modelMonth in sortedList) {
//                            ModelComplaintRegistrationDate monthDetailModel = modelMonth.getComplainRegDate();
//                            String monthName = monthDetailModel.getMonth();
                            //         String monthName = "Monthhhhhh";


                            val monthDetailModel = modelMonth.complainRegDateStr
                            Log.d("--monthDetailModel--", " $monthDetailModel")
                            val mnt_list =
                                monthDetailModel.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                                    .toTypedArray()

                            val date = mnt_list[0]
                            val month = mnt_list[1].toInt()
                            val year = mnt_list[2]

                            val monthString = DateFormatSymbols().months[month - 1]

                            val monthName = monthString









                            if (listMonthName.size == 0) listMonthName.add(monthName)
                            else {
                                var existFlag = false
                                for (i in listMonthName.indices) {
                                    if (monthName.equals(listMonthName[i], ignoreCase = true)) {
                                        existFlag = true
                                        break
                                    }
                                }
                                if (!existFlag) listMonthName.add(monthName)
                            }
                        }

                        try {
                            binding!!.textFirstMonth.text = listMonthName[0]
                            binding!!.textSecondMonth.text = listMonthName[1]
                            binding!!.textCurrentMonth.text = listMonthName[2]
                        } catch (e: Exception) {
                            e.printStackTrace()
                            binding!!.barGraphTotalComplaints.visibility = View.GONE
                        }
                    } else {
                        makeToast(modelComplaint!!.message)
                    }
                } else {
                    makeToast(resources.getString(R.string.error))
                }
            }

            override fun onFailure(call: Call<ModelComplaint?>, t: Throwable) {
                hideProgress()
                makeToast(resources.getString(R.string.error_message))
            }
        })
    }

    private fun districtList() {
        viewModel!!.district.observe(
            this
        ) { modelDistrict: ModelDistrict? ->
            if (modelDistrict!!.status == "200") {
                district_List = modelDistrict.district_List

                if (district_List != null && district_List!!.size > 0) {
                    district_List!!.reverse()
                    val l = ModelDistrictList()
                    l.districtId = (-1).toString()
                    l.districtNameEng = "--" + resources.getString(R.string.district) + "--"
                    district_List!!.add(l)
                    district_List!!.reverse()

                    val dataAdapter =
                        ArrayAdapter(
                            mContext!!, android.R.layout.simple_spinner_item,
                            district_List!!
                        )
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding!!.spinnerDistrict.adapter = dataAdapter
                }
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

    override fun onBackPressed() {
        super.onBackPressed()
    }

    //start total complaints bar graph
    private fun totalComplaintBarGraph(modelComplaintList: List<ModelComplaintList>) {
        binding!!.barGraphTotalComplaints.description.isEnabled = false

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        binding!!.barGraphTotalComplaints.setMaxVisibleValueCount(60)
        binding!!.barGraphTotalComplaints.setDrawValueAboveBar(true)

        // scaling can now only be done on x- and y-axis separately
        binding!!.barGraphTotalComplaints.setPinchZoom(false)

        binding!!.barGraphTotalComplaints.setDrawBarShadow(false)
        binding!!.barGraphTotalComplaints.setDrawGridBackground(false)

        // add a nice and smooth animation
        binding!!.barGraphTotalComplaints.animateY(1500)

        //        binding.barGraphTotalComplaints.getLegend().setEnabled(false);
        val l = binding!!.barGraphTotalComplaints.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)
        l.xEntrySpace = 10f
        l.yEntrySpace = 0f
        l.yOffset = 7f
        l.textSize = 12f

        binding!!.barGraphTotalComplaints.setExtraOffsets(0f, 0f, 0f, 0f)
        setDataTotalComplaintsBarGraph(modelComplaintList)
    }

    private fun setDataTotalComplaintsBarGraph(modelComplaintList: List<ModelComplaintList>) {
        val barWidth = 0.3f
        val barSpace = 0.0f
        val groupSpace = 0.06f
        val groupCount = 3

        val valuesTotalComplaints = ArrayList<BarEntry>()
        val valuesResolvedComplaints = ArrayList<BarEntry>()

        val modelResolveComplaintList: MutableList<ModelComplaintList> = ArrayList()

        for (i in modelComplaintList.indices) {
            if (modelComplaintList[i].complainStatusId == "3") {
                modelResolveComplaintList.add(modelComplaintList[i])
            }
        }

        val listMonthName: MutableList<String> = ArrayList()
        for (modelMonth in modelComplaintList) {
//            ModelComplaintRegistrationDate monthDetailModel = modelMonth.getComplainRegDate();
//            String monthName = monthDetailModel.getMonth();

            //     String monthName = "Monthhhhhh";


            val monthDetailModel = modelMonth.complainRegDateStr
            Log.d("--monthDetailModel--", " $monthDetailModel")
            val mnt_list =
                monthDetailModel.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            val date = mnt_list[0]
            val month = mnt_list[1].toInt()
            val year = mnt_list[2]

            val monthString = DateFormatSymbols().months[month - 1]

            val monthName = monthString

            if (listMonthName.size == 0) listMonthName.add(monthName)
            else {
                var existFlag = false
                for (i in listMonthName.indices) {
                    if (monthName.equals(listMonthName[i], ignoreCase = true)) {
                        existFlag = true
                        break
                    }
                }
                if (!existFlag) listMonthName.add(monthName)
            }
        }


        val listFirstMonthTotal: MutableList<ModelComplaintList> = ArrayList()
        val listSecondMonthTotal: MutableList<ModelComplaintList> = ArrayList()
        val listCurrentMonthTotal: MutableList<ModelComplaintList> = ArrayList()

        for (model in modelComplaintList) {
            //     String monthName1 = model.getComplainRegDate().getMonth();
            val monthName = model.complainRegDateStr



            Log.d("--monthDetailModel--", " $monthName")
            val mnt_list =
                monthName.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            val date = mnt_list[0]
            val month = mnt_list[1].toInt()
            val year = mnt_list[2]

            val monthString = DateFormatSymbols().months[month - 1]


            // String monthName1 = "Monthhhhhh";
            val monthName1 = monthString



            if (listMonthName.size > 0) {
                for (j in listMonthName.indices) {
                    val monthName2 = listMonthName[j]
                    if (monthName1.equals(monthName2, ignoreCase = true)) {
                        when (j) {
                            0 -> listFirstMonthTotal.add(model)
                            1 -> listSecondMonthTotal.add(model)
                            2 -> listCurrentMonthTotal.add(model)
                        }
                    }
                }
            }
        }


        val listFirstMonthResolve: MutableList<ModelComplaintList> = ArrayList()
        val listSecondMonthResolve: MutableList<ModelComplaintList> = ArrayList()
        val listCurrentMonthResolve: MutableList<ModelComplaintList> = ArrayList()

        for (model in modelResolveComplaintList) {
            //     String monthName1 = model.getComplainRegDate().getMonth();
            //  String monthName1 = "Monthhhhhh";


            val monthName = model.complainRegDateStr



            Log.d("--monthDetailModel--", " $monthName")
            val mnt_list =
                monthName.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            val date = mnt_list[0]
            val month = mnt_list[1].toInt()
            val year = mnt_list[2]

            val monthString = DateFormatSymbols().months[month - 1]
            // String monthName1 = "Monthhhhhh";
            val monthName1 = monthString

            if (listMonthName.size > 0) {
                for (j in listMonthName.indices) {
                    val monthName2 = listMonthName[j]
                    if (monthName1.equals(monthName2, ignoreCase = true)) {
                        when (j) {
                            0 -> listFirstMonthResolve.add(model)
                            1 -> listSecondMonthResolve.add(model)
                            2 -> listCurrentMonthResolve.add(model)
                        }
                    }
                }
            }
        }

        valuesTotalComplaints.add(BarEntry(0f, listFirstMonthTotal.size.toFloat()))
        valuesResolvedComplaints.add(BarEntry(0f, listFirstMonthResolve.size.toFloat()))

        valuesTotalComplaints.add(BarEntry(1f, listSecondMonthTotal.size.toFloat()))
        valuesResolvedComplaints.add(BarEntry(1f, listSecondMonthResolve.size.toFloat()))

        valuesTotalComplaints.add(BarEntry(2f, listCurrentMonthTotal.size.toFloat()))
        valuesResolvedComplaints.add(BarEntry(2f, listCurrentMonthResolve.size.toFloat()))

        val xAxis = binding!!.barGraphTotalComplaints.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setCenterAxisLabels(false)
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = false
        //        xAxis.setAxisMinimum(0f);
        xAxis.valueFormatter = IndexAxisValueFormatter()

        val set1: BarDataSet
        val set2: BarDataSet
        if (binding!!.barGraphTotalComplaints.data != null &&
            binding!!.barGraphTotalComplaints.data.dataSetCount > 0
        ) {
            set1 = binding!!.barGraphTotalComplaints.data.getDataSetByIndex(0) as BarDataSet
            set2 = binding!!.barGraphTotalComplaints.data.getDataSetByIndex(1) as BarDataSet
            set1.values = valuesTotalComplaints
            set2.values = valuesResolvedComplaints
            binding!!.barGraphTotalComplaints.data.notifyDataChanged()
            binding!!.barGraphTotalComplaints.notifyDataSetChanged()
        } else {
            set1 = BarDataSet(valuesTotalComplaints, resources.getString(R.string.total))
            set1.setColors(*MATERIAL_COLORS)
            set1.setDrawValues(true)

            set2 = BarDataSet(
                valuesResolvedComplaints,
                resources.getString(R.string.complaint_resolve)
            )
            set2.setColors(*MATERIAL_COLORS2)
            set2.setDrawValues(true)

            val dataSets = ArrayList<IBarDataSet>()
            dataSets.add(set1)
            dataSets.add(set2)

            val data = BarData(dataSets)
            data.setValueTextSize(13f)
            data.dataSetCount
            binding!!.barGraphTotalComplaints.data = data
            binding!!.barGraphTotalComplaints.setFitBars(true)
        }

        binding!!.barGraphTotalComplaints.xAxis.axisMinimum = 0f

        //        binding.barGraphTotalComplaints.getXAxis().setAxisMaximum(0
//                + binding.barGraphTotalComplaints.getBarData().getGroupWidth(groupSpace, barSpace) * groupCount);
        binding!!.barGraphTotalComplaints.barData.barWidth = 0.40f
        binding!!.barGraphTotalComplaints.groupBars(groupSpace, groupSpace, barSpace)
        binding!!.barGraphTotalComplaints.invalidate()
    }

    companion object {
        val MATERIAL_COLORS: IntArray = intArrayOf(rgb("#5d62b5"))
        val MATERIAL_COLORS2: IntArray = intArrayOf(rgb("#29c3be"))

        //end total complaints bar graph
        fun rgb(hex: String): Int {
            val color = hex.replace("#", "").toLong(16) as Int
            val r = (color shr 16) and 0xFF
            val g = (color shr 8) and 0xFF
            val b = (color shr 0) and 0xFF
            return Color.rgb(r, g, b)
        }
    }
}