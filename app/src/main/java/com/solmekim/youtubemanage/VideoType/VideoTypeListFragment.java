package com.solmekim.youtubemanage.VideoType;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.solmekim.youtubemanage.Database.VideoDBOpenHelper;
import com.solmekim.youtubemanage.R;
import com.solmekim.youtubemanage.Util.InterfaceClass;
import com.solmekim.youtubemanage.Util.Util;

import java.util.ArrayList;

public class VideoTypeListFragment extends Fragment {

    private VideoDBOpenHelper dataBaseOpenHelper;

    private VideoTypeListAdapter videoTypeListAdapter;

    private ArrayList<String> videoNameList;

    private RelativeLayout relativeLayout;

    private EditText videotype;
    private Button videotypeAdd;

    private ListView listview;

    private FloatingActionButton fabButton;
    private Boolean flag;

    private InputMethodManager inputMethodManager;

    private AlertDialog.Builder builder;

    private InterfaceClass.SendNewVideoTabInfoToActivity sendNewVideoTabInfoToActivity;

    public static VideoTypeListFragment newInstance(ArrayList<String> videoNameList) {
        Bundle args = new Bundle();
        args.putStringArrayList("videoNameList", videoNameList);
        VideoTypeListFragment videoTypeListFragment = new VideoTypeListFragment();
        videoTypeListFragment.setArguments(args);
        return videoTypeListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        videoNameList = getArguments().getStringArrayList(getResources().getString(R.string.videoNameList));

        sendNewVideoTabInfoToActivity = (InterfaceClass.SendNewVideoTabInfoToActivity) getActivity();

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_videotype, container, false);
        init(view);

        return view;

    }

    private void init(View view) {

        dataBaseOpenHelper = new VideoDBOpenHelper(getContext());

        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        flag = false;

        builder = new AlertDialog.Builder(getActivity());

        relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeLayout);

        videotype = (EditText) view.findViewById(R.id.videotype);
        videotypeAdd = (Button) view.findViewById(R.id.videotype_add);
        videotypeAdd.setOnClickListener(onClickListener);

        listview = (ListView) view.findViewById(R.id.videotypeListview);

        fabButton = (FloatingActionButton) view.findViewById(R.id.fabButton);
        fabButton.setOnClickListener(onClickListener);

        videoTypeListAdapter = new VideoTypeListAdapter(getContext(),getActivity(), videoNameList);
        listview.setAdapter(videoTypeListAdapter);


    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fabButton:

                    if (!flag) {

                        relativeLayout.setVisibility(View.VISIBLE);
                        videotype.getText().clear();
                        videotype.requestFocus();
                        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                        flag = true;
                    } else {

                        relativeLayout.setVisibility(View.GONE);
                        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        flag = false;
                    }

                    break;


                case R.id.videotype_add:

                    final String videoType = videotype.getText().toString();

                    if (videoType.replace(" ", "").equals("")) {
                        Util.MakeToast(getResources().getString(R.string.enter_videoType), getActivity());
                    } else {

                        builder.setMessage(videoType + getResources().getString(R.string.confirm_enter_videoType))
                                .setPositiveButton(getContext().getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        setVideoType(videoType);
                                    }
                                }).setNegativeButton(getContext().getResources().getString(R.string.cancel), null)
                                .create().show();
                    }

            }
        }
    };

    private void setVideoType(String videoType) {

        Cursor cursor = dataBaseOpenHelper.selectVideoTabAllColumns();

        while(cursor.moveToNext()) {
            if(videoType.equals(cursor.getString(cursor.getColumnIndex(getContext().getResources().getString(R.string.VideoTab))))) {
                Util.MakeToast(getString(R.string.videoAlreadyExist), getContext());
                return ;
            }
        }

        if(dataBaseOpenHelper.insertVideoTabColumn(videoType) >0) {

            relativeLayout.setVisibility(View.GONE);
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            flag = false;

            Util.MakeToast(getString(R.string.addNewVideoType), getContext());
            videoNameList.add(videoType);
            videoTypeListAdapter.notifyDataSetChanged();

            sendNewVideoTabInfoToActivity.sendNewVideoTabInfo(videoType);
        } else {
            Util.MakeToast(getString(R.string.addVideoTypeError), getContext());
        }
    }
}
