package com.callmangement.ehr.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.R
import com.callmangement.ehr.api.APIClient.getRetrofitClientWithoutHeaders
import com.callmangement.ehr.api.APIInterface
import com.callmangement.ehr.ehrActivities.AttendanceViewDetail
import com.callmangement.ehr.ehrActivities.BaseActivity
import com.callmangement.ehr.models.AttStatusDatum
import com.callmangement.ehr.models.AttendanceDetailsInfo
import com.callmangement.ehr.models.UpdateAttendanceStatusRoot
import com.callmangement.ehr.support.Utils.hideCustomProgressDialogCommonForAll
import com.callmangement.ehr.support.Utils.isNetworkAvailable
import com.callmangement.utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects

class AttendanceListingAdapter(
    private val mContext: Context,
    private var attendanceDetailsInfoList: MutableList<AttendanceDetailsInfo>?,
    private var statusDatumList: List<AttStatusDatum>,
    private val mOnItemViewClickListener: OnItemViewClickListener,
    var user_type_id: String
) : RecyclerView.Adapter<AttendanceListingAdapter.MyViewHolder>() {
    var StatusId: String? = null
    var Statustype: String? = null
    var userIdId: String? = null
    var PunchInDate: String? = null
    var districtId: String? = null
    var originalDate: String? = null
    var formattedDate: String? = null
    var Remark: String? = null
    var AttendanceId: String? = null
    var ToUserId: String? = null
    private val playerNames = ArrayList<String>()
    var statuss: Int = 0
    var preference: PrefManager? = null


    private val isLoadingAdded = false // For pagination loading state


    /*  @SuppressLint("NotifyDataSetChanged")
   public void refreshItem(List<AttendanceDetailsInfo> campDetailsInfoList) {
       this.attendanceDetailsInfoList = attendanceDetailsInfoList;
       notifyDataSetChanged();
   }*/
    /*
    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<AttendanceDetailsInfo> list, List<AttStatusDatum> statusDatumList) {
        this.attendanceDetailsInfoList = list;
        this.statusDatumList = statusDatumList;
        notifyDataSetChanged();
    }
*/
    //   24 feb 2024
    @SuppressLint("SuspiciousIndentation")
    fun setData(
        newAttendanceList: MutableList<AttendanceDetailsInfo>,
        statusDatumList: MutableList<AttStatusDatum>
    ) {
        if (attendanceDetailsInfoList == null) {
            attendanceDetailsInfoList = ArrayList()
        } else {
            attendanceDetailsInfoList!!.clear() // Clear old data when setting new data
        }
        this.statusDatumList = statusDatumList

        attendanceDetailsInfoList!!.addAll(newAttendanceList)
        notifyDataSetChanged() // Notify that data has been refreshed
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemview = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_attendance_listing, parent, false)
        return MyViewHolder(itemview)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: MyViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val attendanceDetailsInfo = attendanceDetailsInfoList!![position]


        if (user_type_id == "1") {
            holder.rl_spinner.visibility = View.VISIBLE
            holder.ll_AttnSub.visibility = View.VISIBLE
        } else if (user_type_id == "2") {
            holder.rl_spinner.visibility = View.VISIBLE
            holder.ll_AttnSub.visibility = View.VISIBLE
        } else if (user_type_id == "16") {
            holder.rl_spinner.visibility = View.VISIBLE
            holder.ll_AttnSub.visibility = View.VISIBLE
        } else if (user_type_id == "3") {
            holder.rl_spinner.visibility = View.GONE
            holder.ll_AttnSub.visibility = View.GONE
        } else if (user_type_id == "4") {
            holder.rl_spinner.visibility = View.GONE
            holder.ll_AttnSub.visibility = View.GONE
        } else {
        }


        val statusAtt = attendanceDetailsInfo.attendanceStatus.toString()
        if (statusAtt != null) {
            if (statusAtt.length > 0) {
                holder.tv_status.text = statusAtt
                if (statusAtt == "Present") {
                    holder.tv_status.setTextColor(mContext!!.resources.getColor(R.color.colorGreen))
                } else if (statusAtt == "Absent") {
                    holder.tv_status.setTextColor(mContext!!.resources.getColor(R.color.colorRedDark))
                } else if (statusAtt == "HalfDay") {
                    holder.tv_status.setTextColor(mContext!!.resources.getColor(R.color.imagepicker_colorAccent))
                } else {
                    holder.tv_status.setTextColor(mContext!!.resources.getColor(R.color.colorDarkBlue))
                }
            } else {
                holder.tv_status.text = "-----"
            }
        } else {
            holder.tv_status.text = "------"
        }


        val DisName = attendanceDetailsInfo.districtName
        holder.txtDis.text = DisName


    //    val remarkAtt = attendanceDetailsInfo.remark.trim { it <= ' ' } ?: ""
        val remarkAtt = attendanceDetailsInfo.remark?.trim() ?: ""





        if (remarkAtt == "null") {
            // Handle the case where remarkAtt is null
            Log.d("remarkDebug", "Remark is null")
            holder.tv_remark.text = "ok"
            holder.ll_remark.visibility = View.INVISIBLE
        } else if (remarkAtt.isEmpty()) {
            // Handle the case where remarkAtt is an empty string
            Log.d("remarkDebug", "Remark is an empty string")
            holder.tv_remark.text = "------"
            holder.ll_remark.visibility = View.INVISIBLE
        } else {
            // Handle the case where remarkAtt is a non-empty string
            Log.d("remarkDebug", "Remark length: " + remarkAtt.length)
            holder.tv_remark.text = remarkAtt
            holder.ll_remark.visibility = View.VISIBLE
        }















        if (attendanceDetailsInfo.punchInDate != null) {
            holder.txtDateAndDay.text = attendanceDetailsInfo.punchInDate
        } else {
        }
        val PunchInTime = attendanceDetailsInfo.punchInTime
        val PunchOutTime = attendanceDetailsInfo.punchOutTime
        holder.txtusername.text = attendanceDetailsInfo.username
        Log.d("remarkkhkjkhkhk", "PunchInTime$PunchInTime")
        Log.d("remarkkhkjkhkhk", "PunchOutTime$PunchOutTime")


        //   Log.d("attandanceout", attendanceDetailsInfo.getPunchOutDate());


        /* try {
            Date dt = new Date(PunchInTime);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
           PunchIn = sdf.format(dt);
        } catch (Exception e) {

        }

        try {
            Date dt = new Date(PunchOutTime);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
   PunchOut = sdf.format(dt);
        } catch (Exception e) {

        }*/
        Log.d("remarkkhkjkhkhk", "PunchInTimeyhfgh$PunchInTime")
        Log.d("remarkkhkjkhkhk", "PunchOutTimehfgh$PunchOutTime")
        if (PunchInTime != null && PunchOutTime != null) {
            holder.txtTimeStatus.text = "$PunchInTime - $PunchOutTime"
            Log.d("yes", "first")
        } else if (PunchInTime != null && PunchOutTime == null) {
            holder.txtTimeStatus.text = "$PunchInTime - ____"
            Log.d("yes", "sec")
        } else if (PunchInTime == null && PunchOutTime == null) {
            holder.txtTimeStatus.text = "____" + " - " + "____"
            Log.d("yes", "third")
        } else {
            Log.d("yes", "fourth")
        }


        /*  if (PunchInTime != null && PunchOutTime != null) {
            holder.txtTimeStatus.setText(PunchInTime + " - " + PunchOutTime);
        } else if (PunchInTime != null) {
            holder.txtTimeStatus.setText(PunchInTime + " - " + "__:__");
        } else if (PunchOutTime != null) {
            holder.txtTimeStatus.setText("__:__" + " - " + PunchOutTime);
        }*/
        holder.crd_item.setOnClickListener {
            userIdId = attendanceDetailsInfo.userId.toString()
            PunchInDate = attendanceDetailsInfo.punchInDate
            Log.d("PunchInDate", "--$PunchInDate")
            districtId = attendanceDetailsInfo.districtId.toString()

            originalDate = PunchInDate
            val originalFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy")
            var date: Date? = null
            try {
                date = originalFormat.parse(originalDate)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            val targetFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
            formattedDate = targetFormat.format(date)
            Log.d("PunchInDate", "--$formattedDate")

            // formattedDate is now "2023-08-04"
            val i = Intent(mContext!!, AttendanceViewDetail::class.java)
            i.putExtra("YouruserIdId", userIdId)
            i.putExtra("formattedDate", formattedDate)
            i.putExtra("districtId", districtId)

            // i.putExtra("YouruserIdId", userIdId);
            mContext!!.startActivity(i)
            // Toast.makeText(mContext!!, " "+ attendanceDetailsInfo.getUsername(), Toast.LENGTH_SHORT).show();
        }
        Log.d("Att_Size", " " + statusDatumList.size)

        if (statusDatumList.size > 0) {
            //  playerNames.add(statusDatumList.get(position).getAttendanceStatus());
            //   Collections.singletonList(statusDatumList.get(position).getAttendanceStatus())


            val arrayList: ArrayList<String?> = ArrayList<String?>()

            for (i in statusDatumList.indices) {
                //  playerNames.add(attStatusData.get(i).getAttendanceStatus().toString());
                arrayList.add(statusDatumList[i].attendanceStatus)
                Log.d("Att_Status", " " + statusDatumList[i].attendanceStatus)
            }


            val spinnerArrayAdapter =
                ArrayAdapter(mContext!!, android.R.layout.simple_spinner_item, arrayList)
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // The drop down view
            holder.spinnerAttType.adapter = spinnerArrayAdapter
            //  holder.spinnerAttType.setSelection(attendanceDetailsInfo.getAttendanceStatusId() - 1);
            holder.spinnerAttType.setSelection(attendanceDetailsInfo.attendanceStatusId - 1)
            Log.d("StatusIdStatusId", " " + attendanceDetailsInfo.attendanceStatusId)


            holder.spinnerAttType.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        adapterView: AdapterView<*>?,
                        view: View,
                        i: Int,
                        l: Long
                    ) {
                        StatusId = statusDatumList[i].statusID.toString()
                        Statustype = statusDatumList[i].attendanceStatus.toString()
                        Log.d("StatusId", " $StatusId")
                    }


                    override fun onNothingSelected(adapterView: AdapterView<*>?) {
                    }
                }
        }


        /*

//        if (surveyFormDetailsInfo.getStatusId() != null && surveyFormDetailsInfo.getStatusId().equalsIgnoreCase("1"))
//            holder.txtStatus.setText("PENDING");
//        if (surveyFormDetailsInfo.getStatusId() != null && surveyFormDetailsInfo.getStatusId().equalsIgnoreCase("2"))
//            holder.txtStatus.setText("UPLOADED");
//        if (surveyFormDetailsInfo.getStatusId() != null && surveyFormDetailsInfo.getStatusId().equalsIgnoreCase("3"))
//            holder.txtStatus.setText("DELETED");
//
//        if (surveyFormDetailsInfo.getStatusId() != null && surveyFormDetailsInfo.getStatusId().equalsIgnoreCase(Constants.SURVEY_STATUS_ID_COMPLETED)) {
//            holder.txtStatus.setTextColor(mContext!!.getResources().getColor(R.color.colorGreen));
//        }
//
//        if (surveyFormDetailsInfo.getCustomerName() != null)
//            holder.customer_name.setText(surveyFormDetailsInfo.getCustomerName());
//
//        if (surveyFormDetailsInfo.getBill_ChallanNo() != null)
//            holder.bill_challan_no.setText(surveyFormDetailsInfo.getBill_ChallanNo());
//
//        if (surveyFormDetailsInfo.getAddress() != null)
//            holder.customer_address.setText(surveyFormDetailsInfo.getAddress());
//
//        if (surveyFormDetailsInfo.getTypeOfCall() != null)
//            holder.type_of_call.setText(surveyFormDetailsInfo.getTypeOfCall());
//
//        if (surveyFormDetailsInfo.getInstallationDateStr() != null) {
//            SimpleDateFormat input = new SimpleDateFormat("dd-MM-yyyy");
//            SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd");
//            try {
//                Date oneWayTripDate = input.parse(surveyFormDetailsInfo.getInstallationDateStr());                 // parse input
//                holder.date_of_installation.setText(output.format(oneWayTripDate));    // format output
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        }
//
//        if (surveyFormDetailsInfo.getMobileNumber() != null)
//            holder.mobile_number.setText(surveyFormDetailsInfo.getMobileNumber());
//
//        if (surveyFormDetailsInfo.getCustomer_Remark() != null)
//            holder.customer_remarks.setText(surveyFormDetailsInfo.getCustomer_Remark());
//
//        holder.buttonUpload.setVisibility(View.GONE);
//        holder.buttonEdit.setVisibility(View.GONE);
//        holder.buttonDownload.setVisibility(View.GONE);
//        holder.buttonViewUploaded.setVisibility(View.GONE);
//        holder.buttonDeleteCamp.setVisibility(View.GONE);
//
//        if (surveyFormDetailsInfo.getStatusId() != null && surveyFormDetailsInfo.getStatusId().equalsIgnoreCase(Constants.SURVEY_STATUS_ID_INITIATED)) {
//            holder.buttonDeleteCamp.setVisibility(View.VISIBLE);
//        }
//
//        holder.buttonDownload.setVisibility(View.VISIBLE);
//        holder.buttonEdit.setVisibility(View.VISIBLE);
//        holder.buttonViewUploaded.setVisibility(View.VISIBLE);
//        holder.buttonUpload.setVisibility(View.VISIBLE);
//
//        holder.buttonUpload.setOnClickListener(new OnSingleClickListener() {
//            @Override
//            public void onSingleClick(View v) {
//                if (mOnItemUploadClickListener != null)
//                    mOnItemUploadClickListener.onItemClick(surveyFormDetailsInfo, position);
//            }
//        });
//
//        holder.buttonDownload.setOnClickListener(new OnSingleClickListener() {
//            @Override
//            public void onSingleClick(View v) {
//                if (mOnItemDownloadClickListener != null)
//                    mOnItemDownloadClickListener.onItemClick(surveyFormDetailsInfo, position);
//            }
//        });
//
//        holder.buttonViewUploaded.setOnClickListener(new OnSingleClickListener() {
//            @Override
//            public void onSingleClick(View v) {
//                if (mOnItemViewClickListener != null)
//                    mOnItemViewClickListener.onItemClick(surveyFormDetailsInfo, position);
//            }
//        });
//
//        holder.buttonDeleteCamp.setOnClickListener(new OnSingleClickListener() {
//            @Override
//            public void onSingleClick(View v) {
//                if (mOnItemDeleteClickListener != null)
//                    mOnItemDeleteClickListener.onItemClick(surveyFormDetailsInfo, position);
//            }
//        });
//
//        holder.buttonEdit.setOnClickListener(new OnSingleClickListener() {
//            @Override
//            public void onSingleClick(View v) {
//                if (mOnItemEditClickListener != null)
//                    mOnItemEditClickListener.onItemClick(surveyFormDetailsInfo, position);
//            }
//        });
*/
        var notnull = 0
        for (Info in attendanceDetailsInfoList!!) {
            if (Info.punchOutTime != null) {
                notnull++
            }
        }
        Log.d("nullCount", "" + notnull)
        val qty = notnull.toString()
        val intent = Intent("custom-message")
        //            intent.putExtra("quantity",Integer.parseInt(quantity.getText().toString()));
        intent.putExtra("quantity", qty)
        LocalBroadcastManager.getInstance(mContext!!).sendBroadcast(intent)


        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.shadow_dark
                )
            )
        } else {
            holder.itemView.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.shadow_light
                )
            )
        }


        //gagandeep
        holder.ll_AttnSub.setOnClickListener {
            val builder = AlertDialog.Builder(mContext!!)
            builder.setTitle("")

            // Set an EditText view to get user input

            // set the custom layout
            val customLayout = LayoutInflater.from(mContext!!).inflate(R.layout.alartdialoge, null)
            builder.setView(customLayout)
            val editText =
                customLayout.findViewById<EditText>(R.id.inputRemark) // replace 'editTextId' with the actual ID of your EditText

            // add a button
            builder.setPositiveButton(
                "Ok"
            ) { dialog: DialogInterface?, which: Int ->
                if (isNetworkAvailable(mContext!!)) {
                    Log.d("useid", "$user_type_id $userIdId")
                    Log.d("StatusId", " $StatusId")

                    //  Utils.hideKeyboard(AttendanceListingAdapter.class);
                    // Utils.showCustomProgressDialogCommonForAll((Activity) mContext!!, mContext!!.getResources().getString(R.string.please_wait));
                    holder.progressBar.visibility = View.VISIBLE
                    val apiInterface = getRetrofitClientWithoutHeaders(
                        mContext!!,
                        BaseActivity.BaseUrl()!!
                    ).create(
                        APIInterface::class.java
                    )
                    Remark = editText.text.toString()
                    preference = PrefManager(mContext!!)

                    val USER_Id = preference!!.useR_Id


                    AttendanceId = attendanceDetailsInfo.id.toString()
                    ToUserId = attendanceDetailsInfo.userId.toString()
                    Log.d("AttendanceId", AttendanceId!!)
                    Log.d("USER_Id", USER_Id)
                    Log.d("ToUserId", ToUserId!!)
                    Log.d("StatusId", StatusId!!)
                    val call = apiInterface.UpdateAttStatus(
                        AttendanceId,
                        USER_Id,
                        ToUserId,
                        StatusId,
                        Remark
                    )
                    call!!.enqueue(object : Callback<UpdateAttendanceStatusRoot?> {
                        override fun onResponse(
                            call: Call<UpdateAttendanceStatusRoot?>,
                            response: Response<UpdateAttendanceStatusRoot?>
                        ) {
                            hideCustomProgressDialogCommonForAll()
                            holder.progressBar.visibility = View.GONE
                            if (response.isSuccessful) {
                                if (response.code() == 200) {
                                    if (response.body() != null) {
                                        if (response.body()!!.status == "200") {
                                            hideCustomProgressDialogCommonForAll()
                                            val getErrorTypesRoot = response.body()

                                            val status = getErrorTypesRoot!!.status
                                            if (status == "200") {
                                                // Intent i =new Intent(mContext!!, AttendanceListingActivity.class);
                                                //  mContext!!.startActivity(i);

                                                // notifyItemChanged(holder.getAdapterPosition());

                                                attendanceDetailsInfoList!![position].attendanceStatus =
                                                    getErrorTypesRoot.data.attendanceStatus
                                                attendanceDetailsInfoList!![position].remark =
                                                    getErrorTypesRoot.data.remark
                                                attendanceDetailsInfoList!![position].attendanceStatusId =
                                                    getErrorTypesRoot.data.statusID


                                                //  attendanceDetailsInfoList.get(position).setAttendanceStatus(Statustype);
                                                //  attendanceDetailsInfoList.get(position).setRemark(Remark);
                                                notifyDataSetChanged()

                                                //notifyItemChanged();
                                                /* boolean checkstatus= Boolean.parseBoolean(getErrorTypesRoot.getStatus());
                                                                if (checkstatus==true)
    
                                                                {
                                                                    //  makeToast(response.body().getMessage());
    
                                                                    Log.d("checkstatus","checkstatus"+checkstatus);
    
                                                                    Intent i =new Intent(AttendanceListingAdapter.this, ErrorPosActivity.class);
                                                                     startActivity(i);
                                                                    makeToast(response.body().getMessage());
                                                                }
                                                                else
                                                                {
                                                                    //   makeToast(response.body().getMessage());
                                                                }*/
                                                makeToast(response.body()!!.message)
                                            } else {
                                                //    makeToast(response.body().getMessage());
                                            }

                                            Log.d(
                                                "getErrorTypesRoot..",
                                                "getErrorTypesRoot..$getErrorTypesRoot"
                                            )


                                            //   makeToast(response.body().getResponse().getMessage());
                                            Log.d(
                                                this@AttendanceListingAdapter.toString(),
                                                "fggafdaf$getErrorTypesRoot"
                                            )
                                        } else {
                                            makeToast(response.body()!!.message.toString())
                                        }
                                    } else {
                                        makeToast(mContext!!.resources.getString(R.string.error))
                                    }
                                } else {
                                    makeToast(mContext!!.resources.getString(R.string.error))
                                }
                            } else {
                                makeToast(mContext!!.resources.getString(R.string.error))
                            }
                        }

                        override fun onFailure(
                            call: Call<UpdateAttendanceStatusRoot?>,
                            error: Throwable
                        ) {
                            hideCustomProgressDialogCommonForAll()
                            holder.progressBar.visibility = View.GONE

                            makeToast(mContext!!.resources.getString(R.string.error))

                            call.cancel()
                        }
                    })
                } else {
                    makeToast(mContext!!.resources.getString(R.string.no_internet_connection))
                }
            }

                .setNegativeButton(
                    R.string.cancel
                ) { dialog, which ->
                    // Handle Cancel button click
                }
            // create and show the alert dialog
            val dialog = builder.create()
            dialog.show()
        }
    }


    // Add code nby me Now
    /*  public void addItems(List<AttendanceDetailsInfo> newItems) {
        int startPosition = attendanceDetailsInfoList.size();
        attendanceDetailsInfoList.addAll(newItems);
        notifyItemRangeInserted(startPosition, newItems.size());
    }*/
    //
    fun makeToast(string: String?) {
        if (TextUtils.isEmpty(string)) return
        Toast.makeText(mContext!!, string, Toast.LENGTH_SHORT).show()
    }

    override fun getItemCount(): Int {
        return if (attendanceDetailsInfoList == null) 0
        else attendanceDetailsInfoList!!.size
    }


    //now
    /* public void addData(List<AttendanceDetailsInfo> newAttendanceList, List<AttStatusDatum> attStatusData) {
        if (attendanceDetailsInfoList == null) {
            attendanceDetailsInfoList = new ArrayList<>();
        } else {
            attendanceDetailsInfoList.clear(); // Clear old data
        }
        attendanceDetailsInfoList.addAll(newAttendanceList);
      //  notifyDataSetChanged(); // Notify RecyclerView of data change
    }*/
    /*
 public void addData(List<AttendanceDetailsInfo> moreData, List<AttStatusDatum> attStatusData) {
        int startPosition = attendanceDetailsInfoList.size();
        attendanceDetailsInfoList.addAll(moreData);
        notifyItemRangeInserted(startPosition, moreData.size());
    }
*/
    //24 feb 2025
    fun addData(
        newAttendanceList: List<AttendanceDetailsInfo>,
        attStatusData: List<AttStatusDatum>
    ) {
        if (attendanceDetailsInfoList == null) {
            attendanceDetailsInfoList = ArrayList()
            this.statusDatumList = attStatusData
        }

        val startPosition = attendanceDetailsInfoList!!.size
        attendanceDetailsInfoList!!.addAll(newAttendanceList)

        //  this.statusDatumList = attStatusData;
        notifyItemRangeInserted(startPosition, newAttendanceList.size) // Optimized refresh
    }


    fun interface OnItemViewClickListener {
        fun onItemClick(surveyFormDetailsInfo: AttendanceDetailsInfo?, position: Int)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtDateAndDay: TextView = itemView.findViewById(R.id.txtDateAndDay)
        var txtDate: TextView = itemView.findViewById(R.id.txtDate)
        var txtTimeStatus: TextView = itemView.findViewById(R.id.txtTimeStatus)
        var txtTime: TextView = itemView.findViewById(R.id.txtTime)
        var buttonViewUploaded: LinearLayout =
            itemView.findViewById(R.id.buttonViewUploaded)
        var ll_AttnSub: LinearLayout = itemView.findViewById(R.id.ll_AttnSub)
        var ll_status: LinearLayout? = null
        var rl_spinner: RelativeLayout = itemView.findViewById(R.id.rl_spinner)
        var crd_item: CardView = itemView.findViewById(R.id.crd_item)
        var txtusername: AppCompatTextView =
            itemView.findViewById(R.id.txtusername)
        var tv_status: AppCompatTextView = itemView.findViewById(R.id.tv_status)
        var tv_remark: AppCompatTextView = itemView.findViewById(R.id.tv_remark)
        var txtDis: AppCompatTextView = itemView.findViewById(R.id.txtDis)
        var progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        var spinnerAttType: AppCompatSpinner =
            itemView.findViewById(R.id.spinnerAttType)
        var ll_remark: LinearLayout = itemView.findViewById(R.id.ll_remark)
    }

    companion object {
        private const val ITEM_TYPE_DATA = 0
        private const val ITEM_TYPE_LOADING = 1
    }
}