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
 * Find the shortest path for an orienteering course using A* search
 * CSCI-331 Lab 1
 *
 * @author Derek Garcia
 **/
public class lab1 {

    private static double[][] ELEVATIONS;   // obj to track elevations
    // 1 pxl = 10.29 m x 7.55 m
    private static final double X_DIST = 10.29;
    private static final double Y_DIST = 7.55;

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

        /**
         * Calculate distance in meters to other Coordinates
         *
         * @param other Target Coordinate
         * @return distance to other coordinate in meters
         */
        private double calcDistance(Coordinate other){
            double xDist = Math.pow(this.x - other.x, 2);
            double yDist = Math.pow(this.y - other.y, 2);
            double zDist = Math.pow((this.elevation - other.elevation), 2);
            return Math.sqrt( (X_DIST * xDist) + (Y_DIST * yDist) + zDist);
        }

        /**
         * Get all 8 adjacent successors to current coordinate
         *
         * @return 8 adjacent successors
         */
        public LinkedList<Coordinate> getSuccessors(){
            LinkedList<Coordinate> successors = new LinkedList<>();
            // Index 8 adjacent ares
            for(int x = -1; x < 2; x++){
                for(int y = -1; y < 2; y++){
                    // Don't add current not to successors
                    if(x != 0 || y != 0)
                        successors.add(new Coordinate(this.x + x, this.y + y, this));
                }
            }
            return successors;
        }


        /**
         * Compare Coordinates based on their x and y coordinates
         *
         * @param obj Other object
         * @return true if x and y coordinates match, false otherwise
         */
        @Override
        public boolean equals(Object obj) {
            if(obj instanceof Coordinate other){
                return this.x == other.x && this.y == other.y;
            }
            return false;
        }

        /**
         * @return (x, y) to string
         */
        @Override
        public String toString(){
            return "(" + this.x + ", " + this.y + ")";
        }
    }

    /**
     * Utility Compilation that compares Coordinate f values
     */
    private static class HeuristicComparator implements Comparator<Coordinate>{
        /**
         * @param o1 the first object to be compared.
         * @param o2 the second object to be compared.
         * @return compared f value
         */
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
            LinkedList<Coordinate> coordinates = new LinkedList<>();
            BufferedReader br = new BufferedReader(new FileReader(pathFile));   // Open file

            // Add all control points to coordinate list
            while(br.ready()){
                String[] points = br.readLine().split(" ");
                coordinates.add(new Coordinate(Integer.parseInt(points[0]), Integer.parseInt(points[1])));
            }

            // Close stream and return findings
            br.close();
            return coordinates;
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


    /**
     * Draws path on terrain image between control points
     *
     * @param terrain terrain png to draw path on
     * @param goal Goal Coordinate to start at
     */
    private static void drawPath(BufferedImage terrain, Coordinate goal){
        // Start at goal and move backwards until reach previous control point
        while(goal != null){
            terrain.setRGB(goal.x, goal.y, Color.red.getRGB());
            goal = goal.parent;
        }
    }

    /**
     * Calculate terrain cost based on given rgb code
     *
     * @param rgb rgb code of current terrain
     * @return terrain cost
     */
    private static int getTerrainCost(int rgb){
        // Convert to hex
        String buf = Integer.toHexString(rgb);
        String hex = "#" + buf.substring(buf.length() - 6).toUpperCase();
        // Get terrain cost
        return switch (hex) {
            // open land
            case "#F89412" -> 3;
            // rough meadow
            case "#FFC000" -> 5;
            // Easy forest movement
            case "#FFFFFF" -> 6;
            // Slow run forest
            case "#02D03C" -> 8;
            // Walk forest
            case "#028828" -> 7;
            // Impassible vegetation
            case "#054918" -> Integer.MAX_VALUE;
            // Lake/Swamp/Marsh
            case "#0000FF" -> 20;
            // Paved road
            case "#473303" -> 1;
            // Footpath
            case "#000000" -> 2;
            // Out of bounds
            case "#CD0066" -> Integer.MAX_VALUE;

            // Default to illegal area
            default -> Integer.MAX_VALUE;
        };
    }


    /**
     * Perform A* search
     *
     * @param terrain Terrain to reference
     * @param source Starting coordinate
     * @param sink Ending coordinate
     * @return Ending coordinate with path back to start
     */
    private static Coordinate doAStarSearch(BufferedImage terrain, Coordinate source, Coordinate sink){
        // timer for metrics
        long start = System.currentTimeMillis();

        // init queues
        TreeSet<Coordinate> frontier = new TreeSet<>(new HeuristicComparator());
        LinkedList<Coordinate> explored = new LinkedList<>();

        // Get and set initial Coordinate
        source.totalDistance = 0;
        frontier.add(source);

        // Repeat until nothing is left in the frontier
        while( !frontier.isEmpty() ){
            Coordinate parent = frontier.pollFirst();

            // Go through all current coordinate's successor
            assert parent != null;
            for(Coordinate successor : parent.getSuccessors()){

                // if find goal, return Coordinate with path info
                if(successor.equals(sink)){
                    // Report metrics
                    System.out.println("\t" + source + " -> " + sink + " | Time Elapsed: " + ((System.currentTimeMillis() - start) / (double) 1000) + " seconds");
                    return successor;
                }

                // Get cost and make sure not infeasible
                int terrainCost = getTerrainCost(terrain.getRGB(successor.x, successor.y));
                if(terrainCost == Integer.MAX_VALUE)
                    continue;

                // Calculate total distance
                successor.totalDistance = terrainCost + successor.calcDistance(parent) + parent.totalDistance;

                // Update f value with heuristic
                successor.f = successor.totalDistance + successor.calcDistance(sink);

                // Check if better successor exists
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

                // No better successor, so add to frontier
                frontier.add(successor);
            }
            explored.push(parent);  // add current to explored
        }

        // No path was found :(
        return null;
    }


    /**
     * Load all files and perform A* search. Writes an updated map with the path taken
     *
     * @param args <terrain-image> <elevation-file> <path-file> <output-image-filename>
     */
    public static void main(String[] args){
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
        } catch (Exception e){
            System.err.println("Failed to load arguments | Message: " + e.getMessage());
            return;
        }

        try {
            System.out.println("Total Control Points: " + goals.size());
            long start = System.currentTimeMillis();

            // Repeat until only goal is left
            while(goals.size() != 1){
                Coordinate goal = doAStarSearch(terrain, goals.pop(), goals.peek());
                drawPath(terrain, goal);
            }
            System.out.println("Total Time Elapsed: " + ((System.currentTimeMillis() - start) / (double) 1000));    // time for metrics

        } catch (Exception e){
            System.err.println(e.getMessage());
        }

        // Attempt to write final image
        try {
            ImageIO.write(terrain, "png", new File(args[3]));
        } catch (Exception e){
            System.err.println("Unable to write to file \"" + args[3] + "\"| Message: " + e.getMessage());
        }
    }
}
