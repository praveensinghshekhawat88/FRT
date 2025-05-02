package com.callmangement.EHR.adapter;

import static com.callmangement.EHR.ehrActivities.BaseActivity.BaseUrl;
import static java.lang.String.valueOf;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.callmangement.EHR.api.APIClient;
import com.callmangement.EHR.api.APIInterface;
import com.callmangement.EHR.ehrActivities.AttendanceViewDetail;
import com.callmangement.EHR.models.AttStatusDatum;
import com.callmangement.EHR.models.AttendanceDetailsInfo;
import com.callmangement.EHR.models.UpdateAttendanceStatusRoot;
import com.callmangement.EHR.support.Utils;
import com.callmangement.R;
import com.callmangement.utils.PrefManager;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendanceListingAdapter extends RecyclerView.Adapter<AttendanceListingAdapter.MyViewHolder> {

    private static final int ITEM_TYPE_DATA = 0;
    private static final int ITEM_TYPE_LOADING = 1;


    private final Context mContext;
    String user_type_id, StatusId, Statustype;
    String userIdId, PunchInDate, districtId, originalDate, formattedDate;
    String Remark, AttendanceId, ToUserId;
    private List<AttendanceDetailsInfo> attendanceDetailsInfoList;
    private List<AttStatusDatum> statusDatumList;
    private final OnItemViewClickListener mOnItemViewClickListener;
    private final ArrayList<String> playerNames = new ArrayList<String>();
    int statuss = 0;
    PrefManager preference;


    private boolean isLoadingAdded = false; // For pagination loading state

    public AttendanceListingAdapter(Context context, List<AttendanceDetailsInfo> attendanceDetailsInfoList, List<AttStatusDatum> statusDatumList,
                                    OnItemViewClickListener mOnItemViewClickListener, String user_type_id) {
        this.mContext = context;
        this.attendanceDetailsInfoList = attendanceDetailsInfoList;
        this.statusDatumList = statusDatumList;
        this.mOnItemViewClickListener = mOnItemViewClickListener;
        this.user_type_id = user_type_id;


    }



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
  public void setData(List<AttendanceDetailsInfo> newAttendanceList, List<AttStatusDatum> statusDatumList) {
        if (attendanceDetailsInfoList == null) {
            attendanceDetailsInfoList = new ArrayList<>();

        } else {
            attendanceDetailsInfoList.clear(); // Clear old data when setting new data
        }
      this.statusDatumList = statusDatumList;

        attendanceDetailsInfoList.addAll(newAttendanceList);
        notifyDataSetChanged(); // Notify that data has been refreshed
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemview = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendance_listing, parent, false);
        return new MyViewHolder(itemview);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        AttendanceDetailsInfo attendanceDetailsInfo = attendanceDetailsInfoList.get(position);


        if (user_type_id.equals("1")) {
            holder.rl_spinner.setVisibility(View.VISIBLE);
            holder.ll_AttnSub.setVisibility(View.VISIBLE);
        } else if (user_type_id.equals("2")) {
            holder.rl_spinner.setVisibility(View.VISIBLE);
            holder.ll_AttnSub.setVisibility(View.VISIBLE);
        } else if (user_type_id.equals("16")) {
            holder.rl_spinner.setVisibility(View.VISIBLE);
            holder.ll_AttnSub.setVisibility(View.VISIBLE);
        } else if (user_type_id.equals("3")) {
            holder.rl_spinner.setVisibility(View.GONE);
            holder.ll_AttnSub.setVisibility(View.GONE);


        } else if (user_type_id.equals("4")) {
            holder.rl_spinner.setVisibility(View.GONE);
            holder.ll_AttnSub.setVisibility(View.GONE);

        } else {

        }



        String statusAtt = valueOf(attendanceDetailsInfo.getAttendanceStatus());
        if (statusAtt != null) {
            if (statusAtt.length() > 0) {
                holder.tv_status.setText(statusAtt);
                if (statusAtt.equals("Present")) {
                    holder.tv_status.setTextColor(mContext.getResources().getColor(R.color.colorGreen));

                } else if (statusAtt.equals("Absent")) {
                    holder.tv_status.setTextColor(mContext.getResources().getColor(R.color.colorRedDark));

                } else if (statusAtt.equals("HalfDay")) {
                    holder.tv_status.setTextColor(mContext.getResources().getColor(R.color.imagepicker_colorAccent));
                } else {
                    holder.tv_status.setTextColor(mContext.getResources().getColor(R.color.colorDarkBlue));
                }

            } else {
                holder.tv_status.setText("-----");
            }
        } else {
            holder.tv_status.setText("------");
        }


        String DisName = attendanceDetailsInfo.getDistrictName();
        holder.txtDis.setText(DisName);


        String remarkAtt = valueOf(attendanceDetailsInfo.getRemark()).trim();





        if (remarkAtt.equals("null")) {
            // Handle the case where remarkAtt is null
            Log.d("remarkDebug", "Remark is null");
            holder.tv_remark.setText("ok");
            holder.ll_remark.setVisibility(View.INVISIBLE);
        } else if (remarkAtt.isEmpty()) {
            // Handle the case where remarkAtt is an empty string
            Log.d("remarkDebug", "Remark is an empty string");
            holder.tv_remark.setText("------");
            holder.ll_remark.setVisibility(View.INVISIBLE);

        } else {
            // Handle the case where remarkAtt is a non-empty string
            Log.d("remarkDebug", "Remark length: " + remarkAtt.length());
            holder.tv_remark.setText(remarkAtt);
            holder.ll_remark.setVisibility(View.VISIBLE);

        }















        if (attendanceDetailsInfo.getPunchInDate() != null){
            holder.txtDateAndDay.setText(attendanceDetailsInfo.getPunchInDate());

        }

        else {

        }
        String PunchInTime = attendanceDetailsInfo.getPunchInTime();
        String PunchOutTime = attendanceDetailsInfo.getPunchOutTime();
        holder.txtusername.setText(attendanceDetailsInfo.getUsername());
        Log.d("remarkkhkjkhkhk", "PunchInTime" + PunchInTime);
        Log.d("remarkkhkjkhkhk", "PunchOutTime" + PunchOutTime);

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

        Log.d("remarkkhkjkhkhk", "PunchInTimeyhfgh" + PunchInTime);
        Log.d("remarkkhkjkhkhk", "PunchOutTimehfgh" + PunchOutTime);
        if (PunchInTime != null && PunchOutTime != null) {
            holder.txtTimeStatus.setText(PunchInTime + " - " + PunchOutTime);
            Log.d("yes","first");
        } else if (PunchInTime != null  && PunchOutTime==null) {
            holder.txtTimeStatus.setText(PunchInTime + " - " + "____");
            Log.d("yes","sec");

        } else if (PunchInTime == null &&PunchOutTime==null ) {
            holder.txtTimeStatus.setText("____" + " - " + "____");
            Log.d("yes","third");

        }
        else {
            Log.d("yes","fourth");

        }


      /*  if (PunchInTime != null && PunchOutTime != null) {
            holder.txtTimeStatus.setText(PunchInTime + " - " + PunchOutTime);
        } else if (PunchInTime != null) {
            holder.txtTimeStatus.setText(PunchInTime + " - " + "__:__");
        } else if (PunchOutTime != null) {
            holder.txtTimeStatus.setText("__:__" + " - " + PunchOutTime);
        }*/

        holder.crd_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userIdId = String.valueOf(attendanceDetailsInfo.getUserId());
                PunchInDate = attendanceDetailsInfo.getPunchInDate();
                Log.d("PunchInDate","--" +PunchInDate);
                districtId = String.valueOf(attendanceDetailsInfo.getDistrictId());

                originalDate = PunchInDate;
                DateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date date = null;
                try {
                    date = originalFormat.parse(originalDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
                formattedDate = targetFormat.format(date);
                Log.d("PunchInDate", "--"+formattedDate);

// formattedDate is now "2023-08-04"

                Intent i = new Intent(mContext, AttendanceViewDetail.class);
                i.putExtra("YouruserIdId", userIdId);
                i.putExtra("formattedDate", formattedDate);
                i.putExtra("districtId", districtId);

                // i.putExtra("YouruserIdId", userIdId);

                mContext.startActivity(i);


                // Toast.makeText(mContext, " "+ attendanceDetailsInfo.getUsername(), Toast.LENGTH_SHORT).show();

            }
        });
        Log.d("Att_Size", " " + statusDatumList.size());

        if (statusDatumList.size() > 0) {


            //  playerNames.add(statusDatumList.get(position).getAttendanceStatus());
            //   Collections.singletonList(statusDatumList.get(position).getAttendanceStatus())

            ArrayList<String> arrayList = new ArrayList();

            for (int i = 0; i < statusDatumList.size(); i++) {
                //  playerNames.add(attStatusData.get(i).getAttendanceStatus().toString());
                arrayList.add(statusDatumList.get(i).getAttendanceStatus());
                Log.d("Att_Status", " " + statusDatumList.get(i).getAttendanceStatus());

            }


            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, arrayList);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
            holder.spinnerAttType.setAdapter(spinnerArrayAdapter);
          //  holder.spinnerAttType.setSelection(attendanceDetailsInfo.getAttendanceStatusId() - 1);
            holder.spinnerAttType.setSelection(attendanceDetailsInfo.getAttendanceStatusId() - 1);
            Log.d("StatusIdStatusId", " " + attendanceDetailsInfo.getAttendanceStatusId());


            holder.spinnerAttType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    StatusId = valueOf(statusDatumList.get(i).getStatusID());
                    Statustype = valueOf(statusDatumList.get(i).getAttendanceStatus());
                    Log.d("StatusId", " " + StatusId);
                }


                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

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
//            holder.txtStatus.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
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


        int notnull = 0;
        for (AttendanceDetailsInfo Info : attendanceDetailsInfoList) {
            if (Info.getPunchOutTime() != null) {
                notnull++;
            }

        }
        Log.d("nullCount", "" + notnull);
        String qty = valueOf(notnull);
        Intent intent = new Intent("custom-message");
        //            intent.putExtra("quantity",Integer.parseInt(quantity.getText().toString()));
        intent.putExtra("quantity", qty);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);


        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.shadow_dark));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.shadow_light));
        }




//gagandeep
        holder.ll_AttnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("");
                // Set an EditText view to get user input

                // set the custom layout
                final View customLayout = LayoutInflater.from(mContext).inflate(R.layout.alartdialoge, null);
                builder.setView(customLayout);
                final EditText editText = customLayout.findViewById(R.id.inputRemark); // replace 'editTextId' with the actual ID of your EditText

                // add a button
                builder.setPositiveButton("Ok", (dialog, which) -> {
                            if (Utils.isNetworkAvailable(mContext)) {
                                Log.d("useid", user_type_id + " " + userIdId);
                                Log.d("StatusId", " " + StatusId);

                                //  Utils.hideKeyboard(AttendanceListingAdapter.class);
                                // Utils.showCustomProgressDialogCommonForAll((Activity) mContext, mContext.getResources().getString(R.string.please_wait));
                                holder.progressBar.setVisibility(View.VISIBLE);
                                APIInterface apiInterface = APIClient.GetRetrofitClientWithoutHeaders(mContext, BaseUrl()).create(APIInterface.class);
                                Remark = editText.getText().toString();
                                preference = new PrefManager(mContext);

                                String USER_Id = preference.getUSER_Id();


                                AttendanceId = String.valueOf(attendanceDetailsInfo.getId());
                                ToUserId = String.valueOf(attendanceDetailsInfo.getUserId());
                                Log.d("AttendanceId", AttendanceId);
                                Log.d("USER_Id", USER_Id);
                                Log.d("ToUserId", ToUserId);
                                Log.d("StatusId", StatusId);
                                Call<UpdateAttendanceStatusRoot> call = apiInterface.UpdateAttStatus(AttendanceId, USER_Id, ToUserId, StatusId, Remark);
                                call.enqueue(new Callback<UpdateAttendanceStatusRoot>() {
                                    @Override
                                    public void onResponse(@NonNull Call<UpdateAttendanceStatusRoot> call, @NonNull Response<UpdateAttendanceStatusRoot> response) {
                                        Utils.hideCustomProgressDialogCommonForAll();
                                        holder.progressBar.setVisibility(View.GONE);
                                        if (response.isSuccessful()) {
                                            if (response.code() == 200) {
                                                if (response.body() != null) {
                                                    if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {
                                                        Utils.hideCustomProgressDialogCommonForAll();
                                                        UpdateAttendanceStatusRoot getErrorTypesRoot = response.body();
                                                        Log.d("ressssssssss..", "getErrorTypesRoot.." + response.body().toString());
                                                        Log.d("getErrorTypesRoot..", "getErrorTypesRoot.." + getErrorTypesRoot);
                                                        Log.d("dataaaaaa", "remarkkk" + getErrorTypesRoot.getData().getRemark());
                                                        Log.d("dataaaaaa..", "statusss" + getErrorTypesRoot.getData().getAttendanceStatus());

                                                        String status = getErrorTypesRoot.getStatus();
                                                        if (status.equals("200")) {

                                                            // Intent i =new Intent(mContext, AttendanceListingActivity.class);
                                                            //  mContext.startActivity(i);

                                                            // notifyItemChanged(holder.getAdapterPosition());

                                                            attendanceDetailsInfoList.get(position).setAttendanceStatus(getErrorTypesRoot.getData().getAttendanceStatus());
                                                            attendanceDetailsInfoList.get(position).setRemark(getErrorTypesRoot.getData().getRemark());
                                                            attendanceDetailsInfoList.get(position).setAttendanceStatusId(getErrorTypesRoot.getData().getStatusID());

                                                            //  attendanceDetailsInfoList.get(position).setAttendanceStatus(Statustype);
                                                            //  attendanceDetailsInfoList.get(position).setRemark(Remark);


                                                            notifyDataSetChanged();
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

                                                            makeToast(response.body().getMessage());

                                                        } else {
                                                            //    makeToast(response.body().getMessage());
                                                        }

                                                        Log.d("getErrorTypesRoot..", "getErrorTypesRoot.." + getErrorTypesRoot);


                                                        //   makeToast(response.body().getResponse().getMessage());
                                                        Log.d(valueOf(AttendanceListingAdapter.this), "fggafdaf" + getErrorTypesRoot);


                                                    } else {
                                                        makeToast(valueOf(response.body().getMessage()));
                                                    }


                                                } else {
                                                    makeToast(mContext.getResources().getString(R.string.error));
                                                }
                                            } else {
                                                makeToast(mContext.getResources().getString(R.string.error));
                                            }
                                        } else {
                                            makeToast(mContext.getResources().getString(R.string.error));
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<UpdateAttendanceStatusRoot> call, @NonNull Throwable error) {
                                        Utils.hideCustomProgressDialogCommonForAll();
                                        holder.progressBar.setVisibility(View.GONE);

                                        makeToast(mContext.getResources().getString(R.string.error));

                                        call.cancel();
                                    }
                                });
                            } else {
                                makeToast(mContext.getResources().getString(R.string.no_internet_connection));

                            }

                        })

                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Handle Cancel button click
                            }
                        });
                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });


    }

// Add code nby me Now
  /*  public void addItems(List<AttendanceDetailsInfo> newItems) {
        int startPosition = attendanceDetailsInfoList.size();
        attendanceDetailsInfoList.addAll(newItems);
        notifyItemRangeInserted(startPosition, newItems.size());
    }*/

    //






    public void makeToast(String string) {
        if (TextUtils.isEmpty(string)) return;
        Toast.makeText(mContext, string, Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        if (attendanceDetailsInfoList == null)
            return 0;
        else
            return attendanceDetailsInfoList.size();
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
  public void addData(List<AttendanceDetailsInfo> newAttendanceList, List<AttStatusDatum> attStatusData) {
        if (attendanceDetailsInfoList == null) {
            attendanceDetailsInfoList = new ArrayList<>();
            this.statusDatumList = attStatusData;

        }

        int startPosition = attendanceDetailsInfoList.size();
        attendanceDetailsInfoList.addAll(newAttendanceList);

      //  this.statusDatumList = attStatusData;

        notifyItemRangeInserted(startPosition, newAttendanceList.size()); // Optimized refresh
    }






    public interface OnItemViewClickListener {
        void onItemClick(AttendanceDetailsInfo surveyFormDetailsInfo, int position);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtDateAndDay, txtDate, txtTimeStatus, txtTime;
        public LinearLayout buttonViewUploaded, ll_AttnSub, ll_status;
        RelativeLayout rl_spinner;
        CardView crd_item;
        AppCompatTextView txtusername, tv_status, tv_remark,txtDis;
        ProgressBar progressBar;
        AppCompatSpinner spinnerAttType;
        LinearLayout ll_remark;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDateAndDay = itemView.findViewById(R.id.txtDateAndDay);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtTimeStatus = itemView.findViewById(R.id.txtTimeStatus);
            txtTime = itemView.findViewById(R.id.txtTime);
            crd_item = itemView.findViewById(R.id.crd_item);
            txtusername = itemView.findViewById(R.id.txtusername);
            buttonViewUploaded = itemView.findViewById(R.id.buttonViewUploaded);
            rl_spinner = itemView.findViewById(R.id.rl_spinner);
            spinnerAttType = itemView.findViewById(R.id.spinnerAttType);
            ll_AttnSub = itemView.findViewById(R.id.ll_AttnSub);
            tv_status = itemView.findViewById(R.id.tv_status);
            tv_remark = itemView.findViewById(R.id.tv_remark);
            progressBar = itemView.findViewById(R.id.progressBar);
            txtDis = itemView.findViewById(R.id.txtDis);
            ll_remark = itemView.findViewById(R.id.ll_remark);


        }
    }
}