package ch.epfl.cs211.VideoCapture;

public class HoughComparator implements java.util.Comparator<Integer> {
    final int[] accumulator;

    public HoughComparator(int[] accumulator) {
        this.accumulator = accumulator;
    }

    @Override
    public int compare(Integer l1, Integer l2) {
        if (accumulator[l1] > accumulator[l2] || (accumulator[l1] == accumulator[l2] && l1 < l2)) return -1;
        return 1;
    }
}
