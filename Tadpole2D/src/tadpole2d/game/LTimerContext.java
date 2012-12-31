package tadpole2d.game;

public class LTimerContext
{
	//����,������ʱ�䣬���ߵĺ��룬
	private long millisTime,sinceLastUpdateTime,millisSleepTime,millisOverSleepTime;

	public LTimerContext()
	{
		millisTime = 0;
		sinceLastUpdateTime = 0;
	}	

	public synchronized void setMillisTime(long millisTime)
	{
		this.millisTime = millisTime;
	}

	public synchronized long getMillisTime()
	{
		return millisTime;
	}

	public synchronized void setSinceLastUpdateTime(long sinceLastUpdateTime)
	{
		this.sinceLastUpdateTime = sinceLastUpdateTime;
	}

	public synchronized long getSinceLastUpdateTime()
	{
		return sinceLastUpdateTime;
	}

	public long getMillisSleepTime()
	{
		return millisSleepTime;
	}

	public void setMillisSleepTime(long millisSleepTime)
	{
		this.millisSleepTime = millisSleepTime;
	}

	public long getMillisOverSleepTime()
	{
		return millisOverSleepTime;
	}

	public void setMillisOverSleepTime(long millisOverSleepTime)
	{
		this.millisOverSleepTime = millisOverSleepTime;
	}

}