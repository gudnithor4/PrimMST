/*************************************************************************
 *  Compilation:  javac EdgeWeightedGraph.java
 *  Execution:    java EdgeWeightedGraph filename.txt
 *  Dependencies: Bag.java Edge.java In.java StdOut.java
 *
 *  An edge-weighted undirected graph, implemented using adjacency lists.
 *  Parallel edges and self-loops are permitted.
 *************************************************************************/

/**
 *  The EdgeWeightedGraph class represents an edge-weighted
 *  graph of vertices named 0 through V - 1, where each
 *  undirected edge is of type {@link Edge} and has a real-valued weight.
 *  It supports the following two primary operations: add an edge to the graph,
 *  iterate over all of the edges incident to a vertex. It also provides
 *  methods for returning the number of vertices V and the number
 *  of edges E. Parallel edges and self-loops are permitted.
 *  This implementation uses an adjacency-lists representation, which 
 *  is a vertex-indexed array of @link{Bag} objects.
 *  All operations take constant time (in the worst case) except
 *  iterating over the edges incident to a given vertex, which takes
 *  time proportional to the number of such edges.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
import java.lang.Runtime;

public class EdgeWeightedGraph {
    public  int V;
    public int E;
    private int count;
    private Bag<Edge>[] adj;
    
    /**
     * Initializes an empty edge-weighted graph with V vertices and 0 edges.
     * param V the number of vertices
     * @throws java.lang.IllegalArgumentException if V < 0
     */
    public EdgeWeightedGraph(int V) {
        if (V < 0) throw new IllegalArgumentException("Number of vertices must be nonnegative");
        this.V = V;
        this.E = 0;
        adj = (Bag<Edge>[]) new Bag[V];
        for (int v = 0; v < V; v++) {
            adj[v] = new Bag<Edge>();
        }
    }

    /**
     * Initializes a random edge-weighted graph with V vertices and E edges.
     * param V the number of vertices
     * param E the number of edges
     * @throws java.lang.IllegalArgumentException if V < 0
     * @throws java.lang.IllegalArgumentException if E < 0
     */
    public EdgeWeightedGraph(int V, int E) {
        this(V);
        if (E < 0) throw new IllegalArgumentException("Number of edges must be nonnegative");
        for (int i = 0; i < E; i++) {
            int v = (int) (Math.random() * V);
            int w = (int) (Math.random() * V);
            int weight = (int) (Math.round(100 * Math.random()) / 100.0);
            Edge e = new Edge(v, w, weight);
            addEdge(e);
        }
    }

    /**  
     * Initializes an edge-weighted graph from an input stream.
     * The format is the number of vertices V,
     * followed by the number of edges E,
     * followed by E pairs of vertices and edge weights,
     * with each entry separated by whitespace.
     * @param in the input stream
     * @throws java.lang.IndexOutOfBoundsException if the endpoints of any edge are not in prescribed range
     * @throws java.lang.IllegalArgumentException if the number of vertices or edges is negative
     */
    public EdgeWeightedGraph(In in) {
        this(in.readInt());
        while(!in.isEmpty()) {
            int v = in.readInt();
            int w = in.readInt();
            int weight = in.readInt();
            Edge e = new Edge(v, w, weight);
            addEdge(e);
        }
    }
    /**
     * Returns the number of vertices in the edge-weighted graph.
     * @return the number of vertices in the edge-weighted graph
     */
    public int V() {
        return V;
    }

    /**
     * Returns the number of edges in the edge-weighted graph.
     * @return the number of edges in the edge-weighted graph
     */
    public int E() {
        return E;
    }

    /**
     * Adds the undirected edge e to the edge-weighted graph.
     * @param e the edge
     * @throws java.lang.IndexOutOfBoundsException unless both endpoints are between 0 and V-1
     */
    public void addEdge(Edge e) {
        int v = e.either();
        int w = e.other(v);
        if (v < 0 || v >= V) throw new IndexOutOfBoundsException("vertex " + v + " is not between 0 and " + (V-1));
        if (w < 0 || w >= V) throw new IndexOutOfBoundsException("vertex " + w + " is not between 0 and " + (V-1));
        adj[v].add(e);
        adj[w].add(e);
        E++;
    }

    /**
     * Returns the edges incident on vertex v.
     * @return the edges incident on vertex v as an Iterable
     * @param v the vertex
     * @throws java.lang.IndexOutOfBoundsException unless 0 <= v < V
     */
    public Iterable<Edge> adj(int v) {
        if (v < 0 || v >= V) throw new IndexOutOfBoundsException("vertex " + v + " is not between 0 and " + (V-1));
        return adj[v];
    }

    /**
     * Returns all edges in the edge-weighted graph.
     * To iterate over the edges in the edge-weighted graph, use foreach notation:
     * for (Edge e : G.edges()).
     * @return all edges in the edge-weighted graph as an Iterable.
     */
    public Iterable<Edge> edges() {
        Bag<Edge> list = new Bag<Edge>();
        for (int v = 0; v < V; v++) {
            int selfLoops = 0;
            for (Edge e : adj(v)) {
                if (e.other(v) > v) {
                    list.add(e);
                }
                // only add one copy of each self loop (self loops will be consecutive)
                else if (e.other(v) == v) {
                    if (selfLoops % 2 == 0) list.add(e);
                    selfLoops++;
                }
            }
        }
        return list;
    }

    /**
     * Returns a string representation of the edge-weighted graph.
     * This method takes time proportional to E + V.
     * @return the number of vertices V, followed by the number of edges E,
     *   followed by the V adjacency lists of edges
     */
    public String toString() {
        String NEWLINE = System.getProperty("line.separator");
        StringBuilder s = new StringBuilder();
        s.append(V + " " + E + NEWLINE);
        for (int v = 0; v < V; v++) {
            s.append(v + ": ");
            for (Edge e : adj[v]) {
                s.append(e + "  ");
            }
            s.append(NEWLINE);
        }
        return s.toString();
    }

}
