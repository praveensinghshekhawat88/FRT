package com.callmangement.ehr.ehrActivities

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import com.callmangement.R
import com.callmangement.databinding.ActivitySurveyFormListingBinding
import com.callmangement.ehr.adapter.SurveyFormListingAdapter
import com.callmangement.ehr.adapter.SurveyFormListingAdapter.OnItemDeleteClickListener
import com.callmangement.ehr.adapter.SurveyFormListingAdapter.OnItemDownloadClickListener
import com.callmangement.ehr.adapter.SurveyFormListingAdapter.OnItemEditClickListener
import com.callmangement.ehr.adapter.SurveyFormListingAdapter.OnItemUploadClickListener
import com.callmangement.ehr.api.APIClient.getRetrofitClientWithoutHeaders
import com.callmangement.ehr.api.APIInterface
import com.callmangement.ehr.models.ModelDeleteASurveyForm
import com.callmangement.ehr.models.ModelGetSurveyFormDocList
import com.callmangement.ehr.models.ModelSurveyFormListing
import com.callmangement.ehr.models.SurveyFormDetailsInfo
import com.callmangement.ehr.models.SurveyFormDocInfo
import com.callmangement.ehr.support.EqualSpacingItemDecoration
import com.callmangement.ehr.support.Utils.hideCustomProgressDialogCommonForAll
import com.callmangement.ehr.support.Utils.hideKeyboard
import com.callmangement.ehr.support.Utils.isNetworkAvailable
import com.callmangement.ehr.support.Utils.showCustomProgressDialogCommonForAll
import com.callmangement.utils.PrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SurveyFormsListingActivity : BaseActivity() {
    private var mActivity: Activity? = null
    private var binding: ActivitySurveyFormListingBinding? = null
    private var preference: PrefManager? = null
    private var campDetails_infoArrayList: List<SurveyFormDetailsInfo> = ArrayList()
    private var adapter: SurveyFormListingAdapter? = null
    val LAUNCH_SURVEY_FORM_FILLING_ACTIVITY: Int = 555

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivitySurveyFormListingBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        init()
    }

    private fun init() {
        mActivity = this
        preference = PrefManager(mActivity!!)

        binding!!.actionBar.ivBack.visibility = View.VISIBLE
        binding!!.actionBar.ivThreeDot.visibility = View.GONE
        binding!!.actionBar.layoutLanguage.visibility = View.GONE
        binding!!.actionBar.buttonPDF.visibility = View.GONE
        binding!!.actionBar.textToolbarTitle.text =
            resources.getString(R.string.manage_survey_forms)

        campDetails_infoArrayList = ArrayList()
        adapter = SurveyFormListingAdapter(
            mActivity!!,
            campDetails_infoArrayList,
            onItemUploadClickListener,
            onItemDownloadClickListener,
            onItemViewClickListener,
            onItemDeleteClickListener,
            onItemEditClickListener
        )
        binding!!.rvAllSurveyForms.setHasFixedSize(true)
        binding!!.rvAllSurveyForms.layoutManager =
            LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        binding!!.rvAllSurveyForms.addItemDecoration(
            EqualSpacingItemDecoration(
                30,
                EqualSpacingItemDecoration.VERTICAL
            )
        )
        binding!!.rvAllSurveyForms.adapter = adapter

        setClickListener()

        getSurveyForms("0", "", "", "", "0")
    }


    var onItemViewClickListener: SurveyFormListingAdapter.OnItemViewClickListener =
        object : SurveyFormListingAdapter.OnItemViewClickListener {
            override fun onItemClick(surveyFormDetailsInfo: SurveyFormDetailsInfo?, position: Int) {
                val USER_Id = preference!!.useR_Id
                viewUploadedImages(USER_Id, surveyFormDetailsInfo!!)
            }
        }

    private fun viewUploadedImages(UserID: String, surveyFormDetailsInfo: SurveyFormDetailsInfo) {
        if (isNetworkAvailable(mActivity!!)) {
            hideKeyboard(mActivity!!)
            showCustomProgressDialogCommonForAll(
                mActivity!!,
                resources.getString(R.string.please_wait)
            )

            val apiInterface = getRetrofitClientWithoutHeaders(
                mActivity!!,
                BaseUrl()!!
            ).create(
                APIInterface::class.java
            )

            val paramStr = GetSurveyFormDocListQueryParams()
            val splitArray =
                paramStr!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val paramData: MutableMap<String?, String?> = HashMap()
            paramData[splitArray[0]] = UserID
            paramData[splitArray[1]] = surveyFormDetailsInfo.serveyFormId

            val call = apiInterface.callGetSurveyFormDocListApi(GetSurveyFormDocList(), paramData)
            call!!.enqueue(object : Callback<ModelGetSurveyFormDocList?> {
                override fun onResponse(
                    call: Call<ModelGetSurveyFormDocList?>,
                    response: Response<ModelGetSurveyFormDocList?>
                ) {
                    hideCustomProgressDialogCommonForAll()

                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (response.body()!!.status == "200") {
                                    val campDocInfoArrayList = ArrayList<SurveyFormDocInfo>()

                                    if (response.body()!!.list != null && response.body()!!.list.size > 0) {
                                        for (i in response.body()!!.list.indices) {
                                            val surveyFormDocInfo = response.body()!!.list[i]

                                            val imagePath =
                                                BaseUrl() + surveyFormDocInfo.documentPath
                                            surveyFormDocInfo.documentPath = imagePath

                                            campDocInfoArrayList.add(surveyFormDocInfo)
                                        }

                                        val intent = Intent(
                                            mActivity,
                                            ViewSurveyFormDetailsActivity::class.java
                                        )
                                        val bundle = Bundle()
                                        bundle.putSerializable("mylist", campDocInfoArrayList)
                                        bundle.putSerializable(
                                            "surveyFormDetails",
                                            surveyFormDetailsInfo
                                        )
                                        intent.putExtras(bundle)
                                        startActivity(intent)
                                    } else {
                                        val intent = Intent(
                                            mActivity,
                                            ViewSurveyFormDetailsActivity::class.java
                                        )
                                        val bundle = Bundle()
                                        bundle.putSerializable("mylist", campDocInfoArrayList)
                                        bundle.putSerializable(
                                            "surveyFormDetails",
                                            surveyFormDetailsInfo
                                        )
                                        intent.putExtras(bundle)
                                        startActivity(intent)
                                    }
                                } else {
                                    makeToast(response.body()!!.message)
                                }
                            } else {
                                makeToast(resources.getString(R.string.error))
                            }
                        } else {
                            makeToast(resources.getString(R.string.error))
                        }
                    } else {
                        makeToast(resources.getString(R.string.error))
                    }
                }

                override fun onFailure(call: Call<ModelGetSurveyFormDocList?>, error: Throwable) {
                    hideCustomProgressDialogCommonForAll()

                    makeToast(resources.getString(R.string.error))

                    call.cancel()
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }

    var onItemEditClickListener: OnItemEditClickListener = object : OnItemEditClickListener {
        override fun onItemClick(surveyFormDetailsInfo: SurveyFormDetailsInfo?, position: Int) {
            val intent = Intent(mActivity, EditSurveyFormActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("surveyFormDetails", surveyFormDetailsInfo)
            intent.putExtras(bundle)
            startActivityForResult(intent, LAUNCH_SURVEY_FORM_FILLING_ACTIVITY)
        }
    }

    var onItemDeleteClickListener: OnItemDeleteClickListener = object : OnItemDeleteClickListener {
        override fun onItemClick(surveyFormDetailsInfo: SurveyFormDetailsInfo?, position: Int) {
            val builder = AlertDialog.Builder(
                mActivity!!
            )
            builder.setMessage(resources.getString(R.string.delete_survey_form_now))
                .setCancelable(false)
                .setPositiveButton(resources.getString(R.string.yes)) { dialog: DialogInterface, id: Int ->
                    dialog.cancel()
                    val USER_Id = preference!!.useR_Id
                    deleteASurveyForm(
                        USER_Id,
                        surveyFormDetailsInfo!!.serveyFormId,
                        surveyFormDetailsInfo.ticketNo
                    )
                }
                .setNegativeButton(
                    resources.getString(R.string.cancel)
                ) { dialog: DialogInterface, i: Int ->
                    dialog.cancel()
                }
            val alert = builder.create()
            alert.setTitle(resources.getString(R.string.alert))
            alert.show()
        }
    }

    private fun deleteASurveyForm(UserID: String, ServeyFormId: String?, TicketNo: String?) {
        if (isNetworkAvailable(mActivity!!)) {
            hideKeyboard(mActivity!!)
            showCustomProgressDialogCommonForAll(
                mActivity!!,
                resources.getString(R.string.please_wait)
            )

            val apiInterface = getRetrofitClientWithoutHeaders(
                mActivity!!,
                BaseUrl()!!
            ).create(
                APIInterface::class.java
            )

            val paramStr = DeleteASurveyFormQueryParams()
            val splitArray =
                paramStr!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val paramData: MutableMap<String?, String?> = HashMap()
            paramData[splitArray[0]] = UserID
            paramData[splitArray[1]] = ServeyFormId
            paramData[splitArray[2]] = TicketNo

            val call = apiInterface.callDeleteASurveyFormApi(DeleteASurveyForm(), paramData)
            call!!.enqueue(object : Callback<ModelDeleteASurveyForm?> {
                override fun onResponse(
                    call: Call<ModelDeleteASurveyForm?>,
                    response: Response<ModelDeleteASurveyForm?>
                ) {
                    hideCustomProgressDialogCommonForAll()

                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (response.body()!!.status == "200") {
                                    makeToast(response.body()!!.message)

                                    campDetails_infoArrayList = ArrayList()
                                    adapter!!.setData(campDetails_infoArrayList)

                                    getSurveyForms("0", "", "", "", "0")
                                } else {
                                    makeToast(response.body()!!.message)
                                }
                            } else {
                                makeToast(resources.getString(R.string.error))
                            }
                        } else {
                            makeToast(resources.getString(R.string.error))
                        }
                    } else {
                        makeToast(resources.getString(R.string.error))
                    }
                }

                override fun onFailure(call: Call<ModelDeleteASurveyForm?>, error: Throwable) {
                    hideCustomProgressDialogCommonForAll()

                    makeToast(resources.getString(R.string.error))

                    call.cancel()
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }


    var onItemUploadClickListener: OnItemUploadClickListener = object : OnItemUploadClickListener {
        override fun onItemClick(surveyFormDetailsInfo: SurveyFormDetailsInfo?, position: Int) {
            val builder = AlertDialog.Builder(
                mActivity!!
            )
            builder.setMessage(resources.getString(R.string.upload_survey_form_sheet_for_survey_form_now))
                .setCancelable(false)
                .setPositiveButton(resources.getString(R.string.yes)) { dialog: DialogInterface, id: Int ->
                    dialog.cancel()
                    val intent = Intent(mActivity, UploadSurveyFormReportActivity::class.java)
                    val bundle = Bundle()
                    bundle.putString("ServeyFormId", surveyFormDetailsInfo!!.serveyFormId)
                    bundle.putString("TicketNo", surveyFormDetailsInfo.ticketNo)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
                .setNegativeButton(
                    resources.getString(R.string.cancel)
                ) { dialog: DialogInterface, i: Int ->
                    dialog.cancel()
                }
            val alert = builder.create()
            alert.setTitle(resources.getString(R.string.alert))
            alert.show()
        }
    }

    var onItemDownloadClickListener: OnItemDownloadClickListener =
        object : OnItemDownloadClickListener {
            override fun onItemClick(surveyFormDetailsInfo: SurveyFormDetailsInfo?, position: Int) {
                val builder = AlertDialog.Builder(
                    mActivity!!
                )
                builder.setMessage(resources.getString(R.string.download_survey_form_now))
                    .setCancelable(false)
                    .setPositiveButton(resources.getString(R.string.yes)) { dialog: DialogInterface, id: Int ->
                        dialog.cancel()
                        val intent = Intent(mActivity, SurveyFormPdfActivity::class.java)
                        val bundle = Bundle()
                        bundle.putSerializable("surveyFormDetailsInfo", surveyFormDetailsInfo)
                        intent.putExtras(bundle)
                        startActivity(intent)
                    }
                    .setNegativeButton(
                        resources.getString(R.string.cancel)
                    ) { dialog: DialogInterface, i: Int ->
                        dialog.cancel()
                    }
                val alert = builder.create()
                alert.setTitle(resources.getString(R.string.alert))
                alert.show()
            }
        }

    private fun getSurveyForms(
        ServeyFormId: String,
        TicketNo: String,
        StartDate: String,
        EndDate: String,
        StatusId: String
    ) {
        if (isNetworkAvailable(mActivity!!)) {
            hideKeyboard(mActivity!!)
            showCustomProgressDialogCommonForAll(
                mActivity!!,
                resources.getString(R.string.please_wait)
            )

            val apiInterface = getRetrofitClientWithoutHeaders(
                mActivity!!,
                BaseUrl()!!
            ).create(
                APIInterface::class.java
            )

            val USER_Id = preference!!.useR_Id

            val paramStr = GetSurveyFormListQueryParams()
            val splitArray =
                paramStr!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val paramData: MutableMap<String?, String?> = HashMap()
            paramData[splitArray[0]] = USER_Id
            paramData[splitArray[1]] = ServeyFormId
            paramData[splitArray[2]] = TicketNo
            paramData[splitArray[3]] = StartDate
            paramData[splitArray[4]] = EndDate
            paramData[splitArray[5]] = StatusId

            val call = apiInterface.callSurveyFormListApi(GetSurveyFormList(), paramData)
            call!!.enqueue(object : Callback<ModelSurveyFormListing?> {
                override fun onResponse(
                    call: Call<ModelSurveyFormListing?>,
                    response: Response<ModelSurveyFormListing?>
                ) {
                    hideCustomProgressDialogCommonForAll()

                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                if (response.body()!!.status == "200") {
                                    if (response.body()!!.list.size > 0) {
                                        binding!!.textNoCampSchedule.visibility = View.GONE
                                        binding!!.rvAllSurveyForms.visibility = View.VISIBLE
                                        adapter!!.setData(response.body()!!.list)
                                    } else {
                                        binding!!.textNoCampSchedule.visibility = View.VISIBLE
                                        binding!!.rvAllSurveyForms.visibility = View.GONE
                                    }
                                } else {
                                    makeToast(response.body()!!.message)
                                }
                            } else {
                                makeToast(resources.getString(R.string.error))
                            }
                        } else {
                            makeToast(resources.getString(R.string.error))
                        }
                    } else {
                        makeToast(resources.getString(R.string.error))
                    }
                }

                override fun onFailure(call: Call<ModelSurveyFormListing?>, error: Throwable) {
                    hideCustomProgressDialogCommonForAll()

                    makeToast(resources.getString(R.string.error))

                    call.cancel()
                }
            })
        } else {
            makeToast(resources.getString(R.string.no_internet_connection))
        }
    }

    fun makeToast(string: String?) {
        if (TextUtils.isEmpty(string)) return
        Toast.makeText(mActivity, string, Toast.LENGTH_SHORT).show()
    }

    private fun setClickListener() {
        binding!!.actionBar.ivBack.setOnClickListener { view: View? -> onBackPressed() }

        binding!!.actionBar.ivThreeDot.setOnClickListener { view: View? ->
            val popupMenu =
                PopupMenu(
                    this@SurveyFormsListingActivity,
                    binding!!.actionBar.ivThreeDot
                )
            popupMenu.menuInflater.inflate(R.menu.menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem: MenuItem? ->
                val builder = AlertDialog.Builder(
                    mActivity!!
                )
                builder.setMessage(resources.getString(R.string.do_you_want_to_logout_from_this_app))
                    .setCancelable(false)
                    .setPositiveButton(
                        resources.getString(R.string.logout)
                    ) { dialog: DialogInterface, id: Int ->
                        dialog.cancel()
                    }
                    .setNegativeButton(
                        resources.getString(R.string.cancel)
                    ) { dialog: DialogInterface, id: Int -> dialog.cancel() }
                val alert = builder.create()
                alert.setTitle(resources.getString(R.string.alert))
                alert.show()
                true
            }
            popupMenu.show()
        }

        binding!!.buttonFillNewSurveyForm.setOnClickListener { view: View? ->
            val intent = Intent(mActivity, FillNewSurveyFormActivity::class.java)
            startActivityForResult(intent, LAUNCH_SURVEY_FORM_FILLING_ACTIVITY)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LAUNCH_SURVEY_FORM_FILLING_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                campDetails_infoArrayList = ArrayList()
                adapter!!.setData(campDetails_infoArrayList)

                getSurveyForms("0", "", "", "", "0")
            }
        }
    }
}