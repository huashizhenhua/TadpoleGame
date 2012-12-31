package tadpole2d.game;

import java.util.Random;

import android.app.Activity;
import android.view.View;

public class LASystem
{
	public static final Random random = new Random();

	public static final String encoding = "UTF-8";

	public static final int DEFAULT_MAX_FPS = 100;

	private static LAHandler handler;	

	public static void setSystemHandler(Activity activity,View view)
	{
		handler = new LAHandler(activity,view);
	}

	public static LAHandler getSystemHandler()
	{
		return handler;
	}

	/**
	 * 调用系统回收
	 */
	public static final void gc()
	{
		System.gc();
	}

	//��ָ������ʹ��gc����ϵͳ��Դ
	public static final void gc(final long rand)
	{
		gc(100,rand);
	}

	//��ָ����Χ�ڵ�ָ������ִ��gc
	public static final void gc(final int size,final long rand)
	{
		if(rand > size)
		{
			throw new RuntimeException(("GC random probability "+ rand + " > " + size).intern());
		}
		if(LASystem.random.nextInt(size) <= rand)
		{
			LASystem.gc();
		}
	}


}