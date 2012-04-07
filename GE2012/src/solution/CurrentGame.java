package solution;

import java.util.ArrayList;

import game.GameMove;
import game.GameState;
import game.Util;

import hex.HexMove;
import hex.HexState;

import solution.board.BoardController;
import solution.board.HexPoint;
import solution.board.Player;
import solution.board.implementations.dijkstraBoard.DijkstraBoard;
import solution.debug.DebugWindow;
import solution.solvers.SolverController;

/**
 * A wrapper for our current game so we don't get locked into playing 1-game games in our code
 * 
 * @author Daniel Centore
 * @author Mike DiBuduo
 *
 */
public class CurrentGame
{
	public static final int CHARACTER_SUBTRACT = 96;

	private BoardController boardController;
	private SolverController solverController;

	public static final int CONNECT_NUMBERS = 0; // connect from 1-11 wins (white)
	public static final int CONNECT_LETTERS = 1; // connect from A-K wins (black)
	private int connectRoute = -1; //the connection direction we need to solve for to win

	public void init()
	{
		boardController = new BoardController(this);
		solverController = new SolverController(this);
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
			HexPoint me = new HexPoint(5, 'f');

			result = toHexMove(me);

			solverController.setInitial(me);


			connectRoute = CONNECT_NUMBERS;
		}
		else
		{
			// they just went - time to counter

			// apply their move to our boards
			boardController.applyMove(point.getX(), point.getY(), Player.YOU);

			// calculate our next move
			if (connectRoute < 0) // our first move (they just went first)
			{
				// TODO: smarter first point choice. get far enough away from enemy.
				HexPoint initial;
				if (boardController.getIndivBoard().getNode(5, 'f').getOccupied() == Player.YOU)
					initial = new HexPoint(7, 'f');
				else
					initial = new HexPoint(5, 'f');

				solverController.setInitial(initial);

				connectRoute = CONNECT_LETTERS;

				result = toHexMove(initial);
			}
			else
			{
				HexPoint move = null;
				
				try
				{
					move = solverController.getMove();// .toHexPoint();
					new DijkstraBoard(boardController.getIndivBoard(), this);
					
				} catch (Exception e)
				{
					DebugWindow.println("ERROR: CHECK THE STACK TRACE!!!!");
					e.printStackTrace();
				}

				if (move == null)
				{
					DebugWindow.println("WARNING: Resorting to random because we couldn't find a usable path");
					result = chooseRandomPoint(state);
				}
				else
					result = toHexMove(move);
			}

		}

		// apply our move to the board
		point = parseTheirString(result.toString());

//		DebugWindow.println(point.toString());

		boardController.applyMove(point.getX(), point.getY(), Player.ME);
		// /\ == == /\

		return result;
	}
	
	/**
	 * selects a random {@link HexMove}
	 * @param state the current {@link GameState}
	 * @return a random {@link HexMove}
	 */
	public HexMove chooseRandomPoint(GameState state)
	{
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
		return list.get(which);
}

	/**
	 * Converts one of our {@link HexPoint}s in to one of their {@link HexMove}s
	 * @param point The {@link HexPoint} to convert
	 * @return A nice {@link HexMove}
	 */
	public HexMove toHexMove(HexPoint point)
	{
		int x = point.getX() - 1;
		int y = point.getY() - CHARACTER_SUBTRACT - 1;

		return new HexMove(x, y);
	}

	/**
	 * Converts the {@link String} we got with the last move to a {@link HexPoint}
	 * @param move A {@link String} in the format of x-y (ie 3-4) where x is the row (starting at 0) and y is the column
	 * @return A {@link HexPoint} following our standards
	 */
	public HexPoint parseTheirString(String move)
	{
		String[] k = move.split("-");
		if (k.length < 2)
			return null;

		int x = Integer.parseInt(k[0]) + 1;
		char y = (char) (Integer.parseInt(k[1]) + CHARACTER_SUBTRACT + 1);

		return new HexPoint(x, y);
	}

	/**
	 * Gets the {@link BoardController} we are using in this game
	 * @return
	 */
	public BoardController getBoardController()
	{
		return boardController;
	}

	/**
	 * Gets the {@link SolverController} we are using in this game
	 * @return the current {@link SolverController}
	 */
	public SolverController getSolverController()
	{
		return solverController;
	}

	/**
	 * gets the connection route we need to take in order to win
	 * @return the current connection direction
	 */
	public int getConnectRoute()
	{
		return connectRoute;
	}
}
