
package com.itap.voiceemoticon.third;

import android.app.Activity;
import android.content.Context;

import com.itap.voiceemoticon.VEApplication;
import com.sina.weibo.sdk.WeiboSDK;
import com.sina.weibo.sdk.api.IWeiboAPI;
import com.sina.weibo.sdk.api.MusicObject;
import com.sina.weibo.sdk.api.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMessage;

public class WeiboHelper {

    public static final String APP_KEY = "1828437997";

    public WeiboHelper(Context context) {
    }

    public void sendMusic(Activity activity, String musicUrl) {
//        MusicObject musicObject = new MusicObject();
//        musicObject.duration = 11;
//        musicObject.description = musicUrl;
//        musicObject.identify = musicUrl;
//        musicObject.dataUrl = musicUrl;
//        musicObject.dataHdUrl = musicUrl;
//        musicObject.h5Url = musicUrl;
//        musicObject.actionUrl = musicUrl;
//        musicObject.defaultText = musicUrl;
//        musicObject.title = musicUrl;
//        musicObject.schema = "";
//        musicObject.thumbData = new byte[]{};
        
        TextObject textObject = new TextObject();
        textObject.text = "sdfsdfdsfsd";

        
        WeiboMessage weiboMessage = new WeiboMessage();
        weiboMessage.mediaObject = textObject;

        SendMessageToWeiboRequest req = new SendMessageToWeiboRequest();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = weiboMessage;

        VEApplication.sWeiboApi.sendRequest(activity, req);
    }
}
