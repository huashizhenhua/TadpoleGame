package com.tadpolemusic.activity;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.tadpolemusic.R;
import com.tadpolemusic.TMLog;
import com.tadpolemusic.VEApplication;
import com.tadpolemusic.activity.dialog.MenuDialog;
import com.tadpolemusic.activity.fragment.CenterFragment;
import com.tadpolemusic.activity.fragment.LeftMenuConfig;
import com.tadpolemusic.activity.fragment.menu.IMenuControl;
import com.tadpolemusic.activity.fragment.menu.LeftMenuFragment;
import com.tadpolemusic.activity.fragment.menu.RightMenuFragment;
import com.tadpolemusic.adapter.MyMusicItem;
import com.tadpolemusic.media.MusicData;
import com.tadpolemusic.media.MusicPlayState;
import com.tadpolemusic.media.MusicPlayer;
import com.tadpolemusic.media.service.MusicPlayerProxy;

public class LeftAndRightActivity extends SherlockFragmentActivity implements IMenuControl {


    static final String TAG = "LeftAndRightActivity";

    private static class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        private static final int FIRST_PAGE_MARGIN_RIGHT_DP = 150; // 50dp

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

    private Handler mHandler = new Handler();

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
        TMLog.step(TAG, "onCreate");
        // content view
        setContentView(R.layout.activity_left_right);

        // init actionBar
        mActionBar = getSupportActionBar();
        mActionBar.hide();



        // view pager
        mViewPager = (ViewPager) this.findViewById(R.id.container);


        ArrayList<MyMusicItem> musicItemList = LeftMenuConfig.getMyMusicItem();

        // create fragments
        mLeft = new LeftMenuFragment();
        mLeft.setLocalMusicItems(musicItemList);
        mCenter = new CenterFragment();
        mRight = new RightMenuFragment();

        // add fragment to view pager
        mFragments.add(mLeft);
        mFragments.add(mCenter);
        mFragments.add(mRight);
        mViewPager.setAdapter(new MyFragmentPagerAdapter(this, getSupportFragmentManager(), mFragments));

        // set default content;
        //        setCenterContent(LeftMenuConfig.localMusicItem);
        mLeft.setDefaultSelectItem(musicItemList.get(0));

//        savedInstanceState.pu
        
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        TMLog.step(TAG, "onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        TMLog.step(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    public void scrollToCenter() {
        mViewPager.setCurrentItem(1);
    }


    @Override
    public void scrollToRight() {
        mViewPager.setCurrentItem(2);
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



    // -------------------------------------------
    // Footer -- Music Player
    // -------------------------------------------
    private MusicData mCurMusicData;
    private int mCurPlayListIndex;
    private Timer mPollMusicTimer;

    private Vector<ActivityMusicListenter> mListenerList = new Vector<LeftAndRightActivity.ActivityMusicListenter>();

    public void registerMusicListener(ActivityMusicListenter listener) {
        mListenerList.add(listener);
    }

    public void unRegisterMusicListenr(ActivityMusicListenter listener) {
        mListenerList.remove(listener);
    }

    @Override
    public void onStart() {
        super.onStart();
        registerReceiver(mMusicPlayerReceiver, VEApplication.getMusicPlayer(this).getBrocastIntentFilter());
    }

    @Override
    public void onStop() {
        super.onStop();
        this.unregisterReceiver(mMusicPlayerReceiver);
    }

    /**
     * @param period
     *            millseconds
     */
    public void startPoll(int period) {
        if (mPollMusicTimer != null) {
            mPollMusicTimer.cancel();
            mPollMusicTimer = null;
        }
        final LeftAndRightActivity me = this;
        mPollMusicTimer = new Timer();

        mPollMusicTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                //                Log.d("mPollMusicTimer", "=====>mPollMusicTimer");
                final MusicPlayerProxy player = VEApplication.getMusicPlayer(me);
                final MusicData md = mCurMusicData;

                //                Log.w("", "mCurMusicData = " + mCurMusicData);
                if (md == null) {
                    return;
                }

                int musicPos = player.getCurPosition();
                final String timerText = md.getTimerText(musicPos);

                final String progressTimeText = md.getProgressTimeText(musicPos);
                final String durationText = md.getDurtaionText();

                final int progress = md.getProgress(musicPos);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        me.onMusicTimeAndProgressUpdate(timerText, progress);
                        me.onMusicTimeAndProgressUpdate(progressTimeText, durationText, progress);
                    }
                });
            }
        }, 0, period);
    }



    public void stopPoll() {
        if (mPollMusicTimer != null) {
            mPollMusicTimer.cancel();
            mPollMusicTimer = null;
        }
    }


    private BroadcastReceiver mMusicPlayerReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final LeftAndRightActivity me = LeftAndRightActivity.this;
            if (intent.getAction().equals(MusicPlayer.BROCAST_NAME)) {
                final Bundle data = intent.getExtras();
                final int state = data.getInt(MusicPlayState.PLAY_STATE_NAME, MusicPlayState.MPS_INVALID);
                final int playListIndex = data.getInt(MusicPlayState.PLAY_MUSIC_INDEX);
                Log.d(VEApplication.TAG, " onReceive intent action = " + intent.getAction() + ", state = " + state + ", curPlayIndex = " + mCurPlayListIndex);



                final int playBufferPercent = data.getInt(MusicPlayState.PLAY_BUFFER_PERCENT);
                //                int brocastType = data.getInt(MusicPlayer.KEY_BROCAST_TYPE);
                //
                //                if (brocastType == MusicPlayer.BROCAST_TYPE_BUFFER_UPDATE) {
                //                    int percent = data.getInt(MusicPlayer.KEY_PERCENT);
                //                    mSeekBarTime.setSecondaryProgress(percent);
                //                    return;
                //                }

                final MusicData musicData = data.getParcelable(MusicData.KEY_MUSIC_DATA);
                // Log.d(VEApplication.TAG, "musicData = " + musicData);
                me.runOnUiThread(new Runnable() {
                    public void run() {

                        me.onMusicPlayBufferUpdate(playBufferPercent);

                        switch (state) {
                        case MusicPlayState.MPS_PREPARE:
                            Log.d(VEApplication.TAG, " STATE_PLAY_START");
                            me.onMusicPreparing();
                            break;
                        case MusicPlayState.MPS_PLAYING:
                            Log.d(VEApplication.TAG, " STATE_PLAY_PLAYING");
                            me.onMusicPlayStart(musicData, playListIndex);
                            break;
                        case MusicPlayState.MPS_NOFILE:
                            Log.d(VEApplication.TAG, " STATE_PLAY_PLAYING");
                            me.onPlayListNoFile(playListIndex);
                            break;
                        case MusicPlayState.MPS_PAUSE:
                            Log.d(VEApplication.TAG, " STATE_PLAY_COMPLETE");
                            me.onMusicPlayComplete();
                            break;
                        case MusicPlayState.MPS_INVALID:
                            Log.d(VEApplication.TAG, "STATE_INVALID");
                            me.onMusicPlayComplete();
                            break;
                        default:
                            Log.d(VEApplication.TAG, " state = " + state);
                            break;
                        }
                    }
                });
            }
        }
    };


    protected void onMusicPlayBufferUpdate(int playBufferPercent) {
        for (int i = 0, N = mListenerList.size(); i < N; i++) {
            mListenerList.get(i).onMusicPlayBufferUpdate(playBufferPercent);
        }
    }


    private void onMusicPlayStart(MusicData musicData, int playListIndex) {
        for (int i = 0, N = mListenerList.size(); i < N; i++) {
            mListenerList.get(i).onMusicPlayStart(musicData, playListIndex);
        }
        mCurMusicData = musicData;
        this.startPoll(600);

    }

    private void onMusicPreparing() {
        for (int i = 0, N = mListenerList.size(); i < N; i++) {
            mListenerList.get(i).onMusicPreparing();
        }
    }

    private void onMusicTimeAndProgressUpdate(String timerText, int progress) {
        for (int i = 0, N = mListenerList.size(); i < N; i++) {
            mListenerList.get(i).onMusicTimeAndProgressUpdate(timerText, progress);
        }
    }

    protected void onMusicTimeAndProgressUpdate(String progressTimeText, String durationText, int progress) {
        for (int i = 0, N = mListenerList.size(); i < N; i++) {
            mListenerList.get(i).onMusicTimeAndProgressUpdate(progressTimeText, durationText, progress);
        }
    }

    private void onMusicPlayComplete() {
        mCurMusicData = null;
        this.stopPoll();

        for (int i = 0, N = mListenerList.size(); i < N; i++) {
            mListenerList.get(i).onMusicPlayComplete();
        }
        Log.d(VEApplication.TAG, "onMusicPlayComplete");
    }

    private void onPlayListNoFile(int playListIndex) {
        mCurMusicData = null;
        this.stopPoll();
        for (int i = 0, N = mListenerList.size(); i < N; i++) {
            mListenerList.get(i).onPlayListNoFile(playListIndex);
        }
    }



    public static interface ActivityMusicListenter {
        void dispatchPlayingInfo(int playListIndex);

        void onMusicPlayBufferUpdate(int playBufferPercent);

        void onMusicTimeAndProgressUpdate(String progressTimeText, String durationText, int progress);

        void onMusicPlayStart(MusicData musicData, int playListIndex);

        void onMusicPreparing();

        void onMusicTimeAndProgressUpdate(String timerText, int progress);

        void onMusicPlayComplete();

        void onPlayListNoFile(int playListIndex);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        TMLog.step(TAG, "onDestroy");
    }

}
