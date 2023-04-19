import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * file: lab3.java
 * CSCI-331 Lab 3
 * *
 * @author Derek Garcia
 **/
public class lab3 {

    /**
     * Tests if the file path
     *
     * @param path file path to test
     * @throws LabException.BadArgs File doesn't exist
     */
    private static void assertFileExists(String path) throws LabException.BadArgs {
        if(!new File(path).isFile())
            throw new LabException.BadArgs("\"" + path + "\" is does not exist");
    }

    /**
     * Load data from file into an object
     *
     * @param filepath data file path
     * @return list of all the loaded data
     * @throws IOException File path is bad
     * @throws LabException.BadDatFile Unable to read the data file
     */
    private static List<Data> loadData(String filepath) throws IOException, LabException.BadDatFile {

        // get initial weight
        Path path = Paths.get(filepath);
        double initialWeight = 1.0 / Files.lines(path).parallel().count();

        // init vars
        BufferedReader br = new BufferedReader(new FileReader(filepath));
        String line = br.readLine();
        List<Data> dataList = new ArrayList<>();

        // Create list of data objects
        while (line != null){
            dataList.add(new Data(line, initialWeight));
            line = br.readLine();
        }
        br.close();

        return dataList;
    }

    /**
     * Generate a decision tree to a file with or without ada boosting
     *
     * @param examples file of training data
     * @param hypothesisOut file to write model to
     * @param learningType dt: generate decision tree | ada: generate decision tree and adaboost
     */
    public static void train(String examples, String hypothesisOut, String learningType){
        // Attempt to load the data from file
        List<Data> dataList;
        try{
            dataList = loadData(examples);
        } catch (Exception e){
            System.err.println("Failed to load .dat file | Msg: " + e.getMessage());
            return;
        }

        // Populate the features list
        List<Feature> features = new ArrayList<>(){
            {
                add(new tCount());
                add(new nCount());
                add(new enArticles());
                add(new nlArticles());
                add(new FreqE());
            }
        };

        // both dt and ada need tree, build
        Node dtRoot = Node.buildTree(new ArrayList<>(dataList), new ArrayList<>(features));

        // adaboost if given
        if(learningType.equals("ada")){
            assert dtRoot != null;
            dtRoot.adaBoost();
        }

        // Try to write Decision Tree to file
        try{
            assert dtRoot != null;
            dtRoot.serialize(hypothesisOut);
        } catch (Exception e){
            System.err.println("Failed to write to file | Msg: " + e.getMessage());
        }

        predict(hypothesisOut, examples);
    }

    /**
     * Given a Decision Tree file, attempt to predict the language from a given file
     *
     * @param hypothesis path to decision tree model
     * @param file path with test data
     */
    public static void predict(String hypothesis, String file){

        // Try to load test data from file
        Node dtRoot;
        List<Data> dataList;
        try{
            dataList = loadData(file);
        } catch (Exception e){
            System.err.println("Failed to load .dat file | Msg: " + e.getMessage());
            return;
        }

        // Try to load decision tree from file
        try{
            dtRoot = Node.deSerialize(hypothesis);
        } catch (Exception e){
            System.err.println("Failed to load decision tree file | Msg: " + e.getMessage());
            return;
        }

        // todo better comment
        double count = dataList.size();
        double correct = 0;
        for(Data d : dataList){
            String out = dtRoot.predict(d);
             System.out.println( out + " : " + d);
//            System.out.println(out);
            Data.Language lang;
            if(out.equals("en")){
                lang = Data.Language.EN;
            } else {
                lang = Data.Language.NL;
            }

            if(d.matchLanguage(lang))
                correct++;
        }
        System.out.println(correct / count);
    }

    /**
     * Driver function
     *
     * @param args training or test args
     */
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
                }

                case "predict" -> {
                    // correct arg count
                    if (args.length != 3)
                        throw new LabException.BadArgs("Expected 2 arguments but got " + (args.length - 1));   // -1 for keyword

                    // check if valid file
                    assertFileExists(args[1]);

                    // assert sentence frags exist
                    assertFileExists(args[2]);
                }
                default -> throw new LabException.BadArgs("Unknown keyword, expected \"train\" or \"predict\" but got \"" + args[0] + "\"");
            }
        } catch (LabException.BadArgs e){
            // print error and break
            System.err.println("BadArgsError: " + e.getLocalizedMessage());
            System.err.println("Expected Usage: java lab3 train <examples> <hypothesisOut> <learning-type>");
            System.err.println("Expected Usage: java lab3 predict <hypothesis> <file>");
            return;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // OK, run based on keyword
        switch (args[0]){
            case "train" ->     train(args[1], args[2], args[3]);
            case "predict" ->   predict(args[1], args[2]);
        }
    }
}
