package networkflow;

/**
 * An edge in a flow network
 * 
 * @author ramusa2
 *
 */
public class Edge {

    private final Node start;

    private final Node end;

    private int capacity;

    private int flow;

    public Edge(Node startNode, Node endNode, int edgeCapacity) {
	start = startNode;
	end = endNode;
	capacity = edgeCapacity;
	flow = 0;
    }

    /**
     * Returns true iff there is enough capacity to add the new flow; returns
     * false (without adding any flow) otherwise.
     */
    public boolean addFlow(int flowToAdd) {
	int newFlow = flow + flowToAdd;
	if (newFlow > capacity) {
	    return false;
	}
	flow = newFlow;
	this.end.addIncomingFlow(flowToAdd);
	return true;
    }

    public int flow() {
	return flow;
    }

    public int capacity() {
	return capacity;
    }

    public Node start() {
	return start;
    }

    public Node end() {
	return end;
    }

    public boolean isSaturated() {
	return flow == capacity;
    }

    public void addCapacity(int capacityToAdd) {
	capacity += capacityToAdd;
    }
}
