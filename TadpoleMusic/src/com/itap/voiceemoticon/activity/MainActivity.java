
package com.itap.voiceemoticon.activity;

import java.util.ArrayList;

import net.youmi.android.AdManager;
import net.youmi.android.diy.DiyManager;

import org.tadpole.view.ViewPager;
import org.tadpoleframework.app.AlertDialog;
import org.tadpoleframework.common.APNUtil;
import org.tadpoleframework.widget.SwitchButton;
import org.tadpoleframework.widget.adapter.AdapterCallback;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.itap.voiceemoticon.R;
import com.itap.voiceemoticon.VEApplication;
import com.itap.voiceemoticon.activity.fragment.AppRecommendFragment;
import com.itap.voiceemoticon.activity.fragment.BaseFragment;
import com.itap.voiceemoticon.activity.fragment.HotVoiceFragment;
import com.itap.voiceemoticon.activity.fragment.MyCollectFragment;
import com.itap.voiceemoticon.activity.fragment.SearchFragment;
import com.itap.voiceemoticon.adapter.MyPagerAdapter;
import com.itap.voiceemoticon.adapter.VoiceAdapter;
import com.itap.voiceemoticon.api.Voice;
import com.itap.voiceemoticon.media.MusicData;
import com.itap.voiceemoticon.media.MusicPlayer;
import com.itap.voiceemoticon.third.UmengEvent;
import com.itap.voiceemoticon.util.AndroidUtil;
import com.itap.voiceemoticon.util.MusicUtil;
import com.itap.voiceemoticon.widget.MarqueeTextView;
import com.itap.voiceemoticon.widget.WeixinAlert;
import com.itap.voiceemoticon.widget.WeixinAlert.OnAlertSelectId;
import com.itap.voiceemoticon.wxapi.WXEntryActivity;
import com.sina.weibo.sdk.api.BaseResponse;
import com.sina.weibo.sdk.api.IWeiboHandler;
import com.umeng.analytics.MobclickAgent;

public class MainActivity extends SherlockFragmentActivity implements ActionBar.TabListener,
        ViewPager.OnPageChangeListener, AdapterCallback<Voice>, IWeiboHandler.Response,
        OnAlertSelectId {

    /**
     * MusicInfo update span
     */
    private static final int UPDATE_TIME_TEXT_LOOP_SPAN = 500;

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current tab position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    public MyCollectFragment myCollectVoiceFragment;

    private Tab mTabHostVoice;

    private Tab mTabMyCollection;

    private Tab mTabSearch;

    private ImageView mBtnPlay;

    private TextView mTextViewTime;

    private MarqueeTextView mTextViewMusicTitle;

    private SeekBar mSeekBarTime;

    private ProgressBar mProgressBarPrepare;

    private View mViewFooter;

    private ViewPager mViewPager;

    private Handler mHandler = new Handler();

    private BroadcastReceiver mMusicPlayerReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.d(VEApplication.TAG, " onReceive intent action " + intent.getAction());
            if (intent.getAction().equals(MusicPlayer.BROCAST_NAME)) {
                final Bundle data = intent.getExtras();
                final int state = data.getInt(MusicPlayer.KEY_STATE);

                int brocastType = data.getInt(MusicPlayer.KEY_BROCAST_TYPE);

                if (brocastType == MusicPlayer.BROCAST_TYPE_BUFFER_UPDATE) {
                    int percent = data.getInt(MusicPlayer.KEY_PERCENT);
                    mSeekBarTime.setSecondaryProgress(percent);
                    return;
                }

                final MusicData musicData = data.getParcelable(MusicPlayer.KEY_STATE_DATA);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        handleMusicPlayerIntent(state, musicData);
                    }
                });
            }
        }
    };

    @Override
    public void onResponse(BaseResponse arg0) {
        System.out.println("onResponse arg0 = " + arg0);
    }

    /**
     * flag for preventing onTabSelected on onPageSelected cycle invoke . To
     * avoid stackoverflow true meam must be prevent
     */
    private boolean mFlagPreventCycleInvoke = true;

    private Tab mTabAppRecommend;

    private void handleMusicPlayerIntent(int state, MusicData musicData) {
        Log.d(VEApplication.TAG, "musicData = " + musicData);
        switch (state) {
            case MusicPlayer.STATE_PLAY_START:
                Log.d(VEApplication.TAG, " STATE_PLAY_START");
                MainActivity.this.onMusicPlayStart(musicData);
                break;
            case MusicPlayer.STATE_PLAY_PREPARING:
                Log.d(VEApplication.TAG, " STATE_PLAY_PREPARING");
                MainActivity.this.onMusicPreparing();
                break;
            case MusicPlayer.STATE_PLAY_COMPLETE:
                Log.d(VEApplication.TAG, " STATE_PLAY_COMPLETE");
                MainActivity.this.onMusicPlayStop();
                onMusicTimeAndProgressUpdate(MusicUtil.TIME_TEXT_START, 0);
                break;
            case MusicPlayer.STATE_INVALID:
                Log.d(VEApplication.TAG, "STATE_INVALID");
                MainActivity.this.onMusicPlayStop();
                break;
            case MusicPlayer.STATE_PLAY_STOP:
                Log.d(VEApplication.TAG, "STATE_PLAY_STOP");
                MainActivity.this.onMusicPlayStop();
                break;
            default:
                Log.d(VEApplication.TAG, " state = " + state);
                break;
        }

    }

    private void performUpdateMusicProgressLoop() {
        MusicPlayer musicPlayer = VEApplication.getMusicPlayer(getApplicationContext());
        int curPostion = musicPlayer.getCurrentPostion();
        int duration = musicPlayer.getDuration();

        String timeText = MusicUtil.getToTimeText(curPostion, duration);
        int progress = MusicUtil.getProgress(curPostion, duration);

        onMusicTimeAndProgressUpdate(timeText, progress);
        if (musicPlayer.isPlaying()) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    performUpdateMusicProgressLoop();
                }
            }, UPDATE_TIME_TEXT_LOOP_SPAN);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        System.out.println("----->onNewIntent");

        VEApplication.sWeiboApi.responseListener(getIntent(), this);
    }

    private void onMusicPreparing() {
        mProgressBarPrepare.setVisibility(View.VISIBLE);
        mBtnPlay.setBackgroundResource(android.R.drawable.ic_media_pause);
    }

    private void onMusicPlayStart(MusicData musicData) {
        mTextViewMusicTitle.setText(musicData.musicName);
        mTextViewMusicTitle.startFor0();
        mProgressBarPrepare.setVisibility(View.GONE);

        performUpdateMusicProgressLoop();
    }

    private void onMusicTimeAndProgressUpdate(String timeText, int progress) {
        mTextViewTime.setText(timeText);
        mSeekBarTime.setProgress(progress);
    }

    private void onMusicPlayStop() {
        Log.d(VEApplication.TAG, "onMusicPlayComplete");
        mTextViewMusicTitle.clearAnimation();
        mProgressBarPrepare.setVisibility(View.GONE);
        mBtnPlay.setBackgroundResource(android.R.drawable.ic_media_play);
        mTextViewMusicTitle.stopScroll();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(VEApplication.TAG, "---->MainActivity onCreate call");
        super.onCreate(savedInstanceState);
        MobclickAgent.onError(this); // umeng error handle

        VEApplication.sWeiboApi.responseListener(getIntent(), this);
        setContentView(R.layout.activity_main);
        WXEntryActivity.isRunning = true;

        // Set up the action bar to show tabs.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayUseLogoEnabled(false);

        // For each of the sections in the app, add a tab to the action bar.
        mTabHostVoice = actionBar.newTab().setText(R.string.title_section_hot_voice)
                .setTabListener(this);
        mTabMyCollection = actionBar.newTab().setText(R.string.title_section_my_collection)
                .setTabListener(this);
        mTabSearch = actionBar.newTab().setText(R.string.title_section_search).setTabListener(this);
        mTabAppRecommend = actionBar.newTab().setText(R.string.title_section_app_recommend).setTabListener(this);;

        actionBar.addTab(mTabHostVoice);
        actionBar.addTab(mTabMyCollection);
        actionBar.addTab(mTabSearch);
       
     
        if(APNUtil.getMProxyType(this) == APNUtil.PROXYTYPE_WIFI) {
            actionBar.addTab(mTabAppRecommend);
        }
        
        actionBar.selectTab(mTabHostVoice);

        RelativeLayout container = (RelativeLayout)this.findViewById(R.id.container);

        mViewPager = new ViewPager(this);
        mViewPager.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT));

        container.addView(mViewPager);
        ArrayList<BaseFragment> viewList = new ArrayList<BaseFragment>();

        LayoutInflater inflater = LayoutInflater.from(this);

        HotVoiceFragment hotVoiceFragment = new HotVoiceFragment(this);
        viewList.add(hotVoiceFragment);

        myCollectVoiceFragment = new MyCollectFragment(this);
        viewList.add(myCollectVoiceFragment);

        SearchFragment searchFragment = new SearchFragment(this);
        viewList.add(searchFragment);
        

        if(APNUtil.getMProxyType(this) == APNUtil.PROXYTYPE_WIFI) {
            // 初始化应用的发布ID和密钥，以及设置测试模式
            AdManager.getInstance(this).init("f4c12ac956d1bdb6", "ebaec11e527854aa", false);
            DiyManager.initAdObjects(this);
            AppRecommendFragment appRecommendFragment = new AppRecommendFragment(this);
            viewList.add(appRecommendFragment);
        }

        mViewPager.setAdapter(new MyPagerAdapter(viewList));
        mViewPager.setOnPageChangeListener(this);

        initMusicPlayBar();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicPlayer.BROCAST_NAME);
        this.registerReceiver(mMusicPlayerReceiver, intentFilter);

        if (false == APNUtil.isNetworkAvailable(this)) {
            AlertDialog dialog = new AlertDialog(this);
            dialog.setTitle("网络不可用");
            dialog.setMsg("请打开网络并“下拉刷新”");
            dialog.show();
        }
    }

    private void initMusicPlayBar() {
        mBtnPlay = (ImageView)this.findViewById(R.id.btn_play);
        mTextViewTime = (TextView)this.findViewById(R.id.text_view_time);
        mSeekBarTime = (SeekBar)this.findViewById(R.id.seek_bar_time);
        mProgressBarPrepare = (ProgressBar)this.findViewById(R.id.progress_bar_preparing);
        mTextViewMusicTitle = (MarqueeTextView)this.findViewById(R.id.text_view_music_title_slide);
        mViewFooter = (View)this.findViewById(R.id.footer);
        mViewFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MusicPlayer musicPlayer = VEApplication
                        .getMusicPlayer(getApplicationContext());
                if (musicPlayer.isPlaying()) {
                    musicPlayer.stopMusic();
                } else {
                    musicPlayer.resume();
                }
            }
        });
        mSeekBarTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                final MusicPlayer musicPlayer = VEApplication
                        .getMusicPlayer(getApplicationContext());
                musicPlayer.seek(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }
        });
    }

    // ----------------------------------------------------------------
    // ViewPager
    // ----------------------------------------------------------------

    @Override
    public void onPageSelected(int position) {
        AndroidUtil.hideInputMethod(getWindow().getDecorView());
        if (true == mFlagPreventCycleInvoke) {
            mFlagPreventCycleInvoke = false;
            return;
        }
        final ActionBar actionBar = getSupportActionBar();
        mFlagPreventCycleInvoke = true;
        actionBar.selectTab(actionBar.getTabAt(position));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    // ----------------------------------------------------------------
    // State
    // ----------------------------------------------------------------

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current tab position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getSupportActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current tab position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getSupportActionBar()
                .getSelectedNavigationIndex());
    }

    // ----------------------------------------------------------------
    // Tab
    // ----------------------------------------------------------------

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        if (true == mFlagPreventCycleInvoke) {
            mFlagPreventCycleInvoke = false;
            return;
        }
        mFlagPreventCycleInvoke = true;

        if (null == mViewPager && null == tab)
            return;

        for (int i = 0, len = getSupportActionBar().getTabCount(); i < len; i++) {
            if (tab.equals(getSupportActionBar().getTabAt(i))) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    // ----------------------------------------------------------------
    // Action Menu Items
    // ----------------------------------------------------------------

    private static final int MENU_ITEM_ID_ABOUT = 1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItemAbout = menu.add(0, MENU_ITEM_ID_ABOUT, 0, "About");
        menuItemAbout.setIcon(android.R.drawable.ic_menu_info_details);
        menuItemAbout.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();
        switch (itemId) {
            case MENU_ITEM_ID_ABOUT:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        VEApplication.getMusicPlayer(this).destory();
        this.unregisterReceiver(mMusicPlayerReceiver);
        super.onDestroy();
        Log.d(VEApplication.TAG, "---->onDestroy call");
    }

    @Override
    public void onCommand(View view, final Voice obj, int command) {
        final MainActivity me = this;
        switch (command) {
            case VoiceAdapter.CMD_SHARE:
                BaseDialog dialog = WeixinAlert.buildAlertDialog(view.getContext(), "发送【" + obj.title + "】", "", this, null);
                dialog.setTag(obj);
                dialog.show();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onClick(Dialog dialog, int whichButton) {
        if(false == dialog instanceof BaseDialog) {
            return;
        }
        
        final BaseDialog dlg = (BaseDialog)dialog;
        Object tag = dlg.getTag();
        if(false == tag instanceof Voice) {
            return;
        }
        
        final Voice obj = (Voice) tag;
        boolean isHideTitle = false;
        SwitchButton sb = (SwitchButton)dialog.findViewById(R.id.switchbtn);
        isHideTitle = sb.isTurnOn();

        switch (whichButton) {
            case R.id.webchat:
                MobclickAgent.onEvent(this, UmengEvent.VOICE_SHARE_TO_WEIXIN);
                obj.sendToWeixin(this, isHideTitle);
                break;
            case R.id.qq:
                MobclickAgent.onEvent(this, UmengEvent.VOICE_SHARE_TO_QQ);
                obj.sendToQQ(this, isHideTitle);
                break;
            case R.id.friends:
                MobclickAgent.onEvent(this, UmengEvent.VOICE_SHARE_TO_FRIENDS);
                obj.sendToFriends(this, isHideTitle);
                break;
            default:
                break;
        }
    }
    
    @Override
    public void finish() {
        overridePendingTransition(0, 0);
        super.finish();
    }
    
}
