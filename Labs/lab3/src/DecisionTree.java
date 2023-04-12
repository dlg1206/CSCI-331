import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class DecisionTree {

    public static class Node implements Serializable {
        private final Node parent;
        private Node left;
        private Node right;

        private final List<Data> data;
        private String msg;


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

        public void setMsg(String msg){
            this.msg = msg;
        }

        @Override
        public String toString() {
            return this.msg;
        }
    }


    public static Node trainRecursive(Node parent, List<Data> examples, List<Feature> features){

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
        curNode.setLeft(trainRecursive(curNode, new ArrayList<>(target.getIsEN()), new ArrayList<>(features)));

        // recurse right
        curNode.setRight(trainRecursive(curNode, new ArrayList<>(target.getIsNotEN()), new ArrayList<>(features)));

        return curNode;
    }

    public static void serializeNode(Node node, String filepath) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(node);
            out.flush();

            try (FileOutputStream outputStream = new FileOutputStream(filepath)) {
                outputStream.write(bos.toByteArray());
            }
        }
        // ignore close exception
    }

//    public static Node deSerializeNode(Node node, String filepath) throws IOException {
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        ObjectOutputStream oos = new ObjectOutputStream(bos);
//        oos.writeObject(node);
//        oos.flush();
//        byte [] data = bos.toByteArray();
//    }
}
