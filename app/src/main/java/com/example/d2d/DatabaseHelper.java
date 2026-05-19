package com.example.d2d;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "D2D_Local.db";
    private static final int DATABASE_VERSION = 5;

    private static final String TABLE_USERS = "users";
    private static final String TABLE_ORDERS = "orders";
    private static final String TABLE_SESSIONS = "sessions";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id TEXT, " +
                "role TEXT, " +
                "email TEXT, " +
                "name TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_ORDERS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "order_id TEXT UNIQUE, " +
                "restaurant_name TEXT, " +
                "status TEXT, " +
                "price TEXT, " +
                "customer_name TEXT, " +
                "customer_id TEXT, " +
                "timestamp LONG)");

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

    public void saveUser(String userId, String role, String email, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, null, null);
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("role", role);
        values.put("email", email);
        values.put("name", name);
        db.insert(TABLE_USERS, null, values);
    }

    public void saveOrder(String orderId, String restaurantName, String status, String price) {
        saveOrder(orderId, restaurantName, status, price, "Customer", "unknown");
    }

    public void saveOrder(String orderId, String restaurantName, String status, String price, String customerName, String customerId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("order_id", orderId);
        values.put("restaurant_name", restaurantName);
        values.put("status", status);
        values.put("price", price);
        values.put("customer_name", customerName);
        values.put("customer_id", customerId);
        values.put("timestamp", System.currentTimeMillis());
        db.insertWithOnConflict(TABLE_ORDERS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public Cursor getAllOrders() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT order_id, restaurant_name, status, price, customer_name, customer_id, timestamp FROM " + TABLE_ORDERS + " ORDER BY timestamp DESC", null);
    }

    public Cursor getCompletedOrders() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT order_id, restaurant_name, status, customer_name, customer_id FROM " + TABLE_ORDERS + " WHERE status = 'Collected' ORDER BY timestamp DESC", null);
    }

    public Cursor getActiveOrder() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT order_id, restaurant_name, status FROM " + TABLE_ORDERS + " WHERE status != 'Collected' AND status != 'pending_confirmation' ORDER BY timestamp DESC LIMIT 1", null);
    }

    public Cursor getPendingConfirmationOrder() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT order_id, restaurant_name, status FROM " + TABLE_ORDERS + " WHERE status = 'pending_confirmation' ORDER BY timestamp DESC LIMIT 1", null);
    }

    public void updateOrderStatus(String orderId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", newStatus);
        db.update(TABLE_ORDERS, values, "order_id = ?", new String[]{orderId});
    }

    public void saveSession(String userId, String token) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_SESSIONS + " SET is_active = 0");
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

    public void clearLocalSession() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SESSIONS, null, null);
        db.delete(TABLE_USERS, null, null);
    }

    public void markActiveOrdersAsCollected() {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", "Collected");
        db.update(TABLE_ORDERS, values, "status != 'Collected'", null);
    }

    public Cursor getAllActiveOrders() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT order_id, restaurant_name, status, customer_name, customer_id FROM " + TABLE_ORDERS + " WHERE status != 'Collected' ORDER BY timestamp DESC", null);
    }

    public void updateOrderStatusLocal(String orderId, String status) {
        updateOrderStatus(orderId, status);
    }
}