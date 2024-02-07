package ca.jrvs.apps.bettergrep;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface JavaGrep {

    /**
     * High level workflow of the Grep Application.
     * @throws IOException exception while performing input output operations.
     */
    void process() throws IOException;

    /**
     * Traverse through the given directory and return all the files
     *
     * @param rootDirectory The path to the root directory from which the search will begin
     * @return files inside the root directory
     */
    Stream<Path> listAllFiles(String rootDirectory);

    /**
     * Reads a given file and return all the lines
     *
     * @param inputFile the file which is to be read
     * @return lines inside the file
     * @throws IllegalArgumentException if the given file is not a file
     */
    Stream<String> readLines(Path inputFile);

    /**
     * checks whether the line contains the regex pattern passed by the user
     * @param line input string
     * @return true if there's a match otherwise false
     */
    boolean containsPattern(String line);

    /**
     * writes lines to a file
     * @param lines matched strings / lines with the pattern
     * @throws IOException if write operation failed
     */
    void writeToFile(Stream<String> lines) throws IOException;
}
