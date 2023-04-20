import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * file: Feature.java
 * Feature template and its implementations
 *
 * @author Derek Garcia
 **/
public abstract class Feature implements Comparable<Feature>, Serializable {

    protected Set<Data> isEN;
    protected Set<Data> isNL;
    protected double numENCorrect;    // number of correctly identified EN phrases
    protected double numENIncorrect;  // number of incorrectly identified EN phrases
    private double remainder = -1;

    /**
     * Utility Log base 2
     * @param n arg
     * @return log2(number)
     */
    private double log2(double n){
        if(n == 0)
            return 0;
        return Math.log(n) / Math.log(2);
    }

    /**
     * Binary entropy equation
     *
     * @param part Top
     * @param whole bottom
     * @return Binary entropy
     */
    private double B(double part, double whole){

        // short circuit checks
        // B(1/0) = 0 and B(1) = 0
        if(whole == 0 || (part / whole == 1))
            return 0;
        // else do equation
        double q = part / whole;
        return -(q * log2(q) + (1 - q) * log2(1 - q));
    }

    /**
     * Get the remainder
     *
     * @param dataList list of data to check
     */
    public void getRemainder(List<Data> dataList){
        // reset any stored values
        this.isEN = new HashSet<>();
        this.isNL = new HashSet<>();
        this.numENCorrect = 0;
        this.numENIncorrect = 0;

        // Test the feature against each data
        for(Data d : dataList){
            // Feature reports english
            if( isEnglish(d) ){
                this.isEN.add(d);
                // Test was correct
                if(d.matchLanguage(Data.Language.EN))
                    this.numENCorrect++;
            } else {
                // Feature reports dutch
                this.isNL.add(d);
                // Test was wrong
                if(d.matchLanguage(Data.Language.EN))
                    this.numENIncorrect++;
            }
        }
        // Calculate remainder
        // (numTrue / total) * B(numA / numTrue) + (numFalse / total) * B(numNotA / numFalse)
        this.remainder = ((double) this.isEN.size() / dataList.size()) * B(this.numENCorrect, this.isEN.size()) +
                ((double) this.isNL.size() / dataList.size()) * B(this.numENIncorrect, this.isNL.size());
    }

    // getters
    public Set<Data> getIsEN(){
        return this.isEN;
    }
    public Set<Data> getIsNL(){
        return this.isNL;
    }

    /**
     * Check if data is English or not
     *
     * @param data data to check
     * @return true if en, false otherwise
     */
    protected abstract boolean isEnglish(Data data);

    /**
     * Get the name of the test
     * @return name of test
     */
    protected abstract String getTestName();


    // compare remainders
    @Override
    public int compareTo(Feature o) {
        return (int) (1000 * this.remainder - 1000 * o.remainder);  // shift 1000 int doesn't go to 0
    }

    @Override
    public String toString() {
        return getTestName();
    }
}


