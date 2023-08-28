/*
 * This class is the implementation of the A* pathfinding algorithm as detailed in the book
 * AI for Games, Third Edition by Ian Millington
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

public class AStar{
	private static PriorityQueue<NodeRecord> open; // list of nodes that are unexplored
	private static PriorityQueue<NodeRecord> closed; // list of nodes that has been explored

	// This function
	public static ArrayList<Connection> pathFindAStar(Graph graph, int start, int goal, Heuristic heuristic){
		NodeRecord current = new NodeRecord();
		ArrayList<Connection> connections;
		int endNode;
		int endNodeCost;
		NodeRecord endNodeRecord;
		int endNodeHeuristic;

		// Initialize the record for the start node.
		NodeRecord startRecord = new NodeRecord(start, null, 0, heuristic.estimate(start));

		open = new PriorityQueue<>();
		open.add(startRecord);
		closed = new PriorityQueue<>();

		// Iterate through processing each node
		while(open.size() > 0){
			current = open.poll(); // smallest element as this is a priority queue

			// terminates if the current node is the goal node
			if(current.getNode() == goal)
				break;
			// otherwise get its outgoing connections
			else
				connections = graph.getConnections(current.getNode());

			// Loop through each connection in turn
			for(Connection connection : connections){
				// get the cost estimate for the end node
				endNode = connection.getToNode();
				endNodeCost = current.getCostSoFar() + connection.getCost();

				// if the node is closed we may have to skip, or remove it from the closed it
				if (has(closed, endNode)){
					// find the record in the closed list corresponding to the endNode
					endNodeRecord = find(closed, endNode);

					// skip if we didn't find a shorter route
					if (endNodeRecord.getCostSoFar() <= endNodeCost)
						continue;

					// otherwise remove it from the closed list;
					closed.remove(endNodeRecord);

					// we can use the node's old cost values to calculate its heuristic without calling the possibly expensive heuristic function
					endNodeHeuristic = endNodeRecord.getEstimatedTotalCost() - endNodeRecord.getCostSoFar();
				}
				// skip if the node is open and a better route was not found
				else if(has(open, endNode)){
					// find the record in the open list corresponding to the endNode
					endNodeRecord = find(open, endNode);

					// skip if our route is no better
					if (endNodeRecord.getCostSoFar() <= endNodeCost)
						continue;

					// again calculate its heuristic
					endNodeHeuristic = endNodeRecord.getEstimatedTotalCost() - endNodeRecord.getCostSoFar();
				}
				// Otherwise we know we’ve got an unvisited node, so make a record for it
				else
					endNodeHeuristic = heuristic.estimate(endNode);

				endNodeRecord = new NodeRecord(endNode, connection, endNodeCost, endNodeCost + endNodeHeuristic);
				
				if (!has(open, endNode))
					open.add(endNodeRecord);
			}

			open.remove(current);
			closed.add(current);
		}

		// We are here if we have either found the goal, or if we have no more nodes to search, find which
		if(current.getNode() != goal)
			// We have run out of nodes without finding the goal, so there's no solution
			return null;
		else{
			// Compile the list of connections in the path
			ArrayList<Connection> path = new ArrayList<>();

			// Work back along the path, accumulating connections
			while (current.getNode() != start) {
				path.add(current.getConnection());
				current = find(closed, current.getConnection().getFromNode());
			}

			// Reverse the path, and return it.
			Collections.reverse(path);
			return path;
		}
	}

	// This function returns true if there exists a record that correspond to the given index
	private static boolean has(PriorityQueue<NodeRecord> queue, int index){
		boolean result = false;

		for (NodeRecord listNode : queue){
			if (listNode.getNode() == index){
				result = true;
				break;
			}
		}

		return result;
	}

	// This function returns the record that correspond to the given index
	private static NodeRecord find(PriorityQueue<NodeRecord> queue, int index){
		NodeRecord result = null;

		for (NodeRecord record : queue) {
			if (record.getNode() == index){
				result = record;
				break;
			}
		}

		return result;
	}
}
