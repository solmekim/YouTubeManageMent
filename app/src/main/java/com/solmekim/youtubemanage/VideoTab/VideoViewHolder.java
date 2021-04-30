package com.solmekim.youtubemanage.VideoTab;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.solmekim.youtubemanage.R;

public class VideoViewHolder extends RecyclerView.ViewHolder {

    public AppCompatCheckBox selectCheckBox;
    public LinearLayout listLinearLayout;
    public ImageView youTubeThumbnailView;
    public TextView youTubeDuration;
    public TextView title;
    public TextView viewCount;
    public TextView uploadDate;

    public VideoViewHolder(View itemView) {

        super(itemView);

        selectCheckBox = (AppCompatCheckBox) itemView.findViewById(R.id.selectCheckBox);
        listLinearLayout = (LinearLayout) itemView.findViewById(R.id.listLinearLayout);
        youTubeThumbnailView = (ImageView) itemView.findViewById(R.id.youtubeThumbnailView);
        youTubeDuration = (TextView) itemView.findViewById(R.id.youtubeDuration);
        title = (TextView) itemView.findViewById(R.id.youtubeTitle);
        viewCount = (TextView) itemView.findViewById(R.id.youtubeViewCount);
        uploadDate = (TextView) itemView.findViewById(R.id.uploadDate);
    }
}
