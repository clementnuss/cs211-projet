package ch.epfl.cs211.VideoCapture;

import processing.core.PVector;

/**
 * As the main thread executes faster than the image processing thread,
 * this class allows reads and write to be performed concurrently. This way
 * some smoothing can be done in the main thread between two consecutive values
 * returned by the board detection.
 */
public class SynchronizedRotationValue {

    private final PVector rot;

    public SynchronizedRotationValue(){
        rot = new PVector(0,0,0);
    }

    public synchronized PVector getRot() {
        return rot.copy();
    }

    public synchronized void setRot(PVector r){
        rot.x = r.x;
        rot.y = r.y;
        rot.z = r.z;
    }
}
