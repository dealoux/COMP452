/*
 * This class implements the Euclidean distance heuristic
 */

public class Heuristic{
	private int goalNode; // destination node the heuristic function tries to reach
	
	// Default constructor
	public Heuristic(int goalNode){
		this.goalNode = goalNode;
	}
	
	// This method calculates the positions of the given node, the goal node, and returns the Euclidean distance between them
	public int estimate(int node){
		int[] origin = new int[]{node % 16, node / 16};
		int[] goal = new int[]{goalNode % 16, goalNode / 16};

		return length(origin, goal);
	}
	
	// This method returns the Euclidean distance between 2 given points p1 and p2
	public static int length(int[] p1, int[] p2){
		return (int)Math.floor(Math.sqrt((p2[0]-p1[0])*(p2[0]-p1[0]) + (p2[1]-p1[1])*(p2[1]-p1[1])));
	}
}
