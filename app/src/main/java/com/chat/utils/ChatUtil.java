package com.chat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.chat.entity.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by m on 22.09.2017.
 */

public class ChatUtil {
    private static final String SAVED_LOGIN = "login";
    private static final String SAVED_PASS = "pass";

    public static void saveAuth(Activity activity, User user) {
        SharedPreferences sPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(SAVED_LOGIN, user.getName());
        ed.putString(SAVED_PASS, user.getPassword());
        ed.commit();
    }

    public static User loadAuth(Activity activity) {
        SharedPreferences sPref = activity.getPreferences(Context.MODE_PRIVATE);
        String userName = sPref.getString(SAVED_LOGIN, null);
        String pass = sPref.getString(SAVED_PASS, null);
        return new User(userName, pass);

    }

    public static boolean checkAuth(Activity activity) {
        User user = loadAuth(activity);
        return user.getName() != null && user.getPassword() != null;
    }
    public static String toJson(Object o){
         GsonBuilder builder = new GsonBuilder();
         Gson gson = builder.create();
       return gson.toJson(o);
    }

    public static <T> T fromJson(String strJson, Class<T> classOfT){
         GsonBuilder builder = new GsonBuilder();
         Gson gson = builder.create();
       return gson.fromJson(strJson, classOfT);
    }
}
