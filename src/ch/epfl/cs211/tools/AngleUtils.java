package ch.epfl.cs211.tools;

/**
 * Created by Leandro on 08.03.2016.
 */
public final class AngleUtils {

    public static float maxClampAngle(float input, float maxValue){
        if(input > maxValue) return maxValue;
        else return input;
    }

    public static float minClampAngle(float input, float minValue){
        if(input < minValue) return minValue;
        else return input;
    }

    public static float maxClampAngle(float input,float minValue, float maxValue){
        if(input > maxValue) return maxValue;
        else if(input < minValue) return minValue;
        else return input;
    }
}
