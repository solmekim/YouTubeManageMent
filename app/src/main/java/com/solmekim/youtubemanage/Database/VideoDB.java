package com.solmekim.youtubemanage.Database;

import android.provider.BaseColumns;

public final class VideoDB {

    protected static final class VideoTabDB implements BaseColumns {

        protected static final String IndexNum = "IndexNum";
        protected static final String VideoTab = "VideoTab";
        protected static final String _TABLENAME = "VideoTab";
        protected static final String _CREATE = "create table if not exists " + _TABLENAME +
                "(" + IndexNum + " integer primary key autoincrement, "
                + VideoTab + " text not null );";
    }

    protected static final class VideoTabValueDB implements BaseColumns {

        protected static final String IndexNum = "IndexNum";
        protected static final String VideoTab = "VideoTab";
        protected static final String VideoTitle = "VideoTitle";
        protected static final String VideoViewCount ="VideoViewCount";
        protected static final String VideoDuration = "VideoDuration";
        protected static final String VideoUploadTime = "VideoUploadTime";
        protected static final String VideoUrl = "VideoUrl";
        protected static final String VideoID = "VideoID";
        protected static final String VideoDescription = "VideoDescription";

        protected static final String _TABLENAME = "VideoTabValue";
        protected static final String _CREATE = "create table if not exists " + _TABLENAME +
                "(" + IndexNum + " integer primary key autoincrement, "
                + VideoTab + " text not null , "
                + VideoTitle + " text not null , "
                + VideoViewCount + " integer not null , "
                + VideoDuration + " integer not null , "
                + VideoUploadTime + " text not null , "
                + VideoUrl + " text not null , "
                + VideoID + " text not null , "
                + VideoDescription + " text not null );";
    }
}
