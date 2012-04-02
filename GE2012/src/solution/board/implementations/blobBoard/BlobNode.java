package solution.board.implementations.blobBoard;

import java.util.ArrayList;
import java.util.List;

import solution.board.HexPoint;
import solution.board.NodeInterface;
import solution.board.Player;

/**
 * Represents a blob of color
 * 
 * @author Daniel Centore
 *
 */
public class BlobNode implements NodeInterface
{
	private Player player;
	private HexPoint primary;
	private List<HexPoint> allPoints = new ArrayList<HexPoint>();	// All the points that this specific blob contains

	public BlobNode(Player player, HexPoint primary)
	{
		this.primary = primary;
		this.player = player;
		allPoints.add(primary);
	}
	
	@Override
	public List<HexPoint> getPoints()
	{
		return allPoints;
	}

	@Override
	public Player getOccupied()
	{
		return player;
	}

	public void setPlayer(Player player)
	{
		this.player = player;
	}


	public HexPoint getPrimary()
	{
		return primary;
	}


	public void setPrimary(HexPoint primary)
	{
		this.primary = primary;
	}

}
