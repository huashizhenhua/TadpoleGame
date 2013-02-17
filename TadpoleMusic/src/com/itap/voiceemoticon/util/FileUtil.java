package com.itap.voiceemoticon.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.Activity;

import com.itap.voiceemoticon.db.FileOperationListener;

/**
 * <br>==========================
 * <br> author：Zenip
 * <br> email：lxyczh@gmail.com
 * <br> create：2013-1-25上午8:32:01
 * <br>==========================
 */
public class FileUtil {

    public static final String CLASS_NAME = "FileUtil";

    /**
     * 字节缓冲区长度
     */
    public static final int BUFFER_SIZE = 8192;

    public static boolean exists(String path) {
        return new File(path).exists();
    }

    public static boolean createDir(String dirPath) {
        File file = new File(dirPath);
        if (file.exists()) {
            if (file.isDirectory()) {
                return false;
            } else {
                file.delete();
            }
        }
        try {
            return file.mkdirs();
        } catch (Exception exception) {

        }
        return false;
    }

    public static boolean createFile(String dirPath, String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return false;
        }
        try {
            if (!createDir(dirPath))
                return false;

            return file.createNewFile();
        } catch (Exception exception) {
            // Logger.e("create file error ", exception);
        }
        return false;
    }

    public static boolean createFile(String paramString) {
        File f = new File(paramString);
        if (f.exists()) {
            return false;
        }

        try {
            return f.createNewFile();
        } catch (Exception exception) {

            // Logger.e("create file error ", exception);
        }
        return false;
    }

    public static void unZip(Activity paramActivity, String zipFilePath, String dirPath) {
        File file = new File(zipFilePath);
        if ((paramActivity == null) || (!file.exists())) {
            return;
        }

        try {
            FileInputStream fileInputStream = new FileInputStream(zipFilePath);
            unZip(fileInputStream, dirPath, null);
        } catch (Exception exception) {
            // if (b.bD)
            // Logger.e("UnZip 文件操作失败是否没有申明WRITE_EXTERNAL_STORAGE权限 ",
            // exception);
            // else

            // Logger.e("UnZip error, uses permission WRITE_EXTERNAL_STORAGE",
            // exception);
        }
    }

    public static boolean unZip(InputStream inputStream, String dirPath, FileOperationListener<File> operListener) {
        boolean bRet = true;
        try {
            File file = new File(dirPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            ZipInputStream zipInputStream = new ZipInputStream(inputStream);
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                Object object;
                if (zipEntry.isDirectory()) {
                    object = zipEntry.getName();
                    object = ((String) object).substring(0, ((String) object).length() - 1);
                    file = new File(dirPath + File.separator + (String) object);
                    file.mkdirs();
                } else {
                    file = new File(dirPath + File.separator + zipEntry.getName());

                    //					file.createNewFile();

                    if (file.exists()) {
                        file.delete();
                    }


                    FileOutputStream fos = new FileOutputStream(file);

                    byte[] arrayOfByte = new byte[BUFFER_SIZE];
                    int i = -1;
                    while ((i = zipInputStream.read(arrayOfByte)) > 0) {
                        fos.write(arrayOfByte, 0, i);

                    }

                    // 刷新数据并将数据转交给操作系统
                    fos.flush();
                    fos.getFD().sync();
                    fos.close();
                    if (operListener != null) {
                        operListener.callback(file);
                    }
                }
                zipEntry = zipInputStream.getNextEntry();
            }
            zipInputStream.close();
        } catch (Exception exception) {
            // Logger.e("UnZip2 error, uses permission WRITE_EXTERNAL_STORAGE",
            // exception);

            bRet = false;
        }
        return bRet;
    }

    /**
     * @param path
     *            can be file or dir
     */
    public static void delete(String path) {
        if (path == null)
            return;
        File file = new File(path);
        if ((file == null) || (!file.exists())) {
            return;
        }

        if (file.isDirectory()) {
            File[] arrayOfFile = file.listFiles();
            for (int i = 0; i < arrayOfFile.length; i++) {
                delete(arrayOfFile[i].toString());
            }
        }
        file.delete();
    }

    /**
     * 删除目录（不包括目录本身）下的文件及目录
     * 
     * @param path
     */
    public static void deleteSubFile(String path) {
        if (path == null)
            return;
        File file = new File(path);
        if ((file == null) || (!file.exists())) {
            return;
        }

        if (file.isDirectory()) {
            File[] arrayOfFile = file.listFiles();
            for (int i = 0; i < arrayOfFile.length; i++) {
                delete(arrayOfFile[i].toString());
            }
        }
    }

    public static void rename(String path, String reNamePath) {
        if ((path == null) || (reNamePath == null))
            return;
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        File reNameFile = new File(reNamePath);
        file.renameTo(reNameFile);
    }

    /**
     * 
     * @param srcFilePath
     *            原始文件路径
     * @param dstFilePath
     *            目标文件路径
     * @param forced
     *            是否强制覆盖
     * @return
     */
    public static boolean copyFile(String srcFilePath, String dstFilePath, boolean forced) {
        if ((srcFilePath == null) || (dstFilePath == null))
            return false;
        File srcFile = new File(srcFilePath);
        if (!srcFile.exists()) {
            return false;
        }
        try {
            File dstFile = new File(dstFilePath);
            if ((!forced) && (dstFile.exists())) {
                return false;
            }
            if ((!dstFile.exists()) && (!dstFile.createNewFile()))
                return false;

            FileInputStream fileInputStream = new FileInputStream(dstFile);
            FileOutputStream fileOutputStream = new FileOutputStream(dstFile);
            byte[] arrayOfByte = new byte[BUFFER_SIZE];
            int i = -1;
            while ((i = fileInputStream.read(arrayOfByte)) > 0) {
                fileOutputStream.write(arrayOfByte, 0, i);

            }
            fileInputStream.close();
            fileOutputStream.close();
            return true;
        } catch (Exception exception) {

        }
        return false;
    }

    /***
     * 拷贝成功返回File对象，拷贝失败返回空指针
     * 
     * @param inputStream
     * @param dstFilePath
     * @param forceOverride
     * @return
     * @throws IOException
     */
    public static File copyFile(InputStream inputStream, String dstFilePath, boolean forceOverride) throws IOException {
        if ((inputStream == null) || (dstFilePath == null)) {
            return null;
        }
        File dstFile = new File(dstFilePath);
        if ((!forceOverride) && (dstFile.exists())) {
            return null;
        }
        if ((!dstFile.exists()) && (!dstFile.createNewFile()))
            return null;


        byte[] arrayOfByte = new byte[BUFFER_SIZE];
        FileOutputStream ouputStream = new FileOutputStream(dstFile);
        int i = -1;
        while ((i = inputStream.read(arrayOfByte)) > 0) {
            ouputStream.write(arrayOfByte, 0, i);

        }
        inputStream.close();
        ouputStream.close();
        return dstFile;
    }

    public static boolean copyFile(byte[] srcFileBytes, String dstFilePath, boolean forced) {
        if ((srcFileBytes == null) || (dstFilePath == null))
            return false;
        try {
            File dstFile = new File(dstFilePath);
            if ((!forced) && (dstFile.exists())) {
                return false;
            }
            if ((!dstFile.exists()) && (!dstFile.createNewFile()))
                return false;
            FileOutputStream fileOutPutStream = new FileOutputStream(dstFile);
            fileOutPutStream.write(srcFileBytes);
            fileOutPutStream.close();
            return true;
        } catch (Exception exception) {

        }
        return false;
    }

    /***
     * 获取最近一次修改时间
     * 
     * @param path
     * @return
     */
    public static long getLastModified(String path) {
        File file = new File(path);
        if (file.exists()) {
            return file.lastModified();
        }
        return 0;
    }

    /***
     * @param file
     * @return 返回字符串
     * @throws IOException
     *             发生文件不存在或者读取错误的情况下
     */
    public static String readFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        byte[] buffer = new byte[is.available()];
        is.read(buffer);
        is.close();
        return new String(buffer);
    }

    public static long getDirSizeByPath(String path) {
        File dir = new File(path);
        if (dir.exists()) {
            return getDirSize(dir);
        } else {
            return 0;
        }
    }

    public static long getDirSize(File dir) {
        if (dir == null) {
            return 0;
        }
        if (!dir.isDirectory()) {
            return 0;
        }
        long dirSize = 0;
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                dirSize += file.length();
            } else if (file.isDirectory()) {
                dirSize += file.length();
                dirSize += getDirSize(file); // 如果遇到目录则通过递归调用继续统计  
            }
        }
        return dirSize;
    }

}