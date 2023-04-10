/**
 * @author Derek Garcia
 **/

public class LabException {

    public static class BadArgs extends Exception{
        public BadArgs(String msg){
            super(msg);
        }
    }

    public static class BadDatFile extends Exception{
        public BadDatFile(String msg){
            super(msg);
        }
    }
}
