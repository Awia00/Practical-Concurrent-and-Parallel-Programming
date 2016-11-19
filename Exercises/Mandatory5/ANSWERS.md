# Mandatory 5
*Anders Wind - awis@itu.dk*

## Exercise 10.1
### 1)
#### Increment:
Increment gets the current value, and adds one, then calls compareAndSet. compareAndSet will atomically check if the old value is the same and if it is update it to the new value. This implies that if some other thread has updated the value in the meantime, our thread will return false and therefore try to increment again. Due to this we ensure that the update happens at sometime (if livelock does not happen)

#### getBins:
getBins returns a snapshot of the bins. Values of bins might change in the meantime. Since it is a copy, any other program cannot mess up the bins of the histogram.

### getAndClear:
getAndClear works just like increment and therefore the argumentation is the same.

### transferBins:
transferBins makes a new array of AtomicReferences. Then it goes over the elements in the input and puts them into that array. Since only this thread works with the array, multiple threads cannot mess up values. In the end bins is overriden and therefore some updates may have been lost, but since we want to swap the queue out this must be seen as part of the functionality of the method.

### 2)
Yes the implementation produces the correct answer

### 3)
The transactional implementation takes: Time: 3.193138 seconds
Whereas the CAS only takes:             Time: 2.522318 seconds
(CAS is consistently ~1 second faster - ran it about 10 times )

This might be the case if there is mostly reads since this is where CAS shines. Since there is only some overlap a lot of the methods will go through without requiring extra work, furthermore each thread starts at different points, so one thread might be done with a range of numbers before another thread gets to it. 

(Actually I have later found out that the order of which i run the test matters a little. So sometimes CAS runs at ~2.5 seconds if it runs first)
### 4)
(maybe later)

## Exercise 10.2
### 1)
Solved. See src/SimpleRWTryLockTester.java

### 2)
Solved. See src/SimpleRWTryLockTester.java

### 3)
Solved. See src/SimpleRWTryLockTester.java

### 4)
Solved. See src/SimpleRWTryLockTester.java

### 5)
Solved. See src/SimpleRWTryLockTester.java
To run it use the -ea flag

### 6)
Solved. See src/SimpleRWTryLockTester.java
To run it use the -ea flag

One migh have created another kind of test, based on possible situations instead of stress testing with a lot of overlappings. Such like acquiring a lock on one thread, then try to do every operation with the other thread and vice versa.
I cannot be completely certain that all these situations have been tried, but i know that atleast every thread gets to a situation where it cannot acquire a lock.

## Exercise 10.3
### 1)
The raw results are available in results/10.3-results.txt
The graph is available in results/10.3-graph.png
The WrappedTLRandom lock seems to be the fastest on my machine but are closely followed by the TLLocking random and the TLCasRandom. The CasRandom is ultimately the slowest of them all. The 3 fast implementations are quite constant as the number of threads grow but CasRandom is scaling really badly when the amount of threads increase. This makes sense since each thread might have to rollback its work and try again over and over due to the nature of compareAndSet. 
The one which is ultimately the fastest is also the one which has the lowest running time with most threads.