package solution.board;

import java.util.ArrayList;
import java.util.List;

import solution.board.implementations.indivBoard.IndivBoard;

/**
 * This accepts moves and applies them to all our boards
 * 
 * @author Daniel Centore
 *
 */
public class BoardController
{
	private List<BoardInterface> boards = new ArrayList<BoardInterface>();

	public BoardController()
	{
		// Add boards here
		boards.add(new IndivBoard());
	}

	/**
	 * Applies a move to the board
	 * @param x The row we apply on
	 * @param y The column we apply on
	 * @param player Which {@link Player} made the move
	 */
	public void applyMove(int x, char y, Player player)
	{
		for (BoardInterface board : boards)
		{
			board.applyMove(x, y, player);
		}
	}
}
