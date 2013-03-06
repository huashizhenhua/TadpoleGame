package com.tadpolemusic.media.service;

import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.tadpolemusic.media.MusicData;
import com.tadpolemusic.media.MusicPlayer;
import com.tadpolemusic.media.PlayListInfo;
import com.tadpolemusic.media.aidl.MusicConnect;
import com.tadpolemusic.media.aidl.MusicConnect.Stub;

public class MusicService extends Service {

    private static final String TAG = "MusicService";

    private MusicPlayer m_mMusicPlayer;

    private SDStateBrocast mSDStateBrocast;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return mBinder;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        m_mMusicPlayer = new MusicPlayer(this);


        mSDStateBrocast = new SDStateBrocast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        intentFilter.addDataScheme("file");
        registerReceiver(mSDStateBrocast, intentFilter);

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub

        unregisterReceiver(mSDStateBrocast);

        super.onDestroy();


    }



    private MusicConnect.Stub mBinder = new Stub() {

        @Override
        public void refreshMusicList(String playListID, List<MusicData> musicFileList) throws RemoteException {
            // TODO Auto-generated method stub
            m_mMusicPlayer.refreshMusicList(playListID, musicFileList);
        }


        @Override
        public void getFileList(List<MusicData> musicFileList) throws RemoteException {
            // TODO Auto-generated method stub
            List<MusicData> tmp = m_mMusicPlayer.getFileList();
            int count = tmp.size();
            for (int i = 0; i < count; i++) {
                musicFileList.add(tmp.get(i));
            }
        }

        @Override
        public int getCurPosition() throws RemoteException {
            // TODO Auto-generated method stub
            return m_mMusicPlayer.getCurPosition();
        }

        @Override
        public int getDuration() throws RemoteException {
            // TODO Auto-generated method stub
            return m_mMusicPlayer.getDuration();
        }

        @Override
        public boolean pause() throws RemoteException {
            // TODO Auto-generated method stub
            return m_mMusicPlayer.pause();
        }

        @Override
        public boolean play(int position) throws RemoteException {
            // TODO Auto-generated method stub

            Log.i(TAG, "play pos = " + position);
            return m_mMusicPlayer.play(position);
        }

        @Override
        public boolean playNext() throws RemoteException {
            // TODO Auto-generated method stub
            return m_mMusicPlayer.playNext();
        }

        @Override
        public boolean playPre() throws RemoteException {
            // TODO Auto-generated method stub
            return m_mMusicPlayer.playPre();
        }

        @Override
        public boolean rePlay() throws RemoteException {
            // TODO Auto-generated method stub
            return m_mMusicPlayer.replay();
        }

        @Override
        public boolean seekTo(int rate) throws RemoteException {
            // TODO Auto-generated method stub
            return m_mMusicPlayer.seekTo(rate);
        }

        @Override
        public boolean stop() throws RemoteException {
            // TODO Auto-generated method stub
            return m_mMusicPlayer.stop();
        }

        @Override
        public int getPlayState() throws RemoteException {
            // TODO Auto-generated method stub
            return m_mMusicPlayer.getPlayState();
        }

        @Override
        public void exit() throws RemoteException {
            // TODO Auto-generated method stub
            m_mMusicPlayer.exit();
        }


        @Override
        public void sendPlayStateBrocast() throws RemoteException {
            // TODO Auto-generated method stub
            m_mMusicPlayer.sendPlayStateBrocast();
        }


        @Override
        public void setPlayMode(int mode) throws RemoteException {
            // TODO Auto-generated method stub
            m_mMusicPlayer.setPlayMode(mode);
        }


        @Override
        public int getPlayMode() throws RemoteException {
            // TODO Auto-generated method stub
            return m_mMusicPlayer.getPlayMode();
        }


        @Override
        public PlayListInfo getCurPlayListInfo() throws RemoteException {
            // TODO Auto-generated method stub
            return m_mMusicPlayer.getCurPlayListInfo();
        }


        @Override
        public void getCurrentPlayListInfo(PlayListInfo info) throws RemoteException {
            m_mMusicPlayer.getCurrentPlayListInfo(info);
        }
    };

    class SDStateBrocast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {

            } else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {

            } else if (Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(action)) {

            } else if (Intent.ACTION_MEDIA_EJECT.equals(action)) {
                m_mMusicPlayer.exit();
            }

        }
    }
}
