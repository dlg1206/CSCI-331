import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Derek Garcia
 **/

public abstract class FeatureTest {

    protected static double log2(double n){
        return Math.log(n) / Math.log(2);
    }

    // B function
    protected double B(double q){
        if(q == 0)
            return 0;
        return -(q * log2(q) + (1 - q) * log2(1 - q));
    }

    // perform remainder calc for booleans
    public abstract double getRemainder(List<Data> dataList);

}

class FeatureFreqT extends FeatureTest{



    @Override
    public double getRemainder(List<Data> dataList) {
        Set<Data> isTrue = new HashSet<>();
        Set<Data> isFalse = new HashSet<>();
        double numEnT = 0;
        double numEnF = 0;
        for(Data d : dataList){
            if(d.getCountIndex('t') ==2 ){
                isTrue.add(d);
                if(d.matchLanguage(Data.Language.EN))
                    numEnT++;
            } else {
                isFalse.add(d);
                if(d.matchLanguage(Data.Language.EN))
                    numEnF++;
            }
        }

        // (numTrue / total) * B(numA / numTrue) + (numFalse / total) * B(numNotA / numFalse)
        double remainder = ((double) isTrue.size() / dataList.size()) * B(numEnT / isTrue.size()) +
                ((double) isFalse.size() / dataList.size()) * B(numEnF / isFalse.size());

        return remainder;
    }
}
