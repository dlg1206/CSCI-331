import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

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

    //
    // Utility Objects
    //

    /**
     * Coordinate class to track coordinates
     */
    private static class Coordinate{
        public int x;
        public int y;

        /**
         * Make new Coordinate
         * @param x x value
         * @param y y value
         */
        public Coordinate(int x, int y){
            this.x = x;
            this.y = y;
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
     * Helps index elvations as to not get row / column and x and y confused
     *
     * @param x x coordinate
     * @param y y coordinate
     * @return elevation at the given coordinate
     */
    private static double getElevationAt(int x, int y){
        return ELEVATIONS[y][x];    // index row column vs x y
    }



    private static void drawPath(BufferedImage terrain, LinkedList<Coordinate> path){
        for(Coordinate c : path)
            terrain.setRGB(c.x, c.y, Color.red.getRGB());
    }

    private static void doAStarSearch(BufferedImage terrain){

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
        try{
            BufferedImage terrain = ImageIO.read(new File(args[0]));
            ELEVATIONS = loadElevations(args[1]);
            LinkedList<Coordinate> goals = loadGoalCoords(args[2]);
            drawPath(terrain, goals);
            ImageIO.write(terrain, "png", new File(args[3]));
        } catch (Exception e){
            System.err.println("Failed to load arguments | Message: " + e.getMessage());
        }

    }
}
