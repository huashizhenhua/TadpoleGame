package com.itap.voiceemoticon.api.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.tadpoleframework.common.ArrayUtil;
import org.tadpoleframework.common.JSONUtil;

import android.os.Bundle;
import android.util.Log;

import com.itap.voiceemoticon.VEApplication;
import com.itap.voiceemoticon.api.PageList;
import com.itap.voiceemoticon.api.VEHttpCaller;
import com.itap.voiceemoticon.api.VEResponse;
import com.itap.voiceemoticon.api.Voice;
import com.itap.voiceemoticon.api.VoiceEmoticonApi;
import com.itap.voiceemoticon.common.ConstValues;
import com.itap.voiceemoticon.db.UserVoice;
import com.itap.voiceemoticon.util.HttpManager;
import com.itap.voiceemoticon.weibo.VEAccount;
import com.itap.voiceemoticon.weibo.WeiboHelper;

public class VoiceEmoticonApiImpl implements VoiceEmoticonApi {

	private RequestURLGenerator mRequestURLGenerator;

	private ServerRequestController mServerRequestController;

	public VoiceEmoticonApiImpl() {
		mRequestURLGenerator = RequestURLGenerator.getInstance();
	}

	@Override
	public PageList<Voice> getHostVoicesList(int startIndex, int maxResults) {
		Log.e(VEApplication.TAG, "getHostVoicesList called");
		Bundle data = mRequestURLGenerator.generateRequestUrl(
				ConstValues.GETPOPULAR, null);
		String url = data.getString("url");
		url = url + "?start_index=" + startIndex + "&max_results=" + maxResults;
		VEResponse resp = VEHttpCaller.doGet(url);
		if (resp.isSuccess()) {
			return Voice.buildPageListFromJSON(resp.data);
		}
		return null;
	}

	@Override
	public PageList<Voice> searchHostVoices(String searchKey, int startIndex,
			int maxResults) {
		Log.e(VEApplication.TAG, "searchHostVoices called");
		Bundle bundle = new Bundle();
		bundle.putString(ConstValues.KEY, searchKey);
		bundle.putInt(ConstValues.STARTINDEX, startIndex);
		bundle.putInt(ConstValues.MAXRESULT, maxResults);
		Bundle data = mRequestURLGenerator.generateRequestUrl(
				ConstValues.SEARCH, bundle);
		String url = data.getString("url");
		url = url + "?start_index=" + startIndex + "&max_results=" + maxResults;
		VEResponse resp = VEHttpCaller.doGet(url);
		if (resp.isSuccess()) {
			return Voice.buildPageListFromJSON(resp.data);
		}
		return null;
	}

	@Override
	public void statistics(ArrayList<String> urlList) {
		Log.e(VEApplication.TAG, "statistics called");
		Bundle bundle = new Bundle();
		String[] strArr = (String[]) urlList
				.toArray(new String[urlList.size()]);
		bundle.putStringArray(ConstValues.HOTSTAT, strArr);
		Bundle data = mRequestURLGenerator.generateRequestUrl(
				ConstValues.SENDSTATISTICS, bundle);
		String url = data.getString("url");
		String body = data.getString("body");
		VEResponse resp = VEHttpCaller.doPost(url, body);
		return;
	}

	private static final String USER_VOICE_UPLOAD_URL = "http://vetest.sinaapp.com/user_voice_get";

	@Override
	public ArrayList<UserVoice> getList(String uid, String platform) {
		// 通过Map构造器传参
		String url = USER_VOICE_UPLOAD_URL + "?uid=" + uid + "&platform="
				+ platform;
		VEResponse veResponse = VEHttpCaller.doGet(url);
		if (veResponse.isSuccess()) {
			JSONObject jsonObj = veResponse.data;
			JSONArray jsonArr = jsonObj.optJSONArray("user_voice");
			ArrayList<UserVoice> list = JSONUtil.convertToList(jsonArr,
					UserVoice.class);
			return list;
		}
		return new ArrayList<UserVoice>();
	}

	private static final String USER_VOICE_DELETE_URL = "http://vetest.sinaapp.com/user_voice_delete";

	@Override
	public void delete(long[] idArr) {
		String url = USER_VOICE_DELETE_URL;
		String body = "{\"ids\":" + ArrayUtil.join(idArr) + "}";
		VEResponse response = VEHttpCaller.doPost(url, body);
	}
}
