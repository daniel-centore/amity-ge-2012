package solution.board;

import java.awt.Point;

/**
 * Represents a single point on the hex grid
 * 
 * @author Daniel Centore
 *
 */
public class HexPoint
{
	private int x;
	private char y;
	
	/**
	 * Creates a representation of a single point on a grid
	 * Similar to the {@link Point} class in the Java API
	 * @param x The row we are on
	 * @param y The column we are on
	 */
	public HexPoint(int x, char y)
	{
		this.x = x;
		this.y = y;
	}

	/**
	 * Gets the row (integer) of the point
	 * @return
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * Sets the row
	 * @param x The row to set it to
	 */
	public void setX(int x)
	{
		this.x = x;
	}

	/**
	 * Gets the column as a char
	 * @return The column
	 */
	public char getY()
	{
		return y;
	}

	/**
	 * Sets the column
	 * @param y The column to set it to
	 */
	public void setY(char y)
	{
		this.y = y;
	}
	
}