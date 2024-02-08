package ca.jrvs.apps.bettergrep;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaGrepImp implements JavaGrep {

    static final Logger logger = LoggerFactory.getLogger(JavaGrepImp.class);

    private String regex;
    private String rootPath;
    private String outputFile;

    public static void main(String[] args) {

        // Creating a default simplistic configuration of logger (Displays on console).
        BasicConfigurator.configure();

        if (args.length != 3) {
            logger.error("[End-side Issue] Insufficient Command Line Arguments.");
            throw new IllegalArgumentException(
                "Usage: JavaGrepImp [regexPattern] [rootPath] [outputPath]"
            );
        }

        JavaGrepImp javaGrepImp = new JavaGrepImp();
        javaGrepImp.setRegex(args[0]);
        javaGrepImp.setRootPath(args[1]);
        javaGrepImp.setOutputFile(args[2]);

        try {
            javaGrepImp.process();
        } catch (IOException ioException) {
            JavaGrepImp.logger.error("Error: Unable to Process.", ioException);
        }
    }

    /**
     * High level workflow of the Grep Application.
     *
     * @throws IOException exception while performing input output operations.
     */
    @Override
    public void process() throws IOException {

        List<String> matchedLines = new ArrayList<>();

        // Stream of all the available files inside the directory.
        Stream<Path> pathStream = listAllFiles(this.rootPath);
        pathStream.
            forEach(filePath -> readLines(filePath).
                forEach(line -> {
                    if (containsPattern(line)) {
                        matchedLines.add(line);
                    }
                })
            );

        // Converting the list of matched lines into a Stream of Strings.
        Stream<String> matchedLinesStream = matchedLines.stream();

        // Storing the matched lines inside the output file.
        writeToFile(matchedLinesStream);
    }

    /**
     * Traverse through the given directory and return all the files
     *
     * @param rootDirectory The path to the root directory from which the search will begin
     * @return files inside the root directory
     */
    @Override
    public Stream<Path> listAllFiles(String rootDirectory) {

        Stream<Path> allFilesStream;
        try {
            // Further filtering and returning Paths which are only files, not directories.
            allFilesStream = Files.walk(Paths.get(rootDirectory))
                .filter(file -> !Files.isDirectory(file));
            return allFilesStream;

        } catch (IOException ioException) {
            logger.error("Error Traversing the directory structure." + ioException);
            throw new RuntimeException(ioException);
        }
    }

    /**
     * Reads a given file and return all the lines
     *
     * @param inputFile the file which is to be read
     * @return lines inside the file
     * @throws IllegalArgumentException if the given file is not a file
     */
    @Override
    public Stream<String> readLines(Path inputFile) {

        try {
            // streamOfFileLines = Files.lines(inputFile);
            BufferedReader bufferedReader = Files.newBufferedReader(inputFile);

            // Return a Stream of String consisting of all the lines in the file.
            return bufferedReader.lines();

        } catch (IOException ioException) {
            logger.error("Cannot perform read operation on the file " + inputFile);
            throw new RuntimeException(ioException);
        }
    }

    /**
     * checks whether the line contains the regex pattern passed by the user
     *
     * @param line input string
     * @return true if there's a match otherwise false
     */
    @Override
    public boolean containsPattern(String line) {

        // Under the hood, the .compile method creates an instance of the Pattern class via parameterized constructor.
        Pattern regexPattern = Pattern.compile(this.regex);

        // .matcher method is the method of Pattern class taking the character sequence as a parameter.
        // .matcher() method creates a new instance of the Matcher class and returns its reference.
        Matcher patternMatcher = regexPattern.matcher(line);

        return patternMatcher.find();
    }

    /**
     * writes lines to a file
     *
     * @param lines matched strings / lines with the pattern
     * @throws IOException if write operation failed
     */
    @Override
    public void writeToFile(Stream<String> lines) throws IOException {

        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Path.of(this.outputFile))) {
            lines.forEach(line -> {
                try {
                    bufferedWriter.write(line);
                    bufferedWriter.newLine();
                } catch (IOException ioException) {
                    logger.error(String.valueOf(ioException));
                    throw new RuntimeException(ioException);
                }
            });
        }
    }

    /**
     * getter method
     *
     * @return the regex pattern
     */
    public String getRegex() {
        return regex;
    }

    /**
     * setter method
     *
     * @param regex the regex pattern
     */
    public void setRegex(String regex) {
        this.regex = regex;
    }

    /**
     * getter method
     *
     * @return the root path of the directory
     */
    public String getRootPath() {
        return rootPath;
    }

    /**
     * setter method
     *
     * @param rootPath the root path of the directory
     */
    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    /**
     * getter method
     *
     * @return the output file name
     */
    public String getOutputFile() {
        return outputFile;
    }

    /**
     * setter method
     *
     * @param outputFile the output file name
     */
    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }
}
