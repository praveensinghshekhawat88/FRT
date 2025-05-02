package com.callmangement.Network;

import android.text.TextUtils;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by King Jocoa on 9/23/2016.
 */
public class MultipartRequester {
    public static RequestBody fromString(String data) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), data);
    }
    public static MultipartBody.Part fromFile(String key, String data) {
        if (TextUtils.isEmpty(data)) return null;
        if (data.contains("http:") || data.contains("https:")) return null;
        File file = new File(data);
        String fileName = file.getName();
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        return MultipartBody.Part.createFormData(key, fileName, requestFile);
    }
}
