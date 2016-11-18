# Exercise 6 (11)
*Anders Wind - awis@itu.dk*

## 10.1

### 1)
Done see src/TestMSQueue.java

### 2)
Done see src/TestMSQueue.java

### 3)
Interestingly enough removing the if (last == tail.get()) in enqueue and if (first == head.get()) in dequeue does not change a thing. 
This makes sense since as told in the lecture the proof of this working still holds when these checks are left out. Also the reason they are in is to make sure that the last and first element is not changed since they were found, but even with the check the element could be changed right after and therefore even if it was important for the functionality, one could still make an interleaving where it would destroy the functionality. - atleast that seems to be so.

I removed the statements at D13 with just "head.set(next);" that made the concurrent test fail. The bug materialize as wrong elements being removed from the queue.

## 10.2

## 10.3