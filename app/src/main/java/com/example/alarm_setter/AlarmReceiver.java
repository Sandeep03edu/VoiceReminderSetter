package com.example.alarm_setter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.example.alarm_setter.App.CANCEL_NOTIFICATION;

public class AlarmReceiver extends BroadcastReceiver
{
    private NotificationManagerCompat notificationManagerCompat;

    public static Context context2;
    public static Ringtone ringtone;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onReceive(Context context, Intent intent)
    {
        context2=context;
        //we will use vibrator first
        Vibrator vibrator = (Vibrator)context.getSystemService(context.VIBRATOR_SERVICE);
        vibrator.vibrate(4000);

        Toast.makeText(context, "Alarm Done", Toast.LENGTH_LONG).show();
        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null)
        {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        ringtone = RingtoneManager.getRingtone(context, alarmUri);
        ringtone.play();

        notificationManagerCompat = NotificationManagerCompat.from(context);

        Intent activityIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, activityIntent, 0);

        Intent broadcastIntent = new Intent(context, NotificationReceiver.class);
        broadcastIntent.putExtra("check", 1);
        PendingIntent actionIntent = PendingIntent.getBroadcast(context,
                0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

//        Intent broadcastIntent2 = new Intent(context, NotificationReceiver.class);
//        broadcastIntent2.putExtra("check", 2);
//        PendingIntent actionIntent2 = PendingIntent.getBroadcast(context,
//                1, broadcastIntent2, PendingIntent.FLAG_UPDATE_CURRENT);

//        message = message.trim();
        String message = MainActivity.speechedText;
        message = message.trim();
        Notification notification =new NotificationCompat.Builder(context, CANCEL_NOTIFICATION)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Reminder")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setColor(Color.BLUE)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .addAction(R.mipmap.ic_launcher, "Cancel", actionIntent)
//                .addAction(R.mipmap.ic_launcher, "snooze for 3 min", actionIntent2)
                .build();
        notificationManagerCompat.notify(1, notification);
    }

    public static void clearNotification(Context context1) {
        NotificationManagerCompat.from(context1).cancelAll();
    }
}