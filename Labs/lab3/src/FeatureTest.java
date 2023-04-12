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
        return Math.log(n) / Math.log(2);
    }

    // B function
    private double B(double q){
        if(q == 0)
            return 0;
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
            applyTest(d);
        }
        // (numTrue / total) * B(numA / numTrue) + (numFalse / total) * B(numNotA / numFalse)
        double remainder = ((double) this.isEN.size() / dataList.size()) * B(this.numENCorrect / this.isEN.size()) +
                ((double) this.isNotEN.size() / dataList.size()) * B(this.numENIncorrect / this.isNotEN.size());

        return remainder;
    }

    protected abstract void applyTest(Data data);

}

class FeatureFreqT extends FeatureTest{

    @Override
    protected void applyTest(Data data) {
        if(data.getCountIndex('t') ==2 ){
            this.isEN.add(data);
            if(data.matchLanguage(Data.Language.EN))
                this.numENCorrect++;
        } else {
            this.isNotEN.add(data);
            if(data.matchLanguage(Data.Language.EN))
                this.numENIncorrect++;
        }
    }
}
