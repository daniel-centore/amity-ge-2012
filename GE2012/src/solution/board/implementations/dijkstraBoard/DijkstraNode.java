package solution.board.implementations.dijkstraBoard;

import java.util.ArrayList;
import java.util.List;

import solution.board.Player;

/**
 * A node used in Dijkstra's algorithm
 * 
 * @author Daniel Centore
 *
 */
public class DijkstraNode
{
	private List<DijkstraNode> touching = new ArrayList<DijkstraNode>(); // All the nodes touching this one
	
	// Our position and occupier
	private int x;
	private char y;
	private Player player;
	
	// Info for Dijkstra's
	private DijkstraNode from = null;
	private double weight = Double.MAX_VALUE;
	private boolean completed = false;
	
	/**
	 * Create a node for Dijkstra's algorithm
	 * @param x The X location
	 * @param y The Y location
	 * @param player The occupying {@link Player} (with some quirks for bridges)
	 */
	protected DijkstraNode(int x, char y, Player player)
	{
		this.x = x;
		this.y = y;
		
		this.player = player;
	}
	
	
	/**
	 * Tries to set a new weight for this node
	 * @param from Where the path is coming from (used for debug)
	 * @param weight The new weight to add
	 * @return True if we applied it (it is lower than the current); False otherwise
	 */
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
	
	/**
	 * Resets the node so we can do another round of difficulty finding
	 */
	protected void resetNode()
	{
		from = null;
		weight = Double.MAX_VALUE;
		completed = false;
	}
	
	/**
	 * Marks a node as a neighbor of this one
	 * @param node The {@link DijkstraNode} to add
	 */
	protected void addNeighbor(DijkstraNode node)
	{
		touching.add(node);
	}

	/**
	 * Gets all neighbors of this node (including walls)
	 * @return The {@link List} of neighbors
	 */
	protected List<DijkstraNode> getNeighbors()
	{
		return touching;
	}

	/**
	 * Gets the X value of the node
	 * @return The X value
	 * @throws RuntimeException If you try to get the value of a wall
	 */
	protected int getX()
	{
		if (x < 0)
			throw new RuntimeException("DONT GET THE VALUE OF A WALL!!");
		
		return x;
	}

	/**
	 * Gets the Y value of the node
	 * @return The Y value
	 * @throws RuntimeException If you try to get the value of a wall
	 */
	protected char getY()
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

	/**
	 * Are we done with this node in Dijkstra's?
	 * @return True if we are; False otherwise
	 */
	protected boolean isCompleted()
	{
		return completed;
	}

	/**
	 * Marks a node as having been traversed or not
	 * @param completed True to mark it so; False otherwise
	 */
	protected void setCompleted(boolean completed)
	{
		this.completed = completed;
	}

	/**
	 * Gets the node which led to this one on the path (or null for initial)
	 * @return The {@link DijkstraNode} that led to this one
	 */
	protected DijkstraNode getFrom()
	{
		return from;
	}

	/**
	 * Gets the weight that this node was assiged
	 * @return The weight that the path to this node is
	 */
	protected double getWeight()
	{
		return weight;
	}

	/**
	 * Gets the {@link Player} currently occupying this space
	 * @return The {@link Player}
	 */
	protected Player getPlayer()
	{
		return player;
	}

	/**
	 * Sets the {@link Player} currently occupying this space
	 * @param player The {@link Player} to set it to
	 */
	protected void setPlayer(Player player)
	{
		this.player = player;
	}
}
