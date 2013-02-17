package com.umeng.example.xp;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.example.R;
import com.umeng.ui.BaseSinglePaneActivity;
import com.umeng.xp.Promoter;
import com.umeng.xp.common.ExchangeConstants;
import com.umeng.xp.controller.ExchangeDataService;
import com.umeng.xp.controller.XpListenersCenter.ExchangeDataRequestListener;

/**
 * 将广告数据封装到自定义的View上。
 * 1.必须指定service展现类型（service.layoutType = ExchangeConstants.type_large_image;）
 * 2.广告被展示时必须调用接口将展示report 发送给服务器(service.reportImpression(promoter);)
 * 3.广告点击必须调用接口处理 （service.clickOnPromoter(promoter);）
 * @author Jhen
 *
 */
public class PromoterDataExample extends BaseSinglePaneActivity {
	@Override
	protected Fragment onCreatePane() {
		return new ExampleFragment();
	}
	
	public static class ExampleFragment extends Fragment {
		Context mContext;
		
		@Override
	    public void onAttach(Activity activity) {
	        super.onAttach(activity);
	        mContext = activity;
	    }
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			final View root = inflater.inflate(
					R.layout.umeng_example_xp_custom_promoter_wall, container,
					false);
			
			final ExchangeDataService service = new ExchangeDataService("40459");
			//添加feature 类型 （必须）
			service.layoutType = ExchangeConstants.type_large_image;
			
			//异步请求数据
			service.requestDataAsyn(mContext, new ExchangeDataRequestListener() {
				
				@Override
				public void dataReceived(int status, List<Promoter> data) {
					if(status == 1 && data != null){//成功获取数据
						//将数据封装到自定义的View上。
						packagePromoter(data,root,service);
					}
				}
			});
	
			return root;
		}
		protected void packagePromoter(List<Promoter> data, View root, final ExchangeDataService service) {
			for(int i=0;i<data.size() && i<3;i++){
				final Promoter promoter = data.get(i);
				
				//××××××× 选择合适的View START××××××× 
				ViewGroup parent = null;
				switch (i) {
				case 0:
					parent = (ViewGroup) root.findViewById(R.id.promoter_main);
					break;
				case 1:
					parent = (ViewGroup) root.findViewById(R.id.promoter_left);
					break;
				case 2:
					parent = (ViewGroup) root.findViewById(R.id.promoter_right);
					break;
				}
				//××××××× 选择合适的View END××××××× 	
			
				final ImageView imv = (ImageView)parent.findViewById(R.id.imagev);
				TextView adTv = (TextView) parent.findViewById(R.id.adword);
				TextView titleTv = (TextView) parent.findViewById(R.id.title);
				
				//设置标题
				if(titleTv != null)
					titleTv.setText(promoter.title);

				//加载图片
				if(!TextUtils.isEmpty(promoter.img))
				new ImageLoadTask(promoter.img) {
					@Override
					public void onRecived(Drawable result) {
						if(result != null)
							imv.setImageDrawable(result);
					}
				}.execute();
				
				//设置广告语
				adTv.setText(promoter.ad_words);
				
				//发送展示report（必须）
				service.reportImpression(promoter);
				
				//添加广告点击事件
				root.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						service.clickOnPromoter(promoter);
					}
				});
				
			}
		}
	}
	
}