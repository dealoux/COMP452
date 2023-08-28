package CoreSystem;

import Steering.*;
import java.awt.*;

public class Player extends Kinematic {
    InputHandler input;
    private final float speed = 4f;
    private int size;
    private Color c = Color.white;

    public Player(float[] pos, int size){
        super(pos, 0f, new float[]{0f, 0f}, 0f);
        this.size = size;
        input = new InputHandler();
    }

    public void update(){
        if(input.up){
            velocity[Vector2DMath.Y] = -speed;
        }
        else if(input.down){
            velocity[Vector2DMath.Y] = speed;
        }
        else{
            velocity[Vector2DMath.Y] = 0f;
        }

        if(input.left){
            velocity[Vector2DMath.X] = -speed;
        }
        else if(input.right){
            velocity[Vector2DMath.X] = speed;
        }
        else{
            velocity[Vector2DMath.X] = 0f;
        }

        update(speed, 1f);
    }

    public void draw(Graphics2D g2D){
        super.draw(g2D);
        g2D.setColor(c);
        //g2D.fillOval((int) position[Vector2DMath.X], (int) position[Vector2DMath.Y], diameter, diameter);
        g2D.drawPolygon(new int[] {-24, 0, 24}, new int[] {-36, 36, -36}, 3);
    }
}
