/*
 * This class sets up and display the game board visually as well as handling most of the game logic
 */

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;


public class Board extends JPanel implements ActionListener, MouseListener {
	private final int CELLS_PER_ROW = 30; // number of cells on a single row/col
	private final int TOTAL_CELLS = CELLS_PER_ROW * CELLS_PER_ROW; // total number of cells in the game space
	private Node[] nodes = new Node[TOTAL_CELLS]; // used to represent each cell node in the Game space
	private final int CELL_SIZE = 25; // size of a single cell
	private final int SCREEN_SIZE = CELL_SIZE * CELLS_PER_ROW; // Size of the screen
	private final int INFO_PANEL_SIZE = 20;
	private final int NO_COST = 0;

	AntManager manager = new AntManager();
	private int spawnLocation = -1;
	private ArrayList<Integer> foodLocations = new ArrayList<>();
	private ArrayList<Integer> waterLocations = new ArrayList<>();
	private ArrayList<Integer> poisonLocations = new ArrayList<>();

	private Graph graph = new Graph();// the graph used to represent the Game space
	private final String[] ICONS = {"food.png", "water.png", "terrain.png", "poison.png"};// the different images used for the tiles
	private final String[] OCCUPANTS = {"food", "water", "terrain", "poison"};// the different objects used in the game
	private Image food, water, terrain, poison, ant, antHill, antWithFood, antInWater, antInHill;// images used in the game

	private JPanel buttonPane = new JPanel(new GridLayout(1, 3)); // adds a JPanel to hold the buttons
	private JButton execute = new JButton("Execute");// the button to execute the search of the tiles on the board
	private JButton clearBoard = new JButton("Clear Board");// the button to remove the tiles on the board

	private int start = 0;// used to represent the start node in the search, where the ant is
	private boolean executing = false; // flag whether the game is running
	private Timer timer; // delay timer
	private final int DELAY = 10; // delay for 10s between event

	private File buttonSound = new File("buttonPress.wav");// sound for button presses
	private File startSound = new File("pathFound.wav");// sound for when execution start
	private File clearSound = new File("clearSound.wav"); // sound for when clearing the board

	public class AntManager {
		public int currentSpeed = 0;
		public int simulationDelay = 10;
		private Image antImage = new ImageIcon("ant.png").getImage();
		private Transition foundFood, foundHome, foundWater, foundPoison;
		private State foodSearch, homeSearch, waterSearch;

		public ArrayList<Entity> antList;

		public AntManager(){
			antList= new ArrayList<>();

			foundFood = new Transition(homeSearch, new String[]{"PickupFood"});
			foundHome = new Transition(waterSearch, new String[]{"InAntHill"});
			foundWater = new Transition(foodSearch, new String[]{"DrinkWater"});
			foundPoison = new Transition(null, new String[]{"Died"});

			foodSearch = new State(new String[]{"SearchForFood"}, new String[]{"DrinkWater"}, new String[]{"PickupFood"}, new Transition[]{foundFood, foundPoison});
			homeSearch = new State(new String[]{"SearchForHome"}, new String[]{"PickUpFood"}, new String[]{"EnterAntHill"}, new Transition[]{foundHome, foundPoison});
			waterSearch = new State(new String[]{"SearchForWater"}, new String[]{"LeaveAntHill"}, new String[]{"DrinkWater"}, new Transition[]{foundWater, foundPoison});
		}

		public boolean initialized(){
			return spawnLocation > -1;
		}

		public void reset(){
			antList= new ArrayList<>();
			spawnLocation = -1;
		}

		public void spawnAnt(){
			if(!initialized()){
				Random random = new Random();
				int index = random.nextInt(TOTAL_CELLS);
				while(nodes[index].isOccupied())
					index = spawnLocation = random.nextInt(TOTAL_CELLS);
			}

			Entity newAnt = new Entity(spawnLocation, antImage);
			newAnt.stateMachine = new StateMachine(foodSearch);

			newAnt.states.add(foodSearch);
			newAnt.states.add(homeSearch);
			newAnt.states.add(waterSearch);

			antList.add(newAnt);
		}

		public synchronized void simulate(){
			ArrayList<Entity> deadAnts = new ArrayList<>();// holds all ants killed during current loop
			ArrayList<Integer> bornAnts = new ArrayList<Integer>();// holds all ants
			for (int i = 0; i < antList.size(); i++) {// loops through all the ants
				int deadAnt = 999999;// determines whether an ant has died and holds the index
				int antBorn = 999999;// determines whether an ant has been born and holds the index

				Entity iAnt = antList.get(i);// creates duplicate of each ant, necessary for deletion purposes

				// checks if the Ant has any current actions to complete, if not get from the state machine
				if (iAnt.getActions().isEmpty())
					iAnt.setActions(iAnt.stateMachine.update());

				ArrayList<String> actions = iAnt.getActions();
				if(actions.size() == 0 ) return;

				String action = actions.remove(0);// gets the first action in the list

				int newPosition = iAnt.getPosition();// will hold the next position of the ant
				boolean alive = true;// used to flag ant deaths
				iAnt.stateMachine.getCurrentState().getTransition(foundFood).setTrigger(false);
				iAnt.stateMachine.getCurrentState().getTransition(foundHome).setTrigger(false);
				iAnt.stateMachine.getCurrentState().getTransition(foundWater).setTrigger(false);

				switch(action) {// switch handles the different actions registered by the FSM
					case ("Died"):
						nodes[iAnt.getPosition()].setImage(terrain);// sets the node image to terrain
						nodes[iAnt.getPosition()].setOccupant("terrain");// sets the node occupant to terrain
						alive = false;
						break;// breaks from switch
					case ("SearchForFood"):
						newPosition = getAStar(iAnt, (int)(Math.random() * foodLocations.size()));// gets new position through wander method
						nodes[iAnt.getPosition()].setImage(new ImageIcon(nodes[iAnt.getPosition()].getOccupant() + ".png").getImage());// sets current node image as previous occupant
						iAnt.setPosition(newPosition);// updates the ant position
						nodes[iAnt.getPosition()].setImage(ant);// sets the new node image as the ant
						if (nodes[newPosition].getOccupant().equals("food")) {// checks if food has been found
							iAnt.stateMachine.getCurrentState().getTransition(foundFood).setTrigger(true);// updates foundFood trigger
						}
						break;
					case ("PickupFood"):
						nodes[iAnt.getPosition()].setImage(antWithFood);// sets node image to ant with food
						nodes[iAnt.getPosition()].setOccupant("terrain");// sets node occupant to terrain
						break;// breaks from switch
					case ("EnterAntHill"):
					case ("LeaveAntHill"):
						nodes[iAnt.getPosition()].setImage(antInHill);// updates node image to ant in hill
						break;// breaks from switch
					case ("InAntHill"):
						nodes[iAnt.getPosition()].setImage(antHill);// updates node image to ant hill
						break;// breaks from switch
					case ("SearchForHome"):
						newPosition = getAStar(iAnt, spawnLocation);// gets new position from A*
						nodes[iAnt.getPosition()].setImage(new ImageIcon(nodes[iAnt.getPosition()].getOccupant() + ".png").getImage());// updates old node image to current occupant
						iAnt.setPosition(newPosition);// updates ant position
						nodes[iAnt.getPosition()].setImage(antWithFood);// updates new node image to ant with food
						if (newPosition == spawnLocation) {// if home has been found
							antBorn = spawnLocation;// gets node for new ant
							iAnt.stateMachine.getCurrentState().getTransition(foundHome).setTrigger(true);// sets found home trigger to true
							nodes[iAnt.getPosition()].setImage(antInHill);// sets new node image to ant in hill
						}// if (home)
						break;// breaks from switch
					case ("SearchForWater"):
						newPosition = getAStar(iAnt, (int)(Math.random() * waterLocations.size()));// gets new position from wander
						nodes[iAnt.getPosition()].setImage(new ImageIcon(nodes[iAnt.getPosition()].getOccupant() + ".png").getImage());// updates old node to previous occupant image
						iAnt.setPosition(newPosition);// updates ant position
						nodes[iAnt.getPosition()].setImage(ant);// updates new node image to ant
						if (nodes[newPosition].getOccupant().equals("water")) {// checks if water has been found
							iAnt.stateMachine.getCurrentState().getTransition(foundWater).setTrigger(true);// sets found water trigger to true
						}// if (water)
						break;// breaks from switch
					case ("DrinkWater"):
						nodes[iAnt.getPosition()].setImage(antInWater);// updates node image to ant in water
						nodes[iAnt.getPosition()].setOccupant("terrain");// updates node occupant to terrain
						break;// breaks from switch
					default:
						break;// breaks from switch
				}// switch(action)

				if (nodes[newPosition].getOccupant().equals("poison")) {// checks if ant steps in poison
					nodes[iAnt.getPosition()].setImage(terrain);// updates the node image to terrain
					nodes[iAnt.getPosition()].setOccupant("terrain");// updates the node occupant to terrain
					alive = false;// flags that ant was killed
					deadAnt = i;// gets number of ant killed
				}// if (poison)

				if (alive) {// checks if ant is alive
					if (antList.get(i).getPosition() == spawnLocation && !action.equals("EnterAntHill") && !action.equals("LeaveAntHill") && !action.equals("SearchForHome"))// checks if ant hill image needs to be added
						nodes[antList.get(i).getPosition()].setImage(new ImageIcon("antHill.png").getImage());//sets image to ant hill
				}// if (alive)

				if (deadAnt != 999999) {// checks if current ant was killed
					deadAnts.add(iAnt);// adds number of ant killed
				}// if (ant died)

				if (antBorn != 999999) {// check if an ant was born
					bornAnts.add(antBorn);// adds number of ant born
				}// if (ant born)
			}// for (ants)

			for (Entity ant : deadAnts)// loops through all ants killed in current game loop
				antList.remove(ant);// removes ants killed during game loop

			for (Integer node : bornAnts)// loops through all ant born in current game loop
				spawnAnt();
		}
	}

	// getAStar method - calls the A* algorithm to help the Ant find its way home
	public int getAStar(Entity ant, int goal) {
		ArrayList<Connection> path = AStar.pathFindAStar(graph, ant.getPosition(), goal, new Heuristic(goal));// creates a new path from the A* result
		ArrayList<Integer> nodesTo = new ArrayList<>();// list to hold the toNodes on the path
		for (Connection connection : path)// loops through the path
			nodesTo.add(connection.getToNode());// adds the toNodes to the list
		return nodesTo.remove(0);// returns the first node
	}// getAStar(Ant) method

	// Default constructor
	public Board(){
		// Game board settings
		addMouseListener(this);
		setFocusable(true);
		setPreferredSize(new Dimension(SCREEN_SIZE, SCREEN_SIZE + INFO_PANEL_SIZE));
		setBackground(Color.WHITE);
		setDoubleBuffered(true);
		setLayout(new BorderLayout());

		// UI button settings
		execute.addActionListener(this);
		clearBoard.addActionListener(this);
		buttonPane.setLayout(new GridLayout(1, 3));
		buttonPane.add(clearBoard);
		buttonPane.add(execute);
		add(buttonPane, BorderLayout.SOUTH); // adds the statusPane holding the path details to the display

		for (int i = 0; i < nodes.length; i++)// loops through the game nodes
			nodes[i] = new Node();// fills each element with a blank node to start

		food = new ImageIcon("food.png").getImage();// creates the food image
		water = new ImageIcon("water.png").getImage();// creates the water image
		terrain = new ImageIcon("terrain.png").getImage();// creates the terrain image
		poison = new ImageIcon("poison.png").getImage();// creates the poison image
		ant = new ImageIcon("ant.png").getImage();// creates the ant image
		antHill = new ImageIcon("antHill.png").getImage();// creates the antHill image
		antWithFood = new ImageIcon("antWithFood.png").getImage();// creates the antWithFood image
		antInWater = new ImageIcon("antInWater.png").getImage();// creates the antInWater image
		antInHill = new ImageIcon("antInHill.png").getImage();// creates the antInHill image

		clearBoard.doClick();// calls the method to populate the board with random tiles to start

		timer = new Timer(DELAY, this);// creates timer with between-event DELAY
		timer.start();
	}

	// This function is the main game loop
	@Override
	public void actionPerformed(ActionEvent e) {
		// checks if the game should execute
		if (e.getSource() == execute){
			//playSound(startSound);
			randomize();
			execute();
			execute.setEnabled(false); // disable the execute button
		}

		// checks if the board should be cleared
		else if (e.getSource() == clearBoard) {
			playSound(clearSound);
			clear();
			execute.setEnabled(true); // enable the execute button
		}

		repaint();// repaints with current information
	}

	// This function generates a randomly filled game board
	public void randomize() {
		Random random = new Random();// random generator used to get random int
		int r; // for manipulating object chance
		int type; // type of terrain

		for (int i = 0; i < CELLS_PER_ROW; i++) {
			for (int j = 0; j < CELLS_PER_ROW; j++) {
				int index = i * CELLS_PER_ROW + j;
				r = random.nextInt(32);
				// occurrence manipulating
				if (r < 4){
					type = 0;
					foodLocations.add(index);
				}
				else if (r < 8){
					type = 1;
					waterLocations.add(index);
				}
				else if (r < 31){
					type = 2;
				}
				else{
					type = 3;
					poisonLocations.add(index);
				}
				if (!nodes[index].isOccupied()) {
					nodes[index] = new Node(new Point(i * SCREEN_SIZE / CELLS_PER_ROW, j * SCREEN_SIZE / CELLS_PER_ROW), new ImageIcon(ICONS[type]).getImage(), true, OCCUPANTS[type]);// creates random tile
				}
			}
		}
	}

	// This function runs the A* pathfinding algorithm and return the result if found
	public void execute() {
		for (int i = 0; i < CELLS_PER_ROW; i++) {
			for (int j = 0; j < CELLS_PER_ROW; j++) {
				addConnections(i, j);
			}
		}
		manager.spawnAnt();
		executing = true;
	}

	// This method replaces all the cells in the game space with blank cells
	public void clear(){
		for (int i = 0; i < TOTAL_CELLS; i++){
			nodes[i] = new Node(new Point(i / CELLS_PER_ROW * CELL_SIZE, i % CELLS_PER_ROW * CELL_SIZE), new ImageIcon("empty.png").getImage(), false, "empty");
		}
		manager.reset();
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
				if(!nodes[index].isOccupied() && !manager.initialized()){
					nodes[index] = new Node(posCell, new ImageIcon("antHill.png").getImage(), true, "antHill");
					spawnLocation = index;
				}
				else if(nodes[index].isOccupied()){
					manager.spawnAnt();
				}
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
	// Also manages logic functions for displaying the game cells and calling ... to simulate the ant colony
	private void doDrawing(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		int index;

		for (int i = 0; i < CELLS_PER_ROW; i++) {
			for (int j = 0; j < CELLS_PER_ROW; j++) {
				index = i * CELLS_PER_ROW + j;
				g2.drawImage(nodes[index].getImage(), nodes[index].getPos().x, nodes[index].getPos().y, this);// draws each tile with its current information
			}
		}

		if(executing)
			if(manager.currentSpeed++ % manager.simulationDelay == 0)
				manager.simulate();
	}
}