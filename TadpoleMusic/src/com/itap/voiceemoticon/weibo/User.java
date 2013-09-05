package com.itap.voiceemoticon.weibo;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tadpoleframework.common.JSONUtil;
import org.tadpoleframework.common.StringUtil;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import com.itap.voiceemoticon.VEApplication;
import com.zenip.weibo.sdk.android.api.UsersAPI;

public class User implements Serializable {

	private static final long serialVersionUID = 7431294131953990833L;

	public long id;

	public String screen_name;

	public String name;

	public String profile_image_url;

	public String avatar_large;

	public int followers_count;

	public int friends_count;

	public int statuses_count;

	public String location;

	public String description;

	public String url;

	public static User fromResponse(String response) {

		System.out.println("fromResponse = " + response);

		if (response == null) {
			return null;
		}
		try {
			JSONObject jsonObj = new JSONObject(response);
			return fromResponse(jsonObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static User fromResponse(JSONObject jsonObj) {
		if (jsonObj == null) {
			return null;
		}

		try {
			User ret = new User();
			// ret.id = jsonObj.getLong("id");
			// ret.profile_image_url = jsonObj.getString("profile_image_url");
			// ret.screen_name = jsonObj.optString("screen_name");
			// ret.avatar_large = jsonObj.optString("avatar_large");
			// ret.name = jsonObj.optString("name");

			JSONUtil.copyJsonToObj(ret, User.class, jsonObj);
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static User getUserFromShowJson(String screen_name, long uid)
			throws Exception {
		String response = getResponseFromShowJson(screen_name, uid);
		User user = User.fromResponse(response);
		return user;
	}

	public static String getResponseFromShowJson(String screen_name, long uid)
			throws Exception {
		String response = null;
		// prefer uid
		if (uid != 0) {
			response = VEApplication.getUsersAPI().show(uid);
		} else if (StringUtil.isNotEmpty(screen_name)) {
			response = VEApplication.getUsersAPI().show(screen_name);
		}
		return response;
	}

	public static User getUserFromShowJson(long uid) throws Exception {
		return getUserFromShowJson("", uid);
	}

	public static String getResponseFromShowJson(long uid) throws Exception {
		return getResponseFromShowJson("", uid);
	}

}
