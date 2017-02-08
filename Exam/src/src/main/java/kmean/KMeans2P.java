package kmean;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static java.util.concurrent.Executors.newWorkStealingPool;

/**
 * Created by ander on 10-01-2017.
 */
public class KMeans2P implements KMeans {
    // Sequential version 2.  Data represention: An array points of
    // Points and a same-index array myCluster of the Cluster to which
    // each point belongs, so that points[pi] belongs to myCluster[pi],
    // for each Point index pi.  A Cluster holds a mutable mean field
    // and has methods for aggregation of its value.

    private final Point[] points;
    private final int k;
    private Cluster[] clusters;
    private int iterations;

    public KMeans2P(Point[] points, int k) {
        this.points = points;
        this.k = k;
    }

    public void findClusters(int[] initialPoints) {
        final Cluster[] clusters = GenerateData.initialClusters(points, initialPoints, Cluster::new, Cluster[]::new);
        final Cluster[] myCluster = new Cluster[points.length];
        boolean converged = false;
        ExecutorService executorService = newWorkStealingPool();
        while (!converged) {
            final int taskCount = 8, perTask = points.length / taskCount;
            iterations++;
            { // Assignment step: put each point in exactly one cluster
                List<Callable<Void>> tasks = new ArrayList<>();
                for(int t = 0; t<taskCount; t++)
                {
                    final int
                            from = perTask * t,
                            to = (t+1 == taskCount) ? points.length : perTask * (t+1);

                    tasks.add(() -> {
                        for (int pi=from; pi<to; pi++) {
                            Point p = points[pi];
                            Cluster best = null;
                            for (Cluster c : clusters)
                                if (best == null || p.sqrDist(c.mean) < p.sqrDist(best.mean))
                                    best = c;
                            myCluster[pi] = best;
                        }
                        return null;
                    });
                }
                try {
                    List<Future<Void>> futures = executorService.invokeAll(tasks);
                    for (Future<?> future : futures)
                        future.get();
                } catch (InterruptedException exn) {
                    System.out.println("Interrupted: " + exn);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
            { // Update step: recompute mean of each cluster
                for (Cluster c : clusters)
                    c.resetMean();

                List<Callable<Void>> tasks = new ArrayList<>();
                for(int t = 0; t<taskCount; t++)
                {
                    final int
                            fromPoints = perTask * t,
                            toPoints = (t+1 == taskCount) ? points.length : perTask * (t+1);

                    tasks.add(() -> {
                        for(int pi = fromPoints; pi<toPoints; pi++)
                        {
                            myCluster[pi].addToMean(points[pi]);
                        }
                        return null;
                    });
                }
                try {
                    List<Future<Void>> futures = executorService.invokeAll(tasks);
                    for (Future<?> future : futures)
                        future.get();
                } catch (InterruptedException exn) {
                    System.out.println("Interrupted: " + exn);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }

                converged = true;
                for (Cluster c : clusters)
                    converged &= c.computeNewMean();
            }
            //System.out.printf("[%d]", iterations); // To diagnose infinite loops
        }
        this.clusters = clusters;
        executorService.shutdown();
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

        private Object lock = new Object();
        public void addToMean(Point p) {
            //synchronized (lock) {
                sumx += p.x;
                sumy += p.y;
                count++;
            //}
        }

        // Recompute mean, return true if it stays almost the same, else false
        public boolean computeNewMean() {
            Point oldMean;
            //synchronized (lock) {
                oldMean = this.mean;
                this.mean = new Point(sumx / count, sumy / count);
            //}
            return oldMean.almostEquals(this.mean);
        }

        public void resetMean() {
            //synchronized (lock) {
                sumx = sumy = 0.0;
                count = 0;
            //}
        }

        @Override
        public Point getMean() {
            return mean;
        }
    }
}
