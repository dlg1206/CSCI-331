import java.io.BufferedReader;
import java.io.FileReader;
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
        private List<Predicate> predicates;
        public Clause(List<Predicate> predicates){
            this.predicates = new ArrayList<>(predicates);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Clause other = (Clause) o;
            return this.toString().equals(other.toString());
        }

        @Override
        public String toString() {
            StringBuilder string = new StringBuilder();
            for(Predicate p : predicates)
                string.append(p).append(" ");
            return string.toString();
        }

    }
    
    private static class Predicate{
        private boolean isNegated = false;
        private final String id;
        private final List<Term> arguments = new ArrayList<>();
        public Predicate(List<Token> tokens){

            // check if negated
            if(tokens.get(0).type == Token.tokenType.NEGATION){
                this.isNegated = true;
                tokens.remove(0);
            }

            this.id = tokens.get(0).value;
            tokens.remove(0);

            // Parse arguments
            while (!tokens.isEmpty()){
                Token curToken = tokens.remove(0);
                switch (curToken.type){
                    case CONSTANT -> this.arguments.add(new Constant(curToken.value));
                    case VARIABLE -> this.arguments.add(new Variable(curToken.value));
                    case FUNCTION -> this.arguments.add(new Function(curToken.value, tokens));
                }

            }
        }

        public boolean isComplement(Predicate other){
            return this.isNegated != other.isNegated && this.id.equals(other.id) && this.arguments.equals(other.arguments);
        }


        @Override
        public String toString() {
            StringBuilder string = new StringBuilder();

            if(this.isNegated) string.append('!');

            string.append(this.id);

            if(!this.arguments.isEmpty()) string.append("(");

            for(int i = 0; i < this.arguments.size(); i++){
                string.append(this.arguments.get(i));

                if(i + 1 < this.arguments.size()) string.append(",");
            }
            if(!this.arguments.isEmpty()) string.append(")");
            return string.toString();
        }
    }

    private static abstract class Term {
    }

    private static class Constant extends Term{
        private final String id;
        public Constant(String id){
            this.id = id;
        }
        @Override
        public String toString() {
            return this.id;
        }
    }

    private static class Variable extends Term{
        private final String id;
        // value?
        public Variable(String id){
            this.id = id;
        }
        @Override
        public String toString() {
            return this.id;
        }
    }

    private static class Function extends Term{
        private final String id;
        private final List<Term> arguments = new ArrayList<>();
        public Function(String id, List<Token> tokens) {
            this.id = id;

            int openCount = 1;
            int closeCount = 0;
            tokens.remove(0);
            // Parse arguments
            while (openCount != closeCount){
                Token curToken = tokens.remove(0);
                switch (curToken.type){
                    case CONSTANT -> this.arguments.add(new Constant(curToken.value));
                    case VARIABLE -> this.arguments.add(new Variable(curToken.value));
                    case FUNCTION -> this.arguments.add(new Function(curToken.value, tokens));
                    case OPEN_PARENTHESIS -> openCount++;
                    case CLOSED_PARENTHESIS -> closeCount++;
                }

            }
        }

        @Override
        public String toString() {
            StringBuilder string = new StringBuilder();

            string.append(this.id);

            string.append("(");

            for(int i = 0; i < this.arguments.size(); i++){
                string.append(this.arguments.get(i));

                if(i + 1 < this.arguments.size()) string.append(",");
            }
            string.append(")");
            return string.toString();
        }

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
            List<Token> tokens = new ArrayList<>();
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

    public static boolean plResolution(List<Clause> knowledgeBase){

        List<Clause> clauses = new ArrayList<>(knowledgeBase);
        Set<Clause> n_ew = new LinkedHashSet<>();
        for(;;){
            List<Clause> clausesList = new ArrayList<>(clauses);
            for (int i = 0; i < clausesList.size() - 1; i++) {
                Clause ci = clausesList.get(i);
                for (int j = i + 1; j < clausesList.size(); j++) {
                    Clause cj = clausesList.get(j);

                    List<Clause> resolvents = resolve(ci, cj);
//                    System.out.println("[ " + ci + " ] + [ " + cj + "] => ");
                    for(Clause c : resolvents){
//                        System.out.println("\t" + c);
                        if(c.predicates.isEmpty()) return false;
                    }

                    n_ew.addAll(resolvents);
                }
            }
            // if new is subset of clauses then return false
            if (clauses.containsAll(n_ew)) return true;

            // clauses <- clauses U new
            clauses.addAll(n_ew);
        }

    }

    public static List<Clause> resolve(Clause ci, Clause cj){
        List<Clause> result = new ArrayList<>();
        List<Predicate> union = new ArrayList<>(ci.predicates);
        union.addAll(cj.predicates);
        for(Predicate pi : ci.predicates){
            for(Predicate pj : cj.predicates){
                if(pi.isComplement(pj)){
                    List<Predicate> p = new ArrayList<>(union);
                    p.remove(pi);
                    p.remove(pj);
                    result.add(new Clause(p));
                }
            }
        }
        return result;
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


        if(plResolution(clauses)){
            System.out.println("yes");
        } else {
            System.out.println("no");
        }

    }
}
