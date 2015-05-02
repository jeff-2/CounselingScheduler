package networkflow;

import java.util.Collection;
import java.util.HashMap;

/**
 * A node in a flow network
 * 
 * @author ramusa2
 *
 */
public class Node {

    private final String label;

    private final HashMap<Node, Edge> outgoing;

    private final HashMap<Node, Edge> incoming;

    private int incomingFlow;

    public Node(String name) {
	label = name;
	outgoing = new HashMap<Node, Edge>();
	incoming = new HashMap<Node, Edge>();
	incomingFlow = 0;
    }

    public String label() {
	return label;
    }

    /**
     * Creates a new edge (with provided capacity) to node 'end' if one does not
     * already exist, else adds capacity to the existing edge.
     */
    public void createEdgeTo(Node end, int capacity) {
	Edge e = outgoing.get(end);
	if (e == null) {
	    e = new Edge(this, end, capacity);
	    this.outgoing.put(end, e);
	    end.incoming.put(this, e); // Note: we should check that end
				       // doesn't have an incoming edge to this
				       // node already
	} else {
	    e.addCapacity(capacity);
	}
    }

    public void addIncomingFlow(int flowToAdd) {
	this.incomingFlow += flowToAdd;
    }

    public int getIncomingFlow() {
	return this.incomingFlow;
    }

    public void addFlowTo(Node en, int f) {
	Edge e = this.outgoing.get(en);
	if (e != null) {
	    e.addFlow(f);
	}
    }

    public Collection<Edge> edges() {
	return this.outgoing.values();
    }

    public String toString() {
	return label;
    }
}
