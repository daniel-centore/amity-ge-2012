
package solution.solvers;

import solution.board.HexPoint;

/**
 * Handles point weights. Note that a point weighted 0 will NEVER BE CHOSEN (unless we have no other choice)
 *
 * @author Daniel Centore
 * @author Mike DiBuduo
 * @deprecated
 */
public class WeightedPoint extends HexPoint
{

	private float weight; //the weight of the space
	public WeightedPoint(int x, char y, float weight)
	{
		super(x, y);
		
		this.weight = weight;
	}

	/**
	 * returns weight of point
	 * @return weight
	 */
	public float getWeight()
	{
		return weight;
	}

	/**
	 * sets the weight of the point
	 * @param weight the new weight
	 */
	public void setWeight(float weight)
	{
		this.weight = weight;
	}
	
	/**
	 * converts a weighted point into a {@link HexPoint}
	 * @return a {@link HexPoint} representation of the WeightedPoint
	 */
	public HexPoint toHexPoint()
	{
		return new HexPoint(super.getX(), super.getY());
	}
	
	
	

}