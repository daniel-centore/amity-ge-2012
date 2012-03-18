package solution.solvers.h_search;

import java.util.ArrayList;
import java.util.List;
import solution.board.HexPoint;

/**
 * Class documentation.
 *
 * @author Mike DiBuduo
 */
public class And_Or_Deduction_Rule 
{
	//TODO Scott, check to make sure I did this right. I think I did though.
	public void orDeductionAndUpdate(List<HexPoint> C, List<HexPoint> SC, List<HexPoint> u, List<HexPoint> i)
	{
		for (HexPoint sc1 : SC)
		{
			List<HexPoint> u1 = new ArrayList<HexPoint>();//The union of sc1 and u
			u1.addAll(u);	
			u1.add(sc1);			
			List<HexPoint> i1 = new ArrayList<HexPoint>();//Intersection of i and sc1
			for (HexPoint iPoint : i)
			{
				if (sc1.equals(iPoint))
				{
					i1.add(sc1);
				}
			}
			if (!i1.isEmpty())
			{
				C.addAll(u1);
			}
			else
			{
				SC.remove(sc1);
				orDeductionAndUpdate(C, SC, u1, i1);
			}
		}
		
	}

}