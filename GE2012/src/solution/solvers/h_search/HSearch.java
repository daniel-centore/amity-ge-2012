package solution.solvers.h_search;

import java.util.ArrayList;
import java.util.List;

import solution.board.HexPoint;
import solution.solvers.AmitySolver;
import solution.solvers.WeightedPoint;

/**
 * Class documentation.
 *
 * @author Mike DiBuduo
 * @author Scott DellaTorre
 */
public class HSearch implements AmitySolver {

    private void hSearch(List<HexPoint> G) {
        List[][] C = new List[121][121], SC = new List[121][121];
        for (int i = 0; i < C.length; i++) {
            for (int j = 0; j < C[i].length; j++) {
                C[i][j] = new ArrayList<SpecialHexPoint>();
            }
        }
        
        List<VirtualConnection> VCs = new ArrayList<VirtualConnection>();
        List<VirtualConnection> newVCs = new ArrayList<VirtualConnection>();
        do {
            for (HexPoint g : G) {
                for (HexPoint g1 : G) {
                    for (HexPoint g2 : G) {

                        if (g1 == g2) {
                            continue;
                        }
//LOOP2 over g1, g2 ∈ G such that:
                        //g1 != g2,
                        //at least one of the lists C(g1, g) or C(g2, g) contains at least one new carrier.
                        //If g is black then, additionally, g1 and g2 should be both empty.
                        //LOOP3 over c1 ∈ C(g1, g) and c2 ∈ C(g2, g) such that:
                        //At least one of the carriers c1 or c2 is new,
                        //c1 ∩ c2 = ∅,
                        //g1 ∈/ c2 and g2 ∈/ c1.
                        //IF (g is black)
                        //c = c1 ∪ c2. // the AND Deduction Rule
                        //UPDATE C(g1, g2) with c.
                        //ELSE
                        //sc = c1 ∪ g ∪ c2. // the AND Deduction Rule
                        //UPDATE SC(g1, g2) with sc.
                        //IF (the last UPDATE is successful)
                        //APPLY_THE_OR_DEDUCTION_RULE_AND_UPDATE
                        //(C_SET = C(g1, g2),
                        //SC_SET = SC(g1, g2) − sc,
                        //UNION = sc,
                        //INTERSECTION = sc )
                        //END of IF
                        //END of IF
                        //END of LOOP3
                        //END of LOOP2
                        //END of LOOP1
                        {
                        }
                    }
                }
            }
        } while (!newVCs.isEmpty());

    }

    private void orDeductionAndUpdate(List<HexPoint> C, List<HexPoint> SC,
            List<HexPoint> u, List<HexPoint> i) {
        for (HexPoint sc1 : SC) {
            List<HexPoint> u1 = new ArrayList<HexPoint>(u); //union
            u1.add(sc1);

            List<HexPoint> i1 = new ArrayList<HexPoint>();
            if (i.contains(sc1)) {
                i1.add(sc1);
                List<HexPoint> newSC = new ArrayList<HexPoint>(SC);
                newSC.remove(sc1);
                orDeductionAndUpdate(C, newSC, u1, i1);
            } else {
                C.addAll(u1);
                //C = u1;
                //idk which one it means by update
            }

        }

    }

    private class SpecialHexPoint extends HexPoint {

        private boolean isNew = true;

        public SpecialHexPoint(int x, char y) {
            super(x, y);
        }

        public SpecialHexPoint(HexPoint hexPoint) {
            super(hexPoint.getX(), hexPoint.getY());
        }

        private void setOld() {
            isNew = false;
        }
    }

    private class VirtualConnection {

        private final ArrayList<HexPoint> carriers;
        private boolean isNew = true;
        private final HexPoint x, y;

        private VirtualConnection(ArrayList<HexPoint> carriers, HexPoint x,
                HexPoint y) {
            this.carriers = carriers;
            this.x = x;
            this.y = y;
        }

        private void setOld() {
            isNew = false;
        }
    }

    @Override
    public float getWeight() {
        //TODO: Calculate weight
        return 1;
    }

    @Override
    public List<WeightedPoint> getPoints() {
        //TODO: get points
        throw new UnsupportedOperationException("Not supported yet.");
    }
}