package com.tadpolemusic.media.aidl;

import com.tadpolemusic.media.MusicData;
import com.tadpolemusic.media.PlayListInfo;

interface MusicConnect {

   PlayListInfo getCurPlayListInfo();

   void getCurrentPlayListInfo(out PlayListInfo info);

   void refreshMusicList(String playListID, in List<MusicData> musicFileList);
   
   void getFileList(out List<MusicData> musicFileList);
   
   boolean rePlay();
   
   boolean play(int position);
   
   boolean pause();
   
   boolean stop();
   
   boolean playNext();
   
   boolean playPre();
   
   boolean seekTo(int rate);
   
   int getCurPosition();
   
   int getDuration();
   
   int getPlayState();
   
   int getPlayMode();
   
   void setPlayMode(int mode);
   
   void sendPlayStateBrocast();
   
   void exit();
}


