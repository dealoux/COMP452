package Steering;

public class Vector2DMath {
    public static int X = 0;
    public static int Y = 1;

    // calculate and return the length of the given vector v
    public static float length(float[] v){
        return Math.round(Math.sqrt( v[X]*v[X] + v[Y]*v[Y] ));
    }

    // calculate and return the normalized vector of the given vector v
    public static float[] normalize(float[] v){
        float mag = Math.abs(v[X]) + Math.abs(v[Y]);// gets magnitude
        return new float[] { v[X] / mag, v[Y] / mag };
    }

    public static boolean initialized(float[] v){
        return (v[X] != 0 && v[Y] != 0);
    }

    //
    public static float[] limit(float[] v, float limit){
        return multiply(normalize(v), limit);
    }

    public static float[] copy(float[] v){
        return new float[] {v[X], v[Y]};
    }

    // calculate and return the distance between the given vectors v1 and v2
    public static float distance(float[] v1, float[] v2) {
        return Math.round(Math.sqrt( (v1[X]-v2[X])*(v1[X]-v2[X]) + (v1[Y]-v2[Y])*(v1[Y]-v2[Y]) ));
    }

    // subtraction between the given vectors v1 and v2
    public static float[] subtract(float[] v1, float[] v2){
        return new float[] { v1[X]-v2[X], v1[Y]-v2[Y] };
    }

    // multiplication between the given vector v and a constant c
    public static float[] multiply(float[] v, float c){
        return new float[] { v[X]*c, v[Y]*c };
    }
    // multiplication between the given vectors v1 and v2
    public static float[] multiply(float[] v1, float[] v2){
        return new float[] { v1[X]*v2[X], v1[Y]*v2[Y] };
    }

    // division between the given vector v and a constant c
    public static float[] divide(float[] v, float c){
        return new float[] { v[X]/c, v[Y]/c };
    }
    // division between the given vectors v1 and v2
    public static float[] divide(float[] v1, float[] v2){
        return new float[] { v1[X]/v2[X], v1[Y]/v2[Y] };
    }

    // addition between the given vector v and a constant c
    public static float[] add(float[] v, float c){
        return new float[] { v[X]+c, v[Y]+c };
    }
    // addition between the given vectors v1 and v2
    public static float[] add(float[] v1, float[] v2){
        return new float[] { v1[X]+v2[X], v1[Y]+v2[Y] };
    }
}
