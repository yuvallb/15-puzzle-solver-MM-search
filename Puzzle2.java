import java.util.*;

public class Puzzle2 {
	
	public static final boolean cleanHeap = true;
	
	public static void main(String args[])
	{
		State s;
		Node[] solution;
		
		//s = new State(new byte[][] {{1,2,3,4},{5,6,7,8},{9,10,11,12},{13,14,0,15}});
		State goal = new State(new byte[][] {{1,2,3,4},{5,6,7,8},{9,10,11,12},{13,14,15,0}});
		//Node solution = solve(s, goal);
		//System.out.print(solution);
		
		//s = new State(new byte[][] {{0,15,14,13},{12,11,10,9},{8,7,6,5},{4,3,2,1}});
		//System.out.println("H3: " + s.h3());
		
		//s = new State(new byte[][] {{2,10,3,0},{1,6,8,4},{5,9,7,12},{13,14,11,15}});
		//s = new State(new byte[][] {{2,11,3,10},{1,9,4,0},{6,13,7,8},{5,14,15,12}});
		//s = new State(new byte[][] {{1,2,3,4},{8,7,6,5},{12,11,10,9},{0,14,15,13}});
		s = new State(new byte[][] {{4,3,2,1},{8,7,6,5},{12,11,10,9},{0,14,15,13}});
		solution = biDirectionalSolve(s, goal);
		System.out.println("Path Length: " + (solution[0].pathLength() + solution[1].pathLength()));
		System.out.print(solution[0].pathToString());
		System.out.print(solution[1].revPathToStringSkipFirst());
	}
	
	public static Node solve(State initial, State goal)
	{
		int nodesGenerated = 0;
		byte[] ops = {State.Left, State.Right, State.Up, State.Down};
		PriorityQueue<Node> openHeap = new PriorityQueue<Node>();
		HashMap<State, Node> openHash = new HashMap<State,Node>();
		HashMap<State, Node> closedHash = new HashMap<State, Node>();
	
		Node n = new Node(initial, null, State.None, initial.h2());
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
			if (cleanHeap && !openHash.containsKey(s))
					continue;
			openHash.remove(s);
			closedHash.put(s, n);
			for (byte op : ops)
			{
				State newState = s.move(op);
				if (newState == null || closedHash.containsKey(newState))
					continue;
				if (!openHash.containsKey(newState))
				{
					Node newNode = new Node(newState, n, op, newState.h2());
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
						if (cleanHeap)
							openHeap.remove(existingNode);
						openHeap.add(existingNode);
					}
				}
			}
		}
		return null;
	}
	
	public static Node[] biDirectionalSolve(State initial, State goal)
	{
		int nodesGenerated = 0;
		byte[] ops = {State.Left, State.Right, State.Up, State.Down};
		
		PriorityQueue<Node> openHeap = new PriorityQueue<Node>();
		HashMap<State, Node> openHash = new HashMap<State,Node>();
		HashMap<State, Node> closedHash = new HashMap<State, Node>();
	
		PriorityQueue<Node> revOpenHeap = new PriorityQueue<Node>();
		HashMap<State, Node> revOpenHash = new HashMap<State,Node>();
		HashMap<State, Node> revClosedHash = new HashMap<State, Node>();
	
		Node n = new Node(initial, null, State.None, initial.h3(goal));
		nodesGenerated++;
		openHash.put(initial, n);
		openHeap.add(n);
		
		n = new Node(goal, null, State.None, goal.h3(initial));
		nodesGenerated++;
		revOpenHash.put(goal, n);
		revOpenHeap.add(n);
		
		//
		int maxDepth = 0;
		while(!openHeap.isEmpty())
		{
			///////////////////////////////////////////////////////////
			// FORWARD SEARCH
			
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
			if (cleanHeap && !openHash.containsKey(s))
					continue;
			openHash.remove(s);
			closedHash.put(s, n);
			for (byte op : ops)
			{
				State newState = s.move(op);
				if (newState == null || closedHash.containsKey(newState))
					continue;
				if (!openHash.containsKey(newState))
				{
					Node newNode = new Node(newState, n, op, newState.h3(goal));
					nodesGenerated++;
					if (revOpenHash.containsKey(newState))
					{
						System.out.println("Nodes Generated: " + nodesGenerated);
						return new Node[]{newNode, revOpenHash.get(newState)};
					}
					if (revClosedHash.containsKey(newState))
					{
						System.out.println("Nodes Generated: " + nodesGenerated);
						return new Node[]{newNode, revClosedHash.get(newState)};
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
						if (cleanHeap)
							openHeap.remove(existingNode);
						openHeap.add(existingNode);
					}
				}
			}
			
			///////////////////////////////////////////////////////////
			// BACKWARDS SEARCH
						
			//if (nodesGenerated % 1000 == 0)
			//	System.out.println("Nodes generated: " + nodesGenerated);
			n = revOpenHeap.poll();
			//
			if (n.getG() > maxDepth)
			{
				maxDepth = n.getG();
				System.out.println("Max Depth: " + maxDepth);
			}
			s = n.getState();
			if (cleanHeap && !revOpenHash.containsKey(s))
					continue;
			revOpenHash.remove(s);
			revClosedHash.put(s, n);
			for (byte op : ops)
			{
				State newState = s.move(op);
				if (newState == null || revClosedHash.containsKey(newState))
					continue;
				if (!revOpenHash.containsKey(newState))
				{
					Node newNode = new Node(newState, n, op, newState.h3(initial));
					nodesGenerated++;
					if (openHash.containsKey(newState))
					{
						System.out.println("Nodes Generated: " + nodesGenerated);
						return new Node[] {openHash.get(newState), newNode};
					}
					if (closedHash.containsKey(newState))
					{
						System.out.println("Nodes Generated: " + nodesGenerated);
						return new Node[] {closedHash.get(newState), newNode};
					}
					revOpenHash.put(newState, newNode);
					revOpenHeap.add(newNode);
				}
				else
				{
					Node existingNode = revOpenHash.get(newState);
					if (n.getG() + 1 < existingNode.getG()) {
						existingNode.setG((short)(n.getG() + 1));
						existingNode.setBackPtr(n);
						existingNode.setOp(op);
						if (cleanHeap)
							revOpenHeap.remove(existingNode);
						revOpenHeap.add(existingNode);
					}
				}
			}
		}
		return null;
	}
}
