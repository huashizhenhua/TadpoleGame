package com.itap.voiceemoticon;

import java.util.ArrayList;

import org.tadpoleframework.thread.ForegroundThread;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.itap.voiceemoticon.api.Voice;
import com.itap.voiceemoticon.api.VoiceEmoticonApi;
import com.itap.voiceemoticon.api.impl.VoiceEmoticonApiImpl;
import com.itap.voiceemoticon.db.DaoFactory;
import com.itap.voiceemoticon.db.VoiceDao;
import com.itap.voiceemoticon.media.MusicPlayer;
import com.itap.voiceemoticon.third.WeixinHelper;
import com.itap.voiceemoticon.weibo.LoginAcountManager;
import com.itap.voiceemoticon.weibo.WeiboHelper;
import com.itap.voiceemoticon.weibo.WeiboLoginListener;
import com.tencent.tauth.Tencent;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.zenip.weibo.sdk.android.api.UsersAPI;

public class VEApplication extends Application {

	public static Context sContext = null;

	public static final String TAG = "VEApplication";

	public static VoiceEmoticonApi getVoiceEmoticonApi() {
		return new VoiceEmoticonApiImpl();
	}

	public static final String PREF_NAME = "voiceemoticon";

	public static final String PREF_KEY_HIDE_TITLE = "hidetitle";
	
	private static SharedPreferences sPrefs;

	private static Oauth2AccessToken sSinaToken = null;

	public static void setSinaToken(Oauth2AccessToken token) {
		sSinaToken = token;
	}

	public static UsersAPI getUsersAPI() {
		return new UsersAPI(sSinaToken);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		sPrefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
		sContext = this;

		LoginAcountManager.init(this);
		WeiboHelper.init(this);

		ForegroundThread.startRun();
	}

	public static boolean getHideTitle() {
		return sPrefs.getBoolean(PREF_KEY_HIDE_TITLE, false);
	}

	public static void setHideTitle(boolean flag) {
		Editor editor = sPrefs.edit();
		editor.putBoolean(PREF_KEY_HIDE_TITLE, flag);
		editor.commit();
	}
	
	private static MusicPlayer mMusicPlayer;

	private static Tencent sInstance;

	public static synchronized MusicPlayer getMusicPlayer(Context context) {
		if (mMusicPlayer == null) {
			mMusicPlayer = new MusicPlayer(context.getApplicationContext());
		}
		return mMusicPlayer;
	}

	public static void runOnThread(Runnable runnable) {
		new Thread(runnable).start();

	}

	// -------------------------------------------------------------
	// Music Collected Cache
	// -------------------------------------------------------------

	private static ArrayList<String> sMusicCollectedCached = null;

	/**
	 * 是否已经被收藏 instruction。
	 */
	public static boolean isCollected(Context context, String path) {
		if (sMusicCollectedCached == null) {
			reloadVoiceCollectedCache(context);
		}
		return sMusicCollectedCached.contains(path);
	}

	public static void reloadVoiceCollectedCache(Context context) {
		sMusicCollectedCached = new ArrayList<String>();
		VoiceDao dao = DaoFactory.getInstance(context).getVoiceDao();
		ArrayList<Voice> list = dao.allVoices();
		for (int i = 0, len = list.size(); i < len; i++) {
			Voice voice = list.get(i);
			sMusicCollectedCached.add(voice.url);
		}
	}

	private static final Handler sHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			if (MsgDef.MSG_TOAST == msg.what) {
				Toast.makeText(sContext, (String) msg.obj, Toast.LENGTH_LONG)
						.show();
			}
			return false;
		}
	});

	public static void toast(String msg) {
		if (null == sContext) {
			return;
		}
		Message.obtain(sHandler, MsgDef.MSG_TOAST, msg).sendToTarget();
	}
}
