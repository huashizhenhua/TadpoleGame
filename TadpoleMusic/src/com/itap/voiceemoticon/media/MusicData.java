package com.itap.voiceemoticon.media;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class MusicData implements Parcelable {

    public final static String KEY_MUSIC_DATA = "musicData";
    private final static String KEY_MUSIC_NAME = "musicName";
    private final static String KEY_MUSIC_TIME = "musicTime";
    private final static String KEY_MUSIC_DURATION = "musicDuration";
    private final static String KEY_MUSIC_PATH = "musicPath";

    public String musicName;

    public int musicCurTime;
    public int musicDuration;

    public String musicPath;

    public MusicData() {
        musicName = "";
        musicCurTime = 0;
        musicDuration = 0;
        musicPath = "";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle mBundle = new Bundle();
        mBundle.putString(KEY_MUSIC_NAME, musicName);
        mBundle.putInt(KEY_MUSIC_TIME, musicCurTime);
        mBundle.putString(KEY_MUSIC_PATH, musicPath);
        mBundle.putInt(KEY_MUSIC_DURATION, musicDuration);
        dest.writeBundle(mBundle);
    }

    public static final Parcelable.Creator<MusicData> CREATOR = new Parcelable.Creator<MusicData>() {

        @Override
        public MusicData createFromParcel(Parcel source) {
            MusicData data = new MusicData();
            Bundle mBundle = new Bundle();
            mBundle = source.readBundle();
            data.musicName = mBundle.getString(KEY_MUSIC_NAME);
            data.musicCurTime = mBundle.getInt(KEY_MUSIC_TIME);
            data.musicPath = mBundle.getString(KEY_MUSIC_PATH);
            data.musicDuration = mBundle.getInt(KEY_MUSIC_DURATION);
            return data;
        }

        @Override
        public MusicData[] newArray(int size) {
            return new MusicData[size];
        }

    };

    public CharSequence getTimerText() {
        if (musicDuration == 0) {
            return "00:00/00:00";
        }
        int curTime = musicCurTime / 1000;
        int totalTime = musicDuration / 1000;
        int curminute = curTime / 60;
        int cursecond = curTime % 60;
        int totalminute = totalTime / 60;
        int totalsecond = totalTime % 60;
        String curTimeString = String.format("%02d:%02d", curminute, cursecond);
        String totalTimeString = String.format("%02d:%02d", totalminute, totalsecond);
        curTimeString = curTimeString + "/" + totalTimeString;

        long end = System.currentTimeMillis();
        return curTimeString;
    }

    public int getProgress() {
        if (musicDuration == 0) {
            return 0;
        }
        return (100 * musicCurTime) / musicDuration;
    }
}
