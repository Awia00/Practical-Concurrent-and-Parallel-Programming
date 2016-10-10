# Mandatory 1
*Anders Wind - awis*


## 6.1
See TestStripedMap.java for all the implementations
### 1)
Done

### 2)
Locking on stripe s makes sure that no element can be added or removed at the same time from that strip - affecting the sizes[strip].

### 3) 
Done

### 4)
Done

### 5)
I choose the second approach since locks are acquired one at a time and therefore more concurrency can happen on the other strips of the map while the foreach is executing.

### 6)
Done

### 7)
My machine is running Windows 10 but I run the implementation through bash on windows

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

The results seem reasonable since the synchronized map does not allow any concurrency, we get a double up in speed when the striped map is used. Even though my machine has 4 cores - there is still risk of threads working on elements in the same strip.

### 8)
An example of a downside is that the size method become extremely slow since it would have many more elements to iterate over and sum. Search would also get slower since the linked list would be longer since each stripe would have fewer bucket, and reallocate would get called more often for the same reason. 
Furthermore you increase the memory consumption a lot by increasing the amount of locks so on huge datasets that might be a problem. 

### 9)
The positive about using a stripe for each element would be that working on different parts of the map would never requiring waiting for each thread. So many operations across the map could happen concurrently. When only 16 locks are present there is a chance around 16

### 10)
It is important for example for reallocate since the elements get put into buckets of the same stripe as before. If it was not the case, a thread which is working on an element, while a reallocate happens, might not get the lock on the correct stripe since the element could change stripe.


## 6.2
See TestStripedMap.java for all the implementations

### 1)
Done

### 2)
Done

### 3)
Sinze we only put an item if it is missing - aka we are adding an element, then ofcourse the size only changes if we add something.

### 4)
Done

### 5)
Done

### 6)
SynchronizedMap       16         507073.3 us   14828.70          2
99992.0
StripedMap            16         240694.8 us   40686.56          2
99992.0
StripedWriteMap       16         145205.6 us   39201.40          2
99992.0
WrapConcHashMap       16         174371.4 us   81073.07          2
99992.0

It seems pretty reasonable that the StripedWriteMap is faster than any other implementation since it allows concurrent reads, and only locks when neccesary. Why it is faster than the WrapConcHashMap is not clear, but it is quite close to the StripedWriteMap, which makes sense.


## 6.3

### 1)
current thread hashCode               0.0 us       0.00  134217728
ThreadLocalRandom                     0.0 us       0.00   67108864
AtomicLong                       817279.4 us   17205.48          2
LongAdder                        146523.3 us   14192.32          2
LongCounter                      608574.4 us  371303.17          2
NewLongAdder                     432395.8 us   93181.59          2
NewLongAdderPadded               189110.1 us   15608.16          2

As described in the class documentation, the LondAdder is very fast compared to the AtomicLong. The NewLongAdderPadded gets close to the same performace, but as Sestoft writes, the LongAdder is meticiously created by experts. Sestofts write that a 6 times speedup from AtomicLong to LongAdder should be expected, on a 4 core machine, which is what i get. On the other hand, the NewLongAddder also corrosponds to the 2x speedup compared to the AtomicLong. All in all the results seem reasonable.

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

So yes the objects actually make the code run faster, probably due to the false shared cache. 