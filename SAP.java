import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {
    private final Digraph G;
    
        // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        this.G = new Digraph(G);
    }
    
        // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        int[] res = shortest(v, w);
        return res[0];
    }
    
        // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        int[] res = shortest(v, w);
        return res[1];
    }
    
        // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        int [] res = shortest(v, w);
        return res[0];
    }
    
        // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        int[] res = shortest(v, w);
        return res[1];
    }
    
//        // do unit testing of this class
//    public static void main(String[] args)
        private int[] shortest(int w, int v) {
        int[] res = new int[2];
        DeluxeBFS wBFS = new DeluxeBFS(G, w);
        DeluxeBFS vBFS = new DeluxeBFS(G, v);
        boolean[] wMarked = wBFS.getMarked();
        boolean[] vMarked = vBFS.getMarked();
        int minDis = Integer.MAX_VALUE;
        int anc = Integer.MAX_VALUE;
        
        for (int i = 0; i < wMarked.length; i++) {
            if (wMarked[i] && vMarked[i]) {
                int dis = wBFS.distTo(i) + vBFS.distTo(i);
                if (dis < minDis) {
                    minDis = dis;
                    anc = i;
                }
            }
        }
        if (minDis == Integer.MAX_VALUE) {
            res[0] = -1;
            res[1] = -1;
            return res;
        } else {
            res[0] = minDis;
            res[1] = anc;
            return res;
        }
    }
    private int[]  shortest(Iterable<Integer> v, Iterable<Integer> w) {
        int[] res = new int[2];
        int minDis = Integer.MAX_VALUE;
        int anc = Integer.MAX_VALUE;
        
        for (int i : v) {
            for (int j : w) {
                int[] temp = shortest(i, j);
                if (temp[0] != -1 && temp[0] < minDis) {
                    minDis = temp[0];
                    anc = temp[1];
                }
            }
        }
        if (minDis == Integer.MAX_VALUE) {
            res[0] = -1;
            res[1] = -1;
            return res;
        } else {
            res[0] = minDis;
            res[1] = anc;
            return res;
        }
    }
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
