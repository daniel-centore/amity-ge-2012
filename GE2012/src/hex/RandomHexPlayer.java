package hex;
import game.*;
import java.util.*;

public class RandomHexPlayer extends GamePlayer {
	public RandomHexPlayer(String n) 
	{
		super(n, new HexState(), false);
	}
	public GameMove getMove(GameState state, String lastMove)
	{
		HexState board = (HexState)state;
		ArrayList<HexMove> list = new ArrayList<HexMove>();  
		HexMove mv = new HexMove();
		for (int r=0; r<HexState.N; r++) {
			for (int c=0; c<HexState.N; c++) {
				mv.row = r;
				mv.col = c;
				if (board.moveOK(mv)) {
					list.add((HexMove)mv.clone());
				}
			}
		}
		int which = Util.randInt(0, list.size()-1);
		return list.get(which);
	}
//	public static void main(String [] args)
//	{
//		GamePlayer p = new RandomHexPlayer("Random+");
//		p.compete(args, 1);
//	}
}
