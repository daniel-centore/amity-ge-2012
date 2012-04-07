package solution.board.implementations.dijkstraBoard;

import java.util.ArrayList;
import java.util.List;

import solution.board.HexPoint;

public class DijkstraNode
{
	private List<DijkstraNode> touching = new ArrayList<DijkstraNode>();
	private int x;
	private char y;
	
	// For dijkstra's algorithm
	private DijkstraNode from = null;
	private int weight = Integer.MAX_VALUE;
	private boolean completed = false;
	
	public DijkstraNode(int x, char y)
	{
		this.x = x;
		this.y = y;
	}
	
	
	// true if we actually took the new weight
	// weight should be the total this would be
	protected boolean setNode(DijkstraNode from, int weight)
	{
		if (weight < this.weight)
		{
			this.weight = weight;
			this.from = from;
			
			return true;
		}
		
		return false;
	}
	
	protected void resetNode()
	{
		from = null;
		weight = Integer.MAX_VALUE;
		completed = false;
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


	public boolean isCompleted()
	{
		return completed;
	}


	public void setCompleted(boolean completed)
	{
		this.completed = completed;
	}


	public List<DijkstraNode> getTouching()
	{
		return touching;
	}


	public DijkstraNode getFrom()
	{
		return from;
	}


	public int getWeight()
	{
		return weight;
	}
}
