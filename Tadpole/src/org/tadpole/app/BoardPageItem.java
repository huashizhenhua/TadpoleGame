package org.tadpole.app;

import org.json.JSONObject;

import android.graphics.Color;

public class BoardPageItem {
    public static final String COLOR_RED = "orange";
    public static final String COLOR_BLUE = "blue";

    public String name;
    public String title;
    public String icon;
    public String color = COLOR_BLUE;
    public boolean editable = true;

    /**
     * used runtime
     */
    public String id;
    public boolean hide;
    public int sortTag = 0;

    public static BoardPageItem fromJSONObject(JSONObject jsonObj) {
        BoardPageItem item = new BoardPageItem();
        item.color = jsonObj.optString("color", COLOR_BLUE);
        item.name = jsonObj.optString("name");
        item.title = jsonObj.optString("title");
        item.icon = jsonObj.optString("icon");
        item.editable = jsonObj.optBoolean("editable", true);
        return item;
    }
    
    public JSONObject toJSONObject(){
        return null;
    }
}
