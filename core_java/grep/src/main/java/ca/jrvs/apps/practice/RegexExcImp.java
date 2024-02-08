package ca.jrvs.apps.practice;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexExcImp implements RegexExc {

    /**
     * returns true if the extension of the filename is jpg or jpeg (Not case-sensitive)
     *
     * @param filename the name of the file
     * @return boolean
     */
    @Override
    public boolean matchJpeg(String filename) {

        String regex = "^\\w+\\.(jpg|jpeg)";

        // Under the hood, the .compile method creates an instance of the Pattern class via parameterized constructor.
        Pattern regexPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

        // .matcher method is the method of Pattern class taking the character sequence as a parameter.
        // .matcher() method creates a new instance of the Matcher class and returns its reference.
        Matcher patternMatcher = regexPattern.matcher(filename);

        return patternMatcher.find();
    }

    /**
     * returns true if the ipAddress is valid. for the sake of simplicity, the range of valid IP
     * addresses are from 0.0.0.0 to 999.999.999.999
     *
     * @param ipAddress The IP Address
     * @return boolean
     */
    @Override
    public boolean matchIp(String ipAddress) {

        String regex = "^[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$";
        Pattern regexPattern = Pattern.compile(regex);
        Matcher patternMatcher = regexPattern.matcher(ipAddress);
        return patternMatcher.find();
    }

    /**
     * returns true if the line is empty (e.g. empty, whitespace, tabs, etc.)
     *
     * @param line empty line/s.
     * @return boolean
     */
    @Override
    public boolean isEmptyLine(String line) {

        String regex = "^\\s*$";
        Pattern regexPattern = Pattern.compile(regex);
        Matcher patternMatcher = regexPattern.matcher(line);
        return patternMatcher.find();
    }
}
