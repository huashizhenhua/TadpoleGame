
package com.itap.voiceemoticon.media;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.util.Log;

import com.itap.voiceemoticon.VEApplication;
import com.itap.voiceemoticon.activity.HttpGetProxy;

public class MusicPlayer implements OnBufferingUpdateListener, OnPreparedListener,
        OnCompletionListener, OnErrorListener {

    public static final String BROCAST_NAME = "com.itap.voiceemotion.music.player";

    // brocast type has two types
    public static final String KEY_BROCAST_TYPE = "msgType";

    public static final int BROCAST_TYPE_PLAY_STATE = 0;

    public static final int BROCAST_TYPE_BUFFER_UPDATE = 1;

    public static final String KEY_STATE = "state";

    public static final int STATE_INVALID = 5;

    public static final int STATE_PLAY_START = 1;

    public static final int STATE_PLAY_COMPLETE = 2;

    public static final int STATE_PLAY_STOP = 7;

    public static final int STATE_PLAY_PREPARING = 9;

    public static final String KEY_STATE_DATA = "data";

    public static final String KEY_PERCENT = "percent";

    private Context mContext;

    private MediaPlayer mPlayer = null;

    private int mState = STATE_INVALID;

    private String mCurUrl = "hack url";

    private String mTitle = "";

    private HttpGetProxy mHttpGetProxy = null;

    private static final int LOCAL_PORT = 9091;

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

    private HashMap<String, MusicData> mMusicMap = new HashMap<String, MusicData>();

    public void resume() {
        playMusic(mCurUrl, mTitle);
    }

    public void playMusic(final String urlParam, final String title) {
        final String url = urlParam;
        final MusicPlayer me = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (url == null) {
                        Log.e(VEApplication.TAG, "playMusic can't be null");
                        return;
                    }
                    mHttpGetProxy.printStateInfo();
                    if (!mCurUrl.equals(url) || mState == STATE_INVALID
                            || mState == STATE_PLAY_COMPLETE) {
                        mHttpGetProxy.closeOpenedStreams();
                        mPlayer.stop();
                        mPlayer.reset();
                        // mHttpGetProxy.getProxyUrl(url)
                        mPlayer.setDataSource(url);
                        mPlayer.prepareAsync();
                        mState = STATE_PLAY_PREPARING;
                    } else {
                        if (mState == STATE_PLAY_STOP) {
                            mPlayer.prepareAsync();
                            mState = STATE_PLAY_PREPARING;
                        }
                    }
                    mCurUrl = url;
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
        if (mPlayer == null) {
            return;
        }
        mPlayer.stop();
        mState = STATE_PLAY_STOP;
        this.sendPlayStateBrocast();
    }

    public void exit() {
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
            musicData.musicPath = mCurUrl;
            musicData.musicName = mTitle;
            mMusicMap.put(mCurUrl, musicData);
            intent.putExtra(KEY_STATE, mState);
            intent.putExtra(KEY_STATE_DATA, musicData);
            intent.putExtra(KEY_BROCAST_TYPE, BROCAST_TYPE_PLAY_STATE);

            mContext.sendBroadcast(intent);
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mState == STATE_PLAY_COMPLETE) {
            return;
        }
        mp.start();
        mState = STATE_PLAY_START;
        this.sendPlayStateBrocast();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        if (mPlayer.isPlaying()) {
            Log.d(VEApplication.TAG, "onBufferingUpdate percent = " + percent);
            sendBufferUpdateBrocast(percent);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
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
        if (mState != STATE_INVALID && mState != STATE_PLAY_COMPLETE) {
            return mPlayer.getCurrentPosition();
        }
        return 0;
    }

    public int getDuration() {
        if (mState != STATE_INVALID && mState != STATE_PLAY_COMPLETE
                && mState != STATE_PLAY_PREPARING) {
            return mPlayer.getDuration();
        }
        return 0;
    }

    public boolean isPlaying() {
        if (mPlayer != null) {
            return mPlayer.isPlaying() || mState == STATE_PLAY_PREPARING;
        }
        return false;
    }

    public void seek(int percent) {
        if (mPlayer != null && mPlayer.isPlaying()) {
            long duration = getDuration();
            long seekMillSecond = (duration * percent) / 100;
            mPlayer.seekTo((int)seekMillSecond);
        }
    }

    public void destory() {
        mHttpGetProxy.stop();
    }
}
