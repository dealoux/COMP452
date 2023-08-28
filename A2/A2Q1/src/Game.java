/*
 * Main class for this program
 */

import javax.swing.*;
import java.awt.*;

public class Game extends JFrame {
	// Game() constructor, initialize a game instance
	public Game() {
		setContentPane(new Board());// sets the content pane to that of an instance of the Board class
		pack();
		setTitle("A* Pathfinding");
		setResizable(false);
		setLocationRelativeTo(null);
		JFrame.setDefaultLookAndFeelDecorated(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}// Game() constructor
	
	// main method - used to run the A* Pathfinding game 
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			Game game = new Game();// creates new Game instance
			game.setVisible(true);// sets the game instance to be visible
		});
	}
}
