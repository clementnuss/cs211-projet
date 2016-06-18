package ch.epfl.cs211;

import processing.core.PVector;

/**
 * A synchronized class so the thread performing the hough transform can safely write data while
 * the main thread can access it to perform a bit of smoothing when going from one rotation to the other
 */
public class SynchronizedRotationValue {

    private final PVector rot;

    public SynchronizedRotationValue() {
        rot = new PVector(0, 0, 0);
    }

    public synchronized PVector getRot() {
        return rot.copy();
    }

    public synchronized void setRot(PVector r) {
        rot.x = r.x;
        rot.y = r.y;
        rot.z = r.z;
    }
}
