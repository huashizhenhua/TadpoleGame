package org.tadpole.util;

import java.util.ArrayList;

/**
 * util that offer some methods for operating list
 * 
 * <br>==========================
 * <br> author：Zenip
 * <br> email：lxyczh@gmail.com
 * <br> create：2013-1-13上午10:32:25
 * <br>==========================
 */
public class ListUtil {

    /**
     * swap list item。
     * 
     * 
     * @param fromList
     * @param toList
     * @param from
     *            "from" item position
     * @param to
     *            "to" item position
     */
    public static void swapListItem(ArrayList<Object> fromList, ArrayList<Object> toList, int from, int to) {
        Object fromObj = fromList.get(from);
        Object toObj = toList.get(to);

        toList.add(to, fromObj);
        toList.remove(to + 1);

        fromList.add(from, toObj);
        fromList.remove(from + 1);
    }

}
