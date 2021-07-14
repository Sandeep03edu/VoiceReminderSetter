package com.example.alarm_setter;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static com.example.alarm_setter.App.CANCEL_NOTIFICATION;

public class MainActivity extends AppCompatActivity {
    TimePicker alarmTimePicker;
    static AlarmManager alarmManager;
    ImageView mic;
    public static String speechedText;
    TextView temptext;
    static PendingIntent pendingIntent;
    int suffix = -1;
    long milliSeconds;
    long targetMilliseconds;

    private NotificationManagerCompat notificationManagerCompat;

    public static final int REQUEST_SPEECH_TEXT = 1;
    public static final int IS_SECOND = 20;
    public static final int IS_MINUTE = 21;
    public static final int IS_HOUR = 22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        temptext = findViewById(R.id.tempText);
        temptext.setMovementMethod(new ScrollingMovementMethod());
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        mic = findViewById(R.id.mic);
        alarmTimePicker = findViewById(R.id.timePicker);

        notificationManagerCompat = NotificationManagerCompat.from(this);

        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speechToText();
            }
        });

    }
    private void speechToText(){
        Intent intent1 = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent1.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent1.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent1.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

        try {
            startActivityForResult(intent1, REQUEST_SPEECH_TEXT);
        }catch (Exception e){
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        temptext.setText("");
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_SPEECH_TEXT && resultCode== RESULT_OK && data!=null){
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            speechedText = (Objects.requireNonNull(result).get(0));
            speechedText = speechedText.toLowerCase();
            checkAfter();
            checkSuffix();
            getMilliSeconds();
            setAlarmPickerTime();
            launchAlarm();
        }
        temptext.append("Speeched Text: " + speechedText + "\n");
        temptext.append("Alarm Time: "+ alarmTimePicker.getHour() + " " + alarmTimePicker.getMinute() + "\n");
        temptext.append("Suffix is " + suffix + "\n");
    }
    private void launchAlarm() {
        speechedText = speechedText.trim();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
        calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());

        temptext.append("Calender TargetTime: " +  calendar.get(Calendar.HOUR_OF_DAY) + " " + calendar.get(Calendar.MINUTE)+ "\n");

        Intent intent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        long time = (calendar.getTimeInMillis() - (calendar.getTimeInMillis() % 60000));
        if (System.currentTimeMillis() > time) {
            // setting time as AM and PM
            if (calendar.AM_PM == 0)
                time = time + (1000 * 60 * 60 * 12);
            else
                time = time + (1000 * 60 * 60 * 24);
        }
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 10000, pendingIntent);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setAlarmPickerTime() {
        temptext.append("Local time: " + System.currentTimeMillis() + "\n");
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        temptext.append("Formatted: " + formatter.format(date) + "\n");

        targetMilliseconds = System.currentTimeMillis() + milliSeconds;
        Date targetTime = new Date(targetMilliseconds);
        temptext.append("Target time: " + formatter.format(targetTime) + "\n");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(targetTime);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);

        temptext.append("Clock Final time: " + hour + " " + min + "\n");
        alarmTimePicker.setMinute(min);
        alarmTimePicker.setHour(hour);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void snoozedAlarm(long snoozedTime){
        Calendar calendar = Calendar.getInstance();
        Date targetTime = new Date(snoozedTime);
        calendar.setTime(targetTime);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);

        alarmTimePicker.setMinute(min);
        alarmTimePicker.setHour(hour);

        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
        calendar2.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());

        Intent intent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        long time2 = (calendar.getTimeInMillis() - (calendar.getTimeInMillis() % 60000));
        if (System.currentTimeMillis() > time2) {
            // setting time as AM and PM
            if (calendar.AM_PM == 0)
                time2 = time2 + (1000 * 60 * 60 * 12);
            else
                time2 = time2 + (1000 * 60 * 60 * 24);
        }

        SimpleDateFormat formatter= new SimpleDateFormat("HH:mm:ss");
        Toast.makeText(this, "Target Time: " + formatter.format(targetTime), Toast.LENGTH_LONG).show();

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time2, 10000, pendingIntent);
    }


    private void getMilliSeconds() {
        long duration = 0;
        for(int i=0; i<speechedText.length(); ++i){
            if(speechedText.charAt(i)>='0' && speechedText.charAt(i)<='9'){
                while(speechedText.charAt(i)>='0' && speechedText.charAt(i)<='9') {
                    speechedText = speechedText.replace(speechedText.charAt(i), ' ');
                    duration += speechedText.charAt(i) - '0';
                    duration *= 10;
                    i++;
                }
            }
        }
        duration/=10;
        temptext.append("Duration is :" + duration + "\n");

        if(suffix==IS_SECOND){
            if(duration<60){
                duration=60;
                Toast.makeText(this, "Minimum time is 60 sec(1 minute)", Toast.LENGTH_SHORT).show();
            }
            milliSeconds = duration*1000;
        }
        else if(suffix==IS_MINUTE){
            milliSeconds = duration*60*1000;
        }
        else if(suffix == IS_HOUR){
            milliSeconds = duration*60*60*1000;
        }

        temptext.append("Milliseconds: " + milliSeconds + "\n");
    }
    private void checkSuffix() {
        int hr1 = speechedText.split("hours", -1).length-1;
        int hr2 = speechedText.split("hrs", -1).length-1;
        int hr3 = speechedText.split("hour", -1).length-1;
        int hr4 = speechedText.split("hr", -1).length-1;

        int m1 = speechedText.split("minutes", -1).length-1;
        int m2 = speechedText.split("minute", -1).length-1;
        int m3 = speechedText.split("mins", -1).length-1;
        int m4 = speechedText.split("min", -1).length-1;

        int s1 = speechedText.split("seconds", -1).length-1;
        int s2 = speechedText.split("second", -1).length-1;
        int s3 = speechedText.split("secs", -1).length-1;
        int s4 = speechedText.split("sec", -1).length-1;

        if(s1>0 || s2>0 || s3>0 || s4>0){
            suffix = IS_SECOND;
            speechedText = speechedText.replace("seconds", "");
            speechedText = speechedText.replace("second", "");
            speechedText = speechedText.replace("secs", "");
            speechedText = speechedText.replace("sec", "");
        }
        else if(m1>0 || m2>0 ||m3>0 || m4>0) {
            suffix = IS_MINUTE;
            speechedText = speechedText.replace("minutes", "");
            speechedText = speechedText.replace("minute", "");
            speechedText = speechedText.replace("mins", "");
            speechedText = speechedText.replace("min", "");
        }
        else if(hr1>0 || hr2>0 || hr3>0 || hr4>0){
            suffix = IS_HOUR;
            speechedText = speechedText.replace("hours", "");
            speechedText = speechedText.replace("hour", "");
            speechedText = speechedText.replace("hrs", "");
            speechedText = speechedText.replace("hr", "");
        }
    }
    private void checkAfter() {
        String word = "after ";
        int check = speechedText.split(word, -1).length-1;

        if(check==0){
            speechedText="";
        }
        else{
            speechedText = speechedText.replace(word, "");
        }
    }
    public static void CancelAlarm(){
        alarmManager.cancel(pendingIntent);
    }

    public String getSpeechedText(){
        return speechedText;
    }

    public void TempNotification(Context context){

        Intent activityIntent = new Intent(getBaseContext(), MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(),
                0, activityIntent, 0);

//        Intent broadcastIntent = new Intent(context, NotificationReceiver.class);
//        broadcastIntent.putExtra("check", 1);
//        PendingIntent actionIntent = PendingIntent.getBroadcast(context,
//                0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//
//        Intent broadcastIntent2 = new Intent(context, NotificationReceiver.class);
//        broadcastIntent2.putExtra("check", 2);
//        PendingIntent actionIntent2 = PendingIntent.getBroadcast(context,
//                1, broadcastIntent2, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification =new NotificationCompat.Builder(getBaseContext(), CANCEL_NOTIFICATION)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("title")
                .setContentText("message")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setColor(Color.BLUE)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .build();
//                .addAction(R.mipmap.ic_launcher, "Toast1", actionIntent)
//                .addAction(R.mipmap.ic_launcher, "Toast2", actionIntent2)

        notificationManagerCompat.notify(1, notification);
    }
}