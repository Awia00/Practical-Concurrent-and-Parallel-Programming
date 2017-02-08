package kmean;// Various implementations of k-means clustering
// sestoft@itu.dk * 2017-01-04

import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TestKMeans {
  public static void main(String[] args) {
    // There are n points and k clusters
    ExtraUtil.SystemInfo();
    final int n = 200_000, k = 81;
    final Point[] points = GenerateData.randomPoints(n);
    final int[] initialPoints = GenerateData.randomIndexes(n, k);
    for (int i=0; i<3; i++) {
      // timeKMeans(new KMeans1(points, k), initialPoints);
      // timeKMeans(new KMeans1P(points, k), initialPoints);
      // timeKMeans(new KMeans2(points, k), initialPoints);
      // timeKMeans(new KMeans2P(points, k), initialPoints);
      // timeKMeans(new KMeans2Q(points, k), initialPoints);
      // timeKMeans(new KMeans2Stm(points, k), initialPoints);
      // timeKMeans(new KMeans3(points, k), initialPoints);
       timeKMeans(new KMeans3P(points, k), initialPoints);
      System.out.println();
    }
  }

  public static void timeKMeans(KMeans km, int[] initialPoints) {
    Timer t = new Timer();
    km.findClusters(initialPoints);
    double time = t.check();
    // To avoid seeing the k computed clusters, comment out next line: 
    km.print();
    System.out.printf("%-20s Real time: %9.3f%n", km.getClass(), time);
  }
}


// ----------------------------------------------------------------------



// ----------------------------------------------------------------------



// ----------------------------------------------------------------------



// ----------------------------------------------------------------------

// DO NOT MODIFY ANYTHING BELOW THIS LINE

// Immutable 2D points (x,y) with some basic operations

class Point {
  public final double x, y;

  public Point(double x, double y) {
    this.x = x;
    this.y = y;
  }

  // The square of the Euclidean distance between this Point and that
  public double sqrDist(Point that) {
    return sqr(this.x - that.x) + sqr(this.y - that.y);
  }
  
  private static double sqr(double d) {
    return d * d;
  }

  // Reasonable when original point coordinates are integers.
  private static final double epsilon = 1E-10;

  // Approximate equality of doubles and Points.  There are better
  // ways to do this, but here we prefer simplicity.
  public static boolean almostEquals(double x, double y) {
    return Math.abs(x - y) <= epsilon;
  }

  public boolean almostEquals(Point that) {
    return almostEquals(this.x, that.x) && almostEquals(this.y, that.y);
  }
  
  @Override
  public String toString() {
    return String.format("(%17.14f, %17.14f)", x, y);
  }
}

// Printing and approximate comparison of clusters

abstract class ClusterBase {
  public abstract Point getMean();

  // Two Clusters are considered equal if their means are almost equal
  @Override
  public boolean equals(Object o) {
    return o instanceof ClusterBase
      && this.getMean().almostEquals(((ClusterBase)o).getMean());
  }
  
  @Override
  public String toString() {
    return String.format("mean = %s", getMean());
  }
}

// Generation of test data

class GenerateData {
  // Intentionally not really random, for reproducibility
  private static final Random rnd = new Random(42);

  // An array of means (centers) of future point clusters,
  // approximately arranged in a 9x9 grid
  private static final Point[] centers =
    IntStream.range(0, 9).boxed()
       .flatMap(x -> IntStream.range(0, 9).mapToObj(y -> new Point(x*10+4, y*10+4)))
       .toArray(Point[]::new);

  // Make a random point near a randomly chosen center
  private static Point randomPoint() {
    Point orig = centers[rnd.nextInt(centers.length)];
    return new Point(orig.x + rnd.nextDouble() * 8, orig.y + rnd.nextDouble() * 8);
  }

  // Make an array of n points near some of the centers
  public static Point[] randomPoints(int n) {
    return Stream.<Point>generate(GenerateData::randomPoint).limit(n).toArray(Point[]::new);
  }

  // Make an array of k distinct random numbers of the range [0...n-1]
  public static int[] randomIndexes(int n, int k) {
    final HashSet<Integer> initial = new HashSet<>();
    while (initial.size() < k)
      initial.add(rnd.nextInt(n));
    return initial.stream().mapToInt(i -> i).toArray();
  }
  
  // Select k distinct Points as cluster centers, passing in functions
  // to create appropriate Cluster objects and Cluster arrays.
  public static <C extends ClusterBase>
    C[] initialClusters(Point[] points, int[] pointIndexes,
                        Function<Point,C> makeC, IntFunction<C[]> makeCArray)
  {
    C[] initial = makeCArray.apply(pointIndexes.length);
    for (int i=0; i<pointIndexes.length; i++)
      initial[i] = makeC.apply(points[pointIndexes[i]]);
    return initial;
  }
}

// Crude wall clock timing utility, measuring time in seconds
   
class Timer {
  private long start = 0, spent = 0;
  public Timer() { play(); }
  public double check() { return (start==0 ? spent : System.nanoTime()-start+spent)/1e9; }
  public void pause() { if (start != 0) { spent += System.nanoTime()-start; start = 0; } }
  public void play() { if (start == 0) start = System.nanoTime(); }
}


