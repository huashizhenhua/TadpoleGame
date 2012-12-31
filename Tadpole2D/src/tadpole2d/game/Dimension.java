package tadpole2d.game;

//�����
public class Dimension
{
	private int x,y,width,height;

	public Dimension(int x,int y,int w,int h)
	{
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
	}

	public int getX()
	{
		return x;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public int getY()
	{
		return y;
	}

	public void setY(int y)
	{
		this.y = y;
	}

	public int getWidth()
	{
		return width;
	}

	public void setWidth(int w)
	{
		this.width = w;
	}

	public int getHeight()
	{
		return height;
	}

	public void setHeight(int h)
	{
		this.height = h;
	}

}