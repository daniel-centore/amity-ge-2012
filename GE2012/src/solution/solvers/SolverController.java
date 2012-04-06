package solution.solvers;

import solution.CurrentGame;
import solution.board.HexPoint;

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

	public SolverController(CurrentGame curr)
	{
		this.curr = curr;
	}

	/**
	 * Chooses our next move
	 * @return
	 */
	public HexPoint getMove()
	{
		return null;
	}


}
