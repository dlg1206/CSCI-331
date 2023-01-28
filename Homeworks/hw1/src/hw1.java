import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Uses Dijkstra's algorithm to find word ladders
 *
 * @author Derek Garcia
 **/

public class hw1 {
   private static class Node {

        private final String word;
        private final LinkedList<Node> edges;   // adj list

        private int distance = -1;
        private Node path = null;


        /**
         * Node Constructor. builds node
         *
         * @param word word
         */
        public Node(String word) {
            this.word = word;
            this.edges = new LinkedList<>();
        }


        ///
        /// Getters
        ///

        // get edges
        public LinkedList<Node> getEdges(){
            return this.edges;
        }

        // get distance
        public int getDistance(){
            return this.distance;
        }

        //get parent
        public Node getParent(){
            return this.path;
        }


        ///
        /// Setters
        ///

        // set distance
        public void setDistance(int distance) {
            this.distance = distance;
        }

        // set parent
        public void setPath(Node path) {
            this.path = path;
        }


        ///
        /// Methods
        ///

        /**
         * Adds an edge to this node's adj list
         *
         * @param node adj node
         */
        public void addEdge(Node node) {
            this.edges.add(node);
        }

        public boolean isAdjacent(Node other){
            // adjacent nodes must be the same length
            if(this.word.length() != other.toString().length())
                return false;

            // Compare strings
            int cost = 0;
            for(int i = 0; i < this.word.length(); i++){
                // compare characters
                if(this.word.charAt(i) != other.toString().charAt(i))
                    cost++;
                // if differ by more than 1 letter, not adjacent
                if( cost > 1)
                    return false;
            }
            // only differ by 1
            return true;
        }

        // prints node correctly
        @Override
        public String toString() {
            return this.word;
        }

    }


    /**
     * Parses input file to create a graph of given values
     *
     * @param fileName name of input file
     * @return graph
     * @throws IOException filename is bad
     */
    private static ArrayList<Node> parseDict(String fileName) throws IOException {
        // Create new Buffered Reader
        BufferedReader br = new BufferedReader(new FileReader(fileName));

        ArrayList<Node> graph = new ArrayList<>();

        String line = br.readLine();    // get first node info

        // Read entire file
        while (line != null){
            Node newNode = new Node(line);

            for(Node oldNode : graph){
                // if nodes adjacent, add an edge
                if(oldNode.isAdjacent(newNode)){
                    oldNode.addEdge(newNode);
                    newNode.addEdge(oldNode);
                }
            }
            graph.add(newNode);
            line = br.readLine();
        }

        br.close();
        return graph;
    }

    /**
     * Completes Dijkstra Algorithm
     *
     * @param source Node to start at
     */
    private static void doDijkstra(Node source){

        // init vars
        LinkedList<Node> queue = new LinkedList<>();
        HashMap<Integer, ArrayList<Node>> result = new HashMap<>();

        // Get and set initial node
        Node curNode = source;
        curNode.setDistance(0);

        // add to results
        result.put(0, new ArrayList<>());
        result.get(0).add(curNode);

        // Repeat until nothing is left in the queue
        for( ;; ){

            // Go through all od the adjacent nodes to current node
            for(Node adj : curNode.getEdges()){

                // Add to queue if adj hasn't been visited and not already in queue
                if(adj.getDistance() < 0 && !queue.contains(adj)){
                    queue.add(adj);
                }

                int newDist = curNode.getDistance() + 1;   // All edges have a weight of 1

                // If distance hasn't been set or the new distance is better
                if(adj.getDistance() < 0 || adj.getDistance() > newDist){

                    // make new key if needed
                    if(!result.containsKey(newDist))
                        result.put(newDist, new ArrayList<>());

                    // if improving distance, remove from old distance
                    if(adj.getDistance() >= 0)
                        result.get(adj.getDistance()).remove(adj);

                    // Update values
                    adj.setDistance(newDist);
                    adj.setPath(curNode);

                    // update result
                    result.get(newDist).add(adj);
                }
            }

            // End if queue is empty, else get next node
            if(queue.isEmpty()){
                break;
            } else {
                curNode = queue.pop();
            }
        }

    }

    /**
     * Prints a path between 2 nodes
     *
     * @param source node to start at
     * @param dest destination node
     */
    private static void printPath(Node source, Node dest){

        // build string from dest back to source
        StringBuilder path = new StringBuilder();
        while(dest != source){
            if(dest == null){
                System.out.println("No Solution");
                return;
            }
            path.append(dest).append("\n");
            dest = dest.getParent();
        }

        path.append(source);    // add source

        // print full string
        System.out.println(path);
    }

    /**
     * Looks through graph to get a node with the given word
     *
     * @param graph graph to search through
     * @param word word to find node of
     * @return Node if exists, null otherwise
     */
    private static Node getByString(ArrayList<Node> graph, String word){
        for(Node node : graph){
            if(node.toString().equals(word)){
                return node;
            }
        }

        // no node was found
        System.out.println(word + " was not found");
        return null;
    }


    /**
     * Runs WordLadder
     * @param args [dictionary source] [start word] [target word]
     */
    public static void main(String[] args){

        if(args.length != 3){
            System.err.println("Incorrect number of arguments; expected <dictionary path> <start word> <target word> ");
            return;
        }
        ArrayList<Node> graph;
        try{
            // Make graph
            graph = parseDict(args[0]);
        } catch (Exception e){
            System.err.println("Failed to read file: " + args[0]);
            return;
        }


        // get result
        Node source = getByString(graph, args[1]);
        Node dest = getByString(graph, args[2]);
        if(source == null || dest == null)
            return;

        doDijkstra(source);  // use given source
        // print specific path
        printPath(getByString(graph, args[1]), dest);

    }
}
