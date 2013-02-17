package com.umeng.example.fb;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umeng.example.R;
import com.umeng.fb.NotificationType;
import com.umeng.fb.UMFeedbackService;
import com.umeng.ui.BaseSinglePaneActivity;

public class FbHome extends BaseSinglePaneActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected Fragment onCreatePane() {
		return new FbHomeFragment();
	}

	/**
	 * Do not change this to anonymous class as it will crash when orientation
	 * changes.
	 * 
	 * @author GC
	 * 
	 */
	public static class FbHomeFragment extends Fragment {
		Context mContext;

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			mContext = activity;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View root = inflater.inflate(R.layout.umeng_example_fb_home,
					container, false);
			root.findViewById(R.id.umeng_example_fb_home_btn_simple)
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							
							/*
							// “友盟反馈”还支持反馈信息的定制化，以便在反馈页面中收集额外信息。例如，开发者想进行有奖反馈，他可能需要收集用户的QQ、手机号等联系方式用于确认，另外还可能需要用户姓名、奖品寄送地址等信息。
							FeedBackListener listener = new FeedBackListener() {
								@Override
								public void onSubmitFB(Activity activity) {

									EditText phoneText = (EditText) activity
											.findViewById(R.id.feedback_phone);
									EditText qqText = (EditText) activity
											.findViewById(R.id.feedback_qq);
									EditText nameText = (EditText) activity
											.findViewById(R.id.feedback_name);
									EditText emailText = (EditText) activity
											.findViewById(R.id.feedback_email);

									Map<String, String> contactMap = new HashMap<String, String>();
									contactMap.put("phone", phoneText.getText()
											.toString());
									contactMap.put("qq", qqText.getText()
											.toString());
									UMFeedbackService.setContactMap(contactMap);

									Map<String, String> remarkMap = new HashMap<String, String>();
									remarkMap.put("name", nameText.getText()
											.toString());
									remarkMap.put("email", emailText
											.getText().toString());
									UMFeedbackService.setRemarkMap(remarkMap);
								}

								@Override
								public void onResetFB(Activity activity,
										Map<String, String> contactMap,
										Map<String, String> remarkMap) {
							`
									// FB initialize itself,load other attribute
									// from local storage and set them
									EditText phoneText = (EditText) activity
											.findViewById(R.id.feedback_phone);
									EditText qqText = (EditText) activity
											.findViewById(R.id.feedback_qq);
									EditText nameText = (EditText) activity
											.findViewById(R.id.feedback_name);
									EditText emailText = (EditText) activity
											.findViewById(R.id.feedback_email);

									if (remarkMap != null) {
										nameText.setText(remarkMap.get("name"));
										emailText.setText(remarkMap
												.get("email"));
									}
									if (contactMap != null) {
										phoneText.setText(contactMap
												.get("phone"));
										qqText.setText(contactMap.get("qq"));
									}
								}
							};

							UMFeedbackService.setFeedBackListener(listener);

						*/
							UMFeedbackService.enableNewReplyNotification(
									mContext, NotificationType.AlertDialog);
							// 如果您程序界面是iOS风格，我们还提供了左上角的“返回”按钮，用于退出友盟反馈模块。启动友盟反馈模块前，您需要增加如下语句来设置“返回”按钮可见：
							UMFeedbackService.setGoBackButtonVisible();

							UMFeedbackService.openUmengFeedbackSDK(mContext);
						}
					});
			return root;
		}
	}
}