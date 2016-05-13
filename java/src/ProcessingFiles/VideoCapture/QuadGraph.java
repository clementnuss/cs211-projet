package ProcessingFiles.VideoCapture;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.*;


public class QuadGraph {

    static final float QUAD_MAX_AREA = 250000;
    static final float QUAD_MIN_AREA = 40000;

    private static List<int[]> cycles = new ArrayList<>();
    private static int[][] graph;


    public static void build(List<PVector> lines, int width, int height) {

        int n = lines.size();

        // The maximum possible number of edges is n * (n - 1)/2
        graph = new int[n * (n - 1) / 2][2];

        int idx = 0;

        for (int i = 0; i < lines.size(); i++) {
            for (int j = i + 1; j < lines.size(); j++) {
                if (intersect(lines.get(i), lines.get(j), width, height)) {
                    int[] edge = {i, j};
                    graph[idx++] = edge;
                }
            }
        }

        // System.out.println("Graph building complete");
    }

    /**
     * Returns true if polar lines 1 and 2 intersect
     * inside an area of size (width, height)
     */
    private static boolean intersect(PVector line1, PVector line2, int width, int height) {

        double sin_t1 = Math.sin(line1.y);
        double sin_t2 = Math.sin(line2.y);
        double cos_t1 = Math.cos(line1.y);
        double cos_t2 = Math.cos(line2.y);
        float r1 = line1.x;
        float r2 = line2.x;

        double denom = cos_t2 * sin_t1 - cos_t1 * sin_t2;

        int x = (int) ((r2 * sin_t1 - r1 * sin_t2) / denom);
        int y = (int) ((-r2 * cos_t1 + r1 * cos_t2) / denom);

        return 0 <= x && 0 <= y && width >= x && height >= y;
    }

    public static List<Quad> getQuads(List<PVector> lines){
        findCycles();
        List<Quad> quads = new ArrayList<>();
        for (int[] quad : cycles) {
            PVector l1 = lines.get(quad[0]);
            PVector l2 = lines.get(quad[1]);
            PVector l3 = lines.get(quad[2]);
            PVector l4 = lines.get(quad[3]);

            PVector c12 = intersection(l1, l2);
            PVector c23 = intersection(l2, l3);
            PVector c34 = intersection(l3, l4);
            PVector c41 = intersection(l4, l1);

            List<PVector> cList = sortCorners(Arrays.asList(c12,c23,c34,c41));
            quads.add(new Quad(cList.get(0), cList.get(1), cList.get(2), cList.get(3)));
        }

        return quads;
    }

    /**
     *
     * @param l a list from which to select the best quad
     * @return -1 if all quads were invalid, the index of the best quad otherwise
     */
    public static int indexOfBestQuad(List<Quad> l){

        int indexOfBestQuad = -1;
        int i = 0;
        float biggestArea = 0;

        for(Quad q : l){
            if(q.isConvex() && q.isNonFlat() && q.hasValidArea()){
                float a = q.getArea();
                if(a > biggestArea){
                    biggestArea = a;
                    indexOfBestQuad = i;
                }
            }
            i++;
        }
        return indexOfBestQuad;
    }

    private static PVector intersection(PVector l1, PVector l2) {
        float r1 = l1.x;
        float phi1 = l1.y;
        float r2 = l2.x;
        float phi2 = l2.y;
        float d = PApplet.cos(phi2) * PApplet.sin(phi1) - PApplet.cos(phi1) * PApplet.sin(phi2);
        PVector inter = new PVector(0,0);
        if (d != 0) {
            inter.x = ((r2 * PApplet.sin(phi1)) - (r1 * PApplet.sin(phi2))) / d;
            inter.y = ((r1 * PApplet.cos(phi2)) - (r2 * PApplet.cos(phi1))) / d;
        }
        return inter;
    }

    private static List<int[]> findCycles() {

        cycles.clear();
        for (int i = 0; i < graph.length; i++) {
            for (int j = 0; j < graph[i].length; j++) {
                findNewCycles(new int[]{graph[i][j]});
            }
        }
        return cycles;
    }

    private static void findNewCycles(int[] path) {
        int n = path[0];
        int x;
        int[] sub = new int[path.length + 1];

        for (int i = 0; i < graph.length; i++)
            for (int y = 0; y <= 1; y++)
                if (graph[i][y] == n)
                //  edge refers to our current node
                {
                    x = graph[i][(y + 1) % 2];
                    if (!visited(x, path))
                    //  neighbor node not on path yet
                    {
                        sub[0] = x;
                        System.arraycopy(path, 0, sub, 1, path.length);
                        //  explore extended path
                        findNewCycles(sub);
                    } else if ((path.length == 4) && (x == path[path.length - 1]))
                    //  cycle found
                    {
                        int[] p = normalize(path);
                        int[] inv = invert(p);
                        if (isNew(p) && isNew(inv)) {
                            cycles.add(p);
                        }
                    }
                }
    }

    //  check of both arrays have same lengths and contents
    private static boolean equals(int[] a, int[] b) {
        Boolean ret = (a[0] == b[0]) && (a.length == b.length);

        for (int i = 1; ret && (i < a.length); i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }

        return ret;
    }

    //  create a path array with reversed order
    private static int[] invert(int[] path) {
        int[] p = new int[path.length];

        for (int i = 0; i < path.length; i++) {
            p[i] = path[path.length - 1 - i];
        }

        return normalize(p);
    }

    //  rotate cycle path such that it begins with the smallest node
    private static int[] normalize(int[] path) {
        int[] p = new int[path.length];
        int x = smallest(path);
        int n;

        System.arraycopy(path, 0, p, 0, path.length);

        while (p[0] != x) {
            n = p[0];
            System.arraycopy(p, 1, p, 0, p.length - 1);
            p[p.length - 1] = n;
        }

        return p;
    }

    //  compare path against known cycles
    //  return true, iff path is not a known cycle
    private static boolean isNew(int[] path) {
        Boolean ret = true;

        for (int[] p : cycles) {
            if (equals(p, path)) {
                ret = false;
                break;
            }
        }

        return ret;
    }

    //  return the int of the array which is the smallest
    private static int smallest(int[] path) {
        int min = path[0];

        for (int p : path)
            if (p < min)
                min = p;

        return min;
    }

    //  check if vertex n is contained in path
    private static boolean visited(int n, int[] path) {
        Boolean ret = false;

        for (int p : path) {
            if (p == n) {
                ret = true;
                break;
            }
        }

        return ret;
    }

    private static List<PVector> sortCorners(List<PVector> quad) {

        // 1 - Sort corners so that they are ordered clockwise
        PVector a = quad.get(0);
        PVector b = quad.get(2);

        PVector center = new PVector((a.x + b.x) / 2, (a.y + b.y) / 2);

        Collections.sort(quad, new CWComparator(center));


        // 2 - Sort by upper left most corner
        PVector origin = new PVector(0, 0);
        float distToOrigin = 1000;

        for (PVector p : quad) {
            if (p.dist(origin) < distToOrigin) distToOrigin = p.dist(origin);
        }

        while (quad.get(0).dist(origin) != distToOrigin)
            Collections.rotate(quad, 1);


        return quad;
    }
}

class CWComparator implements Comparator<PVector> {

    private PVector center;

    CWComparator(PVector center) {
        this.center = center;
    }

    @Override
    public int compare(PVector b, PVector d) {
        if (Math.atan2(b.y - center.y, b.x - center.x) < Math.atan2(d.y - center.y, d.x - center.x))
            return -1;
        else return 1;
    }
}