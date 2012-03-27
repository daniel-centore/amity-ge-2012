package solution.solvers.multiplied;

import java.util.ArrayList;
import java.util.List;

import solution.CurrentGame;
import solution.debug.DebugWindow;
import solution.solvers.AmitySolver;
import solution.solvers.WeightedPoint;

/**
 * This solver is for testing only. It just weighs the spot by multiplying its x and y coords
 * 
 * @author Daniel Centore
 *
 */
public class MultSolver implements AmitySolver
{

	public MultSolver(CurrentGame curr)
	{
		DebugWindow.println("WARNING: You are using MultSolver which is ONLY for testing");
	}
	
	@Override
	public float getWeight()
	{
		return 1.0f;
	}

	@Override
	public List<WeightedPoint> getPoints()
	{
		ArrayList<WeightedPoint> result = new ArrayList<WeightedPoint>();
		
		for (int i = 1; i <= 11; i++)
		{
			for (char c = 'a'; c <= 'k'; c++)
				result.add(new WeightedPoint(i, c, i*c));
		}
		
		return result;
	}

}
