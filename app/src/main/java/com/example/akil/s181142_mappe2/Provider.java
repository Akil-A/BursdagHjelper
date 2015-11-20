package com.example.akil.s181142_mappe2;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;


public class Provider extends ContentProvider
{
    public final static String PROVIDER = "com.example.akil.s181142_mappe2.Provider";
    private static final int CONTACT = 1;
    private static final int CONTACT_ID = 2;
    private DBHandler mDBHandler;
    private SQLiteDatabase mDB;

    private static final UriMatcher uriMatcher;
    static
    {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER, "contact", CONTACT);
        uriMatcher.addURI(PROVIDER, "contact/#", CONTACT_ID);
    }

    @Override
    public boolean onCreate()
    {
        mDBHandler = new DBHandler(getContext());
        mDB = mDBHandler.getWritableDatabase();
        return true;
    }

    @Override
    public String getType(Uri uri)
    {
        switch(uriMatcher.match(uri))
        {
            case CONTACT_ID : return "vnd.android.cursor.dir/vnd.com.example.akil.s181142_mappe2";
            case CONTACT  : return "vnd.android.cursor.item/vnd.com.example.akil.s181142_mappe2";
            default : throw new IllegalArgumentException("Ugyldig URI" + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,String sortOrder)
    {


        Cursor cur;

        if (uriMatcher.match(uri) == CONTACT)
        {
            cur = mDB.query(DBHandler.TABLE_CONTACTS, projection, "_id = " + uri.getPathSegments().get(1) ,selectionArgs, null, null, sortOrder);
            return cur;
        }
        else
        {
            cur = mDB.query(DBHandler.TABLE_CONTACTS, null, null, null, null, null, null);
            return cur;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        if(uriMatcher.match(uri) == CONTACT_ID) {
            mDB.delete(DBHandler.TABLE_CONTACTS, "_id = " + uri.getPathSegments().get(1), selectionArgs);
            getContext().getContentResolver().notifyChange(uri, null);
            return 1;
        }
        if (uriMatcher.match(uri) == CONTACT) {
            mDB.delete(DBHandler.TABLE_CONTACTS, null, null);
            getContext().getContentResolver().notifyChange(uri, null);
            return 2;
        }
        return 0;
    }

   @Override
    public Uri insert(Uri arg0, ContentValues arg1)
    {
        mDB = mDBHandler.getWritableDatabase();
        mDB.insert(DBHandler.TABLE_CONTACTS, null, arg1);

        Cursor cursor = mDB.query(DBHandler.TABLE_CONTACTS, null, null, null, null, null, null);
        cursor.moveToLast();
        long minid = cursor.getLong(0);
        getContext().getContentResolver().notifyChange(arg0, null);

        return ContentUris.withAppendedId(arg0, minid);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        if(uriMatcher.match(uri) == CONTACT){
        mDB.update(DBHandler.TABLE_CONTACTS, values, "_id = " + uri.getPathSegments().get(1), null);
        return 1;
        }
        if(uriMatcher.match(uri) == CONTACT_ID) {
            mDB.update(DBHandler.TABLE_CONTACTS, null, null, null);
            getContext().getContentResolver().notifyChange(uri, null);
            return 2;
        }
        return 0;
    }
}
