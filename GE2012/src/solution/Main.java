package solution;

import game.GamePlayer;
import game.Tournament;

import hex.HumanHexPlayer;
import hex.RandomHexPlayer;
import hex.RoteHexPlayer;

import solution.debug.DebugWindow;

/**
 * THIS CLASS SHOULD NOT BE INCLUDED IN THE FINAL COPY
 * It is just so we don't have to run their setup all the time.
 * 
 * @author Daniel Centore
 *
 */
public class Main
{
	public static void main(final String[] args)
	{
		DebugWindow.println("Hello");
		new Thread()
		{
			public void run()
			{
				Tournament.initializeServer();
			}
		}.start();

		try
		{
			Thread.sleep(2000);
		} catch (InterruptedException e)
		{
		}

		new Thread()
		{
			public void run()
			{
				//new HexPlayer_Amity("Amity Regional High School").compete(args, 0);
				new solution.solvers.HSearchPlayer().compete(args, 0);
			}
		}.start();

		 GamePlayer competitor = new HumanHexPlayer("Human");
		// GamePlayer competitor = new RoteHexPlayer("Rote");
		// GamePlayer competitor = new RandomHexPlayer("Random");
		//GamePlayer competitor = new solution.solvers.HSearchPlayer();

		/** Uncomment this if the competitor isn't a HumanHexPlayer */
	    //competitor.makeFrameVisible();
		competitor.compete(args, 0);
	}

}
