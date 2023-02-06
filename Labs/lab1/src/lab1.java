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
 * CSCI-331 Lab 1
 *
 * @author Derek Garcia
 **/
public class lab1 {

    private static double[][] ELEVATIONS;

    private static class Coordinate{
        public int x;
        public int y;

        public Coordinate(int x, int y){
            this.x = x;
            this.y = y;
        }
        @Override
        public String toString(){
            return "x: " + this.x + " y: " + this.y;
        }
    }


    private static void doAStarSearch(BufferedImage terrain){

    }


    private static LinkedList<Coordinate> loadGoalCoords(String pathFile) throws Exception {
        try{
            BufferedReader br = new BufferedReader(new FileReader(pathFile));
            LinkedList<Coordinate> coords = new LinkedList<>();
            while(br.ready()){
                String[] points = br.readLine().split(" ");
                coords.add(new Coordinate(Integer.parseInt(points[0]), Integer.parseInt(points[1])));
            }
            br.close();
            return coords;
        } catch (Exception e){
            System.err.println("Unable to read file \"" + pathFile + "\" into coordinates");
            throw e;
        }
    }


    private static double[][] loadElevations(String elevationFile) throws IOException {
        try{
            System.out.println("Loading Elevations file. . .");
            BufferedReader br = new BufferedReader(new FileReader(elevationFile));
            double[][] elevations = new double[500][395];     // elevations hardcoded as per assignment
            int row = 0;
            while(br.ready()){
                String[] elevation = br.readLine().strip().split("   ");

                // Add each double.
                for(int col = 0; col < 395; col++)
                    elevations[row][col] = Double.parseDouble(elevation[col]);
                row++;
            }
            br.close();
            System.out.println("Complete!");
            return elevations;

        } catch (Exception e){
            System.err.println("Unable to read file \"" + elevationFile + "\" into array");
            throw e;
        }
    }

    private static double getElevationAt(int x, int y){
        return ELEVATIONS[y][x];    // index row column vs x y
    }

    private static void drawPath(BufferedImage terrain, LinkedList<Coordinate> path){
        for(Coordinate c : path)
            terrain.setRGB(c.x, c.y, Color.red.getRGB());
    }

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
