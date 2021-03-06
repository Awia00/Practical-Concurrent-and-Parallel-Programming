// Pipelined sorting using P>=1 stages, each maintaining an internal
// collection of size S>=1.  Stage 1 contains the largest items, stage
// 2 the second largest, ..., stage P the smallest ones.  In each
// stage, the internal collection of items is organized as a minheap.
// When a stage receives an item x and its collection is not full, it
// inserts it in the heap.  If the collection is full and x is less
// than or equal to the collections's least item, it forwards the item
// to the next stage; otherwise forwards the collection's least item
// and inserts x into the collection instead.

// When there are itemCount items and stageCount stages, each stage
// must be able to hold at least ceil(itemCount/stageCount) items,
// which equals (itemCount-1)/stageCount+1.

// sestoft@itu.dk * 2016-01-10

import java.util.stream.DoubleStream;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.IntToDoubleFunction;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.*;
import org.multiverse.api.references.*;
import org.multiverse.api.StmUtils;
import static org.multiverse.api.StmUtils.*;

public class SortingPipeline {
  public static void main(String[] args) {
    SystemInfo();
    final int count = 100_000, P = 5;
    testBlockingNSortPipeline(40, P);
    testWrappedSortPipeline(40, P);
    testUnboundedSortPipeline(40, P);
    testWaitFreeSortPipeline(40, P);
    testMSUnboundedSortPipeline(40, P);
    testStmBlockingSortPipeline(40, P);
    Mark7("WrapppedArrayDoubleQueue: ", 
      i -> testWrappedSortPipeline(count, P));
    Mark7("BlockingNDoubleQueue: ", 
      i -> testBlockingNSortPipeline(count, P));
    Mark7("UnboundedDoubleQueue: ", 
      i -> testUnboundedSortPipeline(count, P));
    Mark7("WaitFreeQueue: ", 
      i -> testWaitFreeSortPipeline(count, P));
    Mark7("MSUnboundedDoubleQueue: ", 
      i -> testMSUnboundedSortPipeline(count, P));
    Mark7("StmBlockingNDoubleQueue: ", 
      i -> testStmBlockingSortPipeline(count, P));
  }

  private static double testWrappedSortPipeline(final int count, final int P)
  {
    final double[] arr = DoubleArray.randomPermutation(count);
    final BlockingDoubleQueue[] queues = new BlockingDoubleQueue[P+1];
    for(int i = 0; i < P+1; i++)
    {
      queues[i] = new WrapppedArrayDoubleQueue();
    } 
    sortPipeline(arr, P, queues);
    return (double)arr.length;
  }
  
  private static double testBlockingNSortPipeline(final int count, final int P)
  {
    final double[] arr = DoubleArray.randomPermutation(count);
    final BlockingDoubleQueue[] queues = new BlockingDoubleQueue[P+1];
    for(int i = 0; i < P+1; i++)
    {
      queues[i] = new BlockingNDoubleQueue();
    } 
    sortPipeline(arr, P, queues);
    return (double)arr.length;
  }
  
  private static double testUnboundedSortPipeline(final int count, final int P)
  {
    final double[] arr = DoubleArray.randomPermutation(count);
    final BlockingDoubleQueue[] queues = new BlockingDoubleQueue[P+1];
    for(int i = 0; i < P+1; i++)
    {
      queues[i] = new UnboundedDoubleQueue();
    } 
    sortPipeline(arr, P, queues);
    return (double)arr.length;
  }

  private static double testWaitFreeSortPipeline(final int count, final int P)
  {
    final double[] arr = DoubleArray.randomPermutation(count);
    final BlockingDoubleQueue[] queues = new BlockingDoubleQueue[P+1];
    for(int i = 0; i < P+1; i++)
    {
      queues[i] = new WaitFreeQueue(50);
    } 
    sortPipeline(arr, P, queues);
    return (double)arr.length;
  }

  private static double testMSUnboundedSortPipeline(final int count, final int P)
  {
    final double[] arr = DoubleArray.randomPermutation(count);
    final BlockingDoubleQueue[] queues = new BlockingDoubleQueue[P+1];
    for(int i = 0; i < P+1; i++)
    {
      queues[i] = new MSUnboundedDoubleQueue();
    } 
    sortPipeline(arr, P, queues);
    return (double)arr.length;
  }

  private static double testStmBlockingSortPipeline(final int count, final int P)
  {
    final double[] arr = DoubleArray.randomPermutation(count);
    final BlockingDoubleQueue[] queues = new BlockingDoubleQueue[P+1];
    for(int i = 0; i < P+1; i++)
    {
      queues[i] = new StmBlockingNDoubleQueue();
    } 
    sortPipeline(arr, P, queues);
    return (double)arr.length;
  }


  private static void sortPipeline(final double[] arr, final int P, final BlockingDoubleQueue[] queues) {
    final Thread[] threads = new Thread[P+2];
    final int size = arr.length/P;

    threads[0] = new Thread(new DoubleGenerator(arr, arr.length, queues[0])); 
    threads[P+1] = new Thread(new SortedChecker(arr.length, queues[P]));
    threads[0].start();
    threads[P+1].start();

    for(int i = 1; i<=P; i++)
    {
      threads[i] = new Thread(new SortingStage(queues[i-1], queues[i], size, arr.length, P, i));
      threads[i].start();
    }
    for(int i = 0; i<P+2; i++)
    {
      try
      {
        threads[i].join();
      }catch(Exception e)
      {
        System.out.println(e);
      }
    }
  }

  static class SortingStage implements Runnable {
    private final BlockingDoubleQueue input, output;
    private final double[] heap;
    private int heapSize;
    private int itemCount;
    
    SortingStage(BlockingDoubleQueue input, BlockingDoubleQueue output, int size, int n, int p, int i)
    {
      this.input = input;
      this.output = output;
      this.itemCount = n+(p-i)*size;
      this.heap = new double[size];
    }
    
    public void run() { 
      while (itemCount > 0) {
        double x = input.take();
        if (heapSize < heap.length) { // heap not full, put x into it
          heap[heapSize++] = x;
          DoubleArray.minheapSiftup(heap, heapSize-1, heapSize-1);
        } else if (x <= heap[0]) { // x is small, forward
          output.put(x);
          itemCount--;
        } else { // forward least, replace with x
          double least = heap[0];
          heap[0] = x;
          DoubleArray.minheapSiftdown(heap, 0, heapSize-1);
          output.put(least);
          itemCount--;
        }
      }
    }
  }

  static class DoubleGenerator implements Runnable {
    private final BlockingDoubleQueue output;
    private final double[] arr;  // The numbers to feed to output
    private final int infinites;

    public DoubleGenerator(double[] arr, int infinites, BlockingDoubleQueue output) {
      this.arr = arr;
      this.output = output;
      this.infinites = infinites;
    }

    public void run() { 
      for (int i=0; i<arr.length; i++)  // The numbers to sort
        output.put(arr[i]);
      for (int i=0; i<infinites; i++)   // Infinite numbers for wash-out
        output.put(Double.POSITIVE_INFINITY);
    }
  }

  static class SortedChecker implements Runnable {
    // If DEBUG is true, print the first 100 numbers received
    private final static boolean DEBUG = false;
    private final BlockingDoubleQueue input;
    private final int itemCount; // the number of items to check

    public SortedChecker(int itemCount, BlockingDoubleQueue input) {
      this.itemCount = itemCount;
      this.input = input;
    }

    public void run() { 
      int consumed = 0;
      double last = Double.NEGATIVE_INFINITY;
      while (consumed++ < itemCount) {
        double p = input.take();
        if (DEBUG && consumed <= 100) 
          System.out.print(p + " ");
        if (p <= last)
          System.out.printf("Elements out of order: %g before %g%n", last, p);
        last = p;
      }
      if (DEBUG)
        System.out.println();
    }
  }

  // --- Benchmarking infrastructure ---

  // NB: Modified to show milliseconds instead of nanoseconds

  public static double Mark7(String msg, IntToDoubleFunction f) {
    int n = 10, count = 1, totalCount = 0;
    double dummy = 0.0, runningTime = 0.0, st = 0.0, sst = 0.0;
    do { 
      count *= 2;
      st = sst = 0.0;
      for (int j=0; j<n; j++) {
        Timer t = new Timer();
        for (int i=0; i<count; i++) 
          dummy += f.applyAsDouble(i);
        runningTime = t.check();
        double time = runningTime * 1e3 / count;
        st += time; 
        sst += time * time;
        totalCount += count;
      }
    } while (runningTime < 0.25 && count < Integer.MAX_VALUE/2);
    double mean = st/n, sdev = Math.sqrt((sst - mean*mean*n)/(n-1));
    System.out.printf("%-25s %15.1f ms %10.2f %10d%n", msg, mean, sdev, count);
    return dummy / totalCount;
  }

  public static void SystemInfo() {
    System.out.printf("# OS:   %s; %s; %s%n", 
                      System.getProperty("os.name"), 
                      System.getProperty("os.version"), 
                      System.getProperty("os.arch"));
    System.out.printf("# JVM:  %s; %s%n", 
                      System.getProperty("java.vendor"), 
                      System.getProperty("java.version"));
    // The processor identifier works only on MS Windows:
    System.out.printf("# CPU:  %s; %d \"cores\"%n", 
                      System.getenv("PROCESSOR_IDENTIFIER"),
                      Runtime.getRuntime().availableProcessors());
    java.util.Date now = new java.util.Date();
    System.out.printf("# Date: %s%n", 
      new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(now));
  }

  // Crude wall clock timing utility, measuring time in seconds
   
  static class Timer {
    private long start, spent = 0;
    public Timer() { play(); }
    public double check() { return (System.nanoTime()-start+spent)/1e9; }
    public void pause() { spent += System.nanoTime()-start; }
    public void play() { start = System.nanoTime(); }
  }
}

// ----------------------------------------------------------------------

// Queue interface

interface BlockingDoubleQueue {
  double take();
  void put(double item);
}

class WrapppedArrayDoubleQueue implements BlockingDoubleQueue {
  private ArrayBlockingQueue<Double> queue;
  public WrapppedArrayDoubleQueue()
  {
    queue = new ArrayBlockingQueue<Double>(50);
  }
  
  public double take()
  {
    try{
      return queue.take();
    } catch(Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void put(double item)
  {
    try{
      queue.put(item);
    } catch(Exception e) {}
  }
}

class BlockingNDoubleQueue implements BlockingDoubleQueue {
  private final double[] arr = new double[50];
  private volatile int head, tail;
  private final Object lock = new Object();

  public double take()
  {
    synchronized(lock)
    {
      while(head==tail) 
      {
        try { lock.wait(); } 
        catch (InterruptedException exn) { } 
      }
      
      double element = arr[head];
      head = (head + 1) % arr.length;
      lock.notifyAll();
      return element;
    }
  }

  public void put(double item)
  {
    synchronized(lock)
    {
      while((tail + 1) % arr.length == head)
      {
        try { lock.wait(); } 
        catch (InterruptedException exn) { } 
      }
      arr[tail] = item;
      tail = (tail + 1) % arr.length;
      lock.notifyAll();
    }
  }
}

class UnboundedDoubleQueue implements BlockingDoubleQueue {
  private final Node sentinel = new Node(null, 0);
  private Node head = sentinel, tail = sentinel;

  public double take()
  {
    synchronized(head) // if head is tail then I also have that lock
    {
      while(head == tail) // if this is the case they are both the sentinel : the queue is empty
      {
        try { head.wait(); } 
        catch (InterruptedException exn) { } 
      }
      head = head.getNext();
      double element = head.getValue();
      return element;
    }
  }

  public void put(double item)
  {
    synchronized(tail) // if tail is head then I also have that lock
    {
      Node prev = tail;
      Node newNode = new Node(null, item);
      tail.setNext(newNode);
      tail = newNode;
      prev.notifyAll();
    }
  }

  private class Node {
    private Node next;
    private final double value;
    public Node(Node next, double value)
    {
      this.next = next;
      this.value = value;
    }

    public void setNext(Node node)
    {
      next = node;
    }

    public Node getNext()
    {
      return next;
    }

    public double getValue()
    {
      return value;
    }
  }
}

class WaitFreeQueue implements BlockingDoubleQueue {
  private volatile int head = 0, tail = 0;
  private final double[] items;

  public WaitFreeQueue(int capacity)
  {
    items = new double[capacity];
  }

  public double take()
  {
    while(tail - head == 0)
    {
      Thread.yield();
    }
    double x = items[head % items.length];
    head++;
    return x;
  }

  public void put(double item)
  {
    while(tail - head == items.length)
    {
      Thread.yield();
    }
    items[tail % items.length] = item;
    tail++;
  }
}

class MSUnboundedDoubleQueue implements BlockingDoubleQueue {
  private AtomicReference<Node> head, tail;
  private final AtomicReferenceFieldUpdater<Node, Node> nextUpdater = AtomicReferenceFieldUpdater.newUpdater(Node.class, Node.class, "next");
  public MSUnboundedDoubleQueue()
  {
    Node sentinel = new Node(null, 0);
    head = new AtomicReference<Node>(sentinel);
    tail = new AtomicReference<Node>(sentinel);
  }
  public double take()
  {
    while(true)
    {
      Node first = head.get(),
          last = tail.get(),
          next = first.next;
      if (first == head.get()) {
        if (first == last) {
          if (next == null)
          {
            Thread.yield();
            continue;
          }
          else
            tail.compareAndSet(last, next);
        } else {
          double result = next.item;
          if(head.compareAndSet(first, next))
          {
            return result;
          }
        }
      }
    } 
  }

  public void put(double item)
  {
    Node node = new Node(null, item);
    while(true)
    {
      Node last = tail.get(), next = last.next;
      if(last == tail.get())
      {
        if(next == null) 
        {
          if(nextUpdater.compareAndSet(last, next, node))
          {
            tail.compareAndSet(last, node);
            return;
          }
        } else {
          tail.compareAndSet(last, next);
        }
      }
    }
  }

  private class Node {
    public volatile Node next;
    public final double item;
    public Node(Node next, double item)
    {
      this.next = next;
      this.item = item;
    }

    
  }
}

class StmBlockingNDoubleQueue implements BlockingDoubleQueue {
  private final double[] arr = new double[50];
  private volatile int head, tail;

  public double take()
  {
    return atomic ( () -> {      
      while(head==tail) 
      {
        Thread.yield();
      }
      
      double element = arr[head];
      head = (head + 1) % arr.length;
      return element;
    });
  }

  public void put(double item)
  {
    atomic ( () -> {  
      while((tail + 1) % arr.length == head)
      {
        Thread.yield();
      }
      arr[tail] = item;
      tail = (tail + 1) % arr.length;
    });
  }
}

// ----------------------------------------------------------------------

class DoubleArray {
  public static double[] randomPermutation(int n) {
    double[] arr = fillDoubleArray(n);
    shuffle(arr);
    return arr;
  }

  private static double[] fillDoubleArray(int n) {
    double[] arr = new double[n];
    for (int i = 0; i < n; i++)
      arr[i] = i + 0.1;
    return arr;
  }

  private static final java.util.Random rnd = new java.util.Random();

  private static void shuffle(double[] arr) {
    for (int i = arr.length-1; i > 0; i--)
      swap(arr, i, rnd.nextInt(i+1));
  }

  // Swap arr[s] and arr[t]
  private static void swap(double[] arr, int s, int t) {
    double tmp = arr[s]; arr[s] = arr[t]; arr[t] = tmp;
  }

  // Minheap operations for parallel sort pipelines.  
  // Minheap invariant: 
  // If heap[0..k-1] is a minheap, then heap[(i-1)/2] <= heap[i] for
  // all indexes i=1..k-1.  Thus heap[0] is the smallest element.

  // Although stored in an array, the heap can be considered a tree
  // where each element heap[i] is a node and heap[(i-1)/2] is its
  // parent. Then heap[0] is the tree's root and a node heap[i] has
  // children heap[2*i+1] and heap[2*i+2] if these are in the heap.

  // In heap[0..k], move node heap[i] downwards by swapping it with
  // its smallest child until the heap invariant is reestablished.

  public static void minheapSiftdown(double[] heap, int i, int k) {
    int child = 2 * i + 1;                          
    if (child <= k) {
      if (child+1 <= k && heap[child] > heap[child+1])
        child++;                                  
      if (heap[i] > heap[child]) {
        swap(heap, i, child); 
        minheapSiftdown(heap, child, k); 
      }
    }
  }

  // In heap[0..k], move node heap[i] upwards by swapping with its
  // parent until the heap invariant is reestablished.
  public static void minheapSiftup(double[] heap, int i, int k) {
    if (0 < i) {
      int parent = (i - 1) / 2;
      if (heap[i] < heap[parent]) {
        swap(heap, i, parent); 
        minheapSiftup(heap, parent, k); 
      }
    }
  }
}
