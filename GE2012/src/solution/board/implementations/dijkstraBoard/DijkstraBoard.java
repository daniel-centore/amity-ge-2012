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
	private Player player;		// the player the board is for
	private List<DijkstraNode> nodes = new ArrayList<DijkstraNode>();
	
	// the four walls
	private DijkstraNode wallA = new DijkstraNode(-1, '!');
	private DijkstraNode wallK = new DijkstraNode(-1, '!');
	private DijkstraNode wallOne = new DijkstraNode(-1, '!');
	private DijkstraNode wallEle = new DijkstraNode(-1, '!');
	
	public DijkstraBoard(HexPoint test, IndivBoard indivBoard, CurrentGame curr)
	{
		List<HexPoint> bridges = new ArrayList<HexPoint>();
		
		this.indivBoard = indivBoard;
		
		player = indivBoard.getNode(test).getOccupied();
		
		DebugWindow.println("Initializing map");
		
		DijkstraNode initial = new DijkstraNode(test.getX(), test.getY());
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
				DijkstraNode newNode = new DijkstraNode(b.getX(), b.getY());
				
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
