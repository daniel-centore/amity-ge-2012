package solution;

import game.GameMove;
import game.GameState;
import game.Util;
import hex.HexMove;
import hex.HexState;

import java.util.ArrayList;

import com.sun.corba.se.impl.orbutil.DenseIntMapImpl;

import solution.board.BoardController;
import solution.debug.DebugWindow;

/**
 * A wrapper for our current game so we don't get locked into playing 1-game games in our code
 * 
 * @author Daniel Centore
 *
 */
public class CurrentGame
{
	private BoardController controller;
	
	public CurrentGame()
	{
		controller = new BoardController();
	}
	
	/**
	 * Called from {@link HexPlayer_Amity}. We need to return our next move here!
	 * @param state The current state of the game
	 * @param lastMove What the last move was
	 * @return An acceptable move in {@link GameMove} format
	 */
	public GameMove getMove(GameState state, String lastMove)
	{
		DebugWindow.println(lastMove);
		HexState board = (HexState) state;
		ArrayList<HexMove> list = new ArrayList<HexMove>();
		HexMove mv = new HexMove();
		for (int r = 0; r < HexState.N; r++)
		{
			for (int c = 0; c < HexState.N; c++)
			{
				mv.row = r;
				mv.col = c;
				if (board.moveOK(mv))
				{
					list.add((HexMove) mv.clone());
				}
			}
		}
		int which = Util.randInt(0, list.size() - 1);
		GameMove result = list.get(which);

		return result;
	}
}
