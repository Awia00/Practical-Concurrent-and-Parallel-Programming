package msqueue;// sestoft@itu.dk * 2016-11-18, 2017-01-08

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Random;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.concurrent.Executors.newCachedThreadPool;

public class TestMSQueueNeater extends Tests {
  public static void main(String[] args) {
    testSequentialIntegerCorrectness(new MSQueueNeater<>());
    testSequentialStringCorrectness(new MSQueueNeater<>());
    testConcurrencyCorrectness(1_000_000, 10, 10);
  }

  /**
   * The queue most be empty to begin with
   * @param queue
   */
  private static void testSequentialIntegerCorrectness(final UnboundedQueue<Integer> queue)
  {
    System.out.println("Starting sequential integer test");
    assert queue.dequeue() == null;
    queue.enqueue(1);
    assert queue.dequeue() == 1;
    assert queue.dequeue() == null;
    assert queue.dequeue() == null;
    queue.enqueue(1);
    queue.enqueue(2);
    queue.enqueue(3);
    assert queue.dequeue() == 1;
    assert queue.dequeue() == 2;
    assert queue.dequeue() == 3;
    assert queue.dequeue() == null;
    assert queue.dequeue() == null;
    queue.enqueue(1);
    queue.enqueue(2);
    queue.enqueue(3);
    assert queue.dequeue() == 1;
    queue.enqueue(4);
    queue.enqueue(5);
    assert queue.dequeue() == 2;
    assert queue.dequeue() == 3;
    assert queue.dequeue() == 4;
    queue.enqueue(6);
    assert queue.dequeue() == 5;
    assert queue.dequeue() == 6;
    assert queue.dequeue() == null;
    assert queue.dequeue() == null;


    // a random stress test
    Random r = new Random();
    int total = 100_000, amtAdded = 0, amtRemoved = 0;
    while(amtAdded != total || amtRemoved != total)
    {
      if(r.nextBoolean() && amtAdded != total)
      {
        queue.enqueue(amtAdded);
        amtAdded++;
      }
      else
      {
        if(amtRemoved != total && amtRemoved < amtAdded)
        {
          assert queue.dequeue() == amtRemoved;
          amtRemoved++;
        }
      }
    }
    assert queue.dequeue() == null;
    System.out.println(" - Sequential integer test success");
  }
  private static void testSequentialStringCorrectness(final UnboundedQueue<String> queue)
  {
    System.out.println("Starting sequential string test");
    assert queue.dequeue() == null;
    queue.enqueue("1");
    assert queue.dequeue().equals("1");
    assert queue.dequeue() == null;
    assert queue.dequeue() == null;
    queue.enqueue("1");
    queue.enqueue("2");
    queue.enqueue("3");
    assert queue.dequeue().equals("1");
    assert queue.dequeue().equals("2");
    assert queue.dequeue().equals("3");
    assert queue.dequeue() == null;
    assert queue.dequeue() == null;
    queue.enqueue("1");
    queue.enqueue("2");
    queue.enqueue("3");
    assert queue.dequeue().equals("1");
    queue.enqueue("4");
    queue.enqueue("5");
    assert queue.dequeue().equals("2");
    assert queue.dequeue().equals("3");
    assert queue.dequeue().equals("4");
    queue.enqueue("6");
    assert queue.dequeue().equals("5");
    assert queue.dequeue().equals("6");
    assert queue.dequeue() == null;
    assert queue.dequeue() == null;
    System.out.println(" - sequential string test success");
  }

  private static void testConcurrencyCorrectness(final int size, final int producers, final int consumers)
  {
    final Tests test1 = new UnboundedQueueManyToManyConcurrencyTest(size, producers, consumers, new MSQueueNeater<>());
    test1.runTest(newCachedThreadPool());
    final Tests test2 = new UnboundedQueueOneToOneConcurrencyTest(size, new MSQueueNeater<>());
    test2.runTest(newCachedThreadPool());
  }

}

class Tests {
  public static void assertEquals(int x, int y) throws Exception {
    if (x != y) 
      throw new Exception(String.format("ERROR: %d not equal to %d%n", x, y));
  }

  public static void assertTrue(boolean b) throws Exception {
    if (!b) 
      throw new Exception(String.format("ERROR: assertTrue"));
  }

  void runTest(ExecutorService pool) {
    throw new NotImplementedException();
  }
}
class ThreadAndNum {
  public final int threadNumber;
  public final int number;

  ThreadAndNum(int threadNumber, int number) {
    this.threadNumber = threadNumber;
    this.number = number;
  }
}
class UnboundedQueueManyToManyConcurrencyTest extends Tests {
  protected final CyclicBarrier startBarrier, stopBarrier;
  protected final UnboundedQueue<ThreadAndNum> queue;
  protected final AtomicInteger enquedSum = new AtomicInteger(0);
  protected final AtomicInteger dequedSum = new AtomicInteger(0);
  protected final int size;
  protected final int producers;
  protected final int consumers;

  public UnboundedQueueManyToManyConcurrencyTest(final int size, final int producers, final int consumers, UnboundedQueue<ThreadAndNum> queue)
  {
    this.size = size;
    this.producers = producers;
    this.consumers = consumers;
    this.queue = queue;
    this.startBarrier = new CyclicBarrier((producers+consumers) + 1);
    this.stopBarrier = new CyclicBarrier((producers+consumers) + 1);
  }

  @Override
  public void runTest(ExecutorService pool) {
    try {
      for(int i = 0; i < producers; i++)
      {
        pool.execute(new Producer(i));
      }
      for(int i = 0; i < consumers; i++)
      {
        pool.execute(new Consumer(i));
      }

      System.out.println("Starting many-to-many test");
      startBarrier.await();
      stopBarrier.await();


      assertTrue(queue.dequeue() == null);
      assertEquals(enquedSum.get(), dequedSum.get());
      System.out.println(" - many-to-many test success");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    pool.shutdown();
  }

  class Producer implements Runnable
  {
    final int threadNumber;

    Producer(int threadNumber) {
      this.threadNumber = threadNumber;
    }

    @Override
    public void run() {
      try {
        final Random r = new Random();
        int sum = 0;
        startBarrier.await();
        for(int i = 0; i<size;)
        {
            if(r.nextBoolean()) {
                queue.enqueue(new ThreadAndNum(threadNumber, i+1));
                sum += i+1;
                i++;
            }
        }
        enquedSum.getAndAdd(sum);
        stopBarrier.await();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  class Consumer implements Runnable
  {
    final int threadNumber;


    Consumer(int threadNumber) {
      this.threadNumber = threadNumber;
    }
    @Override
    public void run() {
      try {
        final int[] producerLastNumber = new int[producers];;
        int sum = 0;
        startBarrier.await();
        for(int i = 0; i<size; )
        {
          ThreadAndNum item = queue.dequeue();
          if(item != null)
          {
            assert producerLastNumber[item.threadNumber] < item.number;
            producerLastNumber[item.threadNumber] = item.number;
            sum += item.number;
            i++;
          }
        }
        dequedSum.getAndAdd(sum);
        stopBarrier.await();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}

class UnboundedQueueOneToOneConcurrencyTest extends Tests {
  protected final CyclicBarrier startBarrier, stopBarrier;
  protected final UnboundedQueue<Integer> queue;
  protected final AtomicInteger enqueuedSum = new AtomicInteger(0);
  protected final AtomicInteger dequeuedSum = new AtomicInteger(0);
  protected final int size;

  public UnboundedQueueOneToOneConcurrencyTest(final int size, UnboundedQueue<Integer> queue)
  {
    this.size = size;
    this.queue = queue;
    this.startBarrier = new CyclicBarrier(3);
    this.stopBarrier = new CyclicBarrier(3);
  }

  @Override
  public void runTest(ExecutorService pool) {
    try {
      pool.execute(new Producer());
      pool.execute(new Consumer());

      startBarrier.await();
      System.out.println("Starting one-to-one test");
      stopBarrier.await();


      assertTrue(queue.dequeue() == null);
      assertEquals(enqueuedSum.get(), dequeuedSum.get());
      System.out.println(" - one-to-one test success");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    pool.shutdown();
  }

  class Producer implements Runnable
  {
    @Override
    public void run() {
      try {
        int sum = 0;
        startBarrier.await();
        for(int i = 0; i<size; i++)
        {
          queue.enqueue(i);
          sum += i;
        }
        enqueuedSum.getAndAdd(sum);
        stopBarrier.await();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  class Consumer implements Runnable
  {

    @Override
    public void run() {
      try {
        int sum = 0;
        startBarrier.await();
        for(int i = 0; i<size; )
        {
          Integer item = queue.dequeue();
          if(item != null)
          {
            assert i == item;
            sum += item;
            i++;
          }
        }
        dequeuedSum.getAndAdd(sum);
        stopBarrier.await();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}

interface UnboundedQueue<T> {
  void enqueue(T item);
  T dequeue();
}

// Unbounded non-blocking list-based lock-free queue by Michael and
// Scott 1996.  This version inspired by suggestions from Niels
// Abildgaard Roesen.

class MSQueueNeater<T> implements UnboundedQueue<T> {
  private final AtomicReference<Node<T>> head, tail;

  public MSQueueNeater() {
    Node<T> dummy = new Node<T>(null, null);
    head = new AtomicReference<Node<T>>(dummy);
    tail = new AtomicReference<Node<T>>(dummy);
  }

  public void enqueue(T item) { // at tail
    Node<T> node = new Node<T>(item, null);
    while (true) { // MODIFICATION 1:
      final Node<T> last = tail.get(), next = last.next.get();
      if (next != null) {
        //tail.compareAndSet(last, null); // MODIFICATION 2:
        tail.set(next); // MODIFICATION 3:
      } else {// MODIFICATION 3:
        last.next.set(node);// MODIFICATION 3:
        tail.set(node);// MODIFICATION 3:
        return;
      }
    }
  }

  public T dequeue() { // from head
    while (true) {
      final Node<T> first = head.get(), last = tail.get(), next = first.next.get();
      if (next == null) {
        return null;
      } else if (first == last) {
        //tail.compareAndSet(last, null); // MODIFICATION 2:
        tail.set(next); // MODIFICATION 3:
      }
      else {// MODIFICATION 3:
        head.set( next);// MODIFICATION 3:
        return next.item;
      }
    }
  }

  private static class Node<T> {
    final T item;
    final AtomicReference<Node<T>> next;

    public Node(T item, Node<T> next) {
      this.item = item;
      this.next = new AtomicReference<Node<T>>(next);
    }
  }
}
