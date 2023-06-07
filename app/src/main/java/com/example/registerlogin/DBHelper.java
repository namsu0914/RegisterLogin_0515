package com.example.registerlogin;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "sk.db";
    private static final int DATABASE_VERSION = 1;

    // 테이블 생성 쿼리
    private static final String CREATE_TABLE_USER = "CREATE TABLE sk (userID string, sk string)";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 데이터베이스 버전이 변경되었을 때 필요한 업그레이드 작업을 수행할 수 있습니다.
        // 여기서는 간단히 테이블을 삭제하고 다시 생성합니다.
        db.execSQL("DROP TABLE IF EXISTS sk");
        onCreate(db);
    }
}