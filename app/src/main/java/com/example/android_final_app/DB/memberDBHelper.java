package com.example.android_final_app.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class memberDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "memberDBHelper";
    private Context context;

    // 데이터베이스 버전 및 이름
    public static final int DATABASE_VERSION = 3; // 버전을 3으로 변경
    public static final String DATABASE_NAME = "MOBILE_FINAL_PROJECT.db";

    // 테이블 생성 SQL 문
    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + memberDB.TABLE_MEMBER + " (" +
                    memberDB.COL_ID + " TEXT PRIMARY KEY, " +
                    memberDB.COL_PASSWORD + " TEXT, " +
                    memberDB.COL_EMAIL + " TEXT, " +
                    memberDB.COL_NAME + " TEXT, " +
                    memberDB.COL_IS_MANAGER + " INTEGER DEFAULT 0)"; // 기본값 0 추가

    // 테이블 삭제 SQL 문
    private static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + memberDB.TABLE_MEMBER;

    // 생성자
    public memberDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // 데이터베이스가 처음 생성될 때 호출되는 메서드
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 테이블 생성
        db.execSQL(SQL_CREATE_TABLE);
        // 기본 관리자 계정 삽입
        insertDefaultAdmin(db);
    }

    // 데이터베이스 업그레이드 시 호출되는 메서드
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 기존 테이블 삭제
        db.execSQL(SQL_DELETE_TABLE);
        // 테이블 다시 생성
        onCreate(db);
    }

    // 기본 관리자 계정을 삽입하는 메서드
    private void insertDefaultAdmin(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(memberDB.COL_NAME, "admin"); // 관리자 이름
        values.put(memberDB.COL_EMAIL, "admin@admin.com"); // 관리자 이메일
        values.put(memberDB.COL_ID, "admin"); // 관리자 ID
        values.put(memberDB.COL_PASSWORD, "admin"); // 관리자 비밀번호
        values.put(memberDB.COL_IS_MANAGER, 1); // 관리자 권한 (정수형으로 삽입)

        // 데이터베이스에 관리자 계정 삽입
        long newRowId = db.insert(memberDB.TABLE_MEMBER, null, values);

        Log.d(TAG, "Admin row ID: " + newRowId);
        if (newRowId != -1) {
            Log.d(TAG, "Admin member inserted with ID: " + newRowId);
        } else {
            Log.e(TAG, "Failed to insert admin member");
        }
    }

    // 새로운 회원을 추가하는 메서드
    public long addMember(String name, String email, String id, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(memberDB.COL_NAME, name);
        values.put(memberDB.COL_EMAIL, email);
        values.put(memberDB.COL_ID, id);
        values.put(memberDB.COL_PASSWORD, password);

        // 데이터베이스에 회원 정보 삽입
        long newRowId = db.insert(memberDB.TABLE_MEMBER, null, values);

        Log.d(TAG, "New row ID: " + newRowId);
        if (newRowId != -1) {
            // 회원 추가 성공
            Log.d(TAG, "New member inserted with ID: " + newRowId);
            if (context != null) {
                Toast.makeText(context, "회원 가입 성공", Toast.LENGTH_SHORT).show();
            }
        } else {
            // 회원 추가 실패
            Log.e(TAG, "Failed to insert new member");
            if (context != null) {
                Toast.makeText(context, "회원 가입 실패", Toast.LENGTH_SHORT).show();
            }
        }
        return newRowId;
    }

    // 회원 정보를 조회하는 메서드
    public Cursor getMember(String id, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                memberDB.COL_NAME,
                memberDB.COL_EMAIL,
                memberDB.COL_ID,
                memberDB.COL_PASSWORD,
                memberDB.COL_IS_MANAGER
        };

        String selection = memberDB.COL_ID + " = ? AND " + memberDB.COL_PASSWORD + " = ?";
        String[] selectionArgs = {id, password};

        // 데이터베이스에서 회원 정보 조회
        Cursor cursor = db.query(
                memberDB.TABLE_MEMBER,   // 테이블 이름
                projection,              // 반환할 컬럼 목록
                selection,               // WHERE 절
                selectionArgs,           // WHERE 절 인자
                null,                    // GROUP BY 절
                null,                    // HAVING 절
                null                     // ORDER BY 절
        );
        return cursor;
    }

    // ID 중복 여부 확인 메서드
    public boolean isUserIdExists(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {memberDB.COL_ID};
        String selection = memberDB.COL_ID + " = ?";
        String[] selectionArgs = { userId };

        Log.d(TAG, "Query: SELECT " + memberDB.COL_ID + " FROM " + memberDB.TABLE_MEMBER + " WHERE " + memberDB.COL_ID + " = '" + userId + "'");

        Cursor cursor = db.query(memberDB.TABLE_MEMBER, columns, selection, selectionArgs, null, null, null);
        boolean exists = (cursor.getCount() > 0);

        Log.d(TAG, "Cursor count: " + cursor.getCount());

        cursor.close();
        return exists;
    }

    // 데이터베이스가 이미 존재하는 경우 기본 관리자 계정을 삽입하는 메서드
    public void insertAdminIfNotExists() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + memberDB.COL_ID + " FROM " + memberDB.TABLE_MEMBER + " WHERE " + memberDB.COL_ID + " = 'admin'";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() == 0) {
            insertDefaultAdmin(db);
        }
        cursor.close();
    }
}
