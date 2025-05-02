package com.callmangement.network

import com.callmangement.BuildConfig

object AppConfig {
    private const val DEBUG = true

    // Versions
    const val PACKAGE_NAME: String = BuildConfig.APPLICATION_ID
    const val VERSION_NAME: String = BuildConfig.VERSION_NAME
    const val VERSION_CODE: Int = BuildConfig.VERSION_CODE

    const val NOTIFICATION_ID: Int = 100
    const val NOTIFICATION_ID_BIG_IMAGE: Int = 101

    const val api_key: String = ""
}
