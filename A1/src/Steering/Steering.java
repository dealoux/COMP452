package Steering;

import java.lang.annotation.Target;
import java.util.ArrayList;
import CoreSystem.*;

public class Steering {
    public float[] linear;
    public float angular;

    private static float decayCoefficient;


    public Steering(){
        this.linear = new float[2];
        this.angular = 0f;

        decayCoefficient = 1f;
    }

    private Steering base(float[] direction, float maxAcceleration){
        Steering result = new Steering();
        result.linear = direction;

        result.linear = Vector2DMath.limit(result.linear, maxAcceleration);

        result.angular = 0;
        return result;
    }

    public Steering seek(float[] target, Kinematic entity, float maxAcceleration){
        return base(Vector2DMath.subtract(target, entity.position), maxAcceleration);
    }

    public Steering flee(float[] target, Kinematic entity, float maxAcceleration){
        return base(Vector2DMath.subtract(entity.position, target), maxAcceleration);
    }

    public Steering arrive(float[] target, Kinematic entity, float maxSpeed, float maxAcceleration, float targetRadius, float slowRadius, float timeToTarget){
        Steering result = new Steering();
        float[] direction = Vector2DMath.subtract(target, entity.position);
        float distance = Vector2DMath.length(direction);

        if(distance < targetRadius){
            return null;
        }

        float targetSpeed = (distance > slowRadius) ? maxSpeed : maxSpeed * distance/slowRadius;

        float[] targetVelocity = direction;
        targetVelocity = Vector2DMath.normalize(targetVelocity);
        targetVelocity = Vector2DMath.multiply(targetVelocity, targetSpeed);

        result.linear = Vector2DMath.subtract(targetVelocity, entity.velocity);
        result.linear = Vector2DMath.divide(result.linear, timeToTarget);

        if(Vector2DMath.length(result.linear) > maxAcceleration){
            result.linear = Vector2DMath.limit(result.linear, maxAcceleration);
        }

        result.angular = 0;
        return result;
    }

    public Steering align(float target, Kinematic entity, float maxRotation, float maxAngularAcceleration, float targetRadius, float slowRadius, float timeToTarget){
        Steering result = new Steering();
        float rotation = (float)Math.toRadians(target - entity.rotation);
        float rotationSize = Math.abs(rotation);

        if(rotationSize < targetRadius)
            return null;

        float targetRotation = (rotationSize > slowRadius) ? maxRotation : maxRotation * rotationSize/slowRadius;

        targetRotation *= rotation/rotationSize;

        result.angular = targetRotation - entity.rotation;
        result.angular /= timeToTarget;

        float angularAcceleration = Math.abs(result.angular);
        if(angularAcceleration > maxAngularAcceleration){
            result.angular /= angularAcceleration;
            result.angular *= maxAngularAcceleration;
        }

        result.linear = new float[]{0, 0};
        return result;
    }

    public Steering separation(ArrayList<NPC> targets, Kinematic entity, float maxAcceleration, float distanceThreshold){
        Steering result = new Steering();

        for(NPC target : targets){
            // check if the target is close
            float[] direction = Vector2DMath.subtract(target.position, entity.position);
            float distance = Vector2DMath.length(direction);

            if(distance != 0f && distance < distanceThreshold){
                // calculate the strength of repulsion using the inverse square law
                float strength = Math.min(decayCoefficient / (distance * distance), maxAcceleration);

                // add the acceleration
                direction = Vector2DMath.normalize(direction);
                result.linear = Vector2DMath.add(result.linear, Vector2DMath.multiply(direction, strength));
            }
        }

        return result;
    }
}

