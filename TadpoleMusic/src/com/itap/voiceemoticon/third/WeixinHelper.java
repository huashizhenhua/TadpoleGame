
package com.itap.voiceemoticon.third;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.itap.voiceemoticon.R;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXAppExtendObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXMusicObject;
import com.tencent.mm.sdk.openapi.WXWebpageObject;

import org.tadpoleframework.widget.image.BitmapHelper;

public class WeixinHelper {
    public final static String APP_ID = "wx21df18dbb520d624";

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
    public void sendWebpage(String title, String description, String webpageUrl, int scene) {
        WXWebpageObject obj = new WXWebpageObject();
        obj.webpageUrl = webpageUrl;

        WXMediaMessage msg = new WXMediaMessage();
        msg.title = title;
        msg.mediaObject = obj;
        msg.description = description;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.scene = scene;
        req.transaction = "" + System.currentTimeMillis();
        req.message = msg;

        mApi.sendReq(req);
    }

    /**
     * send music to weixin
     * 
     * @param title
     * @param description
     * @param musicUrl
     */
    public void sendMusicToWeixin(String title, String description, String musicUrl, String downloadPageUrl, int scene) {
        WXMusicObject musicObj = new WXMusicObject();
        musicObj.musicDataUrl = musicUrl;
        musicObj.musicUrl = downloadPageUrl;

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = musicObj;
        msg.description = description;
        msg.title = title;
        msg.thumbData = BitmapHelper.bmpToByteArray(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon), true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.scene = scene;
        req.transaction = "" + System.currentTimeMillis();
        req.message = msg;

        mApi.sendReq(req);
    }
    

}
