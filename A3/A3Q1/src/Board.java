/*
 * This class sets up and display the game board visually as well as handling most of the game logic
 */

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;


public class Board extends JPanel implements ActionListener, MouseListener {
	private final int CELLS_HORIZONTAL = 7; // number of cells on a single row
	private final int CELLS_VERTICAL = 6; // number of cells on a single column
	private Node[][] grids = new Node[CELLS_HORIZONTAL][CELLS_VERTICAL]; // used to represent each cell node in the Game space
	private final int CELL_SIZE = 100; // size of a single cell
	private final int CHIP_DIAMETER = (int) (CELL_SIZE * .75f); // diameter of a single chip
	private final int CHIP_OFFSET = CHIP_DIAMETER/6;
	// Size of the screen
	private final int SCREEN_HORIZONTAL_SIZE = CELL_SIZE * CELLS_HORIZONTAL;
	private final int SCREEN_VERTICAL_SIZE = CELL_SIZE * CELLS_VERTICAL;
	private final int INFO_PANEL_SIZE = 20;

	private JPanel buttonPane = new JPanel(new GridLayout(1, 3)); // adds a JPanel to hold the buttons
	private JButton clearBoard = new JButton("Clear Board");// the button to remove the tiles on the board

	private int turn = 1;
	private boolean gameover = false;
	private final int DEPTH = 4;

	//private File buttonSound = new File("buttonPress.wav");// sound for button presses
	//private File clearSound = new File("clearSound.wav"); // sound for when clearing the board

	class MinimaxResult{
		private int col;
		private int score;

		public MinimaxResult(int col, int score){
			this.col = col;
			this.score = score;
		}

		public int getCol(){ return col; }

		public int getScore(){ return score; }
	}


	// Default constructor
	public Board(){
		// Game board settings
		addMouseListener(this);
		setFocusable(true);
		setPreferredSize(new Dimension(SCREEN_HORIZONTAL_SIZE, SCREEN_VERTICAL_SIZE + INFO_PANEL_SIZE));
		setBackground(Color.WHITE);
		setDoubleBuffered(true);
		setLayout(new BorderLayout());

		// UI button settings
		clearBoard.addActionListener(this);
		buttonPane.setLayout(new GridLayout(1, 3));
		buttonPane.add(clearBoard);
		add(buttonPane, BorderLayout.SOUTH); // adds the statusPane holding the path details to the display

		for (int c = 0; c < grids.length; c++)// loops through the game nodes
			for(int r = 0; r < grids[c].length; r++)
				grids[c][r] = new Node(new Point(c*CELL_SIZE, r*CELL_SIZE));// fills each element with a blank node to start

		clearBoard.doClick();// calls the method to populate the board with random tiles to start
		turn = 1;
		gameover = false;
	}

	// This function is the main game loop
	@Override
	public void actionPerformed(ActionEvent e) {
		// checks if the board should be cleared
		if (e.getSource() == clearBoard) {
			//playSound(clearSound);
			clear();
			repaint();// repaints with current information
		}
	}

	// This method replaces all the cells in the game space with blank cells
	public void clear(){
		for(Node[] rows : grids)
			for (Node cell : rows)
				cell.setType(CellType.EMPTY);

		gameover = false;
	}

	private boolean is_valid_location(int col, Node[][] board){
		return board[col][0].getType() == CellType.EMPTY;
	}

	private int get_next_open_row(int col, Node[][] board){
		for(int row = CELLS_VERTICAL-1; row >= 0; row--){
			if(board[col][row].getType() == CellType.EMPTY)
				return row;
		}
		return -1;
	}


//	private Node[][] copyBoard(Node[][] board){
//		Node[][] copy = new Node[CELLS_HORIZONTAL][CELLS_VERTICAL];
//
//		for (int c = 0; c < board.length; c++)// loops through the game nodes
//			for(int r = 0; r < board[c].length; r++)
//				copy[c][r] = board[c][r];
//
//		return copy;
//	}

	private ArrayList<Integer> get_valid_locations(Node[][] board){
		ArrayList<Integer> result = new ArrayList<>();
		for(int c=0; c<CELLS_HORIZONTAL; c++)
			if(is_valid_location(c, board))
				result.add(c);

		return result;
	}

	private MinimaxResult minimax(Node[][] board, int depth, int alpha, int beta, boolean maximizingPlayer){
		ArrayList<Integer> valid_locations = get_valid_locations(board);
		int score = 0;

		if(depth == 0 || gameover){
			if(gameover){
				if(victoryCheck(board, CellType.BLACK))
					return new MinimaxResult(-1, 1000000000);
				else if(victoryCheck(board, CellType.WHITE))
					return new MinimaxResult(-1, -1000000000);
				else
					return new MinimaxResult(-1, 0);
			}
			else{
				return new MinimaxResult(-1, score_position(board, CellType.BLACK));
			}
		}

		int column = valid_locations.get(0);
		if(maximizingPlayer){
			int maxEval = Integer.MIN_VALUE;

			for(int col : valid_locations){
				int row = get_next_open_row(col, board);
				Node[][] copy = board.clone();
				makeMove(col, row, CellType.BLACK, copy);
				score = minimax(copy, depth-1, alpha, beta, false).getScore();

				if(score > maxEval){
					maxEval = score;
					column = col;
				}
				alpha = Math.max(alpha, maxEval);

				if(alpha >= beta)
					break;
			}
		}
		else{
			int minEval = Integer.MAX_VALUE;

			for(int col : valid_locations){
				int row = get_next_open_row(col, board);
				Node[][] copy = board.clone();
				makeMove(col, row, CellType.WHITE, copy);
				score = minimax(copy, depth-1, alpha, beta, true).getScore();

				if(score < minEval){
					minEval = score;
					column = col;
				}
				beta = Math.min(beta, minEval);

				if(alpha >= beta)
					break;
			}
		}

		return new MinimaxResult(column, score);
	}

	private void makeMove(int x, int y, CellType type, Node[][] board){
		if(x >= 0 && x < CELLS_HORIZONTAL && y>=0 && y<CELLS_VERTICAL)
			board[x][y].setType(type);
	}

	private void AI_Move(){
		MinimaxResult result = minimax(grids, DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, true);

		if(is_valid_location(result.getCol(), grids)){
			int row = get_next_open_row(result.getCol(), grids);
			makeMove(result.getCol(), row, CellType.BLACK, grids);
		}

		turn++;
	}

	// This function handle mouse click events
	public void mousePressed(MouseEvent e) {
		if(!gameover){
			Point posMouse = e.getPoint(); // coordinate of the mouse
			// check if the click is within the bounds of the game space
			if (posMouse.x < SCREEN_HORIZONTAL_SIZE && posMouse.y < SCREEN_VERTICAL_SIZE) {
				// Get the correct starting position of the cell
				int x = (int)Math.floor(posMouse.x / CELL_SIZE);
				int y = get_next_open_row(x, grids);

				makeMove(x, y, CellType.WHITE, grids);
				turn++;
				AI_Move();
				gameover = turn >= 42;
				repaint();// repaints with current information
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

	public boolean victoryCheck(Node[][] board, CellType type){
		int max_c = CELLS_HORIZONTAL - 3;
		int max_r = CELLS_VERTICAL - 3;

		// check vertically
		for(int c = 0; c < CELLS_HORIZONTAL; c++){
			for(int r = 0; r < max_r; r++){
				if(board[c][r].getType() == type
						&& board[c][r+1].getType() == type
						&& board[c][r+2].getType() == type
						&& board[c][r+3].getType() == type)
					return true;
			}
		}

		// check horizontally
		for(int c = 0; c < max_c; c++){
			for(int r = 0; r < CELLS_VERTICAL; r++){
				if(board[c][r].getType() == type
						&& board[c+1][r].getType() == type
						&& board[c+2][r].getType() == type
						&& board[c+3][r].getType() == type)
					return true;
			}
		}

		// check diagonally upward
		for(int c = 3; c < CELLS_HORIZONTAL; c++){
			for(int r = 0; r < max_r; r++){
				if(board[c][r].getType() == type
						&& board[c-1][r+1].getType() == type
						&& board[c-2][r+2].getType() == type
						&& board[c-3][r+3].getType() == type)
					return true;
			}
		}

		// check diagonally downward
		for(int c = 0; c < max_c; c++){
			for(int r = 0; r < max_r; r++){
				if(board[c][r].getType() == type
						&& board[c+1][r+1].getType() == type
						&& board[c+2][r+2].getType() == type
						&& board[c+3][r+3].getType() == type)
					return true;
			}
		}

		return false;
	}

	private int evaluate_window(Node[] window, CellType type){
		int score = 0;
		CellType opp_type = (type == CellType.BLACK) ? CellType.WHITE : CellType.BLACK;

		if(count(window, type) == 4)
			score += 100;
		else if(count(window, type) == 3 && count(window, CellType.EMPTY) == 1)
			score += 5;
		else if(count(window, type) == 2 && count(window, CellType.EMPTY) == 2)
			score += 2;

		if(count(window, opp_type) == 2 && count(window, CellType.EMPTY) == 1)
			score -= 4;

		return score;
	}

	private int count(Node[] window, CellType type){
		int result = 0;

		for(Node cell : window)
			result += cell.getType() == type ? 1 : 0;

		return result;
	}

	private Node[] getWindow(Node[] arr, int start){
		int index = 0;
		Node[] result = new Node[DEPTH];

		while (index < DEPTH){
			result[index++] = arr[start++];
		}

		return result;
	}

	private int score_position(Node[][] board, CellType type){
		int max_c = CELLS_HORIZONTAL - 3;
		int max_r = CELLS_VERTICAL - 3;
		int score = 0;

		for(int c=0; c<CELLS_HORIZONTAL; c++){
			for(int r=0; r<max_r; r++){
				Node[] window = getWindow(board[c], r);
				score += evaluate_window(window, type);
			}
		}

		return score;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		doDrawing(g);
		Toolkit.getDefaultToolkit().sync();// synchronizes the toolkit state
	}

	private void drawGrid(Graphics2D g2, Point pos){
		g2.setColor(Color.black);
		g2.drawRect(pos.x, pos.y, CELL_SIZE, CELL_SIZE);
	}

	private void drawChip(Graphics2D g2, Point pos, Color c){
		g2.setColor(c);
		g2.fillOval(pos.x + CHIP_OFFSET, pos.y + CHIP_OFFSET, CHIP_DIAMETER, CHIP_DIAMETER);
		g2.setColor(Color.black);
		g2.drawOval(pos.x + CHIP_OFFSET, pos.y + CHIP_OFFSET, CHIP_DIAMETER, CHIP_DIAMETER);
	}

	// This function loops through the graph and draw all cells in game space
	// Also manages logic functions for displaying the game cells and calling ... to simulate the ant colony
	private void doDrawing(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(2));

		for (int c = 0; c < grids.length; c++) {
			for (int r = 0; r < grids[c].length; r++) {
				Node cell = grids[c][r];
				switch (cell.getType()) {
					case BLACK:
						drawGrid(g2, cell.getPos());
						drawChip(g2, cell.getPos(), Color.black);
						break;

					case WHITE:
						drawGrid(g2, cell.getPos());
						drawChip(g2, cell.getPos(), Color.white);
						break;

					case EMPTY:
						drawGrid(g2, cell.getPos());
						break;
				}
			}
		}
	}
}