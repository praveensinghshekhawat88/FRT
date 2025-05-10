package com.callmangement.firebase

import com.google.firebase.messaging.FirebaseMessaging


object FirebaseUtils {
    fun registerTopic(topic: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
    }

    fun unregisterTopic(topic: String) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
    }
}
