package solution.solvers;

import java.util.Arrays;
import java.util.List;

import solution.CurrentGame;
import solution.board.HexPoint;
import solution.board.Player;
import solution.board.implementations.indivBoard.IndivBoard;
import solution.board.implementations.indivBoard.IndivNode;
import solution.debug.DebugWindow;

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
	private HexPoint[] blockPoints = new HexPoint[4]; // where we put the block pieces

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

	private SolverController solverController;

	public ClassicBlock(IndivBoard indivBoard, CurrentGame curr, SolverController solverController)
	{
		this.indivBoard = indivBoard;
		this.curr = curr;
		this.solverController = solverController;
	}

	// return true to coninue blocking
	// return false if we're done
	public HexPoint block(HexPoint lastMove)
	{
		if (part == 1)
		{
			initialize();
		}

		HexPoint pt = null;

		if (part == 1)
		{
			pt = blockPoints[part - 1];
		}
		else
		{
			for (int i = 1; i < blockPoints.length; i++)
			{
				if (blockPoints[i] != null && blockPoints[i].touching().contains(lastMove))
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
								blockPoints[i] = null;
								pt = conns.get(1);
								good = false;
							}
							else if (indivBoard.getNode(conns.get(1)).getOccupied() == Player.YOU)
							{
								blockPoints[i] = null;
								pt = conns.get(0);
								good = false;
							}
						}
					}

					if (good)
						pt = blockPoints[i];
				}
			}

			if (pt == null)
				pt = blockPoints[part - 1];
		}

		if (pt == null || !pt.isGood() || indivBoard.getNode(pt).getOccupied() != Player.EMPTY)
		{
			part++;

			if (part > 4)
				return null;
			else
			{
				pt = blockPoints[part - 1];
			}
		}

		// == cleanup

		// remove the point we used
		// it's not an option anymore
		for (int i = 0; i < blockPoints.length; i++)
		{
			if (blockPoints[i] == pt)
				blockPoints[i] = null;
		}

		if (part == 1)
			solverController.setInitial(pt);
		part++;

		return pt;
	}

	private void initialize()
	{
		if (curr.getConnectRoute() < 0)
			curr.setConnectRoute(CurrentGame.CONNECT_LETTERS);

		// find their initial point (they should definitely have one the 1st time this gets called)
		for (IndivNode node : indivBoard.getPoints())
		{
			if (node.getOccupied() == Player.YOU)
			{
				initial = new HexPoint(node.getX(), node.getY());
			}
		}

		// Calculate our differences for where we should place them based on our new initial

		HexPoint[] group = null;

		if (curr.getConnectRoute() == CurrentGame.CONNECT_LETTERS && initial.getX() < 6)
			group = groupA;

		else if (curr.getConnectRoute() == CurrentGame.CONNECT_NUMBERS && initial.getY() >= 'f')
			group = groupB;

		else if (curr.getConnectRoute() == CurrentGame.CONNECT_NUMBERS && initial.getY() < 'f')
			group = groupC;

		else if (curr.getConnectRoute() == CurrentGame.CONNECT_LETTERS && initial.getX() >= 6)
			group = groupD;

		for (int i = 0; i < 4; i++)
		{
			int xDiff = group[0].getX() - group[i + 1].getX();
			int yDiff = group[0].getY() - group[i + 1].getY();

			blockPoints[i] = new HexPoint(initial.getX() + xDiff, (char) (initial.getY() + yDiff));
		}

		DebugWindow.println(initial.toString() + " " + Arrays.toString(blockPoints));
	}

	// returns the four points we want in order
	public HexPoint[] getSpots()
	{
		return null;
	}

	public boolean shouldBlock()
	{
		// return false;
		return (part <= 4);
	}

}