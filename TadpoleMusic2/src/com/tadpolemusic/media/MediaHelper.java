package com.tadpolemusic.media;

import java.io.File;
import java.security.acl.LastOwnerException;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

/**
 * SongInfoUtils
 * 
 * this class is used to get the message of the assign audio file from database
 * 
 * @author wenliang
 *         Created :2011.06.22
 */

public class MediaHelper {
    /**
     * mContext
     */
    private Context mContext = null;

    /**
     * SongInfoUtils()
     * 
     * @param context
     */
    public MediaHelper(Context context) {
        mContext = context;
    }

    //    /**
    //     * getFileInfo()
    //     * 
    //     * @param aFileAbsoulatePath
    //     * @return
    //     */
    //    public String[] getFileInfo(String aFileAbsoulatePath) {
    //        String[] fileMessage = new String[3];
    //        File file = new File(aFileAbsoulatePath);
    //        String fileName = file.getName();
    //        String filePath = "/mnt" + file.getPath();
    //
    //        if (file.exists()) {
    //            if (mContext != null) {
    //                readDataFromSD();
    //
    //                int count = mSongsList.size();
    //                for (int i = 0; i < count; i++) {
    //                    if (mSongsList.get(i).getmFilePath().equals(filePath) && mSongsList.get(i).getmFileName().equals(fileName)) {
    //                        fileMessage[0] = mSongsList.get(i).getmFileTitle();
    //                        fileMessage[1] = mSongsList.get(i).getmAlbum();
    //                        fileMessage[2] = mSongsList.get(i).getmSinger();
    //                        break;
    //                    }
    //                }
    //            }
    //        }
    //
    //        return fileMessage;
    //    }

    /**
     * readDataFromSD()
     */
    public ArrayList<LocalMusicItem> getLocalMusicList() {
        Cursor cursor = mContext.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.YEAR, MediaStore.Audio.Media.MIME_TYPE, MediaStore.Audio.Media.SIZE, MediaStore.Audio.Media.DATA },
                MediaStore.Audio.Media.MIME_TYPE + "=? or " + MediaStore.Audio.Media.MIME_TYPE + "=?", new String[] { "audio/mpeg", "audio/x-ms-wma" }, null);
        ArrayList<LocalMusicItem> songList = new ArrayList<LocalMusicItem>();
        if (cursor == null) {
            for (int i = 0, N = 10; i < N; i++) {
                LocalMusicItem item = new LocalMusicItem();
                item.setFileName("hello");
                item.setFileTitle(",,,,,,");
                songList.add(item);
            }
            return songList;
        }
        while (cursor.moveToNext()) {
            LocalMusicItem item = getLocalMedia(cursor);
            songList.add(item);
        }
        cursor.close();
        return songList;
    }

    /**
     * getSongList()
     * 
     * @param cursor
     */
    public LocalMusicItem getLocalMedia(Cursor cursor) {
        LocalMusicItem item = new LocalMusicItem();
        item.setFileName(cursor.getString(1));// file Name
        item.setFileTitle(cursor.getString(2));// song name
        item.setDuration(cursor.getInt(3));// play time
        item.setmSinger(cursor.getString(4));// artist
        item.setmAlbum(cursor.getString(5));// album
        if (cursor.getString(6) != null) {
            item.setmYear(cursor.getString(6));
        } else {
            item.setmYear("undefine");
        }
        if ("audio/mpeg".equals(cursor.getString(7).trim())) {// file type
            item.setmFileType("mp3");
        } else if ("audio/x-ms-wma".equals(cursor.getString(7).trim())) {
            item.setmFileType("wma");
        }
        if (cursor.getString(8) != null) {// fileSize
            float temp = cursor.getInt(8) / 1024f / 1024f;
            String sizeStr = (temp + "").substring(0, 4);
            item.setFileSize(sizeStr + "M");
        } else {
            item.setFileSize("undefine");
        }

        if (cursor.getString(9) != null) {//file path
            item.setmFilePath(cursor.getString(9));
        }

        return item;
    }
}
