package com.example.akil.s181142_mappe2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Calendar;


public class MyAlarmManagerService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Intent i = new Intent(getApplicationContext(), MySMSService.class);

        SharedPreferences preferences = getSharedPreferences("timePrefs", MODE_PRIVATE);
        int hour = preferences.getInt("hour", -1);
        int minute = preferences.getInt("minute", -1);
        boolean isSet = preferences.getBoolean("isSet", false);

        Calendar now = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        long alarmTime = 0;

        //If a the set alarm time is in the past then delay the alarm for half day.
        if(calendar.getTimeInMillis() <= now.getTimeInMillis()) {
            alarmTime = calendar.getTimeInMillis() + ((AlarmManager.INTERVAL_HALF_DAY)/(2));
        } else {
            alarmTime = calendar.getTimeInMillis();
        }


        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, i, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime, 86400000, pendingIntent);
        return Service.START_NOT_STICKY;
    }
}
