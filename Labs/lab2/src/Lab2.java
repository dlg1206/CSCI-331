import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Predicate;

/**
 * file: lab2.java
 * CSCI-331 Lab 2
 *
 * @author Derek Garcia
 **/
public class Lab2 {

    private static class KnowledgeBase{


        private HashSet<String> Predicates = new HashSet<>();
        private HashSet<String> Variables = new HashSet<>();
        private HashSet<String> Constants = new HashSet<>();
        private HashSet<String> Functions = new HashSet<>();
        private ArrayList<String> Clauses = new ArrayList<>();

        public KnowledgeBase(String filepath) throws IOException {
            BufferedReader br = new BufferedReader(new FileReader(filepath));

            updateKnowledgeBase("Predicates:", this.Predicates, br.readLine().split(" "));
            updateKnowledgeBase("Variables:", this.Variables, br.readLine().split(" "));
            updateKnowledgeBase("Constants:", this.Constants, br.readLine().split(" "));
            updateKnowledgeBase("Functions:", this.Functions, br.readLine().split(" "));

            br.readLine();

            String clause = br.readLine();
            while(clause != null){
                this.Clauses.add(clause);
                clause = br.readLine();
            }
            br.close();
        }

        private void updateKnowledgeBase(String fieldID, HashSet<String> field, String[] fileContents){

            for(String val : fileContents){
                // skip id line
                if(val.equals(fieldID))
                    continue;

                field.add(val);
            }
        }
    }

    private static boolean plResolution(KnowledgeBase kb, String a){
        return false;
    }

    private static String unify(String x, String y, String o){
        return "";
    }

    private static String unifyVar(String var, String x, String o){
        return "";
    }


    public static void main(String[] args) {
        // Check for correct args
        if(args.length != 1){
            System.err.println("Incorrect Number of Arguments");
            System.err.println("Expected usage: java lab2 <path-to-KB.cnf>");
            return;
        }

        // Attempt to make load Knowledge Base from file
        KnowledgeBase kb;
        try{
            kb = new KnowledgeBase(args[0]);
        } catch (Exception e){
            System.err.println("Failed to load Knowledge Base \"" + args[0] + "\"");
            System.err.println("Reason: " + e.getLocalizedMessage());
            return;
        }

        plResolution(kb, "");

    }
}
