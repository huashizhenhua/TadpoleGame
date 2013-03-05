package com.tadpolemusic.activity;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.tadpolemusic.R;
import com.tadpolemusic.activity.fragment.CenterFragment;
import com.tadpolemusic.activity.fragment.LeftMenuConfig;
import com.tadpolemusic.activity.fragment.menu.ILeftMenuControl;
import com.tadpolemusic.activity.fragment.menu.LeftMenuFragment;
import com.tadpolemusic.activity.fragment.menu.RightMenuFragment;
import com.tadpolemusic.activity.widget.MenuDialog;
import com.tadpolemusic.adapter.MyMusicItem;

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
        public void destroyItem(View container, int position, Object object) {
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
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
    private RightMenuFragment mRight;

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
        mRight = new RightMenuFragment();

        // add fragment to view pager
        mFragments.add(mLeft);
        mFragments.add(mCenter);
        mFragments.add(mRight);
        mViewPager.setAdapter(new MyFragmentPagerAdapter(this, getSupportFragmentManager(), mFragments));

        // set default content;
        //        setCenterContent(LeftMenuConfig.localMusicItem);
        mLeft.setDefaultSelectItem(LeftMenuConfig.localMusicItem);
    }

    public void scrollToCenter() {
        mViewPager.setCurrentItem(1);
    }

    @Override
    public void setCenterContent(MyMusicItem item) {
        mCenter.setContent(item);
    }

    @Override
    public void scrollToLeft() {
        mViewPager.setCurrentItem(0);
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


    //-------------------------------------------
    // FullScreen Dailog Menu
    //-------------------------------------------

    private MenuDialog mMenuDialog;

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (mMenuDialog == null) {
                mMenuDialog = new MenuDialog(this);
            }
            mMenuDialog.show();
        }


        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mMenuDialog != null) {
                mMenuDialog.dismiss();
                return true;
            }
        }

        return super.onKeyUp(keyCode, event);
    }
}
