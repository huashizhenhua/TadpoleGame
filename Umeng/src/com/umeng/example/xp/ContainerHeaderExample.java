
package com.umeng.example.xp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.umeng.example.R;
import com.umeng.ui.BaseSinglePaneActivity;
import com.umeng.xp.common.ExchangeConstants;
import com.umeng.xp.controller.ExchangeDataService;
import com.umeng.xp.controller.XpListenersCenter.BindMode;
import com.umeng.xp.controller.XpListenersCenter.InitializeListener;
import com.umeng.xp.controller.XpListenersCenter.LargeGalleryBindListener;
import com.umeng.xp.controller.XpListenersCenter.STATUS;
import com.umeng.xp.view.ExchangeViewManager;
import com.umeng.xp.view.LargeGalleryConfig;
//test
public class ContainerHeaderExample extends BaseSinglePaneActivity {
	
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
			ExchangeConstants.DEBUG_MODE = false;
			String containerSlot =  "";
			String gallerySlot = "40459";
			View root = inflater.inflate(
					R.layout.umeng_example_xp_container_full, container,
					false);
			
			ExchangeConstants.CONTAINER_AUTOEXPANDED=true;
			
			ViewGroup fatherLayout = (ViewGroup) root.findViewById(R.id.rootId);
			final ListView listView = (ListView) root.findViewById(R.id.list);
			
			ExchangeDataService containerService = new ExchangeDataService(containerSlot);
			
			//add largeGallery header......
			final RelativeLayout headerRoot = new RelativeLayout(mContext);
			float scale = mContext.getResources().getDisplayMetrics().density;
			int height = (int) (180 * scale + 0.5f);
			AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(LayoutParams.FILL_PARENT, height);
			headerRoot.setLayoutParams(layoutParams);
			final ExchangeDataService service = new ExchangeDataService(gallerySlot);
			containerService.initializeListener = new InitializeListener() {
				@Override
				public void onStartRequestData(int type) {
				}
				
				@Override
				public void onReceived(int count) {
					ExchangeViewManager viewMgr = new ExchangeViewManager(mContext,service);
					//添加图片绑定回调。
					viewMgr.setLargeGalleryConfig(new LargeGalleryConfig().setBindListener(new LargeGalleryBindListener() {
						@Override
						public void onEnd(STATUS status, ViewGroup view) {
							ImageView imv = (ImageView) view.findViewById(R.id.umeng_xp_large_gallery_item_imv);
							if(status == STATUS.FAIL)
								imv.setImageResource(R.drawable.umeng_xp_large_gallery_failed);
							view.findViewById(R.id.umeng_xp_large_gallery_item_progressbar).setVisibility(View.GONE);
							view.findViewById(R.id.umeng_xp_large_gallery_item_imv).setVisibility(View.VISIBLE);
						}

						@Override
						public void onStart(BindMode mode, ViewGroup view) {
							view.findViewById(R.id.umeng_xp_large_gallery_item_progressbar).setVisibility(View.VISIBLE);
							view.findViewById(R.id.umeng_xp_large_gallery_item_imv).setVisibility(View.GONE);
						}

					}));
					viewMgr.addView(headerRoot, ExchangeConstants.type_large_image);
					listView.addHeaderView(headerRoot);
				}
			};
			
			ExchangeViewManager exchangeViewManager = new ExchangeViewManager(mContext,containerService);
			exchangeViewManager.addView(fatherLayout, listView);

			return root;
		}
		
	}

}
