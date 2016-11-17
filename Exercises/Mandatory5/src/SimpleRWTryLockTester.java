
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CyclicBarrier;
import java.util.Random;

public class SimpleRWTryLockTester
{
  public static void main(String[] args)
  {
    sequentialTest(new SimpleRWTryLock());
    concurrencyTestReadAndWrite(new SimpleRWTryLock(), 2, 100_000);
    concurrencyTestWrite(new SimpleRWTryLock(), 2, 100_000);
  }

  public static void sequentialTest(SimpleRWTryLock lock)
  {
    // postive tests of combinations acquring and unlocking  
    assert lock.readerTryLock();
    lock.readerUnlock();
    assert lock.readerTryLock();
    assert lock.readerTryLock();
    lock.readerUnlock();
    lock.readerUnlock();
    assert lock.writerTryLock();
    lock.writerUnlock();
    assert lock.readerTryLock();
    lock.readerUnlock();
    assert lock.writerTryLock();
    lock.writerUnlock();
    assert lock.writerTryLock();
    lock.writerUnlock();

    // negative non exception throwing tests
    assert lock.readerTryLock();
    assert !lock.writerTryLock();
    assert !lock.writerTryLock();
    lock.readerUnlock();
    
    assert lock.writerTryLock();
    assert !lock.readerTryLock();
    assert !lock.readerTryLock();
    assert !lock.writerTryLock();
    lock.writerUnlock();

    try{
      assert lock.writerTryLock();
      lock.readerUnlock();
      
      assert false;
    }catch(Exception e)
    {
      lock.writerUnlock();
    }
    try{
      assert lock.readerTryLock();
      lock.writerUnlock();

      assert false;
    }catch(Exception e)
    {
      lock.readerUnlock();
    }

    try{
      lock.readerUnlock();
      
      assert false;
    }catch(Exception e)
    {
    }
    try{
      lock.writerUnlock();

      assert false;
    }catch(Exception e)
    {
    }
  }

  public static void concurrencyTestReadAndWrite(SimpleRWTryLock lock, final int threadCount, final int amt)
  {
    System.out.println("\nStarting concurrencyTestReadAndWrite. Threads: " + threadCount + " rounds: " + amt);
    final CyclicBarrier startBarrier = new CyclicBarrier(threadCount + 1), 
      stopBarrier = startBarrier;
    final Thread[] threads = new Thread[threadCount];
    AtomicInteger totalSum = new AtomicInteger(0);
    AtomicInteger readerSum = new AtomicInteger(0);
    AtomicInteger writerSum = new AtomicInteger(0);
    for (int t=0; t<threadCount; t++) {
        threads[t] = 
          new Thread(() -> { 
            // Random random = new Random();
            try { startBarrier.await(); } catch (Exception exn) { }
            int sum =0;
            int reads =0;
            int writes =0;
            try {
              
              for(int i = 0; i<amt; sum+=2)
              {
                // if(random.nextInt(3)==2)
                  if(lock.readerTryLock())
                  {
                  i++;
                  reads++;
                  Thread.yield();
                  lock.readerUnlock();
                  } //else {
                  if(lock.writerTryLock())
                  {
                    i++;
                    writes++;
                    Thread.yield();
                    lock.writerUnlock();
                  }
                // } 
              }
            } catch (Exception exn) {
              exn.printStackTrace();
              assert false; 
            }
            totalSum.getAndAdd(sum);
            readerSum.getAndAdd(reads);
            writerSum.getAndAdd(writes);
            try { stopBarrier.await(); } catch (Exception exn) { }
            
          });
        threads[t].start();
    }
    try { startBarrier.await(); } catch (Exception exn) { }
    Timer timer = new Timer();
    try { stopBarrier.await(); } catch (Exception exn) { }
    System.out.println("\nTime: " + timer.check());
    System.out.println("Total tries pr lock: " + totalSum.get()/(threadCount*amt));
    System.out.println("reads ratio: " + ((double)readerSum.get())/(threadCount*amt)*100);
    System.out.println("writes ratio: " + ((double)writerSum.get())/(threadCount*amt)*100);
  }

  public static void concurrencyTestWrite(SimpleRWTryLock lock, final int threadCount, final int amt)
  {
    System.out.println("\nStarting concurrencyTestWrite. Threads: " + threadCount + " rounds: " + amt);
    final CyclicBarrier startBarrier = new CyclicBarrier(threadCount + 1), 
      stopBarrier = startBarrier;
    final Thread[] threads = new Thread[threadCount];
    AtomicInteger totalSum = new AtomicInteger(0);
    for (int t=0; t<threadCount; t++) {
        threads[t] = 
          new Thread(() -> { 
            // Random random = new Random();
            try { startBarrier.await(); } catch (Exception exn) { }
            int sum =0;
            int reads =0;
            int writes =0;
            try {
              
              for(int i = 0; i<amt; sum+=2)
              {
                  if(lock.writerTryLock())
                  {
                    i++;
                    Thread.yield();
                    lock.writerUnlock();
                  }
              }
            } catch (Exception exn) {
              exn.printStackTrace();
              assert false; 
            }
            totalSum.getAndAdd(sum);
            try { stopBarrier.await(); } catch (Exception exn) { }
            
          });
        threads[t].start();
    }
    try { startBarrier.await(); } catch (Exception exn) { }
    Timer timer = new Timer();
    try { stopBarrier.await(); } catch (Exception exn) { }
    System.out.println("\nTime: " + timer.check());
    System.out.println("Total tries pr lock: " + totalSum.get()/(threadCount*amt));

  }
}

class SimpleRWTryLock {
  private AtomicReference<Holders> holders;
  public SimpleRWTryLock()
  {
    holders = new AtomicReference(null);
  }

  public void print()
  {
    holders.get().print();
  }
  
  public boolean readerTryLock() 
  {
    boolean result = false;
    final Thread current = Thread.currentThread();
    Holders oldValue = holders.get();
    
    ReaderList newValue;
    if(oldValue == null)
    {
      newValue = new ReaderList(current); 
    }
    else if(oldValue instanceof ReaderList)
    {
      newValue = new ReaderList(current, (ReaderList)oldValue);
    }
    else return false;

    return holders.compareAndSet(oldValue, newValue);
  }
  public void readerUnlock()
  {
    final Thread current = Thread.currentThread();
    Holders oldValue = holders.get();
    if(oldValue instanceof ReaderList && ((ReaderList)oldValue).contains(current))
    {
      ReaderList newValue = ((ReaderList)oldValue).remove(current);
      holders.compareAndSet(oldValue, newValue);
    }
    else throw new RuntimeException("readerUnlock");
  }
  public boolean writerTryLock() 
  {
    Holders oldValue = holders.get();
    final Thread current = Thread.currentThread();
    if(oldValue == null)
    {
      Writer newValue = new Writer(current);
      return holders.compareAndSet(oldValue, newValue);
    }
    else return false;
  }
  public void writerUnlock() 
  {
    final Thread current = Thread.currentThread();
    Holders oldValue = holders.get();
    if(oldValue instanceof Writer && oldValue.isSame(current))
    {
      holders.compareAndSet(oldValue, null);
    }
    else throw new RuntimeException("writerUnlock");
  }

  private static abstract class Holders {
    protected final Thread thread;
    public Holders(Thread t)
    {
      thread = t;
    }
    public boolean isSame(Thread t)
    {
      return t==thread;
    }
    abstract void print();
   }

  private static class ReaderList extends Holders {
    private final ReaderList next;
    public ReaderList(Thread t)
    {
      super(t);
      next = null;
    }
    public ReaderList(Thread t, ReaderList current)
    {
      super(t);
      if(current != null)
        next = new ReaderList(current.thread, current.next);
      else
        next = null;
    }
    public void print()
    {
      System.out.println(thread + "");
      next.print();
    }

    public boolean contains(Thread t)
    {
      if(thread == t)
      {
        return true;
      }
      else if(next == null)
      {
        return false;
      }
      else{
        return next.contains(t);
      }
    }

    public ReaderList remove(Thread t)
    {
      if(thread == t)
      {
        return next;
      }
      else if(next == null)
      {
        return this;
      }
      else{
        return new ReaderList(thread, next.remove(thread));
      }
    }
  }
  private static class Writer extends Holders {
    public Writer(Thread t){
      super(t);
    }
    public void print()
    {

    }

  }
}