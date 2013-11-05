
package com.itap.voiceemoticon.activity.fragment;

import com.itap.voiceemoticon.R;
import com.itap.voiceemoticon.VEApplication;
import com.itap.voiceemoticon.api.VEResponse;
import com.itap.voiceemoticon.db.UserVoiceModel;
import com.itap.voiceemoticon.util.HttpManager;
import com.itap.voiceemoticon.weibo.TPAccount;
import com.itap.voiceemoticon.weibo.TPAccountManager;
import com.pocketdigi.utils.FLameUtils;

import org.tadpoleframework.app.AlertDialog;
import org.tadpoleframework.app.DialogOnClickListener;
import org.tadpoleframework.app.GenericDialog;
import org.tadpoleframework.common.FileUtil;
import org.tadpoleframework.common.StringUtil;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class UserVoiceMakeDialog extends AlertDialog implements DialogOnClickListener {

    private static final long TIME_SPAN_RECORD_MIN = 2000;

    private static String mTmpFileName = null;

    private static String mTmpFileNameMp3 = null;

    private EditText mEditTextTitle;

    private Button mBtnVoice;

    private Button mBtnPlay;

    private TextView mTxtViewResult;

    private AudioRecord mRecorder = null;

    private MediaPlayer mPlayer = null;

    private long mRecordStartTime = 0;

    private UserVoiceModel userVoiceModel;

    private boolean isRecordSuccess = false;

    private boolean isRecording = false;

    public UserVoiceMakeDialog(Context context) {
        super(context);
        setTitle("制作语音");

        Button btnYes = (Button)this.findViewById(ID_BUTTON_YES);
        btnYes.setText("保存");

        setContent(R.layout.uservoice_make);

        mBtnPlay = (Button)findViewById(R.id.btn_play);

        mBtnVoice = (Button)findViewById(R.id.btn_siri);

        mEditTextTitle = (EditText)findViewById(R.id.ed_title);

        mTxtViewResult = (TextView)findViewById(R.id.txt_result);

        mTmpFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mTmpFileName += "/voiceemoticon/audiorecordtest.3gp";

        mTmpFileNameMp3 = Environment.getExternalStorageDirectory().getAbsolutePath();
        mTmpFileNameMp3 += "/voiceemoticon/audiorecordtest.mp3";

        userVoiceModel = UserVoiceModel.getDefaultUserVoiceModel();

        mBtnVoice.setOnTouchListener(mBtnRecordTouchlistener);

        setOnClickListener(this);

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

    private short[] mBuffer = null;

    private void startRecording() {

        mRecordStartTime = System.currentTimeMillis();

        int bufferSize = AudioRecord.getMinBufferSize(16000, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        mBuffer = new short[bufferSize];
        mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, 16000,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);

        mRecorder.startRecording();
        isRecording = true;
        try {
            FileUtil.createFileWithDir(mTmpFileName);
            startBufferedWrite(new File(mTmpFileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 写入到文件
     * 
     * @param file
     */
    private void startBufferedWrite(final File file) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DataOutputStream output = null;
                try {
                    output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(
                            file)));
                    while (isRecording) {
                        int readSize = mRecorder.read(mBuffer, 0, mBuffer.length);
                        
                        System.out.println("startBufferedWrite");
                        
                        for (int i = 0; i < readSize; i++) {
                            output.writeShort(mBuffer[i]);
                        }
                    }
                } catch (IOException e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                } finally {
                    if (output != null) {
                        try {
                            output.flush();
                        } catch (IOException e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        } finally {
                            try {
                                output.close();
                            } catch (IOException e) {
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    }
                }
            }
        }).start();
    }

    private void stopRecording() {
        if (null == mRecorder) {
            return;
        }

        mRecorder.stop();

        isRecording = false;

        mRecorder.release();
        mRecorder = null;

        long recordTimeSpan = System.currentTimeMillis() - mRecordStartTime;
        if (recordTimeSpan < TIME_SPAN_RECORD_MIN) {
            mTxtViewResult.setText("录制时间过短");
        } else {
            mTxtViewResult.setText("录制成功, 时间为" + (recordTimeSpan / 1000) + "秒");
            isRecordSuccess = true;
            mBtnPlay.setVisibility(View.VISIBLE);

            FLameUtils lameUtils = new FLameUtils(1, 16000, 96);
            try {
                FileUtil.createFileWithDir(mTmpFileNameMp3);
                System.out.println(lameUtils.raw2mp3(mTmpFileName, mTmpFileNameMp3));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void saveVoice() {
        final String title = mEditTextTitle.getEditableText().toString();
        
        if (StringUtil.isEmpty(title)) {
            Toast.makeText(getContext(), "标题不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isRecordSuccess) {
            Toast.makeText(getContext(), "尚未录制任何语音", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                
                VEApplication.toast("您录制的语音正在上传至服务器");
            	TPAccount veAccount = TPAccountManager.getInstance().getVEAccount();
            	
                // 通过Map构造器传参
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("uid", veAccount.uid);
                params.put("platform", veAccount.platform);
                params.put("title", title);
                String fileName = System.currentTimeMillis() + ".mp3";
                try {
                    VEResponse vResp = VEApplication.getVoiceEmoticonApi().uploadVoice(params, mTmpFileNameMp3, fileName);
                    if (vResp.isSuccess()) {
                        mUrl = vResp.data.optString("url");
                        System.out.println("saveVoice url = " + mUrl);
                        userVoiceModel.saveVoice(title, mTmpFileNameMp3, mUrl);
                        VEApplication.toast("语音上传成功");
                    } else {
                    	System.out.println("vResp.msg = " + vResp.msg);
                    	VEApplication.toast(vResp.msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
    
    String mUrl = null;

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            // 设置要播放的文件
            mPlayer.setDataSource(mTmpFileNameMp3);
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

    @Override
    public void onDialogClick(GenericDialog dialog, int viewId, Object extra) {
        if (viewId == ID_BUTTON_YES) {
            saveVoice();
        }
    }
}
