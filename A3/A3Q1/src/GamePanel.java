import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

class GamePanel extends JPanel implements Runnable, ActionListener, MouseListener {
    private final int CELLS_HORIZONTAL = 7; // number of cells on a single row
    private final int CELLS_VERTICAL = 6; // number of cells on a single column
    private Node[][] nodes = new Node[CELLS_HORIZONTAL][CELLS_VERTICAL]; // used to represent each cell node in the Game space
    private final int CELL_SIZE = 100; // size of a single cell
    private final int CHIP_DIAMETER = (int) (CELL_SIZE * .75f); // diameter of a single chip
    // Size of the screen
    private final int SCREEN_HORIZONTAL_SIZE = CELL_SIZE * CELLS_HORIZONTAL;
    private final int SCREEN_VERTICAL_SIZE = CELL_SIZE * CELLS_VERTICAL;
    private final int INFO_PANEL_SIZE = 20;

    private JPanel buttonPane = new JPanel(new GridLayout(1, 3)); // adds a JPanel to hold the buttons
    private JButton clearBoard = new JButton("Clear Board");// the button to remove the tiles on the board

    int turn = 1;

    //private File buttonSound = new File("buttonPress.wav");// sound for button presses
    //private File clearSound = new File("clearSound.wav"); // sound for when clearing the board

    final int FPS = 60;
    Thread gameThread;


    GamePanel(){
        this.setPreferredSize(new Dimension(SCREEN_VERTICAL_SIZE, SCREEN_HORIZONTAL_SIZE));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
    }

    void startGameThread(){
        gameThread = new Thread(this);
        gameThread.start();
    }

    void update(){

    }

    @Override
    public void run(){
        double updateInterval = 1000000000/FPS;
        double nextUpdateTime = System.nanoTime() + updateInterval;

        while (gameThread != null){
            update();
            repaint();

            try {
                double remainingTime = nextUpdateTime - System.nanoTime();
                remainingTime = (remainingTime < 0) ? 0 : remainingTime/1000000;

                Thread.sleep((long) remainingTime);

                nextUpdateTime += updateInterval;
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    // This function is the main game loop
    @Override
    public void actionPerformed(ActionEvent e) {
        // checks if the board should be cleared
        if (e.getSource() == clearBoard) {
            //playSound(clearSound);
            clear();
        }

        repaint();// repaints with current information
    }

    // This method replaces all the cells in the game space with blank cells
    public void clear(){
        for(Node[] rows : nodes)
            for (Node cell : rows)
                cell.setType(CellType.EMPTY);
    }

    // This function handle mouse click events
    public void mousePressed(MouseEvent e) {
        Point posMouse = e.getPoint(); // coordinate of the mouse
        // check if the click is within the bounds of the game space
        if (posMouse.x < SCREEN_HORIZONTAL_SIZE && posMouse.y < SCREEN_VERTICAL_SIZE) {
            // Get the correct starting position of the cell
            int x = (int)Math.floor(posMouse.x / CELL_SIZE);
            int y = (int)Math.floor(posMouse.y / CELL_SIZE);
            Point posCell = new Point(posMouse.x - posMouse.x % CELL_SIZE, posMouse.y - posMouse.y % CELL_SIZE);

            if(nodes[x][y].getType() == CellType.EMPTY){
                nodes[x][y].setType(CellType.WHITE);
                System.out.println("here");
            }
        }
    }

    // MouseListener events
    public void mouseReleased(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
    public void mouseClicked(MouseEvent e) { }

    private void drawGrid(Graphics2D g2, Point pos){
        g2.drawRect(pos.x, pos.y, CELL_SIZE, CELL_SIZE);
    }

    private void drawChip(Graphics2D g2, Point pos, Color c){
        g2.setColor(c);
        g2.fillOval(pos.x, pos.y, CHIP_DIAMETER, CHIP_DIAMETER);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;

        for (int c = 0; c < nodes.length; c++) {
            for (int r = 0; r < nodes[c].length; r++) {
                Node cell = nodes[c][r];
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
        g2.dispose();
    }
}
