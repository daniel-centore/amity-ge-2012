package solution.board.implementations.dijkstraBoard;

import java.util.ArrayList;
import java.util.List;

import solution.board.HexPoint;

public class DijkstraNode
{
	private List<DijkstraNode> touching = new ArrayList<DijkstraNode>();
	private int x;
	private char y;
	
	public DijkstraNode(int x, char y)
	{
		this.x = x;
		this.y = y;
	}
	
	protected void addNeighbor(DijkstraNode node)
	{
		touching.add(node);
	}

	public List<DijkstraNode> getBridges()
	{
		return touching;
	}

	public int getX()
	{
		if (x < 0)
			throw new RuntimeException("DONT GET THE VALUE OF A WALL!!");
		
		return x;
	}

	public char getY()
	{
		if (x < 0)
			throw new RuntimeException("DONT GET THE VALUE OF A WALL!!");
		
		return y;
	}

	@Override
	public String toString()
	{
		return "DijkstraNode [bridges=" + touching + ", x=" + x + ", y=" + y + "]";
	}
}
