package ca.jrvs.apps.grep;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaGrepImp implements JavaGrep {

    final Logger logger = LoggerFactory.getLogger(JavaGrep.class);

    private String regex;
    private String rootPath;
    private String outputFile;

    public static void main(String[] args) throws IOException {

        // Creating a default simplistic configuration of logger (Displays on console).
        BasicConfigurator.configure();

        if (args.length != 3) {
            throw new IllegalArgumentException(
                "USAGE: JavaGrepImp [regexPattern] [rootPath] [outputPath]"
            );
        }

        JavaGrepImp javaGrepImp = new JavaGrepImp();
        javaGrepImp.setRegex(args[0]);
        javaGrepImp.setRootPath(args[1]);
        javaGrepImp.setOutputFile(args[2]);

        try {
            javaGrepImp.process();
        } catch (IOException ioException) {
            javaGrepImp.logger.error("Error: Unable to Process." + ioException);
            throw new IOException("Error: Unable to Process.", ioException);

        } catch (NullPointerException nullPointerException) {
            javaGrepImp.logger.error("Invalid Path", nullPointerException);
        }
    }

    /**
     * High level workflow of the Grep App
     *
     * @throws IOException exception while accessing streams, files and directories.
     */
    @Override
    public void process() throws IOException {

        List<String> matchedLines = new ArrayList<>();
        for (File file : listAllFiles(rootPath)) {
            for (String line : readLines(file)) {
                if (containsPattern(line)) {
                    matchedLines.add(line);
                }
            }
        }
        writeToFile(matchedLines);
    }

    /**
     * Traverse through the given directory and return all the files.
     *
     * @param rootDirectory The path to the root directory from which the search will begin
     * @return files inside the root directory
     */
    @Override
    public List<File> listAllFiles(String rootDirectory) {

        // Instantiating a File Handler (Used by OS) which is just a reference to the file.
        File basePath = new File(rootDirectory);

        // Checking whether the path to the rootDirectory is valid or not.
        if (!basePath.isDirectory()) {
            logger.error("Invalid Path to the directory. Please provide an appropriate path.");
            return null;
        }

        // Creating a new List which stores the file handler reference objects.
        List<File> fileReferences = new ArrayList<>();

        // This method returns an array of all directories and files in the directory referenced by variable 'basePath'.
        File[] files = basePath.listFiles();

        // If the 'files' variable has null value, then it means there are no files in the 'basePath'.
        if (files != null) {
            for (File file : files) {

                // If it is a directory, get its absolute path and repeat the process.
                if (file.isDirectory()) {
                    fileReferences.addAll(listAllFiles(file.getAbsolutePath()));
                } else {
                    fileReferences.add(file);
                }
            }
        }
        return fileReferences;
    }

    /**
     * Reads a given file and return all the lines
     *
     * @param inputFile the file which is to be read
     * @return lines inside the file
     * @throws IllegalArgumentException if the given file is not a file
     */
    @Override
    public List<String> readLines(File inputFile)
        throws IllegalArgumentException, IOException {

        List<String> listOfLines = new ArrayList<>();

        // The resources are automatically closed inside the try with statement.
        // Opens a connection to the actual file allowing us to access it.
        try (FileReader fileReader = new FileReader(inputFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            String lineRead;

            // When the bufferedReader reaches the end of file, there won't be anything to read.
            // Due to this, it will return a 'null' value and at that point, we can stop reading.
            while ((lineRead = bufferedReader.readLine()) != null) {
                listOfLines.add(lineRead);
            }
        }
        return listOfLines;
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
    public void writeToFile(List<String> lines) throws IOException {

        // The try with resources will auto-close the resources.
        try (FileWriter fileWriter = new FileWriter(this.outputFile);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

            for (String matchedLine : lines) {
                bufferedWriter.write(matchedLine);
                bufferedWriter.newLine();
            }
        }
    }

    /**
     * getter method
     *
     * @return the root path of the directory
     */
    @Override
    public String getRootPath() {
        return rootPath;
    }

    /**
     * setter method
     *
     * @param rootPath the root path of the directory
     */
    @Override
    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    /**
     * getter method
     *
     * @return the regex pattern
     */
    @Override
    public String getRegex() {
        return regex;
    }

    /**
     * setter method
     *
     * @param regex the regex pattern
     */
    @Override
    public void setRegex(String regex) {
        this.regex = regex;
    }

    /**
     * getter method
     *
     * @return the output file name
     */
    @Override
    public String getOutputFile() {
        return outputFile;
    }

    /**
     * setter method
     *
     * @param outputFile the output file name
     */
    @Override
    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }
}
