package com.callmangement.utils

import android.app.Application
import android.content.Context
import com.callmangement.firebase.FirebaseUtils

class MyApp : Application() {
    init {
        mInstance = this
    }

    override fun onCreate() {
        FirebaseUtils.registerTopic("all")
        super.onCreate()
    }

    companion object {
        private const val TAG = "MyApp"
        private lateinit var mInstance: MyApp

        @JvmStatic
        val context: Context
            get() = mInstance
    }
}
