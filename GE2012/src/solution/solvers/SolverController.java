package solution.solvers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import solution.CurrentGame;
import solution.board.HexPoint;
import solution.board.Player;
import solution.board.implementations.dijkstraBoard.DijkstraBoard;
import solution.board.implementations.dijkstraBoard.DijkstraNode;
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
	private CurrentGame curr; // Current game
	private IndivBoard indivBoard;
	private DijkstraBoard dijkstraBoard = null;
	private ClassicBlock classicBlock;
	private HexPoint first = null;	// the first move we made (so we can calculate left and right sides correctly)

	/**
	 * Creates a new {@link SolverController}
	 * @param curr The {@link CurrentGame}
	 */
	public SolverController(CurrentGame curr)
	{
		this.curr = curr;
		indivBoard = curr.getBoardController().getIndivBoard();
		classicBlock = new ClassicBlock(indivBoard, curr);
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
			broken = twoChainsBroken();
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
		return followChain();
	}

	/**
	 * Checks to see if there are two-chains all the way across the board
	 * Assumes that all points are connected either by bridges or directly touching, so this 
	 *    will fail if we have resorted to random. Our fate is determined by then anyways.
	 * @return true if there is a two-chain path across the board, false if not
	 */
	private boolean across(boolean left)
	{
		boolean a = false;
		boolean b = false;

		for (IndivNode node : indivBoard.getPoints())
		{
			if (node.getOccupied() == Player.ME)
			{
				if (curr.getConnectRoute() == CurrentGame.CONNECT_LETTERS)
				{
					if (node.getY() == 'a')
						a = true;
					else if (node.getY() == 'b')
					{
						// check the connection to the wall
						HexPoint[] k = { new HexPoint(node.getX(), 'a'), new HexPoint(node.getX() + 1, 'a') };
						List<HexPoint> good = new ArrayList<HexPoint>();
						for (HexPoint j : k)
						{
							if (j.isGood())
								good.add(j);
						}

						if (IndivNode.empty(good, indivBoard) && good.size() == 2) // only if both connections are good
							a = true;
					}

					if (node.getY() == 'k')
						b = true;
					else if (node.getY() == 'j')
					{
						// check the connection to the wall
						HexPoint[] k = { new HexPoint(node.getX(), 'k'), new HexPoint(node.getX() - 1, 'k') };

						List<HexPoint> good = new ArrayList<HexPoint>();
						for (HexPoint j : k)
						{
							if (j.isGood())
								good.add(j);
						}

						if (IndivNode.empty(good, indivBoard) && good.size() == 2) // only if both connections are good
							b = true;
					}
				}
				else
				{
					if (node.getX() == 1)
						a = true;
					else if (node.getX() == 2)
					{
						// check the connection to the wall
						HexPoint[] k = { new HexPoint(1, node.getY()), new HexPoint(1, (char) (node.getY() + 1)) };

						List<HexPoint> good = new ArrayList<HexPoint>();
						for (HexPoint j : k)
						{
							if (j.isGood())
								good.add(j);
						}

						if (IndivNode.empty(good, indivBoard)) // only if both connections are good
							a = true;
					}

					if (node.getX() == 11)
						b = true;
					else if (node.getX() == 10)
					{
						// check the connection to the wall
						HexPoint[] k = { new HexPoint(11, node.getY()), new HexPoint(11, (char) (node.getY() - 1)) };

						List<HexPoint> good = new ArrayList<HexPoint>();
						for (HexPoint j : k)
						{
							if (j.isGood())
								good.add(j);
						}

						if (IndivNode.empty(good, indivBoard)) // only if both connections are good
							b = true;
					}
				}
			}
		}

		DebugWindow.println("Left: "+a+". Right: "+b+".");

		return (left ? a : b);

	}

	/**
	 * Checks if a hex piece is connected to a *home wall* via bridge or touching
	 * @param node The {@link HexPoint} to check
	 * @return True if connected; False otherwise
	 */
	private boolean connectedToWall(HexPoint node)
	{
		if (curr.getConnectRoute() == CurrentGame.CONNECT_LETTERS)
		{
			if (node.getY() == 'a')
				return true;
			else if (node.getY() == 'b')
			{
				// check the connection to the wall
				HexPoint[] k = { new HexPoint(node.getX(), 'a'), new HexPoint(node.getX() + 1, 'a') };

				List<HexPoint> good = new ArrayList<HexPoint>();
				for (HexPoint j : k)
				{
					if (j.isGood())
						good.add(j);
				}

				if (IndivNode.empty(good, indivBoard)) // only if both connections are good
					return true;
			}

			if (node.getY() == 'k')
				return true;
			else if (node.getY() == 'j')
			{
				// check the connection to the wall
				HexPoint[] k = { new HexPoint(node.getX(), 'k'), new HexPoint(node.getX() - 1, 'k') };

				List<HexPoint> good = new ArrayList<HexPoint>();
				for (HexPoint j : k)
				{
					if (j.isGood())
						good.add(j);
				}

				if (IndivNode.empty(good, indivBoard)) // only if both connections are good
					return true;
			}
		}
		else
		{
			if (node.getX() == 1)
				return true;
			else if (node.getX() == 2)
			{
				// check the connection to the wall
				HexPoint[] k = { new HexPoint(1, node.getY()), new HexPoint(1, (char) (node.getY() + 1)) };

				List<HexPoint> good = new ArrayList<HexPoint>();
				for (HexPoint j : k)
				{
					if (j.isGood())
						good.add(j);
				}

				if (IndivNode.empty(good, indivBoard)) // only if both connections are good
					return true;
			}

			if (node.getX() == 11)
				return true;
			else if (node.getX() == 10)
			{
				// check the connection to the wall
				HexPoint[] k = { new HexPoint(11, node.getY()), new HexPoint(11, (char) (node.getY() - 1)) };

				List<HexPoint> good = new ArrayList<HexPoint>();
				for (HexPoint j : k)
				{
					if (j.isGood())
						good.add(j);
				}

				if (IndivNode.empty(good, indivBoard)) // only if both connections are good
					return true;
			}
		}

		return false;
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
					if (connectedToWall(around) && indivBoard.getNode(around).getOccupied() == Player.EMPTY)
					{
						boolean left = false;
						if ((curr.getConnectRoute() == CurrentGame.CONNECT_LETTERS && around.getY() < 'f') ||
								(curr.getConnectRoute() == CurrentGame.CONNECT_NUMBERS && around.getX() < 6))
							left = true;

						if (!across(left))
							return around;
					}
				}
			}
		}

		return null;
	}

	/**
	 * Gets the next {@link HexPoint} in order to follow a two-chain across the board
	 * @return The next {@link HexPoint} to continue the chain
	 */
	private HexPoint followChain()
	{
		List<HexPoint> possible = new ArrayList<HexPoint>();

		// Add all points which are two-chains from one of our own pieces
		for (IndivNode node : indivBoard.getPoints())
		{
			if (node.getOccupied() == Player.ME)
			{
				List<HexPoint> chains = node.getTwoChains();
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

		double left = Double.MAX_VALUE;
		HexPoint bestLeft = null;

		double right = Double.MAX_VALUE;
		HexPoint bestRight = null;

		if (!itr.hasNext())
			return null;

		// Find the best move for connecting to both the left and right walls
		do
		{
			HexPoint h = itr.next();
			
			double leftDist = calculateDistance(h, true);
			double rightDist = calculateDistance(h, false);
			
			if (leftDist < left)
			{
				bestLeft = h;
				left = leftDist;
			}
			
			if (rightDist < right)
			{
				bestRight = h;
				right = rightDist;
			}

		} while (itr.hasNext());

		// Choose the piece which is *weaker* so that we strengthen the link with that wall
		// TODO: Include number of paths available in this calculation
		if (across(true) && bestRight != null)
			return bestRight;
		else if (across(false) && bestLeft != null)
			return bestLeft;

		if (left > right && bestLeft != null)
			return bestLeft;
		else
			return bestRight;

	}

	/**
	 * Figures out how hard it would be to get to the closest wall
	 * @param pnt the starting {@link HexPoint}
	 * @return The difficulty (arbitrary scale)
	 */
	private double calculateDistance(HexPoint pnt, boolean left)
	{

		DijkstraNode wall;

		if (curr.getConnectRoute() == CurrentGame.CONNECT_LETTERS)
		{
			if (left)
				wall = dijkstraBoard.getWallA();
			else
				wall = dijkstraBoard.getWallK();
		}
		else
		{
			if (left)
				wall = dijkstraBoard.getWallOne();
			else
				wall = dijkstraBoard.getWallEle();
		}

		return dijkstraBoard.findDistance(pnt, wall);

	}

	/**
	 * Checks if a two-chain is broken
	 * @param a The start node
	 * @param b The end node
	 * @return True if it is BROKEN. False if all's good.
	 */
	private boolean broken(HexPoint a, HexPoint b)
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
	 * Checks if a two chain has been broken. 
	 * @return the {@link HexPoint} needed to fix a broken two-chain (or null if none are broken)
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

						// if (checkConnected(node.getPoints().get(0), pnt)) // skip if we are connected anyway in a triangle
						// continue;

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
