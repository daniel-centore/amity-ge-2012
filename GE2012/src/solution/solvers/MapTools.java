package solution.solvers;

import java.util.ArrayList;
import java.util.List;

import solution.CurrentGame;
import solution.board.HexPoint;
import solution.board.Player;
import solution.board.implementations.indivBoard.IndivNode;
import solution.debug.DebugWindow;

public class MapTools
{

	/**
	 * Checks to see if there are two-chains all the way across the board
	 * Assumes that all points are connected either by bridges or directly touching, so this 
	 *    will fail if we have resorted to random. Our fate is determined by then anyways.
	 * @param solverController The {@link SolverController} we are linked with
	 * @param left The side to check for
	 * @return true if there is a two-chain path across the board, false if not
	 */
	protected boolean across(SolverController solverController, boolean left)
	{
		boolean a = false;
		boolean b = false;
	
		for (IndivNode node : solverController.indivBoard.getPoints())
		{
			if (node.getOccupied() == Player.ME)
			{
				if (solverController.curr.getConnectRoute() == CurrentGame.CONNECT_LETTERS)
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
	
						if (IndivNode.empty(good, solverController.indivBoard) && good.size() == 2) // only if both connections are good
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
	
						if (IndivNode.empty(good, solverController.indivBoard) && good.size() == 2) // only if both connections are good
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
	
						if (IndivNode.empty(good, solverController.indivBoard)) // only if both connections are good
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
	
						if (IndivNode.empty(good, solverController.indivBoard)) // only if both connections are good
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
	 * @param solverController The {@link SolverController} we're connected to
	 * @param node The {@link HexPoint} to check
	 * @return True if connected; False otherwise
	 */
	protected boolean connectedToWall(SolverController solverController, HexPoint node)
	{
		if (solverController.curr.getConnectRoute() == CurrentGame.CONNECT_LETTERS)
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
	
				if (IndivNode.empty(good, solverController.indivBoard)) // only if both connections are good
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
	
				if (IndivNode.empty(good, solverController.indivBoard)) // only if both connections are good
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
	
				if (IndivNode.empty(good, solverController.indivBoard)) // only if both connections are good
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
	
				if (IndivNode.empty(good, solverController.indivBoard)) // only if both connections are good
					return true;
			}
		}
	
		return false;
	}

	/**
	 * Checks if a two chain has been broken. 
	 * @param solverController {@link SolverController} we're connected to
	 * @return the {@link HexPoint} needed to fix a broken two-chain (or null if none are broken)
	 */
	protected HexPoint twoChainsBroken(SolverController solverController)
	{
		for (IndivNode node : solverController.indivBoard.getPoints())
		{
			if (node.getOccupied() == Player.ME)
			{
				List<HexPoint> chains = node.getTwoChains();// indivBoard);
				for (HexPoint pnt : chains)
				{
					if (solverController.indivBoard.getNode(pnt).getOccupied() == Player.ME)
					{
						List<HexPoint> connections = pnt.connections(node.getPoints().get(0));
	
						if (connections.size() < 2)
							throw new RuntimeException("Size less than 2!");
	
						HexPoint a = connections.get(0);
						HexPoint b = connections.get(1);
	
						// if (checkConnected(node.getPoints().get(0), pnt)) // skip if we are connected anyway in a triangle
						// continue;
	
						// if either connector is broken, then cling onto the other
						if (solverController.indivBoard.getNode(a).getOccupied() == Player.YOU && solverController.indivBoard.getNode(b).getOccupied() == Player.EMPTY)
							return b;
						else if (solverController.indivBoard.getNode(b).getOccupied() == Player.YOU && solverController.indivBoard.getNode(a).getOccupied() == Player.EMPTY)
							return a;
					}
				}
			}
		}
	
		return null; // nothing broken
	}

}
