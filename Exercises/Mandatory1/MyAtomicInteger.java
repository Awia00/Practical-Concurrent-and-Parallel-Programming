
public class MyAtomicInteger {
    private int value;

    public synchronized int addAndGet(int add)
    {
        value += add;
        return value;
    }

    public synchronized int get()
    {
        return value();
    }
}