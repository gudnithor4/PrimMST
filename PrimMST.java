/******************************************************************************
 *  Compilation:  javac PrimMST.java
 *  Execution:    java PrimMST filename.txt
 *  Dependencies: EdgeWeightedGraph.java Edge.java Queue.java
 *                IndexMinPQ.java UF.java In.java StdOut.java
 *
 *  Compute a minimum spanning tree/forest using Prim's algorithm.
 ******************************************************************************/

/**
 *  The PrimMST class represents a data type for computing a
 *  minimum spanning tree in an edge-weighted graph.
 *  The edge weights can be positive, zero, or negative and need not
 *  be distinct. If the graph is not connected, it computes a minimum
 *  spanning forest, which is the union of minimum spanning trees
 *  in each connected component. The weight() method returns the 
 *  weight of a minimum spanning tree and the edges() method
 *  returns its edges.
 *  This implementation uses Prim's algorithm with an indexed
 *  binary heap.
 *  The constructor takes time proportional to E log V
 *  and extra space (not including the graph) proportional to V,
 *  where V is the number of vertices and E is the number of edges.
 *  Afterwards, the weight() method takes constant time
 *  and the edges() method takes time proportional to V.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Collections;

public class PrimMST {
    private Edge[] edgeTo;        // edgeTo[v] = shortest edge from tree vertex to non-tree vertex
    private int[] distTo;      // distTo[v] = weight of shortest such edge
    private boolean[] marked;     // marked[v] = true if v on tree, false otherwise
    private IndexMinPQ<Integer> pq;

    /**
     * Compute a minimum spanning tree (or forest) of an edge-weighted graph.
     * @param G the edge-weighted graph
     */
    public PrimMST(EdgeWeightedGraph G) {
        edgeTo = new Edge[G.V()];
        distTo = new int[G.V()];
        marked = new boolean[G.V()];
        pq = new IndexMinPQ<Integer>(G.V());
        for (int v = 0; v < G.V(); v++) distTo[v] = Integer.MAX_VALUE;

        for (int v = 0; v < G.V(); v++)      // run from each vertex to find
            if (!marked[v]) prim(G, v);      // minimum spanning forest

        // check optimality conditions
        assert check(G);
    }

    // run Prim's algorithm in graph G, starting from vertex s
    private void prim(EdgeWeightedGraph G, int s) {
        distTo[s] = 0;
        pq.insert(s, distTo[s]);
        while (!pq.isEmpty()) {
            int v = pq.delMin();
            scan(G, v);
        }
    }

    // scan vertex v
    private void scan(EdgeWeightedGraph G, int v) {
        marked[v] = true;
        for (Edge e : G.adj(v)) {
            int w = e.other(v);
            if (marked[w]) continue;         // v-w is obsolete edge
            if (e.weight() < distTo[w]) {
                distTo[w] = e.weight();
                edgeTo[w] = e;
                if (pq.contains(w)) pq.decreaseKey(w, distTo[w]);
                else                pq.insert(w, distTo[w]);
            }
        }
    }

    /**
     * Returns the edges in a minimum spanning tree (or forest).
     * @return the edges in a minimum spanning tree (or forest) as
     *    an iterable of edges
     */
    public Iterable<Edge> edges() {
        Queue<Edge> mst = new Queue<Edge>();
        for (int v = 0; v < edgeTo.length; v++) {
            Edge e = edgeTo[v];
            if (e != null) {
                mst.enqueue(e);
            }
        }
        return mst;
    }

    /**
     * Returns the sum of the edge weights in a minimum spanning tree (or forest).
     * @return the sum of the edge weights in a minimum spanning tree (or forest)
     */
    public int weight() {
        int weight = 0;
        for (Edge e : edges())
            weight += e.weight();
        return weight;
    }


    // check optimality conditions (takes time proportional to E V lg* V)
    private boolean check(EdgeWeightedGraph G) {

        // check weight
        int totalWeight = 0;
        for (Edge e : edges()) {
            totalWeight += e.weight();
        }
        double EPSILON = 1E-12;
        if (Math.abs(totalWeight - weight()) > EPSILON) {
            System.err.printf("Weight of edges does not equal weight(): %d vs. %d\n", totalWeight, weight());
            return false;
        }

        // check that it is acyclic
        UF uf = new UF(G.V());
        for (Edge e : edges()) {
            int v = e.either(), w = e.other(v);
            if (uf.connected(v, w)) {
                System.err.println("Not a forest");
                return false;
            }
            uf.union(v, w);
        }

        // check that it is a spanning forest
        for (Edge e : G.edges()) {
            int v = e.either(), w = e.other(v);
            if (!uf.connected(v, w)) {
                System.err.println("Not a spanning forest");
                return false;
            }
        }

        // check that it is a minimal spanning forest (cut optimality conditions)
        for (Edge e : edges()) {

            // all edges in MST except e
            uf = new UF(G.V());
            for (Edge f : edges()) {
                int x = f.either(), y = f.other(x);
                if (f != e) uf.union(x, y);
            }

            // check that e is min weight edge in crossing cut
            for (Edge f : G.edges()) {
                int x = f.either(), y = f.other(x);
                if (!uf.connected(x, y)) {
                    if (f.weight() < e.weight()) {
                        System.err.println("Edge " + f + " violates cut optimality conditions");
                        return false;
                    }
                }
            }

        }

        return true;
    }

    public static void main(String[] args) {
        final long start = System.nanoTime();
        In in = new In(args[0]);
        String secondMST = "";
        LinkedList ll = new LinkedList();
        EdgeWeightedGraph G = new EdgeWeightedGraph(in);
        PrimMST mst = new PrimMST(G);

        //Finnum fyrst heildarvigt MST(G)
        StdOut.printf("%d\n", mst.weight());

        // Forum i gegnum hvern legg i MST(G)
        for (Edge e : mst.edges()) {
            // Nyja EWG hefur jafn marga hnuta og gamla
            EdgeWeightedGraph F = new EdgeWeightedGraph(G.V, 0);
            //Forum gegnum hvern legg i EWG G og ef leggurinn er ekki sami leggur
            //og vid erum ad skoda ur MST(G) tha er honum baett i nyja EWG-id okkar
            //annars er honum ekki baett vid.
            for (Edge f : G.edges()) {
                if (f != e){
                    F.addEdge(f);
                }
            }
            PrimMST mstF = new PrimMST(F);
            secondMST = String.valueOf(e) + " " + String.valueOf(mstF.weight());
            ll.add(secondMST);
        }

        Collections.sort(ll, new AlphanumComparator());
        for (int i = 0; i < ll.size(); i++) {
            StdOut.printf(ll.get(i) + "\n");
        }
        final long end = System.nanoTime();
        //System.out.println("Timi: " + ((end - start) / 1000000) + "ms");
    }


}
