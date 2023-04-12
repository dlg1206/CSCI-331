import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class DecisionTree {

    private Node root;

    private class Node {
        private final Node parent;
        private Node left;
        private Node right;

        private final List<Data> data;


        public Node(Node parent, List<Data> data){
            this.parent = parent;
            this.data = data;
        }

        public void setLeft(Node left) {
            this.left = left;
        }
        public void setRight(Node right){
            this.right = right;
        }
    }


    private Node trainRecursive(Node parent, List<Data> examples, List<Feature> features){

        // Base case: Only 1 example or run out of features
        if(examples.size() == 1 || features.isEmpty())
            return null;

        for(Feature f: features){
            f.getRemainder(examples);
        }
        Collections.sort(features);
        Feature target = features.remove(0);
        Node curNode = new Node(parent, examples);
        // recurse left
        curNode.setLeft(trainRecursive(curNode, new ArrayList<>(target.getIsEN()), new ArrayList<>(features)));

        // recurse right
        curNode.setRight(trainRecursive(curNode, new ArrayList<>(target.getIsNotEN()), new ArrayList<>(features)));

        return curNode;
    }
    public DecisionTree(List<Data> examples, List<Feature> features) {
        this.root = trainRecursive(null, examples, features);
    }

}
