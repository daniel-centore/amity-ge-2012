package solution.solvers.h_search;

import java.util.ArrayList;
import java.util.List;

import solution.board.BoardInterface;
import solution.board.HexPoint;
import solution.board.Player;
import solution.board.PointUtilities;
import solution.board.implementations.indivBoard.IndivBoard;
import solution.solvers.AmitySolver;
import solution.solvers.WeightedPoint;

/**
 * One method of finding if we have a unblockable connection to the other side of the board
 * A Virtual Connection (VC) is an unblockable path from one point to another
 * A Semi-Virtual Connection(SVC) is a path that if it is not our turn, the opponent can block the connection
 * @author Mike DiBuduo
 * @author Scott DellaTorre
 */
public class HSearch implements AmitySolver
{
	/**
	 * Limit on the number of different minimal virtual
	 * connections with the same ends built by HSearch
	 */
	private static final int M = 1;

	public static void main(String[] args)
	{
		IndivBoard board = new IndivBoard();
		board.applyMove(1, 'a', Player.ME);
		board.applyMove(2, 'b', Player.ME);

		HSearch hey = new HSearch(board);
		hey.hSearch();

		hey.printMe();
	}

	private final BoardInterface board;
	private final List<Connection>[][][][] C, SC;
	private final int N;
	private boolean newVC;

	private void printMe()
	{
		HSearch hey = this;
		for (List<Connection>[][][] start : hey.SC)
		{
			for (List<Connection>[][] start2 : start)
			{
				for (List<Connection>[] start3 : start2)
				{
					for (List<Connection> val : start3)
					{
						for (Connection Connection : val)
						{
							System.out.println(Connection.carriers + " ");
						}
						System.out.println();
					}

				}

			}

		}
	}
	
	public HSearch(BoardInterface board)
	{
		this.board = board;
		N = 11; // TODO PUT BACK LIKE OLD
		// int N = HexState.N;
		C = new List[N][N][N][N];
		SC = new List[N][N][N][N];
	}

	private void addConnection(List<Connection> list, Connection connection)
	{
		if (list.size() < M)
		{
			list.add(connection);
			newVC = true;
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

				if (PointUtilities.areNeighbors(new HexPoint(1 + x1, (char) ('a' + y1)),
						new HexPoint(1 + x2, (char) ('a' + y2))))
				{
					addConnection(C[x1][y1][x2][y2], new Connection(new ArrayList<HexPoint>(), step));
				}
			}
		}

		while (newVC)
		{
			printMe();
			newVC = false;
			step++;
			System.out.println("Step: " + step);

			for (int pos = 0; pos < N * N; pos++)
			{
				int x = pos % N, y = pos / N;
				HexPoint g = new HexPoint(1 + x, (char) ('a' + y));
				boolean gIsMe = board.getNode(g.getX(), g.getY()).getOccupied().equals(Player.ME);

				for (int pos1 = 0; pos1 < N * N; pos1++)
				{

					int x1 = pos1 % N, y1 = pos1 / N;
					HexPoint g1 = new HexPoint(1 + x1, (char) ('a' + y1));

					// If g has my color, g1 must be empty
					if (gIsMe && !board.getNode(g1.getX(), g1.getY()).getOccupied().equals(Player.EMPTY))
					{
						continue;
					}

					// C(g1, g) or C(g2, g) must contain at least one new connection
					boolean containsConnection1 = false;
					for (Connection vc1 : C[x1][y1][x][y])
					{
						if (step - vc1.creationStep <= 1)
						{
							containsConnection1 = true;
							break;
						}
					}

					for (int pos2 = 0; pos2 < N * N; pos2++)
					{
						int x2 = pos2 % N, y2 = pos2 / N;
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

						// C(g1, g) or C(g2, g) must contain at least one new connection
						if (!containsConnection1)
						{
							boolean containsConnection2 = false;
							for (Connection vc2 : C[x2][y2][x][y])
							{
								if (step - vc2.creationStep <= 1)
								{
									containsConnection2 = true;
									break;
								}
							}
							if (!containsConnection2)
							{
								continue;
							}
						}

						for (Connection vc1 : C[x1][y1][x][y])
						{

							// g2 must not be in c1
							if (vc1.carriers.contains(g2))
							{
								continue;
							}

							for (Connection vc2 : C[x2][y2][x][y])
							{

								// g1 must not be in c2
								if (vc2.carriers.contains(g1))
								{
									continue;
								}

								// At least one of the connections c1 or c2 must be new
								if (step - vc1.creationStep > 1 && step - vc2.creationStep > 1)
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
									ArrayList<HexPoint> carriers = new ArrayList<HexPoint>();
									carriers.addAll(vc1.carriers);
									carriers.addAll(vc2.carriers);
									addConnection(C[x1][y1][x2][y2], new Connection(carriers, step));
								}
								else
								{
									ArrayList<HexPoint> carriers = new ArrayList<HexPoint>();
									carriers.addAll(vc1.carriers);
									carriers.addAll(vc2.carriers);
									carriers.add(g);
									Connection vsc = new Connection(carriers, step);

									// If the last update is successful??? (idk what they mean by that)
									// maybe if SC(g1, g2) doesn't already contain vsc, continue???
									if (!SC[x1][y1][x2][y2].contains(vsc))
									{
										applyOrDeductionRuleAndUpdate(C[x1][y1][x2][y2], SC[x1][y2][x2][y2], vsc, vsc, step);
										SC[x1][y1][x2][y2].add(vsc);
									}

								}

							}

						}
					}
				}
			}
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
			Connection u, Connection i, int step)
	{

		for (Connection sc1 : SC)
		{
			ArrayList<HexPoint> carriers = new ArrayList<HexPoint>();
			carriers.addAll(u.carriers);
			carriers.addAll(sc1.carriers);
			Connection u1 = new Connection(carriers, step);

			carriers = new ArrayList<HexPoint>();
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
				addConnection(C, u1);
			}
			else
			{
				List<Connection> SC2 = new ArrayList<Connection>(SC);
				SC2.remove(sc1);
				applyOrDeductionRuleAndUpdate(C, SC2, u1, i1, step);
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
	}

	@Override
	public float getWeight()
	{
		return 1;
	}

	@Override
	public List<WeightedPoint> getPoints()
	{
		hSearch();
		return new ArrayList<WeightedPoint>();

	}
}
