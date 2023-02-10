import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * file: lab1.java
 *
 * Find the shortest path for an orienteering course using A* search
 *
 * CSCI-331 Lab 1
 *
 * @author Derek Garcia
 **/
public class lab1 {

    private static double[][] ELEVATIONS;   // obj to track elevations
    private static double X_DIST = 10.29;
    private static double Y_DIST = 7.55;

    //
    // Utility Objects
    //

    /**
     * Coordinate class to track coordinates
     */
    private static class Coordinate{
        private final int x;
        private final int y;

        private double f;

        private final double elevation;

        private double totalDistance;   // totalDistance from start
        private Coordinate parent = null;

        /**
         * Make new Coordinate
         * @param x x value
         * @param y y value
         */
        public Coordinate(int x, int y){
            this.x = x;
            this.y = y;
            this.elevation = getElevationAt(x, y);
        }

        /**
         * Make new Successor Coordinate
         * @param x x value
         * @param y y value
         */
        public Coordinate(int x, int y, Coordinate parent){
            this.x = x;
            this.y = y;
            this.elevation = getElevationAt(x, y);
            this.parent = parent;
        }

        //
        // Methods
        //

        private double calcDistance(Coordinate other){
            double xDist = Math.pow(this.x - other.x, 2);
            double yDist = Math.pow(this.y - other.y, 2);
            double zDist = Math.pow((this.elevation - other.elevation), 2);
            return Math.sqrt( (X_DIST * xDist) + (Y_DIST * yDist) + zDist);
        }

        public LinkedList<Coordinate> getSuccessors(){
            LinkedList<Coordinate> successors = new LinkedList<>();
            for(int x = -1; x < 2; x++){
                for(int y = -1; y < 2; y++){
                    // Don't add current not to successors
                    if(x != 0 || y != 0)
                        successors.add(new Coordinate(this.x + x, this.y + y, this));
                }
            }
            return successors;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof Coordinate){
                Coordinate other = (Coordinate) obj;
                return this.x == other.x && this.y == other.y;
            }
            return false;
        }

        @Override
        public String toString(){
            return "(" + this.x + ", " + this.y + ")";
        }
    }

    private static class HeuristicComparator implements Comparator<Coordinate>{
        @Override
        public int compare(Coordinate o1, Coordinate o2) {
            return Double.compare(o1.f, o2.f);
        }
    }
    


    //
    // Loaders; load files on startup
    //

    /**
     * Loads Path File with the list of control points
     *
     * @param pathFile file with goal controls
     * @return LinkedList of goal Coordinate objects
     * @throws Exception Issue Reading the Path file
     */
    private static LinkedList<Coordinate> loadGoalCoords(String pathFile) throws Exception {
        // Attempt to load file
        try{
            LinkedList<Coordinate> coords = new LinkedList<>();
            BufferedReader br = new BufferedReader(new FileReader(pathFile));   // Open file

            // Add all control points to coord list
            while(br.ready()){
                String[] points = br.readLine().split(" ");
                coords.add(new Coordinate(Integer.parseInt(points[0]), Integer.parseInt(points[1])));
            }

            // Close stream and return findings
            br.close();
            return coords;
        } catch (Exception e){
            // Something went wrong loading the file
            System.err.println("Unable to read file \"" + pathFile + "\" into coordinates");
            throw e;
        }
    }


    /**
     * Loads Elevation File with the list of control points
     * Hard codes size as per assignment
     *
     * @param elevationFile file with all elevations
     * @return 2D double array with all elevation points
     * @throws IOException Issue Reading the Elevations file
     */
    private static double[][] loadElevations(String elevationFile) throws IOException {
        try{
            double[][] elevations = new double[501][396];     // elevations hardcoded as per assignment

            // Add all elevation points to corresponding point in array
            int row = 0;
            BufferedReader br = new BufferedReader(new FileReader(elevationFile));
            while(br.ready()){
                String[] elevation = br.readLine().strip().split("   ");
                // Add each double
                for(int col = 0; col < 395; col++)
                    elevations[row][col] = Double.parseDouble(elevation[col]);
                row++;
            }
            // Close and return object
            br.close();
            return elevations;

        } catch (Exception e){
            // Something went wrong loading the file
            System.err.println("Unable to read file \"" + elevationFile + "\" into array");
            throw e;
        }
    }


    ///
    /// Helpers
    ///

    /**
     * Helps index elevations as to not get row / column and x and y confused
     *
     * @param x x coordinate
     * @param y y coordinate
     * @return elevation at the given coordinate
     */
    private static double getElevationAt(int x, int y){
        return ELEVATIONS[y][x];    // index row column vs x y
    }


    private static boolean containsBetterSuccessor(LinkedList<Coordinate> list, Coordinate successor){
        if(list.contains(successor)){
            Coordinate other = list.get(list.indexOf(successor));
            return other.f < successor.f;
        }

        return false;   // list doesn't contain successor
    }

    private static void drawPath(BufferedImage terrain, Coordinate goal){
        while(goal != null){
            terrain.setRGB(goal.x, goal.y, Color.red.getRGB());
            goal = goal.parent;
        }

    }

    private static int getTerrainCost(int rgb){
        String buf = Integer.toHexString(rgb);
        String hex = "#" + buf.substring(buf.length()-6).toUpperCase();
        return switch (hex) {
            // open land
            case "#F89412" -> 10;
            // rough meadow
            case "#FFC000" -> 20;
            // Easy forest movement
            case "#FFFFFF" -> 15;
            // Slow run forest
            case "#02D03C" -> 20;
            // Walk forest
            case "#028828" -> 25;
            // Impassible vegetation
            case "#054918" -> 55;
            // Lake/Swamp/Marsh
            case "#0000FF" -> 65;
            // Paved road
            case "#473303" -> 5;
            // Footpath
            case "#000000" -> 10;
            // Out of bounds
            case "#CD0066" -> Integer.MAX_VALUE;

            default -> Integer.MAX_VALUE;
        };


    }


    private static void print(BufferedImage terrain, Set<Coordinate> frontier, LinkedList<Coordinate> explored,Coordinate source, Coordinate sink){
        for(Coordinate c : frontier){
            terrain.setRGB(c.x, c.y, Color.red.getRGB());
        }

        for(Coordinate c : explored){
            terrain.setRGB(c.x, c.y, Color.yellow.getRGB());
        }
        terrain.setRGB(source.x, source.y, Color.BLACK.getRGB());
        terrain.setRGB(sink.x, sink.y, Color.pink.getRGB());
    }


    private static Coordinate doAStarSearch(BufferedImage terrain, Coordinate source, Coordinate sink) throws Exception {
        // timer for metrics
        long start = System.currentTimeMillis();
        long end = start + 20 * 1000;

        // init queues
        TreeSet<Coordinate> frontier = new TreeSet<>(new HeuristicComparator());
        LinkedList<Coordinate> explored = new LinkedList<>();

        // Get and set initial Coordinate
        source.totalDistance = 0;
        frontier.add(source);

        // Repeat until nothing is left in the frontier
        while( !frontier.isEmpty() ){
            if(System.currentTimeMillis() > end){
                print(terrain, frontier, explored,source,sink);
                throw new Exception("Exceeded time limit");
            }


            Coordinate parent = frontier.pollFirst();

            // Go through all current coordinate's successor
            assert parent != null;
            for(Coordinate successor : parent.getSuccessors()){

                // if find goal, return Coordinate with path info
                if(successor.equals(sink)){
//                    print(terrain, frontier, explored,source,sink);
                    System.out.println("\t" + source + " -> " + sink + " | Time Elapsed: " + ((System.currentTimeMillis() - start) / (double) 1000) + " seconds");
                    return successor;
                }

                int terrainCost = getTerrainCost(terrain.getRGB(successor.x, successor.y));
                // inValid
                if(terrainCost == Integer.MAX_VALUE)
                    continue;



                successor.totalDistance = terrainCost + successor.calcDistance(parent) + parent.totalDistance;

//                System.out.println("Calculating Heuristics for: " + successor);
//                successor.f = successor.totalDistance + getHeuristic(terrain, successor, sink);

                successor.f = successor.totalDistance + successor.calcDistance(sink);
                // Check if better successor exists
//                if(containsBetterSuccessor(explored, successor))
//                   continue;
                boolean cont = false;

                for(Coordinate c : frontier){
                    if(successor.equals(c) && c.f < successor.f){
                        cont = true;
                        break;
                    }
                }
                if(cont)
                    continue;
                for(Coordinate c : explored){
                    if(successor.equals(c) && c.f < successor.f){
                        cont = true;
                        break;
                    }
                }
                if (cont)
                    continue;


                    frontier.add(successor);
            }
            explored.push(parent);
        }
//        print(terrain, frontier, explored,source,sink);

        // No path was found :(
        return null;
    }

    /**
     * Load all files and perform A* search. Writes an updated map with the path taken
     *
     * @param args <terrain-image> <elevation-file> <path-file> <output-image-filename>
     */
    public static void main(String[] args) throws Exception {
        // Check for correct args
        if(args.length != 4){
            System.err.println("Incorrect Number of arguments");
            System.err.println("Expected usage: java lab1 <terrain-image> <elevation-file> <path-file> <output-image-filename>");
            return;
        }

        // attempt to load objects
        BufferedImage terrain;
        LinkedList<Coordinate> goals;
        try{
            terrain = ImageIO.read(new File(args[0]));
            ELEVATIONS = loadElevations(args[1]);
            goals = loadGoalCoords(args[2]);
            // Repeat until only goal is left

        } catch (Exception e){
            System.err.println("Failed to load arguments | Message: " + e.getMessage());
            return;
        }

        try {
            long start = System.currentTimeMillis();
            System.out.println("Total Control Points: " + goals.size());
            while(goals.size() != 1){
                Coordinate goal = doAStarSearch(terrain, goals.pop(), goals.peek());
                drawPath(terrain, goal);

            }
            System.out.println("Total Time Elapsed: " + ((System.currentTimeMillis() - start) / (double) 1000));
        } catch (Exception e){
            System.err.println(e);
            ImageIO.write(terrain, "png", new File(args[3]));

        }



        
        
        // Attempt to write final image
        try {
            ImageIO.write(terrain, "png", new File(args[3]));
        } catch (Exception e){
            System.err.println("Unable to write to file \"" + args[3] + "\"| Message: " + e.getMessage());
        }
        

    }
}
