package solution.board.implementations.dijkstraBoard;

import java.util.ArrayList;
import java.util.List;

import solution.board.HexPoint;

public class DijkstraNode
{
	private List<DijkstraNode> bridges = new ArrayList<DijkstraNode>();
	private int x;
	private char y;
	
	public DijkstraNode(int x, char y)
	{
		this.x = x;
		this.y = y;
	}
	
	public void addBridge(DijkstraNode node)
	{
		bridges.add(node);
	}

	public List<DijkstraNode> getBridges()
	{
		return bridges;
	}

	public int getX()
	{
		return x;
	}

	public char getY()
	{
		return y;
	}

	@Override
	public String toString()
	{
		return "DijkstraNode [bridges=" + bridges + ", x=" + x + ", y=" + y + "]";
	}
}
