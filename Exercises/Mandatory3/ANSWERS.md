# Mandatory 1
*Anders Wind - awis*


## 6.1

### 1)

### 2)
missing explanation

### 4)
missing explanation

### 7)
My machine is a windows but I run the implementation through bash on windows

    OS:   Linux; 3.4.0+; amd64
    JVM:  Oracle Corporation; 1.8.0_101
    CPU:  null; 4 "cores"
    Date: 2016-10-07T09:34:28+0000 

    Windows 10: I7 4500U - 1.8 GHz 2.7 GHz turbo

Results:
    SynchronizedMap       16         513726.9 us   17091.04          2
    99992.0
    StripedMap            16         252374.5 us   34500.28          2
    99992.0


Missing explanation

### 8)
An example of a downside is that the size method become extremely slow since it would have many more elements to iterate over and sum. Search would also get slower since the linked list would be longer since each stripe would have fewer bucket, and reallocate would get called more often for the same reason. 
Furthermore you increase the memory consumption a lot by increasing the amount of locks so on huge datasets that might be a problem. 

### 9)
The positive about using a stripe for each element would be that working on different parts of the map would never requiring waiting for each thread. So many operations across the map could happen concurrently. When only 16 locks are present there is a chance around 16

### 10)
It is important for example for reallocate since the elements get put into buckets of the same stripe as before. If it was not the case, a thread which is working on an element, while a reallocate happens, might not get the lock on the correct stripe since the element could change stripe.





## 6.2

### 7)
SynchronizedMap       16         507073.3 us   14828.70          2
99992.0
StripedMap            16         240694.8 us   40686.56          2
99992.0
StripedWriteMap       16         145205.6 us   39201.40          2
99992.0
WrapConcHashMap       16         174371.4 us   81073.07          2
99992.0




## 6.3

### 1)
current thread hashCode               0.0 us       0.00  134217728
ThreadLocalRandom                     0.0 us       0.00   67108864
AtomicLong                       817279.4 us   17205.48          2
LongAdder                        146523.3 us   14192.32          2
LongCounter                      608574.4 us  371303.17          2
NewLongAdder                     432395.8 us   93181.59          2
NewLongAdderPadded               189110.1 us   15608.16          2

### 2)
See TestLongAdders.java for the implementation.

current thread hashCode               0.0 us       0.00  134217728
ThreadLocalRandom                     0.0 us       0.00   67108864
AtomicLong                       813934.3 us   10951.69          2
LongAdder                        140306.9 us   13930.68          2
LongCounter                      609595.9 us  369743.63          2
NewLongAdder                     425597.2 us   66200.00          2
NewLongAdderPadded               183817.4 us   19443.74          2
NewLongAdderLessPadded           221361.8 us   15872.61          2