package solution.board;

import java.util.ArrayList;
import java.util.List;

import solution.board.implementations.blobBoard.BlobBoard;
import solution.board.implementations.indivBoard.IndivBoard;

/**
 * This accepts moves and applies them to all our boards
 * 
 * @author Daniel Centore
 * @author Mike DiBuduo
 *
 */
public class BoardController
{
	private List<BoardInterface> boards = new ArrayList<BoardInterface>();	// all the boards in the game
	
	// List of all the boards (so we can get them separately)
	private IndivBoard indivBoard;
	private BlobBoard blobBoard;

	public BoardController()
	{
		// Add boards here
		boards.add(indivBoard = new IndivBoard());
		boards.add(blobBoard = new BlobBoard());
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

	/**
	 * Gets a list of all the {@link BoardInterface}s in the game
	 * @return boards
	 */
	public List<BoardInterface> getBoards()
	{
		return boards;
	}

	/**
	 * Gets the instance of {@link IndivBoard} for this game
	 * @return indivBoard
	 */
	public IndivBoard getIndivBoard()
	{
		return indivBoard;
	}

	/**
	 * Gets the instance of {@link BlobBoard} for this game
	 * @return blobBoard
	 */
	public BlobBoard getBlobBoard()
	{
		return blobBoard;
	}
}
