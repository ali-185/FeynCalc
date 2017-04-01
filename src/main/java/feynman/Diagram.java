package feynman;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import standardModel.Particle;

/**
 * The model for a Feynman Diagram.
 * @author Alastair Crowe
 */
public class Diagram implements Serializable {
	private static final long serialVersionUID = 1L;
	private class Node {
		final String name;
		final int vertex; 
		final Particle particle;
		final int index;      // Index in nodes array
		int connection = -1;  // Index of connected node
		Node(String name, int vertex, Particle particle, int index) {
			this.name = name;
			this.vertex = vertex;
			this.particle = particle;
			this.index = index;
		}
		@Override
		public String toString() {
			if (connection == -1) {
				return name + "{"+ particle + "}";
			} else {
				return name + "{"+ particle + "}(" + nodes.get(connection).name + ")";
			}
		}
	}
	/* Incoming/Outgoing nodes are treated as one vertex. */
	private static final int INCOMING_VERTEX = -2;
	private static final int OUTGOING_VERTEX = -1;
	/* Number of nodes. */
	private final int noOfIncoming;
	private final int noOfOutgoing;
	private final int noOfVertices;
	private final int size;
	/* List of all nodes. In order of Incoming, Outgoing, Vertices. */
	private final ArrayList<Node> nodes;
	/* List of unconnected node indices corresponding to nodes List. */
	private final ArrayList<Integer> unconnected;
	/**
	 * Creates an unconnected Diagram.
	 * @param incomingElectrons List of unique names.
	 * @param incomingPositrons List of unique names.
	 * @param incomingPhotons List of unique names.
	 * @param outgoingElectrons List of unique names.
	 * @param outgoingPositrons List of unique names.
	 * @param outgoingPhotons List of unique names.
	 * @param vertices List of unique names.
	 */
	public Diagram(List<String> incomingElectrons, List<String> incomingPositrons, List<String> incomingPhotons,
			List<String> outgoingElectrons, List<String> outgoingPositrons, List<String> outgoingPhotons,
			List<String> vertices) {
		noOfIncoming = incomingElectrons.size() + incomingPositrons.size() + incomingPhotons.size();
		noOfOutgoing = outgoingElectrons.size() + outgoingPositrons.size() + outgoingPhotons.size();
		noOfVertices = vertices.size();
		size = noOfIncoming + noOfOutgoing + 3 * noOfVertices;
		nodes = new ArrayList<Node>(size);
		int index = 0;
		for(String name: incomingElectrons) {
			nodes.add(new Node(name, INCOMING_VERTEX, Particle.ELECTRON, index++));
		}
		for(String name: incomingPositrons) {
			nodes.add(new Node(name, INCOMING_VERTEX, Particle.ANTI_ELECTRON, index++));
		}
		for(String name: incomingPhotons) {
			nodes.add(new Node(name, INCOMING_VERTEX, Particle.PHOTON, index++));
		}
		for(String name: outgoingElectrons) {
			nodes.add(new Node(name, OUTGOING_VERTEX, Particle.ELECTRON, index++));
		}
		for(String name: outgoingPositrons) {
			nodes.add(new Node(name, OUTGOING_VERTEX, Particle.ANTI_ELECTRON, index++));
		}
		for(String name: outgoingPhotons) {
			nodes.add(new Node(name, OUTGOING_VERTEX, Particle.PHOTON, index++));
		}
		for(int vertex = 0; vertex < vertices.size(); vertex++) {
			String name = vertices.get(vertex);
			nodes.add(new Node(name, vertex, Particle.ELECTRON, index++));
			nodes.add(new Node(name, vertex, Particle.ANTI_ELECTRON, index++));
			nodes.add(new Node(name, vertex, Particle.PHOTON, index++));
		}
		unconnected = new ArrayList<Integer>(size);
		for(int i = 0; i < nodes.size(); i++) {
			unconnected.add(i);
		}
	}
	/**
	 * @param shareNodes
	 * If true then the diagrams share nodes. 
	 * WARNING: false is not yet implemented.
	 */
	private Diagram(Diagram copy, boolean shareNodes) {
		this.noOfIncoming = copy.noOfIncoming;
		this.noOfOutgoing = copy.noOfOutgoing;
		this.noOfVertices = copy.noOfVertices;
		this.size = copy.size;
		if(shareNodes) {
			this.nodes = copy.nodes;
		} else {
			throw new UnsupportedOperationException();
		}
		this.unconnected = new ArrayList<Integer>(copy.unconnected);
	}
	/** 
	 * @return 
	 * Ascending list of all other indices with the same vertex. 
	 */
	private List<Integer> getVertex(int index) {
		Node node = nodes.get(index);
		int vertex = node.vertex;
		if(vertex == INCOMING_VERTEX || vertex == OUTGOING_VERTEX) {
			return new ArrayList<Integer>(0);
		} else {
			int i0 = noOfIncoming + noOfOutgoing + 3 * vertex;
			ArrayList<Integer> result = new ArrayList<Integer>(2);
			for(int i = i0; i < i0 + 3; i++) {
				if(i != index) {
					result.add(i);
				}
			}
			return result;
		}
	}
	private boolean isConnected() {
		if(!unconnected.isEmpty()) {
			return false;
		}
		ArrayList<Boolean> connected = new ArrayList<Boolean>(size);
		connected.addAll(Collections.nCopies(size, Boolean.FALSE));
		ArrayList<Integer> pending = new ArrayList<Integer>();
		pending.add(0);
		pending.addAll(getVertex(0));
		while(!pending.isEmpty()) {
			int index = pending.remove(pending.size() - 1);
			Node node = nodes.get(index);
			int connection = node.connection;
			if(!connected.get(index)) {
				connected.set(index, Boolean.TRUE);
				connected.set(connection, Boolean.TRUE);
				pending.addAll(getVertex(connection));
			}
		}
		return !connected.contains(false);
	}
	/* Checks if connection is valid.
	 * @param uNodeIndex1, uNodeIndex2
	 * Index in unconnected array
	 */
	private boolean isValidConnection(int uNodeIndex1, int uNodeIndex2) {
		Node node1 = nodes.get(unconnected.get(uNodeIndex1));
		Node node2 = nodes.get(unconnected.get(uNodeIndex2));
		if(!node1.particle.equals(Particle.getAntiParticle(node2.particle))) {
			return false;
		}
		if(node1.vertex == node2.vertex) {
			return false;
		}
		return true;
	}
	/* Sets the connection. Assumes connection is valid. 
	 * @param uNodeIndex1, uNodeIndex2
	 * Index in unconnected array
	 */
	private void setValidConnection(int uNodeIndex1, int uNodeIndex2) {
		Node node1 = nodes.get(unconnected.get(uNodeIndex1));
		Node node2 = nodes.get(unconnected.get(uNodeIndex2));
		node1.connection = node2.index;
		node2.connection = node1.index;
		int maxIndex = Math.max(uNodeIndex1, uNodeIndex2);
		int minIndex = Math.min(uNodeIndex1, uNodeIndex2);
		unconnected.remove(maxIndex);
		unconnected.remove(minIndex);
	}
	/**
	 * @param shareNodes see Diagram(Diagram, boolean)
	 * @return An iterator of each sub-diagram by creating only new
	 * new connections. Each sub-diagram is unique.
	 */
	public Iterator<Diagram> getSubDiagrams(final boolean shareNodes) {
		return new Iterator<Diagram>() {
			// Flag indicating if returned self
			private boolean self = false;
			private Diagram next;
			private Iterator<Diagram> itr;
			private int uNodeIndex1 = unconnected.size() - 1;
			private int uNodeIndex2 = unconnected.size() - 2;
			public boolean hasNext() {
				if(next == null) {
					next = next();
				}
				return next != null;
			}
			public Diagram next() {
				if (next == null) {
					if(isConnected() && !self) {
						self = true;
						next = Diagram.this;
					} else if(itr != null && itr.hasNext()) {
						next = itr.next();
					} else {
						while(uNodeIndex2 >= 0) {
							if(isValidConnection(uNodeIndex1, uNodeIndex2)) {
								Diagram diagram = new Diagram(Diagram.this, shareNodes);
								diagram.setValidConnection(uNodeIndex1, uNodeIndex2);
								itr = diagram.getSubDiagrams(shareNodes);
								if(itr.hasNext()) {
									next = itr.next();
									uNodeIndex2--;
									break;
								}
							}
							uNodeIndex2--;
						}
					}
				}
				Diagram curr = next;
				next = null;
				return curr;			
			}
		};
	}
	/**
	 * @param particle Particle of which to find connections.
	 * @return
	 * Returns a Map of connections for the specified particle. In O(size()) time.
	 */
	public HashMap<String, String> getConnections(Particle particle) {
		boolean isOwnAnti = Particle.getAntiParticle(particle).equals(particle);
		HashMap<String, String> map = new HashMap<String, String>();
		for(Node node: nodes) {
			if(node.connection != -1 && node.particle.equals(particle)) {
				if(!isOwnAnti || node.index < node.connection) {
					map.put(node.name, nodes.get(node.connection).name);
				}
				
			}
		}
		return map;
	}
	@Override
	public String toString() {
		return "Nodes: " + nodes + "\n";
	}
}
