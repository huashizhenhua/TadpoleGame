
package com.itap.voiceemoticon.adapter;

import com.itap.voiceemoticon.activity.fragment.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import org.tadpole.view.PagerAdapter;
import org.tadpole.view.ViewPager;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;

/**
 * ViewPager适配器
 */
public class MyPagerAdapter extends PagerAdapter {
    public List<BaseFragment> mListViews;

    public MyPagerAdapter(List<BaseFragment> mListViews) {
        this.mListViews = mListViews;
    }

    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {

        final List<BaseFragment> listViews = mListViews;

        if (null == listViews) {
            return;
        }

        BaseFragment fragment = listViews.get(arg1);
        View view = fragment.getContent();
        if (null == view) {
            return;
        }

        if (view.getParent() != null) {
            ((ViewPager)arg0).removeView(view);
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
    public Object instantiateItem(View viewPager, int postion) {
        BaseFragment fragment = mListViews.get(postion);
        final View view = fragment.getContent();
        if (null == view) {
            fragment.createContent(LayoutInflater.from(viewPager.getContext()));
            if (null == view) {
                return null;
            }
        }
        ((ViewPager)viewPager).addView(view, 0);
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
        if (position == 0) {
            return 1f;
        } else if (position == 1) {
            return 1f;
        } else {
            return 1f;
        }
    }
}
