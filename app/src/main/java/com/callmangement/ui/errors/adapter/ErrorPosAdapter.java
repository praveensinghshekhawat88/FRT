package com.callmangement.ui.errors.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.callmangement.network.APIService;
import com.callmangement.network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.databinding.ItemManagerPosErrorActivityBinding;

import com.callmangement.ui.errors.activity.ErrorPosActivity;
import com.callmangement.ui.errors.activity.ErrorPosUpdateActivity;
import com.callmangement.ui.errors.activity.ErrorposDetail;
import com.callmangement.ui.errors.model.GetPosDeviceErrorDatum;
import com.callmangement.ui.errors.model.UpdateErrorStatusRoot;
import com.callmangement.utils.Constants;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ErrorPosAdapter extends RecyclerView.Adapter<ErrorPosAdapter.ViewHolder>  implements Filterable {
    private final Context context;
    private final ArrayList<GetPosDeviceErrorDatum> getPosDeviceErrorDatumArrayList;
    String manager_review;

    String user_type_id;

    public ErrorPosAdapter(Context context, ArrayList<GetPosDeviceErrorDatum> getPosDeviceErrorDatumArrayList, String user_type_id) {
        this.context = context;
        this.getPosDeviceErrorDatumArrayList = getPosDeviceErrorDatumArrayList;
        this.user_type_id = user_type_id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemManagerPosErrorActivityBinding binding = ItemManagerPosErrorActivityBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ViewHolder(binding);
    }
    public void makeToast(String string) {
        if (TextUtils.isEmpty(string)) return;
        Toast.makeText(context.getApplicationContext(), string, Toast.LENGTH_SHORT).show();

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

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        GetPosDeviceErrorDatum model = getPosDeviceErrorDatumArrayList.get(position);
      //  Log.d("getDealerMobileNo", " "+model.getDealerMobileNo());


        if (user_type_id.equals("1") ){
            holder.binding.tvItemStatus.setVisibility(View.VISIBLE);
            holder.binding.tvUpdate.setVisibility(View.INVISIBLE);

        } else if (user_type_id.equals("2")) {
            holder.binding.tvItemStatus.setVisibility(View.VISIBLE);
            holder.binding.tvUpdate.setVisibility(View.INVISIBLE);


        } else if (user_type_id.equals("4") ) {


            if (model.getErrorStatus().equals("Pending") ) {

                holder.binding.tvItemStatus.setVisibility(View.INVISIBLE);
                holder.binding.tvUpdate.setVisibility(View.VISIBLE);

            } else if (model.getErrorStatus().equals("Checking")) {
                holder.binding.tvItemStatus.setVisibility(View.INVISIBLE);
                holder.binding.tvUpdate.setVisibility(View.VISIBLE);

            }
            else if (model.getErrorStatus().equals("Resolved")) {
                holder.binding.tvItemStatus.setVisibility(View.INVISIBLE);
                holder.binding.tvUpdate.setVisibility(View.INVISIBLE);

            }


            // districtId = prefManager.getUseR_DistrictId();
        }


        holder.binding.tvUpdate.setOnClickListener(view -> {
            Intent i = new Intent(context, ErrorPosUpdateActivity.class);
            i.putExtra("param",  model);

            context.startActivity(i);

        });
      holder.binding.texterrorregno.setText(model.getErrorRegNo());
        holder.binding.textDistrictName.setText(model.getDistrictNameEng());
        holder.binding.textName.setText(model.getDealerName());
        holder.binding.type.setText(model.getErrorType());

     //   holder.binding.errortype.setText(model.getErrorStatus());
        holder.binding.fpscode.setText(model.getFpscode());
        holder.binding.regdate.setText(model.getErrorRegDate());
        holder.binding.resolvedate.setText(model.getResolveDate());

        if (model.getErrorStatus().equals("Pending") ) {





            holder.binding.errortype.setText(model.getErrorStatus());
           holder.binding.errortype.setTextColor(context.getResources().getColor(R.color.colorRedDark));

            holder.binding.tvItemStatus.setText("Checking");
            holder.binding.tvItemStatus.setBackgroundResource(R.drawable.redbg);


        } else if (model.getErrorStatus().equals("Checking")) {
            holder.binding.errortype.setText(model.getErrorStatus());
          holder.binding.errortype.setTextColor(context.getResources().getColor(R.color.colorActionBar));
            holder.binding.tvItemStatus.setText("Resolved");
            holder.binding.tvItemStatus.setBackgroundResource(R.drawable.bg_rounded_corner_layout);
        }
        else if (model.getErrorStatus().equals("Resolved")) {
            holder.binding.errortype.setText(model.getErrorStatus());
            holder.binding.errortype.setTextColor(context.getResources().getColor(R.color.colorGreenDark));
            holder.binding.tvItemStatus.setVisibility(View.INVISIBLE);
          //  holder.binding.tvItemStatus.setText(model.getErrorStatus());
          //  holder.binding.tvItemStatus.setBackgroundResource(R.drawable.greenbg);


        }
       /* else if (model.getErrorStatusId() == 3) {
            holder.binding.errortype.setText(model.getErrorStatus());
            holder.binding.errortype.setTextColor(context.getResources().getColor(R.color.colorGreenDark));
        }*/

        if (model.getCreatedbyName().isEmpty()) {
         //   holder.binding.expenseCompletedDateLay.setVisibility(View.GONE);
        } else {
          //  holder.binding.expenseCompletedDateLay.setVisibility(View.VISIBLE);
           // holder.binding.textExpenseCompletedDate.setText(model.getCreatedbyName());
        }









        holder.binding.tvItemStatus.setOnClickListener(view -> {
       /*     Intent intent = new Intent(context, ErrorPosUpdateActivity.class);
         // intent.putExtra("param", String.valueOf(model));
            context.startActivity(intent);*/


            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("");


            // set the custom layout
            final View customLayout = LayoutInflater.from(context).inflate(R.layout.alartdialoge, null);
            builder.setView(customLayout);

            // add a button
            builder.setPositiveButton(R.string.ok, (dialog, which) -> {
                // send data from the AlertDialog to the Activity
                EditText editText = customLayout.findViewById(R.id.inputRemark);
          manager_review  = editText.getText().toString();
                        if (Constants.isNetworkAvailable(context.getApplicationContext())) {
                         holder.binding.progressBar.setVisibility(View.VISIBLE);
                            APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);

                            Integer  ErrorStatusIdd = model.getErrorStatusId();
                            String modularStatusIdd = null;
                            if (ErrorStatusIdd==1)
                            {
                                modularStatusIdd = String.valueOf(2);
                            }

                            else if (ErrorStatusIdd==2)
                            {
                                modularStatusIdd = String.valueOf(3);

                            }
                            else if (ErrorStatusIdd==3)
                            {
                                modularStatusIdd = String.valueOf(4);

                            }




                            String  ErrorId = String.valueOf(model.getErrorId());
                            String  errorregno = String.valueOf(model.getErrorRegNo());
                            String  remark = String.valueOf(model.getErrorStatus());
                       //     Log.d("updaterrorstatus"," "+ErrorId+" " +user_type_id+" "+errorregno+" "+ErrorStatusIdd+" "+remark);


                            Call<UpdateErrorStatusRoot> call = service.UpdateErrorStatus(ErrorId,user_type_id,errorregno,modularStatusIdd,manager_review);
                            call.enqueue(new Callback<UpdateErrorStatusRoot>() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onResponse(@NonNull Call<UpdateErrorStatusRoot> call, @NonNull Response<UpdateErrorStatusRoot> response) {
                                    holder.binding.progressBar.setVisibility(View.GONE);

                                    if (response.isSuccessful()) {
                                        try {
                                            if (response.code() == 200) {
                                                if (response.body() != null) {

                                                    if (response.body().getStatus().equals("200")) {
                                                        UpdateErrorStatusRoot updateErrorStatusRoot = response.body();
                                                  //      Log.d("getErrorTypesRoot..","getErrorTypesRoot.."+updateErrorStatusRoot);

                                                        boolean checkstatus=updateErrorStatusRoot.getResponse().status;
                                                        if (checkstatus)

                                                        {
                                                       //     Log.d("checkstatus","checkstatus"+checkstatus);

                                                            Intent i =new Intent(context, ErrorPosActivity.class);
                                                            context.startActivity(i);
                                                            makeToast(response.body().getResponse().getMessage());
                                                        }
                                                        else
                                                        {

                                                            makeToast(response.body().getResponse().getMessage());
                                                        }








                                                    } else {
                                                        makeToast(context.getResources().getString(R.string.error_message));

                                                    }











                                                } else {
                                                    makeToast(context.getResources().getString(R.string.error_message));

                                                }
                                            } else {
                                                makeToast(context.getResources().getString(R.string.error_message));

                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            makeToast(context.getResources().getString(R.string.error_message));

                                        }
                                    } else {
                                        makeToast(context.getResources().getString(R.string.error_message));

                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<UpdateErrorStatusRoot> call, @NonNull Throwable t) {
                                    //  hideProgress();
                                    holder.binding.progressBar.setVisibility(View.GONE);

                                    makeToast(context.getResources().getString(R.string.error_message));

                                }
                            });
                        } else {
                            makeToast(context.getResources().getString(R.string.no_internet_connection));
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

















        });



        holder.binding.rlItem.setOnClickListener(view -> {
            Intent intent = new Intent(context, ErrorposDetail.class);
            intent.putExtra("param",  model);
            context.startActivity(intent);

        });


      //  holder.binding.t

    }

    @Override
    public int getItemCount() {
       // return getPosDeviceErrorDatumArrayList.size();
        if (getPosDeviceErrorDatumArrayList != null){
            return getPosDeviceErrorDatumArrayList.size();
        } else {
            return 0;
        }
    }

















    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemManagerPosErrorActivityBinding binding;
        public ViewHolder(ItemManagerPosErrorActivityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }





    @Override
    public Filter getFilter() {
        return Searched_Filter;
    }

    private final Filter Searched_Filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<GetPosDeviceErrorDatum> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(getPosDeviceErrorDatumArrayList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (GetPosDeviceErrorDatum item : getPosDeviceErrorDatumArrayList) {
                    if (item.getErrorRegNo().toLowerCase().contains(filterPattern) || item.getDeviceCode().toLowerCase().contains(filterPattern) || item.getFpscode().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                getPosDeviceErrorDatumArrayList.clear();
                getPosDeviceErrorDatumArrayList.addAll((ArrayList) results.values);
                notifyDataSetChanged();
            }
        }
    };













}
