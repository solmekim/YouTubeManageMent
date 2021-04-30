package com.solmekim.youtubemanage.VideoTab;

import android.content.Context;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.solmekim.youtubemanage.Database.VideoDBOpenHelper;
import com.solmekim.youtubemanage.R;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.solmekim.youtubemanage.Util.Util.extractYTId;


public class VideoTabRecyclerViewAdapter extends RecyclerView.Adapter<VideoViewHolder> {

    private VideoDBOpenHelper dataBaseOpenHelper;

    private Fragment fragment;
    private Context context;
    private ArrayList<VideoTab> videoTabInfoArrayList;

    private boolean isSelectMode;

    public boolean isSelectMode() {
        return isSelectMode;
    }

    public void setSelectMode(boolean selectMode) {
        isSelectMode = selectMode;
    }


    private int tagIndex;


    public VideoTabRecyclerViewAdapter(ArrayList<VideoTab> videoTabInfoArrayList, Fragment fragment) {
        this.videoTabInfoArrayList = videoTabInfoArrayList;
        this.fragment = fragment;

        isSelectMode = false;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.youtube_list, parent, false);
        context = parent.getContext();
        dataBaseOpenHelper = new VideoDBOpenHelper(context);

        return new VideoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final VideoViewHolder holder, int position) {

        holder.selectCheckBox.setVisibility(isSelectMode ? View.VISIBLE : View.GONE);

        holder.selectCheckBox.setChecked(videoTabInfoArrayList.get(position).getCheckedValue());

        holder.selectCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean value) {
                videoTabInfoArrayList.get(holder.getAdapterPosition()).setCheckedValue(value);
            }
        });


        holder.listLinearLayout.setOnClickListener(onClickListener);
        holder.listLinearLayout.setTag(holder.getAdapterPosition());

        holder.title.setTag(holder.getAdapterPosition());
        holder.viewCount.setTag(holder.getAdapterPosition());


        holder.title.setText(videoTabInfoArrayList.get(holder.getAdapterPosition()).getVideoTitle());

        holder.viewCount.setText(calViewCount(holder.getAdapterPosition()));

        Glide.with(context).load("https://img.youtube.com/vi/" + extractYTId(videoTabInfoArrayList.get(position).getVideoUrl()) + "/maxresdefault.jpg")
                .apply(new RequestOptions().error(R.drawable.error)).into(holder.youTubeThumbnailView);


        holder.youTubeDuration.setText(getDuration(videoTabInfoArrayList.get(position).getVideoDuration()));
        holder.uploadDate.setText(getUploadDate(videoTabInfoArrayList.get(position).getVideoUploadTime()));


    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.listLinearLayout:

                    tagIndex = (Integer) v.getTag();

                    HashMap<String, String> videoTabValue = new HashMap<>();
                    videoTabValue.put(context.getResources().getString(R.string.VideoTitle), videoTabInfoArrayList.get(tagIndex).getVideoTitle());
                    videoTabValue.put(context.getResources().getString(R.string.VideoUploadTime), videoTabInfoArrayList.get(tagIndex).getVideoUploadTime());
                    videoTabValue.put(context.getResources().getString(R.string.VideoUrl), videoTabInfoArrayList.get(tagIndex).getVideoUrl());

                    try {    // update ViewCount

                        dataBaseOpenHelper.updateVideoViewCountColumn(videoTabInfoArrayList.get(tagIndex).getVideoTitle(), videoTabInfoArrayList.get(tagIndex).getVideoUploadTime()
                                , videoTabInfoArrayList.get(tagIndex).getVideoUrl());

                        videoTabInfoArrayList.get(tagIndex).setVideoViewCount(videoTabInfoArrayList.get(tagIndex).getVideoViewCount() + 1);
                        notifyDataSetChanged();

                        Intent intent = new Intent(context, YouTubePlayActivity.class);
                        intent.putExtra(context.getResources().getString(R.string.VideoTab), videoTabInfoArrayList.get(tagIndex).getVideoTab());
                        intent.putExtra(context.getResources().getString(R.string.VideoUrl), extractYTId(videoTabInfoArrayList.get(tagIndex).getVideoUrl()));
                        intent.putExtra(context.getResources().getString(R.string.VideoTitle), videoTabInfoArrayList.get(tagIndex).getVideoTitle());
                        intent.putExtra(context.getResources().getString(R.string.VideoViewCount), videoTabInfoArrayList.get(tagIndex).getVideoViewCount());
                        intent.putExtra(context.getResources().getString(R.string.VideoUploadTime), videoTabInfoArrayList.get(tagIndex).getVideoUploadTime());
                        intent.putExtra(context.getResources().getString(R.string.VideoDescription), videoTabInfoArrayList.get(tagIndex).getVideoDescription());
                        intent.putExtra(context.getResources().getString(R.string.tagIndex), tagIndex);

                        fragment.startActivityForResult(intent, 2);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    };

    private String calViewCount(int position) {
        String viewCount = videoTabInfoArrayList.get(position).getVideoViewCount() + "";
        int viewCountLenght = viewCount.length();
        if (viewCountLenght >= 5 && viewCountLenght < 9) {
            return viewCount.substring(0, viewCount.length() - 4) + "만 views";

        } else if (viewCountLenght >= 9) {
            return viewCount.substring(0, viewCount.length() - 8) + "억 views";
        } else {
            return viewCount + " views";
        }
    }

    private String getDuration(int duration) {

        String time = "";
        int sec, min, hour;

        min = (int) ((duration / (1000 * 60)) % 60);
        hour = (int) ((duration / (1000 * 60 * 60)) % 24);
        sec = (int) (duration / 1000) % 60;

        if (hour != 0) {
            if (hour < 10) {
                time += " 0" + hour + ":";
            } else {
                time += " " + hour + ":";
            }
        }

        if (min < 10) {
            time += " 0" + min + ":";
        } else {
            time += " " + min + ":";
        }

        if (sec < 10) {
            time += " 0" + sec;
        } else {
            time += " " + sec;
        }

        return time;
    }

    private String getUploadDate(String uploadDate) {

        try {
            long now = System.currentTimeMillis();
            Date today = new Date(now);

            SimpleDateFormat simpleDateFormat_full = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Date date = simpleDateFormat_full.parse(uploadDate);
            String newFormat = simpleDateFormat.format(date);
            Date newDate = simpleDateFormat.parse(newFormat);

            int calDate = (int) ((today.getTime() - newDate.getTime()) / (24 * 60 * 60 * 1000));

            if (calDate < 7) {
                return calDate + " 일전";
            } else if (calDate < 30) {
                return (calDate / 7) + " 주전";
            } else if (calDate < 365) {
                return (calDate / 30) + " 달전";
            } else {
                return (calDate / 365) + " 년전";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    public int getItemCount() {
        return videoTabInfoArrayList.size();
    }
}
