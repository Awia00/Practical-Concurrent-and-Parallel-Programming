import java.util.concurrent.atomic.*;

/*
*
*/
public class CasHistogram implements Histogram
{
    private AtomicReference<Integer>[] bins;
    public CasHistogram(int size)
    {
        bins = new AtomicReference[size];
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
        int newValue;
        do{
            oldValue = bins[bin].get();
            newValue = oldValue + 1;
        } while(!bins[bin].compareAndSet(oldValue, newValue));
        return oldValue;
    }
    
    public void transferBins(Histogram hist)
    {
        AtomicReference<Integer>[] newBins = new AtomicReference[hist.getSpan()];
        for(int i = 0; i < newBins.length; i++)
        {
            newBins[i].getAndSet(hist.getCount(i));
        }
        bins = newBins;
    }
}

interface Histogram {
    void increment(int bin);
    int getCount(int bin);
    int getSpan();
    int[] getBins();
    int getAndClear(int bin);
    void transferBins(Histogram hist);
}