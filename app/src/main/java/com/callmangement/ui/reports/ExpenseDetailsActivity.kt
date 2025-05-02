package com.callmangement.ui.reports

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.callmangement.Network.APIService
import com.callmangement.Network.RetrofitInstance
import com.callmangement.R
import com.callmangement.custom.CustomActivity
import com.callmangement.databinding.ActivityExpenseDetailsBinding
import com.callmangement.model.expense.ModelExpensesList
import com.callmangement.ui.home.ZoomInZoomOutActivity
import com.callmangement.utils.Constants
import com.callmangement.utils.PrefManager
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExpenseDetailsActivity : CustomActivity(), View.OnClickListener {
    private var binding: ActivityExpenseDetailsBinding? = null
    private var model: ModelExpensesList? = null
    private var prefManager: PrefManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseDetailsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        initView()
    }

    private fun initView() {
        prefManager = PrefManager(mContext)
        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text = resources.getString(R.string.expense_detail)
        setUpOnClickListener()
        intentData
        setUpData()
    }

    private fun setUpOnClickListener() {
        binding!!.actionBar.ivBack.setOnClickListener(this)
        binding!!.ivChallanImage.setOnClickListener(this)
        binding!!.buttonComplete.setOnClickListener(this)
    }

    private val intentData: Unit
        get() {
            model = intent.getSerializableExtra("param") as ModelExpensesList?
        }

    private fun setUpData() {
        binding!!.inputName.setText(model!!.seName)
        binding!!.inputDistrict.setText(model!!.district)

        binding!!.inputDocketno.setText(model!!.docketNo)
        binding!!.inputcouriername.setText(model!!.courierName)

        binding!!.inputRemark.setText(model!!.remark)
        binding!!.inputRemark.setText(model!!.remark)
        binding!!.inputTotalExpenseAmount.setText(model!!.totalExAmount.toString())
        binding!!.inputExpenseDate.setText(model!!.createdOnStr)

        if (prefManager!!.useR_TYPE_ID == "1" && prefManager!!.useR_TYPE.equals(
                "Admin",
                ignoreCase = true
            )
        ) {
            binding!!.buttonComplete.visibility = View.VISIBLE
        } else if (prefManager!!.useR_TYPE_ID == "2" && prefManager!!.useR_TYPE.equals(
                "Manager",
                ignoreCase = true
            )
        ) {
            binding!!.buttonComplete.visibility = View.VISIBLE
        } else {
            binding!!.buttonComplete.visibility = View.GONE
        }

        if (model!!.completedOnStr.isEmpty()) {
            binding!!.expenseCompletedDateLay.visibility = View.GONE
        } else {
            binding!!.expenseCompletedDateLay.visibility = View.VISIBLE
            binding!!.inputExpenseCompletedDate.setText(model!!.completedOnStr)
        }

        if (model!!.expenseStatusID == 2) {
            binding!!.buttonComplete.visibility = View.GONE
        }

        Glide.with(mContext)
            .load(Constants.API_BASE_URL + model!!.filePath)
            .placeholder(R.drawable.image_not_fount)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(binding!!.ivChallanImage)
    }

    private fun completeExpense() {
        if (Constants.isNetworkAvailable(mContext)) {
            showProgress()
            val service = RetrofitInstance.getRetrofitInstance().create(
                APIService::class.java
            )
            val call = service.completeExpenses(prefManager!!.useR_Id, model!!.expenseId.toString())
            call.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    hideProgress()
                    try {
                        if (response.isSuccessful) {
                            if (response.code() == 200) {
                                if (response.body() != null) {
                                    val responseStr = response.body()!!.string()
                                    val jsonObject = JSONObject(responseStr)
                                    val status = jsonObject.optString("status")
                                    val message = jsonObject.optString("message")
                                    if (status == "200") {
                                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show()
                                        onBackPressed()
                                    } else {
                                        makeToast(message)
                                    }
                                }
                            }
                        } else {
                            makeToast(resources.getString(R.string.error))
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    hideProgress()
                    makeToast(resources.getString(R.string.error_message))
                }
            })
        } else makeToast(resources.getString(R.string.no_internet_connection))
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.iv_back) {
            onBackPressed()
        } else if (id == R.id.ivChallanImage) {
            startActivity(
                Intent(mContext, ZoomInZoomOutActivity::class.java).putExtra(
                    "image",
                    Constants.API_BASE_URL + model!!.filePath
                )
            )
        } else if (id == R.id.buttonComplete) {
            val builder = AlertDialog.Builder(mContext)
            builder.setMessage(resources.getString(R.string.complete_expense_dialog_msg))
                .setCancelable(false)
                .setPositiveButton(resources.getString(R.string.ok)) { dialog: DialogInterface, ids: Int ->
                    dialog.cancel()
                    completeExpense()
                }
                .setNegativeButton(
                    resources.getString(R.string.cancel)
                ) { dialog: DialogInterface, ids: Int -> dialog.cancel() }
            val alert = builder.create()
            alert.setTitle(resources.getString(R.string.alert))
            alert.show()
        }
    }
}