import java.util.concurrent.atomic.*;

/*
*
*/
public class CasHistogram implements Histogram
{
    private final AtomicInteger[] bins;
    public CasHistogram(int span)
    {
        bins = new AtomicInteger[span];
        for(int i =0; i<span; i++)
        {
            bins[i] = new AtomicInteger(0);
        }
    }

    public void increment(int bin)
    {
        int oldValue;
        int newValue;
        do{
            oldValue = bins[bin].get();
            newValue = oldValue + 1;
        } while(!bins[bin].compareAndSet(oldValue, newValue));
    }

    public int getCount(int bin)
    {
        return bins[bin].get();
    }

    public int getSpan()
    {
        return bins.length;
    }

    // returns a clone of the bins
    public int[] getBins()
    {
        int arr[] = new int[getSpan()];
        for(int i = 0; i<getSpan();i++)
        {
            arr[i] = getCount(i);
        }
        return arr;
    }

    public int getAndClear(int bin)
    {
        int oldValue;
        do{
            oldValue = bins[bin].get();
        } while(!bins[bin].compareAndSet(oldValue, 0));
        return oldValue;
    }
    
    public void transferBins(Histogram hist)
    {
        int oldValue;
        for(int i = 0; i < hist.getSpan(); i++)
        {
            int newValue = hist.getAndClear(i);
            do{
                oldValue = bins[i].get();
            } while(!bins[i].compareAndSet(oldValue, newValue));
        }
    }
}