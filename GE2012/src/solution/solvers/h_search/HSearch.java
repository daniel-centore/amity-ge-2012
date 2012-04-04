package solution.solvers.h_search;

import hex.HexState;

import java.util.ArrayList;
import java.util.List;

import solution.board.BoardInterface;
import solution.board.HexPoint;
import solution.board.Player;
import solution.board.PointUtilities;
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

	private final BoardInterface board;
	private final List<SpecialHexPoint>[][][][] C, SC;

	public HSearch(BoardInterface board)
	{
		this.board = board;
		int N = HexState.N;
		C = new List[N][N][N][N];
		SC = new List[N][N][N][N];
	}

	private void hSearch()
	{
		int N = HexState.N;
		int step = 0;

		for (int x1 = 0; x1 < N; x1++)
		{
			for (int y1 = 0; y1 < N; y1++)
			{
				for (int x2 = 0; x2 < N; x2++)
				{
					for (int y2 = 0; y2 < N; y2++)
					{
						C[x1][y1][x2][y2] = new ArrayList<SpecialHexPoint>();
						SC[x1][y1][x2][y2] = new ArrayList<SpecialHexPoint>();

						if (PointUtilities.areNeighbors(new HexPoint(x1, (char) ('A' + y1)),
								new HexPoint(x2, (char) ('A' + y2))))
						{
							C[x1][y1][x2][y2].add(new SpecialHexPoint(null, step));
						}
					}
				}

			}
		}

		boolean newVC;
		do
		{
			newVC = false;
			step++;

			for (int x = 0; x < N; x++)
			{
				for (int y = 0; y < N; y++)
				{

					HexPoint g = new HexPoint(x, (char) ('A' + y));

					for (int x1 = 0; x1 < N; x1++)
					{
						for (int y1 = 0; y1 < N; y1++)
						{

							HexPoint g1 = new HexPoint(x1, (char) ('A' + y1));

							for (int x2 = 0; x2 < N; x2++)
							{
								for (int y2 = 0; y2 < N; y2++)
								{

									HexPoint g2 = new HexPoint(x2, (char) ('A' + y2));

									// g1 must not equal g2
									if (g1.equals(g2))
									{
										continue;
									}

									// C(g1, g) or C(g2, g) must contain at least one new carrier
									boolean containsNewCarrier = false;
									for (SpecialHexPoint c1 : (List<SpecialHexPoint>) C[x1][y1][x][y])
									{
										if (step - c1.creationStep <= 1)
										{
											containsNewCarrier = true;
										}
									}
									for (SpecialHexPoint c2 : (List<SpecialHexPoint>) C[x2][y2][x][y])
									{
										if (step - c2.creationStep <= 1)
										{
											containsNewCarrier = true;
										}
									}
									if (!containsNewCarrier)
									{
										continue;
									}

									// If g has your color, g1 and g2 must both be empty
									if (board.getNode(g.getX(), g.getY()).getOccupied().equals(Player.ME))
									{
										if (!board.getNode(g1.getX(), g1.getY()).getOccupied().equals(Player.EMPTY))
										{
											continue;
										}
										if (!board.getNode(g2.getX(), g2.getY()).getOccupied().equals(Player.EMPTY))
										{
											continue;
										}
									}

									for (SpecialHexPoint c1 : (List<SpecialHexPoint>) C[x1][y1][x][y])
									{

										for (SpecialHexPoint c2 : (List<SpecialHexPoint>) C[x2][y2][x][y])
										{

											// At least one of the carriers c1 or c2 must be new
											if (step - c1.creationStep > 1 && step - c2.creationStep > 1)
											{
												continue;
											}

											// c1 and c2 must not be equal (my interpretation might not be correct here)
											if (c1.hexPoint.equals(c2.hexPoint))
											{
												continue;
											}

											// g1 must not equal c2
											if (g1.equals(c2))
											{
												continue;
											}

											// g2 must not equal c1
											if (g2.equals(c1))
											{
												continue;
											}

											if (board.getNode(g.getX(), g.getY()).getOccupied().equals(Player.ME))
											{
												C[x1][y1][x2][y2].add(new SpecialHexPoint(c1.hexPoint, step));
												C[x1][y1][x2][y2].add(new SpecialHexPoint(c2.hexPoint, step));
												newVC = true;
											}
											else
											{
												List<SpecialHexPoint> sc = new ArrayList<SpecialHexPoint>();
												sc.add(new SpecialHexPoint(g, step));
												sc.add(new SpecialHexPoint(c1.hexPoint, step));
												sc.add(new SpecialHexPoint(c2.hexPoint, step));

												SC[x1][y1][x2][y2].addAll(sc);

												// If the last update is successful??? (idk what they mean by that)
												// maybe if SC(g1, g2) doesn't already contain sc, continue???

												List<SpecialHexPoint> scSet = new ArrayList<SpecialHexPoint>(SC[x1][y2][x2][y2]);
												scSet.removeAll(sc);
												if (applyOrDeductionRuleAndUpdate(C[x1][y1][x2][y2], scSet, sc, sc))
												{
													newVC = true;
												}
											}

										}

									}

								}
							}
						}
					}
				}
			}
		} while (newVC);

	}

	/**
	 * Analyzes the SCV's and determines if a VC can be created
	 * If there are two SVC's between two points on the board, a VC is created
	 * @param C The current list of VC's between two points
	 * @param SC the current list of SVC's between two points
	 * @param u //TODO find out what this is
	 * @param i
	 * @return Whether a new virtual connection was created
	 */
	private boolean applyOrDeductionRuleAndUpdate(List<SpecialHexPoint> C, List<SpecialHexPoint> SC,
			List<SpecialHexPoint> u, List<SpecialHexPoint> i)
	{

		boolean newVC = false;

		for (SpecialHexPoint sc1 : SC)
		{
			List<SpecialHexPoint> u1 = new ArrayList<SpecialHexPoint>(u); // union
			u1.add(sc1);

			List<SpecialHexPoint> i1 = new ArrayList<SpecialHexPoint>();
			if (i.contains(sc1))
			{
				i1.add(sc1);
				List<SpecialHexPoint> newSC = new ArrayList<SpecialHexPoint>(SC);
				newSC.remove(sc1);
				if (applyOrDeductionRuleAndUpdate(C, newSC, u1, i1))
				{
					newVC = true;
				}
			}
			else
			{
				C.addAll(u1);
				newVC = true;
			}

		}

		return newVC;

	}

	private class SpecialHexPoint
	{

		private final int creationStep;
		private final HexPoint hexPoint;

		public SpecialHexPoint(HexPoint point, int step)
		{
			creationStep = step;
			hexPoint = point;
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
