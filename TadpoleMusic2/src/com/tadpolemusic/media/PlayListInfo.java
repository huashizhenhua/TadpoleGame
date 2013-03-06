package com.tadpolemusic.media;

import java.util.List;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class PlayListInfo implements Parcelable {

    private final static String KEY_MUSIC_LIST_ID = "MusicListID";
    private final static String KEY_LIST_SIZE = "ListSize";

    public String playListID;
    public int listSize;

    public PlayListInfo() {
        playListID = "";
    }

    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle mBundle = new Bundle();
        mBundle.putString(KEY_MUSIC_LIST_ID, playListID);
        mBundle.putInt(KEY_LIST_SIZE, listSize);
        dest.writeBundle(mBundle);
    }
    
    public void readFromParcel(Parcel in){
        in.readBundle();
    }
    
    public static final Parcelable.Creator<PlayListInfo> CREATOR = new Parcelable.Creator<PlayListInfo>() {

        @Override
        public PlayListInfo createFromParcel(Parcel source) {
            PlayListInfo info = new PlayListInfo();
            Bundle mBundle = new Bundle();
            mBundle = source.readBundle();
            info.playListID = mBundle.getString(KEY_MUSIC_LIST_ID);
            info.listSize = mBundle.getInt(KEY_LIST_SIZE);
            return info;
        }

        @Override
        public PlayListInfo[] newArray(int size) {
            return new PlayListInfo[size];
        }

    };
}
