package com.itap.voiceemoticon.weibo;

import org.json.JSONObject;

import com.weibo.sdk.android.Oauth2AccessToken;
import com.zenip.weibo.sdk.android.api.AccountAPI;

public class Account {

	private static final String TAG = "Account";

	public long uid;

	public static Account fromGetUid(String response) throws Exception {
		Account ac = null;
		if (response != null) {
			JSONObject jsonObj = new JSONObject(response);
			ac = new Account();
			ac.uid = jsonObj.optLong("uid", 0L);
		}
		return ac;
	}

	public static User getUserPreferCache(Oauth2AccessToken token)
			throws Exception {
		AccountAPI acountAPI = new AccountAPI(token);
		Account account = fromGetUid(acountAPI.getUid());
		String response = User.getResponseFromShowJson(account.uid);
		User user = User.fromResponse(response);
		return user;
	}
}
