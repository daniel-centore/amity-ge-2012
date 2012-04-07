package solution.solvers;

import java.util.ArrayList;
import java.util.Arrays;
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
 * This controls all our solvers and generates the master weight table
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
	private HexPoint initial = null; // our centerpiece/starting move
	private IndivBoard indivBoard;
	private DijkstraBoard dijkstraBoard = null;
	private ClassicBlock classicBlock;

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
	public HexPoint getMove()
	{
		if (initial == null)
			throw new RuntimeException("Should have been set already...");
		
		if (classicBlock.shouldBlock())
		{
			return classicBlock.block();
		}

		dijkstraBoard = new DijkstraBoard(indivBoard, curr);

		HexPoint broken = null;

		try
		{
			// Fix chains between a point and the wall if necessary
			broken = baseTwoChainsBroken();
			if (broken != null)
				return broken;
		} catch (Exception e)
		{
			// Fails on some corner cases. just ignore this.
		}

		// grab immediate fixes
		broken = immediatePoint();
		if (broken != null)
			return broken;

		// Fix chains between points if necessary
		broken = twoChainsBroken();
		if (broken != null)
			return broken;

		// follow chain down board
		return followChain();

		// TODO: Use a new method which actually checks all 2 chains and makes sure they're connected
	}


	/**
	 * Checks to see if there are two-chains all the way across the board
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

						if (IndivNode.empty(good, indivBoard)) // only if both connections are good
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

						if (IndivNode.empty(good, indivBoard)) // only if both connections are good
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

		System.out.println("Left" + a);
		System.out.println("right" + b);

		return (left ? a : b);

	}

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

	// tries to find a point right around a hex which will connect to a side
	private HexPoint immediatePoint()
	{
		for (IndivNode node : indivBoard.getPoints())
		{
			if (node.getOccupied() == Player.ME)
			{
				// DebugWindow.println(node.toString() + " " + node.getPoints().get(0).touching().toString());

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
	 * gets the next {@link HexPoint} in order to follow a two-chain across the board
	 * used when there is a complete two-chain connection from one side to the other
	 * @return the next {@link HexPoint} to complete the chain
	 */
	private HexPoint followChain()
	{
		List<HexPoint> possible = new ArrayList<HexPoint>();

		// add all 2-chains as possible
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

		// DebugWindow.println(possible.toString());
		Iterator<HexPoint> itr = possible.iterator();

		double left = Double.MAX_VALUE;
		HexPoint bestLeft = null;

		double right = Double.MAX_VALUE;
		HexPoint bestRight = null;

		if (!itr.hasNext())
			return null;

		do
		{
			HexPoint h = itr.next();

			if (curr.getConnectRoute() == CurrentGame.CONNECT_LETTERS)
			{
				if (h.getY() < 'f')
				{
					double dist = calculateDistance(h);
					if (dist < left)
					{
						bestLeft = h;
						left = dist;
					}
				}
				else
				{
					double dist = calculateDistance(h);
					if (dist < right)
					{
						bestRight = h;
						right = dist;
					}
				}
			}
			else
			{
				if (h.getX() < 6)
				{
					double dist = calculateDistance(h);
					if (dist < left)
					{
						bestLeft = h;
						left = dist;
					}
				}
				else
				{
					double dist = calculateDistance(h);
					if (dist < right)
					{
						bestRight = h;
						right = dist;
					}
				}
			}

		} while (itr.hasNext());

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
	private double calculateDistance(HexPoint pnt)
	{

		DijkstraNode wall;

		if (curr.getConnectRoute() == CurrentGame.CONNECT_LETTERS)
		{
			if (pnt.getY() < 'f')
				wall = dijkstraBoard.getWallA();
			else
				wall = dijkstraBoard.getWallK();
		}
		else
		{
			if (pnt.getX() < 6)
				wall = dijkstraBoard.getWallOne();
			else
				wall = dijkstraBoard.getWallEle();
		}

		return dijkstraBoard.findDistance(pnt, wall);

	}

	/**
	 * Checks if the path ehre is broken
	 * @param a
	 * @param b
	 * @return
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

				// skip the corners - these aren't two chains anyway
				for (HexPoint b : bad)
				{
					// DebugWindow.println("Skipping over: "+b.toString());
					if (node.equals(b))
						continue niceloop;
				}

				// DebugWindow.println("Testing bridge: "+node.toString());

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

						if (checkConnected(node.getPoints().get(0), pnt)) // skip if we are connected anyway in a triangle
							continue;

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

	// check if these 2 are connected by 2 chains in a triangle fashion
	// assumes a and b are same color
	private boolean checkConnected(HexPoint a, HexPoint b)
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
	 * returns the centerpiece for our algorithm
	 * @return initial
	 */
	public HexPoint getInitial()
	{
		return initial;
	}

	/**
	 * sets the centerpiece for our algorithm
	 * @param initial our centerpiece/starting move
	 */
	public void setInitial(HexPoint initial)
	{
		this.initial = initial;
	}

}
