/**
 * file: LabException.java
 *
 * Utility exceptions for parsing data files
 * @author Derek Garcia
 **/
public class LabException {

    /**
     * Input Arguments were invalid
     */
    public static class BadArgs extends Exception{
        public BadArgs(String msg){
            super(msg);
        }
    }


    /**
     * Unable to read in data file
     */
    public static class BadDatFile extends Exception{
        public BadDatFile(String msg){
            super(msg);
        }
    }
}
