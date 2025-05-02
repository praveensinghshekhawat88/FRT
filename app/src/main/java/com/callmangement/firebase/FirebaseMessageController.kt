package com.callmangement.firebase;

import android.content.Intent;

import com.callmangement.ui.home.MainActivity;
import com.callmangement.utils.MyApp;
import com.callmangement.utils.PrefManager;
import com.google.android.gms.location.LocationRequest;

import java.util.Map;

public class FirebaseMessageController extends FirebaseBaseController {
    private static final String TAG = "MyFirebaseMessagingServ";
    public static boolean parseParam(Map<String, String> params) {
        String title = params.get("title");
        String message = params.get("message");
        Intent intent = MainActivity.newIntent(MyApp.getContext(), title);
        showNotification(MyApp.getContext(), toNewTask(intent), title, message);
        return true;
    }

//    public static boolean parseParam(JSONObject json) {
//        try {
//            Log.e("kkk", String.valueOf(json.getJSONObject("data")));
//            JSONObject data = json.getJSONObject("data");
//            String type = data.getString("type");
//            String title = data.getString("title");
//            String message = data.getString("message");
//            String image = data.getString("image");
//
//            if (TextUtils.equals(type, "1")) {
//                Intent intent = ExamAnnouncementActivity.newIntent(MyApp.getContext(), String.valueOf(data));
//                showNotification(MyApp.getContext(), toNewTask(intent), title, message);
//                return true;
//
//            }else {
//                Intent intent = NotificationActivity.newIntent(MyApp.getContext(), String.valueOf(data));
//                showNotification(MyApp.getContext(), toNewTask(intent), title, message);
//                return true;
//            }
//
//        } catch (JSONException e) {
//            Log.e("Json Exception: ",e.getMessage());
//        } catch (Exception e) {
//            Log.e("Exception: ",e.getMessage());
//        }
//        return false;
//    }

}
