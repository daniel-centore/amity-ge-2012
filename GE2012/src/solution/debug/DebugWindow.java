package solution.debug;

import java.awt.BorderLayout;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class DebugWindow
{
	public static final int MOVE_TIME = 180;
	public static final int GAME_TIME = 850;

	private static long ms = System.currentTimeMillis();
	private static volatile long moveMS = System.currentTimeMillis();

	private static DebugFrame df = new DebugFrame();
	static
	{
		df.setVisible(true);
		
		new Thread()
		{
			public void run()
			{
				while (true)
				{
					updateLabels();
					
					try
					{
						Thread.sleep(500);
					} catch (InterruptedException e)
					{
					}
				}
			}
		}.start();
	}
	
	public static void resetMoveTime()
	{
		moveMS = System.currentTimeMillis();
		updateLabels();
	}
	
	public static void updateLabels()
	{
		int secs = (int) ((System.currentTimeMillis() - ms) / 1000); // total game secs
		
		df.setGameTime(formatSecs(GAME_TIME - secs));
		
		secs = (int) ((System.currentTimeMillis() - moveMS) / 1000);
		df.setMoveTime(formatSecs(MOVE_TIME - secs));
	}
	
	public static String formatSecs(int secs)
	{
		int min = secs / 60;
		int sec = secs % 60;
		
		return min + ":" + (sec < 10 ? "0" : "") + sec;
	}

	public static void println(String s)
	{
		int secs = (int) ((System.currentTimeMillis() - ms) / 1000); // total game secs

		JTextArea text = df.getTextArea();
		text.append(formatSecs(secs) + ">" + s + "\n");
		text.setCaretPosition(text.getDocument().getLength());
	}

}
