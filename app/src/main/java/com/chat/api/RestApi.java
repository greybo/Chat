package com.chat.api;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by m on 22.09.2017.
 */

public interface RestApi {

        @Headers({
                "Content-Type:application/json"
        })
        @POST("fcm/send")
        Call<ResponseBody> sendToTopic(
                @Header("Authorization") String token,
                @Body RequestBody body);

}
