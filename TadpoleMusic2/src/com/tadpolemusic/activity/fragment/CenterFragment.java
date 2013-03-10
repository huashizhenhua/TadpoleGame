package com.tadpolemusic.activity.fragment;

import java.util.Timer;
import java.util.TimerTask;
import java.util.WeakHashMap;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.itap.voiceemoticon.widget.MarqueeTextSurfaceView;
import com.tadpolemusic.R;
import com.tadpolemusic.VEApplication;
import com.tadpolemusic.activity.dialog.PlayListDialog;
import com.tadpolemusic.adapter.MyMusicItem;
import com.tadpolemusic.media.MusicData;
import com.tadpolemusic.media.MusicPlayState;
import com.tadpolemusic.media.MusicPlayer;
import com.tadpolemusic.media.interface1.IOnServiceConnectComplete;
import com.tadpolemusic.media.service.MusicPlayerProxy;

public class CenterFragment extends AbsMenuFragment {

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

    private ImageView mBtnPlay;
    private ImageView mBtnPlayNext;
    private TextView mTextViewTime;
    private MarqueeTextSurfaceView mTextViewMusicTitle;
    private SeekBar mSeekBarTime;
    private ProgressBar mProgressBarPrepare;
    private View mViewFooter;
    private MusicData mCurMusicData;
    private int mCurPlayListIndex;
    private Timer mPollMusicTimer;

    /**
     * @param period
     *            millseconds
     */
    public void startPoll(int period) {
        if (mPollMusicTimer != null) {
            mPollMusicTimer.cancel();
            mPollMusicTimer = null;
        }
        final CenterFragment me = this;
        mPollMusicTimer = new Timer();

        mPollMusicTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                //                Log.d("mPollMusicTimer", "=====>mPollMusicTimer");
                final MusicPlayerProxy player = VEApplication.getMusicPlayer(getActivity());
                final MusicData md = mCurMusicData;
                final View v = mViewFooter;
                final CenterFragment me = CenterFragment.this;

                //                Log.w("", "mCurMusicData = " + mCurMusicData);
                if (md == null) {
                    return;
                }

                int musicPos = player.getCurPosition();
                final String timerText = md.getTimerText(musicPos);
                final int progress = md.getProgress(musicPos);
                v.post(new Runnable() {
                    @Override
                    public void run() {
                        me.onMusicTimeAndProgressUpdate(timerText, progress);
                    }
                });
            }
        }, 0, period);
    }

    public void stopPoll() {
        if (mPollMusicTimer != null) {
            mPollMusicTimer.cancel();
            mPollMusicTimer = null;
        }
    }


    private BroadcastReceiver mMusicPlayerReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final CenterFragment me = CenterFragment.this;
            if (intent.getAction().equals(MusicPlayer.BROCAST_NAME)) {
                final Bundle data = intent.getExtras();
                final int state = data.getInt(MusicPlayState.PLAY_STATE_NAME, MusicPlayState.MPS_INVALID);
                final int playListIndex = data.getInt(MusicPlayState.PLAY_MUSIC_INDEX);
                Log.d(VEApplication.TAG, " onReceive intent action = " + intent.getAction() + ", state = " + state + ", curPlayIndex = " + mCurPlayListIndex);

                //                int brocastType = data.getInt(MusicPlayer.KEY_BROCAST_TYPE);
                //
                //                if (brocastType == MusicPlayer.BROCAST_TYPE_BUFFER_UPDATE) {
                //                    int percent = data.getInt(MusicPlayer.KEY_PERCENT);
                //                    mSeekBarTime.setSecondaryProgress(percent);
                //                    return;
                //                }

                final MusicData musicData = data.getParcelable(MusicData.KEY_MUSIC_DATA);
                // Log.d(VEApplication.TAG, "musicData = " + musicData);
                me.runOnUIThread(new Runnable() {
                    public void run() {
                        switch (state) {
                        case MusicPlayState.MPS_PREPARE:
                            Log.d(VEApplication.TAG, " STATE_PLAY_START");
                            me.onMusicPreparing();
                            break;
                        case MusicPlayState.MPS_PLAYING:
                            Log.d(VEApplication.TAG, " STATE_PLAY_PLAYING");
                            me.onMusicPlayStart(musicData, playListIndex);
                            break;
                        case MusicPlayState.MPS_NOFILE:
                            Log.d(VEApplication.TAG, " STATE_PLAY_PLAYING");
                            me.onPlayListNoFile(playListIndex);
                            break;
                        case MusicPlayState.MPS_PAUSE:
                            Log.d(VEApplication.TAG, " STATE_PLAY_COMPLETE");
                            me.onMusicPlayComplete();
                            break;
                        case MusicPlayState.MPS_INVALID:
                            Log.d(VEApplication.TAG, "STATE_INVALID");
                            me.onMusicPlayComplete();
                            break;
                        default:
                            Log.d(VEApplication.TAG, " state = " + state);
                            break;
                        }
                    }
                });
            }
        }
    };

    @Override
    public void onStart() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicPlayer.BROCAST_NAME);
        getActivity().registerReceiver(mMusicPlayerReceiver, intentFilter);
        super.onStart();
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(mMusicPlayerReceiver);
        super.onStop();
    }

    private void initMusic() {
        //		setContainer(new LocalMusicFragment());

        mBtnPlay = (ImageView) this.findViewById(R.id.btn_play);
        mBtnPlayNext = (ImageView) this.findViewById(R.id.btn_play_next);
        mTextViewTime = (TextView) this.findViewById(R.id.text_view_time);
        mSeekBarTime = (SeekBar) this.findViewById(R.id.seek_bar_time);
        mProgressBarPrepare = (ProgressBar) this.findViewById(R.id.progress_bar_preparing);
        mTextViewMusicTitle = (MarqueeTextSurfaceView) this.findViewById(R.id.text_view_music_title_slide);

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

    private void dispatchPlayingInfo(int playListIndex) {
        if (mCurContent != null) {
            mCurPlayListIndex = playListIndex;
            Log.d("", "onMusicPlayingIndexChange = " + playListIndex);
            mCurContent.onMusicPlayingIndexChange(playListIndex);
        }
    }

    private void onMusicPlayStart(MusicData musicData, int playListIndex) {
        dispatchPlayingInfo(playListIndex);
        mCurMusicData = musicData;
        this.startPoll(600);
        mTextViewMusicTitle.setText(musicData.musicName);
        mTextViewMusicTitle.startScroll();
        mBtnPlay.setBackgroundResource(android.R.drawable.ic_media_pause);

        mProgressBarPrepare.setVisibility(View.GONE);
        mProgressBarPrepare.setVisibility(View.INVISIBLE);

    }

    private void onMusicPreparing() {
        mProgressBarPrepare.setVisibility(View.VISIBLE);
    }

    private void onMusicTimeAndProgressUpdate(String timerText, int progress) {
        mTextViewTime.setText(timerText);
        mSeekBarTime.setProgress(progress);
    }

    private void onMusicPlayComplete() {
        mCurMusicData = null;
        this.stopPoll();
        Log.d(VEApplication.TAG, "onMusicPlayComplete");
        mProgressBarPrepare.setVisibility(View.GONE);
        mTextViewMusicTitle.stopScroll();
        mBtnPlay.setBackgroundResource(android.R.drawable.ic_media_play);
    }

    private void onPlayListNoFile(int playListIndex) {
        dispatchPlayingInfo(playListIndex);
        mCurMusicData = null;
        this.stopPoll();
        mProgressBarPrepare.setVisibility(View.GONE);

        mTextViewMusicTitle.stopScroll();
        mTextViewMusicTitle.setText("");

        mTextViewTime.setText("00:00/00:00");

        mSeekBarTime.setProgress(0);

        mBtnPlay.setBackgroundResource(android.R.drawable.ic_media_play);
    }
}
