import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * @author Derek Garcia
 **/

public class Lab2 {

    private static class Token {
        public enum tokenType {
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

        private final tokenType type;
        private final String value;

        public Token(tokenType type) {
            this.type = type;
            // assign value
            switch (this.type) {
                case OPEN_PARENTHESIS -> this.value = "(";
                case CLOSED_PARENTHESIS -> this.value = ")";
                case COMMA -> this.value = ", ";
                case NEGATION -> this.value = "¬";
                case OR -> this.value = " ∨ ";
                case AND -> this.value = " ∧ ";
                default -> this.value = "~";
            }
        }

        public Token(tokenType type, String value) {
            this.type = type;
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }

    // helper classes
    private static class Clause{
        public Clause(List<Predicate> predicates){

        }
    }
    
    private static class Predicate{
        private Stack<Token> tokens;
        public Predicate(Stack<Token> tokens){
            this.tokens = tokens;
        }

    }
    private class term{

    }

    private static HashSet<String> toHashSet(String[] array){
        List<String> tmp = new ArrayList<>(Arrays.asList(array));
        tmp.remove(0);  // remove line header
        return new HashSet<>(tmp);
    }

    // tokenize list
    public static List<Clause> loadFile(String filepath) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(filepath));

        Set<String> predicates = toHashSet(br.readLine().split(" "));
        Set<String> variables  = toHashSet(br.readLine().split(" "));
        Set<String> constants  = toHashSet(br.readLine().split(" "));
        Set<String> functions  = toHashSet(br.readLine().split(" "));

        List<Clause> clauses = new ArrayList<>();

        // trash "Clauses: " header and start with first line
        br.readLine();
        String clauseStr = br.readLine();

        // Foreach Clause in KB
        while(clauseStr != null) {



            // init vars
            StringBuilder value = new StringBuilder();
            Stack<Token> tokens = new Stack<>();
            List<Predicate> preds = new ArrayList<>();


            // Parse each character
            for (char c : clauseStr.toCharArray()) {

                // test if alphanumeric
                if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                    value.append(c);
                    continue;
                }

                Token.tokenType token = null;
                if (predicates.contains(value.toString())) {
                    token = Token.tokenType.PREDICATE;
                } else if (variables.contains(value.toString())) {
                    token = Token.tokenType.VARIABLE;
                } else if (constants.contains(value.toString())) {
                    token = Token.tokenType.CONSTANT;
                } else if (functions.contains(value.toString())) {
                    token = Token.tokenType.FUNCTION;
                }

                if (token != null) {
                    tokens.add(new Token(token, value.toString()));
                    value = new StringBuilder();
                }


                // Test current character
                switch (c) {
                    case '!' -> tokens.add(new Token(Token.tokenType.NEGATION));
                    case '(' -> tokens.add(new Token(Token.tokenType.OPEN_PARENTHESIS));
                    case ')' -> tokens.add(new Token(Token.tokenType.CLOSED_PARENTHESIS));
                    case ',' -> tokens.add(new Token(Token.tokenType.COMMA));
                    case ' ' -> {
                        preds.add(new Predicate(tokens));
                        tokens.clear();
                    }
                    // Unknown value
                    default -> {
                        br.close();
                        throw new Exception("Parsing Error, unknown string: " + value);
                    }
                }
            }

            clauses.add(new Clause(preds));
            clauseStr = br.readLine();
            
        }


        // close br and return clauses
        br.close();
        return clauses;

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
