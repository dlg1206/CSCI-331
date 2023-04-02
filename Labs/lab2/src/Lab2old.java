import java.io.*;
import java.util.*;

/**
 * file: lab2.java
 * CSCI-331 Lab 2
 *
 * @author Derek Garcia
 **/
public class Lab2old {

    private static HashSet<String> PREDICATES;
    private static HashSet<String> VARIABLES;
    private static HashSet<String> CONSTANTS;
    private static HashSet<String> FUNCTIONS;

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
        private final tokenType type;
        private final String value;

        public Token(tokenType type){
            this.type = type;
            // assign value
            switch (this.type) {
                case OPEN_PARENTHESIS -> this.value = "(";
                case CLOSED_PARENTHESIS -> this.value = ")";
                case COMMA -> this.value = ", ";
                case NEGATION -> this.value = "¬";
                case OR -> this.value = " ∨ ";
                case AND -> this.value = " ∧ ";
                default -> this.value =  "~";
            }
        }
        public Token(tokenType type, String value){
            this.type = type;
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
        
    }

    private static class Literal {
        private Stack<Token> tokens;
        private boolean isNegated = false;


        public Literal(Stack<Token> tokens){
            // remove extraneous or
            if(tokens.get(tokens.size() - 1).type == Token.tokenType.OR)
                tokens.remove(tokens.size() - 1);

            // add wrapper parentheses
//            tokens.add(0, new Token(Token.tokenType.OPEN_PARENTHESIS));
//            tokens.push(new Token(Token.tokenType.CLOSED_PARENTHESIS));

            this.tokens = tokens;

        }
    }



    private static boolean plResolution(List<Literal> kbClauses){


        List<Literal> clauses = new ArrayList<>(kbClauses);

        List<Literal> newClauses = new ArrayList<>();

        for(;;){
            Literal ci = clauses.remove(0);
            for(Literal cj : clauses){
                List<Literal> resolvents = plResolve(ci, cj);
                if(resolvents.isEmpty())
                    return true;
                newClauses.addAll(resolvents);
            }
            if(clauses.contains(newClauses))
                return false;
            clauses.addAll(newClauses);

        }
    }

    private static List<Literal> plResolve(Literal ci, Literal cj){

        List<Literal> resolvents =  new ArrayList<>();

        if(ci.toString().contains(cj.toString())){
            int a =0;
        }

        if(cj.toString().contains(ci.toString())){
            int a =0;
        }

        return null;

    }

    private static String unify(String x, String y, String o){
        return "";
    }

    private static String unifyVar(String var, String x, String o){
        return "";
    }


    private static List<Literal> getKnowledgeBase(String filepath) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(filepath));

        // Read in from file
        PREDICATES = toHashSet(br.readLine().split(" "));
        VARIABLES = toHashSet(br.readLine().split(" "));
        CONSTANTS = toHashSet(br.readLine().split(" "));
        FUNCTIONS = toHashSet(br.readLine().split(" "));

        List<Literal> clause = new ArrayList<>();

        // trash "Clauses: " header and start with first line
        br.readLine();
        String literalString = br.readLine();

        // Foreach Clause in KB
        while(literalString != null){

            // init vars
            StringBuilder value = new StringBuilder();
            Stack<Token> tokens = new Stack<>();

            // Parse each character
            for(char c : literalString.toCharArray()){

                // test if alphanumeric
                if((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')){
                    value.append(c);
                    continue;
                }

                Token.tokenType token = null;
                if(PREDICATES.contains(value.toString())){
                    token = Token.tokenType.PREDICATE;
                } else if (VARIABLES.contains(value.toString())){
                    token = Token.tokenType.VARIABLE;
                } else if (CONSTANTS.contains(value.toString())){
                    token = Token.tokenType.CONSTANT;
                } else if (FUNCTIONS.contains(value.toString())){
                    token = Token.tokenType.FUNCTION;
                }

                if(token != null){
                    tokens.add(new Token(token, value.toString()));
                    value = new StringBuilder();
                }


                // Test current character
                switch (c) {
                    case '!' -> tokens.add(new Token(Token.tokenType.NEGATION));
                    case '(' -> tokens.add(new Token(Token.tokenType.OPEN_PARENTHESIS));
                    case ')' -> tokens.add(new Token(Token.tokenType.CLOSED_PARENTHESIS));
                    case ',' -> tokens.add(new Token(Token.tokenType.COMMA));
                    case ' ' -> tokens.add(new Token(Token.tokenType.OR));
                    // Unknown value
                    default -> {
                        br.close();
                        throw new Exception("Parsing Error, unknown string: " + value);
                    }
                }

            }

            // add clause and get next string
            clause.add(new Literal(tokens));
            literalString = br.readLine();
        }
        // close br and return KB
        br.close();
        return clause;
    }

    private static HashSet<String> toHashSet(String[] array){
        List<String> tmp = new ArrayList<>(Arrays.asList(array));
        tmp.remove(0);  // remove line header
        return new HashSet<>(tmp);
    }


    public static void main(String[] args) {
        // Check for correct args
        if(args.length != 1){
            System.err.println("Incorrect Number of Arguments");
            System.err.println("Expected usage: java lab2 <path-to-KB.cnf>");
            return;
        }

        // Attempt to make load Knowledge Base from file
        List<Literal> clauses;
        try{
            clauses = getKnowledgeBase(args[0]);
        } catch (Exception e){
            System.err.println("Failed to load Knowledge Base \"" + args[0] + "\"");
            System.err.println("Error: " + e);
            return;
        }

        plResolution(clauses);

    }
}
