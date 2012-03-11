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

	private static DebugFrame df = new DebugFrame();
	static
	{
		df.setVisible(true);
	}

	public static void println(String s)
	{
		int secs = (int) ((System.currentTimeMillis() - ms) / 1000);

		int min = secs / 60;
		int sec = secs % 60;

		JTextArea text = df.getTextArea();
		text.append(min + ":" + (sec < 10 ? "0" : "") +sec + ">" + s + "\n");
		text.setCaretPosition(text.getDocument().getLength());
	}

}
