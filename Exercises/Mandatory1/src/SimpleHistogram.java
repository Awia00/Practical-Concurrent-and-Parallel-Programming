// For week 2
// sestoft@itu.dk * 2014-09-04

import java.util.concurrent.atomic.*;

class SimpleHistogram {
  
  public static void main(String[] args) {
    final Histogram histogram = new Histogram5(5_000_000);
    countPrimes(histogram);
    dump(histogram);
  }

  public static void countPrimes(Histogram histogram)
  {
    final int threadCount = 10;
    final Thread[] threads = new Thread[threadCount];
    final int range = 5_000_000/threadCount;
    for(int i = 0; i<threadCount; i++)
    {
      final int newStart = i*range;
      threads[i] = new Thread(()->{
        for(int j = newStart; j < newStart+range; j++)
        {
          int factors = countFactors(j);
          histogram.increment(factors);
        }
      });
      threads[i].start();
    }

    try {
      for (int t=0; t<threadCount; t++) 
        threads[t].join();
    } catch (InterruptedException exn) { }
  }

  public static void dump(Histogram histogram) {
    int totalCount = 0;
    for (int bin=0; bin<histogram.getSpan(); bin++) {
      int count = histogram.getCount(bin);
      if(count != 0)
      {
        System.out.printf("%4d: %9d%n", bin, count);
      }
      totalCount += histogram.getCount(bin);
    }
    System.out.printf("      %9d%n", totalCount);
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


interface Histogram {
  public void increment(int bin);
  public int getCount(int bin);
  public int getSpan();
  public int[] getBins();
}

class Histogram1 implements Histogram {
  private int[] counts;
  public Histogram1(int span) {
    this.counts = new int[span];
  }
  public void increment(int bin) {
    counts[bin] = counts[bin] + 1;
  }
  public int getCount(int bin) {
    return counts[bin];
  }
  public int getSpan() {
    return counts.length;
  }
  public int[] getBins() {
    return counts.clone();
  }
}

class Histogram2 implements Histogram {
  private final int[] counts;
  public Histogram2(int span) {
    this.counts = new int[span];
  }
  public synchronized void increment(int bin) {
    counts[bin] = counts[bin] + 1;
  }
  public synchronized int getCount(int bin) {
    return counts[bin];
  }
  public int getSpan() {
    return counts.length;
  }

  public int[] getBins() {
    return counts.clone();
  }
}


class Histogram3 implements Histogram {
  private final AtomicInteger[] counts;
  public Histogram3(int span) {
    this.counts = new AtomicInteger[span];
    for(int i = 0; i<span; i++)
    {
      counts[i] = new AtomicInteger();
    }
  }
  public void  increment(int bin) {
    counts[bin].getAndIncrement();
  }
  public int getCount(int bin) {
    return counts[bin].get();
  }
  public int getSpan() {
    return counts.length;
  }

  public int[] getBins() {
    int[] values = new int[counts.length];
    for(int i = 0; i< counts.length; i++)
    {
      values[i] = counts[i].intValue();
    }
    return values;
  }
}


class Histogram4 implements Histogram {
  private final AtomicIntegerArray counts;
  public Histogram4(int span) {
    this.counts = new AtomicIntegerArray(span);
  }
  public void increment(int bin) {
    counts.getAndIncrement(bin);
  }
  public int getCount(int bin) {
    return counts.get(bin);
  }
  public int getSpan() {
    return counts.length();
  }
  public int[] getBins() {
    int[] values = new int[counts.length()];
    for(int i = 0; i< counts.length(); i++)
    {
      values[i] = counts.get(i);
    }
    return values;
  }
}


class Histogram5 implements Histogram {
  private final LongAdder[] counts;
  public Histogram5(int span) {
    this.counts = new LongAdder[span];
    for(int i = 0; i<span; i++)
    {
      counts[i] = new LongAdder();
    }
  }
  public void  increment(int bin) {
    counts[bin].increment();
  }
  public int getCount(int bin) {
    return counts[bin].intValue();
  }
  public int getSpan() {
    return counts.length;
  }

  public int[] getBins() {
    int[] values = new int[counts.length];
    for(int i = 0; i< counts.length; i++)
    {
      values[i] = counts[i].intValue();
    }
    return values;
  }
}