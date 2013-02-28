package com.umeng.example.update;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.umeng.update.UmengDownloadListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.example.R;
import com.umeng.ui.BaseSinglePaneActivity;

public class UpdateHome extends BaseSinglePaneActivity {
	private static final String LOG_TAG = UpdateHome.class.getName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 //如果想程序启动时自动检查是否需要更新， 把下面两行代码加在Activity 的onCreate()函数里。
//		UmengUpdateAgent.setUpdateOnlyWifi(false);
//	 	如果您同时使用了手动更新和自动检查更新，为了避免更新回调被多次调用，请加上下面这句代码
//		UmengUpdateAgent.setOnDownloadListener(null);
		UmengUpdateAgent.update(this);
	}

	@Override
	protected Fragment onCreatePane() {
		return new AnalyticsHomeDashboardFragment();
	}

	public static class AnalyticsHomeDashboardFragment extends Fragment {
		private Context mContext;

		@Override	
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			mContext = activity;
		}

		
		UmengUpdateListener updateListener = new UmengUpdateListener() {
			@Override
			public void onUpdateReturned(int updateStatus,
					UpdateResponse updateInfo) {
				switch (updateStatus) {
				case 0: // has update
					Log.i("--->", "callback result");
					UmengUpdateAgent.showUpdateDialog(mContext, updateInfo);
					break;
				case 1: // has no update
					Toast.makeText(mContext, "没有更新", Toast.LENGTH_SHORT)
							.show();
					break;
				case 2: // none wifi
					Toast.makeText(mContext, "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT)
							.show();
					break;
				case 3: // time out
					Toast.makeText(mContext, "超时", Toast.LENGTH_SHORT)
							.show();
					break;
				}

			}
		};
		
		

		private View.OnClickListener listener = new View.OnClickListener() {
			public void onClick(View v) {
				

				// 如果想程序启动时自动检查是否需要更新， 把下面两行代码加在Activity 的onCreate()函数里。
				com.umeng.common.Log.LOG = true;
				UmengUpdateAgent.setUpdateOnlyWifi(false); // 目前我们默认在Wi-Fi接入情况下才进行自动提醒。如需要在其他网络环境下进行更新自动提醒，则请添加该行代码
				UmengUpdateAgent.setUpdateAutoPopup(false);
				UmengUpdateAgent.setUpdateListener(updateListener);
				
				UmengUpdateAgent.setOnDownloadListener(new UmengDownloadListener(){

					@Override
					public void OnDownloadEnd(int result) {
						Log.i(LOG_TAG, "download result : " + result);
						Toast.makeText(mContext, "download result : " + result , Toast.LENGTH_SHORT)
						.show();
					}
					
				});
				
				UmengUpdateAgent.update(mContext);
			}
		};

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View root = inflater.inflate(R.layout.umeng_example_update_main,
					container, false);
			Button checkUpdate = (Button) root
					.findViewById(R.id.umeng_example_update_btn_check_update);
			checkUpdate.setOnClickListener(listener);
			return root;
		}
	}
}