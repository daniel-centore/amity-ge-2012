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
	
	public DijkstraBoard(HexPoint test, IndivBoard indivBoard, CurrentGame curr)
	{
		List<HexPoint> bridges = new ArrayList<HexPoint>();
		
		this.indivBoard = indivBoard;
		
		player = indivBoard.getNode(test).getOccupied();
		
		DebugWindow.println("Initializing map");
		
		DijkstraNode initial = new DijkstraNode(test.getX(), test.getY());
		nodes.add(initial);
		createMap(test, bridges, initial);
		
		DebugWindow.println("Done map");
	}
	
	private void createMap(HexPoint pt, List<HexPoint> bridges, DijkstraNode node)
	{
		for (HexPoint b : indivBoard.getNode(pt).getTwoChains())
		{
			Player p = indivBoard.getNode(b).getOccupied();
			if ((p == Player.EMPTY || p == player) && !bridges.contains(b))
			{
				DijkstraNode newNode = new DijkstraNode(b.getX(), b.getY());
				
				node.addBridge(newNode);	// mark eachother as bridges to eachother
				newNode.addBridge(node);
				
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
}
