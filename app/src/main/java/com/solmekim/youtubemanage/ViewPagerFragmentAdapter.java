package com.solmekim.youtubemanage;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.solmekim.youtubemanage.VideoTab.VideoTab;
import com.solmekim.youtubemanage.VideoTab.VideoTabFragment;
import com.solmekim.youtubemanage.VideoType.VideoTypeListFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewPagerFragmentAdapter extends FragmentStatePagerAdapter {

    private Context context;
    private Activity activity;

    private ArrayList<String> videoNameList;
    private ArrayList<String> totalTabNameList;
    private List<HashMap<String, ArrayList<VideoTab>>> totalVideoTabList;

    public ViewPagerFragmentAdapter(FragmentManager fm, Context context, ArrayList<String> totalTabNameList,
                                    ArrayList<String> videoNameList,
                                    List<HashMap<String, ArrayList<VideoTab>>> totalVideoTabList, Activity activity) {
        super(fm);

        this.context = context;
        this.videoNameList = videoNameList;
        this.totalTabNameList = totalTabNameList;
        this.totalVideoTabList = totalVideoTabList;
        this.activity = activity;
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

                    ArrayList<VideoTab> videoTab;

                    videoTab = totalVideoTabList.get(i).get(videoNameList.get(i));

                    videoTabInfoList.put(context.getResources().getString(R.string.videoTabInfoList), videoTab);

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
}
