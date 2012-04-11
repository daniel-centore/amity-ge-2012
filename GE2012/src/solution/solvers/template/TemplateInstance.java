package solution.solvers.template;

import java.util.List;

import solution.board.HexPoint;

public class TemplateInstance
{

	private final List<HexPoint> carriers;
	private final HexPoint head;

	public TemplateInstance(HexPoint head, List<HexPoint> carriers)
	{
		this.carriers = carriers;
		this.head = head;
	}

	public List<HexPoint> getCarriers()
	{
		return carriers;
	}

	public HexPoint getHead()
	{
		return head;
	}

}
