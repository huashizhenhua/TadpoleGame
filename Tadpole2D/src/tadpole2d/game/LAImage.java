package tadpole2d.game;

import android.graphics.Bitmap;

/**
 * 图片
 * @author Administrator
 *
 */
public class LAImage
{
	private Bitmap bitmap;
	
	private int width,height;

	public LAImage(int width,int height)
	{
		this.width = width;
		this.height = height;
		this.bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);	
	}

	public LAImage(Bitmap bitmap)
	{
		this.width = bitmap.getWidth();
		this.height = bitmap.getHeight();
		this.bitmap = bitmap;
	}

	public LAGraphics getLAGraphics()
	{
		return new LAGraphics(bitmap);
	}
	
	public Bitmap getBitmap()
	{
		return bitmap;
	}

	public int getWidth()
	{
		return width;
	}	

	public int getHeight()
	{
		return height;
	}
	
	public int[] getPixels()
	{
		int pixels[] = new int[width * height];
		bitmap.getPixels(pixels,0,width,0,0,width,height);
		return pixels;
	}
}