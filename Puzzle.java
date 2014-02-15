///////////////////////////////////////////////////////////////////////
// Puzzle.java - main class for 15-puzzle solver
// Jeff Senecal
// CS 482 Exercise #2

import java.util.*;
import java.lang.reflect.Array;

public class Puzzle {
	
	// These are required as a workaround for Java's lack of arrays of generics
	static class NodeHeap extends PriorityQueue<Node> {}
	static class StateNodeHash extends HashMap<State, Node> {}
	
	static final double HWEIGHT = 1.3;
	
	///////////////////////////////////////////////////////////////////
	// main - run solver on test cases and print the results
	public static void main(String args[])
	{
		State[] initials = new State[3];
		
		// Test cases
		initials[0] = new State(new byte[][] {{2,10,3,0},{1,6,8,4},{5,9,7,12},{13,14,11,15}});
		initials[1] = new State(new byte[][] {{2,11,3,10},{1,9,4,0},{6,13,7,8},{5,14,15,12}});
		initials[2] = new State(new byte[][] {{4,3,2,1},{8,7,6,5},{12,11,10,9},{0,14,15,13}});
		
		// Goal state
		State goal = new State(new byte[][] {{1,2,3,4},{5,6,7,8},{9,10,11,12},{13,14,15,0}});

		// Run solver on each test case
		for (State initial : initials)
		{
			Node[] solution = biDirectionalSolve(initial, goal);
			if (solution != null)
			{
				// Output path
				System.out.println("Path Length: " + (solution[0].getG() + solution[1].getG()));
				System.out.print(solution[0].pathToString());
				System.out.print(solution[1].revPathToStringSkipFirst() + "\n\n");
			}
		}
	}
	
	///////////////////////////////////////////////////////////////////
	// biDirectionalSolve -	solve 15-puzzle using bidirectional A*
	//						search
	// Arguments:	fwdInitial - initial state
	// 				fwdGoal - goal state
	// Returns:		Array of 2 Nodes where both forward and backward
	//				paths meet.  First node has back pointer towards
	//				initial state and second node has back pointer
	//				towards goal state.
	public static Node[] biDirectionalSolve(State fwdInitial, State fwdGoal)
	{
		final int FWD = 0;		// Forward direction
		final int REV = 1;		// Backward direction
		
		byte[] ops = {State.Left, State.Right, State.Up, State.Down};
		int[] directions = {FWD, REV};
		
		// These arrays of objects have two elements, one for the forward
		// direction and one for the backward direction.
		
		// Min-heap for removing the node from the open set with the
		// smallest f-score.
		NodeHeap[] openHeap = new NodeHeap[2];
		
		// Hash tables with States as keys and Nodes as data for
		// checking if a state is in the open or closed set.
		StateNodeHash[] openHash = new StateNodeHash[2];
		StateNodeHash[] closedHash = new StateNodeHash[2];
		
		// Initial and goal states
		State[] initial = new State[] {fwdInitial, fwdGoal};
		State[] goal = new State[] {fwdGoal, fwdInitial};
		
		// For both forward and backward directions
		for (int i : directions)
		{
			// Create empty heap and hash tables
			openHeap[i] = new NodeHeap();
			openHash[i] = new StateNodeHash();
			closedHash[i] = new StateNodeHash();
			
			// Add initial node to the open set
			Node n = new Node(initial[i], null, State.None, (short)(initial[i].h(goal[i]) * HWEIGHT));
			openHash[i].put(initial[i], n);
			openHeap[i].add(n);
		}
		
		// For first iteration we start from the forward direction
		int i = FWD; // Index into our arrays for the current direction
		int j = REV; // Index into our arrays for the opposite direction
		
		// While there are still elements in the open set
		while(!openHeap[i].isEmpty())
		{
			// Remove node with minimum f-score
			Node n = openHeap[i].poll();
			State s = n.getState();
				
			// Move the node from the open to closed set
			openHash[i].remove(s);
			closedHash[i].put(s, n);
			
			// For each of the four possible operators
			for (byte op : ops)
			{
				// Create a new state that is the result of the move
				State newState = s.move(op);
				
				// If the move is invalid or has already been tried,
				// go on to next move
				if (newState == null || closedHash[i].containsKey(newState))
					continue;
					
				// If the new state is not already in the open set
				if (!openHash[i].containsKey(newState))
				{
					// Create a new Node for this state
					Node newNode = new Node(newState, n, op, (short)(newState.h(goal[i]) * HWEIGHT));
					
					// Check for a match in the nodes of the opposite direction
					Node matchedNode = null;
					if (openHash[j].containsKey(newState))
						matchedNode = openHash[j].get(newState);
					if (closedHash[j].containsKey(newState))
						matchedNode = closedHash[j].get(newState);
					
					// If there is a match, return the pair of nodes
					if (matchedNode != null)
					{
						int openNodeCount = openHash[i].size() + openHash[j].size() + 1;
						int closedNodeCount = closedHash[i].size() + closedHash[i].size();
						
						System.out.print("Nodes Generated: " + (openNodeCount + closedNodeCount));
						System.out.print(" (" + openNodeCount + " open/");
						System.out.println(closedNodeCount + " closed)");
						
						if (i == FWD)
							return new Node[]{newNode, matchedNode};
						else
							return new Node[]{matchedNode, newNode};
					// Otherwise, add the new node to the open set
					} else {
						openHash[i].put(newState, newNode);
						openHeap[i].add(newNode);
					}
				}
				// If the new state is already in the open set
				else
				{
					// Retrieve the existing node
					Node existingNode = openHash[i].get(newState);
					
					// If we have found a shorter path to this node
					if (n.getG() + 1 < existingNode.getG()) {
						
						// Update node depth, back pointer, and operator
						existingNode.setG((short)(n.getG() + 1));
						existingNode.setBackPtr(n);
						existingNode.setOp(op);
						
						// Remove and re-add node from heap.  This will
						// cause the node to be placed in the proper
						// minheap order.
						openHeap[i].remove(existingNode);
						openHeap[i].add(existingNode);
					}
				}
			}
			
			// Swap directions
			if (i == FWD)
			{
				i = REV;
				j = FWD;
			}
			else
			{
				i = FWD;
				j = REV;
			}
		}
		return null;	// No solution found
	}
}
