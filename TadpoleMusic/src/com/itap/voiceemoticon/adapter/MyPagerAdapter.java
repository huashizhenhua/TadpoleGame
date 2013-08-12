package com.itap.voiceemoticon.adapter;

import java.util.ArrayList;
import java.util.List;

import org.tadpole.view.PagerAdapter;
import org.tadpole.view.ViewPager;

import android.os.Parcelable;
import android.view.View;

/**
 * ViewPager适配器
 */
public class MyPagerAdapter extends PagerAdapter {
    public List<View> mListViews;


    public MyPagerAdapter(List<View> mListViews) {
        this.mListViews = mListViews;
    }

    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        
        final List<View> listViews = mListViews;
        
        if (null == listViews) {
            return;
        }

        View viewToDestory = listViews.get(arg1);
        if (null == viewToDestory) {
            return;
        }
        
        if (viewToDestory.getParent() != null) {
            ((ViewPager) arg0).removeView(mListViews.get(arg1));
        }
    }

    @Override
    public void finishUpdate(View arg0) {
    }

    @Override
    public int getCount() {
        return mListViews.size();
    }

    @Override
    public Object instantiateItem(View arg0, int postion) {
        View view = mListViews.get(postion);
        ((ViewPager) arg0).addView(view, 0);
        return view;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == (arg1);
    }

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void startUpdate(View arg0) {
    }

    @Override
    public float getPageWidth(int position) {
    	if(position == 0){
    		return 1f;
    	}else if(position == 1){
    		return 1f;
    	}else{
    		return 1f;
    	}
    }
}