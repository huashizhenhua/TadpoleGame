package com.itap.voiceemoticon.activity;

import java.util.ArrayList;

import org.tadpole.view.ViewPager;

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
import com.itap.voiceemoticon.adapter.MyPagerAdapter;
import com.itap.voiceemoticon.media.MusicData;
import com.itap.voiceemoticon.media.MusicPlayer;
import com.itap.voiceemoticon.widget.MarqueeTextView;
import com.itap.voiceemoticon.wxapi.WXEntryActivity;

public class MainActivity extends SherlockFragmentActivity implements ActionBar.TabListener {
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
            //            Log.d(VEApplication.TAG, " onReceive intent action " + intent.getAction());
            if (intent.getAction().equals(MusicPlayer.BROCAST_NAME)) {
                final Bundle data = intent.getExtras();
                int state = data.getInt(MusicPlayer.KEY_STATE);

                int brocastType = data.getInt(MusicPlayer.KEY_BROCAST_TYPE);

                if (brocastType == MusicPlayer.BROCAST_TYPE_BUFFER_UPDATE) {
                    int percent = data.getInt(MusicPlayer.KEY_PERCENT);
                    mSeekBarTime.setSecondaryProgress(percent);
                    return;
                }

                final MusicData musicData = data.getParcelable(MusicPlayer.KEY_STATE_DATA);
                //                Log.d(VEApplication.TAG, "musicData = " + musicData);
                switch (state) {
                case MusicPlayer.STATE_PLAY_START:
                    Log.d(VEApplication.TAG, " STATE_PLAY_START");
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.this.onMusicPlayStart(musicData);
                        }
                    });
                    break;
                case MusicPlayer.STATE_PLAY_PLAYING:
                    Log.d(VEApplication.TAG, " STATE_PLAY_PLAYING");
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.this.onMusicPlaying();
                            onMusicTimeAndProgressUpdate(musicData);
                        }
                    });
                    break;
                case MusicPlayer.STATE_PLAY_PREPARING:
                    Log.d(VEApplication.TAG, " STATE_PLAY_PREPARING");
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.this.onMusicPreparing();
                        }
                    });
                    break;
                case MusicPlayer.STATE_PLAY_COMPLETE:
                    Log.d(VEApplication.TAG, " STATE_PLAY_COMPLETE");
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.this.onMusicPlayComplete();
                            onMusicTimeAndProgressUpdate(musicData);
                        }
                    });
                    break;
                case MusicPlayer.STATE_INVALID:
                    Log.d(VEApplication.TAG, "STATE_INVALID");
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.this.onMusicPlayComplete();
                        }
                    });
                    break;
                case MusicPlayer.STATE_PLAY_STOP:
                    Log.d(VEApplication.TAG, "STATE_PLAY_STOP");
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.this.onMusicPlayComplete();
                        }
                    });
                    break;
                default:
                    Log.d(VEApplication.TAG, " state = " + state);
                    break;
                }

            }
        }
    };

    private void onMusicPlayStart(MusicData musicData) {
        mTextViewMusicTitle.setText(musicData.musicName);
        mTextViewMusicTitle.startFor0();
        mProgressBarPrepare.setVisibility(View.GONE);

    }

    private void onMusicPreparing() {
        mProgressBarPrepare.setVisibility(View.VISIBLE);
    }

    private void onMusicPlaying() {
        mProgressBarPrepare.setVisibility(View.INVISIBLE);
        mBtnPlay.setBackgroundResource(android.R.drawable.ic_media_pause);
    }

    private int i = 0;

    private void onMusicTimeAndProgressUpdate(final MusicData musicData) {
        mTextViewTime.setText(musicData.getTimerText());
        mSeekBarTime.setProgress(musicData.getProgress());
    }

    private void onMusicPlayComplete() {
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
        setContentView(R.layout.activity_main);

        WXEntryActivity.isRunning = true;

        // Set up the action bar to show tabs.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayUseLogoEnabled(false);
        // 

        // For each of the sections in the app, add a tab to the action bar.
        mTabHostVoice = actionBar.newTab().setText(R.string.title_section_hot_voice).setTabListener(this);
        mTabMyCollection = actionBar.newTab().setText(R.string.title_section_my_collection).setTabListener(this);
        mTabSearch = actionBar.newTab().setText(R.string.title_section_search).setTabListener(this);

        actionBar.addTab(mTabSearch);
        actionBar.addTab(mTabHostVoice);
        actionBar.addTab(mTabMyCollection);

        mBtnPlay = (ImageView) this.findViewById(R.id.btn_play);
        mTextViewTime = (TextView) this.findViewById(R.id.text_view_time);
        mSeekBarTime = (SeekBar) this.findViewById(R.id.seek_bar_time);
        mProgressBarPrepare = (ProgressBar) this.findViewById(R.id.progress_bar_preparing);
        mTextViewMusicTitle = (MarqueeTextView) this.findViewById(R.id.text_view_music_title_slide);

        mViewFooter = (View) this.findViewById(R.id.footer);
        mViewFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MusicPlayer musicPlayer = VEApplication.getMusicPlayer(getApplicationContext());
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
                final MusicPlayer musicPlayer = VEApplication.getMusicPlayer(getApplicationContext());
                musicPlayer.seek(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }
        });

        RelativeLayout container = (RelativeLayout) this.findViewById(R.id.container);

        mViewPager = new ViewPager(this);
        mViewPager.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

        container.addView(mViewPager);
        ArrayList<View> viewList = new ArrayList<View>();


        LayoutInflater inflater = LayoutInflater.from(this);
        actionBar.selectTab(mTabSearch);

        SearchFragment searchFragment = new SearchFragment(this);
        viewList.add(searchFragment.onCreateView(inflater));

        HotVoiceFragment hotVoiceFragment = new HotVoiceFragment(this);
        viewList.add(hotVoiceFragment.onCreateView(inflater));

        myCollectVoiceFragment = new MyCollectFragment(this);
        viewList.add(myCollectVoiceFragment.onCreateView(inflater));


        mViewPager.setAdapter(new MyPagerAdapter(viewList));
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                case 0:
                    actionBar.selectTab(mTabSearch);
                    break;
                case 1:
                    actionBar.selectTab(mTabHostVoice);
                    break;
                case 2:
                    actionBar.selectTab(mTabMyCollection);
                    break;
                default:
                    break;
                }
                super.onPageSelected(position);
            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicPlayer.BROCAST_NAME);
        this.registerReceiver(mMusicPlayerReceiver, intentFilter);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current tab position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getSupportActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current tab position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getSupportActionBar().getSelectedNavigationIndex());
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        if (mViewPager == null)
            return;
        if (mTabHostVoice.equals(tab)) {
            mViewPager.setCurrentItem(1);
        }
        if (mTabMyCollection.equals(tab)) {
            mViewPager.setCurrentItem(2);
        }
        if (mTabSearch.equals(tab)) {
            mViewPager.setCurrentItem(0);
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }



    //----------------------------------------------------------------
    //Action Menu Items
    //----------------------------------------------------------------


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


}
