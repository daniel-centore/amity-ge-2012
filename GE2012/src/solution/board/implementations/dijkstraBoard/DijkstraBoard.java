package solution.board.implementations.dijkstraBoard;

import java.util.ArrayList;
import java.util.List;

import solution.CurrentGame;
import solution.board.HexPoint;
import solution.board.Player;
import solution.board.implementations.indivBoard.IndivBoard;
import solution.board.implementations.indivBoard.IndivNode;
import solution.debug.DebugWindow;

/**
 * A board used for calculating the dificulty to reach a node using a modification of Dijkstra's algorithm
 * Distance between nodes plays a major factor in calculating the difficulty
 * Certain patterns are weighted higher in order to avoid them
 * 
 * @author Daniel Centore
 * @author Mike DiBuduo
 *
 */
public class DijkstraBoard
{
	private IndivBoard indivBoard;

	private List<DijkstraNode> nodes = new ArrayList<DijkstraNode>();

	// the four walls
	private DijkstraNode wallA;
	private DijkstraNode wallK;
	private DijkstraNode wallOne;
	private DijkstraNode wallEle;

	// Weight's
	private static final double ME_WEIGHT = 0;
	private static final double EMPTY_WEIGHT = 1;
	private static final double YOU_BRIDGE_WEIGHT = 6; // don't attempt to go through an enemy's two-bridge unless things look pretty horrific (/2 because 2 spots)
	private static final double YOU_WEIGHT = 200; // won't overflow but will (almost?) never be the lowest node

	/**
	 * Creates a new {@link DijkstraBoard}.
	 * NOTE: We need to generate a new one each time the {@link IndivBoard} changes
	 * @param indivBoard The {@link IndivBoard} to base our {@link DijkstraNode}s on
	 * @param curr The {@link CurrentGame}
	 */
	public DijkstraBoard(IndivBoard indivBoard, CurrentGame curr)
	{

		this.indivBoard = indivBoard;

		DebugWindow.println("Initializing map");

		// Initialize our map
		HexPoint test = new HexPoint(1, 'a');

		DijkstraNode initial = new DijkstraNode(test.getX(), test.getY(), indivBoard.getNode(test).getOccupied());
		nodes.add(initial);
		createMap();

		// Create the walls
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

		nodes.add(wallA);
		nodes.add(wallK);
		nodes.add(wallOne);
		nodes.add(wallEle);

		// Add the walls if we are touching
		for (DijkstraNode n : nodes)
		{
			if (n == wallA || n == wallK || n == wallOne || n == wallEle)
				continue;

			// No else statements because corners can be part of 2
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

	/**
	 * Gets the {@link DijkstraNode} we want
	 * @param pt The point ( {@link HexPoint} ) that it is located at
	 * @return The {@link DijkstraNode}
	 */
	private DijkstraNode getNode(HexPoint pt)
	{
		for (DijkstraNode node : nodes)
		{
			if (node.getX() == pt.getX() && node.getY() == pt.getY())
				return node;
		}

		return null;
	}

	/**
	 * Finds the dificulty of a connection between two nodes
	 * @param a The first node
	 * @param b The second node
	 * @return The distance (arbitrary units)
	 */
	public synchronized double findDistance(HexPoint a, HexPoint b)
	{
		DijkstraNode dA = getNode(a);
		DijkstraNode dB = getNode(b);

		return findDistance(dA, dB);
	}

	/**
	 * Finds the difficulty of a connection between two nodes
	 * @param a The first node
	 * @param dB The second node
	 * @return The distance (arbitrary units)
	 */
	public synchronized double findDistance(HexPoint a, DijkstraNode dB)
	{
		DijkstraNode dA = getNode(a);

		return findDistance(dA, dB);
	}

	/**
	 * Finds the difficulty of a connection between two nodes
	 * @param dA The first node
	 * @param dB The second node
	 * @return The distance (arbitrary units)
	 */
	public synchronized double findDistance(DijkstraNode dA, DijkstraNode dB)
	{
		resetNodes(); // puts them in a clean state for a new test

		dA.setNode(null, 0);

		// Is end still in the graph?
		while (!dB.isCompleted())
		{
			// Choose the node with the least distance

			double smallestWeight = Double.MAX_VALUE;
			DijkstraNode smallestNode = null;

			for (DijkstraNode n : nodes)
			{
				if (!n.isCompleted() && n.getWeight() <= smallestWeight)
				{
					smallestWeight = n.getWeight();
					smallestNode = n;
				}
			}

			// Remove it from the graph
			smallestNode.setCompleted(true);

			// Calculate distances between it and neighbors that are still in the graph
			// Update distances, choosing the lowest
			for (DijkstraNode n : smallestNode.getNeighbors())
			{
				if (!n.isCompleted())
				{
					double edgeWeight = 500;

					if (n.getPlayer() == Player.ME)
						edgeWeight = ME_WEIGHT;
					else if (n.getPlayer() == Player.EMPTY)
						edgeWeight = EMPTY_WEIGHT;
					else if (n.getPlayer() == Player.YOU)
						edgeWeight = YOU_WEIGHT;
					else if (n.getPlayer() == Player.YOU_BRIDGE)
						edgeWeight = YOU_BRIDGE_WEIGHT;

					n.setNode(smallestNode, smallestNode.getWeight() + edgeWeight);
				}
			}

		}

		return dB.getWeight();
	}

	/**
	 * Resets the node's difficulties so we can run a new algorithm round
	 */
	private void resetNodes()
	{
		for (DijkstraNode n : nodes)
			n.resetNode();
	}

	/**
	 * Marks two nodes as each other's neighbors
	 * @param a The first node
	 * @param b The second node
	 */
	public void makeNeighbors(DijkstraNode a, DijkstraNode b)
	{
		a.addNeighbor(b);
		b.addNeighbor(a);
	}

	/**
	 * Creates the map of nodes based on our current board
	 * Assigns appropriate weights to the nodes
	 */
	private void createMap()
	{
		// Adds all nodes to the board
		for (IndivNode newPoint : indivBoard.getPoints())
		{
			DijkstraNode newNode = new DijkstraNode(newPoint.getX(), newPoint.getY(), newPoint.getOccupied());
			nodes.add(newNode);
		}

		// Recognize the enemy's two-bridges and mark the spaces in-between them as basically not crossable
		for (DijkstraNode node : nodes)
		{
			if (node.getPlayer() == Player.YOU)
			{
				List<HexPoint> bridges = ((IndivNode) indivBoard.getNode(node.getX(), node.getY())).getTwoChains();

				for (HexPoint k : bridges)
				{
					if (indivBoard.getNode(k).getOccupied() == Player.YOU)
					{
						// check to see that the spaces between them pose a hazard
						List<HexPoint> conns = k.connections(new HexPoint(node.getX(), node.getY()));

						int empty = 0;

						for (HexPoint p : conns)
						{
							if (indivBoard.getNode(p).getOccupied() == Player.EMPTY)
								empty++;
						}

						if (empty >= 2) // if they're all empty
						{
							getNode(conns.get(0)).setPlayer(Player.YOU_BRIDGE);
							getNode(conns.get(1)).setPlayer(Player.YOU_BRIDGE);
						}
					}
				}
			}
		}

		// Mark an enemy's corner as their territory
		if (indivBoard.getNode(new HexPoint(10, 'b')).getOccupied() == Player.YOU)
		{
			getNode(new HexPoint(11, 'a')).setPlayer(Player.YOU_BRIDGE);
			getNode(new HexPoint(10, 'a')).setPlayer(Player.YOU_BRIDGE);
		}

		if (indivBoard.getNode(new HexPoint(2, 'j')).getOccupied() == Player.YOU)
		{
			getNode(new HexPoint(1, 'k')).setPlayer(Player.YOU_BRIDGE);
			getNode(new HexPoint(1, 'j')).setPlayer(Player.YOU_BRIDGE);
		}

		// Adds all of a node's neighbors to itself
		for (DijkstraNode node : nodes)
		{
			HexPoint point = new HexPoint(node.getX(), node.getY());

			for (HexPoint touch : point.touching())
			{
				node.addNeighbor(getNode(touch));
			}
		}
	}

	@Override
	public String toString()
	{
		return "DijkstraBoard [nodes=" + nodes + "]";
	}

	/**
	 * Gets the node representing a wall
	 * @return The wall going along the A wall
	 */
	public DijkstraNode getWallA()
	{
		return wallA;
	}

	/**
	 * Gets the node representing a wall
	 * @return The wall going along the K wall
	 */
	public DijkstraNode getWallK()
	{
		return wallK;
	}

	/**
	 * Gets the node representing a wall
	 * @return The wall going along the 1 wall
	 */
	public DijkstraNode getWallOne()
	{
		return wallOne;
	}

	/**
	 * Gets the node representing a wall
	 * @return The wall going along the 11 wall
	 */
	public DijkstraNode getWallEle()
	{
		return wallEle;
	}
}
