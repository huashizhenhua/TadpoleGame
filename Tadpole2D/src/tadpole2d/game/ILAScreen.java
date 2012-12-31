package tadpole2d.game;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;

public interface ILAScreen extends OnTouchListener,OnKeyListener,OnClickListener,OnFocusChangeListener,OnCreateContextMenuListener,OnLongClickListener
{
	public abstract boolean onTouchDown(MotionEvent e);

	public abstract boolean onTouchUp(MotionEvent e);

	public abstract boolean onTouchMove(MotionEvent e);

	public abstract boolean onKeyDown(int keyCode,KeyEvent e);

	public abstract boolean onKeyUp(int keyCode,KeyEvent e);

	public abstract void setScreen(ILAScreen screen);

	public abstract void createUI(LAGraphics g);

	public abstract void runTimer(LTimerContext timer);
	
	public abstract void onCreate(SurfaceView view);
}