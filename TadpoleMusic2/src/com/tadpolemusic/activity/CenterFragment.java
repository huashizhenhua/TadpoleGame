package com.tadpolemusic.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.itap.voiceemoticon.widget.MarqueeTextView;
import com.tadpolemusic.R;
import com.tadpolemusic.VEApplication;
import com.tadpolemusic.activity.fragment.AbsMenuFragment;
import com.tadpolemusic.activity.fragment.LocalMusicFragment;
import com.tadpolemusic.media.MusicData;
import com.tadpolemusic.media.MusicPlayer;

public class CenterFragment extends AbsMenuFragment {

	private ViewGroup mViewGroup;

	private void setContainer(Fragment fragment) {
		FragmentTransaction t = getActivity().getSupportFragmentManager()
				.beginTransaction();
		t.replace(R.id.container, fragment);
		t.commit();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mViewGroup = (ViewGroup) inflater.inflate(R.layout.main_center, null);
		onCreateMusic();
		return mViewGroup;
	}

	private View findViewById(int resId) {
		return mViewGroup.findViewById(resId);
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
			final CenterFragment me = CenterFragment.this;

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
							me.onMusicPlayStart(musicData);
						}
					});
					break;
				case MusicPlayer.STATE_PLAY_PLAYING:
					Log.d(VEApplication.TAG, " STATE_PLAY_PLAYING");
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							me.onMusicPlaying();
							onMusicTimeAndProgressUpdate(musicData);
						}
					});
					break;
				case MusicPlayer.STATE_PLAY_PREPARING:
					Log.d(VEApplication.TAG, " STATE_PLAY_PREPARING");
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							me.onMusicPreparing();
						}
					});
					break;
				case MusicPlayer.STATE_PLAY_COMPLETE:
					Log.d(VEApplication.TAG, " STATE_PLAY_COMPLETE");
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							me.onMusicPlayComplete();
							onMusicTimeAndProgressUpdate(musicData);
						}
					});
					break;
				case MusicPlayer.STATE_INVALID:
					Log.d(VEApplication.TAG, "STATE_INVALID");
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							me.onMusicPlayComplete();
						}
					});
					break;
				case MusicPlayer.STATE_PLAY_STOP:
					Log.d(VEApplication.TAG, "STATE_PLAY_STOP");
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							me.onMusicPlayComplete();
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
//		setContainer(new LocalMusicFragment());

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
						.getMusicPlayer(getActivity().getApplicationContext());
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
								.getMusicPlayer(getActivity().getApplicationContext());
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
}
