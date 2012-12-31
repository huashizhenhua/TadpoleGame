package tadpole2d.game;

import java.io.Serializable;

//ʱ���������
public class LTimer implements Serializable
{
	private static final long seriaVersionUID = 1L;

	private long delay;	

	private boolean active = true;

	private long currentTick;
	
	public LTimer()
	{
		this(150);
	}

	public LTimer(int delay)
	{
		this.delay = delay;
	}

	public boolean action(long elapsedTime)
	{
		if(this.active)
		{
			this.currentTick += elapsedTime;
			if(this.currentTick >= this.delay)
			{
				this.currentTick -= this.delay;
				return true;
			}
		}
		return false;
	}

	//����
	public void refresh()
	{
		this.currentTick = 0;
	}

	public void setEquals(LTimer other)
	{
		this.active = other.active;
		this.delay = other.delay;
		this.currentTick = other.currentTick;
	}

	public boolean getActive()
	{
		return this.active;
	}

	public void setActive(boolean b)
	{
		this.active = b;
		this.refresh();
	}

	public long getDelay()
	{
		return this.delay;
	}
	
	public void setDelay(long delay)
	{
		this.delay = delay;
		this.refresh();
	}

	public long getCurrentTick()
	{
		return this.currentTick;
	}

	public void setCurrentTick(long tick)
	{
		this.currentTick = tick;
	}

}