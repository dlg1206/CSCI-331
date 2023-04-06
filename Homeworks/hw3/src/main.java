import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class main {

    // utility class to hold values
    public static class Class{
        private final Boolean[] attributes = new Boolean[8];
        private final String value;


        public Class(String[] args){

            for(int i = 0; i <args.length - 1; i++)
                this.attributes[i] = args[i].equals("True");

            this.value = args[args.length - 1];
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for(Boolean b : this.attributes){
                sb.append(b ? "True " : "False ");
            }
            return sb + this.value;
        }
    }

    // logBase 2
    private static double log2(double n){
        return Math.log(n) / Math.log(2);
    }

    // B function
    private static double B(double q){
        if(q == 0)
            return 0;
        return -(q * log2(q) + (1 - q) * log2(1 - q));
    }


    // perform remainder calc for booleans
    private static double getRemainder(int i, List<Class> classList){
        Set<Class> isTrue = new HashSet<>();
        Set<Class> isFalse = new HashSet<>();
        double numAT = 0;
        double numAF = 0;
        for(Class c : classList){
            if(c.attributes[i]){
                isTrue.add(c);
                if(c.value.equals("A"))
                    numAT++;
            } else {
                isFalse.add(c);
                if(c.value.equals("A"))
                    numAF++;
            }
        }

        // (numTrue / total) * B(numA / numTrue) + (numFalse / total) * B(numNotA / numFalse)
        double remainder = ((double) isTrue.size() / classList.size()) * B(numAT / isTrue.size()) +
                ((double) isFalse.size() / classList.size()) * B(numAF / isFalse.size());

        System.out.println("Remainder(" + i + "): " + remainder);
        return remainder;

    }

    public static void main(String[] args) throws IOException {
        String filepath = "dtree-data.dat";
        BufferedReader br = new BufferedReader(new FileReader(filepath));

        String line = br.readLine();
        List<Class> classList = new ArrayList<>();
        while (line != null){
            classList.add(new Class(line.split(" ")));
            line = br.readLine();
        }
        double remainderValue = Double.MAX_VALUE;
        int n = -1;
        for(int i = 0; i < 8; i++){
            double r = getRemainder(i, classList);
            if(r < remainderValue){
                remainderValue = r;
                n = i;
            }
        }
        System.out.println("Smallest Remainder: " + n);


    }
}
