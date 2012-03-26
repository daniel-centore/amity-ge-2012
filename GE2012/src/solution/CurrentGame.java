package solution;

import game.GameMove;
import game.GameState;
import game.Util;
import hex.HexMove;
import hex.HexState;

import java.util.ArrayList;
import java.util.List;

import solution.board.BoardController;
import solution.board.HexPoint;
import solution.board.Player;
import solution.debug.DebugWindow;

/**
 * A wrapper for our current game so we don't get locked into playing 1-game games in our code
 * 
 * @author Daniel Centore
 *
 */
public class CurrentGame
{
	private BoardController boardController;
	
	public CurrentGame()
	{
		boardController = new BoardController();
	}
	
	/**
	 * Called from {@link HexPlayer_Amity}. We need to return our next move here!
	 * @param state The current state of the game
	 * @param lastMove What the last move was
	 * @return An acceptable move in {@link GameMove} format
	 */
	public GameMove getMove(GameState state, String lastMove)
	{	
		HexMove result = null;
		
		HexPoint point = parseTheirString(lastMove);
		if (point == null)
		{
			// we're going first - do a nice default move
			result = new HexMove(5, 6);
		}
		else
		{
			// they just went - time to counter
			
			// apply their move to our boards
			boardController.applyMove(point.getX(), point.getY(), Player.YOU);
			
			// calculate our next move
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
			result = list.get(which);
		}
		
		
		// apply our move to the board
		point = parseTheirString(result.toString());
		boardController.applyMove(point.getX(), point.getY(), Player.ME);

		return result;
	}
	
	public HexPoint parseTheirString(String move)
	{
		// TODO: create an inverse of this method for when we return a HexPoint as a HexMove in getMove
		
		String[] k = move.split("-");
		if (k.length  < 2)
			return null;
		
		int x = Integer.parseInt(k[0]) + 1;
		char y = (char) (Integer.parseInt(k[1]) + 97);
		
		return new HexPoint(x, y);
	}
}
