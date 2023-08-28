/*
 * This class the implementation of node record as detailed in the book
 * AI for Games, Third Edition by Ian Millington
 */

public class NodeRecord implements Comparable<NodeRecord>{
	private int node;// the node number
	private Connection connection;// connection that connects the node to the path
	private int costSoFar;// cost up to this node in the path
	private int estimatedTotalCost;// cost estimated from this node to the end of the path
	
	// Default constructor
	public NodeRecord(){
		this.node = 0;
		this.connection = null;
		this.costSoFar = 0;
		this.estimatedTotalCost = 0;
	}
	
	// Initialized constructor
	public NodeRecord(int node, Connection connection, int costSoFar, int estimatedTotalCost){
		this.node = node;
		this.connection = connection;
		this.costSoFar = costSoFar;
		this.estimatedTotalCost = estimatedTotalCost;
	}

	@Override
	public int compareTo(NodeRecord other){
		if(estimatedTotalCost > other.getEstimatedTotalCost())
			return 1;
		else if(estimatedTotalCost < other.getEstimatedTotalCost())
			return -1;
		else
			return 0;
	}

	// Getters
	public int getNode(){
		return this.node;
	}

	public Connection getConnection(){
		return this.connection;
	}

	public int getCostSoFar(){
		return this.costSoFar;
	}

	public int getEstimatedTotalCost(){
		return this.estimatedTotalCost;
	}

	// Result printing format
	public String toString(){
		return "Node: " + node + "Connection: " + connection + " Cost so far: " + costSoFar + " Total estimated cost: " + estimatedTotalCost;
	}
}
