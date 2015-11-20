package com.example.akil.s181142_mappe2;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private DBHandler mDBHandler;
    private int mID;
    private boolean mAdded = false;
    private String mDeleted = null;
    private View mView;
    private SharedPreferences mPreferences = null;
    private boolean mIsRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.main_activity_name));
        mDBHandler = new DBHandler(this);

        //Check if service is running to update the icons in ActionBar
        mIsRunning = isServiceRunning(MainActivity.this, MyAlarmManagerService.class);

        mPreferences = getSharedPreferences("firstTIme", MODE_PRIVATE);

        Intent intent = getIntent();
        mAdded = intent.getBooleanExtra("added", false);
        mDeleted = intent.getStringExtra("deleted");
        mID = intent.getIntExtra("id", -1);
        mView = findViewById(R.id.activity_main_layout);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        Drawable myFabSrc = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_person_add_white_24dp, getTheme());
        Drawable willBeWhite = myFabSrc.getConstantState().newDrawable();
        willBeWhite.mutate().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        fab.setImageDrawable(willBeWhite);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddContactActivity.class);
                startActivity(intent);
            }
        });
        //If a user was updated show this snackbar
        if(mID != -1)
            showSnackBarUpdate();
        else if(mAdded) //If user was added show this snackbar
            showSnackBarAdd();
        else if(mDeleted != null)
            showSnackBarDelete(mDeleted); //If user was deleted show this snackbar
    }


    public void showSnackBarUpdate(){
        Contact contact = mDBHandler.getContact(mID);
        String firstName = contact.getmFirstName();
        String lastName = contact.getmLastName();

        Snackbar snackbar = Snackbar.make(mView, firstName + " " + lastName + getString(R.string.snackbar_contact_update), Snackbar.LENGTH_LONG);
        View view = snackbar.getView();
        view.setBackgroundColor((Color.parseColor(getString(R.string.snackbar_color_blue))));
        snackbar.show();
    }

    public void showSnackBarAdd(){
        int id = mDBHandler.getLastContact();
        Contact contact = mDBHandler.getContact(id);
        String firstName = contact.getmFirstName();
        String lastName = contact.getmLastName();

        Snackbar snackbar = Snackbar.make(mView, firstName + " " + lastName + getString(R.string.snackbar_contact_added), Snackbar.LENGTH_LONG);
        View view = snackbar.getView();
        view.setBackgroundColor((Color.parseColor(getString(R.string.snackbar_color_blue))));
        snackbar.show();
    }

    public void showSnackBarDelete(String name){
        Snackbar snackbar = Snackbar.make(mView, name + getString(R.string.snackbar_contact_deleted), Snackbar.LENGTH_LONG);
        View view = snackbar.getView();
        view.setBackgroundColor((Color.parseColor(getString(R.string.snackbar_color_blue))));
        snackbar.show();
    }

    //This method is taken from Stackoverflow: http://stackoverflow.com/a/5921190/5397331
    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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
            case R.id.icon_start:
                Intent startServiceIntent = new Intent(getApplicationContext(), MyAlarmManagerService.class);
                startService(startServiceIntent);
                Snackbar serviceStartSnackbar = Snackbar.make(mView, R.string.sms_service_on_message, Snackbar.LENGTH_LONG);

                View serviceStartView = serviceStartSnackbar.getView();
                serviceStartView.setBackgroundColor((Color.parseColor(getString(R.string.snackbar_color_blue))));
                serviceStartSnackbar.show();
                mIsRunning = true;
                invalidateOptionsMenu();
                return true;
            case R.id.icon_stop:
                Intent cancelAlarmService = new Intent(getApplicationContext(), MyAlarmManagerService.class);
                PendingIntent pintent = PendingIntent.getService(getApplicationContext(), 0, cancelAlarmService, 0);
                pintent.cancel();
                AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarm.cancel(pintent);
                stopService(cancelAlarmService);

                Snackbar serviceStopsnackbar = Snackbar.make(mView, R.string.sms_service_off_message, Snackbar.LENGTH_LONG);
                View serviceStopView = serviceStopsnackbar.getView();
                serviceStopView.setBackgroundColor((Color.parseColor(getString(R.string.snackbar_color_blue))));
                serviceStopsnackbar.show();
                mIsRunning = false;
                invalidateOptionsMenu();
                return true;
            case R.id.icon_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem done = menu.findItem(R.id.save_contact);
        done.setVisible(false);

        MenuItem delete = menu.findItem(R.id.icon_delete);
        delete.setVisible(false);

        MenuItem serviceStart = menu.findItem(R.id.icon_start);
        MenuItem serviceStop = menu.findItem(R.id.icon_stop);
        //Change icons in case if service is running or not
        if(mIsRunning)  {
            serviceStop.setVisible(true);
            serviceStart.setVisible(false);
            mPreferences.edit().putBoolean("firstrun", false).apply();
        //Fire the broadcast if the app is ran for first time.
        } else if(mPreferences.getBoolean("firstrun", true)) {
            serviceStart.setVisible(false);
            Intent startIntent = new Intent();
            startIntent.setAction("com.example.akil.s181142_mappe2.MyBroadcast");
            sendBroadcast(startIntent);
            serviceStop.setVisible(true);
            mPreferences.edit().putBoolean("firstrun", false).apply();
        } else if( !mIsRunning) {
            serviceStart.setVisible(true);
            serviceStop.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsRunning = isServiceRunning(MainActivity.this, MyAlarmManagerService.class);

        if(mIsRunning){
            Intent service = new Intent(getApplicationContext(), MyAlarmManagerService.class);
            stopService(service);
            startService(service);
        } else {
            Intent cancelAlarmService = new Intent(getApplicationContext(), MyAlarmManagerService.class);
            PendingIntent pintent = PendingIntent.getService(getApplicationContext(), 0, cancelAlarmService, 0);
            pintent.cancel();
            AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarm.cancel(pintent);
            stopService(cancelAlarmService);
        }
        invalidateOptionsMenu();
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
}
