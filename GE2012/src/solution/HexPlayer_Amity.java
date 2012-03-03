package solution;

import hex.*;
import game.*;

public class HexPlayer_Amity extends GamePlayer
{

	public HexPlayer_Amity()
	{
		super("Amity Regional High School", new HexState(), false);
	}

	/**
	 * Initializes the player at the beginning of the tournament. This is called
	 * once, before any games are played. This function must return within
	 * the time alloted by the game timing parameters. Default behavior is
	 * to do nothing.
	 */
	public void init()
	{
	}

	/**
	 * This is called at the start of a new game. This should be relatively
	 * fast, as the player must be ready to respond to a move request, which
	 * should come shortly thereafter. The side being played (HOME or AWAY)
	 * is stored in the side data member. Default behavior is to do nothing. 
	 * @param opponent Name of the opponent being played
	 */
	public void startGame(String opponent)
	{
	}

	/**
	 * Called to inform the player how long the last move took. This can
	 * be used to calibrate the player's search depth. Default behavior is
	 * to do nothing.
	 * @param secs Time for the server to receive the last move
	 */
	public void timeOfLastMove(double secs)
	{
	}

	/**
	 * Called when the game has ended. Default behavior is to do nothing. 
	 * @param result -1 if loss, 0 if draw, +1 if win
	 */
	public void endGame(int result)
	{
	}

	/**
	 * Called at the end of the tournament. Can be used to do
	 * housekeeping tasks. Default behavior is to do nothing.
	 */
	public void done()
	{
	}

	/**
	 * Produces the player's move, given the current state of the game.
	 * This function must return a value within the time alloted by the
	 * game timing parameters.
	 * 
	 * @param state Current state of the game
	 * @param lastMv Opponent's last move. "--" if it is game's first move. 
	 * @return Player's move
	 */
	public GameMove getMove(GameState state, String lastMove)
	{
		HexState board = (HexState)state;
		return null;
	}

}
