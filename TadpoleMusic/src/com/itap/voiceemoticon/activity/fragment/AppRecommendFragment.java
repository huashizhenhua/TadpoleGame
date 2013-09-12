
package com.itap.voiceemoticon.activity.fragment;

import java.util.List;

import net.youmi.android.AdManager;
import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;
import net.youmi.android.diy.AdObject;
import net.youmi.android.diy.DiyManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itap.voiceemoticon.R;
import com.itap.voiceemoticon.activity.MainActivity;

public class AppRecommendFragment extends BaseFragment {

    private MainActivity mActivity;

    public AppRecommendFragment(MainActivity activity) {
        mActivity = activity;
    }

    public View onCreateView(LayoutInflater inflater) {
        System.out.println("AppRecommendFragment onCreateView");
        ViewGroup viewGroup = (ViewGroup)inflater.inflate(R.layout.activity_recommend, null);

        // 实例化广告条g
        AdView adView = new AdView(mActivity, AdSize.SIZE_320x50);
        // 获取要嵌入广告条的布局
        RelativeLayout adLayout = (RelativeLayout)viewGroup.findViewById(R.id.AdLayout);
        // 将广告条加入到布局中
        adLayout.addView(adView);

        ViewGroup fatherLayout = (ViewGroup)viewGroup.findViewById(R.id.ad);
        ListView listView = (ListView)viewGroup.findViewById(R.id.list);
        /**
         * 所需要资源 drawable/btn_back.png drawable/btn_download_details.xml
         * drawable/list_background.xml drawable/btn_download_list.xml
         * drawable/btn_download_list_click.png
         * drawable/btn_download_list_normal.png values/colors.xml
         */
        List list = DiyManager.getAdList(mActivity);
        if(null == list) {
            return viewGroup;
        }

        RecommendAdapter recommendAdapter = new RecommendAdapter(mActivity, list);
        listView.setAdapter(recommendAdapter);

        return viewGroup;
    }

    protected class RecommendAdapter extends BaseAdapter {

        Context context;

        List<AdObject> list;

        LayoutInflater mInflater;

        public RecommendAdapter(Context context, List<AdObject> arrayList) {
            this.context = context;
            this.list = arrayList;
            this.mInflater = mActivity.getLayoutInflater();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final AdObject adObject = list.get(position);
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.iconView = (ImageView)convertView.findViewById(R.id.icon);
                viewHolder.appNameView = (TextView)convertView.findViewById(R.id.appName);
                viewHolder.appDescriptionView = (TextView)convertView.findViewById(R.id.apptxt);
                viewHolder.downloadBtn = (ImageView)convertView.findViewById(R.id.downloadBtn);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder)convertView.getTag();
            }

            viewHolder.iconView.setImageBitmap(adObject.getIcon());
            viewHolder.appNameView.setText(adObject.getAppName());
            viewHolder.appDescriptionView.setText(adObject.getAdText());
            viewHolder.downloadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DiyManager.downloadAd(context, adObject.getAdId());
                }
            });

            return convertView;
        }

        class ViewHolder {
            ImageView iconView;

            TextView appNameView;

            TextView appDescriptionView;

            ImageView downloadBtn;
        }

    }

}
