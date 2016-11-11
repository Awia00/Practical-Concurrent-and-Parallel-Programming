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


### 3)

### 4)


## Exercise 10.2
### 1)

### 2)

### 3)

### 4)

### 5)

### 6)


## Exercise 10.3
### 1)