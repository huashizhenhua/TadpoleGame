package com.tadpolemusic.media.service;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.tadpolemusic.media.MusicData;
import com.tadpolemusic.media.MusicPlayMode;
import com.tadpolemusic.media.MusicPlayState;
import com.tadpolemusic.media.PlayListInfo;
import com.tadpolemusic.media.aidl.MusicConnect;
import com.tadpolemusic.media.interface1.IOnServiceConnectComplete;

public class MusicPlayerProxy {

	private final static String TAG = "ServiceManager";

	private final static String SERVICE_NAME = "com.tadpolemusic.media.service.musicservice";

	private Boolean mConnectComplete;
	
	private ServiceConnection mServiceConnection;

	private MusicConnect mMusicConnect;
	
	private IOnServiceConnectComplete mIOnServiceConnectComplete;
	
	private Context mContext;
	
	
	public MusicPlayerProxy(Context context)
	{
		mContext = context;
		defaultParam();
	}
	
	private void defaultParam()
	{
		mServiceConnection = new ServiceConnection() {
			
			@Override
			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub
				Log.i(TAG, "onServiceDisconnected");
				
			}
			
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				// TODO Auto-generated method stub
				Log.i(TAG, "onServiceConnected");
				mMusicConnect = MusicConnect.Stub.asInterface(service);
				if (mMusicConnect != null)
				{
					if (mIOnServiceConnectComplete != null)
					{
						mIOnServiceConnectComplete.OnServiceConnectComplete();
					}
				}
			}
		};
		
		mConnectComplete = false;
		mMusicConnect = null;
	}
	
	public boolean connectService()
	{
		if (mConnectComplete == true)
		{
			return true;
		}
		
		Intent intent = new Intent(SERVICE_NAME);
		if (mContext != null)
		{
			Log.i(TAG, "begin to connectService	");
			mContext.bindService(intent, mServiceConnection,  Context.BIND_AUTO_CREATE);
			mContext.startService(intent);
			mConnectComplete = true;
			return  true;
		}
		
		return false;
	}
	
	public boolean disconnectService()
	{
		if (mConnectComplete == false)
		{
			return true;
		}
		
		Intent intent = new Intent(SERVICE_NAME);
		if (mContext != null)
		{
			Log.i(TAG, "begin to disconnectService");
			mContext.unbindService(mServiceConnection);	
			mMusicConnect = null;
			mConnectComplete = false;
			return true;
		}
		
		return false;
	}
	
	public void exit()
	{
		if (mConnectComplete)
		{
			try {
				mMusicConnect.exit();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Intent intent = new Intent(SERVICE_NAME);
			mContext.unbindService(mServiceConnection);	
			mContext.stopService(intent);
			mMusicConnect = null;
			mConnectComplete = false;

		}
	}
	
	public void reset()
	{
		if (mConnectComplete)
		{
			try {
				mMusicConnect.exit();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
			}
		}
	}
	
	public void setOnServiceConnectComplete(IOnServiceConnectComplete IServiceConnect)
	{
		mIOnServiceConnectComplete = IServiceConnect;
	}
	
	public void refreshMusicList(String playListID, List<MusicData> FileList)
	{	
		if (mMusicConnect != null)
		{
			try {
				mMusicConnect.refreshMusicList(playListID, FileList);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
	}
	
	
	public List<MusicData> getFileList()
	{
		List<MusicData> musicFileList = new ArrayList<MusicData>();
		try {
			Log.i(TAG, "getFileList	begin...");
			mMusicConnect.getFileList(musicFileList);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return musicFileList;
	}
	
	
	public boolean rePlay()
	{
		if (mMusicConnect != null)
		{
			try {
				Log.i(TAG, "mMusicConnect.rePlay()");
				return mMusicConnect.rePlay();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
		
		return false;
	}
	   
	public  boolean play(int position)
	{
		if (mMusicConnect != null)
		{
			try {
				Log.i(TAG, "mMusicConnect.play = " + position);
				return mMusicConnect.play(position);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
		
		return false;
		
	}
	   
	public boolean pause()
	{
		if (mMusicConnect != null)
		{
			try {
				return mMusicConnect.pause();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
		
		return false;
	}
	   
	public boolean stop()
	{
		if (mMusicConnect != null)
		{
			try {
				return mMusicConnect.stop();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
		
		return false;
	}
	   
	public boolean playNext()
	{
		if (mMusicConnect != null)
		{
			try {
				return mMusicConnect.playNext();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
		
		return false;
	}
	   
	public boolean playPre()
	{
		if (mMusicConnect != null)
		{
			try {
				return mMusicConnect.playPre();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
		
		return false;
	}
	   
	public boolean seekTo(int rate)
	{
		if (mMusicConnect != null)
		{
			try {
				return mMusicConnect.seekTo(rate);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
		
		return false;
	}
	 
	   
	public int getCurPosition()
	{
		if (mMusicConnect != null)
		{
			try {
				return mMusicConnect.getCurPosition();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return 0;
	}
	   
	public int getDuration()
	{
		if (mMusicConnect != null)
		{
			try {
				return mMusicConnect.getDuration();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
		
		return 0;
	}
	   
	public int getPlayState()
	{
		if (mMusicConnect != null)
		{
			try {
				return mMusicConnect.getPlayState();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		
		return MusicPlayState.MPS_NOFILE;
	}

	public void setPlayMode(int mode)
	{

		if (mMusicConnect != null)
		{
			try {
				mMusicConnect.setPlayMode(mode);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}
	
	public int getPlayMode()
	{
		if (mMusicConnect != null)
		{
			try {
				return mMusicConnect.getPlayMode();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		
		return MusicPlayMode.MPM_LIST_LOOP_PLAY;
	}
	
	public void sendPlayStateBrocast()
	{
		if (mMusicConnect != null)
		{
			try {
				mMusicConnect.sendPlayStateBrocast();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}

    public PlayListInfo getCurPlayListInfo() {
        if (mMusicConnect != null)
        {
            try {
             return   mMusicConnect.getCurPlayListInfo();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;   
    }
    
    public  void getCurrentPlayListInfo(PlayListInfo info) {
        if (mMusicConnect != null)
        {
            try {
                mMusicConnect.getCurrentPlayListInfo(info);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
	
}
