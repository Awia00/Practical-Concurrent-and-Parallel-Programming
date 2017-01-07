# Exam 2015
*Anders Wind Steffensen - awis@itu.dk*


## Question 1
I have added two lock objects. A bottomLock and a topLock.
For pop and steal both bottom and top locks are needed since manipulation of either of those variables can at interleavings make them remove the same element.

Interestingly since top can only increase and the failcase of push is when size gets too large (and top is only used negatively : therefore only making size smaller) one can make push only lock on the bottom element.

NOTE: Probably also need to make top volitile then.


## Question 2
Sorttask is threadsafe for the variables a and b. These are primitive types and final which means. Therefore they are readonly which therefore makes it safe to work with from multiple threads. arr however is only threadsafe in the sense that the reference to the array is final and therefore cannot be changed, the values of the array however can cause race conditions and other errors caused by multiple threads working with arr.


## Question 3
It sorts the array.
It is safe since it only reads values(which are final), except for the one place where it swaps to values in the array. This could create race conditions if not for the fact that only one thread will work on that part of the array at a time, since the only other sortTasks that could exist all have different a and b values that do not overlap. Furthermore I already argued that the queue was threadsafe and therefore it does not pose any threat.


## Question 4

## Question 5

1 cores:        Time:   5.022461
true
2 cores:        Time:   4.885933
true
3 cores:        Time:   4.905063
true
4 cores:        Time:   4.903342
true
5 cores:        Time:   4.916938
true
6 cores:        Time:   4.893607
true
7 cores:        Time:   4.804173
true
8 cores:        Time:   4.816813
true
9 cores:        Time:   4.812790

## Question 6
1 cores:        Time:   4.917138
true
2 cores:        Time:   4.977236
true
3 cores:        Time:   5.992478
true
4 cores:        Time:   6.05089
true
5 cores:        Time:   6.018155
true
6 cores:        Time:   6.030002
true
7 cores:        Time:   6.00684
true
8 cores:        Time:   6.030341
true
9 cores:        Time:   6.039544

## Question 7

## Question 11
1 cores:        Time:   4.478738
true
2 cores:        Time:   4.400327
true
3 cores:        Time:   4.254553
true
4 cores:        Time:   4.278442
true
5 cores:        Time:   4.278401
true
6 cores:        Time:   4.295121
true
7 cores:        Time:   4.330412
true
8 cores:        Time:   4.272225
true
9 cores:        Time:   4.281875
true