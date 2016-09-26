# Mandatory 2: week 4
*Anders Wind: awis@itu.dk*


## 4.1
My specs:
CPU: I7-4500U 1.8GHz turbo 2.4GHz, 4 cores (two logical pr physical core)
RAM: 8Gb

### 1)
The results are in the file: 4.1_1_results.txt

The results seem very realistic, and are in the same ballpark as those shown in the microbenchmarks article.
In mark5 there is one odd line:

    1277.0 ns +/-  3880.81         64

This outlier is about 30 times slower than the rest of the result, for 64 iterations. But like Sestoft does in the report, since the standard deviation is huge, we throw away this answer. This is probably because the OS scheduled some other process while the computation ran.

The anwers for Mark7 seem very alligned with those of the microbenchmarks article.

Overall the results seem plausible

### 2)
The results are in the file: 4.1_2_results.txt

Generally the results are a bit slower, but allign very much to the results of microbenchmarks. The deviation is generally a bit lower.

I also have results from a Macbook - the results are shown in the file: 4.1_2_MAC_results.txt
These results are even closer to my first results than the ones shown in microbenchmarks, which is promissing as for the realiability of the benchmarks.


## 4.2

### 1)
What we see in the results with Mark6 is that the timing results for small computations are very high. This makes perfect sense because the deviation is also high. The larger the problem size the smaller the deviation.
There are two results in "Thread create start" which does not follow this pattern. 

    Thread create start              115106.0 ns   41472.58         16
    Thread create start              107876.0 ns   13836.37         32

Here the problemsizes smaller than 16 actually has lower average runtime. This is probably also due to OS scheduling some other task in the background. This is also supported by the fact that the standard deviation is high for these two values.

### 2)
The results are shown in 4.2_2_results.txt

The results generally corrosponds to the results in the slides.

Point creation runs a lot faster than shown in the slides (slide 37: 80.9ns) but the result is also different (8388608 vs 4194304) the same is true for "thread create start" and "thread create start join". The results are different because Mark7 only waits until the running time is 0.25ms which apperently is earlier on my machine than the ones used for the results on the slides. 
The results for the last two mentioned tests are also a bit slower in average than those in the slide, but that can be explained by the slightly slower processor i have (laptop processor with lower baseclock). Furthermmore thread create start has a huge standard deviation more simular to that of the server CPU from the slides. 

## 4.3

### 1)
See the raw results in 4.3_1_results.txt

### 2)
See the graph in: 4.3_2_graph.png

### 3)
The results seem plausible and corrosponds ok with those shown in the slides. First of all running parallel with 1 thread is worse than running sequentially which makes sense due to the overhead of creating a thread compared to not creating one.
The next noticable result is that increasing the amount of threads does not improve the result over 4 threads. This corrosponds fine with the fact that my machine has 4 logical cores. Any amount of threads over 4 will require the CPU to schedule the different threads work, which creates overhead.

### 4) 
Raw data: 4.3_4_results.txt
graph: 4.3_4_graph.png

It does not deviate from the results using longcounter a lot actually they corrosponds very much. One thing to notice is that 8 threads are actually faster than 4 with atomic long, but its a question of a few percentage and therefore is probably just jitter in the measurements. The results are generally a bit slower but only by a few percentage aswell and therefore I would use generally use AtomicLong since it requires less work, and by using a tested library you are less likely to oversee bugs yourself. 

### 5)
See changes in code in TestCountPrimesThreads.java.

At a glance it does not seem to improve the results on my machine either, atleast not a lot. 

## 4.4
### 1)
See TestCache.java for the implementation.

Result:
Memoizer1                       5608030.5 us   25634.88          2

### 2)
Result:
Memoizer2                       2492652.4 us    9271.79          2

### 3)
Result:
Memoizer3                       2308369.2 us    4731.56          2

### 4)
Result:
Memoizer4                       2305144.7 us    2742.13          2

### 5)
Result:
Memoizer5                       2306249.4 us    5819.39          2

### 6)
Result:
Memoizer0                       2407164.9 us   19491.75          2

### 7)
These results corrosponds great to the results I got in the first Mandatory exercise. Memoizer3, 4 and 5 are the fastest, but the simple Memoizer0 holds up quite well and is more simple. There will be a lot of waiting between the 16 threads since they all have to calculate the first 2000 number, and therefore should wait for some of the results instead of calculating them. If we had choosen a lower amount of threads, there would be a lower amount of time waiting and more time calculating numbers.

MANGLER STADIG AT SVARE PÅ GOETz

### 8)
