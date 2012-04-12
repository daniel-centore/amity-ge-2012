package solution.solvers.template;

import java.util.List;

import solution.board.HexPoint;
import solution.board.implementations.indivBoard.IndivBoard;

public interface Template
{
	
	public List<TemplateInstance> findInstances(IndivBoard board);

}
