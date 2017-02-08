package kmean;

/**
 * Created by ander on 10-01-2017.
 */
public class KMeans2 implements KMeans {
    // Sequential version 2.  Data represention: An array points of
    // Points and a same-index array myCluster of the Cluster to which
    // each point belongs, so that points[pi] belongs to myCluster[pi],
    // for each Point index pi.  A Cluster holds a mutable mean field
    // and has methods for aggregation of its value.

    private final Point[] points;
    private final int k;
    private Cluster[] clusters;
    private int iterations;

    public KMeans2(Point[] points, int k) {
        this.points = points;
        this.k = k;
    }

    public void findClusters(int[] initialPoints) {
        final Cluster[] clusters = GenerateData.initialClusters(points, initialPoints, Cluster::new, Cluster[]::new);
        final Cluster[] myCluster = new Cluster[points.length];
        boolean converged = false;
        while (!converged) {
            iterations++;
            {
                // Assignment step: put each point in exactly one cluster
                for (int pi=0; pi<points.length; pi++) {
                    Point p = points[pi];
                    Cluster best = null;
                    for (Cluster c : clusters)
                        if (best == null || p.sqrDist(c.mean) < p.sqrDist(best.mean))
                            best = c;
                    myCluster[pi] = best;
                }
            }
            {
                // Update step: recompute mean of each cluster
                for (Cluster c : clusters)
                    c.resetMean();
                for (int pi=0; pi<points.length; pi++)
                    myCluster[pi].addToMean(points[pi]);
                converged = true;
                for (Cluster c : clusters)
                    converged &= c.computeNewMean();
            }
            // System.out.printf("[%d]", iterations); // To diagnose infinite loops
        }
        this.clusters = clusters;
    }

    public void print() {
        for (Cluster c : clusters)
            System.out.println(c);
        System.out.printf("Used %d iterations%n", iterations);
    }

    static class Cluster extends ClusterBase {
        private Point mean;
        private double sumx, sumy;
        private int count;

        public Cluster(Point mean) {
            this.mean = mean;
        }

        public void addToMean(Point p) {
            sumx += p.x;
            sumy += p.y;
            count++;
        }

        // Recompute mean, return true if it stays almost the same, else false
        public boolean computeNewMean() {
            Point oldMean = this.mean;
            this.mean = new Point(sumx/count, sumy/count);
            return oldMean.almostEquals(this.mean);
        }

        public void resetMean() {
            sumx = sumy = 0.0;
            count = 0;
        }

        @Override
        public Point getMean() {
            return mean;
        }
    }
}
