package com.itap.voiceemoticon.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * <br>=
 * ========================= <br>
 * author：Zenip <br>
 * email：lxyczh@gmail.com <br>
 * create：2013-2-9 <br>=
 * =========================
 */
public class ByteCache {

    private File mFile;

    public ByteCache(File file) {
        mFile = file;
    }

    public long getLength() {
        return mFile.length();
    }

    public boolean isNotEmpty() {
        return (mFile.exists() == true && mFile.length() > 0);
    }

    public String getString() {
        return new String(getBytes());
    }

    public byte[] getBytes() {
        FileInputStream in = null;
        byte[] buffer = null;
        try {
            in = new FileInputStream(mFile);
            buffer = new byte[in.available()];
            in.read(buffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                    in = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return buffer;
    }

    public RandomAccessFile openRAS() {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(mFile, "rw");
            return raf;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return raf;
    }

    public byte[] getBytes(int start, int length) {
        RandomAccessFile raf = null;
        byte[] buffer = null;
        try {
            raf = new RandomAccessFile(mFile, "rw");
            raf.seek(start);
            buffer = new byte[length];
            raf.read(buffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                    raf = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return buffer;
    }

    public boolean isCacheExists(int start, int length) {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(mFile, "rw");
            raf.seek(start);
            byte[] buffer = new byte[length];
            int bRL = raf.read(buffer);
            if (bRL != length) {
                return false;
            }
            for (int i = 0; i < bRL; i++) {
                if (buffer[i] == 0) {
                    return false;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                    raf = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
}
