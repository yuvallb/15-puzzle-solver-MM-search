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
	public short h2(State goal)
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
	// Inversion distance heuristic
	// Calculates number of horizontal and vertical inversions and
	// returns minimum number of steps to eliminate them
	public short h3(State goal)
	{
		// Current and goal states converted to 1D arrays in row-major order
		byte[] hCurrent = new byte[16];
		byte[] hGoal = new byte[16];
		
		// Current and goal states converted to 1D arrays in column-major order
		byte[] vCurrent = new byte[16];
		byte[] vGoal = new byte[16];
		
		// Convert 2D arrays to 1D arrays
		for (int i = 0, x = 0; i < 4; i++)
		{
			for (int j = 0; j < 4; j++, x++)
			{
				hCurrent[x] = board[i][j];
				hGoal[x] = goal.board[i][j];
				vCurrent[x] = board[j][i];
				vGoal[x] = goal.board[j][i];
			}
		}
		
		int hInversions = countInversions(hCurrent, hGoal);
		int vInversions = countInversions(vCurrent, vGoal);
		int invertDistance = hInversions/3 + hInversions%3 + vInversions/3 + vInversions%3;

		// Either the Inversion distance or the Manhattan distance can be larger,
		// so we return the maximum of the two.
		return (short)Math.max(invertDistance, h2(goal));
	}
	
	///////////////////////////////////////////////////////////////////
	// Counts the number of inversions in an array relative to the
	// goal ordering in the target array
	private int countInversions(byte[] array, byte[] target)
	{
		int inversions = 0;
		
		// Set correctPlace[i] to the correct index of tile i
		int[] correctPlace = new int[16];
		for (int i = 0; i < 16; i++)
		{
			if (target[i] != 0)
				correctPlace[target[i]] = i;
		}
		
		// For each tile in the array
		for (int i = 0; i < 16; i++)
		{
			// Skip blank tile
			if (array[i] == 0)
				continue;
				
			// Check every tile that comes after tile i
			for (int j = i+1; j < 16; j++)
			{
				// If that tile is supposed to come before i, we have
				// an inversion, so we increment the counter
				if (array[j] != 0 && correctPlace[array[j]] < correctPlace[array[i]])
					inversions++;
			}
		}
		// Return total number of inversions
		return inversions;
	}
}
