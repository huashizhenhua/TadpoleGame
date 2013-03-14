package com.tadpolemusic.media;

import java.io.File;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import android.content.ContentValues;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.widget.Toast;

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


    public void setMyRingtone(final Context context) {
        new AsyncTask<String, String, String>() {
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                Toast.makeText(context, "设置成功", Toast.LENGTH_LONG).show();
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
            }

            @Override
            protected String doInBackground(String... params) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DATA, musicPath);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, musicName);
                values.put(MediaStore.MediaColumns.TITLE, musicName);
                values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
                values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
                values.put(MediaStore.Audio.Media.IS_ALARM, false);
                values.put(MediaStore.Audio.Media.IS_MUSIC, false);
                Uri uri = MediaStore.Audio.Media.getContentUriForPath(musicPath);
                Uri newUri = context.getContentResolver().insert(uri, values);
                RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, newUri);
                return null;
            }
        }.execute("");
    }


    public void deleteFromDB(Context context) {
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(musicPath);
        context.getContentResolver().delete(uri, MediaStore.Audio.Media.DATA + " = ?", new String[] { musicPath });
    }

    public void deleteFile() {
        File file = new File(musicPath);
        if (file.exists()) {
            file.delete();
        }
    }


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
    
    public String getProgressTimeText(int musicPos){
        if (musicDuration == 0) {
            return "00:00/00:00";
        }
        int curTime = musicPos / 1000;
        int curminute = curTime / 60;
        int cursecond = curTime % 60;
        String curTimeString = String.format("%02d:%02d", curminute, cursecond);
        return curTimeString;
    }
    
    public String getDurtaionText(){
        if (musicDuration == 0) {
            return "00:00";
        }
        int totalTime = musicDuration / 1000;
        int totalminute = totalTime / 60;
        int totalsecond = totalTime % 60;
        String totalTimeString = String.format("%02d:%02d", totalminute, totalsecond).intern();
        return totalTimeString;
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
