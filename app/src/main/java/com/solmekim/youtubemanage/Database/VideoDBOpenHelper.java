package com.solmekim.youtubemanage.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.solmekim.youtubemanage.R;

import java.util.HashMap;
import java.util.List;

import static com.solmekim.youtubemanage.Database.VideoDB.VideoTabValueDB.VideoViewCount;

public class VideoDBOpenHelper {

    private static final String databaseName = "VideoTab.db";
    private static final int DATABASE_VERSION = 1;
    private static SQLiteDatabase sqLiteDatabase;
    private DatabaseHelper databaseHelper;
    private Context context;

    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(VideoDB.VideoTabDB._CREATE);
            db.execSQL(VideoDB.VideoTabValueDB._CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + VideoDB.VideoTabDB._TABLENAME);
            db.execSQL("DROP TABLE IF EXISTS " + VideoDB.VideoTabValueDB._TABLENAME);
            onCreate(db);

        }
    }

    public VideoDBOpenHelper(Context context) {
        this.context = context;
    }

    public boolean isCheckDBOpen() {
        if(databaseHelper != null && sqLiteDatabase != null) {
            return true;
        } else {
            return false;
        }
    }

    public VideoDBOpenHelper open() throws SQLException {
        databaseHelper = new DatabaseHelper(context, databaseName, null, DATABASE_VERSION);
        sqLiteDatabase = databaseHelper.getWritableDatabase();
        return this;
    }

    public void create() {
        databaseHelper.onCreate(sqLiteDatabase);
    }

    public long insertVideoTabColumn(String videoTab) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(VideoDB.VideoTabDB.VideoTab, videoTab);
        return sqLiteDatabase.insert(VideoDB.VideoTabDB._TABLENAME, null, contentValues);
    }

    public long insertVideoTabValueColumn(String VideoTab, String VideoTitle, int VideoViewCount, int VideoDuration,
                                          String VideoUploadTime, String VideoUrl, String VideoID, String VideoDescription) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(VideoDB.VideoTabValueDB.VideoTab, VideoTab);
        contentValues.put(VideoDB.VideoTabValueDB.VideoTitle, VideoTitle);
        contentValues.put(VideoDB.VideoTabValueDB.VideoViewCount, VideoViewCount);
        contentValues.put(VideoDB.VideoTabValueDB.VideoDuration, VideoDuration);
        contentValues.put(VideoDB.VideoTabValueDB.VideoUploadTime, VideoUploadTime);
        contentValues.put(VideoDB.VideoTabValueDB.VideoUrl, VideoUrl);
        contentValues.put(VideoDB.VideoTabValueDB.VideoID, VideoID);
        contentValues.put(VideoDB.VideoTabValueDB.VideoDescription, VideoDescription);

        return sqLiteDatabase.insert(VideoDB.VideoTabValueDB._TABLENAME, null, contentValues);
    }

    public Cursor selectVideoTabAllColumns() {
        return sqLiteDatabase.query(VideoDB.VideoTabDB._TABLENAME, null, null, null, null, null, null);
    }

    public Cursor selectVideoTabValueAllColumns() {
        return sqLiteDatabase.query(VideoDB.VideoTabValueDB._TABLENAME, null, null, null, null, null, null);
    }

    public int updateVideoTabColumn(long id, String VideoTab) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(VideoDB.VideoTabDB.VideoTab, VideoTab);
        return sqLiteDatabase.update(VideoDB.VideoTabDB._TABLENAME, contentValues, "_id=" + id, null);
    }

    public void updateVideoViewCountColumn(String VideoTitle, String VideoUploadTime, String VideoUrl) {

        sqLiteDatabase.execSQL("UPDATE " + VideoDB.VideoTabValueDB._TABLENAME + " SET " + VideoViewCount + " = " + VideoViewCount + " +1 "
                + "WHERE " + VideoDB.VideoTabValueDB.VideoTitle + " = " + "'"+VideoTitle +"'" + " AND " + VideoDB.VideoTabValueDB.VideoUploadTime
                + " = " + "'" + VideoUploadTime + "'" + " AND " + VideoDB.VideoTabValueDB.VideoUrl + " = " + "'" + VideoUrl + "';");
    }

    public int updateVideoInfo(String VideoTab, String VideoUploadTime, String VideoID, String VideoTitle, String VideoDescription) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(VideoDB.VideoTabValueDB.VideoTitle, VideoTitle);
        contentValues.put(VideoDB.VideoTabValueDB.VideoDescription, VideoDescription);

        String whereClause = "VideoTab =? AND VideoUploadTime =? AND VideoID =? ";
        return sqLiteDatabase.update(VideoDB.VideoTabValueDB._TABLENAME, contentValues, whereClause, new String[]{VideoTab, VideoUploadTime, VideoID});
    }

    public int deleteVideoTab(String VideoTab) {

        Cursor cursor = sqLiteDatabase.query(VideoDB.VideoTabValueDB._TABLENAME, new String[]{"VideoTab"}, "VideoTab = ?", new String[]{VideoTab}, null, null, null);

        if(cursor.getCount() >0) {
            return -1;
        } else {
            return sqLiteDatabase.delete(VideoDB.VideoTabDB._TABLENAME, "VideoTab = ?", new String[]{VideoTab});
        }
    }

    public int deleteYoutubeVideo(List<HashMap<String,String>> videoTabInfoList) {
        for(int i=0; i <videoTabInfoList.size(); i++) {
            if(sqLiteDatabase.delete(VideoDB.VideoTabValueDB._TABLENAME, "VideoTab =? AND VideoViewCount =? AND VideoUploadTime = ? AND VideoUrl =?"
                    , new String[]{videoTabInfoList.get(i).get(context.getResources().getString(R.string.VideoTab)), videoTabInfoList.get(i).get(context.getResources().getString(R.string.VideoViewCount)),
                            videoTabInfoList.get(i).get(context.getResources().getString(R.string.VideoUploadTime)), videoTabInfoList.get(i).get(context.getResources().getString(R.string.VideoUrl))}) <0) {
                return -1;
            }
        }
        return 1;

    }

    public void close() {
        databaseHelper = null;
        sqLiteDatabase.close();
    }
}
