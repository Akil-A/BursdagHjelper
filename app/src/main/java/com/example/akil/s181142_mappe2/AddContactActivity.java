package com.example.akil.s181142_mappe2;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class AddContactActivity extends AppCompatActivity {

    private EditText mFirstName;
    private EditText mLastName;
    private EditText mPhoneNumber;
    private DatePicker mDatePicker;
    private int mID;
    private Contact existingContact;
    private DBHandler mDBHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDBHandler = new DBHandler(this);

        mFirstName = (EditText)findViewById(R.id.first_name);
        mLastName = (EditText)findViewById(R.id.last_name);
        mPhoneNumber = (EditText)findViewById(R.id.phone_number);
        mDatePicker = (DatePicker)findViewById(R.id.date);
        mDatePicker.setMaxDate(new Date().getTime());

        Intent returnIntent = getIntent();
        mID = returnIntent.getIntExtra("id", -1);

        //Change the title in ActionBar according to if a contact is being added or edited.
        if(mID != -1) { //If contact already exist fill in their info
            existingContact = existingContact();
            setTitle(getString(R.string.add_contact_activity_title2));
        } else {
            setTitle(getString(R.string.add_contact_activity_title));
        }
    }

    private void addContact(){
        Contact contact;
        String firstName = mFirstName.getText().toString();
        String lastName = mLastName.getText().toString();
        String phoneNumber = mPhoneNumber.getText().toString();

        int year = mDatePicker.getYear();
        int month = mDatePicker.getMonth();
        int day = mDatePicker.getDayOfMonth();

        Calendar date = Calendar.getInstance();
        date.set(year, month, day);

        //If contact already exist update their info else add new contact
        if(mID != -1) {
            existingContact.setmFirstName(firstName);
            existingContact.setmLastName(lastName);
            existingContact.setmPhoneNumber(phoneNumber);
            existingContact.setmBirthday(date);

            mDBHandler.updateContact(existingContact);
        } else {
            contact = new Contact(firstName,lastName,phoneNumber,date);
            mDBHandler.addContact(contact);
        }
    }

    public Contact existingContact(){
            Contact contact;
            contact = mDBHandler.getContact(mID);

            mFirstName.setText(contact.getmFirstName());
            mLastName.setText(contact.getmLastName());
            mPhoneNumber.setText(contact.getmPhoneNumber());

            Calendar date = contact.getmBirthday();
            int year = date.get(Calendar.YEAR);
            int month = date.get(Calendar.MONTH);
            int day = date.get(Calendar.DAY_OF_MONTH);
            mDatePicker.updateDate(year, month, day);

            return contact;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
                if(validation()) {
                    addContact();
                    Intent intent = new Intent(AddContactActivity.this, MainActivity.class);
                    if (mID != -1)
                        intent.putExtra("id", mID);
                    else
                        intent.putExtra("added", true);
                    startActivity(intent);
                    finish();
                }
                return true;
            case R.id.icon_delete:
                final AlertDialog.Builder builder =
                        new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.dialog_mesasge));
                builder.setPositiveButton(getString(R.string.dialog_delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Contact contact = mDBHandler.getContact(mID);
                        Intent intent = new Intent(AddContactActivity.this, MainActivity.class);
                        intent.putExtra("deleted", contact.getmFirstName() + " " + contact.getmLastName());
                        mDBHandler.deleteContact(contact);
                        startActivity(intent);
                        finish();
                    }
                });

                builder.setNegativeButton(getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(mID == -1){
            MenuItem item = menu.findItem(R.id.icon_delete);
            item.setVisible(false);
        }

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

    public boolean validation(){
        View view = findViewById(android.R.id.content);
        if(mFirstName.getText().toString().isEmpty() || mFirstName.getText() == null) {
            Snackbar snackbar = Snackbar.make(view, R.string.name_input_validation, Snackbar.LENGTH_LONG);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor((Color.parseColor(getString(R.string.snackbar_color_red))));
            snackbar.show();
            return false;
        } else if(mLastName.getText().toString().isEmpty() || mFirstName.getText() == null) {
            Snackbar snackbar = Snackbar.make(view, R.string.lastname_input_validation, Snackbar.LENGTH_LONG);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor((Color.parseColor(getString(R.string.snackbar_color_red))));
            snackbar.show();
            return false;
        } else if(mPhoneNumber.getText().toString().isEmpty() || mPhoneNumber.getText() == null) {
            Snackbar snackbar = Snackbar.make(view, R.string.phonenumber_input_validation, Snackbar.LENGTH_LONG);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor((Color.parseColor(getString(R.string.snackbar_color_red))));
            snackbar.show();
            return false;
        } else if(mPhoneNumber.getText().toString().length() != getResources().getInteger(R.integer.phonenumber_length)) {
            Snackbar snackbar = Snackbar.make(view, getString(R.string.phonenumber_message) + getResources().getInteger(R.integer.phonenumber_length) + getString(R.string.number_length), Snackbar.LENGTH_LONG);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor((Color.parseColor(getString(R.string.snackbar_color_red))));
            snackbar.show();
            return false;
        }
        return true;
    }

}
