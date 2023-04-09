import java.io.File;

/**
 * file: lab3.java
 * CSCI-331 Lab 3
 * *
 * @author Derek Garcia
 **/
public class lab3 {


    private static void assertFileExists(String path) throws Exception {
        if(!new File(path).isFile())
            throw new Exception("\"" + path + "\" is does not exist");
    }
    public static void main(String[] args) {

        // validate args
        try{
            if(args.length == 0)
                throw new Exception("No keyword given");

            // test keyword
            switch (args[0]) {
                // test train args
                case "train" -> {

                    // correct arg count
                    if (args.length != 4)
                        throw new Exception("Expected 3 arguments but got " + (args.length - 1));   // -1 for keyword

                    // allowed learning type
                    if (!args[2].equals("dt") && !args[2].equals("ada"))
                        throw new Exception("Unknown learning-type, expected \"dt\" or \"ada\" but got \"" + args[2] + "\"");
                }

                case "predict" -> {
                    // correct arg count
                    if (args.length != 3)
                        throw new Exception("Expected 2 arguments but got " + (args.length - 1));   // -1 for keyword

                    // assert sentence frags exist
                    assertFileExists(args[2]);
                }
                default -> throw new Exception("Unknown keyword, expected \"train\" or \"predict\" but got \"" + args[0] + "\"");
            }

            // 1st arg for both keywords must be valid file
            assertFileExists(args[1]);

        } catch (Exception e){
            // print error and break
            System.err.println("Error: " + e.getLocalizedMessage());
            System.err.println("Expected Usage: java lab3 train <examples> <hypothesisOut> <learning-type>");
            System.err.println("Expected Usage: java lab3 predict <hypothesis> <file>");
            return;
        }


    }
}
