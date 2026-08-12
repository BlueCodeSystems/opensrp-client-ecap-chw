package com.bluecodeltd.ecap.chw.push;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bluecodeltd.ecap.chw.R;
import com.bluecodeltd.ecap.chw.activity.FlagActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FlagsFirebaseMessagingService extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "flags_channel";
    private static final String TOKEN_PREF_KEY = "flags_fcm_token";

    @Override
    public void onMessageReceived(RemoteMessage message) {
        super.onMessageReceived(message);
        createChannel();
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(FlagActivity.ACTION_FLAGS_UPDATED));

        Intent intent = new Intent(this, FlagActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        String title = resolveTitle(message);
        String body = resolveBody(message);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat.from(this).notify((int) System.currentTimeMillis(), builder.build());
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putString(TOKEN_PREF_KEY, token).apply();
        // Push the refreshed token to the PMP API so the backend can target this device.
        FlagsNotificationScheduler.registerDevice(getApplicationContext());
    }

    private String resolveTitle(RemoteMessage message) {
        if (message.getNotification() != null && message.getNotification().getTitle() != null) {
            return message.getNotification().getTitle();
        }
        if (message.getData().containsKey("title")) {
            String title = message.getData().get("title");
            if (title != null && !title.trim().isEmpty()) {
                return title;
            }
        }
        return "New flag";
    }

    private String resolveBody(RemoteMessage message) {
        if (message.getNotification() != null && message.getNotification().getBody() != null) {
            return message.getNotification().getBody();
        }
        if (message.getData().containsKey("body")) {
            String body = message.getData().get("body");
            if (body != null && !body.trim().isEmpty()) {
                return body;
            }
        }
        return "A new flag was created";
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Flags",
                NotificationManager.IMPORTANCE_HIGH
        );
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
    }
}
