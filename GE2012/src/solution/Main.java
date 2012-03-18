package solution;

import solution.debug.DebugWindow;
import game.GamePlayer;
import game.Tournament;
import hex.HumanHexPlayer;
import hex.RandomHexPlayer;

/**
 * This class should not be included in the final copy.
 * It is just so we don't have to run their shitty setup all the time.
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
				new HexPlayer_Amity().compete(args, 0);
			}
		}.start();

		GamePlayer competitor = new HumanHexPlayer("Random");

		/** Uncomment this if the competitor isn't a HumanHexPlayer */
		// competitor.makeFrameVisible();
		competitor.compete(args, 0);
	}

}
