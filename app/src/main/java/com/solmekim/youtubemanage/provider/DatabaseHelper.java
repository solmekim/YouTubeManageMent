package com.solmekim.youtubemanage.provider;

import android.content.ContentResolver;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_FILE_NAME = "VideoTab.db";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_VIDEOTAB_TABLE = "CREATE TABLE IF NOT EXISTS "
            + YouTubeManageContract.TABLE_NAME_VIDEOTAB + " ("
            + YouTubeManageContract.IndexNum + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + YouTubeManageContract.VideoTab + " TEXT NOT NULL );";

    private static final String CREATE_VIDEOTABVALUE_TABLE = "CREATE TABLE IF NOT EXISTS "
            + YouTubeManageContract.TABLE_NAME_VIDEOTABVALUE + " ("
            + YouTubeManageContract.IndexNum + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + YouTubeManageContract.VideoTab + " TEXT NOT NULL , "
            + YouTubeManageContract.VideoTitle + " TEXT NOT NULL , "
            + YouTubeManageContract.VideoViewCount + " INTEGER NOT NULL , "
            + YouTubeManageContract.VideoDuration + " INTEGER NOT NULL , "
            + YouTubeManageContract.VideoUploadTime + " TEXT NOT NULL , "
            + YouTubeManageContract.VideoUrl + " TEXT NOT NULL , "
            + YouTubeManageContract.VideoID + " TEXT NOT NULL ,"
            + YouTubeManageContract.VideoDescription + " TEXT NOT NULL );";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        db.execSQL(CREATE_VIDEOTAB_TABLE);
        db.execSQL(CREATE_VIDEOTABVALUE_TABLE);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        int version = oldVersion;

        if (version < DATABASE_VERSION) {
            db.beginTransaction();
            db.execSQL(CREATE_VIDEOTAB_TABLE);
            db.execSQL(CREATE_VIDEOTABVALUE_TABLE);
            db.setTransactionSuccessful();
            db.endTransaction();
            version = DATABASE_VERSION;
        }

        if (version != DATABASE_VERSION) {
            db.beginTransaction();
            db.execSQL("DROP TABLE IF EXISTS " + YouTubeManageContract.TABLE_NAME_VIDEOTAB);
            db.execSQL("DROP TABLE IF EXISTS " + YouTubeManageContract.TABLE_NAME_VIDEOTABVALUE);
            db.setTransactionSuccessful();
            db.endTransaction();
            onCreate(db);
        }
    }
}
