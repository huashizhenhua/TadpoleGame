
package com.itap.voiceemoticon.activity;

import java.util.ArrayList;

//import net.youmi.android.AdManager;
//import net.youmi.android.diy.DiyManager;

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
import android.os.Message;
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
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.itap.voiceemoticon.Feature;
import com.itap.voiceemoticon.MsgDef;
import com.itap.voiceemoticon.R;
import com.itap.voiceemoticon.VEApplication;
import com.itap.voiceemoticon.activity.fragment.BaseFragment;
import com.itap.voiceemoticon.activity.fragment.HotVoiceFragment;
import com.itap.voiceemoticon.activity.fragment.MyCollectFragment;
import com.itap.voiceemoticon.activity.fragment.SearchFragment;
import com.itap.voiceemoticon.activity.fragment.UserVoiceFragment;
import com.itap.voiceemoticon.activity.fragment.UserVoiceMakeDialog;
import com.itap.voiceemoticon.adapter.MyPagerAdapter;
import com.itap.voiceemoticon.adapter.VoiceAdapter;
import com.itap.voiceemoticon.api.Voice;
import com.itap.voiceemoticon.common.ConstValues;
import com.itap.voiceemoticon.media.MusicData;
import com.itap.voiceemoticon.media.MusicPlayer;
import com.itap.voiceemoticon.third.UmengEvent;
import com.itap.voiceemoticon.third.WXEntryActivity;
import com.itap.voiceemoticon.util.AndroidUtil;
import com.itap.voiceemoticon.util.MusicUtil;
import com.itap.voiceemoticon.weibo.WeiboLoginAcountManager;
import com.itap.voiceemoticon.weibo.TPAccountManager;
import com.itap.voiceemoticon.widget.MarqueeTextView;
import com.itap.voiceemoticon.widget.WeixinAlert;
import com.itap.voiceemoticon.widget.WeixinAlert.OnAlertSelectId;
import com.sina.weibo.sdk.api.BaseResponse;
import com.sina.weibo.sdk.api.IWeiboHandler;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
//import com.sina.weibo.sdk.api.BaseResponse;
//import com.sina.weibo.sdk.api.IWeiboHandler;

public class MainActivity extends SherlockFragmentActivity implements ActionBar.TabListener,
        ViewPager.OnPageChangeListener, AdapterCallback<Voice>, IWeiboHandler.Response,
        OnAlertSelectId, INotify {

    public static void start(Context context, Message msg) {
        System.out.println("start msg = " + msg);
        Intent intent = new Intent();
        intent.setClass(context, MainActivity.class);
        intent.putExtra(ConstValues.INTENT_KEY_MESSAGE, msg);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(intent);
    }

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

    private Tab mTabUserVoice;

    private Tab mTabHostVoice;

    private Tab mTabMyCollection;

    private Tab mTabSearch;

    private Tab mTabAppRecommend;

    private ImageView mBtnPlay;

    private TextView mTextViewTime;

    private MarqueeTextView mTextViewMusicTitle;

    private SeekBar mSeekBarTime;

    private ProgressBar mProgressBarPrepare;

    private View mViewFooter;

    private ViewPager mViewPager;

    private Handler mHandler = new Handler();

    private ArrayList<BaseFragment> mFragmentList = new ArrayList<BaseFragment>();

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

    //
    // @Override
    // public void onResponse(BaseResponse arg0) {
    // System.out.println("onResponse arg0 = " + arg0);
    // }

    /**
     * flag for preventing onTabSelected on onPageSelected cycle invoke . To
     * avoid stackoverflow true meam must be prevent
     */
    private boolean mFlagPreventCycleInvoke = true;

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

        // VEApplication.sWeiboApi.responseListener(getIntent(), this);
        handleIntent(intent);
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

        //-----> update
        final Context mContext = this;
        UmengUpdateAgent.update(this);
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                switch (updateStatus) {
                    case 0: // has update
                        UmengUpdateAgent.showUpdateDialog(mContext, updateInfo);
                        break;
                    case 1: // has no update
                        break;
                    case 2: // none wifi
                        Toast.makeText(mContext, "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT).show();
                        break;
                    case 3: // time out
                        Toast.makeText(mContext, "超时", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        //<----- update

        MobclickAgent.onError(this); // umeng error handle
        
        // NotificationCenter
        NotificationCenter.getInstance().unregister(this, NotificationID.N_USERVOICE_MAKE);
        NotificationCenter.getInstance().register(this, NotificationID.N_USERVOICE_MAKE);

        // VEApplication.sWeiboApi.responseListener(getIntent(), this);
        setContentView(R.layout.activity_main);
        WXEntryActivity.isRunning = true;

        // Set up the action bar to show tabs.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayUseLogoEnabled(false);

        // For each of the sections in the app, add a tab to the action bar.
        mTabUserVoice = actionBar.newTab().setText(R.string.title_section_user_voice)
                .setTabListener(this);
        mTabHostVoice = actionBar.newTab().setText(R.string.title_section_hot_voice)
                .setTabListener(this);
        mTabMyCollection = actionBar.newTab().setText(R.string.title_section_my_collection)
                .setTabListener(this);
        mTabSearch = actionBar.newTab().setText(R.string.title_section_search).setTabListener(this);
        mTabAppRecommend = actionBar.newTab().setText(R.string.title_section_app_recommend)
                .setTabListener(this);
        ;

        actionBar.addTab(mTabUserVoice);

        actionBar.addTab(mTabHostVoice);
        actionBar.addTab(mTabMyCollection);
        actionBar.addTab(mTabSearch);
        if (APNUtil.getMProxyType(this) == APNUtil.PROXYTYPE_WIFI) {
            if (Feature.isYoumi()) {
                actionBar.addTab(mTabAppRecommend);
            }
        }

        RelativeLayout container = (RelativeLayout)this.findViewById(R.id.container);

        mViewPager = new ViewPager(this);
        mViewPager.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));

        container.addView(mViewPager);
        ArrayList<BaseFragment> viewList = new ArrayList<BaseFragment>();

        LayoutInflater inflater = LayoutInflater.from(this);

        UserVoiceFragment userVoiceFragment = new UserVoiceFragment(this);
        viewList.add(userVoiceFragment);

        HotVoiceFragment hotVoiceFragment = new HotVoiceFragment(this);
        viewList.add(hotVoiceFragment);

        myCollectVoiceFragment = new MyCollectFragment(this);
        viewList.add(myCollectVoiceFragment);

        SearchFragment searchFragment = new SearchFragment(this);
        viewList.add(searchFragment);

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

        actionBar.selectTab(mTabHostVoice);

        TextView textView = new TextView(this);
        actionBar.setCustomView(textView);

        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_CUSTOM);

        handleIntent(getIntent());
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

    // private int mCount = 8;

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        if (true == mFlagPreventCycleInvoke) {
            mFlagPreventCycleInvoke = false;
            return;
        }
        // if (( (mCount++) % 10) == 0) {
        // SpotManager.getInstance(this).showSpotAds(this);
        // }

        mFlagPreventCycleInvoke = true;

        if (null == mViewPager || null == tab)
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

    private static final int MENU_ITEM_ID_MAKE_VOICE = 2;

    private static final int MENU_ITEM_ID_LOGIN = 3;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItemMakeVoice = menu.add(0, MENU_ITEM_ID_MAKE_VOICE, 0, "录音");
        menuItemMakeVoice.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        // MenuItem menuLogin = menu.add(0, MENU_ITEM_ID_LOGIN, 0,
        // "登录");
        // menuLogin.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        MenuItem menuItemAbout = menu.add(0, MENU_ITEM_ID_ABOUT, 0, "更多");
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
            case MENU_ITEM_ID_MAKE_VOICE:
                MobclickAgent.onEvent(this, UmengEvent.VOICE_MAKE);
                Notification notification = NotificationCenter
                        .obtain(NotificationID.N_USERVOICE_MAKE);
                NotificationCenter.getInstance().notify(notification);
                break;
            case MENU_ITEM_ID_LOGIN:
                MobclickAgent.onEvent(this, UmengEvent.LOGIN_WEIBO);
                TPAccountManager.getInstance().login(this, Message.obtain());
                break;
            default:
                break;
        }
        item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    public void sendMessage() {

    }

    @Override
    protected void onDestroy() {
        VEApplication.getMusicPlayer(this).destory();
        this.unregisterReceiver(mMusicPlayerReceiver);
        NotificationCenter.getInstance().unregister(this, NotificationID.N_USERVOICE_MAKE);

        super.onDestroy();
        Log.d(VEApplication.TAG, "---->onDestroy call");

        if (null == mFragmentList) {
            return;
        }

        for (BaseFragment fragment : mFragmentList) {
            fragment.onDestory();
        }
    }

    @Override
    public void onCommand(View view, final Voice obj, int command, int position) {
        final MainActivity me = this;
        switch (command) {
            case VoiceAdapter.CMD_SHARE:
                BaseDialog dialog = WeixinAlert.buildAlertDialog(view.getContext(), "发送【"
                        + obj.title + "】", "", this, null);
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
        if (false == dialog instanceof BaseDialog) {
            return;
        }

        final BaseDialog dlg = (BaseDialog)dialog;
        Object tag = dlg.getTag();
        if (false == tag instanceof Voice) {
            return;
        }

        final Voice obj = (Voice)tag;
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
            case R.id.weibo:
                MobclickAgent.onEvent(this, UmengEvent.VOICE_SHARE_TO_WEIBO);
                obj.sendToWeibo(this, isHideTitle);
                break;
            default:
                break;
        }
    }

    @Override
    public void finish() {
        overridePendingTransition(0, 0);

        if (null != mDialog) {
            mDialog.dismiss();
        }

        super.finish();
    }

    public void superFinish() {
        super.finish();
    }

    @Override
    public void onResponse(BaseResponse arg0) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        System.out.println("onActivityResult = requestCode  = " + requestCode);

        TPAccountManager.getInstance().callback(this, requestCode, resultCode, intent);
    }

    private UserVoiceMakeDialog mDialog = null;

    @Override
    public void notify(Notification notification) {
        if (notification.id == NotificationID.N_USERVOICE_MAKE) {
            if (WeiboLoginAcountManager.getInstance().isLogin()) {
                try {
                    mDialog = new UserVoiceMakeDialog(this);
                    mDialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                VEApplication.toast("进行新浪微博登录");
                Message msg = Message.obtain();
                msg.what = MsgDef.MSG_USER_MAKE_DIALOG;
                TPAccountManager.getInstance().login(this, msg);
            }
        }
    }

    private void handleIntent(Intent intent) {
        System.out.println("handleIntent = " + intent);
        if (null == intent) {
            return;
        }

        Message msg = intent.getParcelableExtra(ConstValues.INTENT_KEY_MESSAGE);
        System.out.println("handleIntent = " + msg);
        if (null == msg) {
            return;
        }

        System.out.println("handleIntent = " + msg);

        if (msg.what == MsgDef.MSG_USER_MAKE_DIALOG) {
            UserVoiceMakeDialog dialog = new UserVoiceMakeDialog(this);
            dialog.show();
        }
    }

}
