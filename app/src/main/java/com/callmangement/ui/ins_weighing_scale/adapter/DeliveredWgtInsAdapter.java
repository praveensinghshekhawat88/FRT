package com.callmangement.ui.ins_weighing_scale.adapter;
import static com.callmangement.utils.Constants.API_BASE_URL;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.callmangement.network.APIService;
import com.callmangement.network.RetrofitInstance;
import com.callmangement.R;
import com.callmangement.ui.ins_weighing_scale.activity.ViewDetail;
import com.callmangement.ui.ins_weighing_scale.activity.NewDelivery;
import com.callmangement.ui.ins_weighing_scale.model.DeliveredWeightInstal.WeighInsData;
import com.callmangement.ui.ins_weighing_scale.model.challan.challanRoot;
import com.callmangement.utils.Constants;
import com.callmangement.utils.PrefManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DeliveredWgtInsAdapter extends RecyclerView.Adapter<DeliveredWgtInsAdapter.ViewHolder> {
    private final ArrayList<WeighInsData> weighInsDataArrayList;
    private static  Context context;
    private static  Activity activity;

    String user_type_id;
    private static String filemyurl;
    private static PrefManager prefManager;

    public DeliveredWgtInsAdapter(ArrayList<WeighInsData> weighInsDataArrayList, Context context, Activity activity,String user_type_id) {
        this.weighInsDataArrayList = weighInsDataArrayList;
        DeliveredWgtInsAdapter.context = context;
        DeliveredWgtInsAdapter.activity = activity;
        this.user_type_id = user_type_id;

    }

// data is passed into the constructor


    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_deliveredlistl, parent, false);
        return new ViewHolder(view);
    }

    public static void makeToast(String string) {
        if (TextUtils.isEmpty(string)) return;
        Toast.makeText(context.getApplicationContext(), string, Toast.LENGTH_SHORT).show();

    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
      WeighInsData model;
        model = weighInsDataArrayList.get(position);


        if (user_type_id.equals("1")) {
            holder.tv_installed.setVisibility(View.GONE);
            holder.tv_verify.setVisibility(View.GONE);

        } else if (user_type_id.equals("2")) {
            holder.tv_installed.setVisibility(View.GONE);
            holder.tv_verify.setVisibility(View.GONE);


        } else if (user_type_id.equals("4")) {
            holder.tv_installed.setVisibility(View.VISIBLE);
            holder.tv_verify.setVisibility(View.VISIBLE);
            if(model.isDeliveryVerify){
                holder.tv_installed.setVisibility(View.VISIBLE);
                holder.tv_verify.setVisibility(View.GONE);
            }else {
                holder.tv_installed.setVisibility(View.GONE);
                holder.tv_verify.setVisibility(View.VISIBLE);
            }
            // districtId = prefManager.getUSER_DistrictId();
        }


        String DealerName = weighInsDataArrayList.get(position).getDealerName();
        String Fpscode = weighInsDataArrayList.get(position).getFpscode();
        String TranDateStr = String.valueOf(weighInsDataArrayList.get(position).getWinghingScaleDeliveredOnStr());
        String DealerMobileNo = weighInsDataArrayList.get(position).getDealerMobileNo();
        String TicketNo = weighInsDataArrayList.get(position).getTicketNo();
        String WeighingScaleSerialNo = weighInsDataArrayList.get(position).getWeighingScaleSerialNo();

        Log.d("mydataatt", "" + model);
        Log.d("mydataatt", DealerName);
        Log.d("mydataatt", Fpscode);
        Log.d("mydataatt", TranDateStr);


        holder.tv_dealername.setText(DealerName);
        holder.tv_fpscode.setText(Fpscode);
        holder.tv_trasdate.setText(TranDateStr);
        holder.tv_dealermobno.setText(DealerMobileNo);
        holder.tv_ticketno.setText(TicketNo);
        holder.tv_weightscaleno.setText(WeighingScaleSerialNo);
       /* holder.tv_view.setOnClickListener(view -> {
            Intent intent = new Intent(context, ViewDetail.class);
           intent.putExtra("param", model);
            context.startActivity(intent);

        });
*/

        holder.downlode_chlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                // Check if the device supports vibration
                if (vibrator.hasVibrator()) {
                    // Vibrate for 500 milliseconds (0.5 seconds)
                    vibrator.vibrate(100);

                    Animation animation = AnimationUtils.loadAnimation(context, R.anim.scale_anim);

                    // Apply the animation to the ImageView
                    holder.downlode_chlan.startAnimation(animation);




               openPdfchallanap( holder,model);



                }


            }
        });
       /* if (status.equals("Yes")& status.length()!=0)
        {
            holder.tv_status.setTextColor(Color.parseColor("#43A047"));
            holder.tv_status.setText(status);
        }
        else if (status.equals("No")& status.length()!=0)
        {
            holder.tv_status.setTextColor(Color.parseColor("#F44336"));
            holder.tv_status.setText(status);

        }
else {

        }*/

        holder.tv_installed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NewDelivery.class);
                intent.putExtra("param", model);
                try {
                    activity.startActivityForResult(intent, 444); // Use startActivityForResult to get the selected file's URI
                } catch (ActivityNotFoundException e) {
                    // Handle the case where no app capable of handling this intent is installed
                }
            }
        });
        holder.tv_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewDetail.class);
                intent.putExtra("param", model);
                intent.putExtra("isFrom", "Verify");
                try {
                    activity.startActivityForResult(intent, 444); // Use startActivityForResult to get the selected file's URI
                } catch (ActivityNotFoundException e) {
                    // Handle the case where no app capable of handling this intent is installed
                }


            }
        });

        holder.tv_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewDetail.class);
                intent.putExtra("param", model);
                intent.putExtra("isFrom", "View");
                try {
                    activity.startActivityForResult(intent, 444); // Use startActivityForResult to get the selected file's URI
                } catch (ActivityNotFoundException e) {
                    // Handle the case where no app capable of handling this intent is installed
                }
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return weighInsDataArrayList.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_ticketno, tv_fpscode, tv_dealername, tv_dealermobno, tv_weightscaleno, tv_trasdate;
        AppCompatTextView tv_view, tv_installed, tv_verify;
        LinearLayout rlItem;
        ImageView downlode_chlan;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_ticketno = itemView.findViewById(R.id.tv_ticketno);
            tv_fpscode = itemView.findViewById(R.id.tv_fpscode);
            tv_dealername = itemView.findViewById(R.id.tv_dealername);
            tv_dealermobno = itemView.findViewById(R.id.tv_dealermobno);
            tv_weightscaleno = itemView.findViewById(R.id.tv_weightscaleno);
            tv_trasdate = itemView.findViewById(R.id.tv_trasdate);
            rlItem = itemView.findViewById(R.id.rlItem);
            tv_view = itemView.findViewById(R.id.tv_view);
            tv_installed = itemView.findViewById(R.id.tv_installed);
            tv_verify = itemView.findViewById(R.id.tv_verify);
            downlode_chlan = itemView.findViewById(R.id.downlode_chlan);
            progressBar = itemView.findViewById(R.id.progressBar);
            //  itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
        }
    }




    private static void openPdfchallanap(ViewHolder holder, WeighInsData model) {

        if (Constants.isNetworkAvailable(context.getApplicationContext())) {
            holder.progressBar.setVisibility(View.VISIBLE);
            APIService service = RetrofitInstance.getRetrofitInstance().create(APIService.class);

           // Integer ErrorStatusIdd = model.getErrorStatusId();
            prefManager = new PrefManager(context);

            String userid = prefManager.getUSER_Id();
            String FPSCode = String.valueOf(model.getFpscode());
            String DeviceTypeId = String.valueOf(model.getDeviceTypeId());
            Log.d("response", " " + userid + " " + FPSCode + " " + DeviceTypeId );


            Call<challanRoot> call = service.DownloadAPIChallanPDF(userid, FPSCode, DeviceTypeId, "2", "0");
            call.enqueue(new Callback<challanRoot>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NonNull Call<challanRoot> call, @NonNull Response<challanRoot> response) {
                    holder.progressBar.setVisibility(View.GONE);

                    if (response.isSuccessful()) {
                        try {
                            if (response.code() == 200) {
                                if (response.body() != null) {

                                    if (Objects.requireNonNull(response.body()).getStatus().equals("200")) {
                                        challanRoot challanroot = response.body();
                                        Log.d("getErrorTypesRoot..", "getErrorTypesRoot.." + challanroot);
                                     String fileUrl = challanroot.getData().getFileUrl();
                                     String endName = challanroot.getData().getFileName();
                                    filemyurl = API_BASE_URL+fileUrl+endName;
                                    //  filemyurl = "https//gggg.pdf";
                                       String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

                                        //     File outputFile = new File("/storage/emulated/0/Download/" + timeStamp+".pdf");


                                      //  String fileUrl = "https://cdn1.byjus.com/wp-content/uploads/2019/10/My-School-Essay-for-class-1.pdf";
                                        String fileName = timeStamp + ".pdf";

                                        DownloadHelper.downloadFile(context, filemyurl, fileName);

                                       // Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                                      Toast.makeText(context, "Downloaded", Toast.LENGTH_SHORT).show();

                                       // context.startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
                                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                                        intent.setType("application/pdf"); // Filter for PDF files

                                       /* try {
                                          context.startActivity(intent ); // Use startActivityForResult to get the selected file's URI
                                        } catch (ActivityNotFoundException e) {
                                            // Handle the case where no app capable of handling this intent is installed
                                        }*/


                                        try {
                                            activity.startActivityForResult(intent, 1); // Use startActivityForResult to get the selected file's URI
                                        } catch (ActivityNotFoundException e) {
                                            // Handle the case where no app capable of handling this intent is installed
                                        }


                                       /* Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse("file:///storage/emulated/0/Download"));
                                        context.startActivity(intent);*/




                                       // openPdfFile(context, fileName);
                                       // String fileName = "your_pdf_file_name.pdf"; // Replace "your_pdf_file_name.pdf" with your actual PDF file name
                                // PdfHelper.openPdfFile(context, fileName,filemyurl);

                                       /* Uri uri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setDataAndType(uri, "resource/folder");
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(Intent.createChooser(intent, "Open Downloads Folder"));
*/

                                     //   context.startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));













                                       /* if (checkstatus == true) {
                                            Log.d("checkstatus", "checkstatus" + checkstatus);

                                            Intent i = new Intent(context, ErrorPosActivity.class);
                                            context.startActivity(i);
                                            makeToast(response.body().getData().getMessage());
                                        } else {

                                            makeToast(response.body().getData().getMessage());
                                        }*/


                                    } else {
                                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                                     //   makeToast(context.getResources().getString(R.string.error_message));

                                    }


                                } else {
                                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();

                                   // makeToast(context.getResources().getString(R.string.error_message));

                                }
                            } else {


                                String msg =  "HTTP Error: "+ response.code();
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

                              //  makeToast(context.getResources().getString(R.string.error_message));

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            String msg =  "HTTP Error: "+ response.code();
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        String msg =  "HTTP Error: "+ response.code();
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onFailure(@NonNull Call<challanRoot> call, @NonNull Throwable t) {
                    //  hideProgress();
                    holder.progressBar.setVisibility(View.GONE);

                    makeToast(context.getResources().getString(R.string.error_message));

                    //      binding.textNoDataFound.setVisibility(View.VISIBLE);
                }
            });
        } else {
            makeToast(context.getString(R.string.no_internet_connection));
        }

    }


 /*   public static void openPdfFile(Context context, String fileName) {

        File file = new File(getExternalFilesDir(null), targetPdf);
        Uri pdfURi = FileProvider.getUriForFile(mActivity, mActivity.getPackageName() + ".provider", file , targetPdf );
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        // share.setType("Attendance/pdf");
        share.setDataAndType(pdfURi, "application/pdf");
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        share.putExtra(Intent.EXTRA_STREAM, pdfURi);
        startActivity(share);
        // startActivity(Intent.createChooser(share, "Share via"));
        Log.d("yesss","ijowh"+pdfURi);



    }*/

}





