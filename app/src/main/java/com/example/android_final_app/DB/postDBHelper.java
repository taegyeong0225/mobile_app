package com.example.android_final_app.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class postDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MOBILE_FINAL_PROJECT.db";
    private static final int DATABASE_VERSION = 3;

    public postDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_POSTS_TABLE = "CREATE TABLE " + postDB.TABLE_POST + " (" +
                postDB.COL_NO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                postDB.COL_TITLE + " TEXT, " +
                postDB.COL_CONTENT + " TEXT, " +
                postDB.COL_AUTHOR + " TEXT, " +
                postDB.COL_RESTAURANT + " TEXT, " +
                postDB.COL_RECOMMEND + " BOOLEAN DEFAULT 0)";
        db.execSQL(CREATE_POSTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + postDB.TABLE_POST);
        onCreate(db);
    }

    // 데이터 삽입 메서드
    public long addPost(String title, String content, String author, String restaurant, boolean recommend) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(postDB.COL_TITLE, title);
        values.put(postDB.COL_CONTENT, content);
        values.put(postDB.COL_AUTHOR, author);
        values.put(postDB.COL_RESTAURANT, restaurant);
        values.put(postDB.COL_RECOMMEND, recommend);
        return db.insert(postDB.TABLE_POST, null, values);
    }

    // 모든 포스트 가져오기
    public Cursor getAllPosts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(postDB.TABLE_POST, null, null, null, null, null, null);
    }

    // 카테고리별 포스트 가져오기
    public Cursor getPostsByCategory(String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = postDB.COL_RESTAURANT + " = ?";
        String[] selectionArgs = { category };
        return db.query(postDB.TABLE_POST, null, selection, selectionArgs, null, null, null);
    }

    // 추천이 가장 많은 식당 가져오기
    public String getTopRecommendedRestaurant() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + postDB.COL_RESTAURANT + ", COUNT(*) as count FROM " + postDB.TABLE_POST +
                " WHERE " + postDB.COL_RECOMMEND + " = 1 GROUP BY " + postDB.COL_RESTAURANT +
                " ORDER BY count DESC LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);

        String topRestaurant = "추천이 가장 많은 식당 정보 없음";
        if (cursor.moveToFirst()) {
            int colIndex = cursor.getColumnIndex(postDB.COL_RESTAURANT);
            if (colIndex != -1) {
                topRestaurant = cursor.getString(colIndex);
            } else {
                Log.e("postDBHelper", "Column " + postDB.COL_RESTAURANT + " not found in cursor");
            }
        }
        cursor.close();
        return topRestaurant;
    }

    // 리뷰가 가장 많은 식당 가져오기
    public String getMostReviewedRestaurant() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + postDB.COL_RESTAURANT + ", COUNT(*) as count FROM " + postDB.TABLE_POST +
                " GROUP BY " + postDB.COL_RESTAURANT + " ORDER BY count DESC LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);

        String mostReviewedRestaurant = "리뷰가 가장 많은 식당 정보 없음";
        if (cursor.moveToFirst()) {
            int colIndex = cursor.getColumnIndex(postDB.COL_RESTAURANT);
            if (colIndex != -1) {
                mostReviewedRestaurant = cursor.getString(colIndex);
            } else {
                Log.e("postDBHelper", "Column " + postDB.COL_RESTAURANT + " not found in cursor");
            }
        }
        cursor.close();
        return mostReviewedRestaurant;
    }

}
