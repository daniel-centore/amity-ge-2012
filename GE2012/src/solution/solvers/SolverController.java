package solution.solvers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import solution.CurrentGame;
import solution.board.HexPoint;
import solution.board.Player;
import solution.board.implementations.indivBoard.IndivBoard;
import solution.board.implementations.indivBoard.IndivNode;
import solution.debug.DebugWindow;

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

		// TODO: if we have chains across, start to fill them in

		// return new HexPoint(6, 'g');
	}

	private HexPoint followChain()
	{
		List<HexPoint> possible = new ArrayList<HexPoint>();

		for (IndivNode node : indivBoard.getPoints())
		{
			if (node.getOccupied() == Player.ME)
			{
				List<HexPoint> chains = node.getTwoChains();// indivBoard);
				for (HexPoint pnt : chains)
				{
					if (indivBoard.getNode(pnt).getOccupied() == Player.EMPTY && IndivNode.empty(pnt.connections(node.getPoints().get(0)), indivBoard))
					{
						possible.add(pnt);
					}
				}
			}
		}

		Iterator<HexPoint> itr = possible.iterator();

		int left = Integer.MAX_VALUE;
		HexPoint bestLeft = null;

		int right = Integer.MAX_VALUE;
		HexPoint bestRight = null;

		do
		{
			HexPoint h = itr.next();
			
			if (curr.getConnectRoute() == CurrentGame.CONNECT_LETTERS)
			{
				System.out.println("letters");
			}
			else
				System.out.println("nums");

			if (curr.getConnectRoute() == CurrentGame.CONNECT_LETTERS)
			{
				if (h.getY() < 'f')
				{
					int dist = calculateDistance(h);
					if (dist < left)
					{
						bestLeft = h;
						left = dist;
					}
				}
				else
				{
					int dist = calculateDistance(h);
					if (dist < right)
					{
						bestRight = h;
						right = dist;
					}
				}
			}
			else
			{
				if (h.getX() < 5)
				{
					int dist = calculateDistance(h);
					if (dist < left)
					{
						bestLeft = h;
						left = dist;
					}
				}
				else
				{
					int dist = calculateDistance(h);
					if (dist < right)
					{
						bestRight = h;
						right = dist;
					}
				}
			}

		} while (itr.hasNext());
		
		if (left > right && bestLeft != null)
			return bestLeft;
		else
			return bestRight;

	}
	
	

	private int calculateDistance(HexPoint pnt)
	{
		if (curr.getConnectRoute() == CurrentGame.CONNECT_LETTERS)
		{
			
			int i = pnt.getY() - 'a';
			int j = 'k' - pnt.getY();
			
			DebugWindow.println(i+" "+j);

			return Math.min(i, j);
		}
		else
		{
			int i = pnt.getX() - 1;
			int j = 11 - pnt.getX();

			return Math.min(i, j);
		}

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
				List<HexPoint> chains = node.getTwoChains();// indivBoard);
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
