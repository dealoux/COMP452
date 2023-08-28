package CoreSystem;

import Steering.*;
import java.awt.*;
import java.util.ArrayList;

public class NPC extends Kinematic {
    private static float maxSpeed;
    private static float npcDistanceThreshold;
    private static float maxAcceleration;
    private static float maxAngularAcceleration;
    private static float maxRotation;
    private static float arriveTargetRadius;
    private static float arriveSlowRadius;
    private static float timeToTarget;

    private static Color c = Color.green;
    private static boolean updateThisFrame;

    public NPC(float[] position, float orientation, float[] velocity, float rotation, int size){
        super(position, orientation, velocity, rotation);
        maxSpeed = 4f;

        npcDistanceThreshold = 80f;
        maxAcceleration = 2f;
        maxAngularAcceleration = .2f;
        maxRotation = 2f;
        arriveTargetRadius = 100f;
        arriveSlowRadius = arriveTargetRadius * 1.5f;
        timeToTarget = .1f;

        updateThisFrame = false;
    }

    public void update(Player player, ArrayList<NPC> separateTargets){
        float distance = Vector2DMath.distance(player.position, this.position);

        Steering result = new Steering();

        if(distance > arriveTargetRadius){
            //result.linear = Vector2DMath.add(result.linear, seek(player.position, this, maxAcceleration).linear);
            result.linear = Vector2DMath.add(result.linear, arrive(player.position, this, maxSpeed, maxAcceleration, arriveTargetRadius, arriveSlowRadius, timeToTarget).linear);
        }
        else{
            result.linear = Vector2DMath.add(result.linear, flee(player.position, this, maxAcceleration).linear);
        }
        updateThisFrame = true;


        Steering sep = separation(separateTargets, this, maxAcceleration, npcDistanceThreshold);
        if(Vector2DMath.initialized(sep.linear)){
            result.linear = Vector2DMath.add(result.linear, sep.linear);
            updateThisFrame = true;
        }

        if(updateThisFrame){
//            this.linear = Vector2DMath.limit(result.linear, 2f);
//            this.angular = Math.max(result.angular, 0f);
            this.linear = result.linear;
            this.angular = result.angular;
            update(maxSpeed, 1);
        }
    }

    public void draw(Graphics2D g2D, int size){
        super.draw(g2D);
        g2D.setColor(c);
        //g2D.fillOval((int) position[Vector2DMath.X], (int) position[Vector2DMath.Y], diameter, diameter);
        g2D.drawPolygon(new int[] {-24, 0, 24}, new int[] {-36, 36, -36}, 3);
    }
}