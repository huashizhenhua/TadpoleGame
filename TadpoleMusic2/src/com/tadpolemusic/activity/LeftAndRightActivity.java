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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
import com.tadpolemusic.activity.fragment.IActivityInterface;
import com.tadpolemusic.activity.fragment.LocalMusicFragment;
import com.tadpolemusic.activity.fragment.menu.LeftMenuFragment;
import com.tadpolemusic.activity.fragment.menu.RightMenuFragment;
import com.tadpolemusic.media.MusicData;
import com.tadpolemusic.media.MusicPlayer;

public class LeftAndRightActivity extends SherlockFragmentActivity implements
		IActivityInterface {

	private static class MyFragmentPagerAdapter extends FragmentPagerAdapter {

		private ArrayList<Fragment> mMyFragments;

		public MyFragmentPagerAdapter(FragmentManager fm,
				ArrayList<Fragment> mFragments) {
			super(fm);
			mMyFragments = mFragments;
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
				return 0.7f;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// init actionBar
		mActionBar = getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);
		mActionBar.setIcon(android.R.drawable.ic_menu_info_details);
		mActionBar.hide();

		// content view
		setContentView(R.layout.activity_left_right);

		// view pager
		mViewPager = (ViewPager) this.findViewById(R.id.container);

		// create fragments
		mFragments.add(new LeftMenuFragment());
		mFragments.add(new CenterFragment());
		mFragments.add(new LeftMenuFragment());

		mViewPager.setAdapter(new MyFragmentPagerAdapter(
				getSupportFragmentManager(), mFragments));
	}

	public void scrollToCenter() {
		mViewPager.setCurrentItem(1);
	}

	public void setContainer(Fragment fragment) {
		FragmentTransaction t = this.getSupportFragmentManager()
				.beginTransaction();
		t = this.getSupportFragmentManager().beginTransaction();
		t.replace(R.id.container, fragment);
		t.commit();
	}

	@Override
	protected void onStart() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(MusicPlayer.BROCAST_NAME);
		this.registerReceiver(mMusicPlayerReceiver, intentFilter);
		super.onStart();
	}

	@Override
	protected void onStop() {
		this.unregisterReceiver(mMusicPlayerReceiver);
		super.onStop();
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
	// Music Player Bar
	// -------------------------------------------

	private ImageView mBtnPlay;
	private TextView mTextViewTime;
	private MarqueeTextView mTextViewMusicTitle;
	private SeekBar mSeekBarTime;
	private ProgressBar mProgressBarPrepare;
	private View mViewFooter;

	private Handler mHandler = new Handler();

	private BroadcastReceiver mMusicPlayerReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			// Log.d(VEApplication.TAG, " onReceive intent action " +
			// intent.getAction());
			if (intent.getAction().equals(MusicPlayer.BROCAST_NAME)) {
				final Bundle data = intent.getExtras();
				int state = data.getInt(MusicPlayer.KEY_STATE);

				int brocastType = data.getInt(MusicPlayer.KEY_BROCAST_TYPE);

				if (brocastType == MusicPlayer.BROCAST_TYPE_BUFFER_UPDATE) {
					int percent = data.getInt(MusicPlayer.KEY_PERCENT);
					mSeekBarTime.setSecondaryProgress(percent);
					return;
				}

				final MusicData musicData = data
						.getParcelable(MusicPlayer.KEY_STATE_DATA);
				// Log.d(VEApplication.TAG, "musicData = " + musicData);
				switch (state) {
				case MusicPlayer.STATE_PLAY_START:
					Log.d(VEApplication.TAG, " STATE_PLAY_START");
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							LeftAndRightActivity.this
									.onMusicPlayStart(musicData);
						}
					});
					break;
				case MusicPlayer.STATE_PLAY_PLAYING:
					Log.d(VEApplication.TAG, " STATE_PLAY_PLAYING");
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							LeftAndRightActivity.this.onMusicPlaying();
							onMusicTimeAndProgressUpdate(musicData);
						}
					});
					break;
				case MusicPlayer.STATE_PLAY_PREPARING:
					Log.d(VEApplication.TAG, " STATE_PLAY_PREPARING");
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							LeftAndRightActivity.this.onMusicPreparing();
						}
					});
					break;
				case MusicPlayer.STATE_PLAY_COMPLETE:
					Log.d(VEApplication.TAG, " STATE_PLAY_COMPLETE");
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							LeftAndRightActivity.this.onMusicPlayComplete();
							onMusicTimeAndProgressUpdate(musicData);
						}
					});
					break;
				case MusicPlayer.STATE_INVALID:
					Log.d(VEApplication.TAG, "STATE_INVALID");
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							LeftAndRightActivity.this.onMusicPlayComplete();
						}
					});
					break;
				case MusicPlayer.STATE_PLAY_STOP:
					Log.d(VEApplication.TAG, "STATE_PLAY_STOP");
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							LeftAndRightActivity.this.onMusicPlayComplete();
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

	private void onCreateMusic() {
		setContainer(new LocalMusicFragment());

		mBtnPlay = (ImageView) this.findViewById(R.id.btn_play);
		mTextViewTime = (TextView) this.findViewById(R.id.text_view_time);
		mSeekBarTime = (SeekBar) this.findViewById(R.id.seek_bar_time);
		mProgressBarPrepare = (ProgressBar) this
				.findViewById(R.id.progress_bar_preparing);
		mTextViewMusicTitle = (MarqueeTextView) this
				.findViewById(R.id.text_view_music_title_slide);

		mViewFooter = (View) this.findViewById(R.id.footer);
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

		mSeekBarTime
				.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {

					}
				});
	}

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
	public void setTitle(String title) {
		// TODO Auto-generated method stub

	}

}
