package com.itap.voiceemoticon.profit;

import android.app.Activity;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.google.ads.AdRequest;

public class GoogleAdmob {
    
    private static String GOOGLE_AD_ID = "a1520506c5f2244";

    public static void addAdView(Activity activity, ViewGroup container) {
      
    }
    public static LinearLayout createLayoutWithAd(Activity activity) {
        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        com.google.ads.AdView goodAdv = new com.google.ads.AdView(activity, com.google.ads.AdSize.BANNER, GOOGLE_AD_ID);
        layout.addView(goodAdv);
        goodAdv.loadAd(new AdRequest());
        
        return layout;
    } 
}
