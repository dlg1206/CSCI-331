import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Derek Garcia
 **/

public class Train {

    private static double log2(double n){
        return Math.log(n) / Math.log(2);
    }

    // B function
    private static double B(double q){
        if(q == 0)
            return 0;
        return -(q * log2(q) + (1 - q) * log2(1 - q));
    }


//    // perform remainder calc for booleans
//    private static double getRemainder(int i, List<Class> classList){
//        Set<Class> isTrue = new HashSet<>();
//        Set<Class> isFalse = new HashSet<>();
//        double numAT = 0;
//        double numAF = 0;
//        for(Class c : classList){
//            if(c.attributes[i]){
//                isTrue.add(c);
//                if(c.value.equals("A"))
//                    numAT++;
//            } else {
//                isFalse.add(c);
//                if(c.value.equals("A"))
//                    numAF++;
//            }
//        }
//
//        // (numTrue / total) * B(numA / numTrue) + (numFalse / total) * B(numNotA / numFalse)
//        double remainder = ((double) isTrue.size() / classList.size()) * B(numAT / isTrue.size()) +
//                ((double) isFalse.size() / classList.size()) * B(numAF / isFalse.size());
//
//        System.out.println("Remainder(" + i + "): " + remainder);
//        return remainder;
//
//    }

    public static void train(List<Data> examples, String hypothesisOut, String learningType){
        /*
        features
        1. # e's > 15% word, T:nl, F:en, approx 17/13 respectively
        2. # highest number percent is "t", T:en F:nl
        3. # highest number percent is "n", T:nl F:en
        4. en articles (the, an, a)
        5. nl articles (see list)
         */


    }
}
