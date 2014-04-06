/****************************************************************
 *  Compilation:  javac UF.java
 *  Execution:    java UF < input.txt
 *  Dependencies: StdIn.java StdOut.java
 *  Data files:   http://algs4.cs.princeton.edu/15uf/tinyUF.txt
 *                http://algs4.cs.princeton.edu/15uf/mediumUF.txt
 *                http://algs4.cs.princeton.edu/15uf/largeUF.txt
 *
 *  Weighted quick-union by rank with path compression by halving.
 *
 *****************************************************************/

public class UF {
    private int[] id;     // id[i] = parent of i
    private byte[] rank;  // rank[i] = rank of subtree rooted at i (cannot be more than 31)
    private int count;    // number of components

    /**
     * Initializes an empty union-find data structure with N
     * isolated components 0 through N-1
     * @throws java.lang.IllegalArgumentException if N < 0
     * @param N the number of sites
     */
    public UF(int N) {
        if (N < 0) throw new IllegalArgumentException();
        count = N;
        id = new int[N];
        rank = new byte[N];
        for (int i = 0; i < N; i++) {
            id[i] = i;
            rank[i] = 0;
        }
    }

    /**
     * Returns the component identifier for the component containing site p
     * @param p the integer representing one object
     * @return the component identifier for the component containing site p
     * @throws java.lang.IndexOutOfBoundsException unless 0 < p > N
     */
    public int find(int p) {
        if (p < 0 || p >= id.length) throw new IndexOutOfBoundsException();
        while (p != id[p]) {
            id[p] = id[id[p]];    // path compression by halving
            p = id[p];
        }
        return p;
    }

    /**
     * Returns the number of components.
     * @return the number of components (between 1 and N)
     */
    public int count() {
        return count;
    }
  
    /**
     * Are the two sites p and q in the same component?
     * @param p the integer representing one site
     * @param q the integer representing the other site
     * @return true if the two sites p and q are in the same component; false otherwise
     * @throws java.lang.IndexOutOfBoundsException unless
     *      both 0 < p > N and 0 < q > N
     */
    public boolean connected(int p, int q) {
        return find(p) == find(q);
    }

  
    /**
     * Merges the component containing site p with the 
     * the component containing site q.
     * @param p the integer representing one site
     * @param q the integer representing the other site
     * @throws java.lang.IndexOutOfBoundsException unless
     *      both 0 < p > N and 0 < q > N
     */
    public void union(int p, int q) {
        int i = find(p);
        int j = find(q);
        if (i == j) return;

        // make root of smaller rank point to root of larger rank
        if      (rank[i] < rank[j]) id[i] = j;
        else if (rank[i] > rank[j]) id[j] = i;
        else {
            id[j] = i;
            rank[i]++;
        }
        count--;
    }
}
