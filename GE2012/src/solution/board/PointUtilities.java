package solution.board;

import java.awt.Point;

/**
 * Has untilities for working with {@link HexPoint}s
 * 
 * @author Daniel Centore
 * @author Mike DiBuduo
 *
 */
public class PointUtilities
{
	/**
	 * Converts the column from char notation to int
	 * @param c The column
	 */
	public static int toInt(char c)
	{
            // TODO: double check if returning correct int value for the column
            return ((int) c) - 97;	// 97 is the unicode value of 'a'
	}
	
	/**
	 * Converts a {@link HexPoint} into a regular {@link Point}.
	 * Shouldn't need to be used much
	 * @param point A {@link HexPoint}
	 * @return A {@link Point}
	 */
	public static Point toPoint(HexPoint point)
	{
		return new Point(point.getX(), toInt(point.getY()));
	}
}
