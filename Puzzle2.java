import java.util.*;

public class Puzzle2 {
	
	public static void main(String args[])
	{
		State s = new State(new byte[][] {{1,2,3,4},{5,6,7,8},{9,10,11,12},{13,14,0,15}});
		State goal = new State(new byte[][] {{1,2,3,4},{5,6,7,8},{9,10,11,12},{13,14,15,0}});
		Node solution = solve(s, goal);
		//System.out.print(solution);
		
		//s = new State(new byte[][] {{0,15,14,13},{12,11,10,9},{8,7,6,5},{4,3,2,1}});
		//System.out.println("H3: " + s.h3());
		
		//s = new State(new byte[][] {{2,10,3,0},{1,6,8,4},{5,9,7,12},{13,14,11,15}});
		s = new State(new byte[][] {{2,11,3,10},{1,9,4,0},{6,13,7,8},{5,14,15,12}});
		//s = new State(new byte[][] {{1,2,3,4},{8,7,6,5},{12,11,10,9},{0,14,15,13}});
		//s = new State(new byte[][] {{4,3,2,1},{8,7,6,5},{12,11,10,9},{0,14,15,13}});
		solution = solve(s, goal);
		System.out.print(solution);
	}
	
	public static Node solve(State initial, State goal)
	{
		int nodesGenerated = 0;
		byte[] ops = {State.Left, State.Right, State.Up, State.Down};
		PriorityQueue<Node> openHeap = new PriorityQueue<Node>();
		HashMap<State, Node> openHash = new HashMap<State,Node>();
		HashMap<State, Node> closedHash = new HashMap<State, Node>();
	
		Node n = new Node(initial, null, State.None, initial.h3());
		nodesGenerated++;
		if (initial.isSolved(goal))
			return n;
			
		openHash.put(initial, n);
		openHeap.add(n);
		//
		int maxDepth = 0;
		while(!openHeap.isEmpty())
		{
			//if (nodesGenerated % 1000 == 0)
			//	System.out.println("Nodes generated: " + nodesGenerated);
			n = openHeap.poll();
			//
			if (n.getG() > maxDepth)
			{
				maxDepth = n.getG();
				System.out.println("Max Depth: " + maxDepth);
			}
			State s = n.getState();
			//if (!openHash.containsKey(s))
			//	continue;
			openHash.remove(s);
			closedHash.put(s, n);
			for (byte op : ops)
			{
				State newState = s.move(op);
				if (newState == null || closedHash.containsKey(newState))
					continue;
				if (!openHash.containsKey(newState))
				{
					Node newNode = new Node(newState, n, op, newState.h3());
					nodesGenerated++;
					if (newState.isSolved(goal))
					{
						System.out.println("Nodes Generated: " + nodesGenerated);
						return newNode;
					}
					openHash.put(newState, newNode);
					openHeap.add(newNode);
				}
				else
				{
					Node existingNode = openHash.get(newState);
					if (n.getG() + 1 < existingNode.getG()) {
						existingNode.setG((short)(n.getG() + 1));
						existingNode.setBackPtr(n);
						existingNode.setOp(op);
						openHeap.remove(existingNode);
						openHeap.add(existingNode);
					}
				}
			}
		}
		return null;
	}
}
