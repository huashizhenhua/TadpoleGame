package com.tadpolemusic.activity;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.itap.voiceemoticon.widget.MarqueeTextView;
import com.slidingmenu.lib.SlidingMenu;
import com.tadpolemusic.R;
import com.tadpolemusic.VEApplication;
import com.tadpolemusic.activity.fragment.AbsCenterContent;
import com.tadpolemusic.activity.fragment.CenterFragment;
import com.tadpolemusic.activity.fragment.LeftMenuConfig;
import com.tadpolemusic.activity.fragment.center.LocalMusicFragment;
import com.tadpolemusic.activity.fragment.menu.ILeftMenuControl;
import com.tadpolemusic.activity.fragment.menu.LeftMenuFragment;
import com.tadpolemusic.activity.fragment.menu.RightMenuFragment;
import com.tadpolemusic.adapter.MyMusicItem;
import com.tadpolemusic.media.MusicData;
import com.tadpolemusic.media.MusicPlayer;

public class LeftAndRightActivity extends SherlockFragmentActivity implements ILeftMenuControl {

    private static class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        private static final int FIRST_PAGE_MARGIN_RIGHT_DP = 50; // 50dp

        private ArrayList<Fragment> mMyFragments;
        private float mfirstPageScale;

        public MyFragmentPagerAdapter(Context ctx, FragmentManager fm, ArrayList<Fragment> mFragments) {
            super(fm);
            mMyFragments = mFragments;
            WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics metrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(metrics);
            float density = metrics.density;
            float screenWidth = metrics.widthPixels;
            float screenWidthDIP = screenWidth / density;
            mfirstPageScale = 1 - (FIRST_PAGE_MARGIN_RIGHT_DP / screenWidthDIP);
        }

        @Override
        public Fragment getItem(int index) {
            return mMyFragments.get(index);
        }

        @Override
        public int getCount() {
            return mMyFragments.size();
        }

        @Override
        public float getPageWidth(int position) {
            if (position == 0) {
                return mfirstPageScale;
            } else if (position == 1) {
                return 1f;
            } else {
                return 1f;
            }
        }
    }

    // ------------------------------------------
    // Main UI Structure
    // ------------------------------------------

    private ActionBar mActionBar;
    private SlidingMenu mSlidingMenu;
    private ViewPager mViewPager;
    private ArrayList<Fragment> mFragments = new ArrayList<Fragment>(3);


    private LeftMenuFragment mLeft;
    private CenterFragment mCenter;
    private LeftMenuFragment mRight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // init actionBar
        mActionBar = getSupportActionBar();
        mActionBar.hide();

        // content view
        setContentView(R.layout.activity_left_right);

        // view pager
        mViewPager = (ViewPager) this.findViewById(R.id.container);

        // create fragments
        mLeft = new LeftMenuFragment(LeftMenuConfig.myMusicItems);
        mCenter = new CenterFragment();
        mRight = new LeftMenuFragment(LeftMenuConfig.myMusicItems);

        // add fragment to view pager
        mFragments.add(mLeft);
        mFragments.add(mCenter);
        mFragments.add(mRight);
        mViewPager.setAdapter(new MyFragmentPagerAdapter(this, getSupportFragmentManager(), mFragments));

        // set default content;
        setCenterContent(LeftMenuConfig.localMusicItem);
    }

    public void scrollToCenter() {
        mViewPager.setCurrentItem(1);
    }

    @Override
    public void setCenterContent(MyMusicItem item) {
        mCenter.setContent(item);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            mSlidingMenu.toggle();
            return true;
        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }


    // -------------------------------------------

}
