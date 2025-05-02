package com.callmangement.utils

import android.app.Application
import android.content.Context
import com.callmangement.firebase.FirebaseUtils.registerTopic

class MyApp : Application() {
    init {
        mInstance = this
    }

    override fun onCreate() {
        registerTopic("all")
        super.onCreate()
    }

    companion object {
        private const val TAG = "MyApp"
        private lateinit var mInstance: MyApp

        val context: Context
            get() = mInstance
    }
}
