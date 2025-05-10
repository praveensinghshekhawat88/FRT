package com.callmangement.firebase

import com.callmangement.ui.home.MainActivity.Companion.newIntent
import com.callmangement.utils.MyApp.Companion.context

object FirebaseMessageController : FirebaseBaseController() {
    private const val TAG = "MyFirebaseMessagingServ"
    fun parseParam(params: Map<String?, String?>): Boolean {
        val title = params["title"]
        val message = params["message"]
        val intent = newIntent(context, title)
        FirebaseBaseController.Companion.showNotification(
            context,
            FirebaseBaseController.Companion.toNewTask(intent),
            title,
            message
        )
        return true
    } //    public static boolean parseParam(JSONObject json) {
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
