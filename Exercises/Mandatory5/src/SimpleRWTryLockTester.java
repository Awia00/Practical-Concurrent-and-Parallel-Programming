
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.CyclicBarrier;

public class SimpleRWTryLockTester
{
  public static void main(String[] args)
  {
    sequentialTest(new SimpleRWTryLock());
    concurrencyTest(new SimpleRWTryLock(), 16, 1_000_000);
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

  public static void concurrencyTest(SimpleRWTryLock lock, final int threadCount, final int amt)
  {
    final CyclicBarrier startBarrier = new CyclicBarrier(threadCount + 1), 
      stopBarrier = startBarrier;
    final Thread[] threads = new Thread[threadCount];
    for (int t=0; t<threadCount; t++) {
        threads[t] = 
          new Thread(() -> { 
            try { startBarrier.await(); } catch (Exception exn) { }
            
            try {
              for(int i = 0; i<amt; )
              {
                if(lock.readerTryLock())
                {
                  // do some work
                  i++;
                  lock.readerUnlock();
                }
                if(lock.writerTryLock())
                {
                  // do some work
                  i++;
                  lock.writerUnlock();
                }
              }
            } catch (Exception exn) { 
              assert false; 
            }

            try { stopBarrier.await(); } catch (Exception exn) { }
          });
        threads[t].start();
    }
    try { startBarrier.await(); } catch (Exception exn) { }
    Timer timer = new Timer();
    try { stopBarrier.await(); } catch (Exception exn) { }
    System.out.println("\nTime: " + timer.check());
  }
}

class SimpleRWTryLock {
  private AtomicReference<Holders> holders;
  public SimpleRWTryLock()
  {
    holders = new AtomicReference(null);
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
    if(oldValue instanceof ReaderList )
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
      next = current;
    }

    public ReaderList remove(Thread t)
    {
      if(thread == t)
      {
        return next;
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

  }
}