import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Derek Garcia
 **/

public abstract class FeatureTest {

    protected Set<Data> isEN;
    protected Set<Data> isNotEN;
    protected double numENCorrect;    // number of correctly identified EN phrases
    protected double numENIncorrect;  // number of incorrectly identified EN phrases

    private double log2(double n){
        if(n == 0)
            return 0;
        return Math.log(n) / Math.log(2);
    }

    // B function
    private double B(double q){

        return -(q * log2(q) + (1 - q) * log2(1 - q));
    }

    // perform remainder calc for booleans
    public double getRemainder(List<Data> dataList){
        // reset any stored values
        this.isEN = new HashSet<>();
        this.isNotEN = new HashSet<>();
        this.numENCorrect = 0;
        this.numENIncorrect = 0;

        for(Data d : dataList){
            if( isEnglish(d) ){
                this.isEN.add(d);
                if(d.matchLanguage(Data.Language.EN))
                    this.numENCorrect++;
            } else {
                this.isNotEN.add(d);
                if(d.matchLanguage(Data.Language.EN))
                    this.numENIncorrect++;
            }
        }
        // (numTrue / total) * B(numA / numTrue) + (numFalse / total) * B(numNotA / numFalse)
        // (numTrue / total)
        double a = ((double) this.isEN.size() / dataList.size());
        // B(numA / numTrue)
        double b = B(this.numENCorrect / this.isEN.size());
        // (numFalse / total)
        double c = ((double) this.isNotEN.size() / dataList.size());
        // B(numNotA / numFalse)
        double e = B(this.numENIncorrect / this.isNotEN.size());
        return ((double) this.isEN.size() / dataList.size()) * B(this.numENCorrect / this.isEN.size()) +
                ((double) this.isNotEN.size() / dataList.size()) * B(this.numENIncorrect / this.isNotEN.size());
    }

    protected abstract boolean isEnglish(Data data);
    protected abstract String getTestName();

    @Override
    public String toString() {
        return getTestName();
    }
}
class FreqE extends FeatureTest{

    @Override
    protected boolean isEnglish(Data data) {
        return data.getFrequency('c') < 0.15;
    }

    @Override
    protected String getTestName() {
        return "# e's > 15% word, T:nl, F:en, approx 17/13 respectively";
    }
}

class tCount extends FeatureTest{

    @Override
    protected boolean isEnglish(Data data) {
        return data.getCountIndex('t') == 2;
    }

    @Override
    protected String getTestName() {
        return "'t' is 2nd most common letter";
    }
}

class nCount extends FeatureTest{

    @Override
    protected boolean isEnglish(Data data) {
        return data.getCountIndex('n') != 2;
    }

    @Override
    protected String getTestName() {
        return "# highest number percent is \"n\", T:nl F:en";
    }
}

class enArticles extends FeatureTest {
    private final List<String> articles = new ArrayList<>() {
        {
            add("the");
            add("an");
            add("a");
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

class nlArticles extends FeatureTest {

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

