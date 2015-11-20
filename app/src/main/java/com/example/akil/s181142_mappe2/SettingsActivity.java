package com.example.akil.s181142_mappe2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;

import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {

    private TimePicker mTimePicker;
    private EditText birthdayEditText;
    private Context mContext;
    private Switch mServiceSwitch;
    private boolean mSwitchValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.settings_activity_title));

        mContext = getApplicationContext();
        birthdayEditText = (EditText)findViewById(R.id.sms_text);
        mTimePicker = (TimePicker)findViewById(R.id.timepicker);
        mTimePicker.setIs24HourView(DateFormat.is24HourFormat(this));
        Calendar calendar = Calendar.getInstance();

        SharedPreferences timePrefs = mContext.getSharedPreferences("timePrefs", Context.MODE_PRIVATE);

        //If time not set before set current time on TimePicker else set the old alarm time
        int hour = timePrefs.getInt("hour", -1);
        if(hour == -1) {
            mTimePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
            mTimePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
        } else {
            mTimePicker.setCurrentHour(hour);
            int minute = timePrefs.getInt("minute", -1);
            mTimePicker.setCurrentMinute(minute);
        }
        SharedPreferences messagePrefs = mContext.getSharedPreferences("birthdayPrefs", Context.MODE_PRIVATE);
        String birthdayMessage = messagePrefs.getString("birthdayMessage", null);

        //Set default birthday SMS message if the default is not changed.
        if(birthdayMessage == null) {
            birthdayEditText.setText(R.string.birthday_message);
        } else {
            birthdayEditText.setText(birthdayMessage);
        }

        mServiceSwitch = (Switch)findViewById(R.id.serviceSwitch);
        mServiceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mSwitchValue = true;
                } else {
                    mSwitchValue = false;
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.save_contact:
                if(validation()){
                    saveTime();
                    saveBirthDayMessage();
                    //If Service Switch is on true start update the service else stop the service
                    if(mSwitchValue){
                        Intent startService = new Intent(getApplicationContext(), MyAlarmManagerService.class);
                        startService(startService);
                        finish();
                    } else {
                        Intent stopService = new Intent(getApplicationContext(), MyAlarmManagerService.class);
                        stopService(stopService);
                        finish();
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem delete = menu.findItem(R.id.icon_delete);
        delete.setVisible(false);

        MenuItem settings = menu.findItem(R.id.icon_settings);
        settings.setVisible(false);

        MenuItem cancel = menu.findItem(R.id.icon_stop);
        cancel.setVisible(false);

        MenuItem start = menu.findItem(R.id.icon_start);
        start.setVisible(false);

        MenuItem saveContact = menu.findItem(R.id.save_contact);
        saveContact.setVisible(true);
        return super.onPrepareOptionsMenu(menu);
    }

    public void saveTime(){
        SharedPreferences preferences = mContext.getSharedPreferences("timePrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("hour", mTimePicker.getCurrentHour());
        editor.putInt("minute", mTimePicker.getCurrentMinute());
        editor.putBoolean("isSet", true);
        editor.apply();
    }

    public void saveBirthDayMessage(){
        SharedPreferences preferences = mContext.getSharedPreferences("birthdayPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("birthdayMessage", birthdayEditText.getText().toString());
        editor.apply();
    }


    @Override
    protected void onResume() {
        super.onResume();

        boolean isRunning = MainActivity.isServiceRunning(SettingsActivity.this, MyAlarmManagerService.class);

        if(isRunning)
            mServiceSwitch.setChecked(true);
        else
            mServiceSwitch.setChecked(false);
    }

    public boolean validation(){
        View view = findViewById(android.R.id.content);
        if(birthdayEditText.getText().toString().isEmpty() || birthdayEditText.getText() == null) {
            Snackbar snackbar = Snackbar.make(view, R.string.snackbar_sms_validation, Snackbar.LENGTH_LONG);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor((Color.parseColor(getString(R.string.snackbar_color_red))));
            snackbar.show();
            return false;
        }
        return true;
    }
}
