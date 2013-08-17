
package com.example.voiceinput;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private static final String LOG_TAG = "AudioRecordTest";

    private static String mTmpFileName = null;

    // 录音按钮
    private Button mSaveButton = null;
    
    // 录音按钮
    private RecordButton mRecordButton = null;

    private MediaRecorder mRecorder = null;
    
    private TextView mTextView = null;

    // 回放按钮
    private PlayButton mPlayButton = null;

    private MediaPlayer mPlayer = null;

    private long mRecordStartTime = 0;
    
    private UserVoiceModel userVoiceModel;

    // 当录音按钮被click时调用此方法，开始或停止录音
    private void onRecord(boolean start) {
        if (start) {
            mTextView.setText("请说话");
            startRecording();
        } else {
            stopRecording();
        }
    }

    // 当播放按钮被click时调用此方法，开始或停止播放
    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        ArrayList<UserVoice> list = userVoiceModel.getAll();
        System.out.println("UserVoice = " + list);
        
        mPlayer = new MediaPlayer();
        try {
            // 设置要播放的文件
            mPlayer.setDataSource(mTmpFileName);
            mPlayer.prepare();
            // 播放之
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    // 停止播放
    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;

    }

    private void startRecording() {

        mRecordStartTime = System.currentTimeMillis();

        mRecorder = new MediaRecorder();
        // 设置音源为Micphone
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // 设置封装格式
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mTmpFileName);
        // 设置编码格式
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private static final long TIME_SPAN_RECORD_MIN = 2000;

    private void stopRecording() {
        if (null == mRecorder) {
            return;
        }
        
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;

        long recordTimeSpan = System.currentTimeMillis() - mRecordStartTime;
        if (recordTimeSpan < TIME_SPAN_RECORD_MIN) {
            mTextView.setText("录制时间过短");
        } else {
            mTextView.setText("录制成功");
            userVoiceModel.saveVoice("hello world",  mTmpFileName);
        }
    }

    private OnTouchListener mBtnRecordTouchlistener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            final int action = event.getAction();

            if (MotionEvent.ACTION_DOWN == action) {
                onRecord(true);
            }

            if (MotionEvent.ACTION_UP == action || MotionEvent.ACTION_CANCEL == action) {
                
                System.out.println("action = " + action);
                
                onRecord(false);
            }

            return false;

        }
    };

    // 定义录音按钮
    class RecordButton extends Button {
        boolean mStartRecording = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    setText("Stop recording");
                } else {
                    setText("Start recording");
                }
                mStartRecording = !mStartRecording;
            }
        };

        public RecordButton(Context ctx) {
            super(ctx);
            setText("按下说话");
            setOnTouchListener(mBtnRecordTouchlistener);
        }
    }

    // 定义播放按钮
    class PlayButton extends Button {
        boolean mStartPlaying = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    setText("Stop playing");
                } else {
                    setText("Start playing");
                }
                mStartPlaying = !mStartPlaying;
            }
        };

        public PlayButton(Context ctx) {
            super(ctx);
            setText("Start playing");
            setOnClickListener(clicker);
        }
    }

    // 构造方法
    public MainActivity() {
        mTmpFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mTmpFileName += "/audiorecordtest.3gp";
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        userVoiceModel = new UserVoiceModel(this, null);
        
        // 构造界面
        LinearLayout ll = new LinearLayout(this);
        
        mRecordButton = new RecordButton(this);
        ll.addView(mRecordButton, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0));
        
        mPlayButton = new PlayButton(this);
        ll.addView(mPlayButton, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0));
        
        mSaveButton = new Button(this);
        ll.addView(mSaveButton, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0));
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });
        
        mTextView = new TextView(this);
        ll.addView(mTextView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0));
        
        setContentView(ll);
    }
    
    @Override
    public void onPause() {
        super.onPause();
        // Activity暂停时释放录音和播放对象
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
}
