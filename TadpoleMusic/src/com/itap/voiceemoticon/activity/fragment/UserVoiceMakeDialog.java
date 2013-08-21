
package com.itap.voiceemoticon.activity.fragment;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.tadpoleframework.app.AlertDialog;
import org.tadpoleframework.common.StringUtil;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.itap.voiceemoticon.R;
import com.itap.voiceemoticon.db.UserVoice;
import com.itap.voiceemoticon.db.UserVoiceModel;
import com.itap.voiceemoticon.util.FormFile;
import com.itap.voiceemoticon.util.SocketHttpRequester;

public class UserVoiceMakeDialog extends AlertDialog implements OnClickListener {

    private static final long TIME_SPAN_RECORD_MIN = 2000;

    private static String mTmpFileName = null;

    private EditText mEditTextTitle;

    private Button mBtnVoice;

    private Button mBtnPlay;

    private TextView mTxtViewResult;

    private MediaRecorder mRecorder = null;

    private MediaPlayer mPlayer = null;

    private long mRecordStartTime = 0;

    private UserVoiceModel userVoiceModel;

    private boolean isRecordSuccess = false;

    public UserVoiceMakeDialog(Context context) {
        super(context);
        setTitle("制作语音");

        Button btnYes = (Button)this.findViewById(R.id.btn_yes);
        btnYes.setText("保存");

        setContent(R.layout.uservoice_make);

        mBtnPlay = (Button)findViewById(R.id.btn_play);

        mBtnVoice = (Button)findViewById(R.id.btn_siri);

        mEditTextTitle = (EditText)findViewById(R.id.ed_title);

        mTxtViewResult = (TextView)findViewById(R.id.txt_result);

        mTmpFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mTmpFileName += "/audiorecordtest.3gp";

        userVoiceModel = UserVoiceModel.getDefaultUserVoiceModel();

        mBtnVoice.setOnTouchListener(mBtnRecordTouchlistener);

        setButtonListener(this);

        mBtnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                stopPlaying();
                startPlaying();
            }
        });
    }

    // 当录音按钮被click时调用此方法，开始或停止录音
    private void onRecord(boolean start) {
        if (start) {
            mTxtViewResult.setText("请说话");
            startRecording();
        } else {
            stopRecording();
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
            Log.e("UserVoiceMakeDialog", "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        if (null == mRecorder) {
            return;
        }

        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;

        long recordTimeSpan = System.currentTimeMillis() - mRecordStartTime;
        if (recordTimeSpan < TIME_SPAN_RECORD_MIN) {
            mTxtViewResult.setText("录制时间过短");
        } else {
            mTxtViewResult.setText("录制成功, 时间为" + (recordTimeSpan / 1000) + "秒");
            isRecordSuccess = true;
            mBtnPlay.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        if (which == R.id.btn_yes) {
            saveVoice();
        }

    }

    private void saveVoice() {
        final String title = mEditTextTitle.getEditableText().toString();
        if (StringUtil.isEmpty(title)) {
            Toast.makeText(getContext(), "标题不能为空", Toast.LENGTH_SHORT).show();
        }
        if (!isRecordSuccess) {
            Toast.makeText(getContext(), "尚未录制任何语音", Toast.LENGTH_SHORT).show();
        }
        final UserVoice userVoice = userVoiceModel.saveVoice(title, mTmpFileName);

        if (null == userVoice) {
            return;
        }
        
        new Thread(new Runnable() {
            
            @Override
            public void run() {
             // 通过Map构造器传参
                Map<String, String> params = new HashMap<String, String>();
                File uploadFile = new File(userVoice.path);
                params.put("uid", "unknown");
                params.put("platform", "unknown");
                params.put("title", title);
                try {
                    // 取得上传文件的名称
                    // 文件上传JavaBean
                    // ，通过New调用构造方法（此处是调用第二个构造函数，以输入、输出流上传），audio/mpeg为Mp3文件的内容类型
                    // 如果不知道上传文件的内容类型，可以在IE浏览器上传一个文件测试，在后台观察源码（通过HttpWatch）
                    FormFile formfile = new FormFile(uploadFile.getName(), uploadFile, "file", "audio/mpeg");
                    SocketHttpRequester.post("http://vetest.sinaapp.com/user_voice_upload", params,
                            formfile);
                    Toast.makeText(getContext(), "dsfsdfsdf", 1).show();
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
            }
        }).start();
        
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            // 设置要播放的文件
            mPlayer.setDataSource(mTmpFileName);
            mPlayer.prepare();
            // 播放之
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 停止播放
    private void stopPlaying() {
        if (null == mPlayer) {
            return;
        }
        mPlayer.release();
        mPlayer = null;
    }
}
