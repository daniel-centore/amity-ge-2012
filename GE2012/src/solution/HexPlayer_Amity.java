package solution;

import game.GameMove;
import game.GamePlayer;
import game.GameState;
import hex.HexState;

/**
 * Our own custom solver designed to compete in a Hex tornament
 * 
 * @author Mike DiBuduo
 * @author Daniel Centore
 *
 */
public class HexPlayer_Amity extends GamePlayer
{
	private CurrentGame currentGame;
	
	public HexPlayer_Amity(String name)
	{
		super(name, new HexState(), false);
	}

	/**
	 * Initializes the player at the beginning of the tournament. This is called
	 * once, before any games are played. This function must return within
	 * the time alloted by the game timing parameters. Default behavior is
	 * to do nothing.
	 */
	public void init()
	{
		System.gc();	// let's clean up other people's junk
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
		currentGame = new CurrentGame();
		currentGame.init();
	}

	/**
	 * Called to inform the player how long the last move took. This can
	 * be used to calibrate the player's search depth. Default behavior is
	 * to do nothing.
	 * @param secs Time for the server to receive the last move
	 */
	public void timeOfLastMove(double secs)
	{
		// we'll probably do this on our own
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
		GameMove result = currentGame.getMove(state, lastMove);
		
		return result;
	}
	
	public static void main(String[] args)
	{
		GamePlayer p = new HexPlayer_Amity("Amity"); // give your player a name here
		p.compete(args, 1);
	}

}
