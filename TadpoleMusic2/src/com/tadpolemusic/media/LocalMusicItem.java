package com.tadpolemusic.media;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import android.app.Activity;

/**
 * Song
 * 
 * this is the message set for song
 * 
 * @author wenliang Created :2011.06.22
 */

public class LocalMusicItem {
    
    /**
     * fileName
     */
    private String mFileName = "";

    /**
     * song name
     */
    private String mFileTitle = "";

    /**
     * play total time
     */
    private int mDuration = 0;

    /**
     * singer
     */
    private String mSinger = "";

    /**
     * album name
     */
    private String mAlbum = "";

    /**
     * mYear
     */
    private String mYear = "";

    /**
     * mFileType
     */
    private String mFileType = "";

    /**
     * mFileSize
     */
    private String mFileSize = "";

    /**
     * mFilePath
     */
    private String mFilePath = "";


    /**
     * setFileName()
     * 
     * @param fileName
     */
    public void setFileName(String fileName) {
        this.mFileName = fileName;
    }

    /**
     * getFileTitle()
     * 
     * @return
     */
    public String getFileTitle() {
        return mFileTitle;
    }

    /**
     * setFileTitle()
     * 
     * @param fileTitle
     */
    public void setFileTitle(String fileTitle) {
        this.mFileTitle = fileTitle;
    }

    /**
     * getDuration()
     * 
     * @return
     */
    public int getDuration() {
        return mDuration;
    }

    /**
     * setDuration()
     * 
     * @param duration
     */
    public void setDuration(int duration) {
        this.mDuration = duration;
    }

    /**
     * getmSinger()
     * 
     * @return
     */
    public String getSinger() {
        return mSinger;
    }

    /**
     * setmSinger()
     * 
     * @param mSinger
     */
    public void setmSinger(String mSinger) {
        this.mSinger = mSinger;
    }

    /**
     * getmAlbum()
     * 
     * @return
     */
    public String getmAlbum() {
        return mAlbum;
    }

    /**
     * setmAlbum()
     * 
     * @param mAlbum
     */
    public void setmAlbum(String mAlbum) {
        this.mAlbum = mAlbum;
    }

    /**
     * getmYear()
     * 
     * @return
     */
    public String getmYear() {
        return mYear;
    }

    /**
     * setmYear()
     * 
     * @param mYear
     */
    public void setmYear(String mYear) {
        this.mYear = mYear;
    }

    /**
     * getmFileType()
     * 
     * @return
     */
    public String getmFileType() {
        return mFileType;
    }

    /**
     * setmFileType()
     * 
     * @param mFileType
     */
    public void setmFileType(String mFileType) {
        this.mFileType = mFileType;
    }

    /**
     * getmFileSize()
     * 
     * @return
     */
    public String getmFileSize() {
        return mFileSize;
    }

    /**
     * setmFileSize()
     * 
     * @param mFileSize
     */
    public void setFileSize(String mFileSize) {
        this.mFileSize = mFileSize;
    }

    /**
     * getmFilePath()
     * 
     * @return
     */
    public String getmFilePath() {
        return mFilePath;
    }

    /**
     * setmFilePath()
     * 
     * @param mFilePath
     */
    public void setmFilePath(String mFilePath) {
        this.mFilePath = mFilePath;
    }

    public String getFirstLetterInUpcase() {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        String str = PinyinHelper.toHanyuPinyinString(getFileTitle(), format, "");
        if (str != null && str.length() > 0) {
            if (Character.isLetter(str.toCharArray()[0])) {
                return ((String) str.subSequence(0, 1)).toUpperCase();
            }
        }
        return "?";
    }

    public void delete(Activity mContext) {

    }

}