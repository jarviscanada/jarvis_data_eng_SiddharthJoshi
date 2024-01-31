package ca.jrvs.apps.practice;


public interface RegexExc {

    /**
     * returns true if the extension of the filename is jpg or jpeg (Not case-sensitive)
     *
     * @param filename the name of the file
     * @return boolean
     */
    boolean matchJpeg(String filename);

    /**
     * returns true if the ipAddress is valid.
     *
     * @param ipAddress The IP Address
     * @return boolean
     */
    boolean matchIp(String ipAddress);

    /**
     * returns true if the line is empty (e.g. empty, whitespace, tabs, etc.)
     *
     * @param line
     * @return boolean
     */
    boolean isEmptyLine(String line);
}
