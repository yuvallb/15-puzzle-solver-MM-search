import java.util.*;

public class Node implements Comparable<Node> {
	
	private State s;
	private Node back;
	private byte op;
	private short g, h;
	
	private boolean bestFirst = false;
	
	public Node(State s, Node back, byte op, short h)
	{
		this.s = s;
		this.back = back;
		this.op = op;
		if (back == null || bestFirst)
			this.g = 0;
		else
			this.g = (short)(back.g + 1);
		this.h = h;
	}
	
	public void setBackPtr(Node back)
	{
		this.back = back;
	}
	
	public void setOp(byte op)
	{
		this.op = op;
	}
	
	public short getF()
	{
		return (short)(g+h);
	}
	
	public void setG(short g)
	{
		if (bestFirst)
			this.g = 0;
		else
			this.g = g;
	}
	
	public short getG()
	{
		return g;
	}
	
	public State getState()
	{
		return s;
	}
	
	public String toString()
	{
		return toString(0);
	}
	
	public String toString(int pathLength)
	{
		if (back != null)
			return back.toString(pathLength + 1) + "\n" + opString(op) + "\n" + s.toString();
		else
			return "Path Length: " + pathLength + "\n\nInitial State:\n" + s.toString();
	}
	
	public int compareTo(Node otherNode)
	{
		if (this.getF() < otherNode.getF())
			return -1;
		else if (this.getF() == otherNode.getF())
			return 0;
		else
			return 1;
	}
	
	private String opString(byte op)
	{
		switch(op)
		{
			case -2:
				return "Left";
			case 2:
				return "Right";
			case -1:
				return "Down";
			case 1:
				return "Up";
			default:
				return "";
		}
	}
}
