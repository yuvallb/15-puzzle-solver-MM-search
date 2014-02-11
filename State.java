public class State {
	
	private byte[][] board = new byte[4][4];
	
	public static final byte None = 0;
	public static final byte Up = 1;
	public static final byte Down = -1;
	public static final byte Right = 2;
	public static final byte Left = -2;
	
	public State(byte[][] board)
	{
		this.board = board;
	}
	
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
	
	public State move(byte op)
	{
		byte[][] newBoard = new byte[4][4];
		
		for (int row = 0; row < 4; row++)
			for (int col = 0; col < 4; col++)
				newBoard[row][col] = board[row][col];
		
		for (int row = 0; row < 4; row++) {
			for (int col = 0; col < 4; col++) {
				if (board[row][col] == 0) {
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
		return new State(newBoard);
	}
	
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
	
	public int hashCode()
	{
		int hash = 3;
		for (byte[] row : board)
			for (byte tile : row)
				hash = 7*hash+tile;
				
		return hash;
	}
	
	public boolean isSolved(State goal)
	{
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				if (board[i][j] != goal.board[i][j])
					return false;
		return true;
	}
	
	public short h0()
	{
		return 0;
	}
	
	public short h1()
	{
		short outOfPlace = 0;
		
		for (int row = 0; row < 4; row++)
		{
			for (int col = 0; col < 4; col++)
			{
				byte tile = board[row][col];
				if (tile == 0)
					continue;
				int correctRow = (tile-1)/4;
				int correctCol = (tile-1)%4;
				if (row != correctRow || col != correctCol)
					outOfPlace++;
			}
		}
		
		return outOfPlace;
	}
	
	public short h2()
	{
		short manhattan = 0;
		
		for (int row = 0; row < 4; row++)
		{
			for (int col = 0; col < 4; col++)
			{
				byte tile = board[row][col];
				if (tile == 0)
					continue;
				int correctRow = (tile-1)/4;
				int correctCol = (tile-1)%4;
				manhattan += Math.abs(correctRow-row);
				manhattan += Math.abs(correctCol-col);
			}
		}
		
		return manhattan;
	}
	
	public short h2(State goal)
	{
		short manhattan = 0;
		
		int correctRow[] = new int[16];
		int correctCol[] = new int[16];
		
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
	
	public short h3()
	{
		short v_invcount = 0;
		short h_invcount = 0;
		
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				for (int x = i; x < 4; x++) {
					for (int y = (x==i)?j:0; y < 4; y++) {
						if (board[i][j] == 0 || board[x][y] == 0)
							continue;
						else if (board[x][y] < board[i][j])
							h_invcount++;
					}
				}
			}
		}
		
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				for (int x = i; x < 4; x++) {
					for (int y = (x==i)?j:0; y < 4; y++) {
						if (board[j][i] == 0 || board[y][x] == 0)
							continue;
						else if (board[j][i]%4 == board[y][x]%4 && board[y][x] < board[j][i])
							v_invcount++;
						else if (board[j][i]%4 == 2 && board[y][x]%4 == 1)
							v_invcount++;
						else if (board[j][i]%4 == 3 && (board[y][x]%4 == 1 || board[y][x]%4 == 2))
							v_invcount++;
						else if (board[j][i]%4 == 0 && board[y][x]%4 != 0)
							v_invcount++;
					}
				}
			}
		}
		
		short inv_dist = (short)(v_invcount/3+v_invcount%3+h_invcount/3+h_invcount%3);
		return (short)Math.max(inv_dist, h2());
	}
	
	public short h3(State goal)
	{
		byte[] hCurrent = new byte[16];
		byte[] hGoal = new byte[16];
		byte[] vCurrent = new byte[16];
		byte[] vGoal = new byte[16];
		
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
		return (short)Math.max(invertDistance, h2(goal));
	}
	
	private int countInversions(byte[] array, byte[] target)
	{
		int inversions = 0;
		
		int[] correctPlace = new int[16];
		for (int i = 0; i < 16; i++)
		{
			if (target[i] != 0)
				correctPlace[target[i]] = i;
		}
		
		for (int i = 0; i < 16; i++)
		{
			if (array[i] == 0)
				continue;
			for (int j = i+1; j < 16; j++)
			{
				if (array[j] != 0 && correctPlace[array[j]] < i)
					inversions++;
			}
		}
		return inversions;
	}
}
