package com.example.akil.s181142_mappe2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MySMSService extends Service {

    private DBHandler mDBHandler;
    private ArrayList<Contact> mBirthdayPersons;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        mDBHandler = new DBHandler(this);
        String birthday = simpleDateFormat.format(Calendar.getInstance().getTime());
        mBirthdayPersons = mDBHandler.getBirthdayPersons(birthday);
        SharedPreferences preferences = getSharedPreferences("birthdayPrefs", MODE_PRIVATE);
        final String smsMessage = preferences.getString("birthdayMessage", getString(R.string.birthday_message));

        if(mBirthdayPersons != null && MainActivity.isServiceRunning(this, MyAlarmManagerService.class)){

            for(final Contact contact : mBirthdayPersons) {
                new Handler(Looper.getMainLooper()).post(new Runnable()
                {  @Override public void run()
                    {
                        SmsManager smsManager = SmsManager.getDefault();
                        String phoneNumber = contact.getmPhoneNumber();
                        smsManager.sendTextMessage(phoneNumber, null, smsMessage, null, null);

                        NotificationCompat.InboxStyle notification = new NotificationCompat.InboxStyle();
                        notification.setBigContentTitle(getString((R.string.BirthdayHelper)));
                        for (int i = 0; i < mBirthdayPersons.size(); i++)
                        {
                            Contact contact1 = mBirthdayPersons.get(i);
                            notification.addLine(contact1.getmFirstName()  + " " + contact1.getmLastName());
                        }

                        notification.setSummaryText(getString(R.string.birthday_mesasge_summary));

                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());
                        builder.setSmallIcon(R.mipmap.ic_launcher);
                        builder.setWhen(System.currentTimeMillis());
                        builder.setAutoCancel(true);
                        builder.setContentTitle("");
                        builder.setContentText("");
                        builder.setStyle(notification);

                        Notification nb = builder.build();
                        notificationManager.notify(0, nb);
                    }
                });
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
