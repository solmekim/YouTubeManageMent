package com.solmekim.youtubemanage.VideoType;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.solmekim.youtubemanage.R;
import com.solmekim.youtubemanage.Util.InterfaceClass;
import com.solmekim.youtubemanage.Util.Util;
import com.solmekim.youtubemanage.provider.YouTubeManageContract;

import java.util.ArrayList;

public class VideoTypeListAdapter extends BaseAdapter {

    private InterfaceClass.SendDeleteVideoTabInfoToActivity sendDeleteVideoTabInfoToActivity;

    private ArrayList<String> videoNameList;
    private Context context;
    private LayoutInflater layoutInflater;
    private Activity parentActivity;
    private ViewHolder viewHolder;
    private AlertDialog.Builder tagBuilder;

    public class ViewHolder {
        TextView videoType;
        ImageView deleteVideo;
    }

    public VideoTypeListAdapter(Context context, Activity parentActivity, ArrayList<String> videoNameList) {
        this.context = context;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.parentActivity = parentActivity;
        this.videoNameList = videoNameList;
        this.sendDeleteVideoTabInfoToActivity = (InterfaceClass.SendDeleteVideoTabInfoToActivity) parentActivity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.videotype_list, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.videoType = (TextView) convertView.findViewById(R.id.videoType);

            viewHolder.deleteVideo = (ImageView) convertView.findViewById(R.id.deleteVideoType);
            viewHolder.deleteVideo.setOnClickListener(onClickListener);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.deleteVideo.setTag(R.string.videoType, videoNameList.get(position));
        viewHolder.deleteVideo.setTag(R.string.tagIndex, position);

        viewHolder.videoType.setText(videoNameList.get(position));
        tagBuilder = new AlertDialog.Builder(parentActivity);

        return convertView;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.deleteVideoType:

                    final String videoType = v.getTag(R.string.videoType).toString();
                    final int index = (Integer) v.getTag(R.string.tagIndex);

                    tagBuilder.setMessage(videoType + context.getResources().getString(R.string.delete_video))
                            .setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteVideo(videoType, index);
                                }
                            }).setNegativeButton(context.getResources().getString(R.string.cancel), null)
                            .create().show();

                    break;
            }
        }
    };

    @Override
    public int getCount() {
        return videoNameList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void deleteVideo(String videoType, int index) {

        if(YouTubeManageContract.deleteVideoTab(context, videoType) >0) {
            Util.MakeToast(context.getString(R.string.delete_videoTab_success),  context);

            sendDeleteVideoTabInfoToActivity.sendDeleteVideoTabInfo(videoType);
            videoNameList.remove(index);
            notifyDataSetChanged();

        } else {
            Util.MakeToast(context.getString(R.string.video_exist), context);
        }
    }

}
