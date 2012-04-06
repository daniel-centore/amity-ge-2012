package solution.board.implementations.indivBoard;

import java.util.ArrayList;
import java.util.List;

import solution.board.HexPoint;
import solution.board.NodeInterface;
import solution.board.Player;

/**
 * Represents an individual spot on the grid
 * 
 * @author Daniel Centore
 *
 */
public class IndivNode implements NodeInterface
{
	private int x; // our 'x' location
	private char y; // our 'y' location
	private Player occupied; // who currently owns the space
	private List<HexPoint> points; // only has one
	private List<HexPoint> twoChains = new ArrayList<HexPoint>();

	/**
	 * Creates an individual spot which is unowned
	 * @param x The x location
	 * @param y The y location
	 */
	public IndivNode(int x, char y)
	{
		this(x, y, Player.EMPTY);
	}

	/**
	 * Creates an individual spot
	 * @param x The x location
	 * @param y The y location
	 * @param occupied Who owns the space
	 */
	public IndivNode(int x, char y, Player occupied)
	{
		this.x = x;
		this.y = y;
		this.occupied = occupied;

		points = new ArrayList<HexPoint>();
		points.add(new HexPoint(x, y));
	}

	private void generateTwoChains()
	{
		HexPoint[] chains = new HexPoint[6];

		chains[0] = new HexPoint(x + 1, (char) (y - 2));
		chains[1] = new HexPoint(x + 2, (char) (y - 1));
		chains[2] = new HexPoint(x + 1, (char) (y + 1));
		chains[3] = new HexPoint(x - 2, (char) (y + 2));
		chains[4] = new HexPoint(x - 1, (char) (y + 1));
		chains[5] = new HexPoint(x - 1, (char) (y - 1));
		
		for (HexPoint h : chains)
		{
			if (h.isGood())
				twoChains.add(h);
		}
		
		
	}
	
	public static void main(String args[])
	{
		IndivNode n = new IndivNode(3, 'c');
		n.generateTwoChains();
		System.out.println(n.twoChains);
	}

	@Override
	public List<HexPoint> getPoints()
	{
		return points;
	}

	/**
	 * @return The 'x' location of this spot
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * @return The 'y' location of this spot
	 */
	public char getY()
	{
		return y;
	}

	@Override
	public Player getOccupied()
	{
		return occupied;
	}

	@Override
	public String toString()
	{
		return "IndivNode [x=" + x + ", y=" + y + ", occupied=" + occupied + "]";
	}

	/**
	 * Sets who owns this square
	 * @param occupied The {@link Player} who occupies this square
	 * @throws RuntimeException If we try to set it to {@link Player#EMPTY}
	 */
	public void setOccupied(Player occupied)
	{
		if (occupied == Player.EMPTY)
			throw new RuntimeException("We can\'t change to empty once the game has started...");

		this.occupied = occupied;
	}

}
