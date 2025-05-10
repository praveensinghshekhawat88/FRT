package com.callmangement.ui.ins_weighing_scale.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Vibrator
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.callmangement.R
import com.callmangement.network.APIService
import com.callmangement.network.RetrofitInstance
import com.callmangement.ui.ins_weighing_scale.activity.NewDelivery
import com.callmangement.ui.ins_weighing_scale.activity.ViewDetail
import com.callmangement.ui.ins_weighing_scale.model.DeliveredWeightInstal.WeighInsData
import com.callmangement.ui.ins_weighing_scale.model.challan.challanRoot
import com.callmangement.utils.Constants.API_BASE_URL
import com.callmangement.utils.Constants.isNetworkAvailable
import com.callmangement.utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DeliveredWgtInsAdapter(
    private val weighInsDataArrayList: ArrayList<WeighInsData>,
    context: Context?,
    activity: Activity?,
    user_type_id: String
) :
    RecyclerView.Adapter<DeliveredWgtInsAdapter.ViewHolder>() {
    var user_type_id: String

    init {
        Companion.context = context
        Companion.activity = activity
        this.user_type_id = user_type_id
    }


    // data is passed into the constructor
    // inflates the row layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_deliveredlistl, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the TextView in each row
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = weighInsDataArrayList[position]


        if (user_type_id == "1") {
            holder.tv_installed.visibility = View.GONE
            holder.tv_verify.visibility = View.GONE
        } else if (user_type_id == "2") {
            holder.tv_installed.visibility = View.GONE
            holder.tv_verify.visibility = View.GONE
        } else if (user_type_id == "4") {
            holder.tv_installed.visibility = View.VISIBLE
            holder.tv_verify.visibility = View.VISIBLE
            if (model.isDeliveryVerify) {
                holder.tv_installed.visibility = View.VISIBLE
                holder.tv_verify.visibility = View.GONE
            } else {
                holder.tv_installed.visibility = View.GONE
                holder.tv_verify.visibility = View.VISIBLE
            }
            // districtId = prefManager.getUseR_DistrictId();
        }


        val DealerName = weighInsDataArrayList[position].dealerName
        val Fpscode = weighInsDataArrayList[position].fpscode
        val TranDateStr = weighInsDataArrayList[position].winghingScaleDeliveredOnStr.toString()
        val DealerMobileNo = weighInsDataArrayList[position].dealerMobileNo
        val TicketNo = weighInsDataArrayList[position].ticketNo
        val WeighingScaleSerialNo = weighInsDataArrayList[position].weighingScaleSerialNo

        Log.d("mydataatt", "" + model)
        Log.d("mydataatt", DealerName)
        Log.d("mydataatt", Fpscode)
        Log.d("mydataatt", TranDateStr)


        holder.tv_dealername.text = DealerName
        holder.tv_fpscode.text = Fpscode
        holder.tv_trasdate.text = TranDateStr
        holder.tv_dealermobno.text = DealerMobileNo
        holder.tv_ticketno.text = TicketNo
        holder.tv_weightscaleno.text = WeighingScaleSerialNo

        /* holder.tv_view.setOnClickListener(view -> {
            Intent intent = new Intent(context, ViewDetail.class);
           intent.putExtra("param", model);
            context.startActivity(intent);

        });
*/
        holder.downlode_chlan.setOnClickListener {
            val vibrator =
                context!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            // Check if the device supports vibration
            if (vibrator.hasVibrator()) {
                // Vibrate for 500 milliseconds (0.5 seconds)
                vibrator.vibrate(100)

                val animation = AnimationUtils.loadAnimation(
                    context,
                    R.anim.scale_anim
                )

                // Apply the animation to the ImageView
                holder.downlode_chlan.startAnimation(animation)




                openPdfchallanap(holder, model)
            }
        }

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
        holder.tv_installed.setOnClickListener {
            val intent = Intent(context, NewDelivery::class.java)
            intent.putExtra("param", model)
            try {
                activity!!.startActivityForResult(
                    intent,
                    444
                ) // Use startActivityForResult to get the selected file's URI
            } catch (e: ActivityNotFoundException) {
                // Handle the case where no app capable of handling this intent is installed
            }
        }
        holder.tv_verify.setOnClickListener {
            val intent = Intent(context, ViewDetail::class.java)
            intent.putExtra("param", model)
            intent.putExtra("isFrom", "Verify")
            try {
                activity!!.startActivityForResult(
                    intent,
                    444
                ) // Use startActivityForResult to get the selected file's URI
            } catch (e: ActivityNotFoundException) {
                // Handle the case where no app capable of handling this intent is installed
            }
        }

        holder.tv_view.setOnClickListener {
            val intent = Intent(context, ViewDetail::class.java)
            intent.putExtra("param", model)
            intent.putExtra("isFrom", "View")
            try {
                activity!!.startActivityForResult(
                    intent,
                    444
                ) // Use startActivityForResult to get the selected file's URI
            } catch (e: ActivityNotFoundException) {
                // Handle the case where no app capable of handling this intent is installed
            }
        }
    }

    // total number of rows
    override fun getItemCount(): Int {
        return weighInsDataArrayList.size
    }

    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var tv_ticketno: TextView = itemView.findViewById(R.id.tv_ticketno)
        var tv_fpscode: TextView = itemView.findViewById(R.id.tv_fpscode)
        var tv_dealername: TextView = itemView.findViewById(R.id.tv_dealername)
        var tv_dealermobno: TextView = itemView.findViewById(R.id.tv_dealermobno)
        var tv_weightscaleno: TextView = itemView.findViewById(R.id.tv_weightscaleno)
        var tv_trasdate: TextView = itemView.findViewById(R.id.tv_trasdate)
        var tv_view: AppCompatTextView = itemView.findViewById(R.id.tv_view)
        var tv_installed: AppCompatTextView =
            itemView.findViewById(R.id.tv_installed)
        var tv_verify: AppCompatTextView = itemView.findViewById(R.id.tv_verify)
        var rlItem: LinearLayout = itemView.findViewById(R.id.rlItem)
        var downlode_chlan: ImageView =
            itemView.findViewById(R.id.downlode_chlan)
        var progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)

        init {
            //  itemView.setOnClickListener(this);
        }

        override fun onClick(view: View) {
        }
    }


    companion object {
        private var context: Context? = null
        private var activity: Activity? = null

        private var filemyurl: String? = null
        private var prefManager: PrefManager? = null

        fun makeToast(string: String?) {
            if (TextUtils.isEmpty(string)) return
            Toast.makeText(context!!.applicationContext, string, Toast.LENGTH_SHORT).show()
        }

        private fun openPdfchallanap(holder: ViewHolder, model: WeighInsData) {
            if (isNetworkAvailable(context!!.applicationContext)) {
                holder.progressBar.visibility = View.VISIBLE
                val service = RetrofitInstance.getRetrofitInstance().create(
                    APIService::class.java
                )

                // Integer ErrorStatusIdd = model.getErrorStatusId();
                prefManager = PrefManager(context!!)

                val userid = prefManager!!.useR_Id
                val FPSCode = model.fpscode.toString()
                val DeviceTypeId = model.deviceTypeId.toString()
                Log.d("response", " $userid $FPSCode $DeviceTypeId")


                val call = service.DownloadAPIChallanPDF(userid, FPSCode, DeviceTypeId, "2", "0")
                call.enqueue(object : Callback<challanRoot?> {
                    @SuppressLint("SetTextI18n")
                    override fun onResponse(
                        call: Call<challanRoot?>,
                        response: Response<challanRoot?>
                    ) {
                        holder.progressBar.visibility = View.GONE

                        if (response.isSuccessful) {
                            try {
                                if (response.code() == 200) {
                                    if (response.body() != null) {
                                        if (response.body()!!.status == "200") {
                                            val challanroot = response.body()
                                            Log.d(
                                                "getErrorTypesRoot..",
                                                "getErrorTypesRoot..$challanroot"
                                            )
                                            val fileUrl = challanroot!!.data.fileUrl
                                            val endName = challanroot.data.fileName
                                            filemyurl = API_BASE_URL + fileUrl + endName
                                            //  filemyurl = "https//gggg.pdf";
                                            val timeStamp = SimpleDateFormat(
                                                "yyyyMMdd_HHmmss",
                                                Locale.getDefault()
                                            ).format(
                                                Date()
                                            )


                                            //     File outputFile = new File("/storage/emulated/0/Download/" + timeStamp+".pdf");


                                            //  String fileUrl = "https://cdn1.byjus.com/wp-content/uploads/2019/10/My-School-Essay-for-class-1.pdf";
                                            val fileName = "$timeStamp.pdf"

                                            DownloadHelper.downloadFile(
                                                context!!,
                                                filemyurl,
                                                fileName
                                            )

                                            // Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                            Toast.makeText(
                                                context,
                                                "Downloaded",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            // context.startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
                                            val intent = Intent(Intent.ACTION_GET_CONTENT)
                                            intent.addCategory(Intent.CATEGORY_OPENABLE)
                                            intent.setType("application/pdf") // Filter for PDF files


                                            /* try {
                                          context.startActivity(intent ); // Use startActivityForResult to get the selected file's URI
                                        } catch (ActivityNotFoundException e) {
                                            // Handle the case where no app capable of handling this intent is installed
                                        }*/
                                            try {
                                                activity!!.startActivityForResult(
                                                    intent,
                                                    1
                                                ) // Use startActivityForResult to get the selected file's URI
                                            } catch (e: ActivityNotFoundException) {
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
                                            Toast.makeText(
                                                context,
                                                response.body()!!.message,
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            //   makeToast(context.getResources().getString(R.string.error_message));
                                        }
                                    } else {
                                        Toast.makeText(
                                            context,
                                            response.message(),
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        // makeToast(context.getResources().getString(R.string.error_message));
                                    }
                                } else {
                                    val msg = "HTTP Error: " + response.code()
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

                                    //  makeToast(context.getResources().getString(R.string.error_message));
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                val msg = "HTTP Error: " + response.code()
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            val msg = "HTTP Error: " + response.code()
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<challanRoot?>, t: Throwable) {
                        //  hideProgress();
                        holder.progressBar.visibility = View.GONE

                        makeToast(context!!.resources.getString(R.string.error_message))

                        //      binding.textNoDataFound.setVisibility(View.VISIBLE);
                    }
                })
            } else {
                makeToast(context!!.getString(R.string.no_internet_connection))
            }
        } /*   public static void openPdfFile(Context context, String fileName) {

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
}





