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
    private Feature feature;


    public void setLIsEn(Node left) {
        this.lIsEn = left;
    }
    public void setRIsNl(Node right){
        this.rIsNl = right;
    }


    private Node(Node parent, List<Data> data, Feature feature){
        this.parentNode = parent;
        this.data = data;
        this.feature = feature;
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

        Node curNode = new Node(parent, examples, target);

        // recurse left if data
        if(target.getIsEN().size() != 0)
            curNode.setLIsEn(buildTree(
                    curNode,
                    new ArrayList<>(target.getIsEN()),
                    new ArrayList<>(features))
            );

        // recurse right if data
        if(target.getIsNL().size() != 0)
            curNode.setRIsNl(buildTree(
                    curNode,
                    new ArrayList<>(target.getIsNL()),
                    new ArrayList<>(features))
            );

        return curNode;
    }

    public String predict(Data data){
        boolean isEnglish = this.feature.isEnglish(data);

        if(isEnglish && this.lIsEn != null)
            return this.lIsEn.predict(data);

        if(!isEnglish && this.rIsNl != null)
            return this.rIsNl.predict(data);

        // base case
        if(isEnglish)
            return "en";
        else
            return "nl";
    }




    // lang that shouldn't be in there
    private double getError(Data.Language expected){
        double enError;
        double nlError = 0;

        // base case, reach leaf
        if(this.lIsEn == null || this.rIsNl == null){
           double error = 0;
           for(Data d : this.data){
               if(!d.matchLanguage(expected)){
                   error += d.getWeight();
                   d.flagError();
               }

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



    public void adaBoost(){

        double error = this.lIsEn.getError(Data.Language.EN) + this.rIsNl.getError(Data.Language.NL);
        double update = error / (1 - error);
        double newWeight = 0;
        for(Data d : this.data){
            if(d.isCorrect())
                d.updateWeight(update);
            newWeight += d.getWeight();
        }

        double normalizeFactor = 1 / newWeight;
        for(Data d : this.data){
            d.updateWeight(normalizeFactor);
        }

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


}
