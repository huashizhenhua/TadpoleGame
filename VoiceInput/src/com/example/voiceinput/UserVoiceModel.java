
package com.example.voiceinput;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.tadpoleframework.common.FileUtil;
import org.tadpoleframework.common.SDCardUtil;
import org.tadpoleframework.common.StringUtil;
import org.tadpoleframework.model.BaseModel;

import android.content.Context;

/**
 * Voice Data Path =
 * /data/data/{packageName}/files/users/{appUid}/uservoice.json Voice Record
 * Path = /sdcard/voiceemoticon/uservoice/{timeStamp}.3gp
 * 
 * @author Administrator
 */
public class UserVoiceModel extends BaseModel<UserVoice> {
    
    
    /**
     * @param clazz
     * @param appUid App Uid is suggest to be a uid for compating the same as
     *            sina uid and baidu uid or more.
     */
    public UserVoiceModel(Context context, String appUid) {
        super(UserVoice.class);
        // config save path
        // setSavePath(savePath)
        if (StringUtil.isBlank(appUid)) {
            appUid = "userdefault";
        }

        String savePath = context.getFilesDir().getAbsolutePath() + File.separator + "user"
                + File.separator + appUid + File.separator + "uservoice.json";
        setSavePath(savePath);
    }

    public String getVoiceSavePath() {
        try {
            return SDCardUtil.getSDPath() + File.separator + "voiceemoticon" + File.separator
                    + "uservoice" + File.separator + System.currentTimeMillis() + ".3gp";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 将语音从tmpPath转换成用户的path
     * 
     * @param tmpPath
     */
    public void saveVoice(String title, String voiceTmpPath) {
        UserVoice userVoice = new UserVoice();
        userVoice.title = title;
        
        String voicePath = copyTmpToUser(voiceTmpPath);
        if(StringUtil.isBlank(voicePath)) {
            System.err.println("saveVoice error voicePath = " + voicePath);
            return;
        }
        userVoice.path = voiceTmpPath;
        add(userVoice);
        
    }
    
    private String copyTmpToUser(String tmpPath) {
        String dstFilePath;
        dstFilePath = getVoiceSavePath();
        try {
            FileUtil.createFileWithDir(dstFilePath);
            if (FileUtil.copyFile(tmpPath, dstFilePath, true)) {
                return dstFilePath;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
