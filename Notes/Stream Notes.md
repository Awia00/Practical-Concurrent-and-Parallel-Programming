# Streams Notes

## STREAMS FROM COLLECTIONS
- Arrays.stream(a) where a is an Arrays
- Stream<Map.Entry<A,V>> = map.entrySet().stream();

- IntStream.range(0,10_000);
- random.ints(5_000)
- bufferedReader.lines();


## GENERATE STREAMS
BEST WAY TO GENERATE: ITS LAZY
- IntStream.iterate(0, x -> x+1)

- IntSupplier
    - int next = 0
    - getAsInt {return next++}

- final int[] next = { 0 }; final array because lambda requires final but element should not be final.
    - generate(() -> next[0]++)


## COLLECTORS
A nice way to print stuff
stream(this).mapToObj(String::valueOf).collect(Collectors.joining(", ", "[", "]"));

## random
.boxed to use flatmap


Methods:
- map
- flatmap
- reduce
- limit
- foreach
- count
- findAny 


**Dijaktra: seperation of concerns: production of solutions versus consumption of solutions**