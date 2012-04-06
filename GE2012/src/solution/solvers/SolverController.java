package solution.solvers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import solution.CurrentGame;
import solution.board.HexPoint;
import solution.board.Player;
import solution.board.implementations.indivBoard.IndivBoard;
import solution.board.implementations.indivBoard.IndivNode;

/**
 * This controls all our solvers and generates the master weight table
 * 
 * @author Daniel Centore
 * @author Mike
 *
 */
public class SolverController
{
	private CurrentGame curr; // Current game
	private HexPoint initial = null; // our centerpiece
	private IndivBoard indivBoard;

	public SolverController(CurrentGame curr)
	{
		this.curr = curr;
		indivBoard = curr.getBoardController().getIndivBoard();
	}

	/**
	 * Chooses our next move
	 * @return
	 */
	public HexPoint getMove()
	{
		if (initial == null)
			throw new RuntimeException("Should have been set already...");

		// Fix chain if necessary
		HexPoint broken = twoChainsBroken();
		if (broken != null)
			return broken;

		// follow chain down board
		return followChain();

		// return new HexPoint(6, 'g');
	}

	private HexPoint followChain()
	{
		for (IndivNode node : indivBoard.getPoints())
		{
			if (node.getOccupied() == Player.ME)
			{
				List<HexPoint> chains = node.getTwoChains();
				for (HexPoint pnt : chains)
				{
					if (indivBoard.getNode(pnt).getOccupied() == Player.EMPTY)
					{
						return pnt;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Checks if a two chain has been broken. If so, fixes it.
	 * @return
	 */
	private HexPoint twoChainsBroken()
	{
		for (IndivNode node : indivBoard.getPoints())
		{
			if (node.getOccupied() == Player.ME)
			{
				List<HexPoint> chains = node.getTwoChains();
				for (HexPoint pnt : chains)
				{
					if (indivBoard.getNode(pnt).getOccupied() == Player.ME)
					{
						List<HexPoint> connections = pnt.connections(node.getPoints().get(0));

						if (connections.size() < 2)
							throw new RuntimeException("Size less than 2!");

						HexPoint a = connections.get(0);
						HexPoint b = connections.get(1);

						// if either connector is broken, then cling onto the other
						if (indivBoard.getNode(a).getOccupied() == Player.YOU && indivBoard.getNode(b).getOccupied() == Player.EMPTY)
							return b;
						else if (indivBoard.getNode(b).getOccupied() == Player.YOU && indivBoard.getNode(a).getOccupied() == Player.EMPTY)
							return a;
					}
				}
			}
		}

		return null; // nothing broken
	}

	public HexPoint getInitial()
	{
		return initial;
	}

	public void setInitial(HexPoint initial)
	{
		this.initial = initial;
	}

}
