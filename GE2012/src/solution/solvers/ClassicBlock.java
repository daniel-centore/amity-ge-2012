package solution.solvers;

import solution.CurrentGame;
import solution.board.HexPoint;
import solution.board.Player;
import solution.board.implementations.indivBoard.IndivBoard;
import solution.board.implementations.indivBoard.IndivNode;

/**
 * 
 * Implements the classic block from http://www.hexwiki.org/index.php?title=Basic_%28strategy_guide%29
 * 
 * @author Daniel Centore
 *
 */
public class ClassicBlock
{
	private int part = 1; // which of the four parts we're on

	private IndivBoard indivBoard;
	private CurrentGame curr;
	private HexPoint initial;

	// ====
	// == These are all relative. We calc the difference from initial on the fly. ==
	// initial, move1, move2, 3, 4

	// letters, x < 6
	private HexPoint[] groupA = { new HexPoint(6, 'f'), new HexPoint(3, 'h'), new HexPoint(3, 'g'), new HexPoint(4, 'i'), new HexPoint(4, 'e') };

	// numbers, y > f
	private HexPoint[] groupB = { new HexPoint(6, 'k'), new HexPoint(7, 'h'), new HexPoint(8, 'h'), new HexPoint(5, 'i'), new HexPoint(9, 'i') };

	// numbers, y <= f
	private HexPoint[] groupC = { new HexPoint(7, 'a'), new HexPoint(6, 'd'), new HexPoint(5, 'd'), new HexPoint(8, 'c'), new HexPoint(4, 'c') };

	// letters, x >=6
	private HexPoint[] groupD = { new HexPoint(1, 'f'), new HexPoint(4, 'd'), new HexPoint(4, 'e'), new HexPoint(3, 'c'), new HexPoint(3, 'g') };

	// ====

	public ClassicBlock(IndivBoard indivBoard, CurrentGame curr)
	{
		this.indivBoard = indivBoard;
		this.curr = curr;
	}

	// return true to coninue blocking
	// return false if we're done
	public HexPoint block()
	{
		if (part == 1)
		{
			for (IndivNode node : indivBoard.getPoints())
			{
				if (node.getOccupied() == Player.YOU)
				{
					initial = new HexPoint(node.getX(), node.getY());
				}
			}

		}

		part++;
		return null;
	}

	// returns the four points we want in order
	public HexPoint[] getSpots()
	{
		return null;
	}

	public boolean shouldBlock()
	{
		return false;
		// return (part <= 4);
	}

}