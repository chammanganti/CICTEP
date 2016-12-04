package com.example.eventplanner.cictep.database;

import android.provider.BaseColumns;

public abstract class DBInfo {

    public DBInfo() {}

    public static abstract class TableUser implements BaseColumns {
        static final String TABLE_NAME = "tbl_user";
        static final String FIRSTNAME = "user_firstname";
        static final String LASTNAME = "user_lastname";
        public static final String USERNAME = "user_username";
        static final String PASSWORD = "user_password";
        public static final String USER_TYPE = "user_type";
        public static final String ISLOGGEDIN = "user_isloggedin";
    }

    static final String CREATE_TBL_USER =
            "CREATE TABLE " + TableUser.TABLE_NAME + " (" +
                    TableUser._ID + " INTEGER PRIMARY KEY, " +
                    TableUser.FIRSTNAME + " TEXT, " +
                    TableUser.LASTNAME + " TEXT, " +
                    TableUser.USERNAME + " TEXT, " +
                    TableUser.PASSWORD + " TEXT, " +
                    TableUser.USER_TYPE + " INTEGER, " +
                    TableUser.ISLOGGEDIN + " INTEGER" +
                    ");";

    static final String DROP_TBL_USER =
            "DROP TABLE IF EXISTS " + TableUser.TABLE_NAME;
}
