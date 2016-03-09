package ch.epfl.cs211.tools;

import java.text.DecimalFormat;

/**
 * Created by Leandro on 08.03.2016.
 */
public final class ValueUtils {

    private final static DecimalFormat threeDForm = new DecimalFormat("#.###");

    /**
     * Limits the input to a given maximum value
     * @param input
     * @param maxValue
     * @return
     */
    public static float maxClamp(float input, float maxValue){
        if(input > maxValue) return maxValue;
        else return input;
    }

    /**
     * Limits the input to a given minimum value
     * @param input
     * @param minValue
     * @return
     */
    public static float minClamp(float input, float minValue){
        if(input < minValue) return minValue;
        else return input;
    }

    /**
     * Limits the input to a given interval
     * @param input
     * @param minValue
     * @param maxValue
     * @return
     */
    public static float clamp(float input, float minValue, float maxValue){
        if(input > maxValue) return maxValue;
        else if(input < minValue) return minValue;
        else return input;
    }

    public static float roundThreeDecimals(float d) {
        return Float.valueOf(threeDForm.format(d));
    }


}
