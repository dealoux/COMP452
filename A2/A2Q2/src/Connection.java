/*
 * This class is the implementation of the Connection class as detailed in the book
 * AI for Games, Third Edition by Ian Millington
 */

public class Connection {
	private int cost; // cost of the connection
	private int fromNode; // origin node of the connection
	private int toNode; // end node of the connection
	
	// Constructor
	public Connection(int fromNode, int toNode, int cost){
		this.fromNode = fromNode;
		this.toNode = toNode;
		this.cost = cost;
	}
	
	// Getters
	public int getCost(){
		return this.cost;
	}

	public int getFromNode() {
		return this.fromNode;
	}

	public int getToNode() {
		return this.toNode;
	}
	
	// setCost method - allows the cost of the connection to be set to a specified value
	public void setCost(int cost) {
		this.cost = cost;
	}

	// Result printing format
	public String toString() {
		return toNode + "";
	}
}
