package com.example.w3d4_as1.entities;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDatabase extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "USERS";
    private static final String COLUMN_1 = "SEED";
    private static final String COLUMN_2 = "NAME";

    private static final String SQL_CREATE = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME
            +" ( "
            +COLUMN_1+"VARCHAR PRIMARY KEY NOT NULL, "
            +COLUMN_2+"VARCHAR NOT NULL "
            +" ) ";


    private static final String SQL_DROP = "DROP TABLE IF EXISTS "+TABLE_NAME;


    public UserDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public UserDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    public static String getTableName() {
        return TABLE_NAME;
    }

    public static String getColumn1() {
        return COLUMN_1;
    }

    public static String getColumn2() {
        return COLUMN_2;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP);
        db.execSQL(SQL_CREATE);
    }
}
