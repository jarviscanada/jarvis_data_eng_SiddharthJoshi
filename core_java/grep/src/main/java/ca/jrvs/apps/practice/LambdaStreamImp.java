package ca.jrvs.apps.practice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LambdaStreamImp implements LambdaStreamExc {

    public static void main(String[] args) {

        LambdaStreamExc lambdaStream = new LambdaStreamImp();

        // Creating a Stream of Strings.
        Stream<String> stringStream = lambdaStream.createString("1", "2", "3", "4", "5");

        // Converting the String to Upper Case.
        Stream<String> upperStringSteam = lambdaStream.toUpperCase(
            "test", "Test", "TEst", "tESt", "TesT", "Oof"
        );
        // upperStringSteam.forEach(string -> System.out.println(string));

        // Filtering Strings based on the pattern.
        Stream<String> filteredStrStream = lambdaStream.filter(upperStringSteam, "ES");

        int[] myIntArray = {11, 22, 1, 3, 4, 28, 98, 2, 0};
        IntStream intStream = lambdaStream.createIntStream(myIntArray);

        // Converting the int stream to its respective type list.
        List<Integer> myIntList = lambdaStream.toList(intStream);
        System.out.println(myIntList);

        // Converting the string stream to its respective type list.
        List<String> myStringList = lambdaStream.toList(filteredStrStream);
        System.out.println(myStringList);

        // Creating a stream of integers from a specific range.
        IntStream rangedIntStream = lambdaStream.createIntStream(5, 24);

        // Converting a stream from one type to another.
        DoubleStream doubleStream = lambdaStream.squareRootIntStream(rangedIntStream);

        // Creating another stream and filtering out even numbers.
        IntStream basicIntStream = lambdaStream.createIntStream(0, 20);
        IntStream streamOfOdds = lambdaStream.getOdd(basicIntStream);
        streamOfOdds.forEach((oddNumber) -> System.out.println(oddNumber));

        // Utilizing lambda function/s.
        Consumer<String> printerString = lambdaStream.getLambdaPrinter("<", ">");
        printerString.accept("test-tag");

        // Utilizing...
        String[] messages = {"html", "head", "title", "body"};
        Consumer<String> printer = lambdaStream.getLambdaPrinter("<", ">");
        lambdaStream.printMessages(messages, printer);

        // Printing Odd Numbers from the stream
        IntStream anotherIntStream = lambdaStream.createIntStream(1, 30);
        Consumer<String> oddPrinter = lambdaStream.getLambdaPrinter("odd number", "!");
        lambdaStream.printOdd(anotherIntStream, oddPrinter);

        // Squaring each number...
        List<Integer> listOne = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> listTwo = Arrays.asList(11, 22, 33, 44, 55);
        List<Integer> listThree = Arrays.asList(111, 222, 333, 444, 555);
        List<List<Integer>> listCeption = Arrays.asList(listOne, listTwo, listThree);
        Stream<List<Integer>> streamOfLists = listCeption.stream();
        Stream<Integer> squaredStream = lambdaStream.flatNestedInt(streamOfLists);
    }

    /**
     * Create a String stream from array
     * note: arbitrary number of value will be stored in an array
     *
     * @param strings an array of strings
     * @return a stream of strings
     */
    @Override
    public Stream<String> createString(String... strings) {

        // .stream method from the Arrays class which will further call methods internally.
        return Arrays.stream(strings);
    }

    /**
     * Convert all strings to uppercase please use createStrStream
     *
     * @param strings an array of strings
     * @return a stream of strings converted to uppercase
     */
    @Override
    public Stream<String> toUpperCase(String... strings) {

        Stream<String> stringStream = Arrays.stream(strings);
        return stringStream.map(string -> string.toUpperCase());
    }

    /**
     * filter strings that contains the pattern e.g. filter(stringStream, "a") will return another
     * stream which no element contains a
     *
     * @param stringStream a stream of strings
     * @param pattern      the pattern to filter
     * @return stream of strings without pattern
     */
    @Override
    public Stream<String> filter(Stream<String> stringStream, String pattern) {

        Predicate<String> filterPredicate = new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return !s.contains(pattern);
            }
        };

        // Another way to achieve this same is...
        // return stringStream.filter((filteredString) -> !filteredString.contains(pattern));

        return stringStream.filter(filterPredicate);
    }

    /**
     * Create a intStream from an arr[]
     *
     * @param arr the array of integer/s
     * @return int primitive specialization of Stream (aka. integer stream)
     */
    @Override
    public IntStream createIntStream(int[] arr) {

        // Specialized Stream for primitive type integers.
        return Arrays.stream(arr);
    }

    /**
     * Convert a stream to a list
     *
     * @param stream the stream of any type
     * @return list of objects
     */
    @Override
    public <E> List<E> toList(Stream<E> stream) {

        List<E> myCustomList = new ArrayList<>();
        stream.forEach((streamElement) -> myCustomList.add(streamElement));
        return myCustomList;
    }

    /**
     * Convert a intStream to list
     *
     * @param intStream the integer stream
     * @return list of integers (Instances)
     */
    @Override
    public List<Integer> toList(IntStream intStream) {

        List<Integer> myIntList = new ArrayList<>();
        intStream.forEach((integer) -> myIntList.add(integer));
        return myIntList;
    }

    /**
     * Create a IntStream range from start to end inclusive
     *
     * @param start indicates from which number the stream will start
     * @param end   indicates at which number the stream will end
     * @return an IntStream
     */
    @Override
    public IntStream createIntStream(int start, int end) {
        return IntStream.rangeClosed(start, end);
    }

    /**
     * Convert an intStream to a doubleStream and compute square root of each element
     *
     * @param intStream the stream of integers
     * @return the stream of doubles
     */
    @Override
    public DoubleStream squareRootIntStream(IntStream intStream) {

        // Another way to approach this.
        // return intStream.asDoubleStream().map((value) -> Math.sqrt(value));

        return intStream
            .mapToDouble((value) -> Double.valueOf(value))
            .map((value) -> Math.sqrt(value));
    }

    /**
     * filter all even number and return odd numbers from a intStream
     *
     * @param intStream the stream of integers
     * @return stream of integer containing odd integers only
     */
    @Override
    public IntStream getOdd(IntStream intStream) {

        // Using Predicate internally to filter out the elements in the stream.
        return intStream
            .filter((integer) -> integer % 2 != 0);
    }

    /**
     * Return a lambda function that print a message with a prefix and suffix This lambda can be
     * useful to format logs
     * <p>
     * More details here: functional interface <a href="http://bit.ly/2pTXRwM">...</a> & <a href="http://bit.ly/33onFig">...</a> - lambda
     * syntax
     * <p>
     * e.g. LambdaStreamExc lse = new LambdaStreamImp();
     *      Consumer<String> printer =
     *      lse.getLambdaPrinter("start>", "<end");
     *      printer.accept("Message body");
     * <p>
     * sout: start>Message body<end
     *
     * @param prefix prefix str
     * @param suffix suffix str
     * @return a lambda function
     */
    @Override
    public Consumer<String> getLambdaPrinter(String prefix, String suffix) {

        return (message) -> {
            String newMessage = prefix + message + suffix;
            System.out.println(newMessage);
        };
    }

    /**
     * Print each message with a given printer using `getLambdaPrinter` method
     * <p>
     * e.g. String[] messages = {"a","b", "c"}; lse.printMessages(messages,
     *      lse.getLambdaPrinter("msg:", "!") );
     * <p>
     *      sout: msg:a! msg:b! msg:c!
     *
     * @param messages the string of messages
     * @param printer the instance of the Consumer interface
     */
    @Override
    public void printMessages(String[] messages, Consumer<String> printer) {

        /* Better way to approach this...
            Stream<String> stringStream = Arrays.stream(messages);
            stringStream.forEach((message) -> printer.accept(message));
        */

        for (String message : messages) {
            printer.accept(message);
        }
    }

    /**
     * Print all odd numbers from a intStream using `createIntStream` and `getLambdaPrinter`
     * methods
     * <p>
     * e.g. lse.printOdd(lse.createIntStream(0, 5), lse.getLambdaPrinter("odd number:", "!"));
     * <p>
     * sout: odd number:1! odd number:3! odd number:5!
     *
     * @param intStream the stream of integers
     * @param printer consumer
     */
    @Override
    public void printOdd(IntStream intStream, Consumer<String> printer) {

        (intStream.filter((number) -> number % 2 != 0))
            .forEach((number) -> printer.accept(Integer.toString(number)));
    }

    /**
     * Square each number from the input.
     * Two solutions - using flatmap in 2nd approach.
     *
     * @param ints stream of collections.
     * @return stream of squared objects from the @param's objects.
     */
    @Override
    public Stream<Integer> flatNestedInt(Stream<List<Integer>> ints) {

        /* Without flatMap
            List<Integer> squaredList = new ArrayList<>();
            ints.forEach(list -> list.forEach((element) -> {
                squaredList.add(element * element);
                System.out.println(squaredList.toString());
            }));

            return squaredList.stream();
        */

        /* flatMap() method is helpful to flatten a Stream of collections to a Stream of objects.
           Objects from all the collections in the original Stream are combined into a single collection.
        */

        // 1. Convert the Stream of collections into a stream of objects.
        // 2. Further square each element by tapping into each object of new stream.
        return ints
            .flatMap(intList -> intList.stream())
            .map((element) -> element * element);
    }
}
