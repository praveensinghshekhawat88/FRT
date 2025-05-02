package com.callmangement.firebase

import com.google.firebase.messaging.FirebaseMessaging


object FirebaseUtils {
    @JvmStatic
    fun registerTopic(topic: String?) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic!!)
    }

    @JvmStatic
    fun unregisterTopic(topic: String?) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic!!)
    }
}
