public class Node implements Comparable<Node> {
    
    private State state;            // The state for this node
    private Node backPtr;            // Back pointer
    private State.Operator op;        // Operator used to get to this node
    private short g;                // Depth of node in search tree
    private short h;                // Computed heuristic for node state
    
    /**
     * @param state    Puzzle state associated with this node
     * @param backPtr  Back pointer
     * @param op       Operation that led to this node
     * @param h        Heuristic for node state
     */
    public Node(State state, Node backPtr, State.Operator op, short h) {
        this.state = state;
        this.backPtr = backPtr;
        this.op = op;
        this.h = h;
        
        // Set root node depth to 0, and child node depth to
        // parent depth + 1
        if (backPtr == null)
            this.g = 0;
        else
            this.g = (short)(backPtr.g + 1);
    }
    
    /**
     * @param backPtr  Back pointer to previous node
     */
    public void setBackPtr(Node backPtr) {
        this.backPtr = backPtr;
    }

    /**
     * @param op  Operator that led to this state
     */
    public void setOp(State.Operator op) {
        this.op = op;
    }
    
    /**
     * @return  Tree depth plus heuristic
     */
    public short getF() {
        return (short)(g+h);
    }
    
    /**
     * @param g  Tree depth
     */
    public void setG(short g) {
        this.g = g;
    }
    
    /**
     * @return  Tree depth
     */
    public short getG() {
        return g;
    }
    
    /**
     * @return  Game state of the node
     */
    public State getState() {
        return state;
    }
    
    /**
     * @return  String representing the path from initial state to
     *          the state of this node.
     */
    public String pathToString() {
        if (backPtr != null)
            return backPtr.pathToString() + "\n" + op + "\n" + state;
        else
            return "\nInitial State:\n" + state;
    }
    
    /**
     * @return  String representing the path from the current node to
     *          the goal node, skipping the first node in the path to prevent
     *          printing the middle node twice
     */
    public String revPathToStringSkipFirst() {
        if (backPtr != null)
            return "\n" + op.reverse() + "\n" + backPtr.revPathToString();
        else
            return "\n" + op.reverse() + "\n";
    }
    
    /**
     * @return  String representing the path from the current node to the goal node.
     */
    public String revPathToString() {
        if (backPtr != null)
            return state + "\n" + op.reverse() + "\n" + backPtr.revPathToString();
        else
            return state.toString();
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Node otherNode) {
        if (this.getF() < otherNode.getF())
            return -1;
        else if (this.getF() == otherNode.getF())
            return 0;
        else
            return 1;
    }
}
