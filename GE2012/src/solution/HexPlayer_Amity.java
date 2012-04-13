package solution;

import game.GameMove;
import game.GamePlayer;
import game.GameState;
import game.Util;
import hex.HexMove;
import hex.HexState;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Our own custom solver designed to compete in a Hex tornament
 * 
 * @author Mike DiBuduo
 * @author Daniel Centore
 *
 */
public class HexPlayer_Amity extends GamePlayer
{
	private CurrentGame currentGame;
	
	public HexPlayer_Amity(String name)
	{
		super(name, new HexState(), false);
	}

	/**
	 * Initializes the player at the beginning of the tournament. This is called
	 * once, before any games are played. This function must return within
	 * the time alloted by the game timing parameters. Default behavior is
	 * to do nothing.
	 */
	public void init()
	{
		System.gc();	// let's clean up other people's junk
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
		currentGame = new CurrentGame();
		currentGame.init();
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
	}

	/**
	 * Called at the end of the tournament. Can be used to do
	 * housekeeping tasks. Default behavior is to do nothing.
	 */
	public void done()
	{
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
		GameMove result = currentGame.getMove(state, lastMove);
		
		return result;
	}
	
	public static void main(String[] args)
	{
		GamePlayer p = new HexPlayer_Amity("Amity"); // give your player a name here
		p.compete(args, 1);
	}

}

/**
 * A wrapper for our current game so we don't get locked into playing 1-game games in our code
 * 
 * @author Daniel Centore
 * @author Mike DiBuduo
 *
 */
class CurrentGame
{
	public static final int CHARACTER_SUBTRACT = 96; // subtract this from a character to get it's integer value (starting at 1)

	private BoardController boardController; 
	private SolverController solverController;

	//variable for determining which sides are solver is trying to connect
	public static final int CONNECT_NUMBERS = 0; // trying to connect from 1-11 (white)
	public static final int CONNECT_LETTERS = 1; // trying to connect from 'a'-'k' (black)
	private int connectRoute = -1; // a variable that stores the sides we are tring to connect (0 for white, 1 for black)

	/**
	 * Initializes our controllers and such (can't do this until we've established 'this'!)
	 */
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
			solverController.setFirst(me);

			result = toHexMove(me);

			connectRoute = CONNECT_NUMBERS;
		}
		else
		{
			// they just went - time to counter

			// apply their move to our boards
			boardController.applyMove(point.getX(), point.getY(), Player.YOU);

			// calculate our next move

			HexPoint move = null;

			try
			{
				move = solverController.getMove(point);

				if (solverController.getFirst() == null)
					solverController.setFirst(move);
				
			} catch (Exception e)
			{
			}

			if (move == null)
			{
				result = chooseRandomPoint(state);
			}
			else
				result = toHexMove(move);

		}

		// apply our move to the board
		point = parseTheirString(result.toString());

		boardController.applyMove(point.getX(), point.getY(), Player.ME);

		return result;
	}

	/**
	 * Selects a random {@link HexMove}
	 * @param state The current {@link GameState}
	 * @return A random, valid {@link HexMove}
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
	 * Converts one of our {@link HexPoint}s in to a valid {@link HexMove}
	 * @param point The {@link HexPoint} to convert
	 * @return The {@link HexMove} representation of our point
	 */
	public HexMove toHexMove(HexPoint point)
	{
		int x = point.getX() - 1;
		int y = point.getY() - CHARACTER_SUBTRACT - 1;

		return new HexMove(x, y);
	}

	/**
	 * Converts the {@link String} we got with the last move to a {@link HexPoint}
	 * @param move A {@link String} in the format of x-y (ie 3-4) where x is the row (starting at 0) and y is the column(starting at 'a')
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
	 * Gets the {@link BoardController} we are using in this game
	 * @return The current {@link BoardController}
	 */
	public BoardController getBoardController()
	{
		return boardController;
	}

	/**
	 * Gets the {@link SolverController} we are using in this game
	 * @return The current {@link SolverController}
	 */
	public SolverController getSolverController()
	{
		return solverController;
	}

	/**
	 * Gets the connection route we need to take in order to win
	 * @return The current connection direction
	 */
	public int getConnectRoute()
	{
		return connectRoute;
	}

	/**
	 * Sets the connection route for this game
	 * @param connectRoute The sides to connect
	 */
	public void setConnectRoute(int connectRoute)
	{
		this.connectRoute = connectRoute;
	}
}

/**
 * This accepts moves and applies them to all our boards
 * 
 * @author Daniel Centore
 * @author Mike DiBuduo
 *
 */
class BoardController
{
	private List<BoardInterface> boards = new ArrayList<BoardInterface>(); // all the boards in the game

	// List of all the boards (so we can get them separately)
	private IndivBoard indivBoard;

	public BoardController(CurrentGame curr)
	{
		// Add boards here
		boards.add(indivBoard = new IndivBoard());
	}

	/**
	 * Applies a move to the board
	 * @param x The row we apply on
	 * @param y The column we apply on
	 * @param player Which {@link Player} made the move
	 */
	public void applyMove(int x, char y, Player player)
	{
		for (BoardInterface board : boards)
		{
			board.applyMove(x, y, player);
		}
	}

	/**
	 * Gets a list of all the {@link BoardInterface}s in the game
	 * @return boards
	 */
	public List<BoardInterface> getBoards()
	{
		return boards;
	}

	/**
	 * Gets the instance of {@link IndivBoard} for this game
	 * @return indivBoard
	 */
	public IndivBoard getIndivBoard()
	{
		return indivBoard;
	}
}

/**
 * A board used for calculating the dificulty to reach a node using a modification of Dijkstra's algorithm
 * Distance between nodes plays a major factor in calculating the difficulty
 * Certain patterns are weighted higher in order to avoid them
 * 
 * @author Daniel Centore
 * @author Mike DiBuduo
 *
 */
class DijkstraBoard
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

/**
 * A node used in Dijkstra's algorithm
 * 
 * @author Daniel Centore
 *
 */
class DijkstraNode
{
	private List<DijkstraNode> touching = new ArrayList<DijkstraNode>(); // All the nodes touching this one
	
	// Our position and occupier
	private int x;
	private char y;
	private Player player;
	
	// Info for Dijkstra's
	private DijkstraNode from = null;
	private double weight = Double.MAX_VALUE;
	private boolean completed = false;
	
	/**
	 * Create a node for Dijkstra's algorithm
	 * @param x The X location
	 * @param y The Y location
	 * @param player The occupying {@link Player} (with some quirks for bridges)
	 */
	protected DijkstraNode(int x, char y, Player player)
	{
		this.x = x;
		this.y = y;
		
		this.player = player;
	}
	
	
	/**
	 * Tries to set a new weight for this node
	 * @param from Where the path is coming from (used for debug)
	 * @param weight The new weight to add
	 * @return True if we applied it (it is lower than the current); False otherwise
	 */
	protected boolean setNode(DijkstraNode from, double weight)
	{
		if (weight < this.weight)
		{
			this.weight = weight;
			this.from = from;
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Resets the node so we can do another round of difficulty finding
	 */
	protected void resetNode()
	{
		from = null;
		weight = Double.MAX_VALUE;
		completed = false;
	}
	
	/**
	 * Marks a node as a neighbor of this one
	 * @param node The {@link DijkstraNode} to add
	 */
	protected void addNeighbor(DijkstraNode node)
	{
		touching.add(node);
	}

	/**
	 * Gets all neighbors of this node (including walls)
	 * @return The {@link List} of neighbors
	 */
	protected List<DijkstraNode> getNeighbors()
	{
		return touching;
	}

	/**
	 * Gets the X value of the node
	 * @return The X value
	 * @throws RuntimeException If you try to get the value of a wall
	 */
	protected int getX()
	{
		if (x < 0)
			throw new RuntimeException("DONT GET THE VALUE OF A WALL!!");
		
		return x;
	}

	/**
	 * Gets the Y value of the node
	 * @return The Y value
	 * @throws RuntimeException If you try to get the value of a wall
	 */
	protected char getY()
	{
		if (x < 0)
			throw new RuntimeException("DONT GET THE VALUE OF A WALL!!");
		
		return y;
	}

	@Override
	public String toString()
	{
		return "DijkstraNode [x=" + x + ", y=" + y + "]";
	}

	/**
	 * Are we done with this node in Dijkstra's?
	 * @return True if we are; False otherwise
	 */
	protected boolean isCompleted()
	{
		return completed;
	}

	/**
	 * Marks a node as having been traversed or not
	 * @param completed True to mark it so; False otherwise
	 */
	protected void setCompleted(boolean completed)
	{
		this.completed = completed;
	}

	/**
	 * Gets the node which led to this one on the path (or null for initial)
	 * @return The {@link DijkstraNode} that led to this one
	 */
	protected DijkstraNode getFrom()
	{
		return from;
	}

	/**
	 * Gets the weight that this node was assiged
	 * @return The weight that the path to this node is
	 */
	protected double getWeight()
	{
		return weight;
	}

	/**
	 * Gets the {@link Player} currently occupying this space
	 * @return The {@link Player}
	 */
	protected Player getPlayer()
	{
		return player;
	}

	/**
	 * Sets the {@link Player} currently occupying this space
	 * @param player The {@link Player} to set it to
	 */
	protected void setPlayer(Player player)
	{
		this.player = player;
	}
}

/**
 * This is a board where every single spot has a representation
 * 
 * @author Daniel Centore
 *
 */
class IndivBoard implements BoardInterface
{
	public HashMap<HexPoint, IndivNode> map = new HashMap<HexPoint, IndivNode>();	// map of the points on our grid
	
	/**
	 * Creates a new HexPoint for all points on a regular 11x11 board
	 */
	public IndivBoard()
	{
		for (int i = 1; i <= 11; i++)
		{
			for (char c = 'a'; c <= 'k'; c++)
				map.put(new HexPoint(i, c), new IndivNode(i, c));
		}
	}
	
	/**
	 * Gets the {@link IndivNode} at a certain position
	 * @param point A {@link HexPoint} representing the position
	 * @return The {@link IndivNode} at the position
	 */
	public IndivNode getNode(HexPoint point)
	{
		return (IndivNode) getNode(point.getX(), point.getY());
	}
	
	/**
	 * Gets a {@link Collection} of all the {@link IndivNode}s in the board
	 * @return The {@link Collection}
	 */
	public Collection<IndivNode> getPoints()
	{
		return map.values();
	}

	@Override
	public NodeInterface getNode(int x, char y)
	{
		return map.get(new HexPoint(x, y));
	}

	@Override
	public void applyMove(int x, char y, Player player)
	{
		map.get(new HexPoint(x, y)).setOccupied(player);
	}

	@Override
	public String toString()
	{
		return "IndivBoard [map=" + map + "]";
	}

}

/**
 * Represents an individual spot on an {@link IndivBoard}
 * 
 * @author Daniel Centore
 * @author Mike DiBuduo
 *
 */
class IndivNode implements NodeInterface
{
	private int x; // our 'x' location
	private char y; // our 'y' location
	private Player occupied; // who currently owns the space
	private List<HexPoint> points; // only has one

	/**
	 * Creates an individual spot which is unowned
	 * @param x The x location
	 * @param y The y location
	 */
	public IndivNode(int x, char y)
	{
		this(x, y, Player.EMPTY);
	}

	/**
	 * Creates an individual spot which is occupied by a {@link Player}
	 * @param x The x location
	 * @param y The y location
	 * @param occupied Who owns the space
	 */
	public IndivNode(int x, char y, Player occupied)
	{
		this.x = x;
		this.y = y;
		this.occupied = occupied;

		points = new ArrayList<HexPoint>();
		points.add(new HexPoint(x, y));
		
	}

	
	@Override
	public List<HexPoint> getPoints()
	{
		return points;
	}

	/**
	 * Gets the row for this node
	 * @return The 'x' location of this spot
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * gets the column for this node
	 * @return The 'y' location of this spot
	 */
	public char getY()
	{
		return y;
	}

	@Override
	public Player getOccupied()
	{
		return occupied;
	}

	@Override
	public String toString()
	{
		return "IndivNode [x=" + x + ", y=" + y + ", occupied=" + occupied + "]";
	}

	/**
	 * Sets who owns this square
	 * @param occupied The {@link Player} who occupies this square
	 * @throws RuntimeException If we try to set it to {@link Player#EMPTY}
	 */
	public void setOccupied(Player occupied)
	{
		this.occupied = occupied;
	}

	/**
	 * Finds all possible locations that a two-chain can be made from this {@link IndivNode}
	 * @return a list of possible two-chain locations
	 */
	public List<HexPoint> getTwoChains()
	{
		List<HexPoint> twoChains = new ArrayList<HexPoint>();
		
		HexPoint[] chains = new HexPoint[6];

		chains[0] = new HexPoint(x + 1, (char) (y - 2));
		chains[1] = new HexPoint(x + 2, (char) (y - 1));
		chains[2] = new HexPoint(x + 1, (char) (y + 1));
		chains[3] = new HexPoint(x - 1, (char) (y + 2));
		chains[4] = new HexPoint(x - 2, (char) (y + 1));
		chains[5] = new HexPoint(x - 1, (char) (y - 1));
		
		for (HexPoint h : chains)
		{
			if (h.isGood())
				twoChains.add(h);
		}
		
		return twoChains;
	}

	/**
	 * Checks to see if all of the points in an {@link List<E>} are unoccupied
	 * @param connections The {@link List<E>} to check
	 * @param board The current {@link IndivBoard}
	 * @return True if all are unoccupied, false if any one {@link HexPoint} is occupied
	 */
	public static boolean empty(List<HexPoint> connections, IndivBoard board)
	{
		for (HexPoint p : connections)
		{
			if (board.getNode(p).getOccupied() != Player.EMPTY)
				return false;
		}
		
		return true;
	}

}

/**
 * This is the main solver class which links some other solver info
 * Vocabulary: 
 * two-chain: This occurs when there are two empty points between two spaces
 *			  or between a point and the wall. When there is a two-chain,
 *			  we can guarantee a connection between the two points or a 
 *			  connection to the wall.
 * 
 * @author Daniel Centore
 * @author Mike DiBuduo
 *
 */
class SolverController
{
	protected CurrentGame curr; // Current game
	protected IndivBoard indivBoard;
	protected DijkstraBoard dijkstraBoard = null;
	private ClassicBlock classicBlock;
	private HexPoint first = null; // the first move we made (so we can calculate left and right sides correctly)
	protected MapTools mapTools = new MapTools();
	private FollowChain followChain;

	/**
	 * Creates a new {@link SolverController}
	 * @param curr The {@link CurrentGame}
	 */
	public SolverController(CurrentGame curr)
	{
		this.curr = curr;
		indivBoard = curr.getBoardController().getIndivBoard();
		classicBlock = new ClassicBlock(indivBoard, curr);
		followChain = new FollowChain(this);
	}

	/**
	 * Chooses our next move
	 * @param lastMove The last move made by the opponent
	 * @return The next {@link HexPoint} to occupy
	 */
	public HexPoint getMove(HexPoint lastMove)
	{
		HexPoint broken = null;

		try
		{
			// Fix chains between a point and the wall if necessary
			broken = baseTwoChainsBroken(false, false);
			if (broken != null)
				return broken;
		} catch (Exception e)
		{
			// Fails on some corner cases. just ignore this.
		}

		// Fix chains between points if necessary
		try
		{
			broken = mapTools.twoChainsBroken(this, false);
			if (broken != null)
				return broken;
		} catch (Exception e2)
		{
		}

		try
		{
			// Does a classic block if necessary
			if (classicBlock.shouldBlock())
			{
				broken = classicBlock.block(lastMove);

				if (broken != null)
					return broken;
			}
		} catch (Exception e1)
		{
		}

		if (mapTools.across(this, true) && mapTools.across(this, false))
		{
			// start filling in pieces because we've completed an almost-guaranteed connection of 2-chains and it was
			// (hopefully) not disrupted during the last move
			try
			{
				broken = fillWall();

				if (broken != null)
					return broken;
			} catch (Exception e1)
			{
			}

			try
			{
				broken = fillSpaces();

				if (broken != null)
					return broken;
			} catch (Exception e1)
			{
			}
		}

		dijkstraBoard = new DijkstraBoard(indivBoard, curr); // create our dijkstra's board

		try
		{
			// grab immediate 1-step fixes
			broken = immediatePoint();
			if (broken != null)
				return broken;
		} catch (Exception e)
		{
		}

		// follow chain down/across board
		return followChain.followChain(this, lastMove);
	}

	/**
	 * Completes the next 2-chain
	 * @return The {@link HexPoint} to apply or null if there are no applicable chains
	 */
	private HexPoint fillSpaces()
	{
		return mapTools.twoChainsBroken(this, true);
	}

	/**
	 * If we've already established a 2-chain across then fill in the wall nodes
	 */
	private HexPoint fillWall()
	{
		boolean leftWall = false;
		boolean rightWall = false;

		// checks if we've fully contacted the wall yet
		for (IndivNode node : indivBoard.getPoints())
		{
			if (node.getOccupied() == Player.ME)
			{
				if (curr.getConnectRoute() == CurrentGame.CONNECT_LETTERS)
				{
					if (node.getY() == 'a')
						leftWall = true;
					if (node.getY() == 'k')
						rightWall = true;
				}
				else
				{
					if (node.getX() == 1)
						leftWall = true;
					if (node.getY() == 11)
						rightWall = true;
				}
			}
		}

		// Puts baseTwoChains into force mode so it doesn't need the bridge to be broken to apply the move

		if (!leftWall)
		{
			HexPoint broken = baseTwoChainsBroken(true, true);
			if (broken != null)
				return broken;
		}

		if (!rightWall)
		{
			HexPoint broken = baseTwoChainsBroken(true, false);
			if (broken != null)
				return broken;
		}

		return null; // all bases connected or failed
	}

	/**
	 * Looks around and sees if there is a point which when put down will connect us with an edge either
	 *    by bridge or direct connection
	 * @return The {@link HexPoint} if there is one; Null if not
	 */
	private HexPoint immediatePoint()
	{
		for (IndivNode node : indivBoard.getPoints())
		{
			if (node.getOccupied() == Player.ME)
			{
				for (HexPoint around : node.getPoints().get(0).touching())
				{
					if (mapTools.connectedToWall(this, around) && indivBoard.getNode(around).getOccupied() == Player.EMPTY)
					{
						boolean left = false;
						if ((curr.getConnectRoute() == CurrentGame.CONNECT_LETTERS && around.getY() < 'f') ||
								(curr.getConnectRoute() == CurrentGame.CONNECT_NUMBERS && around.getX() < 6))
							left = true;

						if (!mapTools.across(this, left))
							return around;
					}
				}
			}
		}

		return null;
	}

	/**
	 * Checks if a two-chain is broken
	 * @param a The start node
	 * @param b The end node
	 * @return True if it is BROKEN. False if all's good.
	 */
	protected boolean broken(HexPoint a, HexPoint b)
	{
		List<HexPoint> conns = a.connections(b);

		return !IndivNode.empty(conns, indivBoard);
	}

	/**
	 * Checks if two-chains to the walls are broken
	 * @param force True if we want to force a connection to the wall, false if not
	 * @param left Are we on the left side? 
	 * @return The {@link HexPoint} needed to fix a broken two-chain between a point and the wall (or null if none are broken)
	 */
	private HexPoint baseTwoChainsBroken(boolean force, boolean left)
	{
		niceloop: for (IndivNode node : indivBoard.getPoints())
		{
			if (node.getOccupied() == Player.ME)
			{
				HexPoint pt = node.getPoints().get(0);

				HexPoint[] bad = { new HexPoint(11, 'b'), new HexPoint(1, 'j'), new HexPoint(2, 'k'), new HexPoint(10, 'a') };

				// Skip the corners - We can't two-chain here!
				for (HexPoint b : bad)
				{
					if (node.equals(b))
						continue niceloop;
				}

				// Basically checks the appropriate positions to see if one is broken
				// Very ugly and fragile code. Be careful! Things are repeated over and over again with small changes!
				if (curr.getConnectRoute() == CurrentGame.CONNECT_LETTERS && (pt.getY() == 'b' || pt.getY() == 'j'))
				{
					if (pt.getY() == 'b')
					{
						NodeInterface first = indivBoard.getNode(pt.getX(), 'a');
						NodeInterface second = indivBoard.getNode(pt.getX() + 1, 'a');

						if (first.getOccupied() == Player.YOU || (force && left))
						{
							if (second.getOccupied() == Player.EMPTY)
								return second.getPoints().get(0);
						}

						if (second.getOccupied() == Player.YOU)
						{
							if (first.getOccupied() == Player.EMPTY)
								return first.getPoints().get(0);
						}

					}
					else
					{
						NodeInterface first = indivBoard.getNode(pt.getX(), 'k');
						NodeInterface second = indivBoard.getNode(pt.getX() - 1, 'k');

						if ((first.getOccupied() == Player.YOU) || (force && !left))
						{
							if ((second.getOccupied() == Player.EMPTY))
								return second.getPoints().get(0);
						}

						if ((second.getOccupied() == Player.YOU))
						{
							if ((first.getOccupied() == Player.EMPTY))
								return first.getPoints().get(0);
						}
					}
				}
				else if (curr.getConnectRoute() == CurrentGame.CONNECT_NUMBERS && (pt.getX() == 2 || pt.getX() == 10))
				{
					if (pt.getX() == 2)
					{
						NodeInterface first = indivBoard.getNode(1, pt.getY());
						NodeInterface second = indivBoard.getNode(1, (char) (pt.getY() + 1));

						if ((first.getOccupied() == Player.YOU) || force && !left)
						{
							if ((second.getOccupied() == Player.EMPTY))
								return second.getPoints().get(0);
						}

						if (second.getOccupied() == Player.YOU)
						{
							if ((first.getOccupied() == Player.EMPTY))
								return first.getPoints().get(0);
						}
					}
					else
					{
						NodeInterface first = indivBoard.getNode(11, pt.getY());
						NodeInterface second = indivBoard.getNode(11, (char) (pt.getY() - 1));

						if ((first.getOccupied() == Player.YOU) || (force && left))
						{
							if ((second.getOccupied() == Player.EMPTY))
								return second.getPoints().get(0);
						}

						if ((second.getOccupied() == Player.YOU))
						{
							if ((second.getOccupied() == Player.EMPTY))
								return first.getPoints().get(0);
						}
					}
				}
			}
		}

		return null;
	}

	/**
	 * Gets the first point issued on the board
	 * @return The first point (or null if we haven't gone yet)
	 */
	public HexPoint getFirst()
	{
		return first;
	}

	/**
	 * Sets the first point
	 * @param first The first point
	 */
	public void setFirst(HexPoint first)
	{
		this.first = first;
	}

}

/**
 * Implements the classic block from http://www.hexwiki.org/index.php?title=Basic_%28strategy_guide%29
 * 
 * @author Daniel Centore
 *
 */
class ClassicBlock
{
	private int part = 1; // which of the four parts we're on

	private IndivBoard indivBoard; // The board we are on
	private CurrentGame curr; // The Current Game
	private HexPoint initial; // The initial move made by the enemy
	private HexPoint[] blockPoints = new HexPoint[4]; // The pattern we will be using based on situation

	// Move lists for different starting situations
	// These are all relative. We calculate our actual move list on the fly based on these templates
	// Format: initial, move1, move2, 3, 4
	private HexPoint[] groupA = { new HexPoint(6, 'f'), new HexPoint(3, 'h'), new HexPoint(3, 'g'), new HexPoint(4, 'i'), new HexPoint(4, 'e') };
	private HexPoint[] groupB = { new HexPoint(6, 'k'), new HexPoint(7, 'h'), new HexPoint(8, 'h'), new HexPoint(5, 'i'), new HexPoint(9, 'i') };
	private HexPoint[] groupC = { new HexPoint(7, 'a'), new HexPoint(6, 'd'), new HexPoint(5, 'd'), new HexPoint(8, 'c'), new HexPoint(4, 'c') };
	private HexPoint[] groupD = { new HexPoint(1, 'f'), new HexPoint(4, 'd'), new HexPoint(4, 'e'), new HexPoint(3, 'c'), new HexPoint(3, 'g') };

	/**
	 * Creates a controller for making a classic block (as an initial defence)
	 * @param indivBoard The {@link IndivBoard} being used
	 * @param curr The current game
	 */
	public ClassicBlock(IndivBoard indivBoard, CurrentGame curr)
	{
		this.indivBoard = indivBoard;
		this.curr = curr;
	}

	/**
	 * Finds the next move to put down
	 * @param lastMove The move the last player took
	 * @return The {@link HexPoint} to apply (or null to skip to our next algorithm)
	 */
	public HexPoint block(HexPoint lastMove)
	{
		// Intialize stuff if we're just starting
		if (part == 1)
		{
			initialize();
		}

		HexPoint pt = null;

		if (part == 1)
		{
			// Do our first move if they just put down an initial move
			pt = blockPoints[part - 1];
		}
		else
		{
			// Decides which move to put down next and whether or not they are appropriate using black magic and voodoo
			for (int i = 1; i < blockPoints.length; i++)
			{
				if (blockPoints[i] != null && blockPoints[i].isGood() && blockPoints[i].touching().contains(lastMove))
				{
					boolean good = true;
					for (HexPoint p : indivBoard.getNode(blockPoints[i]).getTwoChains())
					{
						if (indivBoard.getNode(p).getOccupied() == Player.ME)
						{
							List<HexPoint> conns = p.connections(blockPoints[i]);
							if (indivBoard.getNode(conns.get(0)).getOccupied() == Player.YOU)
							{
								blockPoints[i] = null;
								pt = conns.get(1);
								good = false;
							}
							else if (indivBoard.getNode(conns.get(1)).getOccupied() == Player.YOU)
							{
								blockPoints[i] = null;
								pt = conns.get(0);
								good = false;
							}
						}
					}

					if (good)
					{
						pt = blockPoints[i];
					}
				}
			}

			if (pt == null)
			{
				pt = blockPoints[part - 1];
			}
		}

		// Decides when it's time to really give up on the classic block or put in one last effort
		while (pt == null || !pt.isGood() || indivBoard.getNode(pt).getOccupied() != Player.EMPTY)
		{
			part++;

			if (part == 3 && blockPoints[1] != null)
			{
				part = 50;
				return null;
			}
			
			
			if (part == 4 && (blockPoints[2] == null || indivBoard.getNode(blockPoints[2]).getOccupied() == Player.YOU))
			{
				part = 50;
				return null;
			}
			
			if (part > 4)
			{
				part = 50;
				return null;
			}
			else
			{
				pt = blockPoints[part - 1];
			}
		}

		if (pt == blockPoints[3] && blockPoints[1] != null)
		{
			part = 50;
			return null;
		}

		// Remove the point we are about to use. It will no longer be an option.
		for (int i = 0; i < blockPoints.length; i++)
		{
			if (blockPoints[i] == pt)
				blockPoints[i] = null;
		}

		part++; // Move on to the next step

		return pt;
	}

	/**
	 * Initializes our data for the first move
	 */
	private void initialize()
	{
		// Find their initial point (they should definitely have one the 1st time this gets called)
		for (IndivNode node : indivBoard.getPoints())
		{
			if (node.getOccupied() == Player.YOU)
			{
				initial = new HexPoint(node.getX(), node.getY());
			}
		}

		if (initial == null)
			throw new RuntimeException("THEY HAD NO INITIAL POINT!!! WHY ARE WE DOING A CLASSIC BLOCK!?!?");

		// Figure out which group of points makes the most sense for our current situation
		HexPoint[] group = null;

		if (curr.getConnectRoute() == CurrentGame.CONNECT_LETTERS && initial.getX() < 6)
		{
			group = groupA;
		}
		else if (curr.getConnectRoute() == CurrentGame.CONNECT_NUMBERS && initial.getY() < 'f')
		{
			group = groupB;
		}
		else if (curr.getConnectRoute() == CurrentGame.CONNECT_NUMBERS && initial.getY() >= 'f')
		{
			group = groupC;
		}
		else if (curr.getConnectRoute() == CurrentGame.CONNECT_LETTERS && initial.getX() >= 6)
		{
			group = groupD;
		}

		// Now calculate the new set of points based on the differences in theirs
		for (int i = 0; i < 4; i++)
		{
			int xDiff = group[0].getX() - group[i + 1].getX();
			int yDiff = group[0].getY() - group[i + 1].getY();

			blockPoints[i] = new HexPoint(initial.getX() + xDiff, (char) (initial.getY() + yDiff));
		}
	}

	/**
	 * Finds out if we should still be using this block.
	 * Will return true iff
	 *  1. We havn't completed the block or given up or
	 *  2. We were the first player to go
	 * 
	 * @param lastMove The last move the other player made
	 * @return True if we should use it; False otherwise 
	 */
	public boolean shouldBlock()
	{
		// Some initialization (should ideally be somewhere else...)
		if (curr.getConnectRoute() < 0)
			curr.setConnectRoute(CurrentGame.CONNECT_LETTERS);

		return (part <= 4 && curr.getConnectRoute() == CurrentGame.CONNECT_LETTERS);
	}

}

/**
 * Solver which has us push towards both sides using weighted chains of nodes
 * 
 * @author Daniel Centore
 * @author Mike DiBuduo
 *
 */
class FollowChain
{
	private SolverController solverController;

	public FollowChain(SolverController solverController)
	{
		this.solverController = solverController;
	}

	/**
	 * Gets the next {@link HexPoint} in order to follow a two-chain across the board
	 * @param solverController The current {@link SolverController}
	 * @param lastMove The last move made by the opponent
	 * @return The next {@link HexPoint} to continue the chain
	 */
	protected HexPoint followChain(SolverController solverController, HexPoint lastMove)
	{
		List<HexPoint> possible = new ArrayList<HexPoint>();

		// Add all points which are two-chains from one of our own pieces
		for (IndivNode node : solverController.indivBoard.getPoints())
		{
			if (node.getOccupied() == Player.ME)
			{
				List<HexPoint> chains = node.getTwoChains();
				for (HexPoint pnt : chains)
				{
					if (solverController.indivBoard.getNode(pnt).getOccupied() == Player.EMPTY && IndivNode.empty(pnt.connections(node.getPoints().get(0)), solverController.indivBoard))
					{
						possible.add(pnt);
					}
				}

				List<HexPoint> touching = node.getPoints().get(0).touching();
				for (HexPoint pnt : touching)
				{
					if (solverController.indivBoard.getNode(pnt).getOccupied() == Player.EMPTY)
					{
						possible.add(pnt);
					}
				}
			}
		}

		Iterator<HexPoint> itr = possible.iterator();

		double left = Double.MAX_VALUE;
		double leftLast = -1;
		HexPoint bestLeft = null;

		double right = Double.MAX_VALUE;
		double rightLast = -1;
		HexPoint bestRight = null;

		if (!itr.hasNext())
			return null;

		// Find the best move for connecting to both the left and right walls
		do
		{
			HexPoint h = itr.next();

			double leftDist = calculateDistance(solverController, h, true);
			double rightDist = calculateDistance(solverController, h, false);

			if (leftDist < left)
			{
				bestLeft = h;
				leftLast = solverController.dijkstraBoard.findDistance(lastMove, h);
				left = leftDist;
			}

			if (rightDist < right)
			{
				bestRight = h;
				rightLast = solverController.dijkstraBoard.findDistance(lastMove, h);
				right = rightDist;
			}

		} while (itr.hasNext());

		// If we have completed a side already, just go for the other one
		if (solverController.mapTools.across(solverController, true) && bestRight != null)
			return bestRight;
		else if (solverController.mapTools.across(solverController, false) && bestLeft != null)
			return bestLeft;

		// Choose the piece which is *weaker* so that we strengthen the link with that wall
		left += within(bestLeft, 2);
		right += within(bestRight, 2);

		left /= leftLast;
		right /= rightLast;

		if (left > right && bestLeft != null)
			return bestLeft;
		else
			return bestRight;
	}

	/**
	 * Counts how many enemy hexes are in a certain radius from one of our points
	 * @param ours The {@link HexPoint} representing where we are
	 * @param within The radius of nodes to look around it
	 * @return The number of enemy hexes in the radius
	 */
	protected int within(HexPoint ours, int within)
	{
		int result = 0;
		for (IndivNode node : solverController.indivBoard.getPoints())
		{
			int x1 = node.getX();
			int y1 = node.getY() - CurrentGame.CHARACTER_SUBTRACT;

			int x2 = ours.getX();
			int y2 = ours.getY() - CurrentGame.CHARACTER_SUBTRACT;

			double dist = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));

			if (dist <= within && node.getOccupied() == Player.YOU)
				result++;
		}

		return result;
	}

	/**
	 * Figures out how hard it would be to get to the closest wall
	 * @param solverController Our {@link SolverController}
	 * @param pnt The starting {@link HexPoint}
	 * @param left Are we on the left side?
	 * @return The difficulty (arbitrary scale)
	 */
	protected double calculateDistance(SolverController solverController, HexPoint pnt, boolean left)
	{

		DijkstraNode wall;

		if (solverController.curr.getConnectRoute() == CurrentGame.CONNECT_LETTERS)
		{
			if (left)
				wall = solverController.dijkstraBoard.getWallA();
			else
				wall = solverController.dijkstraBoard.getWallK();
		}
		else
		{
			if (left)
				wall = solverController.dijkstraBoard.getWallOne();
			else
				wall = solverController.dijkstraBoard.getWallEle();
		}

		double dist = solverController.dijkstraBoard.findDistance(pnt, wall);

		return dist;
	}

}

/**
 * Class which handles functions directly pertaining to the structure of the map (board)
 * 
 * @author Daniel Centore
 *
 */
class MapTools
{

	/**
	 * Checks to see if there are two-chains all the way across the board
	 * Assumes that all points are connected either by bridges or directly touching, so this 
	 *    will fail if we have resorted to random. Our fate is basically determined by then
	 *    anyways.
	 * @param solverController The {@link SolverController} we are linked with
	 * @param left The side to check for
	 * @return True if there is a two-chain path across the board, false if not
	 */
	protected boolean across(SolverController solverController, boolean left)
	{
		boolean a = false;
		boolean b = false;

		for (IndivNode node : solverController.indivBoard.getPoints())
		{
			if (node.getOccupied() == Player.ME)
			{
				if (solverController.curr.getConnectRoute() == CurrentGame.CONNECT_LETTERS)
				{
					if (node.getY() == 'a')
						a = true;
					else if (node.getY() == 'b')
					{
						// check the connection to the wall
						HexPoint[] k = { new HexPoint(node.getX(), 'a'), new HexPoint(node.getX() + 1, 'a') };
						List<HexPoint> good = new ArrayList<HexPoint>();
						for (HexPoint j : k)
						{
							if (j.isGood())
								good.add(j);
						}

						if (IndivNode.empty(good, solverController.indivBoard) && good.size() == 2) // only if both connections are good
							a = true;
					}

					if (node.getY() == 'k')
						b = true;
					else if (node.getY() == 'j')
					{
						// check the connection to the wall
						HexPoint[] k = { new HexPoint(node.getX(), 'k'), new HexPoint(node.getX() - 1, 'k') };

						List<HexPoint> good = new ArrayList<HexPoint>();
						for (HexPoint j : k)
						{
							if (j.isGood())
								good.add(j);
						}

						if (IndivNode.empty(good, solverController.indivBoard) && good.size() == 2) // only if both connections are good
							b = true;
					}
				}
				else
				{
					if (node.getX() == 1)
						a = true;
					else if (node.getX() == 2)
					{
						// check the connection to the wall
						HexPoint[] k = { new HexPoint(1, node.getY()), new HexPoint(1, (char) (node.getY() + 1)) };

						List<HexPoint> good = new ArrayList<HexPoint>();
						for (HexPoint j : k)
						{
							if (j.isGood())
								good.add(j);
						}

						if (IndivNode.empty(good, solverController.indivBoard)) // only if both connections are good
							a = true;
					}

					if (node.getX() == 11)
						b = true;
					else if (node.getX() == 10)
					{
						// check the connection to the wall
						HexPoint[] k = { new HexPoint(11, node.getY()), new HexPoint(11, (char) (node.getY() - 1)) };

						List<HexPoint> good = new ArrayList<HexPoint>();
						for (HexPoint j : k)
						{
							if (j.isGood())
								good.add(j);
						}

						if (IndivNode.empty(good, solverController.indivBoard)) // only if both connections are good
							b = true;
					}
				}
			}
		}

		return (left ? a : b);

	}

	/**
	 * Checks if a hex piece is connected to a *home wall* via bridge or touching
	 * @param solverController The {@link SolverController} we're connected to
	 * @param node The {@link HexPoint} to check
	 * @return True if connected; False otherwise
	 */
	protected boolean connectedToWall(SolverController solverController, HexPoint node)
	{
		if (solverController.curr.getConnectRoute() == CurrentGame.CONNECT_LETTERS)
		{
			if (node.getY() == 'a')
				return true;
			else if (node.getY() == 'b')
			{
				// check the connection to the wall
				HexPoint[] k = { new HexPoint(node.getX(), 'a'), new HexPoint(node.getX() + 1, 'a') };

				List<HexPoint> good = new ArrayList<HexPoint>();
				for (HexPoint j : k)
				{
					if (j.isGood())
						good.add(j);
				}

				if (IndivNode.empty(good, solverController.indivBoard)) // only if both connections are good
					return true;
			}

			if (node.getY() == 'k')
				return true;
			else if (node.getY() == 'j')
			{
				// check the connection to the wall
				HexPoint[] k = { new HexPoint(node.getX(), 'k'), new HexPoint(node.getX() - 1, 'k') };

				List<HexPoint> good = new ArrayList<HexPoint>();
				for (HexPoint j : k)
				{
					if (j.isGood())
						good.add(j);
				}

				if (IndivNode.empty(good, solverController.indivBoard)) // only if both connections are good
					return true;
			}
		}
		else
		{
			if (node.getX() == 1)
				return true;
			else if (node.getX() == 2)
			{
				// check the connection to the wall
				HexPoint[] k = { new HexPoint(1, node.getY()), new HexPoint(1, (char) (node.getY() + 1)) };

				List<HexPoint> good = new ArrayList<HexPoint>();
				for (HexPoint j : k)
				{
					if (j.isGood())
						good.add(j);
				}

				if (IndivNode.empty(good, solverController.indivBoard)) // only if both connections are good
					return true;
			}

			if (node.getX() == 11)
				return true;
			else if (node.getX() == 10)
			{
				// check the connection to the wall
				HexPoint[] k = { new HexPoint(11, node.getY()), new HexPoint(11, (char) (node.getY() - 1)) };

				List<HexPoint> good = new ArrayList<HexPoint>();
				for (HexPoint j : k)
				{
					if (j.isGood())
						good.add(j);
				}

				if (IndivNode.empty(good, solverController.indivBoard)) // only if both connections are good
					return true;
			}
		}

		return false;
	}

	/**
	 * Checks if a two chain has been broken. 
	 * @param solverController {@link SolverController} we're connected to
	 * @return The {@link HexPoint} needed to fix a broken two-chain (or null if none are broken)
	 */
	protected HexPoint twoChainsBroken(SolverController solverController, boolean force)
	{
		for (IndivNode node : solverController.indivBoard.getPoints())
		{
			if (node.getOccupied() == Player.ME)
			{
				List<HexPoint> chains = node.getTwoChains();// indivBoard);
				for (HexPoint pnt : chains)
				{
					if (solverController.indivBoard.getNode(pnt).getOccupied() == Player.ME)
					{
						List<HexPoint> connections = pnt.connections(node.getPoints().get(0));

						if (connections.size() < 2)
							throw new RuntimeException("Size less than 2!");

						HexPoint a = connections.get(0);
						HexPoint b = connections.get(1);

						if (solverController.dijkstraBoard.findDistance(a, b) > 0) // if it's 0, they're already touching somehow. dont waste a turn.
						{
							// if either connector is broken, then cling onto the other
							if (solverController.indivBoard.getNode(a).getOccupied() == Player.YOU && solverController.indivBoard.getNode(b).getOccupied() == Player.EMPTY)
								return b;
							else if (solverController.indivBoard.getNode(b).getOccupied() == Player.YOU && solverController.indivBoard.getNode(a).getOccupied() == Player.EMPTY)
								return a;
							else if (force && IndivNode.empty(connections, solverController.indivBoard))
							{
								// pick one even if we aren't forced to (because we are forced to. ironic, eh?)
								return a;
							}
						}

					}
				}
			}
		}

		return null; // nothing broken. woo!
	}

}

/**
 * Represents a single point on the hex grid
 * This is done by using a 1-11 number system for the rows, and a 'a'-'k' system for the columns
 * 
 * @author Daniel Centore
 * @author Mike DiBuduo
 *
 */
class HexPoint
{
	// Location of our point
	private int x;
	private char y;

	/**
	 * Creates a representation of a single point on a grid
	 * Similar to the {@link Point} class in the Java API
	 * @param x The row we are on
	 * @param y The column we are on
	 */
	public HexPoint(int x, char y)
	{
		this.x = x;
		this.y = y;
	}

	/**
	 * Returns true if its a valid point on a board
	 * @return True if the point is in the 11x11 grid, false if not
	 */
	public boolean isGood()
	{
		int j = y - CurrentGame.CHARACTER_SUBTRACT;
		return (x >= 1 && x <= 11 && j >= 1 && j <= 11);
	}

	/**
	 * Finds the 2 connections to a 2-bridge
	 * @param bridge The second point that must be included in the 2-bridge
	 * @return the 2 {@link HexPoint}s that can complete the two-chain
	 */
	public List<HexPoint> connections(HexPoint bridge)
	{
		List<HexPoint> mine = touching(); //all the points touching me
		List<HexPoint> your = bridge.touching(); //all the points touching the other {@link HexPoint}

		List<HexPoint> result = new ArrayList<HexPoint>();

		// find spots in common
		for (HexPoint h : mine)
		{
			for (HexPoint k : your)
			{
				if (k.equals(h))
					result.add(k);
			}
		}

		return result;
	}

	/**
	 * Generates an array of {@link HexPoint}s next to this {@link HexPoint}
	 * @return An array of all the {@link HexPoint}s touching this {@link HexPoint}
	 */
	public List<HexPoint> touching()
	{
		HexPoint[] hps = new HexPoint[6];

		hps[0] = new HexPoint(x, (char) (y - 1));
		hps[1] = new HexPoint(x + 1, (char) (y - 1));
		hps[2] = new HexPoint(x + 1, y);
		hps[3] = new HexPoint(x, (char) (y + 1));
		hps[4] = new HexPoint(x - 1, (char) (y + 1));
		hps[5] = new HexPoint(x - 1, y);

		List<HexPoint> result = new ArrayList<HexPoint>();
		for (HexPoint h : hps)
		{
			if (h.isGood())
				result.add(h);
		}

		return result;
	}

	/**
	 * Gets the row (integer) of the point
	 * @return x
	 */
	public int getX()
	{
		return x;
	}

	/**
	 * Sets the row
	 * @param x The row to set this point to
	 */
	public void setX(int x)
	{
		this.x = x;
	}

	/**
	 * Gets the column (char) of the point
	 * @return y
	 */
	public char getY()
	{
		return y;
	}

	/**
	 * Sets the column
	 * @param y The column to set this point to
	 */
	public void setY(char y)
	{
		this.y = y;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final HexPoint other = (HexPoint) obj;
		if (this.x != other.x)
		{
			return false;
		}
		if (this.y != other.y)
		{
			return false;
		}

		return true;
	}
	
	@Override
	public String toString()
	{
		return "HexPoint [x=" + x + ", y=" + y + "]";
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

}

/**
 * Represents a node of the board.
 * This is dependent on the type of board.
 * Some may have it represent one position, others may have it represent blobs.
 * 
 * @author Daniel Centore
 * @author Mike DiBuduo
 *
 */
interface NodeInterface
{
	/**
	 * Returns the {@link List} of {@link HexPoint}s that this {@link NodeInterface} contains.
	 * Specifics depend on the type of board
	 * @return A {@link List} of {@link HexPoint}s
	 */
	public List<HexPoint> getPoints();
	
	/**
	 * Returns the {@link Player} that occupies the space
	 * @return {@link Player} that occupies the space currently
	 */
	public Player getOccupied();
	
}

/**
 * Represents the functions that all boards need to be capable of doing
 * 
 * @author Daniel Centore
 *
 */
interface BoardInterface
{
	/**
	 * Returns the {@link NodeInterface} representing this position
	 * @param x The row (int) we are representing
	 * @param y The column (char) we are representing
	 * @return The respective {@link NodeInterface}
	 */
	public NodeInterface getNode(int x, char y);
	
	/**
	 * Applies a move to the board
	 * @param x The row we apply on
	 * @param y The column we apply on
	 * @param player Which {@link Player} made the move
	 */
	public void applyMove(int x, char y, Player player);
}

/**
 * Which player we are talking about (so we don't get locked into a color)
 * 
 * @author Daniel Centore
 *
 */
enum Player
{
	/**
	 * Represents Amity in the game
	 */
	ME,

	/**
	 * Represents our opponent
	 */
	YOU,
	
	/**
	 * Represents an empty hex
	 */
	EMPTY,
	
	/**
	 * ONLY FOR USE IN DIJKSTRAS SOLVER.
	 * BASICALLY A KLUDGE
	 */
	YOU_BRIDGE
}