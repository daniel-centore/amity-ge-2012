package solution.solvers;

import java.util.List;

import solution.CurrentGame;
import solution.board.HexPoint;
import solution.board.Player;
import solution.board.implementations.indivBoard.IndivBoard;
import solution.board.implementations.indivBoard.IndivNode;
import solution.debug.DebugWindow;

/**
 * Implements the classic block from http://www.hexwiki.org/index.php?title=Basic_%28strategy_guide%29
 * 
 * @author Daniel Centore
 *
 */
public class ClassicBlock
{
	private int part = 1; // which of the four parts we're on

	private IndivBoard indivBoard; // The board we are on
	private CurrentGame curr; // The Current Game
	private HexPoint initial; // The initial move made by the enemy
	private HexPoint[] blockPoints = new HexPoint[4]; // The pattern we will be using based on situation

	// Move lists for different starting situations
	// These are all relative. We calculate our actual move list on the fly based on these templates
	// Format: initial, move1, move2, 3, 4
	private HexPoint[] groupA = { new HexPoint(6, 'f'), new HexPoint(3, 'h'), new HexPoint(3, 'g'), new HexPoint(4, 'i'), new HexPoint(4, 'e') };
	private HexPoint[] groupB = { new HexPoint(6, 'k'), new HexPoint(7, 'h'), new HexPoint(8, 'h'), new HexPoint(5, 'i'), new HexPoint(9, 'i') };
	private HexPoint[] groupC = { new HexPoint(7, 'a'), new HexPoint(6, 'd'), new HexPoint(5, 'd'), new HexPoint(8, 'c'), new HexPoint(4, 'c') };
	private HexPoint[] groupD = { new HexPoint(1, 'f'), new HexPoint(4, 'd'), new HexPoint(4, 'e'), new HexPoint(3, 'c'), new HexPoint(3, 'g') };

	/**
	 * Creates a controller for making a classic block (as an initial defence)
	 * @param indivBoard The {@link IndivBoard} being used
	 * @param curr The current game
	 */
	public ClassicBlock(IndivBoard indivBoard, CurrentGame curr)
	{
		this.indivBoard = indivBoard;
		this.curr = curr;
	}

	/**
	 * Finds the next move to put down
	 * @param lastMove The move the last player took
	 * @return The {@link HexPoint} to apply (or null to skip to our next algorithm)
	 */
	public HexPoint block(HexPoint lastMove)
	{
		// Intialize stuff if we're just starting
		if (part == 1)
		{
			initialize();
		}

		HexPoint pt = null;

		if (part == 1)
		{
			// Do our first move if they just put down an initial move
			pt = blockPoints[part - 1];
		}
		else
		{
			// Decides which move to put down next and whether or not they are appropriate using black magic and voodoo
			for (int i = 1; i < blockPoints.length; i++)
			{
				if (blockPoints[i] != null && blockPoints[i].isGood() && blockPoints[i].touching().contains(lastMove))
				{
					boolean good = true;
					for (HexPoint p : indivBoard.getNode(blockPoints[i]).getTwoChains())
					{
						if (indivBoard.getNode(p).getOccupied() == Player.ME)
						{
							List<HexPoint> conns = p.connections(blockPoints[i]);
							DebugWindow.println(conns.toString());
							if (indivBoard.getNode(conns.get(0)).getOccupied() == Player.YOU)
							{
								DebugWindow.println("A");
								blockPoints[i] = null;
								pt = conns.get(1);
								good = false;
							}
							else if (indivBoard.getNode(conns.get(1)).getOccupied() == Player.YOU)
							{
								DebugWindow.println("B");
								blockPoints[i] = null;
								pt = conns.get(0);
								good = false;
							}
						}
					}

					if (good)
					{
						DebugWindow.println("C" + i);
						pt = blockPoints[i];
					}
				}
			}

			if (pt == null)
			{
				DebugWindow.println("D");
				pt = blockPoints[part - 1];
			}
		}

		// Decides when it's time to really give up on the classic block or put in one last effort
		while (pt == null || !pt.isGood() || indivBoard.getNode(pt).getOccupied() != Player.EMPTY)
		{
			part++;

			if (part == 3 && blockPoints[1] != null)
			{
				DebugWindow.println("E");
				part = 50;
				return null;
			}
			
			
			if (part == 4 && indivBoard.getNode(blockPoints[2]).getOccupied() == Player.YOU)
			{
				DebugWindow.println("F");
				part = 50;
				return null;
			}
			
			if (part > 4)
			{
				DebugWindow.println("G");
				part = 50;
				return null;
			}
			else
			{
				DebugWindow.println("H");
				pt = blockPoints[part - 1];
			}
		}

		if (pt == blockPoints[3] && blockPoints[1] != null)
		{
			DebugWindow.println("I");
			part = 50;
			return null;
		}
		DebugWindow.println("J");

		// Remove the point we are about to use. It will no longer be an option.
		for (int i = 0; i < blockPoints.length; i++)
		{
			if (blockPoints[i] == pt)
				blockPoints[i] = null;
		}

		part++; // Move on to the next step

		return pt;
	}

	/**
	 * Initializes our data for the first move
	 */
	private void initialize()
	{
		// Find their initial point (they should definitely have one the 1st time this gets called)
		for (IndivNode node : indivBoard.getPoints())
		{
			if (node.getOccupied() == Player.YOU)
			{
				initial = new HexPoint(node.getX(), node.getY());
			}
		}

		if (initial == null)
			throw new RuntimeException("THEY HAD NO INITIAL POINT!!! WHY ARE WE DOING A CLASSIC BLOCK!?!?");

		// Figure out which group of points makes the most sense for our current situation
		HexPoint[] group = null;

		if (curr.getConnectRoute() == CurrentGame.CONNECT_LETTERS && initial.getX() < 6)
		{
			DebugWindow.println("Group A");
			group = groupA;
		}
		else if (curr.getConnectRoute() == CurrentGame.CONNECT_NUMBERS && initial.getY() < 'f')
		{
			DebugWindow.println("Group B");
			group = groupB;
		}
		else if (curr.getConnectRoute() == CurrentGame.CONNECT_NUMBERS && initial.getY() >= 'f')
		{
			DebugWindow.println("Group C");
			group = groupC;
		}
		else if (curr.getConnectRoute() == CurrentGame.CONNECT_LETTERS && initial.getX() >= 6)
		{
			DebugWindow.println("Group D");
			group = groupD;
		}

		// No calculate the new set of points based on the differences in theirs
		for (int i = 0; i < 4; i++)
		{
			int xDiff = group[0].getX() - group[i + 1].getX();
			int yDiff = group[0].getY() - group[i + 1].getY();

			blockPoints[i] = new HexPoint(initial.getX() + xDiff, (char) (initial.getY() + yDiff));
		}
	}

	/**
	 * Finds out if we should still be using this block.
	 * Will return true iff
	 *  1. We havn't completed the block or given up or
	 *  2. We were the first player to go
	 * 
	 * @param lastMove The last move the other player made
	 * @return True if we should use it; False otherwise 
	 */
	public boolean shouldBlock()
	{
		// Some initialization (should ideally be somewhere else...)
		if (curr.getConnectRoute() < 0)
			curr.setConnectRoute(CurrentGame.CONNECT_LETTERS);

		return (part <= 4 && curr.getConnectRoute() == CurrentGame.CONNECT_LETTERS);
	}

}