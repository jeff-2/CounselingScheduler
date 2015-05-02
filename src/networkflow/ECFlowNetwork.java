package networkflow;

import java.util.Arrays;
import java.util.HashMap;

/**
 * A flow network for scheduling clinicians at the UIUC Counseling Center
 * 
 * @author ramusa2
 *
 */
public class ECFlowNetwork {

    private final Node start;

    private final Node end;

    private final HashMap<String, Node> nodes;

    public ECFlowNetwork() {
	start = new Node("START");
	end = new Node("END");
	nodes = new HashMap<String, Node>();
    }

    public Node start() {
	return start;
    }

    public Node end() {
	return end;
    }

    public Node getNode(String label) {
	return nodes.get(label);
    }

    /**
     * Does not add the node if a node with the same label already exists
     */
    public void addNode(String label) {
	Node n = nodes.get(label);
	if (n == null) {
	    nodes.put(label, new Node(label));
	}
    }

    private void fillFlowNetwork() {
	Node[] path = findPath();
	while (path != null) {
	    pushFlow(path, 1);
	    path = findPath();
	}
    }

    private void pushFlow(Node[] path, int f) {
	if (path[0] != start || path[path.length - 1] != end) {
	    throw new IllegalArgumentException("Not a path!");
	}
	for (int i = 0; i < path.length - 1; i++) {
	    Node st = path[i];
	    Node en = path[i + 1];
	    st.addFlowTo(en, f);
	}
    }

    private Node[] findPath() {
	return findPathRecurse(start, new Node[] {});
    }

    private Node[] findPathRecurse(Node cur, Node[] path) {
	if (cur == end) {
	    Node[] finalPath = Arrays.copyOf(path, path.length + 1);
	    finalPath[finalPath.length - 1] = cur;
	    return finalPath;
	}
	Node[] nextPath = Arrays.copyOf(path, path.length + 1);
	for (Edge e : cur.edges()) {
	    // Note: could shuffle here
	    if (!e.isSaturated()) {
		// && !path.contains(e)) { // Danger: only works if network
		// doesn't contain loops.
		nextPath[nextPath.length - 1] = cur;
		Node[] p = findPathRecurse(e.end(), nextPath);
		if (p != null) {
		    return p;
		}
	    }
	}
	return null;
    }

    public int maxFlow() {
	this.fillFlowNetwork();
	return this.end().getIncomingFlow();
    }
}
