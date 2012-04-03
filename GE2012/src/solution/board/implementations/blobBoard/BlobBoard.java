package solution.board.implementations.blobBoard;

import java.util.ArrayList;
import java.util.List;

import solution.board.BoardInterface;
import solution.board.HexPoint;
import solution.board.NodeInterface;
import solution.board.Player;
import solution.board.PointUtilities;

/**
 * A board where each group of colors is represented as a blob
 * 
 * @author Daniel Centore
 *
 */
public class BlobBoard implements BoardInterface
{
	// All the blobs of color
	private List<BlobNode> nodes = new ArrayList<BlobNode>();

	/**
	 * Generates the board
	 */
	public BlobBoard()
	{
		for (int i = 1; i <= 11; i++)
		{
			for (char c = 'a'; c <= 'k'; c++)
				nodes.add(new BlobNode(Player.EMPTY, new HexPoint(i, c)));
		}
	}

	@Override
	public NodeInterface getNode(int x, char y)
	{
		return findNode(new HexPoint(x, y));
	}

	@Override
	public void applyMove(int x, char y, Player player)
	{
		HexPoint move = new HexPoint(x, y);

		BlobNode node = findNode(move);
		node.setPlayer(player);

		// Neighbors of the new one which also have the same color
		List<BlobNode> coloredNeighbors = new ArrayList<BlobNode>();

		for (HexPoint pt : PointUtilities.getNeighbors(move))
		{
			BlobNode nd = findNode(pt);
			
			if (nd != null && nd.getOccupied() == player && nd != node && !coloredNeighbors.contains(nd))
			{
				nodes.remove(nd);
				coloredNeighbors.add(nd);
			}
		}

		for (BlobNode sameColor : coloredNeighbors)
		{
			node.addPoints(sameColor.getPoints());
		}

	}

	/**
	 * Finds the {@link BlobNode} which contains the point
	 * @param move The {@link HexPoint} to look for
	 * @return The {@link BlobNode} (or null if we couldn't find it)
	 */
	private BlobNode findNode(HexPoint move)
	{
		for (BlobNode node : nodes)
		{
			for (HexPoint p : node.getPoints())
			{
				if (p != null && p.equals(move))
					return node;
			}
		}

		return null;
	}

}
