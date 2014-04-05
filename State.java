///////////////////////////////////////////////////////////////////////
// State.java - Class representing a game state
// Jeff Senecal
// CS 482 Exercise #2

public class State {
	
	// 2D array representing game board where each element is a number
	// between 0 and 15 (0 is used for the blank tile)
	private byte[][] board = new byte[4][4];
	
	// Operators
	public static final byte None = 0;
	public static final byte Up = 1;
	public static final byte Down = -1;
	public static final byte Right = 2;
	public static final byte Left = -2;
	
	///////////////////////////////////////////////////////////////////
	// Constructor - create a new state from a game board array
	public State(byte[][] board)
	{
		this.board = board;
	}
	
	///////////////////////////////////////////////////////////////////
	// Prints out a state as a 2D grid of tiles 1-15 and blank []
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		
		for (byte[] row : board) {
			for (byte tile : row) {
				if (tile == 0)
					sb.append("[] ");
				else
					sb.append(String.format("%2d ", tile));
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	///////////////////////////////////////////////////////////////////
	// Applies the operator to the current state and returns a new state
	// (or null if the operation is not possible)
	public State move(byte op)
	{
		// Create a new empty game board
		byte[][] newBoard = new byte[4][4];
		
		// Initialize the new board to the same as the current board
		for (int row = 0; row < 4; row++)
			for (int col = 0; col < 4; col++)
				newBoard[row][col] = board[row][col];
		
		// Find the location of the blank tile
		for (int row = 0; row < 4; row++) {
			for (int col = 0; col < 4; col++) {
				if (board[row][col] == 0) {
					
					// Try to apply the operation by moving the blank
					// tile in the specified direction
					switch (op) {
						case Up:
							if (row > 0) {
								newBoard[row][col] = board[row-1][col];
								newBoard[row-1][col] = 0;
							} else {
								return null;
							}
							break;
						case Down:
							if (row < 3) {
								newBoard[row][col] = board[row+1][col];
								newBoard[row+1][col] = 0;
							} else {
								return null;
							}
							break;
						case Left:
							if (col > 0) {
								newBoard[row][col] = board[row][col-1];
								newBoard[row][col-1] = 0;
							} else {
								return null;
							}
							break;
						case Right:
							if (col < 3) {
								newBoard[row][col] = board[row][col+1];
								newBoard[row][col+1] = 0;
							} else {
								return null;
							}
							break;
					}
				}
			}
		}
		// Create and return a new State object using the new board
		return new State(newBoard);
	}
	
	///////////////////////////////////////////////////////////////////
	// Returns true if this state is the same as the argument, by
	// checking if all tiles are in the same positions.  Required for
	// use of hash table.
	public boolean equals(Object obj)
	{
		if (obj == this)
			return true;
		
		if (obj == null || obj.getClass() != this.getClass())
			return false;
			
		State other = (State) obj;
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				if (board[i][j] != other.board[i][j])
					return false;
		
		return true;
	}
	
	///////////////////////////////////////////////////////////////////
	// Generates a hash code for use in the hash table.  Any two equal
	// states will always return the same hash code.
	public int hashCode()
	{
		int hash = 3;
		for (byte[] row : board)
			for (byte tile : row)
				hash = 7*hash+tile;
				
		return hash;
	}
	
	///////////////////////////////////////////////////////////////////
	// "Manhattan distance" heuristic
	// Returns sum of the Manhattan distances between pairs of tiles in
	// this state and the goal state
	public short manhattanDistance(State goal)
	{
		short manhattan = 0;
		
		int correctRow[] = new int[16];
		int correctCol[] = new int[16];
		
		// Finds the correct row and column for each tile using the
		// goal state
		for (int row = 0; row < 4; row++)
		{
			for (int col = 0; col < 4; col++)
			{
				if (goal.board[row][col] != 0)
				{
					correctRow[goal.board[row][col]] = row;
					correctCol[goal.board[row][col]] = col;
				}
			}
		}
		
		// Compare each tile's actual row and column to the correct row
		// and column, compute Manhattan distance, and add to sum.
		for (int row = 0; row < 4; row++)
		{
			for (int col = 0; col < 4; col++)
			{
				byte tile = board[row][col];
				
				if (tile != 0)
				{
					manhattan += Math.abs(correctRow[tile]-row);
					manhattan += Math.abs(correctCol[tile]-col);
				}
			}
		}
		
		return manhattan;
	}
	
	///////////////////////////////////////////////////////////////////
	// Linear conflict heuristic
	// Returns the sum of the Manhattan distance and the additional
	// moves required to eliminate conflicts between tiles that are in
	// their goal row or column but in the wrong order.
	public short h(State goal)
	{
		// Required number moves to remove all linear conflicts
		int reqMoves = 0;
		
		// Correct row/column of each tile
		int correctRow[] = new int[16];
		int correctCol[] = new int[16];
		
		// Number or horizontal and vertical conflicts a particular
		// tile is involved in
		int hConflicts[][] = new int[4][4];
		int vConflicts[][] = new int[4][4];
		
		// conflictCount[i] is the number of tiles in a row or column
		// that have i conflicts with other tiles in the same row/column
		int conflictCount[];
		
		// Finds the correct row and column for each tile using the
		// goal state
		for (int row = 0; row < 4; row++)
		{
			for (int col = 0; col < 4; col++)
			{
				if (goal.board[row][col] != 0)
				{
					correctRow[goal.board[row][col]] = row;
					correctCol[goal.board[row][col]] = col;
				}
			}
		}
		
		// For each non-blank tile on the board
		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 4; j++)
			{
				if (board[i][j] != 0)
				{
					// If the tile is in its goal row
					if (correctRow[board[i][j]] == i)
					{
						// For each of the following tiles in the row
						for (int k = j + 1; k < 4; k++)
						{
							// If the second tile is also in its goal row
							// and the two tiles are in the wrong relative order
							// then increase the conflict count for both tiles
							if (board[i][k] != 0 &&
								correctRow[board[i][k]] == i &&
								correctCol[board[i][k]] < correctCol[board[i][j]])
							{
								hConflicts[i][k]++;
								hConflicts[i][j]++;
							}
						}
					}
					// If the tile is in its goal column
					if (correctCol[board[i][j]] == j)
					{
						// For each of the following tiles in the column
						for (int k = i + 1; k < 4; k++)
						{
							// If the second tile is also in its goal column
							// and the two tiles are in the wrong relative order
							// then increase the conflict count for both tiles
							if (board[k][j] != 0 &&
								correctCol[board[k][j]] == j &&
								correctRow[board[k][j]] < correctRow[board[i][j]])
							{
								vConflicts[k][j]++;
								vConflicts[i][j]++;
							}
						}
					}
				}
			}
		}
		
		// For each row, add number of moves to eliminate conflicts to required moves
		for (int i = 0; i < 4; i++)
		{
			conflictCount = new int[4];
			for (int j = 0; j < 4; j++)
			{
				conflictCount[hConflicts[i][j]]++;
			}
			reqMoves += movesForConflicts(conflictCount);
		}
		
		// For each column, add number of moves to eliminate conflicts to required moves
		for (int j = 0; j < 4; j++)
		{
			conflictCount = new int[4];
			for (int i = 0; i < 4; i++)
			{
				conflictCount[vConflicts[i][j]]++;
			}
			reqMoves += movesForConflicts(conflictCount);
		}
		
		// Return the sum of the Manhattan distance and the additional
		// required moves to resolve conflicts
		return (short) (reqMoves + manhattanDistance(goal));
	}
	
	///////////////////////////////////////////////////////////////////
	// Returns the number of moves required to resolve linear conflicts,
	// where conflictCount[i] is the number of tiles with i conflicts.
	// The number of moves required is twice the number of tiles that
	// must be moved to an adjacent row or column in order to get tiles
	// in the correct order.
	private int movesForConflicts(int[] conflictCount)
	{
		// If every tile has 0 conflicts
		// Matches 1234
		if (conflictCount[0] == 4)
			return 0; // No additional moves required

		// If every tile has 3 conflicts
		// Matches 4321
		else if (conflictCount[3] == 4)
			return 6; // 6 additional moves required
			
		// If 2 tiles have 1 conflict each
		// or 2 tiles have 1 conflict each and 1 tile has 2 conflicts
		// or 3 tiles have 1 conflict each and 1 tiles has 3 conflicts
		// Matches 1243,1324,1342,1423,2134,2314,2341,3124,4123
		else if (conflictCount[1] == 2 && conflictCount[2] != 2 || conflictCount[1] == 3)
			return 2; // 2 additional moves required
			
		// Otherwise
		// Matches 1432,2143,2413,2431,3142,3214,3241,3412,3421,4132,4213,4231,4312
		else
			return 4; // 4 additional moves required
	}
}
