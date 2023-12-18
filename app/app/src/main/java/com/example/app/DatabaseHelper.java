package com.example.app;

import android.content.Context;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "sma";
    private static final int DATABASE_VERSION = 1;

    // Table name and column names
    private static final String TABLE_NAME = "Users";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_SALT = "salt";
    private static final String COLUMN_HOST = "host";
    private static final String COLUMN_PASSPHRASE = "passphrase";

    // Create table query
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_USERNAME + " TEXT PRIMARY KEY," +
            COLUMN_PASSWORD + " TEXT," +
            COLUMN_SALT + " TEXT," +
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

    // Method to hash the password using salt
    private String hashPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt.getBytes());
            byte[] hash = digest.digest(password.getBytes());

            // Convert byte array to a string representation
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to generate a salt
    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        return bytesToHex(saltBytes);
    }

    // Helper method to convert byte array to hexadecimal string
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    // Method to add user to the database with salted and hashed password
    public boolean addUser(String username, String password, String host, String passphrase) {
        // Check for empty or null inputs
        if (username == null || username.isEmpty() ||
                password == null || password.isEmpty() ||
                host == null || host.isEmpty() ||
                passphrase == null || passphrase.isEmpty()) {
            return false;
        }

        // Generate a salt
        String salt = generateSalt();

        // Hash the password using salt
        String hashedPassword = hashPassword(password, salt);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, hashedPassword);
        values.put(COLUMN_SALT, salt);
        values.put(COLUMN_HOST, host);
        values.put(COLUMN_PASSPHRASE, passphrase);

        long result = db.insert(TABLE_NAME, null, values);

        if (result != -1) {
            // Successful insertion
            android.util.Log.d("DatabaseHelper", "User inserted successfully");
        } else {
            // Insertion failed
            android.util.Log.d("DatabaseHelper", "User insertion failed");
        }

        return result != -1;
    }

    // Method to check if a username exists in the database
    public boolean checkUserExistence(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +
                COLUMN_USERNAME + "=?", new String[]{username});

        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    // Method to check user credentials for login
    public boolean checkUserCredentials(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT " + COLUMN_PASSWORD + ", " + COLUMN_SALT + " FROM " +
                    TABLE_NAME + " WHERE " + COLUMN_USERNAME + "=?", new String[]{username});

            if (cursor != null && cursor.moveToFirst()) {
                int passwordIndex = cursor.getColumnIndex(COLUMN_PASSWORD);
                int saltIndex = cursor.getColumnIndex(COLUMN_SALT);

                if (passwordIndex >= 0 && saltIndex >= 0) {
                    String hashedPasswordFromDb = cursor.getString(passwordIndex);
                    String salt = cursor.getString(saltIndex);

                    if (hashedPasswordFromDb != null && salt != null) {
                        // Hash the input password with the retrieved salt
                        String hashedInputPassword = hashPassword(password, salt);

                        // Compare the stored hashed password with the hashed input password
                        return hashedPasswordFromDb.equals(hashedInputPassword);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return false;
    }
}