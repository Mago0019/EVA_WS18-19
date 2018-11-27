package client;

public class Spielfeld 
{
	int[][] field;
	int width;
	int hight;

	public Spielfeld() 
	{
		this.field = new int[7][6];
		this.width = 7;
		this.hight = 6;
	}
	
	public Spielfeld(int width, int hight)
	{
		if (width < 16 && hight < 16)
		{
			this.field = new int[width][hight];
			this.width = width;
			this.hight = hight;
		}
		else
		{
			this.field = new int [15][15];
			this.width = 15;
			this.hight = 15;
		}
		
	}
	
	public boolean setStone(int collumn, int player)
	{
		for(int i = hight-1; i > 0 ; i-- )
		{
			if(this.field[i][collumn] == 0)
			{
				this.field[i][collumn]= player;
				return true;
			}
			
		}
		return false;
	}
}
