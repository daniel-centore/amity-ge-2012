package solution.board.implementations.dijkstraBoard;

import java.util.ArrayList;
import java.util.List;

import solution.CurrentGame;
import solution.board.HexPoint;
import solution.board.Player;
import solution.board.implementations.indivBoard.IndivBoard;
import solution.debug.DebugWindow;

public class DijkstraBoard
{
	private IndivBoard indivBoard;
	private List<DijkstraNode> nodes = new ArrayList<DijkstraNode>();

	// the four walls
	private DijkstraNode wallA;// = new DijkstraNode(-1, '!');
	private DijkstraNode wallK;// = new DijkstraNode(-1, '!');
	private DijkstraNode wallOne;// = new DijkstraNode(-1, '!');
	private DijkstraNode wallEle;// = new DijkstraNode(-1, '!');

	private static final double ME_WEIGHT = 0;
	private static final double EMPTY_WEIGHT = 1;
	private static final double YOU_WEIGHT = Double.POSITIVE_INFINITY;

	public DijkstraBoard(IndivBoard indivBoard, CurrentGame curr)
	{
		if (curr.getConnectRoute() == CurrentGame.CONNECT_LETTERS)
		{
			wallA = new DijkstraNode(-1, '!', Player.ME);
			wallK = new DijkstraNode(-1, '!', Player.ME);
			wallOne = new DijkstraNode(-1, '!', Player.YOU);
			wallEle = new DijkstraNode(-1, '!', Player.YOU);
		}
		else
		{
			wallA = new DijkstraNode(-1, '!', Player.YOU);
			wallK = new DijkstraNode(-1, '!', Player.YOU);
			wallOne = new DijkstraNode(-1, '!', Player.ME);
			wallEle = new DijkstraNode(-1, '!', Player.ME);
		}

		List<HexPoint> bridges = new ArrayList<HexPoint>();

		this.indivBoard = indivBoard;

		DebugWindow.println("Initializing map");

		HexPoint test = new HexPoint(1, 'a');

		DijkstraNode initial = new DijkstraNode(test.getX(), test.getY(), indivBoard.getNode(test).getOccupied());
		nodes.add(initial);
		createMap(test, bridges, initial);

		// add the walls if we are touching
		for (DijkstraNode n : nodes)
		{
			// No elses because corners can be part of 2

			if (n.getX() == 1)
				makeNeighbors(wallOne, n);

			if (n.getX() == 11)
				makeNeighbors(wallEle, n);

			if (n.getY() == 'a')
				makeNeighbors(wallA, n);

			if (n.getY() == 'k')
				makeNeighbors(wallK, n);
		}

		DebugWindow.println("Done map");
	}

	private DijkstraNode getNode(HexPoint pt)
	{
		for (DijkstraNode node : nodes)
		{
			if (node.getX() == pt.getX() && node.getY() == pt.getY())
				return node;
		}

		return null;
	}

	public synchronized double findDistance(HexPoint a, HexPoint b)
	{
		resetNodes(); // puts them in a clean state for a new test

		DijkstraNode dA = getNode(a);
		DijkstraNode dB = getNode(b);

		markDistances(dA, dB);

		return dB.getWeight();
	}

	private void markDistances(DijkstraNode node, DijkstraNode end)
	{
		for (DijkstraNode neighbor : node.getNeighbors())
		{
			if (!neighbor.isCompleted())
			{
				int edgeWeight = -1;
				// TODO: mark edge weight
				neighbor.setNode(neighbor, neighbor.getWeight());
			}
		}

		node.setCompleted(true);
	}

	private void resetNodes()
	{
		for (DijkstraNode n : nodes)
			n.resetNode();
	}

	public void makeNeighbors(DijkstraNode a, DijkstraNode b)
	{
		a.addNeighbor(b);
		b.addNeighbor(a);
	}

	private void createMap(HexPoint pt, List<HexPoint> bridges, DijkstraNode node)
	{
		for (HexPoint b : pt.touching())
		{
			Player p = indivBoard.getNode(b).getOccupied();
			if (!bridges.contains(b))
			{
				DijkstraNode newNode = new DijkstraNode(b.getX(), b.getY(), indivBoard.getNode(b).getOccupied());

				makeNeighbors(node, newNode);

				bridges.add(b);

				createMap(b, bridges, newNode);
			}
		}
	}

	@Override
	public String toString()
	{
		return "DijkstraBoard [nodes=" + nodes + "]";
	}

	public DijkstraNode getWallA()
	{
		return wallA;
	}

	public DijkstraNode getWallK()
	{
		return wallK;
	}

	public DijkstraNode getWallOne()
	{
		return wallOne;
	}

	public DijkstraNode getWallEle()
	{
		return wallEle;
	}
}
