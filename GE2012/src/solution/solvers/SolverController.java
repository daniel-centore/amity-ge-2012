package solution.solvers;

import java.util.List;

import solution.CurrentGame;
import solution.board.HexPoint;
import solution.board.Player;
import solution.board.implementations.dijkstraBoard.DijkstraBoard;
import solution.board.implementations.indivBoard.IndivBoard;
import solution.board.implementations.indivBoard.IndivNode;
import solution.debug.DebugWindow;

/**
 * This is the main solver class which links some other solver info
 * Vocabulary: 
 * two-chain: This occurs when there are two empty points between two spaces
 *			  or between a point and the wall. When there is a two-chain,
 *			  we can guarantee a connection between the two points or a 
 *			  connection to the wall.
 * 
 * @author Daniel Centore
 * @author Mike DiBuduo
 *
 */
public class SolverController
{
	CurrentGame curr; // Current game
	IndivBoard indivBoard;
	DijkstraBoard dijkstraBoard = null;
	private ClassicBlock classicBlock;
	private HexPoint first = null;	// the first move we made (so we can calculate left and right sides correctly)
	protected MapTools mapTools = new MapTools();
	private FollowChain followChain;

	/**
	 * Creates a new {@link SolverController}
	 * @param curr The {@link CurrentGame}
	 */
	public SolverController(CurrentGame curr)
	{
		this.curr = curr;
		indivBoard = curr.getBoardController().getIndivBoard();
		classicBlock = new ClassicBlock(indivBoard, curr);
		followChain = new FollowChain(this);
	}

	/**
	 * Chooses our next move
	 * @return the next {@link HexPoint} to occupy
	 */
	public HexPoint getMove(HexPoint lastMove)
	{
		HexPoint broken = null;

		// Fix chains between points if necessary
		try
		{
			broken = mapTools.twoChainsBroken(this);
			if (broken != null)
				return broken;
		} catch (Exception e2)
		{
			DebugWindow.println("ERROR: TwoChainsBroken crashed. Take a look at the trace. Using Default solver.");
			e2.printStackTrace();
		}
		
		try
		{
			// Fix chains between a point and the wall if necessary
			broken = baseTwoChainsBroken();
			if (broken != null)
				return broken;
		} catch (Exception e)
		{
			DebugWindow.println("WARNING: BaseTwoChains crashed. This is usually normal: just a corner case.");
			// Fails on some corner cases. just ignore this.
		}

		try
		{
			// Does a classic block if necessary
			if (classicBlock.shouldBlock())
			{
				broken = classicBlock.block(lastMove);

				if (broken != null)
					return broken;
			}
		} catch (Exception e1)
		{
			DebugWindow.println("ERROR: ClassicBlock crashed. Take a look at the trace. Using Default solver.");
			e1.printStackTrace();
		}

		dijkstraBoard = new DijkstraBoard(indivBoard, curr);

		try
		{
			// grab immediate fixes
			broken = immediatePoint();
			if (broken != null)
				return broken;
		} catch (Exception e)
		{
			DebugWindow.println("ERROR: ImmediatePoint crashed. Take a look at the trace. Using Default solver.");
			e.printStackTrace();
		}

		// follow chain down board
		return followChain.followChain(this);
	}

	/**
	 * Looks around and sees if there is a point which when put down will connect us with an edge either
	 *    by bridge or direct connection
	 * @return The {@link HexPoint} if there is one; Null if not
	 */
	private HexPoint immediatePoint()
	{
		for (IndivNode node : indivBoard.getPoints())
		{
			if (node.getOccupied() == Player.ME)
			{
				for (HexPoint around : node.getPoints().get(0).touching())
				{
					if (mapTools.connectedToWall(this, around) && indivBoard.getNode(around).getOccupied() == Player.EMPTY)
					{
						boolean left = false;
						if ((curr.getConnectRoute() == CurrentGame.CONNECT_LETTERS && around.getY() < 'f') ||
								(curr.getConnectRoute() == CurrentGame.CONNECT_NUMBERS && around.getX() < 6))
							left = true;

						if (!mapTools.across(this, left))
							return around;
					}
				}
			}
		}

		return null;
	}

	/**
	 * Checks if a two-chain is broken
	 * @param a The start node
	 * @param b The end node
	 * @return True if it is BROKEN. False if all's good.
	 */
	protected boolean broken(HexPoint a, HexPoint b) 
	{
		List<HexPoint> conns = a.connections(b);

		return !IndivNode.empty(conns, indivBoard);
	}

	/**
	 * Checks if two-chains to the walls are broken
	 * @return the {@link HexPoint} needed to fix a broken two-chain between a point and the wall (or null if none are broken)
	 */
	private HexPoint baseTwoChainsBroken()
	{
		niceloop: for (IndivNode node : indivBoard.getPoints())
		{
			if (node.getOccupied() == Player.ME)
			{
				HexPoint pt = node.getPoints().get(0);

				HexPoint[] bad = { new HexPoint(11, 'b'), new HexPoint(1, 'j'), new HexPoint(2, 'k'), new HexPoint(10, 'a') };

				// Skip the corners - We can't two-chain here!
				for (HexPoint b : bad)
				{
					if (node.equals(b))
						continue niceloop;
				}

				// Basically checks the appropriate positions to see if one is broken
				// very ugly code...
				if (curr.getConnectRoute() == CurrentGame.CONNECT_LETTERS && (pt.getY() == 'b' || pt.getY() == 'j'))
				{
					if (pt.getY() == 'b')
					{
						if ((indivBoard.getNode(pt.getX(), 'a').getOccupied() == Player.YOU))
							if ((indivBoard.getNode(pt.getX() + 1, 'a').getOccupied() == Player.EMPTY))
								return new HexPoint(pt.getX() + 1, 'a');

						if ((indivBoard.getNode(pt.getX() + 1, 'a').getOccupied() == Player.YOU))
							if ((indivBoard.getNode(pt.getX(), 'a').getOccupied() == Player.EMPTY))
								return new HexPoint(pt.getX(), 'a');
					}
					else
					{
						if ((indivBoard.getNode(pt.getX(), 'k').getOccupied() == Player.YOU))
							if ((indivBoard.getNode(pt.getX() - 1, 'k').getOccupied() == Player.EMPTY))
								return new HexPoint(pt.getX() - 1, 'k');

						if ((indivBoard.getNode(pt.getX() - 1, 'k').getOccupied() == Player.YOU))
							if ((indivBoard.getNode(pt.getX(), 'k').getOccupied() == Player.EMPTY))
								return new HexPoint(pt.getX(), 'k');
					}
				}
				else if (curr.getConnectRoute() == CurrentGame.CONNECT_NUMBERS && (pt.getX() == 2 || pt.getX() == 10))
				{
					if (pt.getX() == 2)
					{
						if ((indivBoard.getNode(1, pt.getY()).getOccupied() == Player.YOU))
							if ((indivBoard.getNode(1, (char) (pt.getY() + 1)).getOccupied() == Player.EMPTY))
								return new HexPoint(1, (char) (pt.getY() + 1));

						if ((indivBoard.getNode(1, (char) (pt.getY() + 1)).getOccupied() == Player.YOU))
							if ((indivBoard.getNode(1, pt.getY()).getOccupied() == Player.EMPTY))
								return new HexPoint(1, pt.getY());
					}
					else
					{
						if ((indivBoard.getNode(11, pt.getY()).getOccupied() == Player.YOU))
							if ((indivBoard.getNode(11, (char) (pt.getY() - 1)).getOccupied() == Player.EMPTY))
								return new HexPoint(11, (char) (pt.getY() - 1));

						if ((indivBoard.getNode(11, (char) (pt.getY() - 1)).getOccupied() == Player.YOU))
							if ((indivBoard.getNode(11, pt.getY()).getOccupied() == Player.EMPTY))
								return new HexPoint(11, pt.getY());
					}
				}
			}
		}

		return null;
	}

	/**
	 * Checks if two points are connected by a third in a triangle
	 * @param a First point
	 * @param b Second point
	 * @return True if they are; False otherwise
	 * @deprecated These are very loosely connected. They can be easily broken.
	 */
	private boolean checkConnectedTriangle(HexPoint a, HexPoint b)
	{
		for (HexPoint p : indivBoard.getNode(a).getTwoChains())
		{
			for (HexPoint k : indivBoard.getNode(b).getTwoChains())
			{
				if (k.equals(p) && !broken(k, a) && !broken(k, b) && !k.equals(a) && !k.equals(b) && indivBoard.getNode(k).getOccupied() == indivBoard.getNode(a).getOccupied())
				{
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Gets the first point issued on the board
	 * @return The first point (or null if we haven't gone yet)
	 */
	public HexPoint getFirst()
	{
		return first;
	}

	/**
	 * Sets the first point
	 * @param first The first point
	 */
	public void setFirst(HexPoint first)
	{
		this.first = first;
	}

}
