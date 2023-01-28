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
     * @return the resulting distance and paths
     */
    private static HashMap<Integer, ArrayList<Node>> doDijkstra(Node source){

//        // init vars
//        LinkedList<Node> queue = new LinkedList<>();
//        HashMap<Integer, ArrayList<Node>> result = new HashMap<>();
//
//        // Get and set initial node
//        Node curNode = source;
//        curNode.setDistance(0);
//
//        // add to results
//        result.put(0, new ArrayList<>());
//        result.get(0).add(curNode);
//
//        // Repeat until nothing is left in the queue
//        for( ;; ){
//
//            // Go through all od the adjacent nodes to current node
//            for(Node adj : curNode.getEdges().keySet()){
//
//                // Add to queue if adj hasn't been visited and not already in queue
//                if(adj.getDistance() < 0 && !queue.contains(adj)){
//                    queue.add(adj);
//                }
//
//                int newDist = curNode.getDistance() + curNode.getEdgeWeight(adj);   // calculate new distance
//
//                // If distance hasn't been set or the new distance is better
//                if(adj.getDistance() < 0 || adj.getDistance() > newDist){
//
//                    // make new key if needed
//                    if(!result.containsKey(newDist))
//                        result.put(newDist, new ArrayList<>());
//
//                    // if improving distance, remove from old distance
//                    if(adj.getDistance() >= 0)
//                        result.get(adj.getDistance()).remove(adj);
//
//                    // Update values
//                    adj.setDistance(newDist);
//                    adj.setPath(curNode);
//
//                    // update result
//                    result.get(newDist).add(adj);
//                }
//            }
//
//            // End if queue is empty, else get next node
//            if(queue.isEmpty()){
//                break;
//            } else {
//                curNode = queue.pop();
//            }
//        }
//        return result;
        return null;
    }

    /**
     * Prints all the paths that stem from the source node
     *
     * @param result A hashmap of distances and all nodes that are that distance away
     */
    private static void printResults(HashMap<Integer, ArrayList<Node>> result){

        // sort the keys from least to greatest distance
        ArrayList<Integer> distances = new ArrayList<>(result.keySet());
        Collections.sort(distances);

        // go through all distances
        for(int dist : distances){

            // For each node at distance dist
            for(Node node : result.get(dist)){
                System.out.print(dist + ": ");

                // Continue backtracking until reach source node
                while(node.getParent() != null){
                    System.out.print(node + "<");
                    node = node.getParent();
                }

                System.out.println(node);   // print source
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

        int dist = 0;   // init dist is 0

        // build string from dest back to source
        StringBuilder path = new StringBuilder();
        while(dest != source){
            path.append(dest).append("<");
            dest = dest.getParent();
            dist++;
        }

        path.append(source);    // add source

        // print full string
        System.out.println(dist +": " + path);
    }

    /**
     * Looks through graph to get a node with the given word
     *
     * @param graph graph to search through
     * @param word word to find node of
     * @return Node if exists, null otherwise
     */
    private static Node getByString(Node[] graph, String word){
        // search through graph
        for(int i = 1; i < graph.length; i++){
           // check if word equal
           if(graph[i].toString().equals(word)){
               return graph[i];
           }
        }
        // no node was found
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
//        HashMap<Integer, ArrayList<Node>> result;
//        Node source = getByString(graph, args[1]);
//        if(source == null){
//            System.out.println(args[1] + " was not found");
//            return;
//        }
//        result = doDijkstra(source);  // use given source
//
//
//        // print full if no destination given
//        if(args.length != 3){
//            printResults(result);
//        } else {
//            // validate destination exists
//            Node dest = getByString(graph, args[2]);
//            if(dest == null){
//                System.out.println(args[2] + " was not found");
//                return;
//            }
//            // print specific path
//            printPath(getByString(graph, args[1]), dest);
//        }
    }
}
