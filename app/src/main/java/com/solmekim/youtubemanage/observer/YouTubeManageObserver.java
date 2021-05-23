package com.solmekim.youtubemanage.observer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;

import com.solmekim.youtubemanage.provider.YouTubeManageContract;

public class YouTubeManageObserver extends ContentObserver {

    public YouTubeManageObserver(Handler handler) {
        super(handler);
    }

    public void registerObserver(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.registerContentObserver(
                YouTubeManageContract.CONTENT_URI_VIDEOTABVALUE, false, this);
    }

    public void unregisterObserver(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        contentResolver.unregisterContentObserver(this);
    }
}
