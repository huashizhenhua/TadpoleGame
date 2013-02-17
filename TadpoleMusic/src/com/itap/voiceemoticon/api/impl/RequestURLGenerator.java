package com.itap.voiceemoticon.api.impl;

import java.util.HashMap;

import android.os.Bundle;

import com.itap.voiceemoticon.api.impl.url.HotVoiceUrl;
import com.itap.voiceemoticon.api.impl.url.RequestUrl;
import com.itap.voiceemoticon.api.impl.url.SearchBusinessUrl;
import com.itap.voiceemoticon.api.impl.url.StatisticsUrl;
import com.itap.voiceemoticon.common.ConstValues;




public class RequestURLGenerator {
	HashMap<Integer, RequestUrl> mStragegies = new HashMap<Integer, RequestUrl>();
	private static RequestURLGenerator mRequestUrlGenerator = null;
	public Bundle generateRequestUrl(int businessId, Bundle data) {
		RequestUrl requestUrl = mStragegies.get(new Integer(businessId));
		if (requestUrl == null)
			return null;
		
		return requestUrl.generateUrlAndBody(data);
	}
	
	public static RequestURLGenerator getInstance() {
		if (mRequestUrlGenerator == null) {
			mRequestUrlGenerator = new RequestURLGenerator();
			mRequestUrlGenerator.addStrategy(new Integer(ConstValues.GETPOPULAR), new HotVoiceUrl());
			mRequestUrlGenerator.addStrategy(new Integer(ConstValues.SEARCH), new SearchBusinessUrl());
			mRequestUrlGenerator.addStrategy(new Integer(ConstValues.SENDSTATISTICS), new StatisticsUrl());
		}
		
		return mRequestUrlGenerator;
	}
	
	public void addStrategy(int id, RequestUrl strategy) {
		mStragegies.put(new Integer(id), strategy);
	}
}
	




