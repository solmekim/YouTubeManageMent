package com.solmekim.youtubemanage;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.solmekim.youtubemanage.VideoTab.VideoTab;
import com.solmekim.youtubemanage.VideoTab.VideoTabFragment;
import com.solmekim.youtubemanage.VideoType.VideoTypeListFragment;
import com.solmekim.youtubemanage.observer.YouTubeManageObserver;
import com.solmekim.youtubemanage.provider.YouTubeManageContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewPagerFragmentAdapter extends FragmentStatePagerAdapter {

    private Context context;
    private Activity activity;

    private ArrayList<String> videoNameList;
    private ArrayList<String> totalTabNameList;
    private List<HashMap<String, ArrayList<VideoTab>>> totalVideoTabList;

    private YouTubeManageObserver mYoutubeManageObserver;

    public ViewPagerFragmentAdapter(FragmentManager fm, Context context, ArrayList<String> totalTabNameList,
                                    ArrayList<String> videoNameList,
                                    List<HashMap<String, ArrayList<VideoTab>>> totalVideoTabList, Activity activity) {
        super(fm);
        this.context = context;
        this.totalTabNameList = totalTabNameList;
        this.videoNameList = videoNameList;
        this.totalVideoTabList = totalVideoTabList;
        this.activity = activity;
        mYoutubeManageObserver = new YouTubeManageObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);

                Cursor cursor = YouTubeManageContract.selectVideoTabValueAllColumns(context);

                if(cursor.moveToLast()) {

                    String VideoTab = cursor.getString(cursor.getColumnIndex(YouTubeManageContract.VideoTab));
                    String VideoTitle = cursor.getString(cursor.getColumnIndex(YouTubeManageContract.VideoTitle));
                    int VideoViewCount = cursor.getInt(cursor.getColumnIndex(YouTubeManageContract.VideoViewCount));
                    String VideoUploadTime = cursor.getString(cursor.getColumnIndex(YouTubeManageContract.VideoUploadTime));
                    String VideoUrl = cursor.getString(cursor.getColumnIndex(YouTubeManageContract.VideoUrl));
                    String VideoDescription = cursor.getString(cursor.getColumnIndex(YouTubeManageContract.VideoDescription));
                    int VideoDuration = cursor.getInt(cursor.getColumnIndex(YouTubeManageContract.VideoDuration));

                    VideoTab videoTab = new VideoTab(VideoTab, VideoTitle, VideoViewCount, VideoDuration, VideoUploadTime, VideoUrl, VideoDescription);

                    for (int i = 0; i < videoNameList.size(); i++) {
                        if (videoNameList.get(i).equals(VideoTab)) {
                            totalVideoTabList.get(i).get(videoNameList.get(i)).add(videoTab);
                        }
                    }
                }
                notifyDataSetChanged();
            }
        };
        mYoutubeManageObserver.registerObserver(context);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        String value = (String) getPageTitle(position);

        if(value.equals(context.getResources().getString(R.string.tab_videokind))) {
            return VideoTypeListFragment.newInstance(videoNameList);
        } else {

            for(int i=0; i < videoNameList.size(); i++) {

                if(value.equals(videoNameList.get(i))) {
                    HashMap<String, ArrayList<VideoTab>> videoTabInfoList = new HashMap<>();

                    videoTabInfoList.put(context.getResources().getString(R.string.videoTabInfoList), totalVideoTabList.get(i).get(videoNameList.get(i)));

                    return VideoTabFragment.newInstance(videoTabInfoList, value);
                }
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        return totalTabNameList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return totalTabNameList.get(position);
    }

    public void addVideoType(String videoType) {

        totalTabNameList.add(videoType);

        HashMap<String ,ArrayList<VideoTab>> videoTabList = new HashMap<>();
        ArrayList<VideoTab> videoTab = new ArrayList<>();

        videoTabList.put(videoType, videoTab);

        this.totalVideoTabList.add(videoTabList);

        notifyDataSetChanged();
    }

    public void deleteVideoType(String videoType) {

        for (int i=0; i <totalTabNameList.size(); i++) {
            if(videoType.equals(totalTabNameList.get(i))) {
                totalTabNameList.remove(i);
                break;
            }
        }

        for(int i=0; i < totalVideoTabList.size(); i++) {
            if(totalVideoTabList.get(i).get(videoType) != null) {

                totalVideoTabList.remove(i);

                break;
            }
        }

        notifyDataSetChanged();

    }

    public void destory() {
        mYoutubeManageObserver.unregisterObserver(context);
    }
}
