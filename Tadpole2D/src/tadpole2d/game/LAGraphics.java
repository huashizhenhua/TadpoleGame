package tadpole2d.game;

import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class LAGraphics {
    private Bitmap bitmap;

    private Canvas canvas;

    private Paint paint;

    private Rect clip;

    public LAGraphics(Bitmap bitmap) {
        this.bitmap = bitmap;
        this.canvas = new Canvas(bitmap);
        this.canvas.clipRect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        this.canvas.save(Canvas.CLIP_SAVE_FLAG);
        this.paint = new Paint();
        this.clip = canvas.getClipBounds();//��Ԫ�߼�ӳ������
    }

    public void drawImage(LAImage img, int x, int y) {
        canvas.drawBitmap(img.getBitmap(), x, y, paint);
    }

    public void drawImage(LAImage img, int x, int y, int w, int h, int x1, int y1, int w1, int h1) {
        canvas.drawBitmap(img.getBitmap(), new Rect(x1, y1, w1, h1), new Rect(x, y, w, h), paint);
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        if (x1 > x2) {
            x1++;
        } else {
            x2++;
        }
        if (y1 > y2) {
            y1++;
        } else {
            y2++;
        }
        canvas.drawLine(x1, y1, x2, y2, paint);
    }

    public void drawRect(int x, int y, int width, int height) {
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(x, y, x + width, y + height, paint);
    }

    public void drawString(String str, int x, int y) {
        canvas.drawText(str, x, y, paint);
    }

    public void drawClear() {
        paint.setColor(Color.BLACK);
        canvas.drawColor(Color.BLACK);
    }

    public void fillRect(int x, int y, int width, int height) {
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(x, y, x + width, y + height, paint);
    }


    public void copyArea(int srcX, int srcY, int width, int height, int dx, int dy) {
        Bitmap copy = Bitmap.createBitmap(bitmap, srcX, srcY, width, height);
        canvas.drawBitmap(copy, srcX + dx, srcY + dy, null);
    }

    public void switchPlace(int srcX, int srcY, int dstX, int dstY, int width, int height) {
        Bitmap srcCopy = Bitmap.createBitmap(bitmap, srcX, srcY, width, height);
        Bitmap dstCopy = Bitmap.createBitmap(bitmap, dstX, dstY, width, height);
        canvas.drawBitmap(srcCopy, dstX, dstY, null);
        canvas.drawBitmap(dstCopy, srcX, srcY, null);
    }

    private HashMap<String, Bitmap> highLightCache = new HashMap<String, Bitmap>();

    public void highLight(int x, int y, int width, int height) {
        GLog.d("LAGraphics", "-----------------highLight call-----------------", "");
        Bitmap copy = Bitmap.createBitmap(bitmap, x, y, width, height);
        highLightCache.put("" + x + y, copy);
        Paint greenPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        greenPaint.setStrokeWidth(1);
        greenPaint.setColor(Color.GREEN);
        //        canvas.drawLine(x, y, x + width, y, greenPaint);
        //        canvas.drawLine(x, y, x, y + height, greenPaint);
        //        canvas.drawLine(x, y + height, x + width, y + height, greenPaint);
        //        canvas.drawLine(x + width, y, x + width, y + height, greenPaint);
        canvas.drawLine(x, y, x + width, y + height, greenPaint);
    }

    public void cancelHighLight(int x, int y, int width, int height) {
        String cacheKey = "" + x + y;
        Bitmap copy = highLightCache.get(cacheKey);
        if (copy != null) {
            canvas.drawBitmap(copy, x, y, null);
        }
    }


    public void clipRect(int x, int y, int width, int height) {
        canvas.clipRect(x, y, x + width, y + height);
        clip = canvas.getClipBounds();
    }

    public void setClip(int x, int y, int width, int height) {
        if (x == clip.left && x + width == clip.right && y == clip.top && y + height == clip.bottom) {
            return;
        }
        if (x < clip.left || x + width > clip.right || y < clip.top || y + height > clip.bottom) {
            canvas.restore();
            canvas.save(Canvas.CLIP_SAVE_FLAG);
        }
        clip.left = x;
        clip.top = y;
        clip.right = x + width;
        clip.bottom = y + height;
        canvas.clipRect(clip);
    }

    //������Բ
    public void drawOval(int x, int y, int width, int height) {
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawOval(new RectF(x, y, x + width, y + height), paint);
    }

    //���ƶ����
    public void drawPolygon(int[] xpoints, int[] ypoints, int npoints) {
        canvas.drawLine(xpoints[npoints - 1], ypoints[npoints - 1], xpoints[0], ypoints[0], paint);
        int i;
        for (i = 0; i < npoints - 1; i++) {
            canvas.drawLine(xpoints[i], ypoints[i], xpoints[i + 1], ypoints[i + 1], paint);
        }
    }

    public void dispose() {
        paint = null;
        canvas = null;
    }

    //24-31 λ��ʾ 0xff��16-23 λ��ʾ��ɫ��8-15 λ��ʾ��ɫ��0-7 λ��ʾ6ɫ
    public void clearRect(int x, int y, int width, int height) {
        canvas.clipRect(x, y, x + width, y + height);
        canvas.drawARGB(0xff, 0xff, 0xff, 0xff);
    }

    public int getClipHeight() {
        return clip.bottom - clip.top;
    }

    public int getClipWidth() {
        return clip.right - clip.left;
    }

    public int getClipX() {
        return clip.left;
    }

    public int getClipY() {
        return clip.top;
    }

    public int getColor() {
        return paint.getColor();
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public Paint getPaint() {
        return paint;
    }

    public Rect getClip() {
        return clip;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setAntiAlias(boolean flag) {
        paint.setAntiAlias(flag);
    }

    public void setAlphaValue(int alpha) {
        paint.setAlpha(alpha);
    }

    //��ɫ͸���
    public void setAlpha(float alpha) {
        setAlphaValue((int) (255 * alpha));
    }

    /**
     * ��ָ������� RGB ֵ����һ�ֲ�͸��� sRGB ��ɫ���� sRGB ֵ�� 16-23 λ��ʾ��ɫ����
     * 8-15 λ��ʾ��ɫ����0-7 λ��ʾ6ɫ��������ʱʵ��ʹ�õ���ɫȡ���ڴӸ��Ŀ�����
     * �ض�����豸����ɫ�ռ����ҵ�����ƥ�����ɫ��alpha ֵ��Ĭ��ֵΪ 255
     */
    public void setColor(int rgb) {
        paint.setColor(rgb);
    }



}