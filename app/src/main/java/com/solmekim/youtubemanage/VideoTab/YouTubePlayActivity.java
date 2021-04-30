package com.solmekim.youtubemanage.VideoTab;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.solmekim.youtubemanage.Database.VideoDBOpenHelper;
import com.solmekim.youtubemanage.R;
import com.solmekim.youtubemanage.Util.Util;

import java.util.HashMap;


public class YouTubePlayActivity extends YouTubeBaseActivity {

    private VideoDBOpenHelper dataBaseOpenHelper;

    private String VideoURL;

    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer youTubePlayer;
    private TextView youtubeTitle, uploadTimeTextView, viewCountTextView, videoDescription;
    private EditText EdityoutubeTitle, EditvideoDescription;
    private ImageView editVideo;
    private LinearLayout bottomLayout;

    private ImageView cancelButton, changeButton;
    private Boolean isFullScreen;
    private Boolean editFlag;

    private InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_youtube_play);
        init();
    }

    private void init() {

        dataBaseOpenHelper = new VideoDBOpenHelper(getApplicationContext());

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        isFullScreen = false;
        editFlag = false;

        VideoURL = getIntent().getExtras().getString(getResources().getString(R.string.VideoUrl));

        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youTubePlayerView);

        youtubeTitle = (TextView) findViewById(R.id.youtubeTitle);
        youtubeTitle.setText(getIntent().getExtras().getString(getResources().getString(R.string.VideoTitle)));


        viewCountTextView = (TextView) findViewById(R.id.viewCountTextView);
        viewCountTextView.setText("조회 수  " + getIntent().getExtras().getInt(getResources().getString(R.string.VideoViewCount)) + " views");

        uploadTimeTextView = (TextView) findViewById(R.id.uploadTimeTextView);
        uploadTimeTextView.setText("게시일   " + getOnlyDate());


        videoDescription = (TextView) findViewById(R.id.videoDescription);
        videoDescription.setText(getIntent().getExtras().getString(getResources().getString(R.string.VideoDescription)));


        editVideo = (ImageView) findViewById(R.id.editVideo);
        editVideo.setOnClickListener(onClickListener);


        bottomLayout = (LinearLayout) findViewById(R.id.bottomLayout);

        cancelButton = (ImageView) findViewById(R.id.cancelButton);
        changeButton = (ImageView) findViewById(R.id.changeButton);

        cancelButton.setOnClickListener(onClickListener);
        changeButton.setOnClickListener(onClickListener);

        EdityoutubeTitle = (EditText) findViewById(R.id.EdityoutubeTitle);
        EditvideoDescription = (EditText) findViewById(R.id.EditvideoDescription);

        youTubePlayerView.initialize(getResources().getString(R.string.YOUTUBE_API_KEY), new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer1, boolean b) {
                youTubePlayer = youTubePlayer1;

                youTubePlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                    @Override
                    public void onFullscreen(boolean b) {
                        isFullScreen = b;
                    }
                });

                youTubePlayer.loadVideo(VideoURL);

            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        });


    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.editVideo:

                    if (!editFlag) {
                        editFlag = true;
                        bottomLayout.setVisibility(View.VISIBLE);

                        youtubeTitle.setVisibility(View.GONE);
                        EdityoutubeTitle.setVisibility(View.VISIBLE);
                        EdityoutubeTitle.setText(youtubeTitle.getText());

                        videoDescription.setVisibility(View.GONE);
                        EditvideoDescription.setVisibility(View.VISIBLE);
                        EditvideoDescription.setText(videoDescription.getText());


                    } else {
                        setDefault();
                    }

                    break;

                case R.id.cancelButton:

                    setDefault();

                    break;

                case R.id.changeButton:

                    changeText();


                    break;
            }


        }
    };


    private String getOnlyDate() {
        String date = getIntent().getExtras().getString(getResources().getString(R.string.VideoUploadTime));
        return date.substring(0, 4) + "." + date.substring(5, 7) + "." + date.substring(8, 10);
    }

    private void changeText() {

        AlertDialog.Builder builder = new AlertDialog.Builder(YouTubePlayActivity.this);
        builder.setTitle(getString(R.string.editVideoInfo))
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String title = EdityoutubeTitle.getText().toString();
                        String description = EditvideoDescription.getText().toString();


                        HashMap<String, String> modifyVideoInfo = new HashMap<>();
                        modifyVideoInfo.put(getResources().getString(R.string.VideoTab), getIntent().getExtras().getString(getResources().getString(R.string.VideoTab)));
                        modifyVideoInfo.put(getResources().getString(R.string.VideoUploadTime), getIntent().getExtras().getString(getResources().getString(R.string.VideoUploadTime)));
                        modifyVideoInfo.put(getResources().getString(R.string.VideoID), getIntent().getExtras().getString(getResources().getString(R.string.VideoUrl)));
                        modifyVideoInfo.put(getResources().getString(R.string.VideoTitle), title);
                        modifyVideoInfo.put(getResources().getString(R.string.VideoDescription), description);
                        modifyVideoInfo.put(getResources().getString(R.string.tagIndex), getIntent().getExtras().getInt(getResources().getString(R.string.tagIndex)) +"");

                        try {

                           if(dataBaseOpenHelper.updateVideoInfo(getIntent().getExtras().getString(getResources().getString(R.string.VideoTab)),
                                    getIntent().getExtras().getString(getResources().getString(R.string.VideoUploadTime)), getIntent().getExtras().getString(getResources().getString(R.string.VideoUrl)),
                                    title, description) >0) {
                               youtubeTitle.setText(title);
                               videoDescription.setText(description);

                               setDefault();

                               Intent resultIntent = new Intent();
                               resultIntent.putExtra(getResources().getString(R.string.modifyVideoInfo), modifyVideoInfo);
                               setResult(RESULT_OK, resultIntent);

                           } else {
                               Util.MakeToast(getString(R.string.editVideoError), getApplicationContext());
                           }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }).setNegativeButton(getResources().getString(R.string.cancel), null)
                .create().show();
    }

    private void setDefault() {

        View view = this.getCurrentFocus();

        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        editFlag = false;
        bottomLayout.setVisibility(View.GONE);

        youtubeTitle.setVisibility(View.VISIBLE);
        EdityoutubeTitle.setVisibility(View.GONE);

        videoDescription.setVisibility(View.VISIBLE);
        EditvideoDescription.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (youTubePlayer != null) {
            youTubePlayer.release();
            youTubePlayer = null;
        }
        youTubePlayerView.destroyDrawingCache();
        youTubePlayerView.onFinishTemporaryDetach();
        youTubePlayerView = null;

    }

    @Override
    public void onBackPressed() {
        if (youTubePlayer != null && isFullScreen) {
            youTubePlayer.setFullscreen(false);
        } else {
            super.onBackPressed();
        }
    }
}
