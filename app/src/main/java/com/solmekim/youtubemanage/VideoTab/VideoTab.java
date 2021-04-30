package com.solmekim.youtubemanage.VideoTab;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoTab implements Parcelable {

    String VideoTab;
    String VideoTitle;
    int VideoViewCount;
    int VideoDuration;
    String VideoUploadTime;
    String VideoUrl;
    String VideoDescription;
    boolean isChecked;

    public VideoTab(String VideoTab, String VideoTitle, int VideoViewCount, int VideoDuration, String VideoUploadTime, String VideoUrl, String VideoDescription) {
        this.VideoTab = VideoTab;
        this.VideoTitle = VideoTitle;
        this.VideoViewCount = VideoViewCount;
        this.VideoDuration = VideoDuration;
        this.VideoUploadTime = VideoUploadTime;
        this.VideoUrl = VideoUrl;
        this.VideoDescription = VideoDescription;
        this.isChecked = false;
    }


    protected VideoTab(Parcel in) {
        VideoTab = in.readString();
        VideoTitle = in.readString();
        VideoViewCount = in.readInt();
        VideoDuration = in.readInt();
        VideoUploadTime = in.readString();
        VideoUrl = in.readString();
        VideoDescription = in.readString();
        isChecked = false;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(VideoTab);
        dest.writeString(VideoTitle);
        dest.writeInt(VideoViewCount);
        dest.writeInt(VideoDuration);
        dest.writeString(VideoUploadTime);
        dest.writeString(VideoUrl);
        dest.writeString(VideoDescription);
    }

    public static final Creator<VideoTab> CREATOR = new Creator<VideoTab>() {
        @Override
        public VideoTab createFromParcel(Parcel in) {
            return new VideoTab(in);
        }

        @Override
        public VideoTab[] newArray(int size) {
            return new VideoTab[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public String getVideoTab() {
        return VideoTab;
    }

    public String getVideoTitle() {
        return VideoTitle;
    }

    public int getVideoViewCount() {
        return VideoViewCount;
    }

    public int getVideoDuration() {
        return VideoDuration;
    }

    public Boolean getCheckedValue() {
        return isChecked;
    }

    public String getVideoUploadTime() {
        return VideoUploadTime;
    }

    public String getVideoUrl() {
        return VideoUrl;
    }

    public String getVideoDescription() {
        return VideoDescription;
    }

    public void setVideoViewCount(int videoViewCount) {
        VideoViewCount = videoViewCount;
    }

    public void setCheckedValue(boolean checked) {
        isChecked = checked;
    }

    public void setVideoTitle(String videoTitle) {
        VideoTitle = videoTitle;
    }

    public void setVideoDescription(String description) {
        VideoDescription = description;
    }
}
