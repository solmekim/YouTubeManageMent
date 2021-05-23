package com.solmekim.youtubemanage.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class YouTubeManageProvider extends ContentProvider {
    private static final String TAG = YouTubeManageProvider.class.getSimpleName();

    DatabaseHelper mDatabaseHelper = null;

    // definitions for UriMatcher
    private static final int ID_VIDEOTAB = 2;
    private static final int ID_VIDEOTABVALUE = 3;

    public static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(YouTubeManageContract.AUTHORITY, YouTubeManageContract.TABLE_NAME_VIDEOTAB, ID_VIDEOTAB);
        uriMatcher.addURI(YouTubeManageContract.AUTHORITY, YouTubeManageContract.TABLE_NAME_VIDEOTABVALUE, ID_VIDEOTABVALUE);
    }

    public YouTubeManageProvider() {
    }

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        int id = uriMatcher.match(uri);
        long rowId = 0;

        switch (id) {
            case ID_VIDEOTAB:
                rowId = db.insert(YouTubeManageContract.TABLE_NAME_VIDEOTAB, null, values);
                break;
            case ID_VIDEOTABVALUE:
                rowId = db.insert(YouTubeManageContract.TABLE_NAME_VIDEOTABVALUE, null, values);
                break;
            default:
                Log.e(TAG, "Insert uri error : uri = " + uri);
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (rowId > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            return ContentUris.withAppendedId(uri, rowId);
        }
        throw new SQLException("Failed to insert row into: " + uri);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        int id = uriMatcher.match(uri);
        int numUpdated;

        switch (id) {
            case ID_VIDEOTAB:
                numUpdated = db.update(YouTubeManageContract.TABLE_NAME_VIDEOTAB, values, selection, selectionArgs);
                break;
            case ID_VIDEOTABVALUE:
                numUpdated = db.update(YouTubeManageContract.TABLE_NAME_VIDEOTABVALUE, values, selection, selectionArgs);
                break;
            default:
                Log.e(TAG, "Update uri error : uri = " + uri);
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return numUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        int id = uriMatcher.match(uri);
        int numDeleted;

        switch (id) {
            case ID_VIDEOTAB:
                numDeleted = db.delete(YouTubeManageContract.TABLE_NAME_VIDEOTAB, selection, selectionArgs);
                break;
            case ID_VIDEOTABVALUE:
                numDeleted = db.delete(YouTubeManageContract.TABLE_NAME_VIDEOTABVALUE, selection, selectionArgs);
                break;
            default:
                Log.e(TAG, "Delete uri error : uri = " + uri);
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return numDeleted;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        int id = uriMatcher.match(uri);

        Cursor cursor = null;

        switch (id) {
            case ID_VIDEOTAB:
                cursor = db.query(YouTubeManageContract.TABLE_NAME_VIDEOTAB, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ID_VIDEOTABVALUE:
                cursor = db.query(YouTubeManageContract.TABLE_NAME_VIDEOTABVALUE, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                Log.e(TAG, "Query uri error : uri = " + uri);
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        int id = uriMatcher.match(uri);
        switch (id) {
            case ID_VIDEOTAB:
                return "vnd.android.cursor.dir/" + YouTubeManageContract.AUTHORITY + "."
                        + YouTubeManageContract.TABLE_NAME_VIDEOTAB;
            case ID_VIDEOTABVALUE:
                return "vnd.android.cursor.dir/" + YouTubeManageContract.AUTHORITY + "."
                        + YouTubeManageContract.TABLE_NAME_VIDEOTABVALUE;
            default:
                Log.e(TAG, "getType uri error: uri = " + uri);
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }
}