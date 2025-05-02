package com.callmangement.EHR.api;

import android.content.Context;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {

    public static Retrofit GetRetrofitClientWithoutHeaders(final Context context, final String BASE_URL) {



        // Create logging interceptor to debug request and response
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Create an interceptor to add headers to every request
        Interceptor headerInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                Request.Builder requestBuilder = originalRequest.newBuilder()
                        .header("Content-Type", "application/json; charset=utf-8");
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        };

   OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.readTimeout(Constants.CONNECTION_TIME_OUT, TimeUnit.SECONDS);
        httpClient.connectTimeout(Constants.CONNECTION_TIME_OUT, TimeUnit.SECONDS);
        httpClient.addInterceptor(headerInterceptor);
        httpClient.addInterceptor(logging);
        OkHttpClient client = httpClient.build();
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }












}
