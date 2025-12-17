package com.example.electricitybillcalc;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ElectricityBill.db";
    private static final int DATABASE_VERSION = 2;

    // Table name
    public static final String TABLE_NAME = "bills";

    // Column names
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_YEAR = "year";
    public static final String COLUMN_MONTH = "month";
    public static final String COLUMN_UNITS = "units";
    public static final String COLUMN_REBATE = "rebate";
    public static final String COLUMN_TOTAL_CHARGES = "total_charges";
    public static final String COLUMN_FINAL_COST = "final_cost";
    public static final String COLUMN_CREATED_AT = "created_at";

    // Create table SQL
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_YEAR + " INTEGER NOT NULL, " +
                    COLUMN_MONTH + " TEXT NOT NULL, " +
                    COLUMN_UNITS + " REAL NOT NULL, " +
                    COLUMN_REBATE + " REAL NOT NULL, " +
                    COLUMN_TOTAL_CHARGES + " REAL NOT NULL, " +
                    COLUMN_FINAL_COST + " REAL NOT NULL, " +
                    COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Version 2 adds the year column
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " +
                    COLUMN_YEAR + " INTEGER DEFAULT 2024");
        }
    }

    // Insert a new bill record
    public long insertBill(Bill bill) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_YEAR, bill.getYear());
        values.put(COLUMN_MONTH, bill.getMonth());
        values.put(COLUMN_UNITS, bill.getUnits());
        values.put(COLUMN_REBATE, bill.getRebate());
        values.put(COLUMN_TOTAL_CHARGES, bill.getTotalCharges());
        values.put(COLUMN_FINAL_COST, bill.getFinalCost());

        long id = db.insert(TABLE_NAME, null, values);
        db.close();

        return id;
    }

    // Get all bills
    public List<Bill> getAllBills() {
        List<Bill> billList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_CREATED_AT + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Bill bill = new Bill();
                bill.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                bill.setYear(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_YEAR)));
                bill.setMonth(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MONTH)));
                bill.setUnits(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_UNITS)));
                bill.setRebate(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_REBATE)));
                bill.setTotalCharges(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_CHARGES)));
                bill.setFinalCost(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_FINAL_COST)));

                billList.add(bill);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return billList;
    }

    // Get bill by ID
    public Bill getBillById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(id)});

        Bill bill = null;
        if (cursor.moveToFirst()) {
            bill = new Bill();
            bill.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            bill.setYear(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_YEAR)));
            bill.setMonth(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MONTH)));
            bill.setUnits(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_UNITS)));
            bill.setRebate(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_REBATE)));
            bill.setTotalCharges(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_CHARGES)));
            bill.setFinalCost(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_FINAL_COST)));
        }

        cursor.close();
        db.close();

        return bill;
    }

    // Delete all bills
    public int deleteAllBills() {
        SQLiteDatabase db = this.getWritableDatabase();
        int count = db.delete(TABLE_NAME, null, null);
        db.close();
        return count;
    }

    // Get bill count
    public int getBillCount() {
        String countQuery = "SELECT COUNT(*) FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }

}