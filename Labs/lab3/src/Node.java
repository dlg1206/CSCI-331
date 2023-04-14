import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Node implements Serializable {
    private final Node parentNode;
    private Node lIsEn;
    private Node rIsNl;

    private final List<Data> data;
    private String msg = "root";


    public void setLIsEn(Node left) {
        this.lIsEn = left;
    }
    public void setRIsNl(Node right){
        this.rIsNl = right;
    }

    public void setMsg(String msg){
        this.msg = msg;
    }

    private Node(Node parent, List<Data> data, String msg){
        this.parentNode = parent;
        this.data = data;
        this.msg = msg;
    }

    public static Node buildTree(Node parent, List<Data> examples, List<Feature> features){

        // Base case: Only 1 example or run out of features
        if(examples.size() == 1 || features.isEmpty())
            return null;

        for(Feature f: features){
            f.getRemainder(examples);
        }
        Collections.sort(features);
        Feature target = features.remove(0);

        Node curNode = new Node(parent, examples, target.toString());

        // recurse left if data
        if(target.getIsEN().size() != 0)
            curNode.setLIsEn(buildTree(
                    new Node(parent, examples, target.toString()),
                    new ArrayList<>(target.getIsEN()),
                    new ArrayList<>(features))
            );

        // recurse right if data
        if(target.getIsNL().size() != 0)
            curNode.setRIsNl(buildTree(
                    new Node(parent, examples, target.toString()),
                    new ArrayList<>(target.getIsNL()),
                    new ArrayList<>(features))
            );

        return curNode;
    }




    // lang that shouldn't be in there
    private double getError(Data.Language expected){
        double enError = 0;
        double nlError = 0;

        // base case, reach leaf
        if(this.lIsEn == null || this.rIsNl == null){
           double error = 0;
           for(Data d : this.data){
//               if(!d.matchLanguage(expected))
//                   error += d.getWeight();
               System.err.println(d);
               error += d.getWeight();  // todo weight tied to node not data?
               d.updateWeight(-1);
           }
           return error;
        }

        // check IsEnglish Dataset for the number of NL phrases
        enError = this.lIsEn.getError(Data.Language.EN);

        // check IsDutch Dataset for the number of EN phrases
        if(this.rIsNl != null)
            nlError = this.rIsNl.getError(Data.Language.NL);


        return enError + nlError;
    }

    private double updateWeight(Data.Language notExpected, double factor){
        double enWeight = 0;
        double nlWeight = 0;

        // base case, reach leaf
        if(this.lIsEn == null && this.rIsNl == null){
            double weight = 0;
            for(Data d : this.data){
                if(!d.matchLanguage(notExpected))
                    weight += d.updateWeight(factor);
            }
            return weight;
        }

        // check IsEnglish Dataset for the number of NL phrases
        if(this.lIsEn != null)
            enWeight = this.lIsEn.updateWeight(Data.Language.EN, factor);

        // check IsDutch Dataset for the number of EN phrases
        if(this.rIsNl != null)
            nlWeight = this.rIsNl.updateWeight(Data.Language.NL, factor);


        return enWeight + nlWeight;
    }

    private void walk(String path) {
        double weight = 0;
        for(Data d : this.data){
            weight += d.getWeight();
        }

        if (this.lIsEn == null || this.rIsNl == null) {

            System.out.println(path + this + " : " + weight);
            return;
        }
        path += this.toString();

        // check IsEnglish Dataset for the number of NL phrases
        if (this.lIsEn != null)
            this.lIsEn.walk(path + "L-[" + weight + "] -> ");
        else
            System.out.println(path + "L-[" + weight + "]");

        // check IsDutch Dataset for the number of EN phrases
        if (this.rIsNl != null)
            this.rIsNl.walk(path + "R-[" + weight + "] -> ");
        else
            System.out.println(path + "R-[" + weight + "]");
    }

    public void adaBoost(){
        walk("");

        double error = this.lIsEn.getError(Data.Language.EN) + this.rIsNl.getError(Data.Language.NL);
        double update = error / (1 - error);
        double newWeight = this.lIsEn.updateWeight(Data.Language.NL, update) + this.rIsNl.updateWeight(Data.Language.EN, update);
        System.out.println(error);
        System.out.println(newWeight);


//
//
//        // recurse left
//        if(this.lIsEn != null)
//            this.lIsEn.adaBoost();
//
//        // recurse right
//        if(this.rIsNl != null)
//            this.rIsNl.adaBoost();



    }


    public void serialize(String filepath) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(this);
            out.flush();

            try (FileOutputStream outputStream = new FileOutputStream(filepath)) {
                outputStream.write(bos.toByteArray());
            }
        }
        System.out.println("DecisionTree written to file \"" + filepath + "\"");
    }

    public static Node deSerialize(String filepath) throws IOException, ClassNotFoundException {
        byte[] nodeData = Files.readAllBytes(Paths.get(filepath));
        ByteArrayInputStream bin = new ByteArrayInputStream(nodeData);
        ObjectInputStream oin = new ObjectInputStream(bin);
        return (Node) oin.readObject();
    }

    @Override
    public String toString() {
        return this.msg;
    }
}
