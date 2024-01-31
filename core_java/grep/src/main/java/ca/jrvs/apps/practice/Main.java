package ca.jrvs.apps.practice;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {

        RegexExcImp regexExc = new RegexExcImp();

        System.out.println(regexExc.matchJpeg("Test.jpg"));
        System.out.println(regexExc.matchJpeg("Test.JPG"));
        System.out.println(regexExc.matchJpeg("3.JPG"));
        System.out.println(regexExc.matchJpeg(".jpeg"));

        System.out.println(regexExc.matchIp("11.32.124.999"));

        System.out.println(regexExc.isEmptyLine(" "));
        System.out.println(regexExc.isEmptyLine(""));
        System.out.println(regexExc.isEmptyLine("\n\n"));

        List<Integer> myIntegers = new ArrayList<>();
        myIntegers.add(1);
        myIntegers.add(2);
        myIntegers.add(3);

        Stream<Integer> integerStream = myIntegers.stream();
        integerStream.forEach(integer -> System.out.println(integer));

        List<Integer> testInteger = Arrays.asList(1, 2, 3, 4, 5);
        testInteger.forEach(number -> System.out.println(number));
    }

    public static void testClass() {

    }
}
