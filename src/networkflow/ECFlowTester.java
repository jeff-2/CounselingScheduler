package networkflow;

public class ECFlowTester {
	
	public static void main(String[] args) {
		ECFlowNetwork network = getSampleNetwork();
		System.out.println(network.maxFlow());
	}
	
	private static ECFlowNetwork getSampleNetwork() {
		ECFlowNetwork network = new ECFlowNetwork();
		Node start = network.start();
		String[] labels = new String[]{"A", "B", "C", "D"};
		for(String l : labels) {
			network.addNode(l);
		}
		Node a = network.getNode("A");
		Node b = network.getNode("B");
		Node c = network.getNode("C");
		Node d = network.getNode("D");
		start.createEdgeTo(a, 5);
		start.createEdgeTo(b, 7);
		a.createEdgeTo(c, 5);
		a.createEdgeTo(d, 1);
		b.createEdgeTo(c, 2);
		b.createEdgeTo(d, 3);
		c.createEdgeTo(network.end(), 10);
		d.createEdgeTo(network.end(), 2);
		return network;
	}

}
