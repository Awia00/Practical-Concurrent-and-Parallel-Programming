// For PCPP exam January 2015
// sestoft@itu.dk * 2015-01-03 with post-exam updates 2015-01-09

// Several versions of sequential and parallel quicksort: 
// A: sequential recursive
// B: sequential using work a deque as a stack

// To do by students:
// C: single-queue multi-threaded with shared lock-based queue
// D: multi-queue multi-threaded with thread-local lock-based queues and stealing
// E: as D but with thread-local lock-free queues and stealing

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.CyclicBarrier;


public class Quicksorts {
  final static int size = 100_000_000; // Number of integers to sort

  public static void main(String[] args) {
    System.out.println("Array size: " + size + "\n");
    //sequentialRecursive();
    // System.out.println(" --- SingleQueueSingleThread --- ");
    // singleQueueSingleThread();
    // System.out.println(" --- SingleQueueMultiThread --- ");
    // for(int i = 1; i<=16; i*=2)
    //   singleQueueMultiThread(i);
    // System.out.println(" --- MultiQueueMultiThread --- ");
    // for(int i = 1; i<=16; i*=2)
    //   multiQueueMultiThread(i);
    System.out.println(" --- MultiQueueMultiThreadCL --- ");
    for(int i = 1; i<=16; i*=2)
      multiQueueMultiThreadCL(i);

    //SimpleDequeTests.runAllTests();
  }

  // ----------------------------------------------------------------------
  // Version A: Standard sequential quicksort using recursion

  private static void sequentialRecursive() {
    int[] arr = IntArrayUtil.randomIntArray(size);
    qsort(arr, 0, arr.length-1);
    System.out.println(IntArrayUtil.isSorted(arr));
  }

  // Sort arr[a..b] endpoints inclusive
  private static void qsort(int[] arr, int a, int b) {
    if (a < b) { 
      int i = a, j = b;
      int x = arr[(i+j) / 2];         
      do {                            
        while (arr[i] < x) i++;       
        while (arr[j] > x) j--;       
        if (i <= j) {
          swap(arr, i, j);
          i++; j--;
        }                             
      } while (i <= j); 
      qsort(arr, a, j); 
      qsort(arr, i, b); 
    }
  }

  // Swap arr[s] and arr[t]
  private static void swap(int[] arr, int s, int t) {
    int tmp = arr[s];  arr[s] = arr[t];  arr[t] = tmp;
  }

  // ----------------------------------------------------------------------
  // Version B: Single-queue single-thread setup; sequential quicksort using queue

  private static void singleQueueSingleThread() {
    SimpleDeque<SortTask> queue = new SimpleDeque<SortTask>(100000);
    int[] arr = IntArrayUtil.randomIntArray(size);
    queue.push(new SortTask(arr, 0, arr.length-1));
    sqstWorker(queue);
    //System.out.println(IntArrayUtil.isSorted(arr));
  }

  private static void sqstWorker(Deque<SortTask> queue) {
    Timer timer = new Timer();
    SortTask task;
    while (null != (task = queue.pop())) {
      final int[] arr = task.arr;
      final int a = task.a, b = task.b;
      if (a < b) { 
        int i = a, j = b;
        int x = arr[(i+j) / 2];         
        do {                            
          while (arr[i] < x) i++;       
          while (arr[j] > x) j--;       
          if (i <= j) {
            swap(arr, i, j);
            i++; j--;
          }                             
        } while (i <= j); 
        queue.push(new SortTask(arr, a, j)); 
        queue.push(new SortTask(arr, i, b));               
      }
    }
    System.out.println("1 thread:\t" + "Time:\t" + timer.check());
  }

  // ---------------------------------------------------------------------- 
  // Version C: Single-queue multi-thread setup 

  private static void singleQueueMultiThread(final int threadCount) {
    int[] arr = IntArrayUtil.randomIntArray(size);
    SimpleDeque<SortTask> queue = new SimpleDeque<SortTask>(100000);
    queue.push(new SortTask(arr, 0, arr.length-1));
    sqmtWorkers(queue, threadCount);
    //System.out.println(IntArrayUtil.isSorted(arr));
  }

  private static void sqmtWorkers(Deque<SortTask> queue, int threadCount) {
    final Thread[] threads = new Thread[threadCount];
    final CyclicBarrier startBarrier = new CyclicBarrier(threadCount + 1), 
      stopBarrier = startBarrier;
    final LongAdder amt = new LongAdder(); amt.increment();
    for(int t = 0; t<threadCount; t++)
    {
      threads[t] = new Thread(() ->
      {
        try { startBarrier.await(); } catch (Exception exn) { }
        SortTask task;
        while (null != (task = getTask(queue, amt))) {
          amt.decrement();
          final int[] arr = task.arr;
          final int a = task.a, b = task.b;
          if (a < b) { 
            int i = a, j = b;
            int x = arr[(i+j) / 2];
            do {
              while (arr[i] < x) i++;
              while (arr[j] > x) j--;
              if (i <= j) {
                swap(arr, i, j);
                i++; j--;
              }                             
            } while (i <= j); 
            queue.push(new SortTask(arr, a, j));
            amt.increment();
            queue.push(new SortTask(arr, i, b));
            amt.increment();
          }
        }
        try { stopBarrier.await(); } catch (Exception exn) { }
      });
      threads[t].start();
    }
    try { startBarrier.await(); } catch (Exception exn) { }
    Timer timer = new Timer();
    try { stopBarrier.await(); } catch (Exception exn) { }
    System.out.println(threadCount + " threads:\t" + "Time:\t" + timer.check());
  }

  // Tries to get a sorting task.  If task queue is empty but some
  // tasks are not yet processed, yield and then try again.
  private static SortTask getTask(final Deque<SortTask> queue, LongAdder ongoing) {
    SortTask task;
    while (null == (task = queue.pop())) {
      if (ongoing.longValue() > 0) 
        Thread.yield();
      else 
        return null;
    }
    return task;
  }


  // ----------------------------------------------------------------------
  // Version D: Multi-queue multi-thread setup, thread-local queues

  private static void multiQueueMultiThread(final int threadCount) {
    int[] arr = IntArrayUtil.randomIntArray(size);
    // To do: ... create queues and so on, then call mqmtWorkers(queues, threadCount)
    SimpleDeque<SortTask>[] queues = new SimpleDeque[threadCount];
    for(int i = 0; i<threadCount; i++)
      queues[i] = new SimpleDeque<SortTask>(100000);
    queues[0].push(new SortTask(arr, 0, arr.length-1));
    mqmtWorkers(queues, threadCount);
    //System.out.println(IntArrayUtil.isSorted(arr));
  }

  // Version E: Multi-queue multi-thread setup, thread-local queues

  private static void multiQueueMultiThreadCL(final int threadCount) {
    int[] arr = IntArrayUtil.randomIntArray(size);
    Deque<SortTask>[] queues = new ChaseLevDeque[threadCount];
    for(int i = 0; i<threadCount; i++)
      queues[i] = new ChaseLevDeque<SortTask>(100000);
    queues[0].push(new SortTask(arr, 0, arr.length-1));
    mqmtWorkers(queues, threadCount);
    //System.out.println(IntArrayUtil.isSorted(arr));
  }

  private static void mqmtWorkers(Deque<SortTask>[] queues, int threadCount) {
    final Thread[] threads = new Thread[threadCount];
    final CyclicBarrier startBarrier = new CyclicBarrier(threadCount + 1), 
      stopBarrier = startBarrier;
    final LongAdder amt = new LongAdder(); amt.increment();
    for(int t = 0; t<threadCount; t++)
    {
      final int myNumber = t;
      threads[t] = new Thread(() ->
      {
        try { startBarrier.await(); } catch (Exception exn) { }
        SortTask task;
        while (null != (task = getTask(myNumber, queues, amt))) {
          amt.decrement();
          final int[] arr = task.arr;
          final int a = task.a, b = task.b;
          if (a < b) { 
            int i = a, j = b;
            int x = arr[(i+j) / 2];
            do {
              while (arr[i] < x) i++;
              while (arr[j] > x) j--;
              if (i <= j) {
                swap(arr, i, j);
                i++; j--;
              }                             
            } while (i <= j); 
            queues[myNumber].push(new SortTask(arr, a, j));
            amt.increment();
            queues[myNumber].push(new SortTask(arr, i, b));
            amt.increment();
          }
        }
        try { stopBarrier.await(); } catch (Exception exn) { }
      });
      threads[t].start();
    }
    try { startBarrier.await(); } catch (Exception exn) { }
    Timer timer = new Timer();
    try { stopBarrier.await(); } catch (Exception exn) { }
    System.out.println(threadCount + " threads:\t" + "Time:\t" + timer.check());
  }

  // Tries to get a sorting task.  If task queue is empty, repeatedly
  // try to steal, cyclically, from other threads and if that fails,
  // yield and then try again, while some sort tasks are not processed.

  private static SortTask getTask(final int myNumber, final Deque<SortTask>[] queues, 
                                  LongAdder ongoing) {
    final int threadCount = queues.length;
    SortTask task = queues[myNumber].pop();
    if (null != task) 
      return task;
    else {
      do {
        for(int i = 0; i<queues.length; i++)
        {
          if(i != myNumber)
          {
            SortTask element = queues[i].steal();
            if(element != null)
              return element;
          }
        }
        Thread.yield();
      } while (ongoing.longValue() > 0);
      return null;
    }
  }
}

// ----------------------------------------------------------------------
// SortTask class, Deque<T> interface, SimpleDeque<T> 

// Represents the task of sorting arr[a..b]
class SortTask {
  public final int[] arr;
  public final int a, b;

  public SortTask(int[] arr, int a, int b) {
    this.arr = arr; 
    this.a = a;
    this.b = b;
  }
}

interface Deque<T> {
  void push(T item);    // at bottom
  T pop();              // from bottom
  T steal();            // from top
}

class SimpleDequeTests {
  public static void runAllTests()
  {
    runSequentialTest(new SimpleDeque<Integer>(5), 5);
    runConcurrencyTest(new SimpleDeque<Integer>(1_000_000), 1_000_000, 8);
    runConcurrencyChase(new SimpleDeque<Integer>(1_000_000), 1_000_000, 8);
    //runSequentialTest(new ChaseLevDeque<Integer>(5), 5);
    //runConcurrencyChase(new ChaseLevDeque<Integer>(1_000_000), 1_000_000, 8);
  }

  private static void runSequentialTest(Deque<Integer> queue, int size)
  {
    queue.push(1);
    assert queue.pop() == 1;
    assert queue.pop() == null;
    queue.push(2);
    queue.push(3);
    assert queue.pop() == 3;
    assert queue.pop() == 2;
    assert queue.pop() == null;


    queue.push(1);
    assert queue.steal() == 1;
    assert queue.steal() == null;
    queue.push(2);
    queue.push(3);
    assert queue.steal() == 2;
    assert queue.steal() == 3;
    assert queue.steal() == null;

    queue.push(1);
    queue.push(2);
    queue.push(3);
    queue.push(4);
    assert queue.steal() == 1;
    assert queue.pop() == 4;
    assert queue.pop() == 3;
    assert queue.steal() == 2;
    assert queue.pop() == null;
    assert queue.steal() == null;

    for(int i = 0; i < size; i++)
    {
      queue.push(i);
    }
    try{
      queue.push(42);
      // this should fail. therefore the assert should not.
      assert false;
    } catch(RuntimeException e)
    {
      
    }
  }

  private static void runConcurrencyTest(final Deque<Integer> queue, int size, int threadCount)
  {
    final Thread[] threads = new Thread[threadCount];
    final int numberOfElements = size/threadCount-1;
    final LongAdder expectedSum = new LongAdder();
    final LongAdder sum = new LongAdder();
    final CyclicBarrier startBarrier = new CyclicBarrier(threadCount + 1), 
      stopBarrier = startBarrier;
    for(int t = 0; t<threadCount; t++)
    {
      threads[t] = new Thread(() -> {
        final Random r = new Random(1);
        int amtAdded = 0, amtRemoved = 0, foundSum = 0, pushedSum = 0;
        try { startBarrier.await(); } catch (Exception exn) { }
        while(amtAdded != numberOfElements || amtRemoved != numberOfElements)
        {
          int testCase = r.nextInt(4); // double as many pushes as steal and pop independently
          switch(testCase)
          {
            case 0: {
              if(amtRemoved != numberOfElements)
              {
                Integer result = queue.steal();
                if(result != null)
                {
                  foundSum += result;
                  amtRemoved++;
                }
                break;
              }
            }
            case 1: {
              if(amtRemoved != numberOfElements)
              {
                Integer result = queue.pop();
                if(result != null)
                {
                  foundSum += result;
                  amtRemoved++;
                }
                break;
              }
            }
            default: { // push stuff
              if(amtAdded != numberOfElements)
              {
                int value = r.nextInt(1000);
                queue.push(value);
                pushedSum += value;
                amtAdded++;
                break;
              }
            }
          }
        }
        expectedSum.add(pushedSum);
        sum.add(foundSum);
        try { stopBarrier.await(); } catch (Exception exn) { }
      });
      threads[t].start();
    }
    try { startBarrier.await(); } catch (Exception exn) { }
    try { stopBarrier.await(); } catch (Exception exn) { }
    

    assert queue.pop() == null;
    assert (expectedSum.sum() == sum.sum());
  }

  private static void runConcurrencyChase(final Deque<Integer> queue, final int size, int threadCount)
  {
    final Thread[] threads = new Thread[threadCount];
    final int numberOfElements = size/threadCount-1;
    final LongAdder expectedSum = new LongAdder();
    final LongAdder sum = new LongAdder();
    final CyclicBarrier startBarrier = new CyclicBarrier(threadCount + 1), 
      stopBarrier = startBarrier;
    threads[0] = new Thread(() -> {
      final Random r = new Random(1);
      int amtAdded = 0, amtRemoved = 0, foundSum = 0, pushedSum = 0;
      try { startBarrier.await(); } catch (Exception exn) { }
      while(amtAdded != size || amtRemoved != numberOfElements)
      {
        int testCase = r.nextInt(2); // double as many pushes as steal and pop independently
        switch(testCase)
        {
          case 0: {
            if(amtRemoved != numberOfElements)
            {
              Integer result = queue.pop();
              if(result != null)
              {
                foundSum += result;
                amtRemoved++;
              }
              break;
            }
          }
          default: { // push stuff
            if(amtAdded != numberOfElements)
            {
              int value = r.nextInt(1000);
              queue.push(value);
              pushedSum += value;
              amtAdded++;
              break;
            }
          }
        }
      }
      expectedSum.add(pushedSum);
      sum.add(foundSum);
      try { stopBarrier.await(); } catch (Exception exn) { }
    });
    for(int t = 1; t<threadCount; t++)
    {
      threads[t] = new Thread(() -> {
        int amtRemoved = 0, foundSum = 0;
        try { startBarrier.await(); } catch (Exception exn) { }
        while(amtRemoved != numberOfElements)
        {
          if(amtRemoved != numberOfElements)
          {
            Integer result = queue.steal();
            if(result != null)
            {
              foundSum += result;
              amtRemoved++;
            }
            break;
          }
        }
        sum.add(foundSum);
        try { stopBarrier.await(); } catch (Exception exn) { }
      });
      threads[t].start();
    }
    try { startBarrier.await(); } catch (Exception exn) { }
    try { stopBarrier.await(); } catch (Exception exn) { }
    

    assert queue.pop() == null;
    assert (expectedSum.sum() == sum.sum());
  }
}

class SimpleDeque<T> implements Deque<T> {
  // The queue's items are in items[top%S...(bottom-1)%S], where S ==
  // items.length; items[bottom%S] is where the next push will happen;
  // items[(bottom-1)%S] is where the next pop will happen;
  // items[top%S] is where the next steal will happen; the queue is
  // empty if top == bottom, non-empty if top < bottom, and full if
  // bottom - top == items.length.  The top field can only increase.
  private long bottom = 0, top = 0;
  private final T[] items;

  public SimpleDeque(int size) {
    this.items = makeArray(size);
  }

  @SuppressWarnings("unchecked") 
  private static <T> T[] makeArray(int size) {
    // Java's @$#@?!! type system requires this unsafe cast    
    return (T[])new Object[size];
  }

  private static int index(long i, int n) {
    return (int)(i % (long)n);
  }

  private Object bottomLock = new Object();
  private Object topLock = new Object();
  public void push(T item) { // at bottom
    synchronized(bottomLock) {
      final long size = bottom - top;
      if (size == items.length) 
        throw new RuntimeException("queue overflow");
      items[index(bottom++, items.length)] = item;
    }
  }

  
  public T pop() { // from bottom
    synchronized(bottomLock) {
        final long afterSize = bottom - 1 - top;
        if (afterSize < 0) 
          return null;
        else
          return items[index(--bottom, items.length)];
    }
  }

  
  public T steal() { // from top
    synchronized(bottomLock) {
      final long size = bottom - top;
      if (size <= 0) 
        return null;
      else
        return items[index(top++, items.length)];
    }
  }
}

// ----------------------------------------------------------------------

// A lock-free queue simplified from Chase and Lev: Dynamic circular
// work-stealing queue, SPAA 2005.  We simplify it by not reallocating
// the array; hence this queue may overflow.  This is close in spirit
// to the original ABP work-stealing queue (Arora, Blumofe, Plaxton:
// Thread scheduling for multiprogrammed multiprocessors, 2000,
// section 3) but in that paper an "age" tag needs to be added to the
// top pointer to avoid the ABA problem (see ABP section 3.3).  This
// is not necessary in the Chase-Lev dequeue design, where the top
// index never assumes the same value twice.

// PSEUDOCODE for ChaseLevDeque class:


class ChaseLevDeque<T> implements Deque<T> {
  volatile long bottom = 0;
  final AtomicLong top = new AtomicLong();
  final T[] items;

  public ChaseLevDeque(int size) {
    this.items = makeArray(size);
  }

  @SuppressWarnings("unchecked") 
  private static <T> T[] makeArray(int size) {
    // Java's @$#@?!! type system requires this unsafe cast    
    return (T[])new Object[size];
  }

  private static int index(long i, int n) {
    return (int)(i % (long)n);
  }

  public void push(T item) { // at bottom
    final long b = bottom, t = top.get(), size = b - t;
    if (size == items.length) 
      throw new RuntimeException("queue overflow");
    items[index(b, items.length)] = item;
    bottom = b+1;
  }

  public T pop() { // from bottom
    final long b = bottom - 1;
    bottom = b;
    final long t = top.get(), afterSize = b - t;
    if (afterSize < 0) { // empty before call
      bottom = t;
      return null;
    } else {
      T result = items[index(b, items.length)];
      if (afterSize > 0) // non-empty after call
        return result;
      else {		// became empty, update both top and bottom
        if (!top.compareAndSet(t, t+1)) // somebody stole result
          result = null;
        bottom = t+1;
        return result;
      }
    }
  }

  public T steal() { // from top
    final long t = top.get(), b = bottom, size = b - t;
    if (size <= 0)
      return null;
    else {
      T result = items[index(t, items.length)];
      if (top.compareAndSet(t, t+1))
        return result;
      else 
        return null;
    }
  }
}


// ----------------------------------------------------------------------

class IntArrayUtil {
  public static int[] randomIntArray(final int n) {
    int[] arr = new int[n];
    for (int i = 0; i < n; i++)
      arr[i] = (int) (Math.random() * n * 2);
    return arr;
  }

  public static void printout(final int[] arr, final int n) {
    for (int i=0; i < n; i++)
      System.out.print(arr[i] + " ");
    System.out.println("");
  }

  public static boolean isSorted(final int[] arr) {
    for (int i=1; i<arr.length; i++)
      if (arr[i-1] > arr[i])
        return false;
    return true;
  }
}

class Timer {
    private long start, spent = 0;
    public Timer() { play(); }
    public double check() { return (System.nanoTime()-start+spent)/1e9; }
    public void pause() { spent += System.nanoTime()-start; }
    public void play() { start = System.nanoTime(); }
}