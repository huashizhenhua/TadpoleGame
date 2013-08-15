
package com.example.voiceinput;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;

public class RadioRecorder {

    private static final String TAG = "RadioRecorder";

    private MediaRecorder mRecorder;

    public RadioRecorder() {
        mRecorder = new MediaRecorder();
    }

    public void start() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        String filename = "hello.3gp";

        mRecorder.setOutputFile(filename);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }
}
