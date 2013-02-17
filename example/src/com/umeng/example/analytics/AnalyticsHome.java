package com.umeng.example.analytics;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.ReportPolicy;
import com.umeng.common.Log;
import com.umeng.example.R;
import com.umeng.ui.BaseSinglePaneActivity;
import com.umeng.common.*;

public class AnalyticsHome extends BaseSinglePaneActivity {	
	private Context mContext;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;	
		MobclickAgent.setDebugMode(true);
		
//		MobclickAgent.setAutoLocation(true);
//		MobclickAgent.setSessionContinueMillis(1000);
//		MobclickAgent.setUpdateOnlyWifi(false);
//		MobclickAgent.setDefaultReportPolicy(this, ReportPolicy.BATCH_BY_INTERVAL, 5*1000);
		
		MobclickAgent.onError(this);
		MobclickAgent.updateOnlineConfig(this);
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(mContext);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(mContext);
	}
	
	/**
	 * android:onClick="onButtonClick"
	 * @param view
	 */
	public void onButtonClick(View view){
		int id = view.getId();
		if(id == R.id.umeng_example_analytics_online_config){
			
			String onlineParams= MobclickAgent.getConfigParams(mContext, "abc");//the demo param's key is 'abc'
			if(onlineParams.equals("")){
				Toast.makeText(mContext, "Get No Online Params", Toast.LENGTH_SHORT).show();
			}else
				Toast.makeText(mContext, "Online Params:"+ onlineParams, Toast.LENGTH_SHORT).show();
		}else if(id == R.id.umeng_example_analytics_event){
			
			MobclickAgent.onEvent(mContext, "click");
			MobclickAgent.onEvent(mContext, "click", "button");
			
		}else if(id == R.id.umeng_example_analytics_ekv){
			
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("type", "popular");
			map.put("artist", "JJLin");
			
			MobclickAgent.onEvent(mContext, "music", map);
		}else if(id == R.id.umeng_example_analytics_duration){
			
			// We need manual to compute the Events duration 
			MobclickAgent.onEventDuration(mContext, "book", 12000);
			MobclickAgent.onEventDuration(mContext, "book", "chapter1", 23000);
			
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("type", "popular");
			map.put("artist", "JJLin");
			
			MobclickAgent.onEventDuration(mContext, "music", map, 2330000);
	
			
		}else if(id == R.id.umeng_example_analytics_event_begin){
			//Log.i("duration", "start");
			//when the events start
			MobclickAgent.onEventBegin(mContext, "music");
			
			MobclickAgent.onEventBegin(mContext, "music", "one");
			
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("type", "popular");
			map.put("artist", "JJLin");
			
			MobclickAgent.onKVEventBegin(mContext, "music", map, "flag0");
			
		}else if(id == R.id.umeng_example_analytics_event_end){
			
			MobclickAgent.onEventEnd(mContext, "music");
			MobclickAgent.onEventEnd(mContext, "music", "one");	
			
			MobclickAgent.onKVEventEnd(mContext, "music", "flag0");
		}else if(id == R.id.umeng_example_analytics_make_crash){
			"123".substring(10);
		}else if(id == R.id.umeng_example_analytics_js_analytic){
			startActivity( new Intent( this, WebviewAnalytic.class) );
		}else if(id == R.id.umeng_example_analytics_flush){
			MobclickAgent.flush(this);
		}
	}

	@Override
	protected Fragment onCreatePane() {
		return new AnalyticsHomeDashboardFragment();	
	}
	
	public static class AnalyticsHomeDashboardFragment extends Fragment {
		
		@Override
	    public void onAttach(Activity activity) {
	        super.onAttach(activity);
	    }
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			return inflater.inflate(R.layout.umeng_example_analytics, container,false);
		}
	}
}