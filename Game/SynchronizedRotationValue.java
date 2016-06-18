
import processing.core.PVector;

/**
 * Created by Leandro on 07.06.2016.
 */


/**
 * A synchronized class so the thread performing the hough transform can safely write data while
 * the main thread can access it to perform a bit of smoothing when going from one rotation to the other
 */
public class SynchronizedRotationValue {

    private PVector rot;

    public SynchronizedRotationValue(){
        rot = new PVector(0,0,0);
    }

    public synchronized void setRot(PVector r){
        rot.x = r.x;
        rot.y = r.y;
        rot.z = r.z;
    }

    public synchronized PVector getRot(){
        return rot.copy();
    }
}