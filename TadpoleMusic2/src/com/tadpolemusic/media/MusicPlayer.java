package com.tadpolemusic.media;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;

import com.tadpolemusic.VEApplication;

public class MusicPlayer implements OnBufferingUpdateListener, OnPreparedListener, OnCompletionListener, OnErrorListener {

    private static final int BROCAST_MILLSECONDS = 500;

    public static final String BROCAST_NAME = "com.itap.voiceemotion.music.player";

    // brocast type has two types
    public static final String KEY_BROCAST_TYPE = "msgType";
    public static final int BROCAST_TYPE_PLAY_STATE = 0;
    public static final int BROCAST_TYPE_BUFFER_UPDATE = 1;

    public static final String KEY_STATE = "state";
    public static final int STATE_INVALID = 5;
    public static final int STATE_PLAY_START = 1;
    public static final int STATE_PLAY_COMPLETE = 2;
    public static final int STATE_PLAY_PLAYING = 3;
    public static final int STATE_PLAY_STOP = 7;
    public static final int STATE_PLAY_PREPARING = 8;

    public static final String KEY_STATE_DATA = "data";

    public static final String KEY_PERCENT = "percent";

    private Context mContext;
    private MediaPlayer mPlayer = null;
    private int mState = STATE_INVALID;
    private String mCurMediaPath = "hack url";
    private String mTitle = "";
    private HttpGetProxy mHttpGetProxy = null;
    private static final int LOCAL_PORT = 9091;

    private int mBufferPercent = 0;
    private Timer mTimer;

    private void startRepeatBrocast(int millseconds) {
        synchronized (this) {
            final MusicPlayer me = this;
            if (mTimer != null) {
                mTimer.cancel();
            }
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    me.sendPlayStateBrocast();
                }
            }, 0, millseconds);
        }
    }

    private void stopRepeatBrocast() {
        synchronized (this) {
            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }
        }
    }


    public MusicPlayer(Context context) {
        mContext = context;
        mPlayer = new MediaPlayer();
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnBufferingUpdateListener(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
        mHttpGetProxy = new HttpGetProxy(LOCAL_PORT);
        mHttpGetProxy.start();
    }

    public void resume() {
        playMusic(mCurMediaPath, mTitle);
    }

    public void playMusic(final String path, final String title) {
        stopRepeatBrocast();
        final String mediaFilePath = path;
        final MusicPlayer me = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mediaFilePath == null) {
                        Log.e(VEApplication.TAG, "playMusic can't be null");
                        return;
                    }

                    if (mediaFilePath.startsWith("http")) {
                        mHttpGetProxy.printStateInfo();
                        Log.d(VEApplication.TAG, "  proxyUrl = " + mHttpGetProxy.getProxyUrl(mediaFilePath));
                    }


                    if (!mCurMediaPath.equals(mediaFilePath) || mState == STATE_INVALID || mState == STATE_PLAY_COMPLETE) {
                        mState = STATE_PLAY_PREPARING;
                        startRepeatBrocast(BROCAST_MILLSECONDS);
                        mPlayer.stop();
                        mPlayer.reset();
                        // remote use proxy
                        if (mediaFilePath.startsWith("http")) {
                            mHttpGetProxy.closeOpenedStreams();
                            mPlayer.setDataSource(mHttpGetProxy.getProxyUrl(mediaFilePath));
                        }
                        // local
                        else {
                            mPlayer.setDataSource(mediaFilePath);
                        }
                        mPlayer.prepareAsync();
                    } else {
                        if (mState == STATE_PLAY_STOP) {
                            mState = STATE_PLAY_PREPARING;
                            startRepeatBrocast(BROCAST_MILLSECONDS);
                            mPlayer.prepareAsync();
                        }
                    }
                    mCurMediaPath = mediaFilePath;
                    mTitle = title;
                } catch (Exception e) {
                    mState = STATE_INVALID;
                    e.printStackTrace();
                }
                me.sendPlayStateBrocast();
            }
        }).start();
    }

    public void stopMusic() {
        stopRepeatBrocast();
        if (mPlayer != null) {
            mPlayer.stop();
            mState = STATE_PLAY_STOP;
            this.sendPlayStateBrocast();
        }
    }

    public void exit() {
        stopRepeatBrocast();
        if (mPlayer != null)
            mPlayer.release();
    }

    public void sendBufferUpdateBrocast(int percent) {
        if (mContext != null) {
            Intent intent = new Intent(BROCAST_NAME);
            intent.putExtra(KEY_BROCAST_TYPE, BROCAST_TYPE_BUFFER_UPDATE);
            intent.putExtra(KEY_PERCENT, percent);
            mContext.sendBroadcast(intent);
        }
    }

    public void sendPlayStateBrocast() {
        if (mContext != null) {
            if (mState == STATE_INVALID) {
                return;
            }
            Intent intent = new Intent(BROCAST_NAME);


            MusicData musicData = new MusicData();
            musicData.musicPath = mCurMediaPath;
            musicData.musicName = mTitle;
            musicData.musicCurTime = getCurrentPostion();
            musicData.musicDuration = getDuration();

            intent.putExtra(KEY_STATE, mState);
            intent.putExtra(KEY_STATE_DATA, musicData);
            intent.putExtra(KEY_BROCAST_TYPE, BROCAST_TYPE_PLAY_STATE);

            mContext.sendBroadcast(intent);
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mState = STATE_PLAY_START;
        this.sendPlayStateBrocast();
        mp.start();
        mState = STATE_PLAY_PLAYING;
        this.sendPlayStateBrocast();
    }


    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        if (mPlayer.isPlaying()) {
            Log.d(VEApplication.TAG, "onBufferingUpdate percent = " + percent);
            mBufferPercent = percent;
            sendBufferUpdateBrocast(percent);
            try {
                this.sendPlayStateBrocast();
            } catch (Exception e) {
                e.printStackTrace();
                mState = STATE_INVALID;
            }
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //        stopRepeatBrocast();
        mState = STATE_PLAY_COMPLETE;
        sendPlayStateBrocast();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(VEApplication.TAG, "onError mState = " + mState);
        mState = STATE_INVALID;
        this.sendPlayStateBrocast();
        return false;
    }

    public int getCurrentPostion() {
        if (mState != STATE_INVALID && mState != STATE_PLAY_COMPLETE && mState != STATE_PLAY_PREPARING) {
            return mPlayer.getCurrentPosition();
        }
        return 0;
    }

    public int getDuration() {
        if (mState != STATE_INVALID && mState != STATE_PLAY_COMPLETE && mState != STATE_PLAY_PREPARING) {
            return mPlayer.getDuration();
        }
        return 0;
    }

    public boolean isPlaying() {
        if (mPlayer != null) {
            return mPlayer.isPlaying();
        }
        return false;
    }

    public void seek(int percent) {
        if (mPlayer != null && mPlayer.isPlaying()) {
            long duration = getDuration();
            long seekMillSecond = (duration * percent) / 100;
            mPlayer.seekTo((int) seekMillSecond);
        }
    }

    public void destory() {
        mHttpGetProxy.stop();
    }
}
