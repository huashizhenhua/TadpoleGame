
package com.itap.voiceemoticon.util;

public class MusicUtil {

    public static final String TIME_TEXT_START = "00:00/00:00";
    
    public static final String getToTimeText(int curPostion, int duration) {
        if (duration == 0) {
            return TIME_TEXT_START;
        }
        int curTime = curPostion / 1000;
        int totalTime = duration / 1000;
        int curminute = curTime / 60;
        int cursecond = curTime % 60;
        int totalminute = totalTime / 60;
        int totalsecond = totalTime % 60;
        String curTimeString = String.format("%02d:%02d", curminute, cursecond);
        String totalTimeString = String.format("%02d:%02d", totalminute, totalsecond);
        curTimeString = curTimeString + "/" + totalTimeString;

        long end = System.currentTimeMillis();
        return curTimeString;
    }

    public static int getProgress(int curPostion, int duration) {
        if (duration == 0) {
            return 0;
        }
        return (100 * curPostion) / duration;
    }
}
