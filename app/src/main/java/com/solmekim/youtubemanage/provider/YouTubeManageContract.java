package com.solmekim.youtubemanage.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.widget.VideoView;

import com.solmekim.youtubemanage.R;
import com.solmekim.youtubemanage.VideoTab.VideoTab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class YouTubeManageContract {

    public static final String AUTHORITY = "com.solmekim.youtubemanage";

    public static final String TABLE_NAME_VIDEOTAB = "VideoTab";
    public static final Uri CONTENT_URI_VIDEOTAB = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME_VIDEOTAB);

    public static final String TABLE_NAME_VIDEOTABVALUE = "VideoTabValue";
    public static final Uri CONTENT_URI_VIDEOTABVALUE = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME_VIDEOTABVALUE);

    public static final String IndexNum = "IndexNum";
    public static final String VideoTab = "VideoTab";
    public static final String VideoTitle = "VideoTitle";
    public static final String VideoViewCount ="VideoViewCount";
    public static final String VideoDuration = "VideoDuration";
    public static final String VideoUploadTime = "VideoUploadTime";
    public static final String VideoUrl = "VideoUrl";
    public static final String VideoID = "VideoID";
    public static final String VideoDescription = "VideoDescription";

    public static Uri insertVideoTabColumn(Context context, String videoTab) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(VideoTab, videoTab);
        return context.getContentResolver().insert(CONTENT_URI_VIDEOTAB, contentValues);
    }

    public static Uri insertVideoTabValueColumn(Context context,
            String videoTab, String videoTitle, int videoViewCount, int videoDuration,
            String videoUploadTime, String videoUrl, String videoID, String videoDescription) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(VideoTab, videoTab);
        contentValues.put(VideoTitle, videoTitle);
        contentValues.put(VideoViewCount, videoViewCount);
        contentValues.put(VideoDuration, videoDuration);
        contentValues.put(VideoUploadTime, videoUploadTime);
        contentValues.put(VideoUrl, videoUrl);
        contentValues.put(VideoID, videoID);
        contentValues.put(VideoDescription, videoDescription);
        return context.getContentResolver().insert(CONTENT_URI_VIDEOTABVALUE, contentValues);
    }

    public static Cursor selectVideoTabAllColumns(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(CONTENT_URI_VIDEOTAB, null, null, null, null);
        if (cursor != null) {
            return cursor;
        } else {
            return null;
        }
    }

    public static Cursor selectVideoTabValueAllColumns(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(CONTENT_URI_VIDEOTABVALUE, null, null, null, null);
        if (cursor != null) {
            return cursor;
        } else {
            return null;
        }
    }

    public static int updateVideoViewCountColumn(Context context, String videoTitle, String videoUploadTime, String videoUrl) {

        ContentResolver contentResolver = context.getContentResolver();
        String whereClause = "VideoTitle =? AND VideoUploadTime =? AND VideoUrl =? ";

        ContentValues contentValues = new ContentValues();
        contentValues.put(VideoViewCount, getViewCount(context, videoTitle, videoUploadTime, videoUrl, whereClause) +1);

        int retValue = -1;
        try {
            retValue = contentResolver.update(CONTENT_URI_VIDEOTABVALUE, contentValues, whereClause, new String[]{videoTitle, videoUploadTime, videoUrl});
        } catch (IllegalArgumentException | SecurityException e) {
            e.printStackTrace();
        }
        return retValue;
    }

    private static int getViewCount(Context context, String videoTitle, String videoUploadTime, String videoUrl, String whereClause) {
        int videoViewCount = -1;

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(CONTENT_URI_VIDEOTABVALUE,new String[]{VideoViewCount}, whereClause, new String[]{videoTitle, videoUploadTime, videoUrl}, null, null);

        if (cursor != null && cursor.getCount() == 1 && cursor.moveToLast()) {
            videoViewCount = cursor.getInt(cursor.getColumnIndex(VideoViewCount));
        }
        return videoViewCount;
    }

    public static int updateVideoInfo(Context context, String videoTab, String videoUploadTime, String videoID, String videoTitle, String videoDescription) {
        ContentResolver contentResolver = context.getContentResolver();

        ContentValues contentValues = new ContentValues();
        contentValues.put(VideoTitle, videoTitle);
        contentValues.put(VideoDescription, videoDescription);

        String whereClause = "VideoTab =? AND VideoUploadTime =? AND VideoID =? ";
        int retValue = -1;
        try {
            retValue = contentResolver.update(CONTENT_URI_VIDEOTABVALUE, contentValues, whereClause, new String[]{videoTab, videoUploadTime, videoID});
        } catch (IllegalArgumentException | SecurityException e) {
            e.printStackTrace();
        }

        return retValue;
    }

    public static ArrayList<String> getVideoTabNameList(Context context) {
        ArrayList<String> videoNameList = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();

        Cursor cursor = contentResolver.query(CONTENT_URI_VIDEOTAB, new String[]{VideoTab}, null, null,null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                videoNameList.add(cursor.getString(cursor.getColumnIndex(VideoTab)));
            }
            cursor.close();
        }
        return videoNameList;
    }

    public static ArrayList<String> getTotalVideoTabNameList(Context context) {
        ArrayList<String> totalTabNameList = new ArrayList<>();
        totalTabNameList.add(context.getString(R.string.tab_videokind));
        for(String videoTabName : getVideoTabNameList(context)) {
            totalTabNameList.add(videoTabName);
        }
        return totalTabNameList;
    }

    public static int deleteVideoTab(Context context, String videoTab) {

        ContentResolver contentResolver = context.getContentResolver();

        try {
            Cursor cursor = contentResolver.query(CONTENT_URI_VIDEOTABVALUE, new String[]{VideoTab}, "VideoTab = ?", new String[]{videoTab}, null, null);

            if (cursor.getCount() > 0) {
                return -1;
            } else {
                return contentResolver.delete(CONTENT_URI_VIDEOTAB, "VideoTab = ?", new String[]{videoTab});
            }
        } catch (IllegalArgumentException | SecurityException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int deleteYoutubeVideo(Context context, List<HashMap<String,String>> videoTabInfoList) {
        ContentResolver contentResolver = context.getContentResolver();
        String whereClause = "VideoTab =? AND VideoViewCount =? AND VideoUploadTime = ? AND VideoUrl =?";
        for (int i = 0; i < videoTabInfoList.size(); i++) {
            if (contentResolver.delete(CONTENT_URI_VIDEOTABVALUE, whereClause
                    , new String[]{videoTabInfoList.get(i).get(VideoTab), videoTabInfoList.get(i).get(VideoViewCount), videoTabInfoList.get(i).get(VideoUploadTime), videoTabInfoList.get(i).get(VideoUrl)}) <0) {
                return -1;
            }
        }
        return 1;
    }
}
