package com.callmangement.network

import android.util.Log
import com.callmangement.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient

import okhttp3.Protocol
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

object RestClient {
    const val CONNECTION_TIME_OUT: Int = 240

    /*
    public static OkHttpClient okClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.MINUTES)
                .writeTimeout(3, TimeUnit.MINUTES)
                .readTimeout(3, TimeUnit.MINUTES)
                .hostnameVerifier((hostname, session) -> true)
                .build();
    }
*/
    fun okClient(): OkHttpClient {
//        return new OkHttpClient.Builder()
//                .connectTimeout(3, TimeUnit.MINUTES)
//                .writeTimeout(3, TimeUnit.MINUTES)
//                .readTimeout(3, TimeUnit.MINUTES)
//                .hostnameVerifier((hostname, session) -> true)
//                .build();

        val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()
        val httpLoggingInterceptor =
            HttpLoggingInterceptor { message -> Log.e("RESPONSE", message) }

        httpClient.addInterceptor(Interceptor { chain: Interceptor.Chain ->
            var originalRequest = chain.request()
            // set OAuth token
            val newRequest: Request.Builder = originalRequest.newBuilder()

            originalRequest = newRequest.build()

            val response = chain.proceed(originalRequest)
            val responseCode = response.code
            response
        })

        httpClient.readTimeout(CONNECTION_TIME_OUT.toLong(), TimeUnit.SECONDS)
        httpClient.writeTimeout(CONNECTION_TIME_OUT.toLong(), TimeUnit.SECONDS)
        httpClient.connectTimeout(CONNECTION_TIME_OUT.toLong(), TimeUnit.SECONDS)
        httpClient.hostnameVerifier(HostnameVerifier { hostname: String?, session: SSLSession? -> true })
        httpClient.addInterceptor(httpLoggingInterceptor)

        //    httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        if (BuildConfig.DEBUG) {
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE)
        }

        // set the connection time to 1 minutes
        httpClient.protocols(listOf(Protocol.HTTP_1_1))

        return httpClient.build()
    }
}
