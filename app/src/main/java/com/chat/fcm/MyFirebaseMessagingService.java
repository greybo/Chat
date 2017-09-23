package com.chat.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.chat.R;
import com.chat.ui.ChatActivity;
import com.chat.utils.ChatConst;
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
            if (handler == null)
                sendNotification(map);
            else
                handler.obtainMessage(ChatConst.HANDLER_RECIVE_MSG, map.get("companionToken")).sendToTarget();
        }
        if (remoteMessage.getNotification() != null) {
            Log.i(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    private void sendNotification(Map<String, String> map) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("companionToken", map.get("companionToken"));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(map.get("companionName"))
                .setContentText("msg: " + map.get("message"))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    public static void setHandler(Handler h) {
        handler = h;
    }


}
