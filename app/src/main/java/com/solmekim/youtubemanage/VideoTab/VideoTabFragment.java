package com.solmekim.youtubemanage.VideoTab;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.solmekim.youtubemanage.Database.VideoDBOpenHelper;
import com.solmekim.youtubemanage.R;
import com.solmekim.youtubemanage.Util.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class VideoTabFragment extends Fragment {

    private VideoDBOpenHelper dataBaseOpenHelper;

    private String type;

    private Spinner sortTypeSpinner;
    private LinearLayout tophiddenLayout;
    private RelativeLayout ManagerLayout;
    private AppCompatCheckBox selectAllCheckbox;
    private ImageView deleteImage;
    private TextView cancelDelete, processDelete;

    private Boolean isPressed;
    private Boolean isTotalPressed;

    private RecyclerView contentRecyclerView;
    private RecyclerView.LayoutManager layoutManager;

    private FloatingActionButton videoAddFabBtn;

    private ArrayList<VideoTab> videoTabInfoArrayList;
    private VideoTabRecyclerViewAdapter videoTabRecyclerViewAdapter;

    private String selectedValue;


    public static VideoTabFragment newInstance(HashMap<String, ArrayList<VideoTab>> videoTabInfoList, String type) {

        Bundle args = new Bundle();
        args.putString("type", type);
        args.putParcelableArrayList("videoTabInfoList", videoTabInfoList.get("videoTabInfoList"));

        VideoTabFragment videoTabFragment = new VideoTabFragment();
        videoTabFragment.setArguments(args);

        return videoTabFragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        type = getArguments().getString(getContext().getResources().getString(R.string.type));
        videoTabInfoArrayList = getArguments().getParcelableArrayList(getResources().getString(R.string.videoTabInfoList));
        dataBaseOpenHelper = new VideoDBOpenHelper(getContext());

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_videotab, container, false);
        init(view);

        return view;
    }

    private void init(View view) {

        ManagerLayout = (RelativeLayout) view.findViewById(R.id.ManagerLayout);

        videoAddFabBtn = (FloatingActionButton) view.findViewById(R.id.videoAddFabBtn);
        videoAddFabBtn.setOnClickListener(onClickListener);

        selectAllCheckbox = (AppCompatCheckBox) view.findViewById(R.id.selectAllCheckBox);
        selectAllCheckbox.setOnCheckedChangeListener(onCheckedChangeListener);

        cancelDelete = (TextView) view.findViewById(R.id.cancelDelete);
        cancelDelete.setOnClickListener(onClickListener);
        processDelete = (TextView) view.findViewById(R.id.processDelete);
        processDelete.setOnClickListener(onClickListener);

        deleteImage = (ImageView) view.findViewById(R.id.deleteImage);
        deleteImage.setOnClickListener(onClickListener);

        tophiddenLayout = (LinearLayout) view.findViewById(R.id.tophiddenLayout);

        isPressed = false;


        sortTypeSpinner = (Spinner) view.findViewById(R.id.listviewsortSpinner);

        ArrayAdapter arrayAdapter = ArrayAdapter.createFromResource(getContext(), R.array.sortTypeItem, R.layout.spinner_item);

        sortTypeSpinner.setAdapter(arrayAdapter);
        sortTypeSpinner.setSelection(0);
        sortTypeSpinner.setOnItemSelectedListener(onItemSelectedListener);
        selectedValue = sortTypeSpinner.getSelectedItem().toString();

        contentRecyclerView = (RecyclerView) view.findViewById(R.id.content_recyclerView);

        layoutManager = new LinearLayoutManager(getContext());

        contentRecyclerView.setLayoutManager(layoutManager);
        contentRecyclerView.setHasFixedSize(true);

        if (!videoTabInfoArrayList.isEmpty()) {
            Collections.sort(videoTabInfoArrayList, new timeComparator());
        }
        videoTabRecyclerViewAdapter = new VideoTabRecyclerViewAdapter(videoTabInfoArrayList, this);
        contentRecyclerView.setAdapter(videoTabRecyclerViewAdapter);


    }

    AppCompatCheckBox.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            for (int i = 0; i < videoTabInfoArrayList.size(); i++) {
                videoTabInfoArrayList.get(i).setCheckedValue(isChecked);
            }
            videoTabRecyclerViewAdapter.notifyDataSetChanged();
        }
    };

    AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            if (!selectedValue.equals(sortTypeSpinner.getSelectedItem().toString())) {
                selectedValue = sortTypeSpinner.getSelectedItem().toString();
                if (selectedValue.equals(getResources().getStringArray(R.array.sortTypeItem)[0])) {
                    Collections.sort(videoTabInfoArrayList, new timeComparator());
                    videoTabRecyclerViewAdapter.notifyDataSetChanged();
                } else if (selectedValue.equals(getResources().getStringArray(R.array.sortTypeItem)[1])) {
                    Collections.sort(videoTabInfoArrayList, new viewComparator());
                    videoTabRecyclerViewAdapter.notifyDataSetChanged();
                }
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };



    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.cancelDelete:

                    resetAllSetting();

                    break;

                case R.id.processDelete:

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(getString(R.string.delete_select_video))
                            .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    List<HashMap<String, String>> videoTabInfoList = new ArrayList<>();
                                    List<Integer> matchIndex = new ArrayList<>();
                                    try {

                                        for(int i=0; i <videoTabInfoArrayList.size(); i++) {
                                            if(videoTabInfoArrayList.get(i).getCheckedValue()) {
                                                HashMap<String, String> videoInfo = new HashMap<>();
                                                videoInfo.put(getResources().getString(R.string.VideoTab), videoTabInfoArrayList.get(i).getVideoTab());
                                                videoInfo.put(getResources().getString(R.string.VideoViewCount), videoTabInfoArrayList.get(i).getVideoViewCount() + "");
                                                videoInfo.put(getResources().getString(R.string.VideoUploadTime), videoTabInfoArrayList.get(i).getVideoUploadTime());
                                                videoInfo.put(getResources().getString(R.string.VideoUrl), videoTabInfoArrayList.get(i).getVideoUrl());
                                                videoTabInfoList.add(videoInfo);
                                                matchIndex.add(i);
                                            }
                                        }

                                        if(videoTabInfoList.isEmpty()) {
                                            Util.MakeToast(getString(R.string.video_not_select), getContext());
                                            return;
                                        }

                                        if(dataBaseOpenHelper.deleteYoutubeVideo(videoTabInfoList) >0) {
                                            for(int i = matchIndex.size() -1 ; i >= 0; i--) {
                                                int value = matchIndex.get(i);
                                                videoTabInfoArrayList.remove(value);

                                            }
                                            resetAllSetting();
                                        } else {
                                            Util.MakeToast(getString(R.string.video_delete_error), getContext());
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            }).setNegativeButton(getResources().getString(R.string.cancel), null)
                            .create()
                            .show();


                    break;

                case R.id.videoAddFabBtn:

                    Intent intent = new Intent(getContext(), VideoAddActivity.class);
                    intent.putExtra(getContext().getResources().getString(R.string.type), type);
                    startActivityForResult(intent, 1);

                    break;

                case R.id.deleteImage:

                    int viewValue;

                    if (!isPressed) {
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                        isPressed = true;
                        viewValue = View.VISIBLE;
                        params.addRule(RelativeLayout.RIGHT_OF, R.id.tophiddenLayout);
                        ManagerLayout.setLayoutParams(params);

                    } else {
                        isPressed = false;
                        viewValue = View.GONE;
                    }

                    tophiddenLayout.setVisibility(viewValue);

                    videoTabRecyclerViewAdapter.setSelectMode(isPressed);
                    videoTabRecyclerViewAdapter.notifyDataSetChanged();

                    break;

            }
        }
    };

    public class timeComparator implements Comparator<VideoTab> {

        @Override
        public int compare(VideoTab firstValue, VideoTab secondValue) {
            String firstTime = firstValue.getVideoUploadTime();
            String secondTime = secondValue.getVideoUploadTime();
            try {
                Date firstDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(firstTime);
                Date secondDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(secondTime);
                if (secondDate.after(firstDate)) {
                    return 1;
                } else {
                    return -1;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    public class viewComparator implements Comparator<VideoTab> {
        @Override
        public int compare(VideoTab firstValue, VideoTab secondValue) {
            int firstViewCount = firstValue.getVideoViewCount();
            int secondViewCount = secondValue.getVideoViewCount();
            if (firstViewCount > secondViewCount) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    public void resetAllSetting() {
        isPressed = false;

        selectAllCheckbox.setChecked(isPressed);

        tophiddenLayout.setVisibility(View.GONE);

        videoTabRecyclerViewAdapter.setSelectMode(isPressed);

        for (int i = 0; i < videoTabInfoArrayList.size(); i++) {
            videoTabInfoArrayList.get(i).setCheckedValue(isPressed);
        }

        videoTabRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == -1) {

            switch (requestCode) {

                case 1: // video ADD Call back

                    HashMap<String, String> hashMap = (HashMap<String, String>) data.getSerializableExtra(getResources().getString(R.string.videoTabInfoList));
                    String Videotab = hashMap.get(getResources().getString(R.string.VideoTab));
                    String VideoTitle = hashMap.get(getResources().getString(R.string.VideoTitle));
                    int VideoViewCount = Integer.parseInt(hashMap.get(getResources().getString(R.string.VideoViewCount)));
                    int VideoDuration = Integer.parseInt(hashMap.get(getResources().getString(R.string.VideoDuration)));
                    String VideoUploadTime = hashMap.get(getResources().getString(R.string.VideoUploadTime));
                    String VideoUrl = hashMap.get(getResources().getString(R.string.VideoUrl));
                    String VideoDescription = hashMap.get(getResources().getString(R.string.VideoDescription));


                    VideoTab videoTabInfo = new VideoTab(Videotab, VideoTitle, VideoViewCount, VideoDuration, VideoUploadTime, VideoUrl, VideoDescription);
                    videoTabInfoArrayList.add(videoTabInfo);

                    videoTabRecyclerViewAdapter.notifyDataSetChanged();

                    break;

                case 2: // video edit call back

                    HashMap<String, String> modifyVideoInfo = (HashMap<String, String>) data.getSerializableExtra(getResources().getString(R.string.modifyVideoInfo));

                    String videoTitle = modifyVideoInfo.get(getResources().getString(R.string.VideoTitle));
                    String videoDescription = modifyVideoInfo.get(getResources().getString(R.string.VideoDescription));
                    int tagindex = Integer.parseInt(modifyVideoInfo.get(getResources().getString(R.string.tagIndex)));

                    videoTabInfoArrayList.get(tagindex).setVideoTitle(videoTitle);
                    videoTabInfoArrayList.get(tagindex).setVideoDescription(videoDescription);

                    videoTabRecyclerViewAdapter.notifyDataSetChanged();

                    break;

            }
        }
    }
}
