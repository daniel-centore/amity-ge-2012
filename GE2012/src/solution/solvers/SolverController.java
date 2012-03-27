package solution.solvers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import solution.CurrentGame;
import solution.board.HexPoint;
import solution.board.Player;
import solution.board.implementations.indivBoard.IndivNode;
import solution.solvers.multiplied.MultSolver;

/**
 * This controls all our solvers and generates the master weight table
 * 
 * @author Daniel Centore
 *
 */
public class SolverController
{
	private List<AmitySolver> solvers;
	private HashMap<HexPoint, WeightedPoint> map;	// map of the points on our grid
	private CurrentGame curr; 
	
	public SolverController(CurrentGame curr)
	{
		this.curr = curr;
		solvers = new ArrayList<AmitySolver>();
		solvers.add(new MultSolver(curr));
	}
	
	/**
	 * Gets the highest rated move. PROBABLY VERY SLOW.
	 * @return
	 */
	public WeightedPoint getMove()
	{
		// clear out the old map
		map = new HashMap<HexPoint, WeightedPoint>();
		
		// iterate through our solvers and merge em
		for (AmitySolver solver : solvers)
		{
			List<WeightedPoint> pts = solver.getPoints();
			float w = solver.getWeight();
			
			// for each point in each solver
			for (WeightedPoint wp : pts)
			{
				HexPoint key = new HexPoint(wp.getX(), wp.getY());
				WeightedPoint val = map.get(key);
				if (val == null)
				{
					val = new WeightedPoint(key.getX(), key.getY(), wp.getWeight() * w);		// if another solver hasn't handled it yet
					map.put(key, val);
				}
				else
					val.setWeight(val.getWeight() * wp.getWeight() * w);		// if we need to merge the weights
			}
		}
		
		// find the highest rated move
		Iterator<WeightedPoint> itr = map.values().iterator();
		WeightedPoint largest = null;
		
		while (itr.hasNext())
		{
			WeightedPoint p = itr.next();
			if (largest == null || p.getWeight() > largest.getWeight())
			{
				if (curr.getBoardController().getIndivBoard().getNode(p.getX(), p.getY()).getOccupied() == Player.EMPTY)	// only if the space is unoccupied
					largest = p;
			}
		}
		
		return largest;
	}

}
