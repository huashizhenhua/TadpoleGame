package com.tadpolemusic.activity.fragment.menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tadpolemusic.R;
import com.tadpolemusic.VEApplication;
import com.tadpolemusic.activity.LeftAndRightActivity.ActivityMusicListenter;
import com.tadpolemusic.activity.dialog.PlayListDialog;
import com.tadpolemusic.activity.fragment.AbsMenuFragment;
import com.tadpolemusic.media.MusicData;
import com.tadpolemusic.media.MusicPlayMode;
import com.tadpolemusic.media.MusicPlayState;
import com.tadpolemusic.media.service.MusicPlayerProxy;


public class RightMenuFragment extends AbsMenuFragment implements ActivityMusicListenter {

    private TextView mTextViewTitle;
    private SeekBar mSeekBarTime;

    private TextView mTextViewProgressTime;
    private TextView mTextViewDuration;

    private ImageButton mImageBtnMode;
    private ImageButton mImageBtnPrev;
    private ImageButton mImageBtnPlay;
    private ImageButton mImageBtnNext;
    private ImageButton mImageBtnList;


    private ImageButton mImageBtnLeft;

    private int curPlayIndex = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_right, null);


        mTextViewTitle = (TextView) view.findViewById(R.id.title);
        mSeekBarTime = (SeekBar) view.findViewById(R.id.seekbar_time);

        mSeekBarTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                VEApplication.getMusicPlayer(seekBar.getContext()).seekTo(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
        });


        final MusicPlayerProxy proxy = VEApplication.getMusicPlayer(getActivity());

        mTextViewProgressTime = (TextView) view.findViewById(R.id.textview_progress_time);
        mTextViewDuration = (TextView) view.findViewById(R.id.textview_duration);

        mImageBtnLeft = (ImageButton) view.findViewById(R.id.btn_left);
        mImageBtnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RightMenuFragment.this.getLeftMenuControll().scrollToCenter();
            }
        });


        mImageBtnMode = (ImageButton) view.findViewById(R.id.btn_mode);
        mImageBtnMode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int playMode = getNextPlayMode(proxy.getPlayMode());
                proxy.setPlayMode(playMode);
                changeImageBtnPlayMode(playMode);
            }
        });
        changeImageBtnPlayMode(proxy.getPlayMode());


        mImageBtnPrev = (ImageButton) view.findViewById(R.id.btn_prev);
        mImageBtnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VEApplication.getMusicPlayer(v.getContext()).playPre();
            }
        });


        mImageBtnPlay = (ImageButton) view.findViewById(R.id.btn_play);
        mImageBtnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (proxy.getPlayState() != MusicPlayState.MPS_PLAYING) {
                    proxy.play(curPlayIndex);
                } else {
                    proxy.pause();
                }
            }
        });

        mImageBtnNext = (ImageButton) view.findViewById(R.id.btn_next);
        mImageBtnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                VEApplication.getMusicPlayer(v.getContext()).playNext();
            }
        });


        mImageBtnList = (ImageButton) view.findViewById(R.id.btn_list);
        mImageBtnList.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new PlayListDialog(v.getContext()).show();
            }
        });

        return view;
    }

    @Override
    public void dispatchPlayingInfo(int playListIndex) {

    }

    @Override
    public void onStart() {
        super.onStart();
        getLeftMenuControll().registerMusicListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getLeftMenuControll().unRegisterMusicListenr(this);
    }

    @Override
    public void onMusicPlayStart(MusicData musicData, int playListIndex) {
        curPlayIndex = playListIndex;
        mTextViewTitle.setText(musicData.musicName);
        mImageBtnPlay.setImageResource(android.R.drawable.ic_media_pause);
    }

    @Override
    public void onMusicPreparing() {

    }

    @Override
    public void onMusicTimeAndProgressUpdate(String timerText, int progress) {
        mSeekBarTime.setProgress(progress);
    }

    @Override
    public void onMusicPlayComplete() {
        mImageBtnPlay.setImageResource(android.R.drawable.ic_media_play);
    }


    public void changeImageBtnPlayMode(int playMode) {
        switch (playMode) {
        case MusicPlayMode.MPM_LIST_LOOP_PLAY:
            mImageBtnMode.setImageResource(R.drawable.widget_playmode_repeate_all_default);
            break;
        case MusicPlayMode.MPM_ORDER_PLAY:
            mImageBtnMode.setImageResource(R.drawable.widget_playmode_sequence_default);
            break;
        case MusicPlayMode.MPM_RANDOM_PLAY:
            mImageBtnMode.setImageResource(R.drawable.widget_playmode_repeate_random_default);
            break;
        case MusicPlayMode.MPM_SINGLE_LOOP_PLAY:
            mImageBtnMode.setImageResource(R.drawable.widget_playmode_repeate_single_default);
            break;
        default:
            break;
        }
    }

    public int getNextPlayMode(int playMode) {
        switch (playMode) {
        case MusicPlayMode.MPM_LIST_LOOP_PLAY:
            return MusicPlayMode.MPM_ORDER_PLAY;
        case MusicPlayMode.MPM_ORDER_PLAY:
            return MusicPlayMode.MPM_RANDOM_PLAY;
        case MusicPlayMode.MPM_RANDOM_PLAY:
            return MusicPlayMode.MPM_SINGLE_LOOP_PLAY;
        case MusicPlayMode.MPM_SINGLE_LOOP_PLAY:
            return MusicPlayMode.MPM_LIST_LOOP_PLAY;
        default:
            return MusicPlayMode.MPM_LIST_LOOP_PLAY;
        }
    }

    @Override
    public void onPlayListNoFile(int playListIndex) {
        curPlayIndex = playListIndex;
        mTextViewTitle.setText("");
        mTextViewProgressTime.setText("00:00");
        mTextViewDuration.setText("00:00");
        mSeekBarTime.setProgress(0);
        mImageBtnPlay.setImageResource(android.R.drawable.ic_media_play);
    }

    @Override
    public void onMusicTimeAndProgressUpdate(String progressTimeText, String durationText, int progress) {
        mTextViewProgressTime.setText(progressTimeText);
        mTextViewDuration.setText(durationText);
        mSeekBarTime.setProgress(progress);
    }

    @Override
    public void onMusicPlayBufferUpdate(int playBufferPercent) {
        mSeekBarTime.setSecondaryProgress(playBufferPercent);
    }
}
