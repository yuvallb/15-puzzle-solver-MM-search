import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class MMsearch {

	/**
     * Solve 15-puzzle using bidirectional A* search
     * 
     * @param fwdInitial initial state
     * @param fwdGoal    goal state
     * @return Array of 2 Nodes where both forward and backward
     *         paths meet.  First node has back pointer towards
     *         initial state and second node has back pointer
     *         towards goal state.
     */
    public static Node[] MMSolve(State fwdInitial, State fwdGoal) {
        
        final int FWD = 0;        // Forward direction
        final int REV = 1;        // Backward direction
        int U = Integer.MAX_VALUE;
        
        int[] directions = {FWD, REV};
        
        // These lists have two elements, one for the forward
        // direction and one for the backward direction.
        
        // Min-heap for removing the node from the open set with the
        // smallest f-score.
        List<Queue<Node>> fOpenHeap = new ArrayList<>(2);
        List<Queue<Node>> gOpenHeap = new ArrayList<>(2);
        List<Queue<Node>> prOpenHeap = new ArrayList<>(2);
        
        // set comparators. by f is built in the Node object
		Comparator<Node> byG = (Node a, Node b) -> {
	        if (a.getDepth() < b.getDepth()) {
	            return -1;
	        } else if (a.getDepth() == b.getDepth()) {
	            return 0;
	        } else {
	            return 1;
	        }	    };
		Comparator<Node> byPriorityAndG = (Node a, Node b) -> {
	        if (a.getPriority() < b.getPriority())
	            return -1;
	        else if (a.getPriority() == b.getPriority()){
		        if (a.getDepth() < b.getDepth()) {
		            return -1;
		        } else if (a.getDepth() == b.getDepth()) {
		            return 0;
		        } else {
		            return 1;
		        }
		    } else
	            return 1;
	    };
	    
        // Hash tables with States as keys and Nodes as data for
        // checking if a state is in the open or closed set.
        List<Map<State, Node>> openHash = new ArrayList<>(2);
        List<Map<State, Node>> closedHash = new ArrayList<>(2);
        
        // Initial and goal states
        State[] initial = new State[] {fwdInitial, fwdGoal};
        State[] goal = new State[] {fwdGoal, fwdInitial};
        
        // For both forward and backward directions
        for (int i : directions) {
            // Create empty heap and hash maps
        	fOpenHeap.add(new PriorityQueue<Node>());
        	gOpenHeap.add(new PriorityQueue<Node>(byG));
        	prOpenHeap.add(new PriorityQueue<Node>(byPriorityAndG));
            openHash.add(new HashMap<State, Node>());
            closedHash.add(new HashMap<State, Node>());
            
            // Add initial node to the open set
            Node n = new Node(initial[i], null, null, (short)(initial[i].h(goal[i])));
            openHash.get(i).put(initial[i], n);
            fOpenHeap.get(i).add(n);
            gOpenHeap.get(i).add(n);
            prOpenHeap.get(i).add(n);
        }
        
        
        // While there are still elements in the open set
        while(!fOpenHeap.get(FWD).isEmpty() && !fOpenHeap.get(REV).isEmpty()) {
        	// get minimum priority
        	int fwdPriority = prOpenHeap.get(FWD).peek().getPriority();
        	int C = Math.min(fwdPriority, prOpenHeap.get(REV).peek().getPriority());
        	
        	// stop condition: test U
        	if (U<=Math.max(Math.max(
        			C, 
        			fOpenHeap.get(FWD).peek().getFScore()
        			),Math.max(
                	fOpenHeap.get(REV).peek().getFScore(),
                	gOpenHeap.get(FWD).peek().getDepth()+
                	gOpenHeap.get(REV).peek().getDepth()+1
        					))) {
        		
                int openNodeCount = openHash.get(FWD).size() + openHash.get(REV).size() + 1;
                int closedNodeCount = closedHash.get(FWD).size() + closedHash.get(REV).size();
                
                System.out.print("Nodes Generated: " + (openNodeCount + closedNodeCount));
                System.out.print(" (" + openNodeCount + " open/");
                System.out.println(closedNodeCount + " closed)");
                System.out.println("Path length: " + U);

        		return new Node[]{}; // TODO -  retrace the path
        	} else if (U <= C) {
                System.out.println("U >= C, but not meeting stop condition!!");
                return null;
        	}
        	
        	// decide direction to expand 
        	int dir = (C==fwdPriority) ? FWD : REV;

        	// choose n ∈ OpenF for which prF (n) = prminF and gF (n) is
			// minimum
			Node n = prOpenHeap.get(dir).poll();

        	
        	// get the state for the selected node
            State s = n.getState();

            // Move the node from the open to closed set, remove from heaps
            openHash.get(dir).remove(s);
            closedHash.get(dir).put(s, n);
            fOpenHeap.get(dir).remove(n);
            gOpenHeap.get(dir).remove(n);
            prOpenHeap.get(dir).remove(n);
            
            // For each of the four possible operators
            for (State.Operator op : State.Operator.values()) {
                // Create a new state that is the result of the move
                State newState = s.move(op);
                
                // If the move is invalid 
                if (newState == null) {
                    continue;
                }
                
                Node newNode = null;
                // if c ∈ OpenF ∪ ClosedF and  gF (c) ≤ gF (n) + cost(n, c) then continue
                {
                	newNode = openHash.get(dir).get(newState);
                	if (newNode == null) {
                		newNode = closedHash.get(dir).get(newState);
                	}
                	// the child is in the open or closed list 
                	if (newNode != null) { 
                		// test if cost is lower now
                		if (newNode.getFScore() <= n.getFScore() + 1) {
                			continue;
                		}
                		openHash.get(dir).remove(newState); 
                		fOpenHeap.get(dir).remove(newNode);
                		gOpenHeap.get(dir).remove(newNode);
                		prOpenHeap.get(dir).remove(newNode);
                		closedHash.get(dir).remove(newState);
                	}
                }
                
                // create new node for this state, if not already found in open/closed lists
                if (newNode == null) {
                	newNode = new Node(newState, n, op, (short)(newState.h(goal[dir]) ));
                }
                
                // add c to OpenF
                openHash.get(dir).put(newState, newNode);
                fOpenHeap.get(dir).add(newNode);
                gOpenHeap.get(dir).add(newNode);
                prOpenHeap.get(dir).add(newNode);
                
                // if c ∈ OpenB then U :=min(U,gF(c)+gB(c))
                Node matchedNode = openHash.get(1-dir).get(newState);
                if (matchedNode != null) {
                	U = Math.min(U, 
                			matchedNode.getDepth() +
                			newNode.getDepth()
                			);
                	if (dir==FWD) {
                		System.out.println("Found path: Forward depth:" + newNode.getDepth() + " backward depth: " + matchedNode.getDepth());
                	} else {
                		System.out.println("Found path: Forward depth:" + matchedNode.getDepth() + " backward depth: " + newNode.getDepth());
                	}
                }
            }
                
        }
        return null;    // No solution found
    }

}
