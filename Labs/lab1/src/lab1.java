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
            this.totalDistance = calcDistance(parent);
        }

        //
        // Methods
        //

        private double calcDistance(Coordinate other){
            double xDist = (this.x - other.x)^2;
            double yDist = (this.y - other.y)^2;
            double zDist = this.elevation - other.elevation;
            zDist = zDist * zDist;
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

       public boolean isSame(Coordinate other){
            return this.x == other.x && this.y == other.y;
       }

        @Override
        public String toString(){
            return "x: " + this.x + " y: " + this.y;
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
            double[][] elevations = new double[500][395];     // elevations hardcoded as per assignment

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



    private static void drawPath(BufferedImage terrain, Coordinate goal){
        while(goal != null){
            terrain.setRGB(goal.x, goal.y, Color.red.getRGB());
            goal = goal.parent;
        }

    }

    private static int getHeuristic(int color){
        // todo
        // check each pixel from start to goal and apply difficult factor base on terrain type
        return 0;
    }

    private static Coordinate doAStarSearch(BufferedImage terrain, Coordinate source, Coordinate sink){
        // init queues
        LinkedList<Coordinate> frontier = new LinkedList<>();
        LinkedList<Coordinate> explored = new LinkedList<>();

        // Get and set initial Coordinate
        source.totalDistance = 0;
        frontier.push(source);

        // Repeat until nothing is left in the frontier
        while( !frontier.isEmpty() ){

            Coordinate curCoordinate = frontier.pop();

            // Go through all current coordinate's successor
            for(Coordinate successor : curCoordinate.getSuccessors()){

                // if find goal, return Coordinate with path info
                if(successor.isSame(sink))
                    return successor;

                successor.totalDistance += curCoordinate.totalDistance; // already has distance from parent

                // successor.f = successor.totalDistance + getHeuristic(1);
                boolean skipSuccessor = false;
                // todo replace to vist / visit with sets and add hashfunt to comp
                for(Coordinate c : frontier){
                    if(c.isSame(successor) && c.f < successor.f){
                        skipSuccessor = true;
                        break;
                    }
                }
                if(skipSuccessor)
                    break;

                for(Coordinate c : explored){
                    if(c.isSame(successor) && c.f < successor.f){
                        skipSuccessor = true;
                        break;
                    }
                }
                if(skipSuccessor)
                    break;
                frontier.add(successor);
            }
            explored.push(curCoordinate);
        }

        // No path was found :(
        return null;
    }

    /**
     * Load all files and perform A* search. Writes an updated map with the path taken
     *
     * @param args <terrain-image> <elevation-file> <path-file> <output-image-filename>
     */
    public static void main(String[] args) {
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

        // Repeat until only goal is left
        while(goals.size() != 1){
            Coordinate goal = doAStarSearch(terrain, goals.pop(), goals.peek());
            drawPath(terrain, goal);
        }

        
        
        // Attempt to write final image
        try {
            ImageIO.write(terrain, "png", new File(args[3]));
        } catch (Exception e){
            System.err.println("Unable to write to file \"" + args[3] + "\"| Message: " + e.getMessage());
        }
        

    }
}
