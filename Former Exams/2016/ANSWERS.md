# PCPP 2016 exam
*Anders Wind - awis@itu.dk*

## Question 1
### 1)
It does not indicate that Mystery is threadsafe. I tried running it multiple times and got these results:

Sum is 1504885.000000 and should be 2000000.000000
Sum is 1847171.000000 and should be 2000000.000000
Sum is 1503898.000000 and should be 2000000.000000
Sum is 1506196.000000 and should be 2000000.000000
Sum is 1492341.000000 and should be 2000000.000000
Sum is 1509382.000000 and should be 2000000.000000

### 2)
The synchronized keyword could be rewritten as a statement.
On static methods the synchronized keyword would be synchronized(Mystery.class) as a class, whereas the synchronized statement for the object would be syncronized(this) where this is the object on which the method is called.

Therefore they do lock on different references and therefore we get the unsafe behavior in the main method.

### 3)
If i were not to change the main method I would make the following change to addInstance

    public synchronized void addInstance(double x) {
        synchronized(Mystery.class)
        {
            sum += x;
        }
    }

And this change works.

## Question 2
### 1)
I would make synchronized to the add and set methods. Then I would make size and items volatile to ensure visibility from changes when the get and size methods are called.

### 2)
It would scale quite poorly since each thread would only get access to the data structure at a time - hence making it act almost completely serial.

### 3)
This would not make it threadsafe because the methods are then still modifying chared data concurrently - therefore race conditions are bound to happen. 

### 4)
In the sense that each method in itself is threadsafe. That is if two threads call set(1, 2) and set(1, 3), concurrently then the thread which accessed the set method last(therefore waiting for the lock) would decide the final value of 1.

## Question 3 

### 1)
One could make the totalSize an AtomicInt or LongAdder - they are threadsafe and provide the same functionality. 

### 2)
I would use javas ConcurrentHashSet instead. It allows multiple threads to add and get(which are the only two methods we need) elements concurrently in a threadsafe manner.

For both of these answers it would be a good idea to make them final aswell since it would provide better performance and it is generally a good idea to make fields final when they dont change and work in a cncurrent environment because visibility....

## Question 4


## Question 5


## Question 6


## Question 7


## Question 8


## Question 9


## Question 10


## Question 11


## Question 12


