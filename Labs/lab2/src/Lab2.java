import java.util.List;

/**
 * @author Derek Garcia
 **/

public class Lab2 {

    // helper classes
    private class Clause{

    }
    private class Predicate{

    }
    private class term{

    }

    public static List<Clause> loadFile(String filePath){
        return null;
    }

    public static void main(String[] args) {
        // Check for correct args
        if(args.length != 1){
            System.err.println("Incorrect Number of Arguments");
            System.err.println("Expected usage: java lab2 <path-to-KB.cnf>");
            return;
        }

        List<Clause> clauses;
        try{
            clauses = loadFile(args[0]);
        } catch (Exception e){
            System.err.println("Failed to load Knowledge Base \"" + args[0] + "\"");
            System.err.println("Error: " + e);
            return;
        }

    }
}
