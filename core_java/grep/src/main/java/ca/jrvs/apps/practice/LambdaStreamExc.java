package ca.jrvs.apps.practice;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface LambdaStreamExc {

    /**
     * Create a String stream from array note: arbitrary number of value will be stored in an array
     *
     * @param strings an array of strings
     * @return a stream of strings
     */
    Stream<String> createString(String... strings);

    /**
     * Convert all strings to uppercase please use createStrStream
     *
     * @param strings an array of strings
     * @return a stream of strings converted to uppercase
     */
    Stream<String> toUpperCase(String... strings);

    /**
     * filter strings that contains the pattern e.g. filter(stringStream, "a") will return another
     * stream which no element contains a
     *
     * @param stringStream a stream of strings
     * @param pattern      the pattern to filter
     * @return stream of strings without pattern
     */
    Stream<String> filter(Stream<String> stringStream, String pattern);

    /**
     * Create a intStream from a arr[]
     *
     * @param arr the array of integer/s
     * @return int primitive specialization of Stream (aka. integer stream)
     */
    IntStream createIntStream(int[] arr);

    /**
     * Convert a stream to a list
     *
     * @param stream the stream of any type
     * @param <E>    generic indicating any type
     * @return list of objects
     */
    <E> List<E> toList(Stream<E> stream);

    /**
     * Convert a intStream to list
     *
     * @param intStream the integer stream
     * @return list of integers (Instances)
     */
    List<Integer> toList(IntStream intStream);

    /**
     * Create a IntStream range from start to end inclusive
     *
     * @param start indicates from which number the stream will start
     * @param end   indicates at which number the stream will end
     * @return an IntStream
     */
    IntStream createIntStream(int start, int end);

    /**
     * Convert an intStream to a doubleStream and compute square root of each element
     *
     * @param intStream the stream of integers
     * @return the stream of doubles
     */
    DoubleStream squareRootIntStream(IntStream intStream);

    /**
     * filter all even number and return odd numbers from a intStream
     *
     * @param intStream the stream of integers
     * @return stream of integer containing odd integers only
     */
    IntStream getOdd(IntStream intStream);

    /**
     * Return a lambda function that print a message with a prefix and suffix This lambda can be
     * useful to format logs
     * <p>
     * You will learn: - functional interface http://bit.ly/2pTXRwM & http://bit.ly/33onFig - lambda
     * syntax
     * <p>
     * e.g. LambdaStreamExc lse = new LambdaStreamImp(); Consumer<String> printer =
     * lse.getLambdaPrinter("start>", "<end"); printer.accept("Message body");
     * <p>
     * sout: start>Message body<end
     *
     * @param prefix prefix str
     * @param suffix suffix str
     * @return
     */
    Consumer<String> getLambdaPrinter(String prefix, String suffix);

    /**
     * Print each message with a given printer Please use `getLambdaPrinter` method
     * <p>
     * e.g. String[] messages = {"a","b", "c"}; lse.printMessages(messages,
     * lse.getLambdaPrinter("msg:", "!") );
     * <p>
     * sout: msg:a! msg:b! msg:c!
     *
     * @param messages the string of messages
     * @param printer
     */
    void printMessages(String[] messages, Consumer<String> printer);

    /**
     * Print all odd numbers from a intStream. Please use `createIntStream` and `getLambdaPrinter`
     * methods
     * <p>
     * e.g. lse.printOdd(lse.createIntStream(0, 5), lse.getLambdaPrinter("odd number:", "!"));
     * <p>
     * sout: odd number:1! odd number:3! odd number:5!
     *
     * @param intStream the stream of integers
     * @param printer
     */
    void printOdd(IntStream intStream, Consumer<String> printer);

    /**
     * Square each number from the input. Please write two solutions and compare difference - using
     * flatMap
     *
     * @param ints
     * @return
     */
    Stream<Integer> flatNestedInt(Stream<List<Integer>> ints);
}

