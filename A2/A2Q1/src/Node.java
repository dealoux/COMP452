/*
 * This class the implementation of a node cell
 */

import java.awt.*;
import java.util.ArrayList;

public class Node{
	private int cost;
	private Point pos;
	private Image image;
	
	// Default constructor
	public Node() {
		this.cost = -1;
	}
	
	// Constructor
	public Node (int cost, Point pos, Image image) {
		this.cost = cost;
		this.pos = pos;
		this.image = image;
	}
	
	// Accessors
	public Point getPos(){
		return pos;
	}
	
	public Image getImage(){
		return image;
	}

	public int getCost(){
		return cost;
	}

	// Setters
	public void setImage(Image image){
		this.image = image;
	}
}
