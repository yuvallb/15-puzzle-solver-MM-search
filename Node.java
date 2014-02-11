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
	
	public int pathLength()
	{
		if (back == null)
			return 0;
		else
			return 1 + back.pathLength();
	}
	
	public String pathToString()
	{
		if (back != null)
			return back.pathToString() + "\n" + opString(op) + "\n" + s;
		else
			return "\nInitial State:\n" + s;
	}
	
	public String revPathToStringSkipFirst()
	{
		if (back != null)
			return "\n" + opString((byte)-op) + "\n" + back.revPathToString();
		else
			return "\n" + opString((byte)-op) + "\n";
	}
	
	public String revPathToString()
	{
		if (back != null)
			return s + "\n" + opString((byte)-op) + "\n" + back.revPathToString();
		else
			return s.toString();
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
