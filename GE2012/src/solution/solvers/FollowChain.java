package solution.solvers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import solution.CurrentGame;
import solution.board.HexPoint;
import solution.board.Player;
import solution.board.implementations.dijkstraBoard.DijkstraNode;
import solution.board.implementations.indivBoard.IndivBoard;
import solution.board.implementations.indivBoard.IndivNode;
import solution.debug.DebugWindow;

public class FollowChain
{
	private SolverController solverController;
	
	public FollowChain(SolverController solverController)
	{
		this.solverController = solverController;
	}

	/**
	 * Gets the next {@link HexPoint} in order to follow a two-chain across the board
	 * @param solverController TODO
	 * @return The next {@link HexPoint} to continue the chain
	 */
	protected HexPoint followChain(SolverController solverController)
	{
		List<HexPoint> possible = new ArrayList<HexPoint>();
	
		// Add all points which are two-chains from one of our own pieces
		for (IndivNode node : solverController.indivBoard.getPoints())
		{
			if (node.getOccupied() == Player.ME)
			{
				List<HexPoint> chains = node.getTwoChains();
				for (HexPoint pnt : chains)
				{
					if (solverController.indivBoard.getNode(pnt).getOccupied() == Player.EMPTY && IndivNode.empty(pnt.connections(node.getPoints().get(0)), solverController.indivBoard))
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
			
			double leftDist = calculateDistance(solverController, h, true);
			double rightDist = calculateDistance(solverController, h, false);
			
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
		if (solverController.mapTools.across(solverController, true) && bestRight != null)
			return bestRight;
		else if (solverController.mapTools.across(solverController, false) && bestLeft != null)
			return bestLeft;
	
		if (left > right && bestLeft != null)
			return bestLeft;
		else
			return bestRight;
	
	}

	/**
	 * Figures out how hard it would be to get to the closest wall
	 * @param solverController TODO
	 * @param pnt the starting {@link HexPoint}
	 * @param left TODO
	 * @return The difficulty (arbitrary scale)
	 */
	protected double calculateDistance(SolverController solverController, HexPoint pnt, boolean left)
	{
	
		DijkstraNode wall;
	
		if (solverController.curr.getConnectRoute() == CurrentGame.CONNECT_LETTERS)
		{
			if (left)
				wall = solverController.dijkstraBoard.getWallA();
			else
				wall = solverController.dijkstraBoard.getWallK();
		}
		else
		{
			if (left)
				wall = solverController.dijkstraBoard.getWallOne();
			else
				wall = solverController.dijkstraBoard.getWallEle();
		}
		
		double dist = solverController.dijkstraBoard.findDistance(pnt, wall);
		
		return dist;
	}
	

}
