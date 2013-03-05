package com.tadpolemusic.media;


public class MusicPlayState {

    public static final int MPS_NOFILE = -1;			// �������ļ�

    public static final int MPS_INVALID = 0;			// ��ǰ�����ļ���Ч

    public static final int MPS_PREPARE = 1;			// ׼������

    public static final int MPS_PLAYING = 2;			// ������

    public static final int MPS_PAUSE = 3;				// ��ͣ


    // TODO this may ugly
    public static final int MPS_HAS_FILE = 4;


    public static final String PLAY_STATE_NAME = "PLAY_STATE_NAME";
    public static final String PLAY_MUSIC_INDEX = "PLAY_MUSIC_INDEX";
    public static final String MUSIC_INVALID = "MUSIC_INVALID";
    public static final String MUSIC_PREPARE = "MUSIC_PREPARE";
    public static final String MUSIC_PLAY = "MUSIC_PLAY";
    public static final String MUSIC_PAUSE = "MUSIC_PAUSE";
    public static final String MUSIC_STOP = "MUSIC_STOP";

}