package tadpole2d.game;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;

public interface ILAHandler extends OnTouchListener,OnKeyListener,OnClickListener,OnFocusChangeListener,OnCreateContextMenuListener,OnLongClickListener
{
	public abstract ILAScreen getScreen();

	public abstract void setFullScreen();

	public abstract void setLandScape(boolean flag);

	public abstract void setScreen(final ILAScreen screen);

	public abstract int getWidth();

	public abstract int getHeight();

	public abstract Dimension getScreenDimension();

	public abstract View getView();

	public abstract Activity getActivity();
	
	public abstract Context getContext();

	public abstract Window getWindow();

	public abstract WindowManager getWindowManager();

}