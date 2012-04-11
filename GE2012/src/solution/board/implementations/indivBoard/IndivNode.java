package solution.board.implementations.indivBoard;

import java.util.ArrayList;
import java.util.List;

import solution.board.HexPoint;
import solution.board.NodeInterface;
import solution.board.Player;

/**
 * Represents an individual spot on an {@link IndivBoard}
 * 
 * @author Daniel Centore
 * @author Mike DiBuduo
 *
 */
public class IndivNode implements NodeInterface
{
	private int x; // our 'x' location
	private char y; // our 'y' location
	private Player occupied; // who currently owns the space
	private List<HexPoint> points; // only has one

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
	 * Creates an individual spot which is occupied by a {@link Player}
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
//		if (occupied == Player.EMPTY)
//			throw new RuntimeException("We can\'t change to empty once the game has started...");

		this.occupied = occupied;
	}

	/**
	 * finds all possible locations that a two-chain can be made from this {@link IndivNode}
	 * @return a list of possible two-chain locations
	 */
	public List<HexPoint> getTwoChains()//IndivBoard board)
	{
		List<HexPoint> twoChains = new ArrayList<HexPoint>();
		
		HexPoint[] chains = new HexPoint[6];

		chains[0] = new HexPoint(x + 1, (char) (y - 2));
		chains[1] = new HexPoint(x + 2, (char) (y - 1));
		chains[2] = new HexPoint(x + 1, (char) (y + 1));
		chains[3] = new HexPoint(x - 1, (char) (y + 2));
		chains[4] = new HexPoint(x - 2, (char) (y + 1));
		chains[5] = new HexPoint(x - 1, (char) (y - 1));
		
		for (HexPoint h : chains)
		{
			if (h.isGood())// && empty(h.connections(this.points.get(0)), board))
				twoChains.add(h);
		}
		
		return twoChains;
	}

	/**
	 * checks to see if all of the points in an {@link List<E>} are unoccupied
	 * @param connections the {@link List<E>} to check
	 * @param board the current {@link IndivBoard}
	 * @return true if all are unoccupied, false if any one {@link HexPoint} is occupied
	 */
	public static boolean empty(List<HexPoint> connections, IndivBoard board)
	{
		for (HexPoint p : connections)
		{
			if (board.getNode(p).getOccupied() != Player.EMPTY)
				return false;
		}
		
		return true;
	}

}
