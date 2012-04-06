package solution.board;

import java.util.List;

/**
 * Represents a node of the board.
 * This is dependent on the type of board.
 * Some may have it represent one position, others may have it represent blobs.
 * 
 * @author Daniel Centore
 * @author Mike
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
	 * Returns the {@link Player} that occupies the space
	 * @return {@link Player} that occupies the space currently
	 */
	public Player getOccupied();
	
}
