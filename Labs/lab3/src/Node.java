import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * file: Node.java
 * Node in the decision tree
 *
 * @author Derek Garcia
 */
public class Node implements Serializable {
    private Node _true;     // left-isEnglish
    private Node _false;     // right-isDutch

    // Values
    private final List<Data> data;
    private final Feature feature;


    /**
     * Private Node Constructor, used for building tree
     *
     * @param data data associated with this node
     * @param feature feature that this node tested
     */
    private Node(List<Data> data, Feature feature){
        this.data = data;
        this.feature = feature;
    }

    /**
     * Set Left child
     * @param left left node
     */
    public void setLIsEn(Node left) {
        this._true = left;
    }

    /**
     * Set right child
     * @param right right node
     */
    public void setRIsNl(Node right){
        this._false = right;
    }

    /**
     * Build a Decision Tree
     *
     * @param examples List of data to test
     * @param features List of features to test data on
     * @return Root Decision Tree node
     */
    public static Node buildTree(List<Data> examples, List<Feature> features){

        // Base case: Run out of features
        if(features.isEmpty())
            return null;

        // Find lowest remainder
        for(Feature f: features){
            f.getRemainder(examples);
        }

        // sort and pop features
        Collections.sort(features);
        Feature target = features.remove(0);

        Node curNode = new Node(examples, target);  // make new node

        // recurse left if data
        if(target.getIsEN().size() != 0)
            curNode.setLIsEn(buildTree(
                    new ArrayList<>(target.getIsEN()),
                    new ArrayList<>(features))
            );

        // recurse right if data
        if(target.getIsNL().size() != 0)
            curNode.setRIsNl(buildTree(
                    new ArrayList<>(target.getIsNL()),
                    new ArrayList<>(features))
            );

        return curNode;
    }

    /**
     * Predict the data language
     *
     * @param data Data to predict
     * @return en if English, nl if Dutch
     */
    public String predict(Data data){

        boolean isEnglish = this.feature.isEnglish(data);

        // base case
        if(this._true == null && this._false == null){
            return this.toString();     // sums weight of data, picks en or nl based on larger sum
        }

        // Else test children
        if(isEnglish) {
            if(this._true != null)
                return this._true.predict(data);
        }
        else{
            if(this._false != null)
                return this._false.predict(data);
        }
        return null;

    }


    /**
     * Calculate error of the decision tree
     *
     * @param expected Expected Language
     * @return error
     */
    private double getError(Data.Language expected){
        double enError;
        double nlError = 0;

        // base case, reach leaf
        if(this._true == null || this._false == null){
           double error = 0;
           for(Data d : this.data){
               // If doesn't match expected, update error and flag
               if(!d.matchLanguage(expected)){
                   error += d.getWeight();
                   d.flagError();
               }
           }
           return error;
        }

        // check IsEnglish Dataset for the number of NL phrases
        enError = this._true.getError(Data.Language.EN);

        // check IsDutch Dataset for the number of EN phrases
        if(this._false != null)
            nlError = this._false.getError(Data.Language.NL);

        // return total error
        return enError + nlError;
    }


    /**
     * Perform adaBoost on this decision tree
     */
    public void adaBoost(){

        // Calculate update
        double error = this._true.getError(Data.Language.EN) + this._false.getError(Data.Language.NL);
        double update = error / (1 - error);

        // Update weights
        double newWeight = 0;
        for(Data d : this.data){
            if(d.isCorrect())
                d.updateWeight(update);
            newWeight += d.getWeight();
        }

        // Normalize weights
        double normalizeFactor = 1 / newWeight;
        for(Data d : this.data){
            d.updateWeight(normalizeFactor);
        }
    }


    /**
     * Serialize Node into object
     *
     * @param filepath output file path
     * @throws IOException unable to write to file
     */
    public void serialize(String filepath) throws IOException {
        // Convert to byte array
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(this);
            out.flush();

            // write to file
            try (FileOutputStream outputStream = new FileOutputStream(filepath)) {
                outputStream.write(bos.toByteArray());
            }
        }
        System.out.println("DecisionTree written to file \"" + filepath + "\"");
    }

    /**
     * Deserialize node file into object
     *
     * @param filepath Node filepath
     * @return Decision tree root node
     * @throws IOException File is bad
     * @throws ClassNotFoundException Node class not found
     */
    public static Node deSerialize(String filepath) throws IOException, ClassNotFoundException {
        // read in byte data
        byte[] nodeData = Files.readAllBytes(Paths.get(filepath));
        ByteArrayInputStream bin = new ByteArrayInputStream(nodeData);
        ObjectInputStream oin = new ObjectInputStream(bin);

        // Cast new node
        return (Node) oin.readObject();
    }

    @Override
    public String toString(){
        double en = 0;
        double nl = 0;
        for(Data d : this.data){
            if(d.matchLanguage(Data.Language.EN)){
                en += d.getWeight();
            } else {
                nl += d.getWeight();
            }
        }

        if(en > nl){
            return "en";
        } else {
            return "nl";
        }
    }
}
