
package solution.solvers;

import solution.board.HexPoint;

/**
 * Class documentation.
 *
 * @author Mike DiBuduo
 */
public class WeightedPoint extends HexPoint
{

	private float weight;
	public WeightedPoint(int x, char y, float weight)
	{
		super(x, y);
		this.weight = weight;
	}

	public float getWeight()
	{
		return weight;
	}
	
	
	
	
	

}