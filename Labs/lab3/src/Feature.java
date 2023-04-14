import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Derek Garcia
 **/

public abstract class Feature implements Comparable<Feature>, Serializable {

    protected Set<Data> isEN;
    protected Set<Data> isNL;
    protected double numENCorrect;    // number of correctly identified EN phrases
    protected double numENIncorrect;  // number of incorrectly identified EN phrases
    private double remainder = -1;

    private double log2(double n){
        if(n == 0)
            return 0;
        return Math.log(n) / Math.log(2);
    }

    // B function
    private double B(double part, double whole){

        // short circuit checks
        // B(1/0) = 0 and B(1) = 0
        if(whole == 0 || (part / whole == 1))
            return 0;
        double q = part / whole;
        return -(q * log2(q) + (1 - q) * log2(1 - q));
    }

    // perform remainder calc for booleans
    public void getRemainder(List<Data> dataList){
        // reset any stored values
        this.isEN = new HashSet<>();
        this.isNL = new HashSet<>();
        this.numENCorrect = 0;
        this.numENIncorrect = 0;

        for(Data d : dataList){
            if( isEnglish(d) ){
                this.isEN.add(d);
                if(d.matchLanguage(Data.Language.EN))
                    this.numENCorrect++;
            } else {
                this.isNL.add(d);
                if(d.matchLanguage(Data.Language.EN))
                    this.numENIncorrect++;
            }
        }
        // (numTrue / total) * B(numA / numTrue) + (numFalse / total) * B(numNotA / numFalse)
        this.remainder = ((double) this.isEN.size() / dataList.size()) * B(this.numENCorrect, this.isEN.size()) +
                ((double) this.isNL.size() / dataList.size()) * B(this.numENIncorrect, this.isNL.size());
    }

    public Set<Data> getIsEN(){
        return this.isEN;
    }
    public Set<Data> getIsNL(){
        return this.isNL;
    }

    protected abstract boolean isEnglish(Data data);
    protected abstract String getTestName();

    @Override
    public int compareTo(Feature o) {
        return (int) (1000 * this.remainder - 1000 * o.remainder);
    }

    @Override
    public String toString() {
        return getTestName();
    }
}
class FreqE extends Feature {

    @Override
    protected boolean isEnglish(Data data) {
        return data.getFrequency('e') < 0.15;
    }

    @Override
    protected String getTestName() {
        return "# e's > 15% word, T:nl, F:en, approx 17/13 respectively";
    }
}

class tCount extends Feature {

    @Override
    protected boolean isEnglish(Data data) {
        return data.getCountIndex('t') == 2;
    }

    @Override
    protected String getTestName() {
        return "'t' is 2nd most common letter";
    }
}

class nCount extends Feature {

    @Override
    protected boolean isEnglish(Data data) {
        return data.getCountIndex('n') != 2;
    }

    @Override
    protected String getTestName() {
        return "# highest number percent is \"n\", T:nl F:en";
    }
}

class enArticles extends Feature {
    private final List<String> articles = new ArrayList<>() {
        {
            add("the");
            add("an");
        }
    };

    @Override
    protected boolean isEnglish(Data data) {
        for (String word : this.articles)
            if (data.containsWord(word))
                return true;
        return false;
    }

    @Override
    protected String getTestName() {
        return "en articles (the, an, a), T:en F:nl";
    }
}

class nlArticles extends Feature {

    private final List<String> articles = new ArrayList<>() {
        {
            add("de");
            add("het");
            add("een");
            add("der");
            add("des");
            add("den");
        }
    };

    @Override
    protected boolean isEnglish(Data data) {
        for (String word : this.articles)
            if (data.containsWord(word))
                return false;
        return true;
    }

    @Override
    protected String getTestName() {
        return "nl articles, f:en t:nl";
    }
}

