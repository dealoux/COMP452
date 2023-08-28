package Steering;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Kinematic extends Steering {
    public float[] position;
    public float orientation;
    public float[] velocity;
    public float rotation;

    public Kinematic(float[] position, float orientation, float[] velocity, float rotation){
        super();
        this.position = position;
        this.orientation = orientation;
        this.velocity = velocity;
        this.rotation = rotation;
    }

    public void update(float maxSpeed, float time){
        // update the position and orientation
        position = Vector2DMath.add(position, Vector2DMath.multiply(velocity, time));
        //newOrientation();
        orientation += rotation * time;

        // and the velocity and rotation
        velocity = Vector2DMath.add(velocity, Vector2DMath.multiply(linear, time));
        rotation += angular * time;

        // check for speeding and clip
        if(Vector2DMath.length(velocity) > maxSpeed){
            velocity = Vector2DMath.normalize(velocity);
            velocity = Vector2DMath.multiply(velocity, maxSpeed);
        }
    }
// float maxRotation, float maxAngularAcceleration, float targetRadius, float slowRadius, float timeToTarget
    public void newOrientation() {
        // Determines if the character is moving
        if (Vector2DMath.length(velocity) > 0){
            //orientation = (float) Math.atan2(-velocity[Vector2DMath.X], velocity[Vector2DMath.Y]);// calculate the orientation from the velocity

//            Steering result = align((float) Math.atan2(-velocity[Vector2DMath.X], velocity[Vector2DMath.Y]),this, maxRotation, maxAngularAcceleration, targetRadius, slowRadius, timeToTarget);
//            if(result != null){
//                this.angular = result.angular;
//            }
        }
    }// setOrientation() method

    public void draw(Graphics2D g2D){
        AffineTransform at = new AffineTransform();
        at.translate((int) position[Vector2DMath.X], (int) position[Vector2DMath.Y]);
        at.rotate(orientation);
        g2D.setTransform(at);

    }
}
