package CoreSystem;

import Steering.Vector2DMath;

import javax.swing.JPanel;
import java.awt.*;
import java.util.ArrayList;

class GamePanel extends JPanel implements Runnable{
    final int TILE_SIZE = 16; // 16x16 tile
    final int TILE_SCALE = 3;
    final int TILE_DISPLAY_SIZE = TILE_SIZE * TILE_SCALE; // 48x48

    // Screen settings
    final int W = 0; // screen's width
    final int H = 1; // screen's height
    final int[] ASPECT_RATIO = new int[]{ 16, 12 };
    final int[] SCREEN_SIZE = new int[]{ TILE_DISPLAY_SIZE * ASPECT_RATIO[W], TILE_DISPLAY_SIZE * ASPECT_RATIO[H] };

    final int FPS = 60;
    Thread gameThread;

    Player player = new Player(new float[] {SCREEN_SIZE[W]/2, SCREEN_SIZE[H]/2}, TILE_DISPLAY_SIZE);
    NPCManager npcManager = new NPCManager();

    class NPCManager{
        private static ArrayList<NPC> npcList;

        public NPCManager(){
            npcList = new ArrayList<>();
            float[] currPos = new float[2];
            float npcSpawnDistance = 75f;

            for(currPos[W] = TILE_DISPLAY_SIZE; currPos[W] < SCREEN_SIZE[W]; currPos[W] += npcSpawnDistance ){
                for(currPos[H] = TILE_DISPLAY_SIZE; currPos[H] < SCREEN_SIZE[H]; currPos[H] += npcSpawnDistance ){
                    npcList.add(new NPC(Vector2DMath.copy(currPos), 0f, new float[] {0, 0},0f, TILE_DISPLAY_SIZE));
                }
            }

//            for(int i=0; i<2; i++){
//                npcList.add(new NPC(Vector2DMath.copy(currPos), 0f, new float[] {0, 0},0f, TILE_DISPLAY_SIZE));
//                currPos[W] += npcSpawnDistance;
//            }
        }

        public void update(){
            for(NPC npc : npcList)
                npc.update(player, npcList);
        }

        public void draw(Graphics2D g2D){
            for(NPC npc : npcList)
                npc.draw(g2D, TILE_DISPLAY_SIZE);
        }
    }

    GamePanel(){
        this.setPreferredSize(new Dimension(SCREEN_SIZE[W], SCREEN_SIZE[H]));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(player.input);
        this.setFocusable(true);
    }

    void startGameThread(){
        gameThread = new Thread(this);
        gameThread.start();
    }

    void update(){
        player.update();
        npcManager.update();
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

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D)g;

        player.draw(g2D);
        npcManager.draw(g2D);
        g2D.dispose();
    }
}
