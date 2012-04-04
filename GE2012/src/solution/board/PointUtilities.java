package solution.board;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Has utilities for working with {@link HexPoint}s
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
		return ((int) c) - 97; // 97 is the unicode value of 'a'
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
	
	/**
	 * Generates a {@link List} of {@link HexPoint}s which are directly surrounding a point
	 * @param of The {@link HexPoint} in the center
	 * @return The neighboring points
	 */
	public static List<HexPoint> getNeighbors(HexPoint of)
	{
		int x = of.getX();
		char y = of.getY();

		// These are possible surroundings (gotta check bounds)
		HexPoint[] potential = {
				new HexPoint(x, (char) (y - 1)),
				new HexPoint(x - 1, y),
				new HexPoint(x - 1, (char) (y + 1)),
				new HexPoint(x, (char) (y + 1)),
				new HexPoint(x + 1, y),
				new HexPoint(x + 1, (char) (y - 1)) };
		
		List<HexPoint> result = new ArrayList<HexPoint>();
		for (HexPoint p : potential)
		{
			// Make sure the point is on the board
			if (p.getX() >= 1 && p.getX() <= 11 && p.getY() >= 'a' && p.getY() <= 'k')
				result.add(p);
		}
		
		return result;
	}
}
