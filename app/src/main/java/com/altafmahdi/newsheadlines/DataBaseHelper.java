/*
 * Copyright (c) 2018 Altaf-Mahdi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.altafmahdi.newsheadlines;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "Places.db";
    private final static String TABLE_NAME = "places";
    private static final String COL_0 = "ID";
    private static final String COL_1 = "LINK";
    private static final String COL_2 = "IMAGE";
    private static final String COL_3 = "TITLE";
    private static final String COL_4 = "DESCRIPTION";

    DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE_NAME+"(ID INTEGER PRIMARY KEY " +
                "AUTOINCREMENT,LINK TEXT,IMAGE TEXT,TITLE TEXT,DESCRIPTION TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_NAME);
        onCreate(db);
    }

    public void insertData(String link, String image, String title, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_1, link);
        cv.put(COL_2, image);
        cv.put(COL_3, title);
        cv.put(COL_4, description);
        db.insert(TABLE_NAME, null, cv);
        cv.clear();
        db.close();
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM "+TABLE_NAME, null);
    }

    public Cursor getData(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM "+TABLE_NAME+" WHERE ID='"+id+"'";
        return db.rawQuery(query,null);
    }

    public void deleteDataBase() {
        SQLiteDatabase db = this.getWritableDatabase();
        onUpgrade(db, 1, 1);
        db.close();
    }

    public void deleteEntry(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "ID=?", new String[]{id});
        db.close();
    }
}
