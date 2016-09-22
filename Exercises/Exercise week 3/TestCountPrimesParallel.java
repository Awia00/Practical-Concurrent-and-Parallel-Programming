
import java.util.Arrays;
import java.util.stream.*;

// Exercise 3.2 both 1 and 2)
public class TestCountPrimesParallel {
  public static void main(String[] args) {
    final int N = 10_000_000;
    final int[] a = IntStream.rangeClosed(1, N).toArray();
    Arrays.parallelSetAll(a, (x) -> isPrime(x)?1:0);
    Arrays.parallelPrefix(a, (x,y) -> x+y);
    System.out.println(a[N-1]);
    for(int i = N/10-1; i<N; i+=N/10)
    {
      System.out.println(a[i]/(i/ Math.log(i)));
    }
  }

  private static boolean isPrime(int n) {
    int k = 2;
    while (k * k <= n && n % k != 0)
      k++;
    return n >= 2 && k * k > n;
  }
}
