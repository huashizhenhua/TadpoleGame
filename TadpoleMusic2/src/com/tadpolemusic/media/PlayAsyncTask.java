package com.tadpolemusic.media;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.tadpolemusic.TMLog;
import com.tadpolemusic.VEApplication;
import com.tadpolemusic.media.service.MusicPlayerProxy;

/**
 * 
 * play music.
 * 
 * if the play list doesn't contain the music, we will refresh it.
 * 
 * <br>==========================
 * <br> author：Zenip
 * <br> email：lxyczh@gmail.com
 * <br> create：2013-3-6
 * <br>==========================
 */
public class PlayAsyncTask extends AsyncTask<Integer, String, String> {

    public static final String TAG = "PlayAsyncTask";

    private List<? extends MusicData> mDataList;
    private String mPlayListId;
    private Context mContext;

    public PlayAsyncTask(Context context, List<? extends MusicData> dataList, String playListId) {
        mDataList = dataList;
        mPlayListId = playListId;
        mContext = context;
    }

    @Override
    protected String doInBackground(Integer... params) {
        if (params.length == 0 || mDataList == null || mContext == null) {
            Log.d("PlayAsyncTask", "调用参数出错");
            return "";
        }

        int position = params[0];

        final MusicPlayerProxy mpProxy = VEApplication.getMusicPlayer(mContext);
        PlayListInfo info = new PlayListInfo();
        mpProxy.getCurrentPlayListInfo(info);

        boolean needToRefresh = !(mPlayListId.equals(info.playListID) && (position < info.listSize));
        if (needToRefresh) {
            mpProxy.refreshMusicList(mPlayListId, (List<MusicData>) mDataList);
            TMLog.step(TAG, "refreshMusicList");
        }
        mpProxy.play(position);

        return "";
    }

}
