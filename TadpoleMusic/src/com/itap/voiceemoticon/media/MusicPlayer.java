package com.itap.voiceemoticon.media;

import java.io.IOException;
import java.net.InetSocketAddress;
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

public class MusicPlayer implements OnBufferingUpdateListener, OnPreparedListener, OnCompletionListener, OnErrorListener {

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
        // "http://qq.djwma.com/mp3/%E4%B8%AD%E5%9B%BD%E5%A5%BD%E5%A3%B0%E9%9F%B3%E7%B2%BE%E9%80%89%E5%A5%BD%E5%90%AC%E6%AD%8C%E6%9B%B2mp3%E4%B8%8B%E8%BD%BD.mp3";
        // Log.d(VEApplication.TAG, "playMusic url = " + url + ", title = "
        // + title);



        final MusicPlayer me = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (url == null) {
                        Log.e(VEApplication.TAG, "playMusic can't be null");
                        return;
                    }
                    // Log.d(VEApplication.TAG, "1111111111111 curUrl = " +
                    // mCurUrl + ", url = " + url + ", mState = " + mState);
                    mHttpGetProxy.printStateInfo();

                    Log.d(VEApplication.TAG, "  proxyUrl = " + mHttpGetProxy.getProxyUrl(url));

                    if (!mCurUrl.equals(url) || mState == STATE_INVALID || mState == STATE_PLAY_COMPLETE) {
                        Log.d(VEApplication.TAG, "2222222222222222  proxyUrl = " + mHttpGetProxy.getProxyUrl(url));
                        mHttpGetProxy.closeOpenedStreams();
                        mPlayer.stop();
                        Log.d(VEApplication.TAG, "3333333333333333  proxyUrl = " + mHttpGetProxy.getProxyUrl(url));
                        mPlayer.reset();
                        Log.d(VEApplication.TAG, "4444444444444444  proxyUrl = " + mHttpGetProxy.getProxyUrl(url));
                        mPlayer.setDataSource(mHttpGetProxy.getProxyUrl(url));
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
        if (mPlayer != null) {
            mPlayer.stop();
            mState = STATE_PLAY_STOP;
            this.sendPlayStateBrocast();
        }
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

            musicData.musicCurTime = getCurrentPostion();
            musicData.musicDuration = getDuration();

            mMusicMap.put(mCurUrl, musicData);

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

    private int mCurPercent = 0;

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        if (mPlayer.isPlaying()) {
            Log.d(VEApplication.TAG, "onBufferingUpdate percent = " + percent);
            mCurPercent = percent;
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
        Bundle data = new Bundle();
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
