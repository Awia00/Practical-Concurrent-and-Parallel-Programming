package kmean;

import java.util.ArrayList;

/**
 * Created by ander on 10-01-2017.
 */
public class KMeans1 implements KMeans {
    // Sequential version 1.  A Cluster has an immutable mean field, and
    // a mutable list of immutable Points.

    private final Point[] points;
    private final int k;
    private Cluster[] clusters;
    private int iterations;

    public KMeans1(Point[] points, int k) {
        this.points = points;
        this.k = k;
    }

    public void findClusters(int[] initialPoints) {
        Cluster[] clusters = GenerateData.initialClusters(points, initialPoints, Cluster::new, Cluster[]::new);
        boolean converged = false;
        while (!converged) {
            iterations++;
            { // Assignment step: put each point in exactly one cluster
                for (Point p : points) {
                    Cluster best = null;
                    for (Cluster c : clusters)
                        if (best == null || p.sqrDist(c.mean) < p.sqrDist(best.mean))
                            best = c;
                    best.add(p);
                }
            }
            { // Update step: recompute mean of each cluster
                ArrayList<Cluster> newClusters = new ArrayList<>();
                converged = true;
                for (Cluster c : clusters) {
                    Point mean = c.computeMean();
                    if (!c.mean.almostEquals(mean))
                        converged = false;
                    if (mean != null)
                        newClusters.add(new Cluster(mean));
                    else
                        System.out.printf("===> Empty cluster at %s%n", c.mean);
                }
                clusters = newClusters.toArray(new Cluster[newClusters.size()]);
            }
        }
        this.clusters = clusters;
    }

    public void print() {
        for (Cluster c : clusters)
            System.out.println(c);
        System.out.printf("Used %d iterations%n", iterations);
    }

    static class Cluster extends ClusterBase {
        private final ArrayList<Point> points = new ArrayList<>();
        private final Point mean;

        public Cluster(Point mean) {
            this.mean = mean;
        }

        @Override
        public Point getMean() {
            return mean;
        }

        public void add(Point p) {
            points.add(p);
        }

        public Point computeMean() {
            double sumx = 0.0, sumy = 0.0;
            for (Point p : points) {
                sumx += p.x;
                sumy += p.y;
            }
            int count = points.size();
            return count == 0 ? null : new Point(sumx/count, sumy/count);
        }
    }
}