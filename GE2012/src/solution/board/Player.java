package solution.board;

/**
 * Which player we are talking about (so we don't get locked into a color)
 * 
 * @author Daniel Centore
 *
 */
public enum Player
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
