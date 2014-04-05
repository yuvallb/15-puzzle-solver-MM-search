///////////////////////////////////////////////////////////////////////
// Node.java - Class representing nodes in the search tree
// Jeff Senecal
// CS 482 Exercise #2

import java.util.*;

public class Node implements Comparable<Node> {
	
	private State s;	// The state for this node
	private Node back;	// Back pointer
	private byte op;	// Operator used to get to this node
	private short g;	// Depth of node in search tree
	private short h;	// Computed heuristic for node state
	
	///////////////////////////////////////////////////////////////////
	// Constructor
	public Node(State s, Node back, byte op, short h)
	{
		// Initialize member variables
		this.s = s;
		this.back = back;
		this.op = op;
		this.h = h;
		
		// Set root node depth to 0, and child node depth to
		// parent depth + 1
		if (back == null)
			this.g = 0;
		else
			this.g = (short)(back.g + 1);
	}
	
	///////////////////////////////////////////////////////////////////
	// Setter for back pointer
	public void setBackPtr(Node back)
	{
		this.back = back;
	}
	
	///////////////////////////////////////////////////////////////////
	// Setter for operator
	public void setOp(byte op)
	{
		this.op = op;
	}
	
	///////////////////////////////////////////////////////////////////
	// Computes and returns f = depth + heuristic
	public short getF()
	{
		return (short)(g+h);
	}
	
	///////////////////////////////////////////////////////////////////
	// Setter for tree depth
	public void setG(short g)
	{
		this.g = g;
	}
	
	///////////////////////////////////////////////////////////////////
	// Getter for tree depth
	public short getG()
	{
		return g;
	}
	
	///////////////////////////////////////////////////////////////////
	// Getter for state
	public State getState()
	{
		return s;
	}
	
	///////////////////////////////////////////////////////////////////
	// Returns a string representing the path from initial state to
	// the state of this node
	public String pathToString()
	{
		if (back != null)
			return back.pathToString() + "\n" + opString(op) + "\n" + s;
		else
			return "\nInitial State:\n" + s;
	}
	
	///////////////////////////////////////////////////////////////////
	// Returns a string representing the path from the current node to
	// the goal node, skipping the first node in the path to prevent
	// printing the middle node twice
	public String revPathToStringSkipFirst()
	{
		if (back != null)
			return "\n" + opString((byte)-op) + "\n" + back.revPathToString();
		else
			return "\n" + opString((byte)-op) + "\n";
	}
	
	///////////////////////////////////////////////////////////////////
	// Returns a string representing the path from the current node to
	// the goal node
	public String revPathToString()
	{
		if (back != null)
			return s + "\n" + opString((byte)-op) + "\n" + back.revPathToString();
		else
			return s.toString();
	}
	
	///////////////////////////////////////////////////////////////////
	// This function is used by the heap to provide a natural ordering
	// for the Node objects
	public int compareTo(Node otherNode)
	{
		if (this.getF() < otherNode.getF())
			return -1;
		else if (this.getF() == otherNode.getF())
			return 0;
		else
			return 1;
	}
	
	///////////////////////////////////////////////////////////////////
	// Returns a string representing the type of operator used
	private String opString(byte op)
	{
		switch(op)
		{
			case State.Left:
				return "Left";
			case State.Right:
				return "Right";
			case State.Down:
				return "Down";
			case State.Up:
				return "Up";
			default:
				return "";
		}
	}
}
