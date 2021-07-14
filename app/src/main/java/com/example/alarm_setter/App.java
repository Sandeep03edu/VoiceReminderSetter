package com.example.alarm_setter;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
    public static final String CANCEL_NOTIFICATION = "cancel alarm";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            NotificationChannel cancelChannel = new NotificationChannel(CANCEL_NOTIFICATION, String.valueOf(R.string.cancel_snooze), NotificationManager.IMPORTANCE_HIGH);
            cancelChannel.setDescription(String.valueOf(R.string.cancel_snooze));

            NotificationManager notificationManager =getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(cancelChannel);
        }
    }
}
