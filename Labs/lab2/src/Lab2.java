import java.io.*;
import java.util.*;

/**
 * file: lab2.java
 * CSCI-331 Lab 2
 *
 * @author Derek Garcia
 **/
public class Lab2 {

    private static Set<String> PREDICATES;
    private static Set<String> VARIABLES;
    private static Set<String> CONSTANTS;
    private static Set<String> FUNCTIONS;


    private static class Token {
        public enum tokenType{
            ID,
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

    private static class Clause {
        private Stack<Token> tokens;


        public Clause(Stack<Token> tokens){
            // remove extraneous or
            if(tokens.get(tokens.size() - 1).type == Token.tokenType.OR)
                tokens.remove(tokens.size() - 1);

            this.tokens = tokens;
        }

        public void negate(){
            Stack<Token> negation = new Stack<>();

            while(!this.tokens.isEmpty()){
                Token curToken = this.tokens.remove(0);

                if(PREDICATES.contains(curToken.value)){
                    // push negation if stack empty or top is not a double negation
                    if(negation.isEmpty() || negation.peek().type != Token.tokenType.NEGATION){
                        negation.push(new Token(Token.tokenType.NEGATION));
                    } else {
                        negation.pop();
                    }
                }

                switch (curToken.type){
                    case AND -> negation.push(new Token(Token.tokenType.OR));
                    case OR -> negation.push(new Token(Token.tokenType.AND));
                    default -> negation.add(curToken);
                }


            }
            this.tokens = negation;

        }

        @Override
        public String toString() {
            StringBuilder clause = new StringBuilder();
            for(Token token : this.tokens){
                clause.append(token);
            }
            return clause.toString();
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


    private static List<Clause> getKnowledgeBase(String filepath) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(filepath));

        // Read in from file
        PREDICATES = toHashSet(br.readLine().split(" "));
        VARIABLES  = toHashSet(br.readLine().split(" "));
        CONSTANTS  = toHashSet(br.readLine().split(" "));
        FUNCTIONS  = toHashSet(br.readLine().split(" "));

        List<Clause> clauses = new ArrayList<>();

        // trash "Clauses: " header and start with first line
        br.readLine();
        String clauseString = br.readLine();

        // Foreach Clause in KB
        while(clauseString != null){

            // init vars
            StringBuilder value = new StringBuilder();
            Stack<Token> tokens = new Stack<>();

            // Parse each character
            for(char c : clauseString.toCharArray()){

                // test if alphanumeric
                if((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')){
                    value.append(c);
                } else {

                    // End of ID. Add new ID if one is present
                    if(!value.isEmpty()){
                        tokens.add(new Token(Token.tokenType.ID, value.toString()));
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
            }

            // add clause and get next string
            clauses.add(new Clause(tokens));
            clauseString = br.readLine();
        }
        // close br and return KB
        br.close();
        return clauses;
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
        List<Clause> clauses;
        try{
            clauses = getKnowledgeBase(args[0]);
        } catch (Exception e){
            System.err.println("Failed to load Knowledge Base \"" + args[0] + "\"");
            System.err.println("Error: " + e);
            return;
        }

        for (Clause c : clauses){
            System.out.println("before negation: " + c);
            c.negate();
            System.out.println("after negation: "  + c);
        }




    }
}
