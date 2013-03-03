package com.tadpolemusic.media;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class MusicData implements Parcelable {

    public final static String KEY_MUSIC_DATA = "MusicData";

    private final static String KEY_MUSIC_NAME = "MusicName";
    private final static String KEY_MUSIC_TIME = "MusicTime";
    private final static String KEY_MUSIC_PATH = "MusicPath";
    private final static String KEY_MUSIC_ARITST = "MusicAritst";

    public String musicName;
    public int musicDuration;
    public String musicPath;
    public String musicAritst;

    public String getTimerText(int musicPos) {
        if (musicDuration == 0) {
            return "00:00/00:00";
        }
        int curTime = musicPos / 1000;
        int totalTime = musicDuration / 1000;
        int curminute = curTime / 60;
        int cursecond = curTime % 60;
        int totalminute = totalTime / 60;
        int totalsecond = totalTime % 60;
        String curTimeString = String.format("%02d:%02d", curminute, cursecond);
        String totalTimeString = String.format("%02d:%02d", totalminute, totalsecond);
        curTimeString = curTimeString + "/" + totalTimeString;
        return curTimeString;
    }

    public int getProgress(int musicPos) {
        if (musicDuration == 0) {
            return 0;
        }
        return (100 * musicPos) / musicDuration;
    }
    

    public MusicData() {
        musicName = "";
        musicDuration = 0;
        musicPath = "";
        musicAritst = "";
    }

    public String getFirstLetterInUpcase() {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        String str = PinyinHelper.toHanyuPinyinString(musicName, format, "");
        if (str != null && str.length() > 0) {
            if (Character.isLetter(str.toCharArray()[0])) {
                return ((String) str.subSequence(0, 1)).toUpperCase();
            }
        }
        return "?";
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub

        Bundle mBundle = new Bundle();

        mBundle.putString(KEY_MUSIC_NAME, musicName);
        mBundle.putInt(KEY_MUSIC_TIME, musicDuration);
        mBundle.putString(KEY_MUSIC_PATH, musicPath);
        mBundle.putString(KEY_MUSIC_ARITST, musicAritst);
        dest.writeBundle(mBundle);
    }

    public static final Parcelable.Creator<MusicData> CREATOR = new Parcelable.Creator<MusicData>() {

        @Override
        public MusicData createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            MusicData Data = new MusicData();

            Bundle mBundle = new Bundle();
            mBundle = source.readBundle();
            Data.musicName = mBundle.getString(KEY_MUSIC_NAME);
            Data.musicDuration = mBundle.getInt(KEY_MUSIC_TIME);
            Data.musicPath = mBundle.getString(KEY_MUSIC_PATH);
            Data.musicAritst = mBundle.getString(KEY_MUSIC_ARITST);


            return Data;
        }

        @Override
        public MusicData[] newArray(int size) {
            // TODO Auto-generated method stub
            return new MusicData[size];
        }

    };
}
