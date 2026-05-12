package com.example.d2d;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "D2D_Local.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_ORDERS = "orders";
    private static final String TABLE_SESSIONS = "sessions";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users Table
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id TEXT, " +
                "role TEXT, " +
                "email TEXT, " +
                "name TEXT)");

        // Create Orders Table
        db.execSQL("CREATE TABLE " + TABLE_ORDERS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "order_id TEXT, " +
                "restaurant_name TEXT, " +
                "status TEXT, " +
                "price TEXT, " +
                "timestamp LONG)");

        // Create Sessions Table
        db.execSQL("CREATE TABLE " + TABLE_SESSIONS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id TEXT, " +
                "token TEXT, " +
                "is_active INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSIONS);
        onCreate(db);
    }

    // --- USER METHODS ---
    public void saveUser(String userId, String role, String email, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, null, null); // Clear old user

        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("role", role);
        values.put("email", email);
        values.put("name", name);

        db.insert(TABLE_USERS, null, values);
    }

    // --- ORDER METHODS ---
    public void saveOrder(String orderId, String restaurantName, String status, String price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("order_id", orderId);
        values.put("restaurant_name", restaurantName);
        values.put("status", status);
        values.put("price", price);
        values.put("timestamp", System.currentTimeMillis());

        db.insert(TABLE_ORDERS, null, values);
    }

    public Cursor getAllOrders() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ORDERS + " ORDER BY timestamp DESC", null);
    }

    // --- SESSION METHODS ---
    public void saveSession(String userId, String token) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_SESSIONS + " SET is_active = 0"); // Deactivate all

        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("token", token);
        values.put("is_active", 1);

        db.insert(TABLE_SESSIONS, null, values);
    }

    public Cursor getActiveSession() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_SESSIONS + " WHERE is_active = 1 LIMIT 1", null);
    }

    public Cursor getActiveUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS + " LIMIT 1", null);
    }
}
