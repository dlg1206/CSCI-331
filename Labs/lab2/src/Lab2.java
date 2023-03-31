import java.io.*;
import java.util.*;

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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Token token = (Token) o;

            if (type != token.type) return false;
            return Objects.equals(value, token.value);
        }

        @Override
        public int hashCode() {
            int result = type != null ? type.hashCode() : 0;
            result = 31 * result + (value != null ? value.hashCode() : 0);
            return result;
        }
    }

    private static class Clause {
        private Stack<Token> tokens;


        public Clause(Stack<Token> tokens){
            // remove extraneous or
            if(tokens.get(tokens.size() - 1).type == Token.tokenType.OR)
                tokens.remove(tokens.size() - 1);

            // add wrapper parentheses
//            tokens.add(0, new Token(Token.tokenType.OPEN_PARENTHESIS));
//            tokens.push(new Token(Token.tokenType.CLOSED_PARENTHESIS));

            this.tokens = tokens;
        }

        public void negate(){
            Stack<Token> negation = new Stack<>();

            while(!this.tokens.isEmpty()){
                Token curToken = this.tokens.remove(0);

                switch (curToken.type){
                    case PREDICATE -> {
                        // push negation if stack empty or top is not a double negation
                        if(negation.isEmpty() || negation.peek().type != Token.tokenType.NEGATION){
                            negation.push(new Token(Token.tokenType.NEGATION));
                        } else {
                            negation.pop();
                        }
                        negation.add(curToken);
                    }
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
            for(Token token : this.tokens)
                clause.append(token);

            return clause.toString();
        }
    }


    private static boolean plResolution(List<Clause> kbClauses){


        List<Clause> clauses = new ArrayList<>(kbClauses);

        List<Clause> newClauses = new ArrayList<>();

        for(;;){
            Clause ci = clauses.remove(0);
            for(Clause cj : clauses){
                List<Clause> resolvents = plResolve(ci, cj);
                if(resolvents.isEmpty())
                    return true;
                newClauses.addAll(resolvents);
            }
            if(clauses.contains(newClauses))
                return false;
            clauses.addAll(newClauses);

        }
    }

    private static List<Clause> plResolve(Clause ci, Clause cj){

        List<Clause> resolvents =  new ArrayList<>();
        for(Token token : ci.tokens){
            if(token.type == Token.tokenType.PREDICATE){
                // 1. check if other has same predicate
                if(cj.tokens.contains(token)){
                    // 2. check if negated
                    try{
                        for(int i = 0; i < cj.tokens.size(); i++){

                            if(token == cj.tokens.get(i) && cj.tokens.get(i - 1).type == Token.tokenType.NEGATION){

                            }


                        }
                    } catch (Exception e){

                    }
                }
            }

        }
        return null;

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
        HashSet<String> predicates = toHashSet(br.readLine().split(" "));
        HashSet<String> variables = toHashSet( br.readLine().split(" "));
        HashSet<String> constants = toHashSet( br.readLine().split(" "));
        HashSet<String> functions = toHashSet(br.readLine().split(" "));

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
                    continue;
                }

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

        plResolution(clauses);

    }
}
