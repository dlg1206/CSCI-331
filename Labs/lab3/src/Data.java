import java.io.Serializable;
import java.util.*;

/**
 * file: Data.java
 * Object representation of Data input
 *
 * @author Derek Garcia
 **/
public class Data implements Serializable {

    // Enums for language
    public enum Language {
        EN,
        NL
    }

    /**
     * Internal utility class for sorting the characters in order of occurrence
     */
    private static class Letter implements Comparable<Letter>, Serializable{
        private final char value;
        private double occurrences = 0;
        public Letter(char c){
            this.value = c;
        }

        public void increment(){this.occurrences++;}

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
    private double weight;
    private boolean isCorrect = true;


    /**
     * Create a new Data object from a sentence fragment and a given seed weight
     *
     * @param fragment Sentence in a language
     * @param weight Initial weight
     * @throws LabException.BadDatFile Unable to parse fragment into Data object
     */
    public Data(String fragment, double weight) throws LabException.BadDatFile {

        this.weight = weight;

        // If training file, set language
        String[] components = fragment.split("\\|");
        if(components.length == 2){
            // assign language
            switch (components[0]){
                case "en" -> this.trainingLang = Language.EN;
                case "nl" -> this.trainingLang = Language.NL;
                default -> throw new LabException.BadDatFile("Unknown language prefix, expected \"en\" or \"nl\" but got \"" + components[0] +"\"");
            }
            // split sentence into words
            this.words = new LinkedHashSet<>(List.of(components[1].toLowerCase().split(" ")));
        } else {
            // just split sentence into words
            this.words = new LinkedHashSet<>(List.of(fragment.toLowerCase().split(" ")));
        }

        // count all the character occurrences
        HashMap<Character, Letter> tmp = new HashMap<>();
        for(String word : this.words){
            for(char c : word.toLowerCase().toCharArray()){
                // skip if not letter
                if((c < 'a' || c > 'z'))
                    continue;
                if(!tmp.containsKey(c))
                    tmp.put(c, new Letter(c));
                tmp.get(c).increment();
                this.numChars++;
            }
        }

        // Get Letters and sort them
        this.letters = new ArrayList<>(tmp.values());
        Collections.sort(this.letters);
    }

    /**
     * number times character occurs / total characters
     *
     * @param c character
     * @return portion of whole
     */
    public double getPercent(char c) {
        // get index of c
        int index = getFrequencyIndex(c);
        if (index == -1)
            return 0;

        // attempt to get frequency
        try{
            return this.letters.get(index).occurrences / this.numChars;
        } catch (Exception e){
            // some math error
            return 0;
        }
    }

    /**
     * Get the index of a character's frequency
     *
     * @param c character
     * @return index of character
     */
    public int getFrequencyIndex(char c) {

        // Parse through letters to until find target character
        for(int i = 0; i < this.letters.size(); i++){
            if(this.letters.get(i).value == c)
                return i;
        }
        // character wasn't found
        return -1;
    }

    /**
     * Check if this data contains a given word
     *
     * @param word word to search for
     * @return true if has word, false otherwise
     */
    public boolean containsWord(String word){
        return this.words.contains(word);
    }


    /**
     * Check if this data language matches the given language
     *
     * @param l language
     * @return true if match, false otherwise
     */
    public boolean matchLanguage(Language l) {
        return this.trainingLang == l;
    }

    /**
     * Get current weight of data
     * @return weight of data
     */
    public double getWeight() {
        return this.weight;
    }

    /**
     * Update data weight by a given factor
     *
     * @param factor factor to update weight with
     * @return updated weight
     */
    public double updateWeight(double factor){
        this.weight = this.weight * factor;
        return this.weight;
    }

    /**
     * @return if this was corrected identified
     */
    public boolean isCorrect(){
        return this.isCorrect;
    }

    /**
     * Flag this data as incorrectly identified
     */
    public void flagError(){
        this.isCorrect = false;
    }

    public LinkedHashSet<String> getWords() {
        return this.words;
    }

    // return all the words stored
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(String word : words){
            sb.append(word).append(" ");
        }
        return sb.toString().trim();
    }

}
