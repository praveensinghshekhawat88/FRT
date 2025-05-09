package com.callmangement.utils

import android.app.ActivityManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.callmangement.model.fps_repeat_on_service_center.ModelRepeatFpsComplaintsList
import com.callmangement.model.inventrory.ModelAddStock
import com.callmangement.model.inventrory.ModelDisputePartsList
import com.callmangement.model.inventrory.ModelPartsDispatchInvoiceList
import com.callmangement.model.inventrory.ModelPartsList
import com.callmangement.model.reports.MonthReportModel
import com.callmangement.ui.distributor.model.PosDistributionDetail
import com.callmangement.ui.iris_derivery_installation.Model.IrisInstallationPendingListResp
import java.nio.charset.StandardCharsets

object Constants {
    const val PACKAGE_NAME: String = "com.callmangement"


    // ******************************  CHANGE BASE_URL IN native-lib.cpp FILE ALSO *********************************
    //public static final String API_BASE_URL = "https://rajepds.com/CallManagement/"; // Staging
    const val API_BASE_URL: String = "http://198.38.88.124/CallManagement/"  // Staging
///    const val API_BASE_URL: String = "https://rajepds.com/" // Production

    // ******************************  CHANGE BASE_URL IN native-lib.cpp FILE ALSO *********************************
    const val SUCCESS_RESULT: Int = 1
    const val ADDRESS: String = PACKAGE_NAME + ".ADDRESS"
    const val LOCAITY: String = PACKAGE_NAME + ".LOCAITY"
    const val STATE: String = PACKAGE_NAME + ".STATE"
    const val DISTRICT: String = PACKAGE_NAME + ".DISTRICT"
    const val COUNTRY: String = PACKAGE_NAME + ".COUNTRY"
    const val POST_CODE: String = PACKAGE_NAME + ".POST_CODE"
    const val RESULT_DATA_KEY: String = PACKAGE_NAME + ".RESULT_DATA_KEY"
    const val RECEVIER: String = PACKAGE_NAME + ".RECEVIER"
    const val LOCATION_DATA_EXTRA: String = PACKAGE_NAME + ".LOCATION_DATA_EXTRA"
    const val FAILURE_RESULT: Int = 0

    @JvmField
    var saveImagePath: String = "ePDSFrt/Images"
    var fixedTime: String = "10:00:00"

    @JvmField
    var currentLat: String = ""

    @JvmField
    var currentLong: String = ""

    @JvmField
    var currentTime: String = ""

    @JvmField
    var fromWhere: String = "fromWhere"

    @JvmField
    var modelProductLists: List<ModelPartsList>? = null

    @JvmField
    var listMonthReport: List<MonthReportModel>? = null

    @JvmField
    var modelPartsList: List<ModelPartsList>? = null

    @JvmField
    var modelPartsDispatchInvoiceList: List<ModelPartsDispatchInvoiceList>? = null

    @JvmField
    var modelDisputePartsList: List<ModelDisputePartsList>? = null

    @JvmField
    var modelAddStock: List<ModelAddStock>? = null

    @JvmField
    var posDistributionDetailsList: List<PosDistributionDetail>? = null

    @JvmField
    var modelRepeatFpsComplaintsList: List<ModelRepeatFpsComplaintsList>? = null
    var firebase_notification_key: String =
        "AAAA0UG0bdQ:APA91bGTvqYSQaB7-wNMf5kE_SXGRIIve3GdlrC9UNTWwbgIfwjOO_-k5_hEOJpli9npSH0CxIG2CoYbhINbiYxuTqmJb0t_4rV5IQfg06m2ZHoRboQ5zFcYsp4Ipch8k4VxKFfA7BAn"

    /*for bikaner*/
    var tehsilIdListFor87: List<String> = mutableListOf("219", "222", "226", "225")
    var tehsilNameListFor87: List<String> =
        mutableListOf("कोलायत", "नोखा", "श्री डूंगरगढ", "लूनकसरण")
    var tehsilIdListFor12: List<String> = mutableListOf("220", "221", "223", "224")
    var tehsilNameListFor12: List<String> = mutableListOf("खाजूवाला", "छतरगढ", "पुगल", "बीकानेर")

    /*for bikaner*/ /*for nagaur*/
    var tehsilIdListFor29: List<String> = mutableListOf("160", "163", "164", "165", "166")
    var tehsilNameListFor29: List<String> =
        mutableListOf("डेगाना", "नावाँ", "परबतसर", "मकराना", "मेड़ता")
    var tehsilIdListFor88: List<String> = mutableListOf("158", "159", "161", "162", "167")
    var tehsilNameListFor88: List<String> =
        mutableListOf("खींवसर", "जायल", "डीडवाना", "नागौर", "लाडनू")

    /*for nagaur*/ /*for udaipur*/
    var tehsilIdListFor37: List<String> = mutableListOf("29", "30", "31", "32", "33", "34", "35")
    var tehsilNameListFor37: List<String> =
        mutableListOf("कोटड़ा", "खेरवाड़ा", "गिर्वा", "गोगुन्दा", "झाड़ोल", "बडगांव", "मावली")
    var tehsilIdListFor89: List<String> = mutableListOf("36", "37", "38", "39", "40", "41")
    var tehsilNameListFor89: List<String> =
        mutableListOf("रिषभदेव", "लसाड़िया", "वल्लभनगर", "सेमारी", "सराड़ा", "सलुम्बर")

    /*for udaipur*/ /*for jaipur*/
    var tehsilIdListFor90: List<String> = listOf("82")
    var tehsilNameListFor90: List<String> = listOf("चौमू")
    var tehsilIdListFor21: List<String> =
        mutableListOf("79", "80", "81", "83", "84", "85", "86", "87", "88", "89", "90", "91")
    var tehsilNameListFor21: List<String> = mutableListOf(
        "आमेर",
        "कोटपुतली",
        "चाकसू",
        "जमवारामगढ़",
        "जयपुर",
        "फुलेरा(सांभर",
        "फागी",
        "बस्सी",
        "मौजमाबाद",
        "विराटनगर",
        "शाहपुरा",
        "सांगानेर"
    )

    /*for jaipur*/
    @JvmField
    var irisInsPendingArrayList: ArrayList<IrisInstallationPendingListResp.Datum> = ArrayList()

    fun isMyServiceRunning(serviceClass: Class<*>, mContext: Context): Boolean {
        val manager = mContext!!.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    @JvmStatic
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var activeNetworkInfo: NetworkInfo? = null
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.activeNetworkInfo
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    @JvmStatic
    fun convertStringToUTF8(s: String): String {
        var out: String? = null
        out = String(s.toByteArray(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1)
        return out
    }
}

