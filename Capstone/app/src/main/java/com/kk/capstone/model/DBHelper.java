/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.kk.capstone.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "articles.db";

    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_WEATHER_TABLE =

                "CREATE TABLE " + Contract.Entry.TABLE_NAME + " (" +
                        Contract.Entry._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        Contract.Entry.COLUMN_CHANNEL       + " TEXT NOT NULL, "                 +

                        Contract.Entry.COLUMN_CHANNEL_KEY + " TEXT NOT NULL,"                  +

                        Contract.Entry.COLUMN_ARTICLE   + " TEXT NOT NULL, "                    +
                        Contract.Entry.COLUMN_DATE   + " TEXT NOT NULL "                    + ");";

        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Contract.Entry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
