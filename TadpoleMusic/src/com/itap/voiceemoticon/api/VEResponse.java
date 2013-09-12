package com.itap.voiceemoticon.api;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * server protocol response
 * <br>==========================
 * <br> author：Zenip
 * <br> email：lxyczh@gmail.com
 * <br> create：2013-1-23下午10:11:34
 * <br>==========================
 */
public class VEResponse {
    public static final String KEY_STATUS = "status";
    public static final String KEY_MSG = "msg";
    public static final String KEY_DATA = "data";

    public static final int STATUS_OK = 0;
    // server error status > 0
    public static final int STATUS_DEFAULT_ERR = 1;

    // client error status < 0
    public static final int STATUS_NO_RESPONSE = -1;


    public int status = STATUS_NO_RESPONSE;
    public String msg = "";
    public JSONObject data = null;

    public boolean isSuccess() {
        return status == STATUS_OK;
    }

    public static VEResponse getErrorResponse() {
        VEResponse resp = new VEResponse();
        resp.status = STATUS_NO_RESPONSE;
        resp.msg = "";
        resp.data = null;
        return resp;
    }

    public static VEResponse buildFromJSONString(String jsonStr) {
        if (jsonStr == null) {
            return VEResponse.getErrorResponse();
        }
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(jsonStr);
            VEResponse response = new VEResponse();
            response.status = jsonObj.optInt(KEY_STATUS, STATUS_NO_RESPONSE);
            response.msg = jsonObj.optString(KEY_MSG, "");
            response.data = jsonObj.optJSONObject(KEY_DATA);
            return response;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return VEResponse.getErrorResponse();
    }

}
