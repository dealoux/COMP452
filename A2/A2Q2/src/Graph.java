/*
 * This class is the implementation of the graph in game space
 */

import java.util.ArrayList;

public class Graph{
	private ArrayList<Connection> connections; // list of edges
	
	// Default constructor
	public Graph(){
		this.connections = new ArrayList<>();// creates an empty set of connections
	}
	
	// getConnections() accessor
	public ArrayList<Connection> getConnections(){
		return this.connections;
	}
	
	// This function returns a list of connections that originate from the given fromNode
	public ArrayList<Connection> getConnections(int fromNode){
		ArrayList<Connection> result = new ArrayList<>();

		// loop through the graph
		for(Connection c : this.connections){
			if (c.getFromNode() == fromNode)
				result.add(c);
		}

		return result;
	}
	
	// This function adds a single connection to the graph
	public void add(Connection connection){
		this.connections.add(connection);
	}
	
	// This function adds a list of connections to the graph
	public void add(ArrayList<Connection> connection){
		this.connections.addAll(connection);
	}
}