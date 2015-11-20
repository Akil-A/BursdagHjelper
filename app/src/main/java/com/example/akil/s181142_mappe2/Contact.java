package com.example.akil.s181142_mappe2;

import java.text.DateFormat;
import java.util.Calendar;

/**
 * Created by akil on 08.10.15.
 */
public class Contact {

    private int _ID;
    private String mFirstName, mLastName, mPhoneNumber;
    private Calendar mBirthday;

    public Contact(int _ID, String mFirstname, String mLastname, String mPhonenumber, Calendar mBirthday) {
        this._ID = _ID;
        this.mFirstName = mFirstname;
        this.mLastName = mLastname;
        this.mPhoneNumber = mPhonenumber;
        this.mBirthday = mBirthday;
    }

    public Contact(String mFirstname, String mLastname, String mPhonenumber, Calendar mBirthday) {
        this.mFirstName = mFirstname;
        this.mLastName = mLastname;
        this.mPhoneNumber = mPhonenumber;
        this.mBirthday = mBirthday;
    }

    public int get_ID() {
        return _ID;
    }

    public String getmFirstName() {
        return mFirstName;
    }

    public void setmFirstName(String mFirstName) {
        this.mFirstName = mFirstName;
    }

    public String getmLastName() {
        return mLastName;
    }

    public void setmLastName(String mLastName) {
        this.mLastName = mLastName;
    }

    public String getmPhoneNumber() {
        return mPhoneNumber;
    }

    public void setmPhoneNumber(String mPhoneNumber) {
        this.mPhoneNumber = mPhoneNumber;
    }

    public Calendar getmBirthday() {
        return mBirthday;
    }

    public void setmBirthday(Calendar mBirthday) {
        this.mBirthday = mBirthday;
    }

    public String toString(){
        return mFirstName + " " + mLastName + DateFormat.getDateInstance(DateFormat.SHORT).format(mBirthday.getTime());
    }
}
