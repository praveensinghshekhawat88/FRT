package com.callmangement.ui.errors.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.R
import com.callmangement.databinding.ItemManagerPosErrorActivityBinding
import com.callmangement.network.APIService
import com.callmangement.network.RetrofitInstance
import com.callmangement.ui.errors.activity.ErrorPosActivity
import com.callmangement.ui.errors.activity.ErrorPosUpdateActivity
import com.callmangement.ui.errors.activity.ErrorposDetail
import com.callmangement.ui.errors.model.GetPosDeviceErrorDatum
import com.callmangement.ui.errors.model.UpdateErrorStatusRoot
import com.callmangement.utils.Constants.isNetworkAvailable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class ErrorPosAdapter(
    private val context: Context,
    getPosDeviceErrorDatumArrayList: ArrayList<GetPosDeviceErrorDatum>,
    var user_type_id: String
) :
    RecyclerView.Adapter<ErrorPosAdapter.ViewHolder>(), Filterable {
    private val getPosDeviceErrorDatumArrayList: ArrayList<GetPosDeviceErrorDatum>? =
        getPosDeviceErrorDatumArrayList
    var manager_review: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemManagerPosErrorActivityBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    fun makeToast(string: String?) {
        if (TextUtils.isEmpty(string)) return
        Toast.makeText(context.applicationContext, string, Toast.LENGTH_SHORT).show()
    }


    /* public void showProgress(String message) {
        hideProgress();
        mDialog = new ProgressDialog(context.getApplicationContext());
        mDialog.setMessage(message);
        mDialog.setCancelable(false);
        mDialog.show();
    }

    public void showProgress() {
        hideProgress();
        mDialog = new ProgressDialog(context.getApplicationContext());
        mDialog.setMessage("Loading...");
        mDialog.setCancelable(false);
        mDialog.show();
    }

    public void hideProgress() {
        try {
            if (mDialog!=null) mDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = getPosDeviceErrorDatumArrayList!![position]


        //  Log.d("getDealerMobileNo", " "+model.getDealerMobileNo());
        if (user_type_id == "1") {
            holder.binding.tvItemStatus.visibility = View.VISIBLE
            holder.binding.tvUpdate.visibility = View.INVISIBLE
        } else if (user_type_id == "2") {
            holder.binding.tvItemStatus.visibility = View.VISIBLE
            holder.binding.tvUpdate.visibility = View.INVISIBLE
        } else if (user_type_id == "4") {
            if (model.errorStatus == "Pending") {
                holder.binding.tvItemStatus.visibility = View.INVISIBLE
                holder.binding.tvUpdate.visibility = View.VISIBLE
            } else if (model.errorStatus == "Checking") {
                holder.binding.tvItemStatus.visibility = View.INVISIBLE
                holder.binding.tvUpdate.visibility = View.VISIBLE
            } else if (model.errorStatus == "Resolved") {
                holder.binding.tvItemStatus.visibility = View.INVISIBLE
                holder.binding.tvUpdate.visibility = View.INVISIBLE
            }


            // districtId = prefManager.getUseR_DistrictId();
        }


        holder.binding.tvUpdate.setOnClickListener { view: View? ->
            val i = Intent(context, ErrorPosUpdateActivity::class.java)
            i.putExtra("param", model)
            context.startActivity(i)
        }
        holder.binding.texterrorregno.text = model.errorRegNo
        holder.binding.textDistrictName.text = model.districtNameEng
        holder.binding.textName.text = model.dealerName
        holder.binding.type.text = model.errorType

        //   holder.binding.errortype.setText(model.getErrorStatus());
        holder.binding.fpscode.text = model.fpscode
        holder.binding.regdate.text = model.errorRegDate
        holder.binding.resolvedate.text = model.resolveDate

        if (model.errorStatus == "Pending") {
            holder.binding.errortype.text = model.errorStatus
            holder.binding.errortype.setTextColor(context.resources.getColor(R.color.colorRedDark))

            holder.binding.tvItemStatus.text = "Checking"
            holder.binding.tvItemStatus.setBackgroundResource(R.drawable.redbg)
        } else if (model.errorStatus == "Checking") {
            holder.binding.errortype.text = model.errorStatus
            holder.binding.errortype.setTextColor(context.resources.getColor(R.color.colorActionBar))
            holder.binding.tvItemStatus.text = "Resolved"
            holder.binding.tvItemStatus.setBackgroundResource(R.drawable.bg_rounded_corner_layout)
        } else if (model.errorStatus == "Resolved") {
            holder.binding.errortype.text = model.errorStatus
            holder.binding.errortype.setTextColor(context.resources.getColor(R.color.colorGreenDark))
            holder.binding.tvItemStatus.visibility = View.INVISIBLE


            //  holder.binding.tvItemStatus.setText(model.getErrorStatus());
            //  holder.binding.tvItemStatus.setBackgroundResource(R.drawable.greenbg);
        }

        /* else if (model.getErrorStatusId() == 3) {
            holder.binding.errortype.setText(model.getErrorStatus());
            holder.binding.errortype.setTextColor(context.getResources().getColor(R.color.colorGreenDark));
        }*/
        if (model.createdbyName.isEmpty()) {
            //   holder.binding.expenseCompletedDateLay.setVisibility(View.GONE);
        } else {
            //  holder.binding.expenseCompletedDateLay.setVisibility(View.VISIBLE);
            // holder.binding.textExpenseCompletedDate.setText(model.getCreatedbyName());
        }









        holder.binding.tvItemStatus.setOnClickListener { view: View? ->
            /*     Intent intent = new Intent(context, ErrorPosUpdateActivity.class);
              // intent.putExtra("param", String.valueOf(model));
                 context.startActivity(intent);*/
            val builder = AlertDialog.Builder(context)
            builder.setTitle("")


            // set the custom layout
            val customLayout = LayoutInflater.from(context).inflate(R.layout.alartdialoge, null)
            builder.setView(customLayout)

            // add a button
            builder.setPositiveButton(R.string.ok) { dialog: DialogInterface?, which: Int ->
                // send data from the AlertDialog to the Activity
                val editText = customLayout.findViewById<EditText>(R.id.inputRemark)
                manager_review = editText.text.toString()
                if (isNetworkAvailable(context.applicationContext)) {
                    holder.binding.progressBar.visibility = View.VISIBLE
                    val service = RetrofitInstance.getRetrofitInstance().create(
                        APIService::class.java
                    )

                    val ErrorStatusIdd = model.errorStatusId
                    var modularStatusIdd: String? = null
                    if (ErrorStatusIdd == 1) {
                        modularStatusIdd = 2.toString()
                    } else if (ErrorStatusIdd == 2) {
                        modularStatusIdd = 3.toString()
                    } else if (ErrorStatusIdd == 3) {
                        modularStatusIdd = 4.toString()
                    }


                    val ErrorId = model.errorId.toString()
                    val errorregno = model.errorRegNo.toString()
                    val remark = model.errorStatus.toString()


                    //     Log.d("updaterrorstatus"," "+ErrorId+" " +user_type_id+" "+errorregno+" "+ErrorStatusIdd+" "+remark);
                    val call = service.UpdateErrorStatus(
                        ErrorId,
                        user_type_id,
                        errorregno,
                        modularStatusIdd,
                        manager_review
                    )
                    call.enqueue(object : Callback<UpdateErrorStatusRoot?> {
                        @SuppressLint("SetTextI18n")
                        override fun onResponse(
                            call: Call<UpdateErrorStatusRoot?>,
                            response: Response<UpdateErrorStatusRoot?>
                        ) {
                            holder.binding.progressBar.visibility = View.GONE

                            if (response.isSuccessful) {
                                try {
                                    if (response.code() == 200) {
                                        if (response.body() != null) {
                                            if (response.body()!!.status == "200") {
                                                val updateErrorStatusRoot = response.body()

                                                //      Log.d("getErrorTypesRoot..","getErrorTypesRoot.."+updateErrorStatusRoot);
                                                val checkstatus =
                                                    updateErrorStatusRoot!!.response.isStatus
                                                if (checkstatus) {
                                                    //     Log.d("checkstatus","checkstatus"+checkstatus);

                                                    val i =
                                                        Intent(
                                                            context,
                                                            ErrorPosActivity::class.java
                                                        )
                                                    context.startActivity(i)
                                                    makeToast(response.body()!!.response.message)
                                                } else {
                                                    makeToast(response.body()!!.response.message)
                                                }
                                            } else {
                                                makeToast(context.resources.getString(R.string.error_message))
                                            }
                                        } else {
                                            makeToast(context.resources.getString(R.string.error_message))
                                        }
                                    } else {
                                        makeToast(context.resources.getString(R.string.error_message))
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    makeToast(context.resources.getString(R.string.error_message))
                                }
                            } else {
                                makeToast(context.resources.getString(R.string.error_message))
                            }
                        }

                        override fun onFailure(
                            call: Call<UpdateErrorStatusRoot?>,
                            t: Throwable
                        ) {
                            //  hideProgress();
                            holder.binding.progressBar.visibility = View.GONE

                            makeToast(context.resources.getString(R.string.error_message))
                        }
                    })
                } else {
                    makeToast(context.resources.getString(R.string.no_internet_connection))
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



        holder.binding.rlItem.setOnClickListener { view: View? ->
            val intent = Intent(context, ErrorposDetail::class.java)
            intent.putExtra("param", model)
            context.startActivity(intent)
        }


        //  holder.binding.t
    }

    override fun getItemCount(): Int {
        // return getPosDeviceErrorDatumArrayList.size();
        return getPosDeviceErrorDatumArrayList?.size ?: 0
    }


    class ViewHolder(val binding: ItemManagerPosErrorActivityBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun getFilter(): Filter {
        return Searched_Filter
    }

    private val Searched_Filter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList = ArrayList<GetPosDeviceErrorDatum>()
            if (constraint == null || constraint.length == 0) {
                filteredList.addAll(getPosDeviceErrorDatumArrayList)
            } else {
                val filterPattern =
                    constraint.toString().lowercase(Locale.getDefault()).trim { it <= ' ' }
                for (item in getPosDeviceErrorDatumArrayList) {
                    if (item.errorRegNo.lowercase(Locale.getDefault())
                            .contains(filterPattern) || item.deviceCode.lowercase(
                            Locale.getDefault()
                        ).contains(filterPattern) || item.fpscode.lowercase(
                            Locale.getDefault()
                        ).contains(filterPattern)
                    ) {
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            if (results.values != null) {
                getPosDeviceErrorDatumArrayList.clear()
                getPosDeviceErrorDatumArrayList.addAll(results.values as Collection<GetPosDeviceErrorDatum>)
                notifyDataSetChanged()
            }
        }
    }
}
