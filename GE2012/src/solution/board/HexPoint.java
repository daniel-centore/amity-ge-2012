package solution.board;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import solution.CurrentGame;

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
	 * Returns true if its a valid point
	 * @return
	 */
	public boolean isGood()
	{
		int j = y - CurrentGame.CHARACTER_SUBTRACT;
		return (x >= 1 && x <= 11 && j >= 1 && j <= 11);
	}

	/**
	 * Finds the 2 connections to a 2-bridge
	 * @param bridge Must create a 2-bridge with this
	 * @return
	 */
	public List<HexPoint> connections(HexPoint bridge)
	{
		List<HexPoint> mine = touching();
		List<HexPoint> your = bridge.touching();

		List<HexPoint> result = new ArrayList<HexPoint>();

		// find spots in common
		for (HexPoint h : mine)
		{
			for (HexPoint k : your)
			{
				if (k.equals(h))
					result.add(k);
			}
		}

		return result;
	}

	// public static void main(String args[])
	// {
	// System.out.println(new HexPoint(7, 'e').connections(new HexPoint(5, 'f')));
	// }
	/**
	 * GENERATES an array of touching hex points
	 * @return
	 */
	public List<HexPoint> touching()
	{
		HexPoint[] hps = new HexPoint[6];

		hps[0] = new HexPoint(x, (char) (y - 1));
		hps[1] = new HexPoint(x + 1, (char) (y - 1));
		hps[2] = new HexPoint(x + 1, y);
		hps[3] = new HexPoint(x, (char) (y + 1));
		hps[4] = new HexPoint(x - 1, (char) (y + 1));
		hps[5] = new HexPoint(x - 1, y);

		List<HexPoint> result = new ArrayList<HexPoint>();
		for (HexPoint h : hps)
		{
			if (h.isGood())
				result.add(h);
		}

		return result;
	}

	/**
	 * Gets the row (integer) of the point
	 * @return x
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
	 * @return y
	 */
	public char getY()
	{
		return y;
	}

	/**
	 * Sets the column
	 * @param y 
	 */
	public void setY(char y)
	{
		this.y = y;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final HexPoint other = (HexPoint) obj;
		if (this.x != other.x)
		{
			return false;
		}
		if (this.y != other.y)
		{
			return false;
		}

		return true;
	}

	/**
	 * returns a String representation of a HexPoint
	 */
	@Override
	public String toString()
	{
		return "HexPoint [x=" + x + ", y=" + y + "]";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

}
