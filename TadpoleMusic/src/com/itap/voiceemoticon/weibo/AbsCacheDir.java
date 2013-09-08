
package com.itap.voiceemoticon.weibo;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import org.tadpoleframework.common.FileUtil;

import android.content.Context;

public abstract class AbsCacheDir {

    private String mCachePath;

    public AbsCacheDir(Context context, String dirName) {
        this.mCachePath = (context.getFilesDir() + File.separator + dirName + File.separator);
        FileUtil.createDir(this.mCachePath);
    }

    protected File createSubDir(String name) {
        String str = this.mCachePath + name + File.separator;
        FileUtil.createDir(str);
        return new File(str);
    }

    protected File createSubFile(String name) {
        String str = this.mCachePath + name;
        FileUtil.createFile(str);
        return new File(str);
    }

    public String readSubFile(String name) throws IOException {
        File file = getSubFile(name);
        return FileUtil.readFile(file);
    }

    public boolean existSubFile(String name) {
        File file = getSubFile(name);
        return file.exists();
    }

    private File getSubFile(String name) {
        String str = this.mCachePath + name;
        File file = new File(str);
        return file;
    }

    protected void close(Closeable obj) {
        if (obj != null) {
            try {
                obj.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
