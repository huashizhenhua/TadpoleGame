
package com.umeng.example.xp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.umeng.common.Log;
import com.umeng.example.R;
import com.umeng.ui.BaseSinglePaneActivity;
import com.umeng.xp.common.ExchangeConstants;
import com.umeng.xp.controller.ExchangeDataService;
import com.umeng.xp.controller.XpListenersCenter.AdapterListener;
import com.umeng.xp.controller.XpListenersCenter.FitType;
import com.umeng.xp.view.ExchangeViewManager;
//test
public class ContainerExample extends BaseSinglePaneActivity {
	public static Listener listener;
	
	public  static interface Listener{
		public void onAdd(ExchangeDataService service,ExchangeViewManager manager);
	}
	
	@Override
	protected Fragment onCreatePane() {
		return new ContainerExampleFragment();
	}
	
	public static class ContainerExampleFragment extends Fragment{
		Context mContext;
		
		@Override
	    public void onAttach(Activity activity) {
	        super.onAttach(activity);
	        mContext = activity;
	    }
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			Log.LOG =true;
			View root = inflater.inflate(
					R.layout.umeng_example_xp_container_activity, container,
					false);
			
			ExchangeConstants.CONTAINER_AUTOEXPANDED=false;
			
			ViewGroup fatherLayout = (ViewGroup) root.findViewById(R.id.ad);
			ListView listView = (ListView) root.findViewById(R.id.list);
			
			ExchangeDataService exchangeDataService = XpHome.preloadDataService != null ? XpHome.preloadDataService : new ExchangeDataService("");
			ExchangeViewManager exchangeViewManager = new ExchangeViewManager(mContext,exchangeDataService);
			
			AdapterListener listener = new AdapterListener() {
				@Override
				public void onFitType(View itemview, FitType fitType) {
					Button button  = (Button) itemview.findViewById(R.id.umeng_xp_ad_action_btn);
					switch (fitType) {
					case BROWSE:
						button.setText("浏览");
						break;
					case OPEN:
						button.setText("打开");
						break;
					case PHONE:
						button.setText("拨打");
						break;
					case DOWNLOAD:
						button.setText("下载");
						break;
					case NEW:
						button.setText("New");
						break;
					}
				}
			};
			
			exchangeViewManager.addView(fatherLayout, listView,listener);
			
			return root;
		}
	}
}
