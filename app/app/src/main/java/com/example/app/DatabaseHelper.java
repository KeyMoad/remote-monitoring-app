package com.example.app;

import android.content.Context;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "sma";
    private static final int DATABASE_VERSION = 2;

    // Table name and column names
    private static final String TABLE_NAME = "Users";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_HOST = "host";
    private static final String COLUMN_PASSPHRASE = "passphrase";

    // Create table query
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_USERNAME + " TEXT PRIMARY KEY," +
            COLUMN_PASSWORD + " TEXT," +
            COLUMN_HOST + " TEXT," +
            COLUMN_PASSPHRASE + " TEXT )";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the existing table if it exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Recreate the table by calling onCreate
        onCreate(db);
    }

    // Method to add user to the database
    public boolean addUser(String username, String password, String host, String passphrase) {
        // Check for empty or null inputs
        if (username == null || username.isEmpty() ||
                password == null || password.isEmpty() ||
                host == null || host.isEmpty() ||
                passphrase == null || passphrase.isEmpty()) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        // Create a ContentValues object to insert data
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_HOST, host);
        values.put(COLUMN_PASSPHRASE, passphrase);

        // Insert the values into the database
        long result = db.insert(TABLE_NAME, null, values);

        if (result != -1) {
            // Successful insertion
            android.util.Log.d("DatabaseHelper", "User inserted successfully");
        } else {
            // Insertion failed
            android.util.Log.d("DatabaseHelper", "User insertion failed");
        }

        // Check if insertion was successful
        return result != -1;
    }

    // Method to check if a username exists in the database
    public boolean checkUserExistence(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Query to check if the username exists in the database
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +
                COLUMN_USERNAME + "=?", new String[]{username});

        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    // Method to check user credentials for login
    public boolean checkUserCredentials(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +
                COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?", new String[]{username, password});

        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }
}
