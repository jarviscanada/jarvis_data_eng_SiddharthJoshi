package ca.jrvs.apps.grep;

import java.io.File;
// import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface JavaGrep {

    /**
     * High level workflow of the Grep App
     *
     * @throws IOException exception while accessing streams, files and directories.
     */
    void process() throws IOException;

    /**
     * Traverse through the given directory and return all the files
     *
     * @param rootDirectory The path to the root directory from which the search will begin
     * @return files inside the root directory
     */
    List<File> listAllFiles(String rootDirectory);

    /**
     * Reads a given file and return all the lines
     *
     * @param inputFile the file which is to be read
     * @return lines inside the file
     * @throws IllegalArgumentException if the given file is not a file
     */
    List<String> readLines(File inputFile) throws IllegalArgumentException, IOException;

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
    void writeToFile(List<String> lines) throws IOException;

    String getRootPath();

    void setRootPath(String rootPath);

    String getRegex();

    void setRegex(String regex);

    String getOutputFile();

    void setOutputFile(String outputFile);
}
