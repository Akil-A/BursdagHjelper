package com.example.akil.s181142_mappe2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


public class MyBroadcastReceiver extends BroadcastReceiver{
    MyAlarmManagerService myAlarmManagerService = new MyAlarmManagerService();
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, MyAlarmManagerService.class);
        context.startService(service);
    }
}
