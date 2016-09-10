// For week 2
// sestoft@itu.dk * 2014-08-29
import java.util.concurrent.atomic.*;

class TestCountFactors {
  
  public static void main(String[] args) {
    final int threadCount = 10;
    Thread[] threads = new Thread[threadCount];

    final int range = 5_000_000/threadCount;

    AtomicInteger count = new AtomicInteger();
    //MyAtomicInteger count = new MyAtomicInteger();

    for(int i = 0; i < threadCount; i++)
    {
      final int newStart = i*range;
      threads[i] = new Thread(()->{
        for (int p = newStart; p < newStart+range; p++)
          count.addAndGet(countFactors(p));
      });
      threads[i].start();
    }

    try {
      for (int t=0; t<threadCount; t++) 
        threads[t].join();
    } catch (InterruptedException exn) { }
    System.out.printf("Total number of factors is %9d%n", count.get());
  }

  public static int countFactors(int p) {
    if (p < 2) 
      return 0;
    int factorCount = 1, k = 2;
    while (p >= k * k) {
      if (p % k == 0) {
	factorCount++;
	p /= k;
      } else 
	k++;
    }
    return factorCount;
  }
}
