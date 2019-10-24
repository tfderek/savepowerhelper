package com.mfox.savepower.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zhangyuanlu on 17/5/1.
 */

public class DBOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME="packageInfo.db";
    private static final int DATABASE_VERSION=1;


    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE packageInfo (_id integer primary key autoincrement,packageName text not null,checked varchar(1))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS packageInfo");
    }
}
