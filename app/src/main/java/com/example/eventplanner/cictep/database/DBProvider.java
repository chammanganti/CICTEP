package com.example.eventplanner.cictep.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.eventplanner.cictep.database.DBInfo.TableUser;

public class DBProvider {

    public static long
    insertUser(DBHelper dbHelper, String firstname, String lastname,
               String username, String password, String userType, String isloggedin) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(TableUser.FIRSTNAME, firstname);
        contentValues.put(TableUser.LASTNAME, lastname);
        contentValues.put(TableUser.USERNAME, username);
        contentValues.put(TableUser.PASSWORD, password);
        contentValues.put(TableUser.USER_TYPE, userType);
        contentValues.put(TableUser.ISLOGGEDIN, isloggedin);

        db.beginTransaction();
        long result = db.insert(TableUser.TABLE_NAME, null, contentValues);
        db.setTransactionSuccessful();
        db.endTransaction();

        return result;
    }

    public static Cursor fetchUser(DBHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TableUser.TABLE_NAME, null);
    }

    public static
    Cursor fetchUser(DBHelper dbHelper, String[] args, String whereArg, String whereVal) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String arguments = "";

        if ("*".equals(args[0]) && 1 == args.length) {
            arguments = "*";
        } else {
            for (int i = 0; i < args.length; i++) {
                if (i == args.length - 1) {
                    arguments += args[i];
                } else {
                    arguments += args[i] + ", ";
                }
            }
        }

        String query = "SELECT " + arguments + " FROM " + TableUser.TABLE_NAME +
                " WHERE " + whereArg + "='" + whereVal + "'";

        db.beginTransaction();
        Cursor cursor = db.rawQuery(query, null);
        db.setTransactionSuccessful();
        db.endTransaction();

        return cursor;
    }

    public static long
    updateUser(DBHelper dbHelper, String column, String value, String whereCol, String whereVal) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(column, value);

        db.beginTransaction();
        long result = db.update(TableUser.TABLE_NAME, contentValues, whereCol + "=?",
                new String[] {whereVal});
        db.setTransactionSuccessful();
        db.endTransaction();

        return result;
    }
}
