/*
 * This class the implementation of a node cell
 */

import java.awt.*;
import java.util.ArrayList;

public class Node{
	private Point pos;
	private Image image;
	private boolean occupied;
	private String occupant;
	
	// Default constructor
	public Node(){
		this.occupied = false;
	}

	// Initialized constructor
	public Node(Point pos, Image image, boolean occupied, String occupant){
		this.pos = pos;
		this.image =image;
		this.occupied = occupied;
		this.occupant = occupant;
	}

	// Accessors
	public Point getPos(){
		return pos;
	}
	
	public Image getImage(){
		return image;
	}

	public boolean isOccupied() { return occupied; }

	public String getOccupant() { return occupant; }
	
	public void setImage(Image image){
		this.image = image;
	}

	public void setOccupant(String occupant) {
		this.occupant = occupant;
	}
}
