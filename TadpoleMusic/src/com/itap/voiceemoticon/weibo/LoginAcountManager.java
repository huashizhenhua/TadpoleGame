package com.itap.voiceemoticon.weibo;

import java.util.ArrayList;

import org.json.JSONArray;
import org.tadpoleframework.common.JSONUtil;
import org.tadpoleframework.common.StringUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.weibo.sdk.android.Oauth2AccessToken;

/**
 * 该类用于保存Oauth2AccessToken到sharepreference，并提供读取功能
 * 
 * @author xiaowei6@staff.sina.com.cn
 */
public class LoginAcountManager {

	private static final String PREFERENCES_NAME = "com_weibo_sdk_android";

	public static final String KEY_LOGIN_ACOUNTS = "loginAcounts";

	private static Context sContext;

	private static LoginAcountManager mMgr = null;

	public LoginAcountManager() {
	}

	public static LoginAcountManager getInstance() {
		if (null == mMgr) {
			mMgr = new LoginAcountManager();
		}
		return mMgr;
	}

	public static void init(Context context) {
		sContext = context.getApplicationContext();
	}

	public boolean isLogin() {
		Oauth2AccessToken token = getLastLoginAccessToken();
		if (token == null) {
			return false;
		}
		return token.isSessionValid();
	}
	
	public void logout() {
		LoginAccount loginAccount = getLastLoginAccount();
		if (null != loginAccount) {
			delete(loginAccount);
		}
	}

	/**
	 * 从SharedPreferences读取accessstoken
	 * 
	 * @param context
	 * @return Oauth2AccessToken
	 */
	public Oauth2AccessToken getLastLoginAccessToken() {
		Oauth2AccessToken token = null;
		ArrayList<LoginAccount> list = getLoginAccountList();
		for (LoginAccount item : list) {
			if (item.lastLogin) {
				token = new Oauth2AccessToken();
				token.setExpiresTime(item.expiresTime);
				token.setToken(item.token);
				break;
			}
		}
		return token;
	}
	
	public LoginAccount getLastLoginAccount() {
		ArrayList<LoginAccount> list = getLoginAccountList();
		for (LoginAccount item : list) {
			if (item.lastLogin) {
				return item;
			}
		}
		return null;
	}

	public void delete(LoginAccount loginAccount) {
		if (null == loginAccount) {
			return;
		}

		ArrayList<LoginAccount> list = getLoginAccountList();
		LoginAccount itemToDel = null;
		for (LoginAccount item : list) {
			if (null != item && item.uid == loginAccount.uid) {
				itemToDel = item;
				break;
			}
		}

		if (null != itemToDel) {
			list.remove(itemToDel);
		}

		saveLoginAccountList(list);

		return;
	}

	public void addOrUpdateLoginAcount(LoginAccount loginAccount) {
		if (null == loginAccount) {
			return;
		}

		loginAccount.lastLogin = true;

		if (0L == loginAccount.createAt) {
			loginAccount.createAt = System.currentTimeMillis();
		}

		ArrayList<LoginAccount> list = getLoginAccountList();
		boolean isUpdated = false;
		for (LoginAccount item : list) {
			if (item.uid == loginAccount.uid) {
				item.lastLogin = loginAccount.lastLogin;
				item.expiresTime = loginAccount.expiresTime;
				item.token = loginAccount.token;
				item.lastLogin = loginAccount.lastLogin;
				isUpdated = true;
			} else {
				item.lastLogin = false;
			}
		}

		if (!isUpdated) {
			list.add(loginAccount);
		}

		saveLoginAccountList(list);
	}

	private String readString(String key) {
		SharedPreferences pref = sContext.getSharedPreferences(
				PREFERENCES_NAME, Context.MODE_APPEND);
		return pref.getString(key, "");
	}

	private void writeString(String key, String value) {
		SharedPreferences pref = sContext.getSharedPreferences(
				PREFERENCES_NAME, Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.putString(key, value);
		// editor.putString("token", token.getToken());
		// editor.putLong("expiresTime", token.getExpiresTime());
		editor.commit();
	}

	public void saveLoginAccountList(ArrayList<LoginAccount> list) {
		JSONArray jsonArr = JSONUtil.convertToJSONArray(list,
				LoginAccount.class);
		writeString(KEY_LOGIN_ACOUNTS, jsonArr.toString());
	}

	public ArrayList<LoginAccount> getLoginAccountList() {
		ArrayList<LoginAccount> list = new ArrayList<LoginAccount>();
		String loginAccountsJsonStr = readString(KEY_LOGIN_ACOUNTS);
		System.out.println("loginAccountsJsonStr = " + loginAccountsJsonStr);

		if (StringUtil.isBlank(loginAccountsJsonStr)) {
			return list;
		}
		list = JSONUtil.convertToList(loginAccountsJsonStr, LoginAccount.class);
		return list;
	}

}
