// Week 3
// sestoft@itu.dk * 2015-09-09

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Date;

public class TestWordStream {
  public static void main(String[] args) {
    String filename = "usr/share/dict/words";
    System.out.println(readWords(filename).count());

    System.out.println("\n Exercise 3.3 2)");
    readWords(filename).limit(100).forEach(System.out::println);

    System.out.println("\n Exercise 3.3 3)");
    readWords(filename).filter(s -> s.length() > 22).forEach(System.out::println);

    System.out.println("\n Exercise 3.3 4)");
    System.out.println(readWords(filename).filter(s -> s.length() > 22).findAny().get());

    System.out.println("\n Exercise 3.3 5)");
    long nonParallel = new Date().getTime();
    readWords(filename).filter(s -> isPalindrome(s)).forEach(System.out::println);
    nonParallel = (new Date().getTime() - nonParallel);

    long parallel = new Date().getTime();
    System.out.println("\n Exercise 3.3 6)");
    readWords(filename).parallel().filter(s -> isPalindrome(s)).forEach(System.out::println);
    parallel = (new Date().getTime() - parallel);

    System.out.println("Non parallel: " + nonParallel+ " Parallel: " + parallel);

    System.out.println("\n Exercise 3.3 7)");
    System.out.println(readWords(filename).mapToInt(s -> s.length()).max().getAsInt());
    System.out.println(readWords(filename).mapToInt(s -> s.length()).min().getAsInt());
    System.out.println(readWords(filename).mapToInt(s -> s.length()).average().getAsDouble());

    System.out.println("\n Exercise 3.3 8)");
    readWords(filename).collect(Collectors.groupingBy(s -> s.length()));

    System.out.println("\n Exercise 3.3 9)");
    readWords(filename).map(s -> letters(s)).limit(100).forEach(System.out::println);
  }

  public static Stream<String> readWords(String filename) {
    try {
      // Exercise 3.3 1)
      BufferedReader reader = new BufferedReader(new FileReader(filename));
      Stream<String> words = reader.lines().parallel(); 
      return words;
    } catch (IOException exn) { 
      return Stream.<String>empty();
    }
  }

  // Exercise 3.3 5)
  public static boolean isPalindrome(String s) {
    String reverse = new StringBuffer(s).reverse().toString();
    return reverse.equals(s) ? true : false; 
  }

  public static Map<Character,Integer> letters(String s) {
    Map<Character,Integer> res = new TreeMap<>(s.chars().mapToObj(i -> (char)i).collect(Collectors.toMap(k-> k, i -> i++)));
    // TO DO: Implement properly
    return res;
  }
}
