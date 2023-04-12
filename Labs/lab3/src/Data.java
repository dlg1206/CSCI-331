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
                if((c < 'a' || c > 'z'))
                    continue;
                if(!tmp.containsKey(c))
                    tmp.put(c, new Letter(c));
                tmp.get(c).increment();
                this.numChars++;
            }
        }
        ArrayList<Letter> sorted = new ArrayList<>(tmp.values());
        Collections.sort(sorted);
        this.letters = new ArrayList<>(sorted);
    }

    public double getFrequency(char c) {
        int index = getCountIndex(c);
        if (index == -1)
            return 0;

        return this.letters.get(index).occurrences / this.numChars;
    }

    public int getCountIndex(char c) {
        for(int i = 0; i < this.letters.size(); i++){
            if(this.letters.get(i).value == c)
                return i + 1;
        }
        return -1;
    }

    public boolean matchLanguage(Language l) {
        return this.trainingLang == l;
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
