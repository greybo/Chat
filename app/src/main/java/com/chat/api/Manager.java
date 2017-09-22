package com.chat.api;

import android.os.Handler;
import android.util.Log;

import com.chat.dao.UserDao;
import com.chat.entity.MyRequestBody;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by m on 20.09.2017.
 */

public class Manager {

    private static final String TAG = "log_tag";
    private static String apiKey = "key=AAAAOEg4f8Q:APA91bGZwWReIoORCrJuG_eGP1yck3vNbZzrqK6ItwuesCPclWgkklRdn4jtYmor7o3g94VIhfpKPl4gGVkiLUcKu2MqT-YXnRbaqpWFh1hwKKiM5QSLkL6TNtBg3mTRGkPO7aExMIuU";

    private final static String URL_FCM = "https://fcm.googleapis.com/";
    private Retrofit retrofit;
    private Handler handler;
    private UserDao dao;

    private GsonBuilder builder = new GsonBuilder();
    private Gson gson = builder.create();

    public Manager(Handler handler) {
        this.handler = handler;
        dao = new UserDao(handler);
    }

    private RestApi getApi(String url) {
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(RestApi.class);
    }

    public void send(String msg, String token) {
        MyRequestBody body = new MyRequestBody();
        body.setTo(token);
        body.getData().setMessage(msg);

        String text = gson.toJson(body);
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), text);
        getApi(URL_FCM).sendToTopic(apiKey, requestBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i(TAG, "send code: " + response.code() + " body: " + response.body());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(TAG, "send onFailure: " + t.getMessage());
            }
        });
    }

    public UserDao getDao() {
        return dao;
    }
}
