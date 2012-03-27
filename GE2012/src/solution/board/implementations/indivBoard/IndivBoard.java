package solution.board.implementations.indivBoard;

import java.util.HashMap;

import solution.board.BoardInterface;
import solution.board.HexPoint;
import solution.board.NodeInterface;
import solution.board.Player;
import solution.debug.DebugWindow;

/**
 * This is a bard where every single spot has a representation
 * 
 * @author Daniel Centore
 *
 */
public class IndivBoard implements BoardInterface
{
	public HashMap<HexPoint, IndivNode> map = new HashMap<HexPoint, IndivNode>();	// map of the points on our grid
	
	public IndivBoard()
	{
		for (int i = 1; i <= 11; i++)
		{
			for (char c = 'a'; c <= 'k'; c++)
				map.put(new HexPoint(i, c), new IndivNode(i, c));
		}
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
