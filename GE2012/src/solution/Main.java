package solution;

import game.GameFrame;
import game.GamePlayer;
import game.Tournament;
import hex.HexCanvas;
import hex.HumanHexPlayer;
import hex.RandomHexPlayer;
import hex.RoteHexPlayer;

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
				// ==    Our Solver     == //
				new HumanHexPlayer("Hey").compete(args, 0);
				// == End of our Solver == //
			}
		}.start();
		
		
		// ==    The Competitor     == //
		GamePlayer competitor = new RandomHexPlayer("Random");
		// == End of the Competitor == //
		
		competitor.makeFrameVisible();
		competitor.compete(args, 0);
	}

}
