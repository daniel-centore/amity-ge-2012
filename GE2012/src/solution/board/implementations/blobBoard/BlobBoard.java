package solution.board.implementations.blobBoard;

import java.util.ArrayList;
import java.util.List;

import solution.board.BoardInterface;
import solution.board.HexPoint;
import solution.board.NodeInterface;
import solution.board.Player;

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

	// The four colored sides of the board
	private BlobNode myEndA = new BlobNode(Player.ME, null);
	private BlobNode myEndB = new BlobNode(Player.ME, null);
	private BlobNode youEndA = new BlobNode(Player.YOU, null);
	private BlobNode youEndB = new BlobNode(Player.YOU, null);

	/**
	 * Generates the board
	 */
	public BlobBoard()
	{
		nodes.add(myEndA);
		nodes.add(myEndB);
		nodes.add(youEndA);
		nodes.add(youEndB);

		for (int i = 1; i <= 11; i++)
		{
			for (char c = 'a'; c <= 'k'; c++)
				nodes.add(new BlobNode(Player.EMPTY, new HexPoint(i, c)));
		}
	}

	@Override
	public NodeInterface getNode(int x, char y)
	{
		return null;
	}

	@Override
	public void applyMove(int x, char y, Player player)
	{
		HexPoint move = new HexPoint(x, y);
		
	}

}
