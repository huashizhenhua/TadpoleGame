package com.umeng.example.xp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.umeng.example.R;
import com.umeng.ui.BaseSinglePaneActivity;
import com.umeng.xp.common.ExchangeConstants;
import com.umeng.xp.controller.ExchangeDataService;
import com.umeng.xp.controller.XpListenersCenter.NTipsChangedListener;

public class XpHome extends BaseSinglePaneActivity {
	public static ExchangeDataService preloadDataService;
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected Fragment onCreatePane() {
		// ExchangeConstants.banner_alpha = 120;
		ExchangeConstants.full_screen = false;
		ExchangeConstants.ONLY_CHINESE = false;
		ExchangeConstants.handler_auto_expand = true;
		ExchangeConstants.DEBUG_MODE = true;
		ExchangeConstants.handler_left = true;
		ExchangeConstants.RICH_NOTIFICATION = false;
		

		return new XpHomeFragment();
	}

	/**
	 * Do not change this to anonymous class as it will crash when orientation
	 * changes.
	 * 
	 * @author lucas
	 * 
	 */
	public static class XpHomeFragment extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			final View root = inflater.inflate(R.layout.umeng_example_xp_home,
					container, false);
			
			//set container preload data
			preloadDataService = new ExchangeDataService("40251");
			preloadDataService.preloadData(getActivity(), new NTipsChangedListener() {
				@Override
				public void onChanged(int flag) {
					TextView view = (TextView) root.findViewById(R.id.umeng_example_xp_container_tips);
					if(flag == -1){
						view.setVisibility(View.INVISIBLE);
					}else if(flag > 1){
						view.setVisibility(View.VISIBLE);
						view.setBackgroundResource(R.drawable.umeng_example_xp_new_tip_bg);
						view.setText(""+flag);
					}else if(flag == 0){
						view.setVisibility(View.VISIBLE);
						view.setBackgroundResource(R.drawable.umeng_example_xp_new_tip);
					}
				};
			}, ExchangeConstants.type_container);
			
			root.findViewById(R.id.umeng_example_xp_home_btn_container)
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							startActivity(new Intent(getActivity(),
									ContainerExample.class));
						}
					});
			root.findViewById(R.id.umeng_example_xp_home_btn_banner)
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							startActivity(new Intent(getActivity(),
									BannerExample.class));
						}
					});
			root.findViewById(R.id.umeng_example_xp_home_btn_handler)
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							startActivity(new Intent(getActivity(),
									HandlerExample.class));
						}
					});
			root.findViewById(R.id.umeng_example_xp_home_btn_wap_ufp)
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO: implement this example.
							startActivity(new Intent(getActivity(),
									WapUfpExample.class));
						}
					});
			root.findViewById(R.id.umeng_example_xp_home_btn_banner_ufp)
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							startActivity(new Intent(getActivity(),
									BannerUfpExample.class));
						}
					});
			root.findViewById(R.id.umeng_example_xp_home_btn_handler_ufp)
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							startActivity(new Intent(getActivity(),
									HandlerUfpExample.class));
						}
					});
			root.findViewById(R.id.umeng_example_xp_home_btn_wap)
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO: implement this example.
							startActivity(new Intent(getActivity(),
									WapExample.class));
						}
					});
			root.findViewById(R.id.umeng_example_xp_home_btn_tab)
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							startActivity(new Intent(getActivity(),
									TabFragment.class));
						}
					});
			root.findViewById(R.id.umeng_example_xp_home_btn_textlink)
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							startActivity(new Intent(getActivity(),
									HyperlinkTextExample.class));
						}
					});
			root.findViewById(R.id.umeng_example_xp_home_btn_container_with_header)
			.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(new Intent(getActivity(),
							ContainerHeaderExample.class));
				}
			});
			root.findViewById(R.id.umeng_example_xp_home_btn_push_ad)
			.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(new Intent(getActivity(),
							PushExample.class));
				}
			});
			root.findViewById(R.id.umeng_example_xp_home_btn_handler_icons)
			.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO: implement Text Link example.
					startActivity(new Intent(getActivity(),
							FullIconExample.class));
				}
			});
			root.findViewById(R.id.umeng_example_xp_home_btn_custom)
			.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO: implement Text Link example.
					startActivity(new Intent(getActivity(),
							PromoterDataExample.class));
				}
			});
			return root;
		}
	}
}