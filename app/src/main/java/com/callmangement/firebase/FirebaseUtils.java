package com.callmangement.firebase;


import com.google.firebase.messaging.FirebaseMessaging;

public class FirebaseUtils {
    public static void registerTopic(String topic){
        FirebaseMessaging.getInstance().subscribeToTopic(topic);
    }

    public static void unregisterTopic(String topic){
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
    }
}
