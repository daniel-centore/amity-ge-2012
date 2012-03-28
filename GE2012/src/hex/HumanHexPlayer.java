package hex;

import javax.swing.*;

import game.*;

public class HumanHexPlayer extends RandomHexPlayer
{
	private GameFrame frame;
	private HexMove move = new HexMove(0, 0);

	public String messageForOpponent(String opponent)
	{
		return "I'm a humanist";
	}

	public HumanHexPlayer(String nname)
	{
		super(nname);
		frame = new GameFrame(nickname, new HexCanvas());
		frame.setVisible(true);
		frame.setResizable(false);
		gameState = new HexState();
	}

	public void timeOfLastMove(double secs)
	{

		// System.out.println("" + secs);
	}

	public GameMove getMove(GameState game, String lastMove)
	{
		char ch = side == GameState.Who.HOME ? HexState.homeSym : HexState.awaySym;
		frame.setTitle("My move (" + ch + ")");
		if (!lastMove.equals("--") && frame.canvas.move != null)
		{
			((HexMove) frame.canvas.move).parseMove(lastMove);
		}

		boolean OK;
		do
		{
			frame.canvas.setBoard(game);
			frame.canvas.repaint();
			frame.canvas.getMove(move, game, this);

			try
			{
				frame.canvas.ready.acquire();
			} catch (Exception e)
			{
			}
			OK = game.moveOK(move);
			if (!OK)
			{
				if ((move.col == 5 || move.row == 5) && game.getNumMoves() <= 1)
					JOptionPane.showMessageDialog(null, "You may not start in the middle!", "Illegal Move", JOptionPane.ERROR_MESSAGE);

				// otherwise you clicked on the line which we don't care about
			}
		} while (!OK);

		game.makeMove(move);
		System.out.println(game);
		frame.canvas.repaint();
		frame.setTitle("Waiting");
		return move;
	}

	public void endGame(int result)
	{
		char ch = side == GameState.Who.HOME ? HexState.homeSym : HexState.awaySym;
		if (result == 1)
		{
			frame.setTitle("Won (" + ch + ")");
		}
		else if (result == -1)
		{
			frame.setTitle("Loss (" + ch + ")");
		}
		else
		{
			frame.setTitle("Draw (" + ch + ")");
		}
	}

	// public static void main(String[] args)
	// {
	// GamePlayer p = new HumanHexPlayer("HUMAN");
	// p.compete(args);
	// }
}
