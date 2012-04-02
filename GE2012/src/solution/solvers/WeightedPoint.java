
package solution.solvers;

import solution.board.HexPoint;

/**
 * Handles point weights. Note that a point weighted 0 will NEVER BE CHOSEN (unless we have no other choice)
 *
 * @author Daniel Centore
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

	public void setWeight(float weight)
	{
		this.weight = weight;
	}
	
	public HexPoint toHexPoint()
	{
		return new HexPoint(super.getX(), super.getY());
	}
	
	
	

}
