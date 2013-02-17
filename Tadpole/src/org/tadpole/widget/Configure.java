package org.tadpole.widget;

import org.tadpole.app.BoardPageItem;

import android.app.Activity;
import android.util.DisplayMetrics;

public class Configure {
    public static final int DRAG_PAGE_INVALID = -1;

    public static final int DRAG_POSITION_INVALID = -1;

    public static boolean isDragging = false;
    public static boolean isChangingPage = false;
    public static boolean isDelDark = false;
    public static boolean isEditMode = false;

    public static final int PAGE_SIZE = 8;

    public static BoardDataConfig<BoardPageItem> boardData;

    public static String draggingDataId = "";
    public static int draggingPage = DRAG_PAGE_INVALID;
    public static int draggingPostion = DRAG_POSITION_INVALID;

    public static int screenHeight = 0;
    public static int screenWidth = 0;
    public static float screenDensity = 0;

    public static int lastPage = 0;
    public static int currentPage = 0;


    public static int removeItem = 0;
    public static int movePageNum = 0;
    public static BoardPageItem draggingItem = null;

    public static int itemCount;

    public static void init(Activity context) {
        if (screenDensity == 0 || screenWidth == 0 || screenHeight == 0) {
            DisplayMetrics dm = new DisplayMetrics();
            context.getWindowManager().getDefaultDisplay().getMetrics(dm);
            Configure.screenDensity = dm.density;
            Configure.screenHeight = dm.heightPixels;
            Configure.screenWidth = dm.widthPixels;
        }
        currentPage = 0;
    }

    public int[] ret(int[] intArray) {
        int size = intArray.length;
        for (int i = size - 1; i >= 0; i--)
            for (int j = 0; j < i; j++)
                if (intArray[j] > intArray[j + 1]) {
                    int t = intArray[j];
                    intArray[j] = intArray[j + 1];
                    intArray[j + 1] = t;
                }
        return intArray;
    }
}