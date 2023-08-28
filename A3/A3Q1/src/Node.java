/*
 * This class the implementation of a node cell
 */

import java.awt.*;

public class Node{
	private Point pos;
	private CellType type;

	// Constructor
	public Node (Point pos) {
		this.pos = pos;
		type = CellType.EMPTY;
	}
	
	// Accessors
	public Point getPos(){
		return pos;
	}

	public CellType getType() { return type; }

	// Setters
	public void setType(CellType type){
		this.type = type;
	}
}

enum CellType{
	EMPTY, BLACK, WHITE
}
