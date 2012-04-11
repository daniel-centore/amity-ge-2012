package solution.solvers;

import game.GameMove;
import game.GamePlayer;
import game.GameState;
import game.GameState.Who;
import hex.HexMove;
import hex.HexState;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import solution.board.BoardInterface;
import solution.board.HexPoint;
import solution.board.Player;
import solution.board.PointUtilities;
import solution.board.implementations.indivBoard.IndivBoard;
import solution.debug.DebugWindow;

/**
 * 
 * @author Scott DellaTorre
 *
 */
public class HSearchPlayer extends GamePlayer
{
	/**
	 * Limit on the number of different minimal virtual
	 * connections with the same ends built by HSearch
	 */
	private static final int M = 2;
	private final BoardInterface board;
	private final List<Connection>[][][][] C, SC;
	private final int N;
	private List<int[]> currConnections = new ArrayList<>(), prevConnections = new ArrayList<>();

	public double calculateResistance(final boolean home, boolean me)
	{

		hSearch();

		double[][][][] resistance = new double[N][N][N][N];

		List[][] neighbors = new List[N][N];
		for (int i = 0; i < N; i++)
		{
			for (int j = 0; j < N; j++)
			{
				neighbors[i][j] = new ArrayList<int[]>();
			}
		}

		for (int x1 = 0; x1 < N; x1++)
		{
			for (int y1 = 0; y1 < N; y1++)
			{

				HexPoint g1 = new HexPoint(1 + x1, (char) ('a' + y1));

				double r1 = 0;
				Player player1 = board.getNode(g1.getX(), g1.getY()).getOccupied();
				if (player1 == (me ? Player.YOU : Player.ME))
				{
					r1 = Double.POSITIVE_INFINITY;
				}
				else if (player1 == Player.EMPTY)
				{
					r1 = 1;
				}

				for (int x2 = x1; x2 < N; x2++)
				{
					for (int y2 = y1; y2 < N; y2++)
					{

						HexPoint g2 = new HexPoint(1 + x2, (char) ('a' + y2));

						double r2 = 0;
						Player player2 = board.getNode(g2.getX(), g2.getY()).getOccupied();
						if (player2 == (me ? Player.YOU : Player.ME))
						{
							r2 = Double.POSITIVE_INFINITY;
						}
						else if (player2 == Player.EMPTY)
						{
							r2 = 1;
						}

						if (PointUtilities.areNeighbors(g1, g2))
						{
							resistance[x1][y1][x2][y2] = r1 + r2;
							resistance[x2][y2][x1][y1] = r1 + r2;
						}
						else if (!C[x1][y1][x2][y2].isEmpty())
						{
							resistance[x1][y1][x2][y2] = r1 + r2 + 0.5;
							resistance[x2][y2][x1][y1] = r1 + r2 + 0.5;
						}
						else
						{
							resistance[x1][y1][x2][y2] = Double.POSITIVE_INFINITY;
							resistance[x2][y2][x1][y1] = Double.POSITIVE_INFINITY;
							continue;
						}

						neighbors[x1][y1].add(new int[] { x2, y2 });
						neighbors[x2][y2].add(new int[] { x1, y1 });

					}
				}
			}
		}

		hSearch();

		double minResistance = Double.POSITIVE_INFINITY;

		for (int i = 0; i < N; i++)
		{

			final int currI = i;

			final double[][][][] dist = new double[N][N][N][N];

			for (int x1 = 0; x1 < N; x1++)
			{
				for (int y1 = 0; y1 < N; y1++)
				{
					for (int x2 = 0; x2 < N; x2++)
					{
						for (int y2 = 0; y2 < N; y2++)
						{
							if (x1 != x2 || y1 != y2)
							{
								dist[x1][y1][x2][y2] = Double.POSITIVE_INFINITY;
							}
						}
					}
				}
			}

			PriorityQueue<int[]> queue = new PriorityQueue<>(N * N,
					new Comparator<int[]>()
					{

						@Override
						public int compare(int[] a, int[] b)
						{
							if (home)
							{
								return Double.compare(dist[0][currI][a[0]][a[1]], dist[0][currI][b[0]][b[1]]);
							}
							else
							{
								return Double.compare(dist[currI][0][a[0]][a[1]], dist[currI][0][b[0]][b[1]]);
							}
						}
					});
			if (home)
			{
				queue.add(new int[] { 0, i });
			}
			else
			{
				queue.add(new int[] { i, 0 });
			}

			while (!queue.isEmpty())
			{

				int[] indices = queue.remove();
				int x = indices[0], y = indices[1];

				for (int[] indices2 : (ArrayList<int[]>) neighbors[x][y])
				{
					int x2 = indices2[0], y2 = indices2[1];
					if (home && dist[0][i][x][y] + resistance[x][y][x2][y2] < dist[0][i][x2][y2])
					{
						dist[0][i][x2][y2] = dist[0][i][x][y] + resistance[x][y][x2][y2];
						queue.add(new int[] { x2, y2 });
					}
					else if (!home && dist[i][0][x][y] + resistance[x][y][x2][y2] < dist[i][0][x2][y2])
					{
						dist[i][0][x2][y2] = dist[i][0][x][y] + resistance[x][y][x2][y2];
						queue.add(new int[] { x2, y2 });
					}
				}

			}

			for (int j = 0; j < N; j++)
			{
				if (home)
				{
					minResistance = Math.min(minResistance, dist[0][i][N - 1][j]);
				}
				else
				{
					minResistance = Math.min(minResistance, dist[i][0][j][N - 1]);
				}
			}

		}

		return minResistance;

	}

	public static void main(String[] args)
	{
		IndivBoard board = new IndivBoard();
		board.applyMove(1, 'a', Player.ME);
		board.applyMove(2, 'b', Player.ME);

		HSearchPlayer test = new HSearchPlayer(board);
		test.hSearch();

		for (int x1 = 0; x1 < test.N; x1++)
		{
			for (int y1 = 0; y1 < test.N; y1++)
			{
				for (int x2 = x1; x2 < test.N; x2++)
				{
					for (int y2 = y1; y2 < test.N; y2++)
					{
						System.out.println((1 + x1) + "," + (char) ('a' + y1) + " to " + (1 + x2) + "," + (char) ('a' + y2) + ":");
						System.out.println("\tVC:" + test.C[x1][y1][x2][y2]);
						System.out.println("\tVSC:" + test.SC[x1][y1][x2][y2]);
					}
				}
			}
		}
	}

	public HSearchPlayer()
	{
		this(new IndivBoard());
	}

	public HSearchPlayer(BoardInterface board)
	{
		super("HSearch", new HexState(), false);
		this.board = board;
		N = 11; // TODO PUT BACK LIKE OLD
		// int N = HexState.N;
		C = new List[N][N][N][N];
		SC = new List[N][N][N][N];
	}

	private void addVirtualConnection(int x1, int y1, int x2, int y2, Connection connection)
	{
		if (C[x1][y1][x2][y2].size() < M)
		{
			C[x1][y1][x2][y2].add(connection);
			C[x2][y2][x1][y1].add(connection);
			currConnections.add(new int[] { x1, y1, x2, y2 });
		}
	}

	private void addVirtualSemiConnection(int x1, int y1, int x2, int y2, Connection connection)
	{
		if (SC[x1][y1][x2][y2].size() < M)
		{
			SC[x1][y1][x2][y2].add(connection);
			SC[x2][y2][x1][y1].add(connection);
		}
	}

	private void hSearch()
	{
		// int N = HexState.N;
		int step = 0;

		for (int pos1 = 0; pos1 < N * N; pos1++)
		{
			for (int pos2 = 0; pos2 < N * N; pos2++)
			{

				int x1 = pos1 % N, y1 = pos1 / N;
				int x2 = pos2 % N, y2 = pos2 / N;

				C[x1][y1][x2][y2] = new ArrayList<Connection>();
				SC[x1][y1][x2][y2] = new ArrayList<Connection>();
			}
		}

		for (int pos1 = 0; pos1 < N * N; pos1++)
		{
			for (int pos2 = pos1; pos2 < N * N; pos2++)
			{

				int x1 = pos1 % N, y1 = pos1 / N;
				int x2 = pos2 % N, y2 = pos2 / N;

				if (PointUtilities.areNeighbors(new HexPoint(1 + x1, (char) ('a' + y1)),
						new HexPoint(1 + x2, (char) ('a' + y2))))
				{
					addVirtualConnection(x1, y1, x2, y2, new Connection(new ArrayList<HexPoint>(), step));
				}
			}
		}

		while (!currConnections.isEmpty())
		{

			prevConnections.clear();
			prevConnections.addAll(currConnections);
			currConnections.clear();
			step++;
			// System.out.println(step);

			for (int[] indices : prevConnections)
			{

				int x1 = indices[0], y1 = indices[1], x = indices[2], y = indices[3];

				HexPoint g = new HexPoint(1 + x, (char) ('a' + y));
				HexPoint g1 = new HexPoint(1 + x1, (char) ('a' + y1));
				boolean gIsMe = board.getNode(g.getX(), g.getY()).getOccupied().equals(Player.ME);

				// If g has my color, g1 must be empty
				if (gIsMe && !board.getNode(g1.getX(), g1.getY()).getOccupied().equals(Player.EMPTY))
				{
					continue;
				}

				for (int x2 = 0; x2 < N; x2++)
				{

					for (int y2 = 0; y2 < N; y2++)
					{

						HexPoint g2 = new HexPoint(1 + x2, (char) ('a' + y2));

						// g1 must not equal g2
						if (g1.equals(g2))
						{
							continue;
						}

						// If g has my color, g2 must be empty
						if (gIsMe && !board.getNode(g2.getX(), g2.getY()).getOccupied().equals(Player.EMPTY))
						{
							continue;
						}

						for (int i = C[x1][y1][x][y].size() - 1; i >= 0; i--)
						{

							Connection vc1 = C[x1][y1][x][y].get(i);

							// g2 must not be in c1
							if (vc1.carriers.contains(g2))
							{
								continue;
							}

							// c1 must be new
							if (step - vc1.creationStep > 1)
							{
								continue;
							}

							for (int j = C[x2][y2][x][y].size() - 1; j >= 0; j--)
							{

								Connection vc2 = C[x2][y2][x][y].get(j);

								// g1 must not be in c2
								if (vc2.carriers.contains(g1))
								{
									continue;
								}

								// c1 and c2 must not share any points
								for (HexPoint hp : vc1.carriers)
								{
									if (vc2.carriers.contains(hp))
									{
										continue;
									}
								}

								if (board.getNode(g.getX(), g.getY()).getOccupied().equals(Player.ME))
								{
									ArrayList<HexPoint> carriers = new ArrayList<>();
									carriers.addAll(vc1.carriers);
									carriers.addAll(vc2.carriers);
									addVirtualConnection(x1, y1, x2, y2, new Connection(carriers, step));
								}
								else
								{
									ArrayList<HexPoint> carriers = new ArrayList<>();
									carriers.addAll(vc1.carriers);
									carriers.addAll(vc2.carriers);
									carriers.add(g);
									Connection vsc = new Connection(carriers, step);

									// If the last update is successful??? (idk what they mean by that)
									// maybe if SC(g1, g2) doesn't already contain vsc, continue???
									if (!SC[x1][y1][x2][y2].contains(vsc))
									{
										applyOrDeductionRuleAndUpdate(C[x1][y1][x2][y2], SC[x1][y1][x2][y2],
												x1, y1, x2, y2, vsc, vsc, step);
										addVirtualSemiConnection(x1, y1, x2, y2, vsc);
									}

								}

							}

						}

					}
				}

			}

			// for (int pos = 0; pos < N * N; pos++)
			// {
			//
			// int x = pos % N, y = pos / N;
			// HexPoint g = new HexPoint(1 + x, (char) ('a' + y));
			// boolean gIsMe = board.getNode(g.getX(), g.getY()).getOccupied().equals(Player.ME);
			//
			// for (int pos1 = 0; pos1 < N * N; pos1++)
			// {
			//
			// int x1 = pos1 % N, y1 = pos1 / N;
			// HexPoint g1 = new HexPoint(1 + x1, (char) ('a' + y1));
			//
			// // If g has my color, g1 must be empty
			// if (gIsMe && !board.getNode(g1.getX(), g1.getY()).getOccupied().equals(Player.EMPTY))
			// {
			// continue;
			// }
			//
			// // C(g1, g) or C(g2, g) must contain at least one new connection
			// boolean containsConnection1 = false;
			// for (Connection vc1 : C[x1][y1][x][y])
			// {
			// if (step - vc1.creationStep <= 1)
			// {
			// containsConnection1 = true;
			// break;
			// }
			// }
			//
			// for (int pos2 = 0; pos2 < N * N; pos2++)
			// {
			//
			// int x2 = pos2 % N, y2 = pos2 / N;
			// HexPoint g2 = new HexPoint(1 + x2, (char) ('a' + y2));
			//
			// // g1 must not equal g2
			// if (g1.equals(g2))
			// {
			// continue;
			// }
			//
			// // If g has my color, g2 must be empty
			// if (gIsMe && !board.getNode(g2.getX(), g2.getY()).getOccupied().equals(Player.EMPTY))
			// {
			// continue;
			// }
			//
			// // C(g1, g) or C(g2, g) must contain at least one new connection
			// if (!containsConnection1)
			// {
			// boolean containsConnection2 = false;
			// for (Connection vc2 : C[x2][y2][x][y])
			// {
			// if (step - vc2.creationStep <= 1)
			// {
			// containsConnection2 = true;
			// break;
			// }
			// }
			// if (!containsConnection2)
			// {
			// continue;
			// }
			// }
			//
			// for (Connection vc1 : C[x1][y1][x][y])
			// {
			//
			// // g2 must not be in c1
			// if (vc1.carriers.contains(g2))
			// {
			// continue;
			// }
			//
			// for (Connection vc2 : C[x2][y2][x][y])
			// {
			//
			// // g1 must not be in c2
			// if (vc2.carriers.contains(g1))
			// {
			// continue;
			// }
			//
			// // At least one of the connections c1 or c2 must be new
			// if (step - vc1.creationStep > 1 && step - vc2.creationStep > 1)
			// {
			// continue;
			// }
			//
			// // c1 and c2 must not share any points
			// for (HexPoint hp : vc1.carriers)
			// {
			// if (vc2.carriers.contains(hp))
			// {
			// continue;
			// }
			// }
			//
			// if (board.getNode(g.getX(), g.getY()).getOccupied().equals(Player.ME))
			// {
			// ArrayList<HexPoint> carriers = new ArrayList<>();
			// carriers.addAll(vc1.carriers);
			// carriers.addAll(vc2.carriers);
			// addVirtualConnection(x1, y1, x2, y2, new Connection(carriers, step));
			// }
			// else
			// {
			// ArrayList<HexPoint> carriers = new ArrayList<>();
			// carriers.addAll(vc1.carriers);
			// carriers.addAll(vc2.carriers);
			// carriers.add(g);
			// Connection vsc = new Connection(carriers, step);
			//
			// // If the last update is successful??? (idk what they mean by that)
			// // maybe if SC(g1, g2) doesn't already contain vsc, continue???
			// if (!SC[x1][y1][x2][y2].contains(vsc))
			// {
			// applyOrDeductionRuleAndUpdate(C[x1][y1][x2][y2], SC[x1][y1][x2][y2],
			// x1, y1, x2, y2, vsc, vsc, step);
			// SC[x1][y1][x2][y2].add(vsc);
			// }
			//
			// }
			//
			// }
			//
			// }
			// }
			// }
			// }
		}

	}

	/**
	 * Analyzes the SCV's and determines if a VC can be created
	 * If there are two SVC's between two points on the board, a VC is created
	 * @param C The current list of VC's between two points
	 * @param SC the current list of SVC's between two points
	 * @param u union of g, c1, and c2
	 * @param i same as u
	 * @return Whether a new virtual connection was created
	 */
	private void applyOrDeductionRuleAndUpdate(List<Connection> C, List<Connection> SC,
			int x1, int y1, int x2, int y2, Connection u, Connection i, int step)
	{

		for (Connection sc1 : SC)
		{
			ArrayList<HexPoint> carriers = new ArrayList<>();
			carriers.addAll(u.carriers);
			carriers.addAll(sc1.carriers);
			Connection u1 = new Connection(carriers, step);

			carriers = new ArrayList<>();
			for (HexPoint hp : i.carriers)
			{
				if (sc1.carriers.contains(hp))
				{
					carriers.add(hp);
				}
			}
			Connection i1 = new Connection(carriers, step);

			if (i1.carriers.isEmpty())
			{
				addVirtualConnection(x1, y1, x2, y2, u1);
			}
			else
			{
				List<Connection> SC2 = new ArrayList<>(SC);
				SC2.remove(sc1);
				applyOrDeductionRuleAndUpdate(C, SC2, x1, y1, x2, y2, u1, i1, step);
			}

		}

	}

	private class Connection
	{
		private final int creationStep;
		private final List<HexPoint> carriers;

		public Connection(List<HexPoint> points, int step)
		{
			creationStep = step;
			carriers = points;
		}

		public boolean equals(Object other)
		{
			if (other instanceof Connection)
			{
				return carriers.equals(((Connection) other).carriers);
			}
			return false;
		}

		public String toString()
		{
			return "Connection[creationStep=" + creationStep + ", carriers=" + carriers + "]";
		}
	}

	/**
	 * Initializes the player at the beginning of the tournament. This is called
	 * once, before any games are played. This function must return within
	 * the time alloted by the game timing parameters. Default behavior is
	 * to do nothing.
	 */
	public void init()
	{
		DebugWindow.println("Amity: Began Init");

		System.gc(); // let's clean up other people's junk

		DebugWindow.println("Amity: Finished Init");
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

		// Debug prints
		DebugWindow.println("Game Started. Opponent: " + opponent);
		DebugWindow.resetGameTime();
		DebugWindow.resetMoveTime();
		DebugWindow.setUpdate(true);
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
		DebugWindow.println("Game ended. Amity Solver " + (result == -1 ? "lost" : "won")); // its impossible to have a draw....
		DebugWindow.setUpdate(false);
	}

	/**
	 * Called at the end of the tournament. Can be used to do
	 * housekeeping tasks. Default behavior is to do nothing.
	 */
	public void done()
	{
	}

	public static final int CHARACTER_SUBTRACT = 96;

	/**
	 * Converts the {@link String} we got with the last move to a {@link HexPoint}
	 * @param move A {@link String} in the format of x-y (ie 3-4) where x is the row (starting at 0) and y is the column
	 * @return A {@link HexPoint} following our standards
	 */
	public HexPoint parseTheirString(String move)
	{
		String[] k = move.split("-");
		if (k.length != 2)
			return null;

		int x = Integer.parseInt(k[0]) + 1;
		char y = (char) (Integer.parseInt(k[1]) + CHARACTER_SUBTRACT + 1);

		return new HexPoint(x, y);
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
		DebugWindow.resetMoveTime();

		HexPoint point = parseTheirString(lastMove);

		if (point != null)
		{
			board.applyMove(point.getX(), point.getY(), Player.YOU);
		}

		HexPoint best = null;
		double minValue = Double.POSITIVE_INFINITY;
		for (int x = 0; x < N; x++)
		{
			for (char y = 0; y < N; y++)
			{

				if (board.getNode(1 + x, (char) ('a' + y)).getOccupied() == Player.EMPTY)
				{
					board.applyMove(1 + x, (char) ('a' + y), Player.ME);

					double myResistance = calculateResistance(state.who == Who.HOME, true);
					double yourResistance = calculateResistance(state.who == Who.AWAY, false);
					double value = myResistance / yourResistance;

					DebugWindow.println((char) ('a' + y) + "" + (1 + x) + ": " + myResistance + " " + yourResistance + " " + value);
					if (value < minValue)
					{
						minValue = value;
						best = new HexPoint(1 + x, (char) ('a' + y));
					}

					board.applyMove(1 + x, (char) ('a' + y), Player.EMPTY);
				}
			}
		}

		board.applyMove(best.getX(), best.getY(), Player.ME);

		DebugWindow.resetMoveTime();

		return toHexMove(best);
	}
}