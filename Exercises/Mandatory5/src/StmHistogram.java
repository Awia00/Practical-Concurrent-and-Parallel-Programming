// For the Multiverse library:
import org.multiverse.api.references.*;
import org.multiverse.api.StmUtils;
import static org.multiverse.api.StmUtils.*;

// Multiverse locking:
import org.multiverse.api.LockMode;
import org.multiverse.api.Txn;
import org.multiverse.api.callables.TxnVoidCallable;

public class StmHistogram implements Histogram {
  private final TxnInteger[] counts;

  public StmHistogram(int span) {
    counts = new TxnInteger[span];
    for(int i = 0; i <span ; i++)
    {
      counts[i] = StmUtils.newTxnInteger(0);
    }
  }

  public void increment(int bin) {
    atomic(() -> counts[bin].increment());
  }

  public int getCount(int bin) {
    return atomic(() -> { return counts[bin].get(); });
  }

  public int getSpan() {
    return counts.length;
  }

  public int[] getBins() {
    final int[] arr = new int[getSpan()];
    for(int i = 0; i<getSpan(); i++)
    {
      final int bin = i;
      atomic(()->arr[bin] = counts[bin].get());
    }
    return arr;
  }

  public int getAndClear(int bin) {
    return counts[bin].getAndSet(0);
  }

  public void transferBins(Histogram hist) {
    for(int i = 0; i<getSpan(); i++)
    {
      final int bin = i;
      atomic(() -> counts[bin].set(hist.getAndClear(bin)));
    }
  }
}

