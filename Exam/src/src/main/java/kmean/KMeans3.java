package kmean;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by ander on 10-01-2017.
 */
public class KMeans3 implements KMeans {
    // Stream-based version. Representation (A2): Immutable Clusters of
    // immutable Points.

    private final Point[] points;
    private final int k;
    private Cluster[] clusters;
    private int iterations;

    public KMeans3(Point[] points, int k) {
        this.points = points;
        this.k = k;
    }

    public void findClusters(int[] initialPoints) {
        Cluster[] clusters = GenerateData.initialClusters(points, initialPoints, Cluster::new, Cluster[]::new);
        boolean converged = false;
        while (!converged) {
            iterations++;
            { // Assignment step: put each point in exactly one cluster
                final Cluster[] clustersLocal = clusters;  // For capture in lambda
                Stream<Point> pointStream = Arrays.stream(points);
                Map<Cluster, List<Point>> groups = pointStream.collect(Collectors.groupingBy((Point p) -> {
                    Cluster best = null;
                    for (Cluster c : clustersLocal)
                        if (best == null || p.sqrDist(c.mean) < p.sqrDist(best.mean))
                            best = c;
                    return best;
                }));
                clusters = groups.entrySet().stream().map( (Map.Entry<Cluster, List<Point>> kv) -> new Cluster(kv.getKey().mean, kv.getValue())).toArray(size -> new Cluster[size]);
            }
            { // Update step: recompute mean of each cluster
                 Cluster[] newClusters = (Cluster[]) Arrays.stream(clusters).map((Cluster c) -> c.computeMean()).toArray(size -> new Cluster[size]);
                converged = Arrays.equals(clusters, newClusters);
                clusters = newClusters;
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
        private final List<Point> points;
        private final Point mean;

        public Cluster(Point mean) {
            this(mean, new ArrayList<>());
        }

        public Cluster(Point mean, List<Point> points) {
            this.mean = mean;
            this.points = Collections.unmodifiableList(points);
        }

        @Override
        public Point getMean() {
            return mean;
        }

        public Cluster computeMean() {
            double sumx = points.stream().mapToDouble(p -> p.x).sum(),
                    sumy = points.stream().mapToDouble(p -> p.y).sum();
            Point newMean = new Point(sumx/points.size(), sumy/points.size());
            return new Cluster(newMean, points);
        }
    }
}