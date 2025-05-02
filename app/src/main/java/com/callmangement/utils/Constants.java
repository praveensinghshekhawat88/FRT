package com.callmangement.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.callmangement.model.fps_repeat_on_service_center.ModelRepeatFpsComplaintsList;
import com.callmangement.model.inventrory.ModelAddStock;
import com.callmangement.model.inventrory.ModelDisputePartsList;
import com.callmangement.model.inventrory.ModelPartsDispatchInvoiceList;
import com.callmangement.model.inventrory.ModelPartsList;
import com.callmangement.model.reports.MonthReportModel;
import com.callmangement.ui.distributor.model.PosDistributionDetail;
import com.callmangement.ui.iris_derivery_installation.Model.IrisInstallationPendingListResp;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Constants {
    public static final String PACKAGE_NAME = "com.callmangement";


    // ******************************  CHANGE BASE_URL IN native-lib.cpp FILE ALSO *********************************

    //public static final String API_BASE_URL = "https://rajepds.com/CallManagement/"; // Staging
//       public static final String API_BASE_URL = "http://198.38.88.124/CallManagement/"; // Staging
    public static final String API_BASE_URL = "https://rajepds.com/"; // Production

    // ******************************  CHANGE BASE_URL IN native-lib.cpp FILE ALSO *********************************

    public static final int SUCCESS_RESULT = 1;
    public static final String ADDRESS = PACKAGE_NAME + ".ADDRESS";
    public static final String LOCAITY = PACKAGE_NAME + ".LOCAITY";
    public static final String STATE = PACKAGE_NAME + ".STATE";
    public static final String DISTRICT = PACKAGE_NAME + ".DISTRICT";
    public static final String COUNTRY = PACKAGE_NAME + ".COUNTRY";
    public static final String POST_CODE = PACKAGE_NAME + ".POST_CODE";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String RECEVIER = PACKAGE_NAME + ".RECEVIER";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";
    static final int FAILURE_RESULT = 0;
    public static String saveImagePath = "ePDSFrt/Images";
    public static String fixedTime = "10:00:00";
    public static String currentLat = "";
    public static String currentLong = "";
    public static String currentTime = "";
    public static String fromWhere = "fromWhere";
    public static List<ModelPartsList> modelProductLists = null;
    public static List<MonthReportModel> listMonthReport = null;
    public static List<ModelPartsList> modelPartsList = null;
    public static List<ModelPartsDispatchInvoiceList> modelPartsDispatchInvoiceList = null;
    public static List<ModelDisputePartsList> modelDisputePartsList = null;
    public static List<ModelAddStock> modelAddStock = null;
    public static List<PosDistributionDetail> posDistributionDetailsList = null;
    public static List<ModelRepeatFpsComplaintsList> modelRepeatFpsComplaintsList = null;
    public static String firebase_notification_key = "AAAA0UG0bdQ:APA91bGTvqYSQaB7-wNMf5kE_SXGRIIve3GdlrC9UNTWwbgIfwjOO_-k5_hEOJpli9npSH0CxIG2CoYbhINbiYxuTqmJb0t_4rV5IQfg06m2ZHoRboQ5zFcYsp4Ipch8k4VxKFfA7BAn";
    /*for bikaner*/
    public static List<String> tehsilIdListFor87 = Arrays.asList("219", "222", "226", "225");
    public static List<String> tehsilNameListFor87 = Arrays.asList("कोलायत", "नोखा", "श्री डूंगरगढ", "लूनकसरण");
    public static List<String> tehsilIdListFor12 = Arrays.asList("220", "221", "223", "224");
    public static List<String> tehsilNameListFor12 = Arrays.asList("खाजूवाला", "छतरगढ", "पुगल", "बीकानेर");
    /*for bikaner*/

    /*for nagaur*/
    public static List<String> tehsilIdListFor29 = Arrays.asList("160", "163", "164", "165", "166");
    public static List<String> tehsilNameListFor29 = Arrays.asList("डेगाना", "नावाँ", "परबतसर", "मकराना", "मेड़ता");
    public static List<String> tehsilIdListFor88 = Arrays.asList("158", "159", "161", "162", "167");
    public static List<String> tehsilNameListFor88 = Arrays.asList("खींवसर", "जायल", "डीडवाना", "नागौर", "लाडनू");
    /*for nagaur*/

    /*for udaipur*/
    public static List<String> tehsilIdListFor37 = Arrays.asList("29", "30", "31", "32", "33", "34", "35");
    public static List<String> tehsilNameListFor37 = Arrays.asList("कोटड़ा", "खेरवाड़ा", "गिर्वा", "गोगुन्दा", "झाड़ोल", "बडगांव", "मावली");
    public static List<String> tehsilIdListFor89 = Arrays.asList("36", "37", "38", "39", "40", "41");
    public static List<String> tehsilNameListFor89 = Arrays.asList("रिषभदेव", "लसाड़िया", "वल्लभनगर", "सेमारी", "सराड़ा", "सलुम्बर");
    /*for udaipur*/

    /*for jaipur*/
    public static List<String> tehsilIdListFor90 = Collections.singletonList("82");
    public static List<String> tehsilNameListFor90 = Collections.singletonList("चौमू");
    public static List<String> tehsilIdListFor21 = Arrays.asList("79", "80", "81", "83", "84", "85", "86", "87", "88", "89", "90", "91");
    public static List<String> tehsilNameListFor21 = Arrays.asList("आमेर", "कोटपुतली", "चाकसू", "जमवारामगढ़", "जयपुर", "फुलेरा(सांभर", "फागी", "बस्सी", "मौजमाबाद", "विराटनगर", "शाहपुरा", "सांगानेर");
    /*for jaipur*/

    public static ArrayList<IrisInstallationPendingListResp.Datum> irisInsPendingArrayList = new ArrayList<>();

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context mContext) {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String convertStringToUTF8(String s) {
        String out = null;
        out = new String(s.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        return out;
    }
}

