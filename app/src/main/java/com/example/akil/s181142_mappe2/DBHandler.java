package com.example.akil.s181142_mappe2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by akil on 04.10.15.
 */
public class DBHandler extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "birthday";
    public static final String TABLE_CONTACTS = "contacts";
    private static final String ID = "_id";
    private static final String FIRST_NAME = "firstname";
    private static final String LAST_NAME = "lastname";
    private static final String PHONENUMBER = "phonenumber";
    private static final String BIRTHDAY = "birthday";

    private static final String TABLE_BIRTHDAY_CHECKER = "birthdayChecker";
    private static final String DATE_CHECKED = "checked";

    private static final int DATABASE_VERSION = 1;

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createPersonTable = "CREATE TABLE " + TABLE_CONTACTS + "(" + ID + " INTEGER PRIMARY KEY, "
                + FIRST_NAME + " TEXT, " + LAST_NAME + " TEXT, " + PHONENUMBER + " INTEGER, "
                + BIRTHDAY + " DATE)";
        db.execSQL(createPersonTable);

        String createBirthdayChecker = "CREATE TABLE " + TABLE_BIRTHDAY_CHECKER + "(" + DATE_CHECKED + " DATE);";
        db.execSQL(createBirthdayChecker);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_CONTACTS);
        onCreate(db);
    }

    public void addContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FIRST_NAME, contact.getmFirstName());
        values.put(LAST_NAME, contact.getmLastName());
        values.put(PHONENUMBER, contact.getmPhoneNumber());
        Calendar date = contact.getmBirthday();
        values.put(BIRTHDAY, new SimpleDateFormat("yyyy-MM-dd").format(date.getTime()));
        db.insert(TABLE_CONTACTS, null, values);
        db.close();
    }

    public ArrayList<Contact>getContacts() {

        ArrayList<Contact> contactArrayList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor != null && cursor.moveToFirst()) {

            do {
                int id = Integer.parseInt(cursor.getString(0));
                String firstName = cursor.getString(1);
                String lastName = cursor.getString(2);
                String phoneNumber = cursor.getString(3);
                Calendar date = Calendar.getInstance();

                try
                {
                    date.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(cursor.getString(4)));
                }
                catch (ParseException e)
                {
                    e.printStackTrace();
                }

                Contact contact = new Contact(id, firstName, lastName, phoneNumber, date);
                contactArrayList.add(contact);
            }while(cursor.moveToNext());
            cursor.close();
            db.close();
        }
        return contactArrayList;
    }

    public Contact getContact(int id) {

        SQLiteDatabase db = this.getReadableDatabase();
        String firstName = "";
        String lastName = "";
        String phoneNumber = "";
        Calendar date = null;

        Cursor cursor = db.query(TABLE_CONTACTS, new String[] { ID, FIRST_NAME,
                        LAST_NAME, PHONENUMBER, BIRTHDAY }, ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }
            firstName = cursor.getString(1);
            lastName = cursor.getString(2);
            phoneNumber = cursor.getString(3);
            date = Calendar.getInstance();

            try
            {
                date.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(cursor.getString(4)));
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
            cursor.close();

        cursor.close();
        db.close();
        return new Contact(id, firstName, lastName, phoneNumber, date);
    }

    public void deleteContact(Contact contact) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(TABLE_CONTACTS, ID + " =?", new String[] { String.valueOf(contact.get_ID()) });
        db.close();
    }

    public int updateContact(Contact contact) {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(FIRST_NAME, contact.getmFirstName());
        values.put(LAST_NAME, contact.getmLastName());
        Calendar date = contact.getmBirthday();
        values.put(BIRTHDAY, new SimpleDateFormat("yyyy-MM-dd").format(date.getTime()));
        values.put(PHONENUMBER, contact.getmPhoneNumber());

        return db.update(TABLE_CONTACTS, values, ID + " =?",
                new String[] { String.valueOf(contact.get_ID()) });
    }

    public ArrayList<Contact> getBirthdayPersons(String birthdayToday){
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<Contact> birthdays = new ArrayList<>();

        Cursor cursor = db.query(TABLE_CONTACTS, new String[]{ID, FIRST_NAME, LAST_NAME,
                        PHONENUMBER, BIRTHDAY}, BIRTHDAY + "=?",
                new String[]{birthdayToday}, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {

                String firstName =  cursor.getString(1);
                String lastName =  cursor.getString(2);
                String phoneNumber =  cursor.getString(3);

                Calendar date = Calendar.getInstance();

                try
                {
                    date.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(cursor.getString(4)));
                }
                catch (ParseException e)
                {
                    e.printStackTrace();
                }

                Contact contact = new Contact(firstName, lastName, phoneNumber, date);
                birthdays.add(contact);

            }while (cursor.moveToNext());

        }
        cursor.close();
        db.close();
        return birthdays;
    }

    public int getLastContact(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + TABLE_CONTACTS, null);
        cursor.moveToLast();
        int id = cursor.getInt(0);
        cursor.close();
        db.close();
        return id;
    }
}


