import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Node implements Serializable {
    private final Node parent;
    private Node lIsEn;
    private Node rIsNl;

    private final List<Data> data;
    private String msg;


    public void setLIsEn(Node left) {
        this.lIsEn = left;
    }
    public void setRIsNl(Node right){
        this.rIsNl = right;
    }

    public void setMsg(String msg){
        this.msg = msg;
    }

    private Node(Node parent, List<Data> data){
        this.parent = parent;
        this.data = data;
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
        if(parent != null)
            parent.setMsg(target.toString());

        Node curNode = new Node(parent, examples);
        // recurse left
        curNode.setLIsEn(buildTree(curNode, new ArrayList<>(target.getIsEN()), new ArrayList<>(features)));

        // recurse right
        curNode.setRIsNl(buildTree(curNode, new ArrayList<>(target.getIsNotEN()), new ArrayList<>(features)));

        return curNode;
    }

    private double getNumWrong(){
        double thisWrong;
//        if(this.parent == null)

        return 0;
    }

    public void adaBoost(){




        // recurse left
        if(this.lIsEn != null)
            this.lIsEn.adaBoost();

        // recurse right
        if(this.rIsNl != null)
            this.rIsNl.adaBoost();



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
