package solution.solvers;

import java.util.List;

/**
 * Interface for one of our solvers in the game
 *
 * @author Daniel Centore
 * @author Mike DiBuduo
 */
public interface AmitySolver 
{
	/**
	 * Gets the overall weight of this solver, based on the situation.
	 * Will probably be 1 for most solvers, unless they detect a reason
	 * that they would probably not be a good choice at the time.
	 * @return
	 */
	public float getWeight();
	
	
}
