package demo.jigsaw;

import system.LAGraphicsUtils;
import tadpole2d.game.LAGameView;
import tadpole2d.game.LAGraphics;
import tadpole2d.game.LAImage;
import tadpole2d.game.LAScreen;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;

public class JigsawScreen extends LAScreen {
    private LAImage imageBack, tmp_imageBack, imageForward;
    private LAGraphics tmp_graphics;
    /**
     * 拼图格子数
     */
    private int totalCellCount;
    /**
     * 拼图单元格宽度
     */
    private int cellWidth;
    /**
     * 拼图单元格高度
     */
    private int cellHeight;
    /**
     * 列数
     */
    private int colCount;
    /**
     * 行数
     */
    private int rowCount;
    /**
     * 屏幕宽度
     */
    private int screenWidth;

    /**
     * 屏幕高度
     */
    private int screenHeight;
    private int blocks[];
    private boolean isEvent;
    /**
     * 主图片
     */
    private String strMainImageFile;

    /**
     * 完成图片
     */
    private String strFinishImage;
    
    private JigsawCell lastClickCell = null;

    public JigsawScreen(LAGameView gameView, String mainImage, String finishImage, int rowCount, int colCount) {
        this.rowCount = rowCount;
        this.colCount = colCount;
        this.strMainImageFile = mainImage;
        this.strFinishImage = finishImage;
    }


    /**
     * 随机生成
     */
    private void rndBlocks() {
        tmp_graphics.drawImage(imageBack, 0, 0);
        for (int i = 0; i < (totalCellCount * rowCount); i++) {
            int srcX = (int) ((double) rowCount * Math.random());
            int srcY = (int) ((double) colCount * Math.random());
            int x2 = (int) ((double) rowCount * Math.random());
            int y2 = (int) ((double) colCount * Math.random());
            copy(srcX, srcY, 0, colCount);
            copy(x2, y2, srcX, srcY);
            copy(0, colCount, x2, y2);
            int j1 = blocks[srcY * rowCount + srcX];
            blocks[srcY * rowCount + srcX] = blocks[y2 * rowCount + x2];
            blocks[y2 * rowCount + x2] = j1;
        }
    }

    private void copy(int srcX, int srcY, int x2, int y2) {
        tmp_graphics.copyArea(srcX * cellWidth, srcY * cellHeight, cellWidth, cellHeight, (x2 - srcX) * cellWidth, (y2 - srcY) * cellHeight);
    }

  
    @Override
    public void onCreate(SurfaceView view) {
        this.imageBack = LAGraphicsUtils.resizeImage(getLAImage(this.strMainImageFile), view.getWidth(), view.getHeight());
        this.screenWidth = imageBack.getWidth();
        this.screenHeight = imageBack.getHeight();
        this.cellWidth = screenWidth / rowCount;
        this.cellHeight = screenHeight / colCount;
        this.tmp_imageBack = new LAImage(screenWidth, screenHeight + cellHeight);
        this.tmp_graphics = tmp_imageBack.getLAGraphics();
        this.totalCellCount = colCount * rowCount;
        this.blocks = new int[totalCellCount];
        this.imageForward = getLAImage(this.strFinishImage);
        for (int i = 0; i < totalCellCount; i++) {
            blocks[i] = i;
        }
        rndBlocks();
    }
    
    @Override
    public void onDraw(LAGraphics g) {
        if (!isEvent)//�״μ���
        {
            g.drawImage(tmp_imageBack, 0, 0);
            int i, j;
            for (i = 0; i < rowCount; i++) {
                for (j = 0; j < colCount; j++) {
                    g.drawRect(i * cellWidth, j * cellHeight, cellWidth, cellHeight);
                }
            }
        }
        if (isEvent && imageForward != null) {
            g.drawImage(imageBack, 0, 0);
            g.drawImage(imageForward, 0, 0);
            tmp_graphics.dispose();
        }
    }
    

    /**
     * 触摸事件
     */
    public boolean onTouchDown(MotionEvent e) {
        if (isEvent) {
            return isEvent;
        }
        int srcX = (int) (e.getX() / cellWidth);
        int srcY = (int) (e.getY() / cellHeight);

        if (lastClickCell != null) {
            if ((srcX == lastClickCell.x) && (srcY == lastClickCell.y)) {
                tmp_graphics.cancelHighLight(lastClickCell.x * cellWidth, lastClickCell.y * cellHeight, cellWidth, cellHeight);
            } else {
                tmp_graphics.cancelHighLight(lastClickCell.x * cellWidth, lastClickCell.y * cellHeight, cellWidth, cellHeight);
                tmp_graphics.switchPlace(srcX * cellWidth, srcY * cellHeight, lastClickCell.x * cellWidth, lastClickCell.y * cellHeight, cellWidth, cellHeight);
                int swapVar = blocks[srcY * rowCount + srcX];
                blocks[srcY * rowCount + srcX] = blocks[lastClickCell.y * rowCount + lastClickCell.x];
                blocks[lastClickCell.y * rowCount + lastClickCell.x] = swapVar;
                int index;
                for (index = 0; index < totalCellCount; index++) {
                    if (blocks[index] != index) {
                        break;
                    }
                }
                if (index == totalCellCount) {
                    isEvent = true;
                }
            }
            lastClickCell = null;
        } else {
            lastClickCell = new JigsawCell();
            lastClickCell.x = srcX;
            lastClickCell.y = srcY;
            tmp_graphics.highLight(srcX * cellWidth, srcY * cellHeight, cellWidth, cellHeight);
        }
        return isEvent;
    }

    public boolean onKeyDown(int keyCode, KeyEvent e) {
        return false;
    }

    public boolean onKeyUp(int keyCode, KeyEvent e) {
        return false;
    }

    public boolean onTouchMove(MotionEvent e) {
        return false;
    }

    public boolean onTouchUp(MotionEvent e) {
        return false;
    }
}