import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    private static List<Data> loadData(String filepath) throws Exception {

        BufferedReader br = new BufferedReader(new FileReader(filepath));

        String line = br.readLine();
        List<Data> dataList = new ArrayList<>();

        while (line != null){
            dataList.add(new Data(line));
            line = br.readLine();
        }
        br.close();

        return dataList;
    }

    public static void train(List<Data> examples, String hypothesisOut, String learningType){

    }

    public static void predict(String hypothesis, String file){

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

                    // check if valid file
                    assertFileExists(args[1]);

                    // allowed learning type
                    if (!args[3].equals("dt") && !args[3].equals("ada"))
                        throw new Exception("Unknown learning-type, expected \"dt\" or \"ada\" but got \"" + args[2] + "\"");

                    train(loadData(args[1]), args[2], args[3]);

                }

                case "predict" -> {
                    // correct arg count
                    if (args.length != 3)
                        throw new Exception("Expected 2 arguments but got " + (args.length - 1));   // -1 for keyword

                    // check if valid file
                    assertFileExists(args[1]);

                    // assert sentence frags exist
                    assertFileExists(args[2]);

                    predict(args[1], args[2]);
                }
                default -> throw new Exception("Unknown keyword, expected \"train\" or \"predict\" but got \"" + args[0] + "\"");
            }
        } catch (Exception e){
            // print error and break
            System.err.println("Error: " + e.getLocalizedMessage());
            System.err.println("Expected Usage: java lab3 train <examples> <hypothesisOut> <learning-type>");
            System.err.println("Expected Usage: java lab3 predict <hypothesis> <file>");
            return;
        }



    }
}
