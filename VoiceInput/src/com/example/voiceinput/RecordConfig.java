
package com.example.voiceinput;

import android.os.Environment;

import java.io.File;

public class RecordConfig {

    private static String sVoiceSaveDir = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "voiceemoticon" + File.separator;

    /**
     * 获取个人语音保存路径
     * 
     * @param fileName
     * @return
     */
    public static String getVoiceFilePath(String fileName) {
        return sVoiceSaveDir + fileName;
    }

}
