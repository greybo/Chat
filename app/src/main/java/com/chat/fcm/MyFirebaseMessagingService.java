package com.chat.fcm;

import android.os.Handler;
import android.util.Log;

import com.chat.utils.ChatСonstants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "log_tag";
    private static Handler handler;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i(TAG, "From: " + remoteMessage.getFrom());
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> map = remoteMessage.getData();
            Log.i(TAG, "Message data payload: " + remoteMessage.getData());
            handler.obtainMessage(ChatСonstants.HANDLER_RECIVE_MSG, map).sendToTarget();
        }
        if (remoteMessage.getNotification() != null) {
            Log.i(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    public static void setHandler(Handler h) {
        handler = h;
    }


}
