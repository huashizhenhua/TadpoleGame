
package com.itap.voiceemoticon.activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class NotificationID {

    /**
     * 制作用户语音消息
     */
    public static final int N_USERVOICE_MAKE = generateNotificationID();
    
    public static final int N_USERVOICE_MODEL_SAVE = generateNotificationID();
    

    public static final int N_ORIENTATION_CHANGE = generateNotificationID();

    public static final int N_POWER_CHANGE = generateNotificationID();

    public static final int N_THEME_CHANGE = generateNotificationID();

    public static final int N_FOREGROUND_CHANGE = generateNotificationID();

    public static final int N_STARTUP_FINISHED = generateNotificationID();

    public static final int N_DEVICE_LOW_MEMORY = generateNotificationID();

    public static final int N_SETTING_CHANGE = generateNotificationID(); // 设置发生变化

    public static final int N_CLEAR_SETTING = generateNotificationID(); // 清除记录通知到内核

    public static final int N_RESET_SETTING = generateNotificationID();

    public static final int N_NETWORK_STATE_CHANGE = generateNotificationID();

    public static final int N_WEBVIEW_INIT_FINISHED = generateNotificationID();

    public static final int N_INIT_DEFER = generateNotificationID(); // 初始化剩下的model的部分

    public static final int N_ON_EXITING = generateNotificationID();

    public static final int N_FULL_SCREEN_MODE_CHANGE = generateNotificationID(); // 全屏模式变化

    public static final int N_INIT_U3_NETWORK = generateNotificationID(); // 初始化u3
                                                                          // webcore

    public static final int N_VERIFY_USER = generateNotificationID(); // 灰度验证

    public static final int N_CLIPBOARD_DATA_CHANGED = generateNotificationID(); // 剪贴板

    public static final int N_SEARCHBAR_TOOLBAR_COMPOSITE = generateNotificationID(); // 工具栏与地址栏合体

    public static final int N_APPMANAGER_ICONS_CLEANUP = generateNotificationID(); // 低内存时清理应用管理图标通知

    public static final int N_MEMORY_EVENT = generateNotificationID(); // 内存消息通知

    public static final int N_ADDON_NIGNATURE_WHITE_LIST_CHANGED = generateNotificationID(); // 插件白名单改变消息

    public static final int N_LAUNCHER_ORIENTATION_CHANGE_ON_LARGE_SCREEN = generateNotificationID(); // 大屏幕设备上转屏时通知widget改变大小



    private static int mNotificationBase = 0;

    private static int generateNotificationID() {
        return mNotificationBase++;
    }

}
