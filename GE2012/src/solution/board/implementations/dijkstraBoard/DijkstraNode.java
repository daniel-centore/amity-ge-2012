package solution.board.implementations.dijkstraBoard;

import java.util.ArrayList;
import java.util.List;

import solution.board.Player;

public class DijkstraNode
{
	private List<DijkstraNode> touching = new ArrayList<DijkstraNode>();
	private int x;
	private char y;
	private Player player;
	
	// For dijkstra's algorithm
	private DijkstraNode from = null;
	private double weight = Double.MAX_VALUE;
	private boolean completed = false;
	
	public DijkstraNode(int x, char y, Player player)
	{
		this.x = x;
		this.y = y;
		
		this.player = player;
	}
	
	
	// true if we actually took the new weight
	// weight should be the total this would be
	protected boolean setNode(DijkstraNode from, double weight)
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
		weight = Double.MAX_VALUE;
		completed = false;
	}
	
	protected void addNeighbor(DijkstraNode node)
	{
		touching.add(node);
	}

	public List<DijkstraNode> getNeighbors()
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
		return "DijkstraNode [x=" + x + ", y=" + y + "]";
	}


	public boolean isCompleted()
	{
		return completed;
	}


	public void setCompleted(boolean completed)
	{
		this.completed = completed;
	}

	public DijkstraNode getFrom()
	{
		return from;
	}


	public double getWeight()
	{
		return weight;
	}


	public Player getPlayer()
	{
		return player;
	}


	public void setPlayer(Player player)
	{
		this.player = player;
	}
}
