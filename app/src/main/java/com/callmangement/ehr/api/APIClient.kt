package com.callmangement.ehr.api

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object APIClient {

    fun getRetrofitClientWithoutHeaders(context: Context, BASE_URL: String): Retrofit {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val headerInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val request = originalRequest.newBuilder()
                .header("Content-Type", "application/json; charset=utf-8")
                .build()
            chain.proceed(request)
        }

        val httpClient = OkHttpClient.Builder()
            .readTimeout(Constants.CONNECTION_TIME_OUT?.toLong()!!, TimeUnit.SECONDS)
            .connectTimeout(Constants.CONNECTION_TIME_OUT?.toLong()!!, TimeUnit.SECONDS)
            .addInterceptor(headerInterceptor)
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
    }
}