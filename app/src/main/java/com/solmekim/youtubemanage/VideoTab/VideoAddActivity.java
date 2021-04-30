package com.solmekim.youtubemanage.VideoTab;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.solmekim.youtubemanage.Database.VideoDBOpenHelper;
import com.solmekim.youtubemanage.R;
import com.solmekim.youtubemanage.Util.Util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static com.solmekim.youtubemanage.Util.Util.extractYTId;

public class VideoAddActivity extends YouTubeBaseActivity {

    private VideoDBOpenHelper dataBaseOpenHelper;

    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer youTubePlayer;
    private ImageView searchVideo;
    private EditText urlLink;
    private EditText videoTitle;
    private EditText videoDescription;
    private ImageView uploadBtn;
    private ImageView checkImage;

    private Boolean checkURL;
    private int VideoDuration;

    private String VIDEO_ID;
    private String type;

    private HashMap<String, String> videoTabValue;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        type = getIntent().getExtras().getString(getResources().getString(R.string.type));
        setContentView(R.layout.activity_video_add);
        init();
    }

    private void init() {

        dataBaseOpenHelper = new VideoDBOpenHelper(getApplicationContext());

        checkURL = false;
        VideoDuration = -1;

        videoTabValue = new HashMap<>();

        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtubeView);

        searchVideo = (ImageView) findViewById(R.id.searchVideo);
        searchVideo.setOnClickListener(onClickListener);

        urlLink = (EditText) findViewById(R.id.urlLink);
        videoTitle = (EditText) findViewById(R.id.videoTitle);
        videoDescription = (EditText) findViewById(R.id.videoDescription);

        uploadBtn = (ImageView) findViewById(R.id.uploadBtn);
        uploadBtn.setOnClickListener(onClickListener);

        checkImage = (ImageView) findViewById(R.id.checkImage);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.searchVideo:

                    if (!urlLink.getText().toString().replace(" ", "").equals("")) {
                        if (youTubePlayer != null) {
                            youTubePlayer.release();
                        }
                        VIDEO_ID = extractYTId(urlLink.getText().toString());

                        youTubePlayerView.initialize(getResources().getString(R.string.YOUTUBE_API_KEY), new YouTubePlayer.OnInitializedListener() {
                            @Override
                            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youtubePlayer, boolean b) {

                                youTubePlayer = youtubePlayer;
                                if (VIDEO_ID == null) {
                                    Toast.makeText(getApplicationContext(),getString(R.string.checkURL) , Toast.LENGTH_SHORT).show();
                                    urlLink.setText("");
                                    checkURL = false;
                                } else {
                                    youTubePlayer.loadVideo(VIDEO_ID);

                                    checkURL = true;
                                }

                            }

                            @Override
                            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                                checkURL = false;
                                Toast.makeText(getApplicationContext(), getString(R.string.searchVideo_error), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        checkURL = false;
                        Toast.makeText(getApplicationContext(), getString(R.string.insertURL), Toast.LENGTH_SHORT).show();
                        break;
                    }

                    break;

                case R.id.uploadBtn:



                    if (checkEmpty()) {
                        Toast.makeText(getApplicationContext(), getString(R.string.insertBlankOrURL), Toast.LENGTH_SHORT).show();
                        break;
                    }

                    VideoDuration = youTubePlayer.getDurationMillis();


                    if (VideoDuration == -1) {
                        Util.MakeToast(getString(R.string.tryAgain), getApplicationContext());
                    }


                    Glide.with(VideoAddActivity.this).load("https://img.youtube.com/vi/" + extractYTId(urlLink.getText().toString() + "/maxresdefault.jpg")).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            Toast.makeText(getApplicationContext(), "고화질 썸네일이 존재하지 않습니다. 고화질 썸네일을 upload하거나 다른 동영상을 upload해주세요", Toast.LENGTH_SHORT).show();
                            checkURL = false;
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {


                            AlertDialog.Builder builder = new AlertDialog.Builder(VideoAddActivity.this);
                            builder.setMessage(getString(R.string.checkVideo))
                                    .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            if (dataBaseOpenHelper.insertVideoTabValueColumn(type, videoTitle.getText().toString(), 0,
                                                    VideoDuration, getTodayTime(), urlLink.getText().toString(), Util.extractYTId(urlLink.getText().toString()),
                                                    videoDescription.getText().toString()) > 0) {
                                                Util.MakeToast(getString(R.string.addNewVideo), getApplicationContext());

                                                videoTabValue.put(getResources().getString(R.string.VideoTab), type);
                                                videoTabValue.put(getResources().getString(R.string.VideoTitle), videoTitle.getText().toString());
                                                videoTabValue.put(getResources().getString(R.string.VideoViewCount), "0");
                                                videoTabValue.put(getResources().getString(R.string.VideoUploadTime), getTodayTime());
                                                videoTabValue.put(getResources().getString(R.string.VideoUrl), urlLink.getText().toString());
                                                videoTabValue.put(getResources().getString(R.string.VideoID), extractYTId(urlLink.getText().toString()));
                                                videoTabValue.put(getResources().getString(R.string.VideoDescription), videoDescription.getText().toString());
                                                videoTabValue.put(getResources().getString(R.string.VideoDuration), VideoDuration + "");

                                                Intent resultIntent = new Intent();
                                                resultIntent.putExtra(getResources().getString(R.string.videoTabInfoList), videoTabValue);
                                                setResult(RESULT_OK, resultIntent);
                                                finish();
                                            }
                                        }
                                    }).setNegativeButton(getResources().getString(R.string.cancel), null)
                                    .create()
                                    .show();
                            return false;
                        }
                    }).into(checkImage);


                    break;
            }
        }
    };

    private Boolean checkEmpty() {
        if (urlLink.getText().toString().replace(" ", "").equals("") ||
                videoTitle.getText().toString().replace(" ", "").equals("") ||
                videoDescription.getText().toString().replace(" ", "").equals("")) {
            return true;
        } else if (!checkURL) {
            return true;
        } else {
            return false;
        }
    }

    private String getTodayTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String getTime = simpleDateFormat.format(date);
        return getTime;
    }
}
