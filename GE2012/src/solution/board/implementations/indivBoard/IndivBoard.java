package solution.board.implementations.indivBoard;

import java.util.Collection;
import java.util.HashMap;

import solution.board.BoardInterface;
import solution.board.HexPoint;
import solution.board.NodeInterface;
import solution.board.Player;

/**
 * This is a board where every single spot has a representation
 * 
 * @author Daniel Centore
 *
 */
public class IndivBoard implements BoardInterface
{
	public HashMap<HexPoint, IndivNode> map = new HashMap<HexPoint, IndivNode>();	// map of the points on our grid
	
	/**
	 * Creates a new HexPoint for all points on a regular 11x11 board
	 */
	public IndivBoard()
	{
		for (int i = 1; i <= 11; i++)
		{
			for (char c = 'a'; c <= 'k'; c++)
				map.put(new HexPoint(i, c), new IndivNode(i, c));
		}
	}
	
	/**
	 * Gets the {@link IndivNode} at a certain position
	 * @param point A {@link HexPoint} representing the position
	 * @return The {@link IndivNode} at the position
	 */
	public IndivNode getNode(HexPoint point)
	{
		return (IndivNode) getNode(point.getX(), point.getY());
	}
	
	/**
	 * Gets a {@link Collection} of all the {@link IndivNode}s in the board
	 * @return The {@link Collection}
	 */
	public Collection<IndivNode> getPoints()
	{
		return map.values();
	}

	@Override
	public NodeInterface getNode(int x, char y)
	{
		return map.get(new HexPoint(x, y));
	}

	@Override
	public void applyMove(int x, char y, Player player)
	{
		map.get(new HexPoint(x, y)).setOccupied(player);
	}

	@Override
	public String toString()
	{
		return "IndivBoard [map=" + map + "]";
	}

}
