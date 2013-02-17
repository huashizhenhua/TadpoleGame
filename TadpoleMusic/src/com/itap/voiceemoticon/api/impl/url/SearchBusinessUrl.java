package com.itap.voiceemoticon.api.impl.url;

import java.net.URLEncoder;

import android.os.Bundle;

import com.itap.voiceemoticon.common.ConstValues;

public class SearchBusinessUrl extends RequestUrl {
	public Bundle generateUrlAndBody(Bundle data){
		String serverUrl = getServerUrl();
		String url = serverUrl + "voice_search?key=" + URLEncoder.encode(data.getString(ConstValues.KEY)) + "&start_index=" + data.getInt(ConstValues.STARTINDEX) + "&max_results=" + data.getInt(ConstValues.MAXRESULT);
		Bundle result = new Bundle();
		result.putString("url", url);		
		return result;
	}
}