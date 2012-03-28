package hex;

import game.GameMove;
import game.GamePlayer;
import game.GameState;

public class RoteHexPlayer extends GamePlayer
{
	public RoteHexPlayer(String n)
	{
		super(n, new HexState(), false);
	}

	public GameMove getMove(GameState state, String lastMove)
	{
		HexState board = (HexState) state;
		HexMove mv = new HexMove();
		if (GameState.Who.HOME == state.getWho())
		{
			for (int c = 0; c < HexState.N; c++)
			{
				for (int r = 0; r < HexState.N; r++)
				{
					mv.row = r;
					mv.col = c;
					if (board.moveOK(mv))
					{
						return mv;
					}
				}
			}
		}
		else
		{
			for (int r = 0; r < HexState.N; r++)
			{
				for (int c = 0; c < HexState.N; c++)
				{
					mv.row = r;
					mv.col = c;
					if (board.moveOK(mv))
					{
						return mv;
					}
				}
			}
		}
		return null;
	}

	// public static void main(String [] args)
	// {
	// GamePlayer p = new RoteHexPlayer("Rote+");
	// p.compete(args, 1);
	// }
}
