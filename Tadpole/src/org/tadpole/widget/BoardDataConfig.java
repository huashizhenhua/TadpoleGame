package org.tadpole.widget;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.tadpole.app.BoardPageItem;
import org.tadpole.util.IOUtil;

public class BoardDataConfig<T> {
    private ArrayList<T> mBoardItemList;
    private int mPageSize;

    public BoardDataConfig(ArrayList<T> boardItemList, int pageSize) {
        this.mBoardItemList = boardItemList;
        this.mPageSize = pageSize;
    }

    public ArrayList<T> getBoardItemList() {
        return mBoardItemList;
    }

    public int getLastPageItemCount() {
        return mBoardItemList.size() % mPageSize;
    }

    public void moveFromTo(int from, int to) {
        T obj = mBoardItemList.remove(from);
        mBoardItemList.add(to, obj);
    }

    public int getPageCount() {
        if (getLastPageItemCount() == 0) {
            return mBoardItemList.size() / mPageSize;
        }
        return (mBoardItemList.size() / mPageSize) + 1;
    }

    public List<T> getPageItemList(int page) {
        return getPageItemListMap().get(page);
    }

    public HashMap<Integer, List<T>> getPageItemListMap() {
        int lastPageItemCount = getLastPageItemCount();
        int pageCount = getPageCount();
        HashMap<Integer, List<T>> map = new HashMap<Integer, List<T>>();
        for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
            List<T> subItemList = null;
            if (pageIndex != pageCount - 1 || lastPageItemCount == 0) {
                subItemList = (List<T>) mBoardItemList.subList(pageIndex * mPageSize, (pageIndex + 1) * mPageSize);
            } else {
                subItemList = (List<T>) mBoardItemList.subList(pageIndex * mPageSize, pageIndex * mPageSize + lastPageItemCount);
            }
            map.put(pageIndex, subItemList);
        }
        return map;
    }

    public static BoardDataConfig fromJSON(JSONArray jsonArray, int pageSize) throws JSONException {
        ArrayList<BoardPageItem> itemList = new ArrayList<BoardPageItem>();
        for (int i = 0, len = jsonArray.length(); i < len; i++) {
            JSONObject itemObj = jsonArray.getJSONObject(i);
            itemList.add(BoardPageItem.fromJSONObject(itemObj));
        }
        return new BoardDataConfig<BoardPageItem>(itemList, pageSize);
    }

    public void writeSerializable(String path) {
        FileOutputStream fo = null;
        ObjectOutputStream oo = null;
        try {
            fo = new FileOutputStream(path);
            oo = new ObjectOutputStream(fo);
            oo.writeObject(mBoardItemList);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.close(fo);
            IOUtil.close(oo);
        }
    }

    public static BoardDataConfig readSerializable(String path) {
        FileInputStream fi = null;
        ObjectInputStream oi = null;
        ArrayList mBoardItemList = null;
        BoardDataConfig boardData = null;
        try {
            fi = new FileInputStream(path);
            oi = new ObjectInputStream(fi);
            mBoardItemList = (ArrayList) oi.readObject();
            boardData = new BoardDataConfig(mBoardItemList, 8);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            IOUtil.close(fi);
            IOUtil.close(oi);
        }
        return boardData;
    }

    public static void main(String[] args) {
        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < 20; i++) {
            list.add("wwww" + i);
        }
        BoardDataConfig<String> config = new BoardDataConfig<String>(list, 8);

        list.remove(0);
        config.writeSerializable("c:\\1.txt");
        config = BoardDataConfig.readSerializable("c:\\1.txt");
        System.out.println("getPageCount = " + config.getPageCount());
    }
}
