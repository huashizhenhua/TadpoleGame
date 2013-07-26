
package com.itap.voiceemoticon.third;

import android.content.Context;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXAppExtendObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXMusicObject;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

public class WeixinHelper {
    public final static String APP_ID = "wxc016b630efe232d5";

    private IWXAPI mApi;

    private Context mContext;

    public WeixinHelper(Context context) {
        this.mContext = context;
        mApi = WXAPIFactory.createWXAPI(context, WeixinHelper.APP_ID, true);
        mApi.registerApp(WeixinHelper.APP_ID);
    }

    /**
     * send my webpage to friends
     * 
     * @param extInfo app descriptioin
     * @param description
     */
    public void sendWebpage(String title, String description, String webpageUrl) {
        WXWebpageObject obj = new WXWebpageObject();
        obj.webpageUrl = webpageUrl;

        WXMediaMessage msg = new WXMediaMessage();
        msg.title = title;
        msg.mediaObject = obj;
        msg.description = description;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = "" + System.currentTimeMillis();
        req.message = msg;

        mApi.sendReq(req);
    }

    /**
     * send music
     * 
     * @param title
     * @param description
     * @param musicUrl
     */
    public void sendMusic(String title, String description, String musicUrl) {
        WXMusicObject musicObj = new WXMusicObject();
        musicObj.musicUrl = musicUrl;

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = musicObj;
        msg.description = description;
        msg.title = title;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.scene = SendMessageToWX.Req.WXSceneSession;
        req.transaction = "" + System.currentTimeMillis();
        req.message = msg;

        mApi.sendReq(req);
    }

}
