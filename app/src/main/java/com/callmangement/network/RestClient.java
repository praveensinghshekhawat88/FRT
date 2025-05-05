package com.callmangement.network;

import android.util.Log;

import com.callmangement.BuildConfig;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class RestClient {

    public static final Integer CONNECTION_TIME_OUT = 240;
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

    public static OkHttpClient okClient() {
//        return new OkHttpClient.Builder()
//                .connectTimeout(3, TimeUnit.MINUTES)
//                .writeTimeout(3, TimeUnit.MINUTES)
//                .readTimeout(3, TimeUnit.MINUTES)
//                .hostnameVerifier((hostname, session) -> true)
//                .build();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.e("RESPONSE", message);
            }
        });

        httpClient.addInterceptor(chain -> {
            Request originalRequest = chain.request();
            // set OAuth token
            Request.Builder newRequest = originalRequest.newBuilder();

            originalRequest = newRequest.build();

            Response response = chain.proceed(originalRequest);
            int responseCode = response.code();

            return response;
        });

        httpClient.readTimeout(CONNECTION_TIME_OUT, TimeUnit.SECONDS);
        httpClient.writeTimeout(CONNECTION_TIME_OUT, TimeUnit.SECONDS);
        httpClient.connectTimeout(CONNECTION_TIME_OUT, TimeUnit.SECONDS);
        httpClient.hostnameVerifier((hostname, session) -> true);
        httpClient.addInterceptor(httpLoggingInterceptor);

    //    httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        if (BuildConfig.DEBUG) {
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }

        // set the connection time to 1 minutes
        httpClient.protocols(Collections.singletonList(Protocol.HTTP_1_1));

        return httpClient.build();
    }
}
