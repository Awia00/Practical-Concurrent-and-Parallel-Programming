package kmean;

import org.multiverse.api.references.TxnDouble;
import org.multiverse.api.references.TxnInteger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.Executors.newWorkStealingPool;
import static org.multiverse.api.StmUtils.atomic;
import static org.multiverse.api.StmUtils.newTxnDouble;
import static org.multiverse.api.StmUtils.newTxnInteger;

/**
 * Created by ander on 10-01-2017.
 */
public class KMeans2Stm implements KMeans {
    // Sequential version 2.  Data represention: An array points of
    // Points and a same-index array myCluster of the Cluster to which
    // each point belongs, so that points[pi] belongs to myCluster[pi],
    // for each Point index pi.  A Cluster holds a mutable mean field
    // and has methods for aggregation of its value.

    private final Point[] points;
    private final int k;
    private Cluster[] clusters;
    private int iterations;

    public KMeans2Stm(Point[] points, int k) {
        this.points = points;
        this.k = k;
    }

    public void findClusters(int[] initialPoints) {
        final Cluster[] clusters = GenerateData.initialClusters(points, initialPoints, Cluster::new, Cluster[]::new);
        final Cluster[] myCluster = new Cluster[points.length];
        final AtomicBoolean converged = new AtomicBoolean();
        ExecutorService executorService = newWorkStealingPool();
        while (!converged.get()) {
            final int taskCount = 8, perTask = points.length / taskCount;
            iterations++;
            {
                // Assignment step: put each point in exactly one cluster
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
            {
                for (Cluster c : clusters)
                    c.resetMean();
                // Update step: recompute mean of each cluster
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

                boolean localConverged = true;
                for (Cluster c : clusters)
                    localConverged &= c.computeNewMean();
                converged.set(localConverged);
            }
            // System.out.printf("[%d]", iterations); // To diagnose infinite loops
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
        private final TxnDouble sumx, sumy;
        private final TxnInteger count;

        public Cluster(Point mean) {
            sumx = newTxnDouble();
            sumy = newTxnDouble();
            count = newTxnInteger();
            this.mean = mean;
        }

        public void addToMean(Point p) {
            atomic (() ->{
                sumx.set(sumx.get() + p.x);
                sumy.set(sumy.get() + p.y);
                count.increment();
            });
        }

        // Recompute mean, return true if it stays almost the same, else false
        public boolean computeNewMean() {
            Point oldMean = this.mean;
            atomic( () -> {
                this.mean = new Point(sumx.get()/count.get(), sumy.get()/count.get());
            });
            return oldMean.almostEquals(this.mean);
        }

        public void resetMean() {
            atomic( () -> {
                sumx.set(0.0);
                sumy.set(0.0);
                count.set(0);
            });
        }

        @Override
        public Point getMean() {
            return mean;
        }
    }
}
