package com.itap.voiceemoticon.api.impl.url;

import android.os.Bundle;

public class HotVoiceUrl extends RequestUrl {
	public Bundle generateUrlAndBody(Bundle data){
		String serverUrl = getServerUrl();
		String url = serverUrl + "get_hot_voice";
		Bundle result = new Bundle();
		result.putString("url", url);		
		return result;
	}
}