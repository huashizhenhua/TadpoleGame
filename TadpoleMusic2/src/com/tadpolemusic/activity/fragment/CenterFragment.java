package com.tadpolemusic.activity.fragment;

import java.util.WeakHashMap;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.itap.voiceemoticon.widget.MarqueeTextSurfaceView;
import com.tadpolemusic.R;
import com.tadpolemusic.TMLog;
import com.tadpolemusic.VEApplication;
import com.tadpolemusic.activity.LeftAndRightActivity.ActivityMusicListenter;
import com.tadpolemusic.activity.dialog.PlayListDialog;
import com.tadpolemusic.adapter.MyMusicItem;
import com.tadpolemusic.media.MusicData;
import com.tadpolemusic.media.MusicPlayState;
import com.tadpolemusic.media.interface1.IOnServiceConnectComplete;
import com.tadpolemusic.media.service.MusicPlayerProxy;

public class CenterFragment extends AbsMenuFragment implements ActivityMusicListenter {

    //-----------------------------------------------------
    //set content fragment
    //-----------------------------------------------------

    private AbsCenterContent mFragmentToSet;
    private AbsCenterContent mCurContent;

    private WeakHashMap<String, AbsCenterContent> mContentCache = new WeakHashMap<String, AbsCenterContent>();

    public void setContent(MyMusicItem item) {
        setContent(item.contentKey, item.centerContentClass);
    }

    private void setContent(boolean toAdd, AbsCenterContent content) {
        if (getActivity() == null) {
            mFragmentToSet = content;
        } else {
            FragmentTransaction t = getActivity().getSupportFragmentManager().beginTransaction();

            // hide previous content
            if (mCurContent != null) {
                t.hide(mCurContent);
            }

            // show new
            if (toAdd) {
                t.add(R.id.main_center_container, content);
            } else {
                t.show(content);
            }

            mCurContent = content;
            t.commit();

            setHeaderTitle(content.geTitle());
        }
    }

    private void setContent(String contentKey, Class<? extends AbsCenterContent> centerContentClass) {
        try {
            boolean needCreate = false;

            // use cache
            if (mContentCache.containsKey(contentKey)) {
                System.out.println("====>in cache");
                AbsCenterContent content = mContentCache.get(contentKey);
                if (content != null) {
                    setContent(false, content);
                } else {
                    System.out.println("====>in cache but null");
                    needCreate = true;
                }
            } else {
                needCreate = true;
            }

            // no cache , to create
            if (needCreate) {
                AbsCenterContent content = centerContentClass.newInstance();
                setContent(true, content);
                mContentCache.put(contentKey, content);
                return;
            }

        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mFragmentToSet != null) {
            setContent(true, mFragmentToSet);
            mFragmentToSet = null;
        }
    }

    // -------------------------------------------
    // MAIN -- 
    // -------------------------------------------

    private ViewGroup mViewGroup;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewGroup = (ViewGroup) inflater.inflate(R.layout.main_center, null);

        final ProgressDialog progress = ProgressDialog.show(getActivity(), "音乐服务", "正在链接..");
        final MusicPlayerProxy player = VEApplication.getMusicPlayer(getActivity().getApplicationContext());

        player.setOnServiceConnectComplete(new IOnServiceConnectComplete() {
            @Override
            public void OnServiceConnectComplete() {
                progress.hide();
                player.setOnServiceConnectComplete(null);
            }
        });
        player.connectService();

        initHeader();
        initMusic();

        return mViewGroup;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            outState.putString("headerTitle", mHeaderTitle.getText().toString());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            String headerTitle = savedInstanceState.getString("headerTitle");
            mHeaderTitle.setText(headerTitle);
        }
        super.onViewStateRestored(savedInstanceState);
    }

    // -------------------------------------------
    // Header -- 
    // -------------------------------------------
    private TextView mHeaderTitle;
    private ImageButton mBtnToLeft;

    private void initHeader() {
        mHeaderTitle = (TextView) this.findViewById(R.id.title);
        mBtnToLeft = (ImageButton) this.findViewById(R.id.button_left);

        final CenterFragment me = this;
        mBtnToLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                me.getLeftMenuControll().scrollToLeft();
            }
        });
    }

    public void setHeaderTitle(String str) {
        mHeaderTitle.setText(str);
    }

    //-----------------------------------------------------
    // useful methods
    //-----------------------------------------------------
    private View findViewById(int resId) {
        return mViewGroup.findViewById(resId);
    }

    // -------------------------------------------
    // Footer -- Music Player
    // -------------------------------------------

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

    private ImageView mBtnPlay;
    private ImageView mBtnPlayNext;
    private TextView mTextViewTime;
    private MarqueeTextSurfaceView mTextViewMusicTitle;
    private SeekBar mSeekBarTime;
    private ProgressBar mProgressBarPrepare;
    private View mViewFooter;
    private int mCurPlayListIndex;

    private ImageView mImageViewICon;

    private void initMusic() {
        //		setContainer(new LocalMusicFragment());

        final CenterFragment me = this;

        mBtnPlay = (ImageView) this.findViewById(R.id.btn_play);
        mBtnPlayNext = (ImageView) this.findViewById(R.id.btn_play_next);
        mTextViewTime = (TextView) this.findViewById(R.id.text_view_time);
        mSeekBarTime = (SeekBar) this.findViewById(R.id.seek_bar_time);
        mProgressBarPrepare = (ProgressBar) this.findViewById(R.id.progress_bar_preparing);
        mTextViewMusicTitle = (MarqueeTextSurfaceView) this.findViewById(R.id.text_view_music_title_slide);

        mImageViewICon = (ImageView) this.findViewById(R.id.image_view_music_icon);
        mImageViewICon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                me.getLeftMenuControll().scrollToRight();
            }
        });

        mViewFooter = (View) this.findViewById(R.id.footer);
        mViewFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PlayListDialog(v.getContext()).show();
            }
        });

        mBtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MusicPlayerProxy musicPlayer = VEApplication.getMusicPlayer(getActivity().getApplicationContext());
                if (musicPlayer.getPlayState() == MusicPlayState.MPS_PLAYING) {
                    musicPlayer.pause();
                } else {
                    musicPlayer.play(mCurPlayListIndex);
                }
            }
        });

        mBtnPlayNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MusicPlayerProxy musicPlayer = VEApplication.getMusicPlayer(getActivity().getApplicationContext());
                musicPlayer.playNext();
            }
        });

        mSeekBarTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                final MusicPlayerProxy musicPlayer = VEApplication.getMusicPlayer(getActivity().getApplicationContext());
                musicPlayer.seekTo(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }
        });
    }

    public void dispatchPlayingInfo(int playListIndex) {
        if (mCurContent != null) {
            mCurPlayListIndex = playListIndex;
            Log.d("", "onMusicPlayingIndexChange = " + playListIndex);
            mCurContent.onMusicPlayingIndexChange(playListIndex);
        }
    }

    public void onMusicPlayStart(MusicData musicData, int playListIndex) {
        dispatchPlayingInfo(playListIndex);
        mTextViewMusicTitle.setText(musicData.musicName);
        mTextViewMusicTitle.startScroll();
        mBtnPlay.setImageResource(android.R.drawable.ic_media_pause);

        //        mProgressBarPrepare.setVisibility(View.GONE);
        //        mProgressBarPrepare.setVisibility(View.INVISIBLE);

    }

    public void onMusicPreparing() {
        //        mProgressBarPrepare.setVisibility(View.VISIBLE);
    }

    public void onMusicTimeAndProgressUpdate(String timerText, int progress) {
        mTextViewTime.setText(timerText);
        mSeekBarTime.setProgress(progress);
    }

    public void onMusicPlayComplete() {
        Log.d(VEApplication.TAG, "onMusicPlayComplete");
        //        mProgressBarPrepare.setVisibility(View.GONE);
        mTextViewMusicTitle.stopScroll();
        mBtnPlay.setImageResource(android.R.drawable.ic_media_play);
    }

    public void onPlayListNoFile(int playListIndex) {
        dispatchPlayingInfo(playListIndex);
        //        mProgressBarPrepare.setVisibility(View.GONE);

        mTextViewMusicTitle.stopScroll();
        mTextViewMusicTitle.setText("");

        mTextViewTime.setText("00:00/00:00");
        mSeekBarTime.setProgress(0);
        mBtnPlay.setImageResource(android.R.drawable.ic_media_play);
    }

    @Override
    public void onMusicTimeAndProgressUpdate(String progressTimeText, String durationText, int progress) {

    }

    private static final String TAG = "CenterFragment";

    @Override
    public void onMusicPlayBufferUpdate(int playBufferPercent) {
        TMLog.step(TAG, "onMusicPlayBufferUpdate playBufferPercent = " + playBufferPercent);
        mSeekBarTime.setSecondaryProgress(playBufferPercent);
    }
}
