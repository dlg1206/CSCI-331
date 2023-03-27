import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * file: lab2.java
 * CSCI-331 Lab 2
 *
 * @author Derek Garcia
 **/
public class Lab2 {



    private static class Token {
        public enum tokenType{
            PREDICATE,
            VARIABLE,
            CONSTANT,
            FUNCTION,
            NEGATION,
            OPEN_PARENTHESIS,
            CLOSED_PARENTHESIS,
            COMMA,
            OR,
            AND
        }

        private final String value;
        private final tokenType type;

        public Token(tokenType type, String value){
            this.type = type;
            this.value = value;
        }



        public tokenType getType() {
            return this.type;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }

    private static class Clause {
        private ArrayList<Token> tokens = new ArrayList<>();


        public Clause(){
        }

        public void addToken(Token token){
            this.tokens.add(token);
        }

        public void negate(){

        }

        @Override
        public String toString() {
            String clause = "";
            for(Token token : this.tokens){
                clause += token;
            }
            return clause;
        }
    }


    private static boolean plResolution(ArrayList<Clause> kbClauses, Clause a){

        if(a != null){
            a.negate();
            kbClauses.add(a);
        }

        return false;
    }

    private static String unify(String x, String y, String o){
        return "";
    }

    private static String unifyVar(String var, String x, String o){
        return "";
    }


    private static ArrayList<Clause> getKnowledgeBase(String filepath) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(filepath));

        // Read in from file
        HashSet<String> predicates = toHashSet("Predicates:", br.readLine().split(" "));
        HashSet<String> variables = toHashSet("Variables:", br.readLine().split(" "));
        HashSet<String> constants = toHashSet("Constants:", br.readLine().split(" "));
        HashSet<String> functions = toHashSet("Functions:", br.readLine().split(" "));

        ArrayList<Clause> clauses = new ArrayList<>();

        br.readLine();
        String clauseString = br.readLine();

        while(clauseString != null){
            clauseString = clauseString.strip();
            StringBuilder value = new StringBuilder();
            Clause clause = new Clause();
            for(char c : clauseString.toCharArray()){

                // test if alphanumeric
                if((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')){
                    value.append(c);
                } else {

                    // end of id
                    Token.tokenType token = null;
                    if(predicates.contains(value.toString())){
                        token = Token.tokenType.PREDICATE;
                    } else if (variables.contains(value.toString())){
                        token = Token.tokenType.VARIABLE;
                    } else if (constants.contains(value.toString())){
                        token = Token.tokenType.CONSTANT;
                    } else if (functions.contains(value.toString())){
                        token = Token.tokenType.FUNCTION;
                    }

                    if(token != null){
                        clause.addToken(new Token(token, value.toString()));
                        value = new StringBuilder();
                    }
                    // test if single char
                    switch (c) {
                        case '!' -> clause.addToken(new Token(Token.tokenType.NEGATION, "!"));
                        case '(' -> clause.addToken(new Token(Token.tokenType.OPEN_PARENTHESIS, "("));
                        case ')' -> clause.addToken(new Token(Token.tokenType.CLOSED_PARENTHESIS, ")"));
                        case ',' -> clause.addToken(new Token(Token.tokenType.COMMA, ","));
                        case '^', ' ' -> clause.addToken(new Token(Token.tokenType.AND, " "));
                        case 'v' -> clause.addToken(new Token(Token.tokenType.OR, "v"));
                        default -> throw new Exception("Parsing Error, unknown string: " + value);
                    }
                }
            }

            clauses.add(clause);
            clauseString = br.readLine();
        }
        br.close();
        return clauses;
    }

    private static HashSet<String> toHashSet(String fieldID, String[] fileContents){
        HashSet<String> field = new HashSet<>();
        for(String val : fileContents){
            // skip id line
            if(val.equals(fieldID))
                continue;

            field.add(val);
        }
        return field;
    }


    public static void main(String[] args) {
        // Check for correct args
        if(args.length != 1){
            System.err.println("Incorrect Number of Arguments");
            System.err.println("Expected usage: java lab2 <path-to-KB.cnf>");
            return;
        }

        // Attempt to make load Knowledge Base from file
        ArrayList<Clause> clauses;
        try{
            clauses = getKnowledgeBase(args[0]);
        } catch (Exception e){
            System.err.println("Failed to load Knowledge Base \"" + args[0] + "\"");
            System.err.println("Reason: " + e.getLocalizedMessage());
            return;
        }

        for (Clause c : clauses)
            System.out.println(c);

//        plResolution(clauses, null);

    }
}
