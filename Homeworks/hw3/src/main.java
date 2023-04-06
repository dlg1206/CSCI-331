import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class main {



    public static class Class{
        private final Boolean[] attributes = new Boolean[8];
        private final String value;


        public Class(String[] args){

            for(int i = 0; i <args.length - 1; i++){
                this.attributes[i] = args[i].equals("True");
            }
            this.value = args[8];
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



    public static void main(String[] args) throws IOException {
        String filepath = "dtree-data.dat";
        BufferedReader br = new BufferedReader(new FileReader(filepath));

        String line = br.readLine();
        List<Class> classList = new ArrayList<>();
        while (line != null){
            classList.add(new Class(line.split(" ")));
            line = br.readLine();
        }
    }
}
