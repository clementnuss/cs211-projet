/**
 *  Visual Computing project (CS211) - 2016
 *  Authors : Clément Nussbaumer, Leandro Kieliger, Louis Rossier
 *
 */

import java.text.DecimalFormat;

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

    /**
     *
     * @param d decimal number to be rounded
     * @return  The rounded number (with e-03 precision)
     */
    public static float roundThreeDecimals(float d) {
        return Float.valueOf(threeDForm.format(d));
    }


}
