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
	public HashMap<IndivNode, HexPoint> map = new HashMap<IndivNode, HexPoint>();	// map of the points on our grid
	
	public IndivBoard()
	{
		for (int i = 1; i <= 11; i++)
		{
			for (char c = 'a'; c <= 'z'; c++)
				map.put(new IndivNode(i, c), new HexPoint(i, c));
		}
		DebugWindow.println(map.toString());
	}

	@Override
	public NodeInterface getNode(int x, char y)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void applyMove(int x, char y, Player player)
	{
		// TODO Auto-generated method stub
		
	}

}
