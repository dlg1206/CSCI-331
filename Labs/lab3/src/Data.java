import java.util.*;

/**
 * @author Derek Garcia
 **/

public class Data {

    public enum Language {
        EN,
        NL
    }
    private class Letter implements Comparable<Letter>{
        private char value;
        private double occurrences = 0;
        public Letter(char c){
            this.value = c;
        }

        public void increment(){this.occurrences++;}

        public double getOccurrences() {
            return this.occurrences;
        }



        @Override
        public String toString() {
            return this.value + ":" + (int) this.occurrences;
        }

        @Override
        public int compareTo(Letter o) {
            return (int) (o.occurrences - this.occurrences);
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof Letter other){
                return this.value == other.value;
            }
            if(obj instanceof Character other){
                return this.value == other;
            }
            return false;
        }
    }

    private Language trainingLang;
    private final List<Letter> letters;
    private final LinkedHashSet<String> words;
    private double numChars = 0;


    public Data(String fragment) throws LabException.BadDatFile {

        String[] components = fragment.split("\\|");
        if(components.length == 2){
            switch (components[0]){
                case "en" -> this.trainingLang = Language.EN;
                case "nl" -> this.trainingLang = Language.NL;
                default -> throw new LabException.BadDatFile("Unknown language prefix, expected \"en\" or \"nl\" but got \"" + components[0] +"\"");
            }
            this.words = new LinkedHashSet<>(List.of(components[1].split(" ")));
        } else {
            this.words = new LinkedHashSet<>(List.of(fragment.split(" ")));
        }

        HashMap<Character, Letter> tmp = new HashMap<>();
        for(String word : this.words){
            for(char c : word.toLowerCase().toCharArray()){
                if(!tmp.containsKey(c))
                    tmp.put(c, new Letter(c));
                tmp.get(c).increment();
                this.numChars++;
            }
        }
        this.letters = new ArrayList<>(tmp.values());
        Collections.sort(this.letters);
    }

    public double getFrequency(char c) {
        if (!this.letters.contains(c))
            return 0;
        int index = this.letters.indexOf(c);

        return this.letters.get(index).occurrences / this.numChars;
    }

    public char getMostCommonChar() {
        return this.letters.get(0).value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(String word : words){
            sb.append(word).append(" ");
        }
        return sb.toString().trim();
    }

}
