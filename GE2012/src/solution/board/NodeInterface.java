package solution.board;

import java.util.List;

/**
 * Represents a node of the board.
 * This is dependent on the type of board.
 * Some may have it represent one position, others may have it represent blobs.
 * 
 * @author Daniel Centore
 *
 */
public interface NodeInterface
{
	/**
	 * Returns the {@link List} of {@link HexPoint}s that this {@link NodeInterface} contains.
	 * Specifics depend on the type of board
	 * @return A {@link List} of {@link HexPoint}s
	 */
	public List<HexPoint> getPoints();
	
	/**
	 * @return The {@link Player} that occupies the space atm
	 */
	public Player getOccupied();
	
}
