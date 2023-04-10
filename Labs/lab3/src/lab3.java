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


    private static void assertFileExists(String path) throws LabException.BadArgs {
        if(!new File(path).isFile())
            throw new LabException.BadArgs("\"" + path + "\" is does not exist");
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
                throw new LabException.BadArgs("No keyword given");

            // test keyword
            switch (args[0]) {
                // test train args
                case "train" -> {

                    // correct arg count
                    if (args.length != 4)
                        throw new LabException.BadArgs("Expected 3 arguments but got " + (args.length - 1));   // -1 for keyword

                    // check if valid file
                    assertFileExists(args[1]);

                    // allowed learning type
                    if (!args[3].equals("dt") && !args[3].equals("ada"))
                        throw new LabException.BadArgs("Unknown learning-type, expected \"dt\" or \"ada\" but got \"" + args[3] + "\"");

                    train(loadData(args[1]), args[2], args[3]);

                }

                case "predict" -> {
                    // correct arg count
                    if (args.length != 3)
                        throw new LabException.BadArgs("Expected 2 arguments but got " + (args.length - 1));   // -1 for keyword

                    // check if valid file
                    assertFileExists(args[1]);

                    // assert sentence frags exist
                    assertFileExists(args[2]);

                    predict(args[1], args[2]);
                }
                default -> throw new LabException.BadArgs("Unknown keyword, expected \"train\" or \"predict\" but got \"" + args[0] + "\"");
            }
        } catch (LabException.BadArgs e){
            // print error and break
            System.err.println("BadArgsError: " + e.getLocalizedMessage());
            System.err.println("Expected Usage: java lab3 train <examples> <hypothesisOut> <learning-type>");
            System.err.println("Expected Usage: java lab3 predict <hypothesis> <file>");

        } catch (LabException.BadDatFile e){
            System.err.println("BadDatFile Error: " + e.getLocalizedMessage());
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
