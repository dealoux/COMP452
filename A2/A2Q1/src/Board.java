/*
 * This class sets up and display the game board visually as well as handling most of the game logic
 */

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;


public class Board extends JPanel implements ActionListener, MouseListener {
	private final int CELLS_PER_ROW = 16; // number of cells on a single row/col
	private final int TOTAL_CELLS = CELLS_PER_ROW * CELLS_PER_ROW; // total number of cells in the game space
	private Node[] nodes = new Node[TOTAL_CELLS]; // used to represent each cell node in the Game space
	private final int CELL_SIZE = 40; // size of a single cell
	private final int SCREEN_SIZE = CELL_SIZE * CELLS_PER_ROW; // Size of the screen
	private final int INFO_PANEL_SIZE = (CELL_SIZE > 54) ? CELL_SIZE : 55;

	private final int OPEN_TERRAIN = 0;
	private final int GRASSLAND = 1;
	private final int SWAMPLAND = 2;
	private final int OBSTACLE = 3;
	private final String[] ICONS = {"terrain.png", "grass.png", "swamp.png", "rock.png", "terrainPath.png", "grassPath.png", "swampPath.png"};
	private final int[] COSTS = {1, 3, 4, 5};
	private final int EMPTY = -1;
	private final int PATH_OFFSET = 4;
	private final int NO_COST = 0;

	private Graph graph = new Graph();// the graph used to represent the Game space
	private int start = 0;// used to represent the start node in the search
	private int goal = 0;// used to represent the goal node in the search
	
	private ArrayList<Connection> results; // result of the A* pathfinding algorithm
	private int resultCount = 0;// used to hold the number of connections in the results of the Pathfinding search
	private boolean done = false; // determine if the game is done and the result should be displayed
	private int gameTime = 0; // runtime of the game

	private Timer timer; // delay timer
	private final int DELAY = 10; // delay for 10s between event

	private File buttonSound = new File("buttonPress.wav");// sound for button presses
	private File progressSound = new File("progressSound.wav");// sound for progressing path nodes
	private File pathFound = new File("pathFound.wav");// sound emitted when a path is found
	private File clearSound = new File("clearSound.wav"); // sound for when clearing the board

	private boolean addAnt = false;// used to determine when an ant has or hasn't been added to the board
	private boolean addDonut = false;// used to determine when a donut has or hasn't been added to the board
	private boolean shouldRandomize = true;// used to determine when tiles should be filled with random entities
	private boolean shouldClearBoard = false;// used to determine when a blank board is in use
	private boolean executing = false;// used to determine whether a path is active

	private JPanel infoPane = new JPanel();// the panel to hold the path details and the buttons
	private JTextField pathDetails = new JTextField();// holds the path details
	private JPanel statusPane = new JPanel();// adds a JPanel to hold the path details of the result
	private JPanel buttonPane = new JPanel(new GridLayout(1, 3)); // adds a JPanel to hold the buttons
	private JButton random = new JButton("Random Fill");// the button to randomize the tiles on the board
	private JButton execute = new JButton("Execute");// the button to execute the search of the tiles on the board
	private JButton clearBoard = new JButton("Clear Board");// the button to remove the tiles on the board

	// Default constructor
	public Board(){
		// Game board settings
		addMouseListener(this);
		setFocusable(true);
		setPreferredSize(new Dimension(SCREEN_SIZE, SCREEN_SIZE + INFO_PANEL_SIZE));
		setBackground(Color.WHITE);
		setDoubleBuffered(true);
		setLayout(new BorderLayout());

		// UI panels
		infoPane.setLayout(new GridLayout(2,1));
		pathDetails.setSize(SCREEN_SIZE, INFO_PANEL_SIZE/2); // sets the size of the path details box
		statusPane.setLayout(new GridLayout(1, 1)); // sets the layout for the status JPanel pane
		statusPane.add(pathDetails); // adds the path details JTextField to the status pane
		infoPane.add(statusPane, BorderLayout.SOUTH); // adds the statusPane holding the path details to the display

		// Buttons settings
		random.addActionListener(this);
		execute.addActionListener(this);
		clearBoard.addActionListener(this);
		buttonPane.setLayout(new GridLayout(1, 3));
		buttonPane.add(clearBoard);
		buttonPane.add(random);
		buttonPane.add(execute);
		
		infoPane.add(buttonPane, BorderLayout.SOUTH);// adds the buttonPane, holding the buttons, to the display
		add(infoPane, BorderLayout.SOUTH);// adds the infoPane to the JPanel displaying the game
		
		for (int i = 0; i < nodes.length; i++)// loops through the game nodes
			nodes[i] = new Node();// fills each element with a blank node to start
		
		clearBoard.doClick();// calls the method to populate the board with random tiles to start
		
		timer = new Timer(DELAY, this);// creates timer with between-event DELAY 
		timer.start();
	}

	// This function is the main game loop
	@Override
	public void actionPerformed(ActionEvent e) {
		// checks if the board should be randomized
		if (e.getSource() == random) {
			playSound(buttonSound);
			randomize();
			execute.setEnabled(true);
			resultCount = 0;
			done = executing = false;
		}

		// checks if the game should execute
		else if (e.getSource() == execute){
			execute();
			shouldRandomize = done = executing = true;
			shouldClearBoard = addAnt = addDonut = false;
			execute.setEnabled(false);// disables execute button
		}

		// checks if the board should be cleared
		else if (e.getSource() == clearBoard) {
			playSound(clearSound);
			shouldClearBoard = true;
			addAnt = addDonut = done = shouldRandomize = executing = false;
			clear();
			execute.setEnabled(true);
			resultCount = 0;
		}

		repaint();// repaints with current information
	}

	// This function generates a randomly filled game board
	public void randomize() {
		Random random = new Random();
		int type; // decide which type of terrain

		for (int i = 0; i < CELLS_PER_ROW; i++) {
			for (int j = 0; j < CELLS_PER_ROW; j++) {
				type = random.nextInt(COSTS[SWAMPLAND]);
				int index = i * CELLS_PER_ROW + j;
				if (nodes[index].getCost() == -1 || shouldRandomize) {// if tile empty or random chosen
					nodes[index] = new Node(COSTS[type], new Point(i * SCREEN_SIZE / CELLS_PER_ROW, j * SCREEN_SIZE / CELLS_PER_ROW), new ImageIcon(ICONS[type]).getImage());// creates random tile
				}
			}
		}

		// checks whether an ant needs to be added
		if ( (!addAnt && shouldClearBoard) || !shouldClearBoard) {
			start = random.nextInt(TOTAL_CELLS);
			Point pos = nodes[start].getPos();
			nodes[start] = new Node(NO_COST, pos, new ImageIcon("ant.png").getImage());
			addAnt = true;
		}

		// checks whether a donut needs to be added
		if ( (!addDonut && shouldClearBoard) || !shouldClearBoard) {
			goal = random.nextInt(TOTAL_CELLS);
			Point pos = nodes[goal].getPos();
			nodes[goal] = new Node(NO_COST, pos, new ImageIcon("donut.png").getImage());
			addDonut = true;
		}
	}

	// This function make sure the graph is always filled during execution, preventing infinite loop
	private void fillCheck(){
		for(Node n : nodes){
			if(n.getCost() == -1){
				randomize();
				break;
			}
		}
	}

	// This function runs the A* pathfinding algorithm and return the result if found
	public void execute() {
		fillCheck(); // make sure the graph is filled before execution

		for (int i = 0; i < CELLS_PER_ROW; i++) {
			for (int j = 0; j < CELLS_PER_ROW; j++) {
				addConnections(i, j);
			}
		}
		
		ArrayList<Connection> temp = new ArrayList<>();
		for (int i = 0; i < graph.getConnections().size(); i++) {
			if (nodes[graph.getConnections().get(i).getToNode()].getCost() != COSTS[OBSTACLE])
				graph.getConnections().get(i).setCost(nodes[graph.getConnections().get(i).getToNode()].getCost());// sets cost for each connection
			else
				temp.add(graph.getConnections().get(i));
		}

		for (Connection connection : temp)
			graph.getConnections().remove(connection);

		Heuristic heuristic = new Heuristic(goal);// creates new heuristic with goal node specified
		results = AStar.pathFindAStar(graph, start, goal, heuristic);// calls A* pathfinding method and receives results
		
		int cost = 0; // total cost of the path
		// checks if a path was found
		if (results != null) {
			for (int i = 0; i < results.size(); i++)
				cost += results.get(i).getCost();
		}
		pathDetails.setText("Path: " + start + "->" + results + " Cost: " + cost);// displays path details
	}
	
	// This method replaces all the cells in the game space with blank cells
	public void clear(){
		pathDetails.setText("Please click where you'd like to start the ant.");// displays message to the user
		for (int i = 0; i < TOTAL_CELLS; i++){
			nodes[i] = new Node(-1, new Point(i / CELLS_PER_ROW * CELL_SIZE, i % CELLS_PER_ROW * CELL_SIZE), new ImageIcon("empty.png").getImage());
		}
	}
	
	// This function add all the possibles connections to the given node position
	public void addConnections(int i, int j) {
		int fromNode = i * CELLS_PER_ROW + j;// origin node of the connection
		int toNode;// destination node of the connection
		// Cells range from 0 to CELL_PER_ROW - 1 (15 in this case)
		int minRange = 0;
		int maxRange = CELLS_PER_ROW - 1;

		// Border checks
		if (i > minRange && j > minRange) {
			toNode = (i-1) * CELLS_PER_ROW + (j-1);
			graph.add(new Connection(fromNode, toNode, NO_COST));
		}
		if (i > minRange) {
			toNode = (i-1) * CELLS_PER_ROW + (j);
			graph.add(new Connection(fromNode, toNode, NO_COST));
		}
		if (i > minRange && j < maxRange) {
			toNode = (i-1) * CELLS_PER_ROW + (j+1);
			graph.add(new Connection(fromNode, toNode, NO_COST));
		}
		if (j > minRange) {
			toNode = (i) * CELLS_PER_ROW + (j-1);
			graph.add(new Connection(fromNode, toNode, NO_COST));
		}
		if (j < maxRange) {
			toNode = (i) * CELLS_PER_ROW + (j+1);
			graph.add(new Connection(fromNode, toNode, NO_COST));
		}
		if (i < maxRange && j > minRange) {
			toNode = (i+1) * CELLS_PER_ROW + (j-1);
			graph.add(new Connection(fromNode, toNode, NO_COST));
		}
		if (i < maxRange) {
			toNode = (i+1) * CELLS_PER_ROW + (j);
			graph.add(new Connection(fromNode, toNode, NO_COST));
		}
		if (i < maxRange && j < maxRange) {
			toNode = (i+1) * CELLS_PER_ROW + (j+1);
			graph.add(new Connection(fromNode, toNode, NO_COST));
		}
	}

	// This function handle mouse click events
	public void mousePressed(MouseEvent e) {
		// only check for input if the game isn't executing
		if(!executing){
			Point posMouse = e.getPoint(); // coordinate of the mouse
			// check if the click is within the bounds of the game space
			if (posMouse.x < SCREEN_SIZE && posMouse.y < SCREEN_SIZE) {
				// Get the correct starting position of the cell
				int x = (int)Math.floor(posMouse.x / CELL_SIZE);
				int y = (int)Math.floor(posMouse.y / CELL_SIZE);
				Point posCell = new Point(posMouse.x - posMouse.x % CELL_SIZE, posMouse.y - posMouse.y % CELL_SIZE);
				int index = x * CELLS_PER_ROW + y;

				// checks if an ant needs to be added
				if (!addAnt && shouldClearBoard) {
					start = index;// gets the node number and sets it to start
					nodes[index] = new Node(NO_COST, posCell, new ImageIcon("ant.png").getImage());
					pathDetails.setText("Please click where you'd like to set the donut.");
					addAnt = true;// sets flag to indicate ant added
				}
				// else checks if a donut needs to be added
				else if (!addDonut && shouldClearBoard) {
					goal = index;// gets the node number and sets it to goal
					nodes[index] = new Node(NO_COST, posCell, new ImageIcon("donut.png").getImage());
					pathDetails.setText("Please add all desired obstacles and/or terrains, then click randomize or execute.");
					addDonut = true;// sets flag to indicate donut added
				}
				// else both the start and goal point are initialized, terrain cycling
				else{
					int cost = nodes[index].getCost();

					// if the current cell is either empty or obstacle, set it to open terrain
					if(cost == EMPTY || cost == COSTS[OBSTACLE])
						nodes[index] = new Node(COSTS[OPEN_TERRAIN], posCell, new ImageIcon(ICONS[OPEN_TERRAIN]).getImage());
						// else if the current cell is open terrain, set it to grassland
					else if(cost == COSTS[OPEN_TERRAIN])
						nodes[index] = new Node(COSTS[GRASSLAND], posCell, new ImageIcon(ICONS[GRASSLAND]).getImage());
						// else if the current cell is grassland, set it to swampland
					else if(cost == COSTS[GRASSLAND])
						nodes[index] = new Node(COSTS[SWAMPLAND], posCell, new ImageIcon(ICONS[SWAMPLAND]).getImage());
						// else if the current cell is swampland, set it to obstacle
					else if(cost == COSTS[SWAMPLAND])
						nodes[index] = new Node(COSTS[OBSTACLE], posCell, new ImageIcon(ICONS[OBSTACLE]).getImage());
				}
				playSound(buttonSound);
			}
		}
	}
	
	// MouseListener events
	public void mouseReleased(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mouseClicked(MouseEvent e) { }

	// This method plays a given sound file
	public void playSound(File sound) {
		// try to perform audio tasks
		try {
			AudioInputStream swampIn = AudioSystem.getAudioInputStream(sound);// gets input stream from file
			Clip clip = AudioSystem.getClip();// gets a clip to use with input
			clip.open(swampIn);
			clip.start();
		} catch (Exception e) { e.printStackTrace(); }
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		doDrawing(g);
		Toolkit.getDefaultToolkit().sync();// synchronizes the toolkit state
	}

	// This function loops through the graph and draw all cells in game space
	// Also manages logic functions for displaying the game cells and printing the result path
	private void doDrawing(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		int index;

		for (int i = 0; i < CELLS_PER_ROW; i++) {
			for (int j = 0; j < CELLS_PER_ROW; j++) {
				index = i * CELLS_PER_ROW + j;
				g2.drawImage(nodes[index].getImage(), nodes[index].getPos().x, nodes[index].getPos().y, this);// draws each tile with its current information
			}
		}
		// checks if results are ready to be displayed
		if (done && results != null && resultCount < results.size()) {
			// staggers the evolution of the display of the search path results
			if (gameTime++ % 75 == 0) {
				// checks for end of path
				if (resultCount == results.size()-1){
					playSound(pathFound);
					done = false;
				}
				// checks for obstacle tiles on the path
				else if(nodes[results.get(resultCount).getToNode()].getCost() == COSTS[OBSTACLE]){
					done = false;// sets flag to indicate result path is done
					resultCount = 0;// resets result count
				}
				else{
					switch(nodes[results.get(resultCount).getToNode()].getCost()){
						case 1:
							nodes[results.get(resultCount++).getToNode()].setImage(new ImageIcon(ICONS[OPEN_TERRAIN + PATH_OFFSET]).getImage());
							break;
						case 3:
							nodes[results.get(resultCount++).getToNode()].setImage(new ImageIcon(ICONS[GRASSLAND + PATH_OFFSET]).getImage());
							break;
						case 4:
							nodes[results.get(resultCount++).getToNode()].setImage(new ImageIcon(ICONS[SWAMPLAND + PATH_OFFSET]).getImage());
							break;
					}
					playSound(progressSound);
				}
			}
		}
		// if no results are currently ready
		else{
			resultCount = 0;
			done = false;
		}
	}
}