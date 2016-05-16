package ProcessingFiles.VideoCapture;

import processing.core.PImage;

import java.util.concurrent.RecursiveAction;
import static ProcessingFiles.VideoCapture.VideoStream.INST;

/**
 * Created by Leandro on 16.05.2016.
 */
public class ParHSVFiltering extends RecursiveAction {

    private final static int PAR_THRESHOLD = 50000;

    private PImage img;
    private int start;
    private int length;
    private HSVBounds bounds;

    /**
     * Class representing a portion of HSVFiltering work to be done in an image.
     * Do not forget to call loadPixels before executing the task in parallel and then call
     * updatePixels when finished
     *
     * @param img
     * @param start The index in the image array where to start work (INCLUDED)
     * @param length   The index in the image array where to length work (EXCLUDED)
     */
    public ParHSVFiltering(PImage img, int start, int length, HSVBounds bounds){
        this.img = img;
        this.start = start;
        this.length = length;
        this.bounds = bounds;
    }


    @Override
    protected void compute() {
        if(length < PAR_THRESHOLD) {
            computeSequentially();
            return;
        }

        //System.out.println("Split task of length "+length);
        int split = length / 2;

        //System.out.println("From "+start+" with length "+split+" and from "+(start+split)+" of length "+(length-split));

        invokeAll(  new ParHSVFiltering(img, start, split, bounds),
                    new ParHSVFiltering(img, start + split, length - split, bounds));
    }

    private void computeSequentially(){
        float minH = bounds.getH_min();
        float maxH = bounds.getH_max();
        float minS = bounds.getS_min();
        float maxS = bounds.getS_max();
        float minV = bounds.getV_min();
        float maxV = bounds.getV_max();

        for (int i = start; i < start + length; i++) {
            int originalColor = img.pixels[i];
            float h = INST.hue(originalColor);

            if(minH < h && h < maxH){
                float s = INST.saturation(originalColor);
                if(minS < s && s < maxS){
                    float v = INST.brightness(originalColor);
                    if(minV < v && v < maxV){
                        img.pixels[i] = 0xFFFFFFFF;
                        continue;
                    }
                }
            }

            img.pixels[i] = 0x0;
        }
    }
}