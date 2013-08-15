package com.example.voiceinput;

/**
 * BaseMode use {@link Object#hashCode} to unique a Data Item
 * which type is  T.
 */

public class BaseModel<T> {

    public static final int TIME_DELAY_SAVE = 2000;

    public BaseModel(){
    }

    // -------- 文件处理 ----------
    /**
     * /data/data/{packageName}/files/voiceemoticon/{platform}{uid}
     */
    private String mSavePath;

    private InputStream getInputStream() {
        ensureFileExist(mSavePath);
        return new FileInputStream(mSavePath);
    }

    private OutputStream getOutputStream() {
        ensureFileExist(mSavePath); 
        return new FileOutputStream(mSavePath);
    }

    
    private ArrayList<T> mList = new ArrayList<T>();

    public ArrayList<T> getAll() {
        if (mList.isEmtpy()) {
            loadModelData();
        }
    	return mList;
    }

    private void loadModelData() {
        mList.clear();
        String content = FileUtils.read(getInputStream());
        JSONArray jsonArr = new JSONArray(content);
        JSONObject jsonObj = null;
        T obj;
        for(int i = 0, len = jsonArr.length(); i < len; i ++) {
             jsonObj = jsonArr.get(i);
             obj = T.newInstance();
             JSONUtil.copyProperties();
             mList.add(obj);
        }
    }


    public void saveAll(ArrayList<T> list) {
        JSONArray jsonArr = new JSONArray();
        JSONObject jsonObj = null;
        for(T t : list) {
            jsonObj = JSONUtil.toJSONString(t);
            jsonArr.addJsonObj(jsonObj);
        }

        OuputStream out = null;
        out = getOutputStream();
        File.write(jsonArr.toJSONString(), out);

        finally(){
            close
        }
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());


    private Runnable mSaveRunnable = new Runnable() {
        public void run() {
            saveAll(mHandler);
        }
    }

    public void add(T obj) {
        mList.add(obj);
        mHandler.postDelayed(mSaveRunnable, TIME_DELAY_SAVE);
    }

    public void delete(T obj) {
        if (null == obj) {
            return;
        }

        ArrayList<T> list = getAll();

        T objToDel = null;

        for (T t: list) {
            if (null == t) {
                continue;
            }

            if(t.hashCode() == obj.hashCode()) {
                objToDel = t;
            }
        }

        if (null != objToDel) {
            list.remove(objToDel);
        }

    }

    private static void ensureFileExist(String path) throws IOException {
        if (StringUtils.isEmpty(path)) {
            throw new IllegalArgumentException("ensureFileExist path can't not be null");
        }

        File file = new File(path);

        boolean exist = file.exists();
        boolean notDirectory = !file.isDirectory();

        if (exist) {
            if (notDirectory) {
                return;
            } else {
                file.delete();
            }
        }

        file.createNewFile();
    }


     // ---------------- Util Method ----------------

    private static void close(Closeable obj) {
        if (null == obj) {
            return;
        }
        try {
            obj.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void ensureFileExist(String path) throws IOException {
        if (StringUtils.isEmpty(path)) {
            throw new IllegalArgumentException("ensureFileExist path can't not be null");
        }

        File file = new File(path);

        boolean exist = file.exists();
        boolean notDirectory = !file.isDirectory();

        if (exist) {
            if (notDirectory) {
                return;
            } else {
                file.delete();
            }
        }

        file.createNewFile();
    }

}
