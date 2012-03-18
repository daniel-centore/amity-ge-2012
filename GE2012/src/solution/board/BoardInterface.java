package solution.board;

/**
 * Represents the functions that all boards need to be capable of doing
 * 
 * @author Daniel Centore
 *
 */
public interface BoardInterface
{
	/**
	 * Returns the {@link NodeInterface} representing this position
	 * @param x The row (int) we are representing
	 * @param y The col (letter) we are represening
	 * @return The respective {@link NodeInterface}
	 */
	public NodeInterface getNode(int x, char y);
	
	/**
	 * Applies a move to the board
	 * @param x The row we apply on
	 * @param y The column we apply on
	 * @param player Which {@link Player} made the move
	 */
	public void applyMove(int x, char y, Player player);
}
