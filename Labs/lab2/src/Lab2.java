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

    private static class KnowledgeBase{

        private class Token{
            public enum tokenType{
                ID,
                NEGATION,
                OPEN_PARENTHESIS,
                CLOSED_PARENTHESIS,
                COMMA,
                OR
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

        private class Clause{
            private final ArrayList<Token> tokens = new ArrayList<>();


            public Clause(){
            }

            public void addToken(Token token){
                this.tokens.add(token);
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


        private HashSet<String> predicates = new HashSet<>();
        private HashSet<String> variables = new HashSet<>();
        private HashSet<String> constants = new HashSet<>();
        private HashSet<String> functions = new HashSet<>();
        private ArrayList<Clause> clauses = new ArrayList<>();

        public KnowledgeBase(String filepath) throws Exception {
            BufferedReader br = new BufferedReader(new FileReader(filepath));

            updateKnowledgeBase("Predicates:", this.predicates, br.readLine().split(" "));
            updateKnowledgeBase("Variables:", this.variables, br.readLine().split(" "));
            updateKnowledgeBase("Constants:", this.constants, br.readLine().split(" "));
            updateKnowledgeBase("Functions:", this.functions, br.readLine().split(" "));

            br.readLine();

            String clauseString = br.readLine();
            while(clauseString != null){
                clauseString = clauseString.strip();
                String value = "";
                Clause clause = new Clause();
                for(char c : clauseString.toCharArray()){

                    // test if alphanumeric
                    if((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')){
                        value += c;
                        continue;
                    } else {
                        // end of id
                        clause.addToken(new Token(Token.tokenType.ID, value));
                        value = "";
                    }

                    // test if single char
                    switch (c) {
                        case '!' -> clause.addToken(new Token(Token.tokenType.NEGATION, "!"));
                        case '(' -> clause.addToken(new Token(Token.tokenType.OPEN_PARENTHESIS, "("));
                        case ')' -> clause.addToken(new Token(Token.tokenType.CLOSED_PARENTHESIS, ")"));
                        case ',' -> clause.addToken(new Token(Token.tokenType.COMMA, ","));
                        case ' ' -> clause.addToken(new Token(Token.tokenType.OR, "^"));
                        default -> throw new Exception("Unrecognized character: " + c);
                    }
                }
                this.clauses.add(clause);
                System.out.println(clause);

                clauseString = br.readLine();
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

        public boolean isPredicate(Token token){
            return this.predicates.contains(token.toString());
        }

        public boolean isVariable(Token token){
            return this.variables.contains(token.toString());
        }

        public boolean isFunction(Token token){
            return this.functions.contains(token.toString());
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
