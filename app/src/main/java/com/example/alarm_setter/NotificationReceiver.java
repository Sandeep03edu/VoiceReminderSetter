package com.example.alarm_setter;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationReceiver extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        int check = intent.getIntExtra("check", -1);

        if(check==1) {
            MainActivity.CancelAlarm();
            Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show();
            Context context2 = AlarmReceiver.context2;
            AlarmReceiver.clearNotification(context2);
            Ringtone ringtone = AlarmReceiver.ringtone;
            ringtone.stop();
        }
    }
}
