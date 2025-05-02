package com.callmangement.network

import android.text.TextUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import okhttp3.RequestBody
import java.io.File

/**
 * Created by King Jocoa on 9/23/2016.
 */
object MultipartRequester {
    @JvmStatic
    fun fromString(data: String?): RequestBody {
        return RequestBody.create("multipart/form-data".toMediaTypeOrNull(), data!!)
    }

    @JvmStatic
    fun fromFile(key: String?, data: String): MultipartBody.Part? {
        if (TextUtils.isEmpty(data)) return null
        if (data.contains("http:") || data.contains("https:")) return null
        val file = File(data)
        val fileName = file.name
        val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        return createFormData(key!!, fileName, requestFile)
    }
}
